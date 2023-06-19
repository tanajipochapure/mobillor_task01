package com.example.myapplication.roomdb

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.TaskModel

class TaskViewModel(private val dbRepository: TaskDBRepository) : ViewModel() {


    fun insertTask(task: TaskModel) {
        dbRepository.insertTask(task)
    }

    fun getAllTask(): LiveData<List<TaskModel>>? {
        return dbRepository.getAllTask()
    }

    fun sortedFind(orderBY: String): LiveData<List<TaskModel>>? {
        return dbRepository.sortedFind(orderBY)
    }

    fun deleteTask(task: TaskModel) {
        dbRepository.deleteTask(task)
    }

    fun updateTask(task: TaskModel) {
        dbRepository.updateTask(task)
    }
}