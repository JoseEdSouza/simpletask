package com.dopae.simpletask.component

import android.content.Context
import android.content.Intent
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentManager
import com.dopae.simpletask.databinding.CardsLayoutTaskBinding
import com.dopae.simpletask.model.Tag
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.utils.TriggerType

class CardTaskComponent(
    private val context: Context,
    private val binding: CardsLayoutTaskBinding,
    supportFragmentManager: FragmentManager,
) {
    val cardTime = CardTimeTaskComponent(context, binding.cardTimeAddTask, supportFragmentManager)
    val cardLocal =
        CardLocalTaskComponent(context, binding.cardLocalAddTask, supportFragmentManager)
    val cardTag = CardTagComponent(context, binding.cardTagAddTask)
    private var readOnly = false
    private var task: Task? = null
    private var lastClickedCard: CardComponent? = null

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

    fun setReadOnly(task: Task): CardTaskComponent {
        readOnly = true
        this.task = task
        cardTag.setReadOnly(task)
        cardLocal.setReadOnly(task)
        cardTime.setReadOnly(task)
        return this
    }

    fun setWriteRead(): CardTaskComponent {
        readOnly = false
        cardTime.setWriteRead()
        cardLocal.setWriteRead()
        cardTag.setWriteRead()
        return this
    }

    private fun cardClicked(clickedCard: CardComponent) {
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

    fun setOnClickListener(listener: OnClickListener){
        binding.root.setOnClickListener(listener)
    }
}


