package com.dopae.simpletask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.dopae.simpletask.component.TaskAdapterComponent
import com.dopae.simpletask.databinding.TaskAdapterBinding
import com.dopae.simpletask.model.Tag
import com.dopae.simpletask.model.Task

class TaskAdapter(private val taskList: List<Task>, private val tagList: List<Tag>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private val positionIdMap = HashMap<Int, String>()
    private var mListener: OnItemClickListener? = null
    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val taskAdapterBinding =
            TaskAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return TaskViewHolder(taskAdapterBinding)
    }


    override fun getItemCount(): Int = taskList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        positionIdMap[position] = task.id
        TaskAdapterComponent(context, holder.binding, task, tagList).init()
    }

    fun interface OnItemClickListener {
        fun onItemClick(id: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }


    inner class TaskViewHolder(val binding: TaskAdapterBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mListener?.let {
                val id = positionIdMap[absoluteAdapterPosition] ?: ""
                if (id != "") {
                    it.onItemClick(id)
                }
            }
        }

    }
}