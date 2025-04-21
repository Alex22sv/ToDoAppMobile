package com.ae22mp.todoapp.ui.viemodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData
import com.ae22mp.todoapp.data.ToDosAppDatabase
import com.ae22mp.todoapp.data.repository.ToDoRepository
import com.ae22mp.todoapp.model.ToDo

class ToDoViewModel(application: Application): AndroidViewModel(application){
    private val repository: ToDoRepository
    val allToDos: LiveData<List<ToDo>>

    init {
        val todoDao = ToDosAppDatabase.getDatabase(application).todoDao()
        repository = ToDoRepository(todoDao)
        allToDos = repository.allToDos.asLiveData()
    }
    fun insert(todo: ToDo) = viewModelScope.launch {
        repository.insert(todo)
    }
    fun update(todo: ToDo) = viewModelScope.launch {
        repository.update(todo)
    }
    fun delete(todo: ToDo) = viewModelScope.launch {
        repository.delete(todo)
    }
    fun toggleIsDone(todo: ToDo) = viewModelScope.launch {
        val updatedToDo = todo.copy(isDone = (!todo.isDone))
        repository.update(updatedToDo)
    }
}