package com.kmp.talktome.domain.model

import dev.shreyaspatil.ai.client.generativeai.type.FunctionType
import dev.shreyaspatil.ai.client.generativeai.type.Schema
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class SessionAnalysis(
    val sentimentScore: Int, // 0-100
    val mood: String,
    val summary: String,
    val areasOfImprovement: List<String> = emptyList(),
    val topics: List<String> = emptyList(),
    val actionItems: List<ActionItem> = emptyList()
) {
    /**
     * Get sentiment level category
     */
    fun getSentimentLevel(): SentimentLevel {
        return when {
            sentimentScore >= 70 -> SentimentLevel.POSITIVE
            sentimentScore >= 40 -> SentimentLevel.NEUTRAL
            else -> SentimentLevel.NEGATIVE
        }
    }

    /**
     * Get color for sentiment score
     */
    fun getSentimentColor(): Int {
        return when (getSentimentLevel()) {
            SentimentLevel.POSITIVE -> 0xFF4CAF50.toInt() // Green
            SentimentLevel.NEUTRAL -> 0xFFFFC107.toInt() // Amber
            SentimentLevel.NEGATIVE -> 0xFFF44336.toInt() // Red
        }
    }

    companion object {
        /**
         * Get the schema for session analysis, useful for generative AI function calling.
         */
        fun getSchema(): Schema<JsonObject> {
            return Schema(
                name = "sessionAnalysis",
                description = "A detailed analysis of a user's therapy or journaling session.",
                type = FunctionType.OBJECT,
                properties = mapOf(
                    "sentimentScore" to Schema(
                        name = "sentimentScore",
                        description = "A score from 0 to 100 representing the sentiment of the session. 0 is very negative, 100 is very positive.",
                        type = FunctionType.NUMBER,
                        nullable = false
                    ),
                    "mood" to Schema(
                        name = "mood",
                        description = "A single word or short phrase describing the dominant mood of the session (e.g., 'Anxious', 'Reflective', 'Hopeful').",
                        type = FunctionType.STRING,
                        nullable = false
                    ),
                    "summary" to Schema(
                        name = "summary",
                        description = "A concise summary of the key points discussed in the session.",
                        type = FunctionType.STRING,
                        nullable = false
                    ),
                    "areasOfImprovement" to Schema(
                        name = "areasOfImprovement",
                        description = "A list of specific areas where the user can focus on personal growth.",
                        type = FunctionType.ARRAY,
                        items = Schema(
                            name = "areasOfImprovement",
                            description = "A single area of improvement.",
                            type = FunctionType.STRING
                        ),
                        nullable = true
                    ),
                    "topics" to Schema(
                        name = "topics",
                        description = "A list of the main topics or themes that were discussed.",
                        type = FunctionType.ARRAY,
                        items = Schema(
                            name = "topics",
                            description = "A single topic.",
                            type = FunctionType.STRING
                        ),
                        nullable = true
                    ),
                    "actionItems" to Schema(
                        name = "actionItems",
                        description = "A list of concrete action items or tasks for the user to complete.",
                        type = FunctionType.ARRAY,
                        items = Schema(
                            name = "ActionItem",
                            description = "A single action item.",
                            type = FunctionType.OBJECT,
                            properties = mapOf(
                                "text" to Schema(
                                    name = "text",
                                    description = "The description of the action item.",
                                    type = FunctionType.STRING,
                                    nullable = false
                                ),
                                "category" to Schema(
                                    name = "category",
                                    description = "The category of the action item (e.g., COGNITIVE, BEHAVIORAL, SOCIAL).",
                                    type = FunctionType.STRING,
                                    nullable = false
                                )
                            ),
                            required = listOf("text", "category")
                        ),
                        nullable = true
                    )
                ),
                required = listOf("sentimentScore", "mood", "summary")
            )
        }
    }
}

@Serializable
data class ActionItem(
    val text: String,
    val category: TodoCategory
)

enum class SentimentLevel {
    POSITIVE,
    NEUTRAL,
    NEGATIVE
}
