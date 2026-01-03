/*
import Foundation
import AVFoundation
import FirebaseAILogic // Required for Gemini Live API
import shared

class iOSGeminiLiveHandler: NSObject, SwiftGeminiBridge {
    private let liveModel = FirebaseAI.firebaseAI().liveModel(modelName: "gemini-2.0-flash")

    func start() {
        Task {
            let session = try await liveModel.connect()
            // Handle streaming responses
            for try await message in session.responses {
                // Play audio back using AVPlayer or AudioEngine
            }
        }
    }

    func send(data: Data) {
        // session.sendAudioRealtime(data)
    }
}*/
import Foundation
import AVFoundation // For audio playback
import FirebaseAILogic // For the Gemini Live API
import shared // Your shared KMP module

// This class is the iOS implementation of the `GeminiLiveService` interface.
class iOSGeminiLiveHandler: NSObject, GeminiLiveService {
    private var liveSession: LiveSession?
    private var audioPlayer: AVAudioPlayer?

    // The callbacks object from the shared ViewModel will be stored here.
    private var callbacks: LiveSessionCallbacks?

    // Function to connect to the Gemini service. Called from the shared ViewModel.
    func connect(config: SessionConfig, callbacks: LiveSessionCallbacks) async throws -> KotlinUnit {
        self.callbacks = callbacks

        do {
            // 1. Initialize and configure the model from the shared config
            let liveModel = FirebaseAI.firebaseAI().liveModel(
                modelName: "gemini-1.5-flash", // It's good practice to use a stable model name
                systemInstruction: config.systemInstruction
            )

            // 2. Connect to the session
            liveSession = try await liveModel.connect()
            self.callbacks?.onOpen()

            // 3. Start a background Task to listen for responses from Gemini
            Task { [weak self] in
                do {
                    for try await message in self?.liveSession?.responses ?? [].async {
                        self?.handleGeminiResponse(message)
                    }
                    self?.callbacks?.onClose()
                } catch {
                    self?.callbacks?.onError(error: error)
                }
            }

            // 4. Start the microphone to send audio to Gemini
            startMicrophone()

            return KotlinUnit()
        } catch {
            self.callbacks?.onError(error: error)
            throw error
        }
    }

    // Function to disconnect. Called from the shared ViewModel.
    func disconnect() async throws -> KotlinByteArray? {
        stopMicrophone()
        liveSession?.close()
        liveSession = nil
        callbacks = nil
        // For simplicity, we are not returning the full audio recording on iOS for now.
        return nil
    }

    // Private method to handle responses from Gemini
    private func handleGeminiResponse(_ response: LiveServerContent) {
        // Handle the text part of the transcript
        if let text = response.text {
            let isUser = response.role == .user
            self.callbacks?.onMessage(text: text, isUser: isUser, timestamp: Int64(Date().timeIntervalSince1970 * 1000))
        }

        // Handle the audio part for playback
        if let audioPart = response.content?.parts.first(where: { $0 is InlineDataPart }) as? InlineDataPart {
            playAudio(data: audioPart.inlineData)
        }
    }

    // --- Audio Handling ---

    private func startMicrophone() {
        // TODO: This is where you would implement audio recording using AVAudioEngine.
        // It captures microphone input and sends it to `liveSession?.sendAudioRealtime(data)`.
        // This part is complex. For now, we can simulate sending data or leave it pending.
        print("Microphone recording should start here.")
    }

    private func stopMicrophone() {
        // TODO: Stop the AVAudioEngine.
        print("Microphone recording should stop here.")
    }

    private func playAudio(data: Data) {
        do {
            // Stop any currently playing audio
            audioPlayer?.stop()
            // Initialize the audio player with the new data from Gemini
            audioPlayer = try AVAudioPlayer(data: data)
            audioPlayer?.play()
        } catch {
            print("Error playing audio: \(error.localizedDescription)")
            self.callbacks?.onError(error: error)
        }
    }
}

