package com.dopae.simpletask

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dopae.simpletask.controller.BottomMenuController
import com.dopae.simpletask.controller.CardTimeTaskController
import com.dopae.simpletask.dao.TaskDAOImp
import com.dopae.simpletask.databinding.ActivityTaskAddBinding
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.model.Trigger

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskAddBinding
    private lateinit var card: CardTimeTaskController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        BottomMenuController(binding.bottomMenuAddTask)
            .init({ save() }, { close() })
        card = CardTimeTaskController(binding.cardTimeAddTask, supportFragmentManager)
        card.init()
        window.statusBarColor = ContextCompat.getColor(this,R.color.task_theme)
    }

    private fun save() {
        var valid = true
        val trigger: Trigger? = null
        if (card.isActivated) {
            // todo - trigger
        }
        var name = ""
        binding.editTextTaskAddName.text.toString().also {
            if (it == "") valid = false
            name = it
        }
        val description = binding.edtTxtAddTaskDescription.text.toString()
        if (valid) {
            TaskDAOImp.getInstance().add(
                Task(
                    name = name,
                    description = description,
                    trigger = trigger
                )
            )
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Selecione uma data válida", Toast.LENGTH_SHORT).show()
        }

    }

    private fun close() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}