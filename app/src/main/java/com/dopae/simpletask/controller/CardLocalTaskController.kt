package com.dopae.simpletask.controller

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.dopae.simpletask.R
import com.dopae.simpletask.databinding.CardTaskLocalReminderBinding
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.model.Trigger
import com.dopae.simpletask.utils.LocalOption
import java.util.Date

class CardLocalTaskController(
    private val context: Context,
    binding: CardTaskLocalReminderBinding,
    private val supportFragmentManager: FragmentManager
) : CardController {
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
        if (readOnly) {
            initSelectedLocalView()
            cardSelectedLocal.visibility = View.VISIBLE
        } else {

            cardSelectedLocal.visibility = View.GONE
            card.setOnClickListener { changeState() }
        }
        cardExpandOptions.visibility = View.GONE

    }

    val info:Any?
        get() = null

    fun setInfo(trigger:Trigger){
        changeState()
    }

    private fun initSelectedLocalView(){
        val text = ContextCompat.getString(context, R.string.addReminderHint)
        cardSelectedLocal.text = text
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