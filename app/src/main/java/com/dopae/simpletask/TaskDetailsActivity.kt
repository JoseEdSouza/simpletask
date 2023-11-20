package com.dopae.simpletask

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.dopae.simpletask.controller.CardTaskController
import com.dopae.simpletask.controller.MenuDetailsController
import com.dopae.simpletask.dao.TaskDAOImp
import com.dopae.simpletask.databinding.ActivityTaskDetailsBinding
import com.dopae.simpletask.databinding.MenuDetailsBinding
import com.dopae.simpletask.model.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TaskDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskDetailsBinding
    private lateinit var task: Task
    private lateinit var cards: CardTaskController
    private lateinit var menu: MenuDetailsBinding
    private lateinit var concludedBtn: ImageButton
    private lateinit var nameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private val dao = TaskDAOImp.getInstance()
    private val edtTaskLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                task = dao.get(task.id)!!
                init()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.extras!!.getInt("ID")
        task = dao.get(id)!!
        menu = binding.bottomMenuTaskDetails
        concludedBtn = menu.imgBtnConcluded
        nameTextView = binding.txtViewTaskDetailsName
        descriptionTextView = binding.txtViewTaskDetailsDescription
        val menuController = MenuDetailsController(menu)
        menuController.init({ flipConcluded() }, { startEditActivity() }, { deleteTask() })
        menu.imgBtnConcluded.setOnLongClickListener { onLongClickConcluded() }
        cards =
            CardTaskController(this, binding.cardsLayoutTaskDetails, supportFragmentManager)
                .setReadOnly(task)
        init()
        window.statusBarColor = ContextCompat.getColor(this, R.color.task_theme)
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
                dao.remove(task.id)
                setResult(Activity.RESULT_OK)
                finish()
            }
            .setNegativeButton(R.string.cancel, null)
        alertDialog.show()
    }

    private fun onLongClickConcluded(): Boolean {
        flipConcluded()
        dao.update(task.id, task)
        setResult(Activity.RESULT_OK)
        finish()
        return true
    }

    private fun flipConcluded() {
        task.flipStatus()
        val color = if (task.concluded) R.color.green_2 else R.color.bottom_menu_icon
        concludedBtn.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, color))
    }

    private fun startEditActivity() {
        val intent = Intent(this, EdtTaskActivity::class.java)
        intent.putExtra("ID", task.id)
        edtTaskLauncher.launch(intent)
    }

    fun close(view: View) {
        dao.update(task.id, task)
        setResult(Activity.RESULT_OK)
        finish()
    }
}