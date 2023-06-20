package com.example.myapplication

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.TaskAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.model.TaskModel
import com.example.myapplication.roomdb.Listener
import com.example.myapplication.roomdb.TaskFactory
import com.example.myapplication.roomdb.TaskViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*


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

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 10 && binding.fabBtn.isExtended)
                    binding.fabBtn.shrink()

                if (dy < -10 && !binding.fabBtn.isExtended)
                    binding.fabBtn.extend()

                if (!binding.recyclerView.canScrollVertically(-1))
                    binding.fabBtn.extend()
            }
        })
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

                val sim = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
                val format = sim.format(Date())

                val model = TaskModel(
                    taskModel!!.id, taskModel.status,
                    taskEt.text.toString().trim(),
                    timeEt.text.toString().trim(),
                    format
                )
                Toast.makeText(context, "Task updated successfully", Toast.LENGTH_LONG).show()
                taskViewModel.updateTask(model)

                bottomSheetDialog.dismiss()
            } else {

                var status = 0
                val sim = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val format = sim.format(Date())
                val taskTime = timeEt.text.toString().trim()
                val parser = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val currentTime = parser.parse(format)?.time
                val deadlineTaskTime = parser.parse(taskTime)?.time
                if (currentTime != null && deadlineTaskTime != null) {
                    when {
                        currentTime < deadlineTaskTime -> status = 2
                        currentTime > deadlineTaskTime ->
                            status = 2

                        else -> status = 0
                    }
                }

                val simNew = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
                val formatNew = simNew.format(Date())
                val model = TaskModel(
                    0, status,
                    taskEt.text.toString().trim(),
                    timeEt.text.toString().trim(),
                    formatNew
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

    private fun showSortBottomDialog() {

        val view = layoutInflater.inflate(R.layout.row_sort_list, null)

        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setContentView(view)

        val allDateTv = view.findViewById<AppCompatTextView>(R.id.allDateTv)
        val currentDateTv = view.findViewById<AppCompatTextView>(R.id.currentDateTv)
        val otherDateTv = view.findViewById<AppCompatTextView>(R.id.otherDateTv)
        val cancelTv = view.findViewById<AppCompatTextView>(R.id.cancelTv)

        allDateTv.setOnClickListener {

            if (localList.isNotEmpty()) {
                setAdapter(localList)
            }
            bottomSheetDialog.dismiss()
        }

        currentDateTv.setOnClickListener {
            val simNew = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
            val formatNew = simNew.format(Date())
            if (localList.isNotEmpty()) {
                val sortedBy = localList.filter { it.date == formatNew }
                setAdapter(sortedBy)
            }
            bottomSheetDialog.dismiss()
        }
        otherDateTv.setOnClickListener {
            val simNew = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
            val formatNew = simNew.format(Date())
            if (localList.isNotEmpty()) {
                val sortedBy = localList.filter { it.date != formatNew }
                setAdapter(sortedBy)
            }
            bottomSheetDialog.dismiss()
        }


        cancelTv.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()

    }

}