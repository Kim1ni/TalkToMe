package com.kmp.talktome.domain.usecase.todo

import com.kmp.talktome.domain.model.SessionAnalysis
import com.kmp.talktome.domain.model.TodoItem
import com.kmp.talktome.domain.repository.TodoRepository
import com.kmp.talktome.domain.util.Result
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CreateTodosFromAnalysisUseCase(
    private val todoRepository: TodoRepository
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        analysis: SessionAnalysis,
        sessionId: String,
        userId: String
    ): Result<Unit> {
        return try {
            analysis.actionItems.forEach { actionItem ->
                val todoItem = TodoItem(
                    id = Uuid.toString(),
                    userId = userId,
                    text = actionItem.text,
                    sessionId = sessionId,
                    category = actionItem.category
                )
                todoRepository.saveTodo(todoItem)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}