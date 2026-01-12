package com.kmp.talktome.domain.usecase.todo

import com.kmp.talktome.domain.model.TodoItem
import com.kmp.talktome.domain.model.TodoReflection
import com.kmp.talktome.domain.repository.TodoRepository
import com.kmp.talktome.domain.util.onError
import com.kmp.talktome.domain.util.onSuccess
import io.github.aakira.napier.Napier

private const val TAG = "AddTodoReflectionUseCase"

class AddTodoReflectionUseCase(
    val todoRepository: TodoRepository
) {
    suspend operator fun invoke(
        id: String,
        todoReflection: TodoReflection
    ) {
        todoRepository.getTodoById(id)
            .onSuccess { todoItem ->
                todoRepository.updateTodo(
                    todo = todoItem.copy(
                        reflection = todoReflection
                    )
                )
            }.onError { error ->
                Napier.e(message = "Error: ${error.message}", tag = TAG)
            }
    }
}