package com.example.myapplication

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.ActivityAddTaskBinding
import com.example.myapplication.model.TaskModel
import com.example.myapplication.roomdb.TaskFactory
import com.example.myapplication.roomdb.TaskViewModel
import java.util.Calendar


class AddTaskActivity : AppCompatActivity() {
    private lateinit var context: Context
    private val taskViewModel: TaskViewModel by lazy {
        ViewModelProvider(this, TaskFactory(applicationContext))[TaskViewModel::class.java]
    }
    private lateinit var binding: ActivityAddTaskBinding
    private var type = 1
    private lateinit var taskModel: TaskModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_task)
        setContentView(binding.root)
        init()
        setEventHandler()
    }

    private fun setEventHandler() {
        binding.addBtn.setOnClickListener {
            if (binding.taskEt.text.toString().trim().isEmpty()) {
                Toast.makeText(context, "Please enter task", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (binding.timeEt.text.toString().trim().isEmpty()) {
                Toast.makeText(context, "Please select time", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (type == 2) {
                val taskModel = TaskModel(
                    taskModel.id, taskModel.status,
                    binding.taskEt.text.toString().trim(),
                    binding.timeEt.text.toString().trim(),
                )
                Toast.makeText(context, "Task updated successfully", Toast.LENGTH_LONG).show()
                taskViewModel.updateTask(taskModel)
            } else {
                val taskModel = TaskModel(
                    0, 0,
                    binding.taskEt.text.toString().trim(),
                    binding.timeEt.text.toString().trim(),
                )
                taskViewModel.insertTask(taskModel)
                Toast.makeText(context, "Task inserted successfully", Toast.LENGTH_LONG).show()
            }

            binding.taskEt.setText("")
            binding.timeEt.setText("")
            type = 1
            //status 0 = NA
            //status 1 = completed
        }

        binding.cancelBtn.setOnClickListener {
            onDestroy()
        }

        binding.timeEt.setOnClickListener {

            val calenderInstance: Calendar = Calendar.getInstance()
            val hr: Int = calenderInstance.get(Calendar.HOUR_OF_DAY)
            val min: Int = calenderInstance.get(Calendar.MINUTE)
            val onTimeListener =
                OnTimeSetListener { view, hourOfDay, minute ->
                    if (view.isShown) {
                        calenderInstance.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calenderInstance.set(Calendar.MINUTE, minute)

                        var hour = hourOfDay
                        val format: String
                        if (hour == 0) {
                            hour += 12
                            format = "AM"
                        } else if (hour == 12) {
                            format = "PM"
                        } else if (hour > 12) {
                            hour -= 12
                            format = "PM"
                        } else {
                            format = "AM"
                        }

                        binding.timeEt.setText(
                            "$hour:${
                                calenderInstance.get(
                                    Calendar.MINUTE
                                )
                            } $format"
                        )
                    }
                }
            val timePickerDialog = TimePickerDialog(
                context,
                onTimeListener, hr, min, false
            )
            timePickerDialog.setTitle("Time")
            timePickerDialog.setCancelable(false)
            timePickerDialog.show()
        }
    }

    private fun init() {
        context = this@AddTaskActivity
        type = intent.getIntExtra("type", 1)
        if (type == 2) {
            taskModel = intent.getSerializableExtra("data") as TaskModel
            binding.taskEt.setText(taskModel.title)
            binding.timeEt.setText(taskModel.time)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}