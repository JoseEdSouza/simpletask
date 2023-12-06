package com.dopae.simpletask

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dopae.simpletask.component.CardTaskComponent
import com.dopae.simpletask.component.MenuDetailsComponent
import com.dopae.simpletask.dao.TaskDAOFirebase
import com.dopae.simpletask.databinding.ActivityTaskDetailsBinding
import com.dopae.simpletask.databinding.MenuDetailsBinding
import com.dopae.simpletask.model.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskDetailsBinding
    private lateinit var task: Task
    private lateinit var cards: CardTaskComponent
    private lateinit var menu: MenuDetailsBinding
    private lateinit var concludedBtn: ImageButton
    private lateinit var nameTextView: TextView
    private lateinit var descriptionTextView: TextView
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
    private val edtTaskLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                lifecycleScope.launch(Dispatchers.IO) {
                    task = dao.get(task.id)!!
                    withContext(Dispatchers.Main) {
                        init()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.task_theme)
        val id = intent.extras!!.getString("ID")!!
        menu = binding.bottomMenuTaskDetails
        concludedBtn = menu.imgBtnConcluded
        nameTextView = binding.txtViewTaskDetailsName
        descriptionTextView = binding.txtViewTaskDetailsDescription
        val menuController = MenuDetailsComponent(menu)
        cards =
            CardTaskComponent(this, binding.cardsLayoutTaskDetails, supportFragmentManager)
        cards.setOnClickListener { startEditActivity() }
        menuController.init({ flipConcluded() }, { startEditActivity() }, { deleteTask() })
        menu.imgBtnConcluded.setOnLongClickListener { onLongClickConcluded() }
        lifecycleScope.launch(Dispatchers.IO + handler) {
            task = dao.get(id)!!
            withContext(Dispatchers.Main) {
                init()
            }
        }
    }


    fun init() {
        cards.setReadOnly(task)
        if (task.concluded) {
            concludedBtn.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.green_2)
            )
        }
        nameTextView.text = task.name
        if (task.hasDescription) {
            descriptionTextView.text = task.description
        } else {
            descriptionTextView.text = ContextCompat.getString(
                this,
                R.string.descriptionHint
            )
            descriptionTextView.setTextColor(ContextCompat.getColor(this, R.color.gray_3))
        }
        cards.init()
    }

    private fun deleteTask() {
        val alertDialog = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setTitle(R.string.deleteTask)
            .setMessage(R.string.deleteTaskConfirmation)
            .setPositiveButton("OK") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO + handler) {
                    dao.remove(task.id)
                    withContext(Dispatchers.Main) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
        alertDialog.show()
    }

    private fun onLongClickConcluded(): Boolean {
        lifecycleScope.launch(Dispatchers.IO + handler) {
            launch {
                dao.update(task.id, task)
            }.join()
            withContext(Dispatchers.Main) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
        return true
    }

    private fun flipConcluded() {
        task.flipStatus()
        val color = if (task.concluded) R.color.green_2 else R.color.bottom_menu_icon
        concludedBtn.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, color))
        Toast.makeText(this, "Segure para salvar", Toast.LENGTH_SHORT).show()
    }

    private fun startEditActivity() {
        val intent = Intent(this, EdtTaskActivity::class.java)
        intent.putExtra("ID", task.id)
        edtTaskLauncher.launch(intent)
    }

    fun close(view: View) {
        with(Dispatchers.IO) {
            if (isActive)
                cancel()
        }
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}