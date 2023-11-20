package com.dopae.simpletask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.dopae.simpletask.controller.TaskAdapterController
import com.dopae.simpletask.dao.TaskDAOImp
import com.dopae.simpletask.databinding.TaskAdapterBinding

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private val taskDAO = TaskDAOImp.getInstance()
    private val positionIdMap = HashMap<Int, Int>()
    private var mListener: OnItemClickListener? = null
    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val taskAdapterBinding =
            TaskAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return TaskViewHolder(taskAdapterBinding)
    }


    override fun getItemCount(): Int = taskDAO.size()

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskDAO.getByPosition(position)
        positionIdMap[position] = task.id
        TaskAdapterController(context, holder.binding, task).init()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
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
                val id = positionIdMap[absoluteAdapterPosition] ?: RecyclerView.NO_POSITION
                if (id != RecyclerView.NO_POSITION) {
                    it.onItemClick(id)
                }
            }
        }

    }
}