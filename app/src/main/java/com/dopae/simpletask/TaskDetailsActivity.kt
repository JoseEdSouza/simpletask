package com.dopae.simpletask

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import com.dopae.simpletask.dao.TaskDAOImp
import com.dopae.simpletask.databinding.ActivityTaskDetailsBinding
import com.dopae.simpletask.model.Task

class TaskDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskDetailsBinding
    private var task: Task? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val id = intent.extras!!.getInt("ID")
        task = TaskDAOImp.getInstance().get(id)
        setTask()
    }

    private fun setTask() {
        task?.let {
            with(it) {
                binding.txtViewTaskDetailsName.text = name
            }
        }
    }

    fun taskOnClick(view: View) {
        val description = binding.txtViewTaskDetailsDescription
        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
        description.visibility = when (description.visibility) {
            View.GONE -> View.VISIBLE
            else -> View.GONE
        }

    }

    fun close(view: View) {
        setResult(Activity.RESULT_OK)
        finish()
    }
}