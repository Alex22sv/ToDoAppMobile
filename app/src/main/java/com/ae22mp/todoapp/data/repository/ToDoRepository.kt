package com.ae22mp.todoapp.data.repository

import com.ae22mp.todoapp.data.local.dao.ToDoDao
import com.ae22mp.todoapp.model.ToDo
import kotlinx.coroutines.flow.Flow

class ToDoRepository(private val todoDao: ToDoDao){
    suspend fun insert(todo: ToDo) {
        todoDao.insert(todo)
    }

    suspend fun update(todo: ToDo){
        todoDao.update(todo)
    }

    suspend fun delete(todo: ToDo){
        todoDao.delete(todo)
    }

    var allToDos: Flow<List<ToDo>> = todoDao.getAllToDos()

    suspend fun getToDoById(id: Int): ToDo? {
        return todoDao.getToDosById(id)
    }
}