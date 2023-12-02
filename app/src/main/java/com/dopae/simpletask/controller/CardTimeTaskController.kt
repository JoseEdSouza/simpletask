package com.dopae.simpletask.controller

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.dopae.simpletask.R
import com.dopae.simpletask.databinding.CardTaskTimeReminderBinding
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.model.TimeTrigger
import com.dopae.simpletask.model.Trigger
import com.dopae.simpletask.utils.TriggerType
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CardTimeTaskController(
    private val context: Context,
    binding: CardTaskTimeReminderBinding,
    private val supportFragmentManager: FragmentManager
) : CardController {
    private val cardExpandOptions = binding.constraintLayoutCardTaskExpandOptions
    private val cardSelectedTime = binding.txtViewSelectedTime
    private val card = binding.root
    private val dateButton = binding.btnCalendarCardTaskTime
    private val timeButton = binding.btnTimeCardTaskTime
    private var readOnly = false
    private var activated = false
    private var selectedDate: Date? = null
    private var selectedTime: Pair<Int, Int>? = null
    private var task: Task? = null

    override fun init() {
        if (readOnly) {
            cardExpandOptions.visibility = View.GONE
            initSelectedTimeView()
        } else {
            initTimeBtnView()
            cardSelectedTime.visibility = View.GONE
            cardExpandOptions.visibility = View.GONE
            card.setOnClickListener { changeState() }
            dateButton.setOnClickListener { openDateSelection() }
            timeButton.setOnClickListener { openTimeSelection() }
        }

    }

    override fun setOnClickListener(onClickListener: View.OnClickListener) {
        card.setOnClickListener(onClickListener)
    }

    private fun initSelectedTimeView() {
        val date = (task?.trigger?.data) as Date?
        val text = date?.let {
            val calendar = Calendar.getInstance()
            calendar.time = it
            val ret =
                SimpleDateFormat("EEE dd/MM, HH:mm", Locale("pt", "BR"))
                    .format(Date(calendar.timeInMillis))
            ret
        } ?: ContextCompat.getString(context, R.string.addReminderHint)
        cardSelectedTime.text = text
    }

    private fun initTimeBtnView() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        dateButton.text =
            SimpleDateFormat("EEE dd/MM", Locale("pt", "BR"))
                .format(Date(calendar.timeInMillis))
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        String.format("%02d:%02d", hour, minute).also { timeButton.text = it }
    }

    fun setReadOnly(task: Task): CardTimeTaskController {
        this.task = task
        readOnly = true
        return this
    }

    fun setWriteRead(): CardTimeTaskController {
        readOnly = false
        task = null
        return this
    }

    override val isActivated: Boolean
        get() = activated


    fun setInfo(trigger: Trigger) {
        if (trigger.type == TriggerType.TIME) {
            val date = trigger.data as Date
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date.time
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            setTime(hour, minute)
            setDate(date)
            changeState()
        }

    }

    val info: Date
        get(): Date {
            val date = selectedDate ?: Date(System.currentTimeMillis())
            val time = selectedTime ?: Pair(9, 0)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar[Calendar.HOUR_OF_DAY] = time.first
            calendar[Calendar.MINUTE] = time.second
            return calendar.time
        }

    override fun changeState() {
        activated = !activated
        with(cardExpandOptions) {
            visibility = if (visibility == View.GONE) View.VISIBLE else View.GONE
        }
    }

    private fun setDate(date: Date) {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("America/Brasilia"))
        utc.time = date
        val local = Calendar.getInstance()
        local.timeInMillis = utc.timeInMillis
        //local[Calendar.DAY_OF_MONTH] += 1
        val dateString = SimpleDateFormat("EEE dd/MM", Locale("pt", "BR")).format(local.time)
        dateButton.text = dateString
        selectedDate = local.time
    }


    private fun setTime(hour: Int, minute: Int) {
        String.format("%02d:%02d", hour, minute).also { timeButton.text = it }
        selectedTime = Pair(hour, minute)
    }

    private fun openDateSelection() {
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTitleText("Selecione a data do lembrete")
            .setPositiveButtonText("Adicionar")
            .setNegativeButtonText("Cancelar")
            .setCalendarConstraints(constraints)
            .setTheme(R.style.MaterialCalendarTheme)
            .build()
        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = Date(it)
            calendar[Calendar.DAY_OF_MONTH]+=1
            setDate(Date(calendar.timeInMillis))
        }
        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun openTimeSelection() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        selectedTime?.let {
            calendar[Calendar.HOUR_OF_DAY] = it.first
            calendar[Calendar.MINUTE] = it.second
        }
        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(calendar[Calendar.HOUR_OF_DAY])
                .setMinute(calendar[Calendar.MINUTE])
                .setTitleText("Selecione a hora do lembrete")
                .setTheme(R.style.MaterialTimePickerTheme)
                .setInputMode(INPUT_MODE_CLOCK)
                .build()
        timePicker.addOnPositiveButtonClickListener { setTime(timePicker.hour, timePicker.minute) }
        timePicker.show(supportFragmentManager, "TIME_PICKER")
    }


}

