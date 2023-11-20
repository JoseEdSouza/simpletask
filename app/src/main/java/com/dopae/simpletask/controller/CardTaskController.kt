package com.dopae.simpletask.controller

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import androidx.fragment.app.FragmentManager
import com.dopae.simpletask.databinding.CardsLayoutTaskBinding
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.utils.TriggerType

class CardTaskController(
    private val context: Context,
    private val binding: CardsLayoutTaskBinding,
    supportFragmentManager: FragmentManager
) {
    val cardTime = CardTimeTaskController(context, binding.cardTimeAddTask, supportFragmentManager)
    val cardLocal =
        CardLocalTaskController(context, binding.cardLocalAddTask, supportFragmentManager)
    val cardTag = CardTagController(context, binding.cardTagAddTask)
    private var readOnly = false
    private var task: Task? = null
    private var lastClickedCard: CardController? = null

    fun init() {
        cardTag.init()
        cardTime.init()
        cardLocal.init()
        if (!readOnly) {
            cardTime.setOnClickListener { cardClicked(cardTime) }
            cardLocal.setOnClickListener { cardClicked(cardLocal) }
        } else
            initReadOnlyLayout()

    }

    private fun initReadOnlyLayout() {
        val cardLocal = binding.cardLocalAddTask.cardTaskLocal
        val cardTime = binding.cardTimeAddTask.cardTaskTime
        var type: TriggerType? = null
        task?.let {
            it.trigger?.let { tr ->
                type = if (tr.type == TriggerType.TIME) TriggerType.TIME else TriggerType.LOCAL
            }
        }
        type?.let {
            when (it) {
                TriggerType.TIME -> cardLocal.visibility = View.GONE
                else -> cardTime.visibility = View.GONE
            }
        }


    }

    fun setReadOnly(task: Task): CardTaskController {
        readOnly = true
        this.task = task
        cardTag.setReadOnly(task)
        cardLocal.setReadOnly(task)
        cardTime.setReadOnly(task)
        return this
    }

    fun setWriteRead(): CardTaskController {
        readOnly = false
        cardTime.setWriteRead()
        cardLocal.setWriteRead()
        cardTag.setWriteRead()
        return this
    }

    private fun cardClicked(clickedCard: CardController) {
        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
        lastClickedCard?.let {
            if (it == clickedCard) {
                clickedCard.changeState()
            } else {
                clickedCard.changeState()
                if (it.isActivated)
                    it.changeState()
                lastClickedCard = clickedCard
            }

        } ?: run {
            clickedCard.changeState()
            lastClickedCard = clickedCard
        }
    }
}


