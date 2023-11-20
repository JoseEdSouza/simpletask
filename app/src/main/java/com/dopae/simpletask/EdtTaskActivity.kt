package com.dopae.simpletask

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.dopae.simpletask.builder.TaskBuilder
import com.dopae.simpletask.controller.CardLocalTaskController
import com.dopae.simpletask.controller.CardTagController
import com.dopae.simpletask.controller.CardTaskController
import com.dopae.simpletask.controller.CardTimeTaskController
import com.dopae.simpletask.controller.MenuAddEditController
import com.dopae.simpletask.dao.TaskDAOImp
import com.dopae.simpletask.databinding.ActivityTaskEdtBinding
import com.dopae.simpletask.exception.NameNotReadyException
import com.dopae.simpletask.exception.TriggerNotReadyException
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.model.TimeTrigger
import com.dopae.simpletask.model.Trigger
import com.dopae.simpletask.utils.TriggerType
import org.w3c.dom.Text

class EdtTaskActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTaskEdtBinding
    private lateinit var cards: CardTaskController
    private lateinit var cardTime: CardTimeTaskController
    private lateinit var cardLocal: CardLocalTaskController
    private lateinit var cardTag: CardTagController
    private lateinit var edtTxtName: EditText
    private lateinit var edtTxtDescription: EditText
    private lateinit var task:Task
    private val dao = TaskDAOImp.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskEdtBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.task_theme)
        val id = intent.extras!!.getInt("ID")
        task = dao.get(id)!!
        val menu = MenuAddEditController(binding.bottomMenuEdtask)
        menu.init({ save() }, { close() })
        edtTxtName = binding.editTextTaskEdtName
        edtTxtDescription = binding.edtTxtEdtTaskDescription
        cards = CardTaskController(this,binding.cardsLayoutTaskEdt,supportFragmentManager)
        cards.init()
        cardTag = cards.cardTag
        cardTime = cards.cardTime
        cardLocal = cards.cardLocal
        init()
    }

    fun init(){
        edtTxtName.setText(task.name)
        if(task.hasDescription)
            edtTxtDescription.setText(task.description)
        task.trigger?.let {
            when(it.type){
                TriggerType.TIME -> cardTime.setInfo(it)
                else -> cardLocal.setInfo(it)
            }
        }
        cardTag.setInfo(task.tags)
    }

    private fun save() {
        val name = edtTxtName.text.toString()
        val description = edtTxtDescription.text.toString()
        val tags = cardTag.info
        val trigger: Trigger? = if (cardTime.isActivated) TimeTrigger(cardTime.info) else null
        var newTask: Task? = null
        try {
            newTask = TaskBuilder()
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
        newTask?.let {
            dao.update(task.id,it)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun close() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

}