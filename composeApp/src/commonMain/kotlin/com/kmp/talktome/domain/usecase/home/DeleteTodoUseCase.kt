package com.kmp.talktome.domain.usecase.home

import com.kmp.talktome.domain.repository.TodoRepository
import com.kmp.talktome.domain.util.Result

class DeleteTodoUseCase(
    private val todoRepository: TodoRepository
) {
    suspend operator fun invoke(todoId: String): Result<Unit> {
        return todoRepository.deleteTodo(todoId)
    }
}