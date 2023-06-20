package com.example.myapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "task")
data class TaskModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val status: Int,
    val title: String,
    val time: String,
    val date: String
) : Serializable
