package com.dopae.simpletask

import android.app.Activity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dopae.simpletask.component.MenuAddEditComponent
import com.dopae.simpletask.component.CardLocalTaskComponent
import com.dopae.simpletask.component.CardTaskComponent
import com.dopae.simpletask.component.CardTimeTaskComponent
import com.dopae.simpletask.builder.TaskBuilder
import com.dopae.simpletask.component.CardTagComponent
import com.dopae.simpletask.dao.TaskDAOImp
import com.dopae.simpletask.databinding.ActivityTaskAddBinding
import com.dopae.simpletask.exception.NameNotReadyException
import com.dopae.simpletask.exception.TriggerNotReadyException
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.model.TimeTrigger
import com.dopae.simpletask.model.Trigger
import com.dopae.simpletask.utils.TriggerType

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskAddBinding
    private lateinit var cardTime: CardTimeTaskComponent
    private lateinit var cardLocal: CardLocalTaskComponent
    private lateinit var cardTag: CardTagComponent
    private lateinit var edtTxtName: EditText
    private lateinit var edtTxtDescription: EditText
    private val dao = TaskDAOImp.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskAddBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.task_theme)
        val menu = MenuAddEditComponent(binding.bottomMenuAddTask)
        menu.init({ save() }, { close() })
        edtTxtName = binding.editTextTaskAddName
        edtTxtDescription = binding.edtTxtAddTaskDescription
        val cards = CardTaskComponent(this, binding.cardsLayoutTaskAdd, supportFragmentManager)
        cards.init()
        cardTag = cards.cardTag
        cardTime = cards.cardTime
        cardLocal = cards.cardLocal
    }

    private fun save() {
        val name = edtTxtName.text.toString()
        val description = edtTxtDescription.text.toString()
        val tags = cardTag.info
        val trigger: Trigger? = if (cardTime.isActivated) TimeTrigger(cardTime.info) else null
        var task: Task? = null
        try {
            task = TaskBuilder()
                .setName(name)
                .setDescription(description)
                .setTrigger(trigger)
                .addTags(tags)
                .allReadyToGo()
                .build()
        } catch (e: NameNotReadyException) {
            Toast.makeText(this, "Adicione um nome", Toast.LENGTH_SHORT).show()
        } catch (e: TriggerNotReadyException) {
            val toast = when (trigger!!.type) {
                TriggerType.TIME -> Toast.makeText(this, "Tempo inválido", Toast.LENGTH_SHORT)
                else -> Toast.makeText(this, "Local inválido", Toast.LENGTH_SHORT)
            }
            toast.show()
        }
        task?.let {
            dao.add(it)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun close() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}