package com.example.myapplication

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.TaskAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.model.TaskModel
import com.example.myapplication.roomdb.Listener
import com.example.myapplication.roomdb.TaskFactory
import com.example.myapplication.roomdb.TaskViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    private lateinit var context: Context
    private val taskViewModel: TaskViewModel by lazy {
        ViewModelProvider(this, TaskFactory(applicationContext))[TaskViewModel::class.java]
    }
    private lateinit var binding: ActivityMainBinding
    private var localList: List<TaskModel> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)
        init()
        initObserver()
        setEventHandler()
    }

    private fun setEventHandler() {
        binding.moreIv.setOnClickListener {
            showSortBottomDialog()
        }
        binding.fabBtn.setOnClickListener {
//            startActivity(
//                Intent(context, AddTaskActivity::class.java)
//                    .putExtra("type", 1)
//            )

            showBottomDialog(1, null)
        }
    }

    private fun showBottomDialog(type: Int, taskModel: TaskModel?) {

        val view = layoutInflater.inflate(R.layout.activity_add_task, null)

        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setContentView(view)

        val cancelBtn = view.findViewById<Button>(R.id.cancelBtn)
        val addBtn = view.findViewById<Button>(R.id.addBtn)
        val taskEt = view.findViewById<AppCompatEditText>(R.id.taskEt)
        val timeEt = view.findViewById<AppCompatEditText>(R.id.timeEt)
        if (type == 2) {
            taskEt.setText(taskModel!!.title)
            timeEt.setText(taskModel.time)
        }
        addBtn.setOnClickListener {
            if (taskEt.text.toString().trim().isEmpty()) {
                Toast.makeText(context, "Please enter task", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (timeEt.text.toString().trim().isEmpty()) {
                Toast.makeText(context, "Please select time", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (type == 2) {

                val model = TaskModel(
                    taskModel!!.id, taskModel.status,
                    taskEt.text.toString().trim(),
                    timeEt.text.toString().trim(),
                )
                Toast.makeText(context, "Task updated successfully", Toast.LENGTH_LONG).show()
                taskViewModel.updateTask(model)

                bottomSheetDialog.dismiss()
            } else {
                val model = TaskModel(
                    0, 0,
                    taskEt.text.toString().trim(),
                    timeEt.text.toString().trim(),
                )
                taskViewModel.insertTask(model)
                Toast.makeText(context, "Task inserted successfully", Toast.LENGTH_LONG).show()
            }

            taskEt.setText("")
            timeEt.setText("")

            bottomSheetDialog.dismiss()

            //status 0 = NA
            //status 1 = completed
        }


        timeEt.setOnClickListener {

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

                        timeEt.setText(
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


        cancelBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun initObserver() {
        taskViewModel.getAllTask()?.observe(this) {
            localList = it
            setAdapter(localList)
        }
    }

    private fun setAdapter(localList: List<TaskModel>) {

        binding.recyclerView.adapter = TaskAdapter(localList, object : Listener {
            override fun onClick(type: Int, task: TaskModel) {
                if (type == 1) {
                    val alertDialog = AlertDialog.Builder(context)
                    alertDialog.setCancelable(false)
                    alertDialog.setTitle("Warning")
                    alertDialog.setMessage("Do you want to delete ${task.title}, this action can't be undone.")
                    alertDialog.setPositiveButton(
                        "OK"
                    ) { dialog, _ ->
                        dialog.dismiss()
                        taskViewModel.deleteTask(task)
                    }
                    alertDialog.setNegativeButton(
                        "Cancel"
                    ) { dialog, _ -> dialog.dismiss() }

                    alertDialog.create().show()
                }
                if (type == 2) {
//                        startActivity(
//                            Intent(context, AddTaskActivity::class.java)
//                                .putExtra("type", type)
//                                .putExtra("data", task)
//                        )

                    showBottomDialog(2, task)
                }
                if (type == 3) {
                    taskViewModel.updateTask(task)
                    Toast.makeText(context, "Task updated successfully", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })

    }

    private fun init() {
        context = this@MainActivity
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.sort_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.completed ->
                if (localList.isNotEmpty()) {
//                    localList.sortedBy { it.status == 1 }
//                    setAdapter(localList)
                    showSortBottomDialog()
                    return true
                }

            R.id.pending ->
                if (localList.isNotEmpty()) {


                    return true
                }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSortBottomDialog() {

        val view = layoutInflater.inflate(R.layout.row_sort_list, null)

        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setContentView(view)

        val completedTv = view.findViewById<AppCompatTextView>(R.id.completedTv)
        val pendingTv = view.findViewById<AppCompatTextView>(R.id.pendingTv)
        val cancelTv = view.findViewById<AppCompatTextView>(R.id.cancelTv)


        completedTv.setOnClickListener {

            if (localList.isNotEmpty()) {
                val sortedBy = localList.sortedBy { it.status == 0 }
                setAdapter(sortedBy)
            }
            bottomSheetDialog.dismiss()
        }
        pendingTv.setOnClickListener {
            if (localList.isNotEmpty()) {
                val sortedBy = localList.sortedBy { it.status == 1 }
                setAdapter(sortedBy)
            }
            bottomSheetDialog.dismiss()
            bottomSheetDialog.dismiss()
        }


        cancelTv.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()

    }

}