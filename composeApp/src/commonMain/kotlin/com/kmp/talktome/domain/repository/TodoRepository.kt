package com.kmp.talktome.domain.repository

import com.kmp.talktome.domain.model.TodoItem
import com.kmp.talktome.domain.model.TodoReflection
import com.kmp.talktome.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    fun getUserTodos(userId: String): Flow<List<TodoItem>>

    fun getActiveTodos(userId: String): Flow<List<TodoItem>>

    fun getCompletedTodos(userId: String): Flow<List<TodoItem>>

    fun getTodosBySession(sessionId: String): Flow<List<TodoItem>>

    suspend fun getTodoById(todoId: String): Result<TodoItem>

    suspend fun saveTodo(todo: TodoItem): Result<Unit>

    suspend fun updateTodo(todo: TodoItem): Result<Unit>

    suspend fun deleteTodo(todoId: String): Result<Unit>

    suspend fun toggleTodoCompletion(todoId: String, reflection: TodoReflection?): Result<Unit>

}