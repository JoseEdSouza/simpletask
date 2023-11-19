package com.dopae.simpletask.controller

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import com.dopae.simpletask.R
import com.dopae.simpletask.dao.TaskDAOImp
import com.dopae.simpletask.databinding.TaskAdapterBinding
import com.dopae.simpletask.model.Task

class CardTaskController(binding: TaskAdapterBinding, private val task: Task) {
    private val taskCard = binding.root
    private val taskName = binding.txtViewTaskName
    val timeIndicator = binding.imgViewCalendar
    val localIndicator = binding.imgViewLocation
    private val descriptionIndicator = binding.imgViewDescription
    private val taskCheckBox = binding.imgBtnCheckbox

    fun init() {
        set()
        taskCheckBox.setOnClickListener { flipCheckbox() }
    }

    fun set() {
        taskName.text = task.name
        if (!task.hasDescription)
            descriptionIndicator.visibility = View.GONE
        if (task.concluded)
            taskCheckBox.setImageResource(R.drawable.checkbox_concluded)
    }

    private fun flipCheckbox() {
        task.concluded = !task.concluded
        TransitionManager.beginDelayedTransition(taskCard, AutoTransition())
        taskCheckBox
            .setImageResource(if (task.concluded) R.drawable.checkbox_concluded else R.drawable.checkbox_empty)
        TaskDAOImp.getInstance().update(task.id, task)
    }


}