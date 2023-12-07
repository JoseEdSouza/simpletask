package com.dopae.simpletask.component

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.dopae.simpletask.AddLocalActivity
import com.dopae.simpletask.R
import com.dopae.simpletask.databinding.CardTaskLocalReminderBinding
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.model.Trigger
import com.dopae.simpletask.utils.LocalTrigger

class CardLocalTaskComponent(
    private val context: Context,
    binding: CardTaskLocalReminderBinding,
    private val supportFragmentManager: FragmentManager
) : CardComponent {
    private val cardExpandOptions = binding.constraintLayoutCardTaskLocalExpandOptions
    private val cardSelectedLocal = binding.txtViewSelectedLocal
    private val card = binding.root
    private val localTriggerBtn = binding.btnLocalOptionCardTaskLocal
    private val localBtn = binding.btnTimeCardTaskLocal
    private var readOnly = false
    private var activated = false
    private var selectedLocalTrigger: LocalTrigger? = null
    private var selectedPlace = null
    private var task: Task? = null
    private var launcher: ActivityResultLauncher<Intent>? = null


    override fun init() {
        if (readOnly) {
            cardSelectedLocal.text = ContextCompat.getString(context, R.string.addReminderHint)
            cardSelectedLocal.visibility = View.VISIBLE
        } else {
            localBtn.setOnClickListener { startLocalActivity() }
            cardSelectedLocal.visibility = View.GONE
            card.setOnClickListener { flipState() }
        }
        cardExpandOptions.visibility = View.GONE

    }

    val info: Any?
        get() = null

    fun setInfo(trigger: Trigger) {
        flipState()
    }

    override fun setOnClickListener(onClickListener: OnClickListener) {
        card.setOnClickListener(onClickListener)
    }

    fun setLauncher(launcher: ActivityResultLauncher<Intent>){
        this.launcher = launcher
    }

    fun setReadOnly(task: Task): CardLocalTaskComponent {
        this.task = task
        readOnly = true
        return this
    }

    fun setWriteRead(): CardLocalTaskComponent {
        readOnly = false
        task = null
        return this
    }

    override val isActivated: Boolean
        get() = activated

    override fun flipState() {
        activated = !activated
        with(cardExpandOptions) {
            visibility = if (visibility == View.GONE) View.VISIBLE else View.GONE
        }
    }

    override fun setActivated() {
        activated = true
        cardExpandOptions.visibility = View.VISIBLE
    }

    override fun setDeactivated() {
        activated = false
        cardExpandOptions.visibility = View.GONE
    }

    private fun startLocalActivity() {
        launcher?.let {
            val intent = Intent(context, AddLocalActivity::class.java)
            it.launch(intent)
        }
    }
}