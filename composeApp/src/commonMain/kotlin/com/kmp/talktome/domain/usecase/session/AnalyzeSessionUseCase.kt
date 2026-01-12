package com.kmp.talktome.domain.usecase.session

import com.kmp.talktome.BuildKonfig
import com.kmp.talktome.domain.util.Result
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.Schema
import dev.shreyaspatil.ai.client.generativeai.type.content
import dev.shreyaspatil.ai.client.generativeai.type.generationConfig
import org.koin.core.component.KoinComponent


class AnalyzeSessionUseCase(
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

    suspend fun generateResponse(prompt: String): Result<String> {
        val inputContent = content { text(prompt) }

        return try {
            val response = generativeModel.generateContent(inputContent).text

            if (!response.isNullOrBlank()) {
                Result.Success(response)
            } else {
                Result.Error(Exception("Empty or null response from model"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
