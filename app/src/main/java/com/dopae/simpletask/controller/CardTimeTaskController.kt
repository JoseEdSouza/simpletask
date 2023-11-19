package com.dopae.simpletask.controller

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import androidx.fragment.app.FragmentManager
import com.dopae.simpletask.R
import com.dopae.simpletask.databinding.CardTaskTimeReminderBinding
import com.dopae.simpletask.model.Task
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CardTimeTaskController(
    binding: CardTaskTimeReminderBinding,
    private val supportFragmentManager: FragmentManager
) {
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

    fun init() {
        initTimeBtnTxt()
        cardSelectedTime.visibility = View.GONE
        cardExpandOptions.visibility = View.GONE
        card.setOnClickListener { changeState() }
        dateButton.setOnClickListener { openDateSelection() }
        timeButton.setOnClickListener { openTimeSelection() }

    }

    private fun initTimeBtnTxt() {
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

    val isActivated: Boolean
        get() = activated

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

    private fun changeState() {
        activated = !activated
        TransitionManager.beginDelayedTransition(card, AutoTransition())
        with(cardExpandOptions) {
            visibility = if (visibility == View.GONE) View.VISIBLE else View.GONE
        }
        // todo - change State if readOnly
    }

    private fun setDate(selection: Long) {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("America/Brasilia"))
        utc.timeInMillis = selection
        val local = Calendar.getInstance()
        local.timeInMillis = utc.timeInMillis
        local[Calendar.DAY_OF_MONTH] += 1
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
        datePicker.addOnPositiveButtonClickListener { setDate(it) }
        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun openTimeSelection() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
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

