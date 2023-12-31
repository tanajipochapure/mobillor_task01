package com.example.myapplication.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.model.TaskModel

@Database(entities = [TaskModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dataBaseDao(): DBDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "taskdb"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}