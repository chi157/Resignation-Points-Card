package com.chi157.resignationpointscard.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_items ORDER BY createdAt ASC")
    fun getAllTodos(): Flow<List<TodoItem>>

    @Insert
    suspend fun insertTodo(item: TodoItem)

    @Update
    suspend fun updateTodo(item: TodoItem)

    @Delete
    suspend fun deleteTodo(item: TodoItem)

    @Query("DELETE FROM todo_items")
    suspend fun deleteAllTodos()
}
