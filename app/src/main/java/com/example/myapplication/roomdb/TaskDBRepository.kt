package com.example.myapplication.roomdb

import androidx.lifecycle.LiveData
import com.example.myapplication.model.TaskModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskDBRepository(private val appDatabase: AppDatabase) {

    fun insertTask(task: TaskModel) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.dataBaseDao().insertTask(task)
        }
    }

    fun getAllTask(): LiveData<List<TaskModel>>? {
        return appDatabase.dataBaseDao().getAllTask()
    }

    fun sortedFind(orderBY: String): LiveData<List<TaskModel>>? {
        return appDatabase.dataBaseDao().sortedFind(orderBY)
    }

    fun deleteTask(task: TaskModel) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.dataBaseDao().deleteTask(task)
        }
    }

    fun updateTask(task: TaskModel) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.dataBaseDao().updateTask(task)
        }
    }
}
