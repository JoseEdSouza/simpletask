package com.dopae.simpletask

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class AddTaskActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_add)
        supportActionBar?.hide()

    }

    fun close(view: View) {
        setResult(Activity.RESULT_OK)
        finish()
    }
}