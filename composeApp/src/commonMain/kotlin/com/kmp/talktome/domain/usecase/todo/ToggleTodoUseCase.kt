package com.kmp.talktome.domain.usecase.todo

import com.kmp.talktome.domain.model.TodoReflection
import com.kmp.talktome.domain.repository.TodoRepository
import com.kmp.talktome.domain.util.Result

class ToggleTodoUseCase(
    private val toDoRepository: TodoRepository
) {
    suspend operator fun invoke(
        toDoId: String,
        reflection: TodoReflection?
    ): Result<Unit> {
        return toDoRepository.toggleTodoCompletion(toDoId, reflection)
    }
}