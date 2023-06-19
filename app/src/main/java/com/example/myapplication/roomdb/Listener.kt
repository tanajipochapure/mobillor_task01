package com.example.myapplication.roomdb

import com.example.myapplication.model.TaskModel

interface Listener {

    fun onClick(type: Int, task: TaskModel)
}