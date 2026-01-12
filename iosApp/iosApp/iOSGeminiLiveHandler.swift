import Foundation
import AVFoundation
import FirebaseAILogic
import shared

class iOSGeminiLiveHandler: NSObject, SwiftGeminiBridge {
    private var liveSession: LiveSession?
    private var callbacks: LiveSessionCallbacks?
    
    private let audioEngine = AVAudioEngine()
    private let playerNode = AVAudioPlayerNode()
    private var inputFormat: AVAudioFormat?
    private var outputFormat: AVAudioFormat?
    
    private let lock = NSLock()
    private var currentInputTranscription: String = ""
    private var currentOutputTranscription: String = ""
    private var fullAudioData = Data()
    
    private let sampleRate: Double = 24000
    
    override init() {
        super.init()
        setupAudioEngine()
    }
    
    private func setupAudioEngine() {
        outputFormat = AVAudioFormat(commonFormat: .pcmFormatInt16, sampleRate: sampleRate, channels: 1, interleaved: false)
        audioEngine.attach(playerNode)
        audioEngine.connect(playerNode, to: audioEngine.mainMixerNode, format: outputFormat)
    }
    
    func connect(config: SessionConfig, modelName: String, callbacks: LiveSessionCallbacks) async throws {
        self.callbacks = callbacks
        
        lock.lock()
        self.currentInputTranscription = ""
        self.currentOutputTranscription = ""
        self.fullAudioData = Data()
        lock.unlock()
        
        let liveGenerationConfig = LiveGenerationConfig(
            speechConfig: SpeechConfig(voice: Voice(name: config.voiceName.name)),
            responseModality: .audio
        )
        
        let liveModel = FirebaseAI.firebaseAI().liveModel(
            modelName: modelName,
            generationConfig: liveGenerationConfig,
            systemInstruction: Content(role: "system", parts: [TextPart(text: config.systemInstruction)])
        )
        
        do {
            liveSession = try await liveModel.connect()
            callbacks.onOpen()
            
            startListening()
            try startRecording()
        } catch {
            callbacks.onError(error: KotlinException(message: error.localizedDescription))
            throw error
        }
    }
    
    func disconnect() async throws -> KotlinByteArray? {
        stopRecording()
        liveSession?.close()
        liveSession = nil
        callbacks?.onClose()
        
        lock.lock()
        let recordedAudio = fullAudioData
        self.currentInputTranscription = ""
        self.currentOutputTranscription = ""
        self.fullAudioData = Data()
        lock.unlock()
        
        callbacks = nil
        
        let byteArray = KotlinByteArray(size: Int32(recordedAudio.count))
        recordedAudio.withUnsafeBytes { ptr in
            if let baseAddress = ptr.baseAddress {
                for i in 0..<recordedAudio.count {
                    byteArray.set(index: Int32(i), value: Int8(bitPattern: baseAddress.load(fromByteOffset: i, as: UInt8.self)))
                }
            }
        }
        return byteArray
    }
    
    private func startListening() {
        Task {
            guard let liveSession = liveSession else { return }
            do {
                for try await response in liveSession.responses {
                    handleResponse(response)
                }
            } catch {
                callbacks?.onError(error: KotlinException(message: error.localizedDescription))
            }
        }
    }
    
    private func handleResponse(_ response: LiveServerContent) {
        let currentTime = Int64(Date().timeIntervalSince1970 * 1000)
        
        // 1. Handle Audio Playback
        response.content?.parts.compactMap { $0 as? InlineDataPart }.forEach { audioPart in
            playAudio(data: audioPart.inlineData)
        }
        
        lock.lock()
        defer { lock.unlock() }
        
        // 2. Handle Interruption
        if response.interrupted {
            playerNode.stop()
            currentOutputTranscription = ""
            callbacks?.onPartialTranscript(text: "", isUser: false)
        }
        
        // 3. Handle Model Output (Text)
        if let outputText = response.outputTranscription?.text {
            currentOutputTranscription += outputText
            callbacks?.onPartialTranscript(text: currentOutputTranscription, isUser: false)
        }
        
        // 4. Handle User Input (STT)
        if let inputText = response.inputTranscription?.text {
            currentInputTranscription += inputText
            callbacks?.onPartialTranscript(text: currentInputTranscription, isUser: true)
        }
        
        // 5. Handle Turn Completion
        if response.turnComplete {
            finalizeTurn(timestamp: currentTime)
        }
    }
    
    private func finalizeTurn(timestamp: Int64) {
        if !currentInputTranscription.isEmpty {
            callbacks?.onMessage(text: currentInputTranscription, isUser: true, timestamp: timestamp)
            callbacks?.onPartialTranscript(text: "", isUser: true)
            currentInputTranscription = ""
        }
        if !currentOutputTranscription.isEmpty {
            callbacks?.onMessage(text: currentOutputTranscription, isUser: false, timestamp: timestamp)
            callbacks?.onPartialTranscript(text: "", isUser: false)
            currentOutputTranscription = ""
        }
    }
    
    private func startRecording() throws {
        let audioSession = AVAudioSession.sharedInstance()
        try audioSession.setCategory(.playAndRecord, mode: .voiceChat, options: [.defaultToSpeaker, .allowBluetooth])
        try audioSession.setActive(true)

        let inputNode = audioEngine.inputNode
        let srate = sampleRate
        inputFormat = AVAudioFormat(commonFormat: .pcmFormatInt16, sampleRate: srate, channels: 1, interleaved: false)
        
        inputNode.installTap(onBus: 0, bufferSize: 1024, format: inputFormat) { [weak self] buffer, time in
            self?.processAudioBuffer(buffer)
        }
        
        try audioEngine.start()
        playerNode.play()
    }
    
    private func stopRecording() {
        audioEngine.inputNode.removeTap(onBus: 0)
        audioEngine.stop()
        playerNode.stop()
        
        try? AVAudioSession.sharedInstance().setActive(false)
    }
    
    private func processAudioBuffer(_ buffer: AVAudioPCMBuffer) {
        guard let liveSession = liveSession else { return }
        
        let data = Data(buffer: buffer)
        
        lock.lock()
        fullAudioData.append(data)
        lock.unlock()
        
        // Calculate volume for UI
        let volume = calculateVolume(buffer)
        DispatchQueue.main.async {
            self.callbacks?.onVolumeUpdate(volume: volume)
        }
        
        Task {
            try? await liveSession.sendAudioRealtime(data: data)
        }
    }
    
    private func playAudio(data: Data) {
        guard let outputFormat = outputFormat else { return }
        guard let buffer = AVAudioPCMBuffer(data: data, format: outputFormat) else { return }
        
        playerNode.scheduleBuffer(buffer, at: nil, options: [], completionHandler: nil)
        if !playerNode.isPlaying {
            playerNode.play()
        }
    }
    
    private func calculateVolume(_ buffer: AVAudioPCMBuffer) -> Float {
        guard let channelData = buffer.int16ChannelData?[0] else { return 0 }
        let length = Int(buffer.frameLength)
        
        var sum: Double = 0
        for i in 0..<length {
            let sample = Double(channelData[i])
            sum += sample * sample
        }
        
        let rms = sqrt(sum / Double(length))
        let normalizedRms = Float(rms / 32767.0)
        return max(0, min(1, normalizedRms))
    }
}

extension Data {
    init(buffer: AVAudioPCMBuffer) {
        let audioBuffer = buffer.audioBufferList.pointee.mBuffers
        self.init(bytes: audioBuffer.mData!, count: Int(audioBuffer.mDataByteSize))
    }
}

extension AVAudioPCMBuffer {
    convenience init?(data: Data, format: AVAudioFormat) {
        let streamDescriptor = format.streamDescription.pointee
        let frameCapacity = UInt32(data.count) / streamDescriptor.mBytesPerFrame
        self.init(pcmFormat: format, frameCapacity: frameCapacity)
        self.frameLength = frameCapacity
        
        let audioBuffer = self.audioBufferList.pointee.mBuffers
        data.withUnsafeBytes { (bufferPointer: UnsafeRawBufferPointer) in
            _ = memcpy(audioBuffer.mData, bufferPointer.baseAddress, data.count)
        }
    }
}

