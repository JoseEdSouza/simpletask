package com.dopae.simpletask.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.dopae.simpletask.dao.DAO
import com.dopae.simpletask.dao.TagDAOImp
import com.dopae.simpletask.databinding.TaskAdapterBinding
import com.dopae.simpletask.model.Task

class TaskAdapter(private val taskDAO: DAO<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val positionIdMap = HashMap<Int, Int>()
    private var mListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val taskAdapterBinding =
            TaskAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(taskAdapterBinding)
    }

    override fun getItemCount(): Int = taskDAO.size()

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskDAO.getByPosition(position)
        positionIdMap[position] = task.id
        holder.taskName.text = task.name
        if (task.description == "") {
            holder.taskIndicators.removeView(holder.binding.imgViewDescription)
        }
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
        val taskIndicators = binding.linearLayoutTasksIndicators
        var taskName = binding.txtViewTaskName
        private val tagDAO = TagDAOImp.getInstance()

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mListener?.let {
                val id = positionIdMap[absoluteAdapterPosition] ?: RecyclerView.NO_POSITION
                if (id!= RecyclerView.NO_POSITION) {
                    it.onItemClick(id)
                }
            }
        }

        private fun setTaskIndicators(task: Task, indicatorsLayout: LinearLayout) {
            if (task.description == "")
                indicatorsLayout.removeView(binding.imgViewDescription)
        }

        private fun setTaskTags(task: Task, tagsLayout: LinearLayout, parent: ConstraintLayout) {
            if (task.tags.size <= 3) {
                parent.removeView(binding.imgViewTreeDots)
                binding.imgViewTag1.backgroundTintList =
                    tagDAO.get(task.tags.first())?.color?.getColorStateList()

            }


        }


    }
}