package com.example.myapplication.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.model.TaskModel

@Dao
interface DBDao {

    @Query("SELECT * FROM task")
    fun getAllTask(): LiveData<List<TaskModel>>?

    @Insert
    fun insertTask(task: TaskModel)

    @Delete
    fun deleteTask(task: TaskModel)

    @Update
    fun updateTask(task: TaskModel)

    @Query("SELECT * FROM task ORDER BY :orderBY ASC")
    fun sortedFind(orderBY: String?): LiveData<List<TaskModel>>?
}