package com.dopae.simpletask.controller

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.View.OnClickListener
import androidx.fragment.app.FragmentManager
import com.dopae.simpletask.databinding.CardTaskLocalReminderBinding
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.utils.LocalOption
import java.util.Date

class CardLocalTaskController(
    private val binding: CardTaskLocalReminderBinding,
    private val supportFragmentManager: FragmentManager
) :CardController {
    private val cardExpandOptions = binding.constraintLayoutCardTaskLocalExpandOptions
    private val cardSelectedLocal = binding.txtViewSelectedLocal
    private val card = binding.root
    private val localOptionButton = binding.btnLocalOptionCardTaskLocal
    private val localButton = binding.btnTimeCardTaskLocal
    private var readOnly = false
    private var activated = false
    private var selectedLocalOption: LocalOption? = null
    private var selectedPlace = null
    private var task: Task? = null


    override fun init() {
        cardExpandOptions.visibility = View.GONE
        cardSelectedLocal.visibility = View.GONE
        card.setOnClickListener { changeState() }
    }

    override fun setOnClickListener(onClickListener: OnClickListener) {
        card.setOnClickListener(onClickListener)
    }

    fun setReadOnly(task: Task): CardLocalTaskController {
        this.task = task
        readOnly = true
        return this
    }

    fun setWriteRead(): CardLocalTaskController {
        readOnly = false
        task = null
        return this
    }

    override val isActivated: Boolean
        get() = activated

    override fun changeState() {
        activated = !activated
        with(cardExpandOptions) {
            visibility = if (visibility == View.GONE) View.VISIBLE else View.GONE
        }
        // todo - change State if readOnly
    }
}