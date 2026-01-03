package com.kmp.talktome.data.live

import com.kmp.talktome.BuildKonfig
import com.kmp.talktome.domain.live.GeminiAnalysisService
import com.kmp.talktome.domain.model.SessionAnalysis
import com.kmp.talktome.domain.model.TranscriptMessage
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.Schema
import dev.shreyaspatil.ai.client.generativeai.type.content
import dev.shreyaspatil.ai.client.generativeai.type.generationConfig
import org.koin.core.component.KoinComponent

class GeminiAnalysisServiceImpl : GeminiAnalysisService {

    /**
     * Analyzes a transcript of a session and returns a structured [SessionAnalysis].
     *
     * @param transcript A list of [TranscriptMessage] objects representing the conversation.
     * @return A [SessionAnalysis] object containing the model's insights.
     */
    override suspend fun analyzeTranscript(transcript: List<TranscriptMessage>): Result<SessionAnalysis> {
        return try {
            val conversationText =
                transcript.joinToString("\n") { transcriptMessage -> "${transcriptMessage.role}: ${transcriptMessage.text}" }

            val prompt = """
                You are an expert therapist supervisor. Analyze the following therapy session transcript.
                Provide a structured JSON output by calling the 'sessionAnalysis' function.
                Your analysis should contain:
                1. A sentiment score (0-100, where 100 is very positive/happy, 0 is very negative/sad).
                2. A one-word mood description (e.g., "Anxious", "Reflective").
                3. A brief summary of the session (maximum 2 sentences).
                4. A list of 2-3 specific areas of improvement for the user (psychological or behavioral).
                5. A list of 3-5 key topics/keywords discussed.
                6. A list of 3-5 concrete, actionable "to-do" items. For each item, assign a category from the available options.
    
                Transcript:
                $conversationText
            """.trimIndent()
            //val prompt = generatePrompt(transcript)
            val response = GenerationUseCase(SessionAnalysis.getSchema()).generateResponse(prompt)

            return if (response != null) {
                Result.success(response as Any as SessionAnalysis)
            } else {
                Result.failure(Exception("Failed to generate analysis"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GenerationUseCase(
    schema: Schema<*>
) : KoinComponent {

    private val generativeModel = GenerativeModel(
        modelName = BuildKonfig.MODEL_NAME,
        apiKey = BuildKonfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
            responseSchema = schema
        }
    )

    suspend fun generateResponse(prompt: String): String? {
        val inputContent = content { text(prompt) }

        return try {
            val response = generativeModel.generateContent(inputContent).text
            response
        } catch (e: Exception) {
            //Napier.e("$e ${e.cause?.message}")
            throw e
        }
    }
}

