package com.example.myapplication.roomdb

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TaskFactory(private val applicationContext: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return TaskViewModel(
            dbRepository = TaskDBRepository(
                AppDatabase.getDatabase(applicationContext)
            )
        ) as T
    }
}