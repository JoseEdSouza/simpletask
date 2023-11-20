package com.dopae.simpletask

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.dopae.simpletask.controller.MenuAddEditController
import com.dopae.simpletask.databinding.ActivityAddTagBinding


class AddTagActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTagBinding
    private lateinit var menu: MenuAddEditController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTagBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.tag_theme)
        menu = MenuAddEditController(binding.bottomMenuAddTag)
        menu.init({ save() }, { close() })
    }

    private fun save() {

    }

    private fun close() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}