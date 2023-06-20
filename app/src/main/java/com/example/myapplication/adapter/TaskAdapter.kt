package com.example.myapplication.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.TaskModel
import com.example.myapplication.roomdb.Listener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(private val userList: List<TaskModel>, private val listener: Listener) :
    RecyclerView.Adapter<TaskAdapter.Holder>() {
    class Holder(item: View) : RecyclerView.ViewHolder(item) {
        val deleteBtn: ImageView = itemView.findViewById(R.id.deleteBtn)
        val titleTv: TextView = itemView.findViewById(R.id.titleTv)
        val timeTv: TextView = itemView.findViewById(R.id.timeTv)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val taskModel = userList[position]
        holder.timeTv.text = taskModel.time
        holder.titleTv.text = taskModel.title

        holder.checkBox.isChecked = taskModel.status == 1

        holder.deleteBtn.setOnClickListener {
            listener.onClick(1, taskModel)
        }

        holder.titleTv.setOnClickListener {
            listener.onClick(2, taskModel)
        }

        if (taskModel.status == 1) {
            holder.titleTv.setTextColor(Color.BLACK)
            holder.titleTv.apply {
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
        } else {
            val sim = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val format = sim.format(Date())
            val taskTime = taskModel.time
            val parser = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val currentTime = parser.parse(format)?.time
            val deadlineTaskTime = parser.parse(taskTime)?.time

            //  if (taskModel.status == 0) {

            if (currentTime != null && deadlineTaskTime != null) {
                when {
                    currentTime < deadlineTaskTime -> holder.titleTv.setTextColor(Color.BLACK)
                    currentTime > deadlineTaskTime ->
                        holder.titleTv.setTextColor(Color.RED)

                    else -> holder.titleTv.setTextColor(Color.BLACK)
                }
            }
            //   }
        }
        holder.checkBox.setOnCheckedChangeListener(object : OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                val simNew = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
                val formatNew = simNew.format(Date())

                if (isChecked) {
                    listener.onClick(3, taskModel.copy(status = 1, date = formatNew))
                } else {
                    listener.onClick(3, taskModel.copy(status = 0, date = formatNew))
                }
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_task_list, parent, false)
        return Holder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getCurrentTime() {
        // val sim:SimpleDateFormat("hh:mm a")
    }
}