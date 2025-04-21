package com.ae22mp.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ae22mp.todoapp.data.local.dao.ToDoDao
import com.ae22mp.todoapp.model.ToDo

@Database(entities = [ToDo::class], version = 1, exportSchema = false)
abstract class ToDosAppDatabase : RoomDatabase(){

    abstract fun todoDao(): ToDoDao

    companion object {
        @Volatile
        private var INSTANCE: ToDosAppDatabase? = null

        fun getDatabase(context: Context): ToDosAppDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDosAppDatabase::class.java,
                    "todo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}