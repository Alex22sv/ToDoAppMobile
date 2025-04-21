package com.ae22mp.todoapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class ToDo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val finishDate: Long,
    var isDone: Boolean = false,
    val creationDate: Long = System.currentTimeMillis()
)