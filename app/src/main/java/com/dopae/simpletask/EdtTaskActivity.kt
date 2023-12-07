package com.dopae.simpletask

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dopae.simpletask.builder.TaskBuilder
import com.dopae.simpletask.component.CardLocalTaskComponent
import com.dopae.simpletask.component.CardTagComponent
import com.dopae.simpletask.component.CardTaskComponent
import com.dopae.simpletask.component.CardTimeTaskComponent
import com.dopae.simpletask.component.MenuAddEditComponent
import com.dopae.simpletask.dao.TaskDAOFirebase
import com.dopae.simpletask.databinding.ActivityTaskEdtBinding
import com.dopae.simpletask.databinding.CardsLayoutTaskBinding
import com.dopae.simpletask.exception.NameNotReadyException
import com.dopae.simpletask.exception.TriggerNotReadyException
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.model.TimeTrigger
import com.dopae.simpletask.model.Trigger
import com.dopae.simpletask.utils.TriggerType
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EdtTaskActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTaskEdtBinding
    private lateinit var cards: CardTaskComponent
    private lateinit var cardsLayout: CardsLayoutTaskBinding
    private lateinit var cardTime: CardTimeTaskComponent
    private lateinit var cardLocal: CardLocalTaskComponent
    private lateinit var cardTag: CardTagComponent
    private lateinit var edtTxtName: EditText
    private lateinit var edtTxtDescription: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var task: Task
    private val dao = TaskDAOFirebase.getInstance()
    private val handler = CoroutineExceptionHandler { context, err ->
        context.cancel()
        val errorMsg = when (err) {
            is FirebaseNetworkException -> R.string.noConnection
            is FirebaseFirestoreException -> R.string.saveContentError
            else -> R.string.unexpectedError
        }
        lifecycleScope.launch(Dispatchers.Main) {
            val snackbar = Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_SHORT)
            snackbar.setBackgroundTint(Color.RED)
            snackbar.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskEdtBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.task_theme)
        val id = intent.extras!!.getString("ID")!!
        progressBar = binding.progressBarEdtTask
        edtTxtName = binding.editTextTaskEdtName
        edtTxtDescription = binding.edtTxtEdtTaskDescription
        cardsLayout = binding.cardsLayoutTaskEdt
        cards = CardTaskComponent(this,cardsLayout,supportFragmentManager)
        cards.init()
        cardTag = cards.cardTag
        cardTime = cards.cardTime
        cardLocal = cards.cardLocal
        val menu = MenuAddEditComponent(binding.bottomMenuEdtask)
        menu.init({ save() }, { close() })
        flipLoading()
        lifecycleScope.launch(Dispatchers.IO+handler) {
            task = dao.get(id)!!
            withContext(Dispatchers.Main){
                init()
            }
        }
    }

    fun init(){
        flipLoading()
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

    private fun flipLoading(){
        TransitionManager.beginDelayedTransition(binding.root,AutoTransition())
        when(progressBar.visibility){
            View.GONE -> {
                progressBar.visibility = View.VISIBLE
                edtTxtName.visibility = View.GONE
                edtTxtDescription.visibility = View.GONE
                cardsLayout.linearLayoutCardsAddTask.visibility = View.GONE
            }
            else -> {
                progressBar.visibility = View.GONE
                edtTxtName.visibility = View.VISIBLE
                edtTxtDescription.visibility = View.VISIBLE
                cardsLayout.linearLayoutCardsAddTask.visibility = View.VISIBLE
            }
        }
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
            lifecycleScope.launch(Dispatchers.IO+handler) {
                dao.update(task.id,it)
                withContext(Dispatchers.Main){
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }

    private fun close() {
        with(Dispatchers.IO) {
            if (isActive)
                cancel()
        }
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

}