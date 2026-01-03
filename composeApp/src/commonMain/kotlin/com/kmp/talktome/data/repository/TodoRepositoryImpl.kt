package com.kmp.talktome.data.repository

import com.kmp.talktome.data.firebase.FirestoreCollections
import com.kmp.talktome.domain.model.TodoItem
import com.kmp.talktome.domain.model.TodoReflection
import com.kmp.talktome.domain.repository.TodoRepository
import com.kmp.talktome.domain.util.Result
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class TodoRepositoryImpl : TodoRepository {
    private val firestore = Firebase.firestore

    override fun getUserTodos(userId: String): Flow<List<TodoItem>> {
        return firestore
            .collection(FirestoreCollections.TODOS)
            .where { "userId" equalTo userId }
            .orderBy("createdAt", Direction.DESCENDING)
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.data<TodoItem>()
                    } catch (e: Exception) {
                        println("Error parsing todo: ${e.message}")
                        null
                    }
                }
            }
    }

    override fun getActiveTodos(userId: String): Flow<List<TodoItem>> {
        return firestore
            .collection(FirestoreCollections.TODOS)
            .where {
                "userId" equalTo userId
                "completed" equalTo false
            }
            .orderBy("createdAt", Direction.DESCENDING)
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.data<TodoItem>()
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }

    override fun getCompletedTodos(userId: String): Flow<List<TodoItem>> {
        return firestore
            .collection(FirestoreCollections.TODOS)
            .where {
                "userId" equalTo userId
                "completed" equalTo true
            }
            .orderBy("completedAt", Direction.DESCENDING)
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.data<TodoItem>()
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }

    override fun getTodosBySession(sessionId: String): Flow<List<TodoItem>> {
        return firestore
            .collection(FirestoreCollections.TODOS)
            .where { "sessionId" equalTo sessionId }
            .orderBy("createdAt", Direction.DESCENDING)
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.data<TodoItem>()
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }

    override suspend fun getTodoById(todoId: String): Result<TodoItem> {
        return try {
            val document = firestore
                .collection(FirestoreCollections.TODOS)
                .document(todoId)
                .get()

            val todo = document.data<TodoItem>()
            Result.Success(todo)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveTodo(todo: TodoItem): Result<Unit> {
        return try {
            firestore
                .collection(FirestoreCollections.TODOS)
                .document(todo.id)
                .set(todo)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateTodo(todo: TodoItem): Result<Unit> {
        return try {
            firestore
                .collection(FirestoreCollections.TODOS)
                .document(todo.id)
                .set(todo)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteTodo(todoId: String): Result<Unit> {
        return try {
            firestore
                .collection(FirestoreCollections.TODOS)
                .document(todoId)
                .delete()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun toggleTodoCompletion(
        todoId: String,
        reflection: TodoReflection?
    ): Result<Unit> {
        return try {
            val todo = getTodoById(todoId).getOrNull()
                ?: return Result.Error(Exception("Todo not found"))

            val updates = if (!todo.completed) {
                // Mark as complete
                mapOf(
                    "completed" to true,
                    "completedAt" to Clock.System.now().toEpochMilliseconds(),
                    "reflection" to reflection
                )
            } else {
                // Mark as incomplete
                mapOf(
                    "completed" to false,
                    "completedAt" to null,
                    "reflection" to null
                )
            }

            firestore
                .collection(FirestoreCollections.TODOS)
                .document(todoId)
                .update(updates)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
