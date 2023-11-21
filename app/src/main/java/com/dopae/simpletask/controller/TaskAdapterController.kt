package com.dopae.simpletask.controller

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import com.dopae.simpletask.R
import com.dopae.simpletask.dao.TagDAOImp
import com.dopae.simpletask.dao.TaskDAOImp
import com.dopae.simpletask.databinding.TaskAdapterBinding
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.utils.TriggerType

class TaskAdapterController(
    private val context: Context,
    binding: TaskAdapterBinding,
    private val task: Task
) {
    private val tagDAO = TagDAOImp.getInstance()
    private val taskCard = binding.root
    private val taskName = binding.txtViewTaskName
    private val timeIndicator = binding.imgViewCalendar
    private val localIndicator = binding.imgViewLocation
    private val descriptionIndicator = binding.imgViewDescription
    private val taskCheckBox = binding.imgBtnCheckbox
    private val treeDotsIndicator = binding.imgViewTreeDots
    private val tagIndicator1 = binding.imgViewTag1
    private val tagIndicator2 = binding.imgViewTag2
    private val tagIndicator3 = binding.imgViewTag3
    private val tagIndicators = listOf(tagIndicator1, tagIndicator2, tagIndicator3)

    fun init() {
        build()
        taskCheckBox.setOnClickListener { flipCheckbox() }
    }

    private fun build() {
        taskName.text = task.name
        descriptionIndicator.visibility = if (!task.hasDescription) View.GONE else View.VISIBLE
        localIndicator.visibility = View.GONE
        timeIndicator.visibility = View.GONE
        task.trigger?.let {
            when (it.type) {
                TriggerType.TIME -> timeIndicator.visibility = View.VISIBLE
                else -> localIndicator.visibility = View.VISIBLE
            }
        }

        if (task.concluded)
            taskCheckBox.setImageResource(R.drawable.checkbox_concluded)
        else
            taskCheckBox.setImageResource(R.drawable.checkbox_empty)
        treeDotsIndicator.visibility = if (task.numTags <= 3) View.GONE else View.VISIBLE
        if (task.numTags > 0) {
            val zip = tagIndicators zip task.tags
            zip.forEach {
                val tag = tagDAO.get(it.second)
                tag?.let { t ->
                    it.first.imageTintList = t.color.getColorStateList(context)
                    it.first.visibility = View.VISIBLE
                } ?: run {
                    task.removeTag(it.second)
                }
            }
        }
    }


    private fun flipCheckbox() {
        task.flipStatus()
        TransitionManager.beginDelayedTransition(taskCard, AutoTransition())
        taskCheckBox
            .setImageResource(if (task.concluded) R.drawable.checkbox_concluded else R.drawable.checkbox_empty)
    }


}