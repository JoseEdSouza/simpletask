package com.dopae.simpletask

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dopae.simpletask.component.MenuAddEditComponent
import com.dopae.simpletask.component.CardLocalTaskComponent
import com.dopae.simpletask.component.CardTaskComponent
import com.dopae.simpletask.component.CardTimeTaskComponent
import com.dopae.simpletask.builder.TaskBuilder
import com.dopae.simpletask.component.CardTagComponent
import com.dopae.simpletask.dao.TaskDAOFirebase
import com.dopae.simpletask.databinding.ActivityTaskAddBinding
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

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskAddBinding
    private lateinit var cardTime: CardTimeTaskComponent
    private lateinit var cardLocal: CardLocalTaskComponent
    private lateinit var cardTag: CardTagComponent
    private lateinit var edtTxtName: EditText
    private lateinit var edtTxtDescription: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var saveBtn: ImageButton
    private val dao = TaskDAOFirebase.getInstance()
    private val handler = CoroutineExceptionHandler { context, err ->
        context.cancel()
        val errorMsg = when (err) {
            is FirebaseNetworkException -> R.string.noConnection
            is FirebaseFirestoreException -> R.string.saveContentError
            else -> R.string.unexpectedError
        }
        lifecycleScope.launch(Dispatchers.Main) {
            progressBar.visibility = View.GONE
            saveBtn.visibility = View.VISIBLE
            val snackbar = Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_SHORT)
            snackbar.setBackgroundTint(Color.RED)
            snackbar.show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskAddBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.task_theme)
        edtTxtName = binding.editTextTaskAddName
        edtTxtDescription = binding.edtTxtAddTaskDescription
        progressBar = binding.bottomMenuAddTask.progressBar
        saveBtn = binding.bottomMenuAddTask.imgBtnSave
        val menu = MenuAddEditComponent(binding.bottomMenuAddTask)
        menu.init({ save() }, { close() })
        val cards =
            CardTaskComponent(this, binding.cardsLayoutTaskAdd, supportFragmentManager)
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
            Toast.makeText(this, R.string.noNameProvidedError, Toast.LENGTH_SHORT).show()
        } catch (e: TriggerNotReadyException) {
            val toast = when (trigger!!.type) {
                TriggerType.TIME -> Toast.makeText(
                    this,
                    R.string.invalidTimeProvidedError,
                    Toast.LENGTH_SHORT
                )

                else -> Toast.makeText(
                    this,
                    R.string.invalidLocalProvidedError,
                    Toast.LENGTH_SHORT
                )
            }
            toast.show()
        }
        task?.let {
            progressBar.visibility = View.VISIBLE
            saveBtn.visibility = View.INVISIBLE

            lifecycleScope.launch(Dispatchers.IO + handler) {
                launch {
                    dao.add(it)
                }
                withContext(Dispatchers.Main) {
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