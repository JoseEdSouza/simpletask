package com.dopae.simpletask.component

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import com.dopae.simpletask.R
import com.dopae.simpletask.dao.TaskDAOFirebase
import com.dopae.simpletask.databinding.TaskAdapterBinding
import com.dopae.simpletask.model.Tag
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.utils.TriggerType
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class TaskAdapterComponent(
    private val context: Context,
    val binding: TaskAdapterBinding,
    private val task: Task,
    private val tagList: List<Tag>
) {
    private val dao = TaskDAOFirebase.getInstance()
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
    private val handler = CoroutineExceptionHandler { context, _ ->
        context.cancel()
    }
    private val scope = CoroutineScope(Dispatchers.IO + handler)
    private val updateTaskJob = scope.launch(start = CoroutineStart.LAZY) { dao.update(task.id,task) }

    fun init() {
        build()
        taskCheckBox.setOnClickListener { flipCheckbox() }
    }

    private fun build() {
        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
        taskName.text = task.name
        descriptionIndicator.visibility = if (!task.hasDescription) View.GONE else View.VISIBLE
        localIndicator.visibility = View.GONE
        timeIndicator.visibility = View.GONE
        tagIndicators.forEach { it.visibility = View.INVISIBLE }
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
            val filteredTags = tagList.filter { it.id in task.tags }
            val zip = tagIndicators zip filteredTags
            zip.forEach {
                it.first.imageTintList = it.second.color.getColorStateList(context)
                it.first.visibility = View.VISIBLE
            }
        }
    }


    private fun flipCheckbox() {
        task.flipStatus()
        TransitionManager.beginDelayedTransition(taskCard, AutoTransition())
        taskCheckBox
            .setImageResource(if (task.concluded) R.drawable.checkbox_concluded else R.drawable.checkbox_empty)
        with(updateTaskJob){
            if(!isActive || isCompleted)
                start()
        }

    }


}