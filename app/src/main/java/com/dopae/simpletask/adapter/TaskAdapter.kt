package com.dopae.simpletask.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.dopae.simpletask.dao.DAO
import com.dopae.simpletask.dao.TagDAOImp
import com.dopae.simpletask.databinding.TaskAdapterBinding
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.utils.TagColor
import kotlin.math.roundToInt
import kotlin.streams.toList

class TaskAdapter(private val taskDAO: DAO<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val taskAdapterBinding =
            TaskAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(taskAdapterBinding)
    }

    override fun getItemCount(): Int = taskDAO.size()

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskDAO.get(position+1)!!
        holder.taskName.text = task.name
        if (task.description == "") {
            holder.taskIndicators.removeView(holder.binding.imgViewDescription)
        }
    }


    inner class TaskViewHolder(val binding: TaskAdapterBinding) : RecyclerView.ViewHolder(binding.root){
        val taskIndicators = binding.linearLayoutTasksIndicators
        var taskName = binding.txtViewTaskName
        private val tagDAO = TagDAOImp.getInstance()
        private fun setTaskIndicators(task:Task,indicatorsLayout:LinearLayout){
            if(task.description == "")
                indicatorsLayout.removeView(binding.imgViewDescription)
        }

        private fun setTaskTags(task:Task,tagsLayout:LinearLayout,parent:ConstraintLayout){
            if(task.tags.size <= 3){
                parent.removeView(binding.imgViewTreeDots)
                binding.imgViewTag1.backgroundTintList = tagDAO.get(task.tags.first())?.color?.getColorStateList()

            }




        }


    }
}