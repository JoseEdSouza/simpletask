package com.dopae.simpletask

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.dopae.simpletask.builder.TagBuilder
import com.dopae.simpletask.controller.ColorTagController
import com.dopae.simpletask.controller.MenuAddEditController
import com.dopae.simpletask.dao.TagDAOImp
import com.dopae.simpletask.databinding.ActivityAddTagBinding
import com.dopae.simpletask.databinding.ColorTagBinding
import com.dopae.simpletask.exception.ColorNotReadyException
import com.dopae.simpletask.exception.NameNotReadyException
import com.dopae.simpletask.model.Tag


class AddTagActivity : AppCompatActivity() {
    private val dao = TagDAOImp.getInstance()
    private lateinit var binding: ActivityAddTagBinding
    private lateinit var menu: MenuAddEditController
    private lateinit var name: EditText
    private lateinit var colorsOptions: ColorTagBinding
    private lateinit var colorController: ColorTagController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTagBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.tag_theme)
        name = binding.editTextTagAddName
        menu = MenuAddEditController(binding.bottomMenuAddTag)
        menu.init({ save() }, { close() })
        colorsOptions = binding.constraintLayoutColorSelection
        colorController = ColorTagController(this, colorsOptions)
        colorController.init()
    }

    private fun save() {
        val name = name.text.toString()
        val color = colorController.info
        var tag: Tag? = null
        try {
            tag = TagBuilder()
                .setName(name)
                .setColor(color)
                .allReadyToGo()
                .build()
        } catch (e: NameNotReadyException){
            Toast.makeText(this, "Adicione um nome", Toast.LENGTH_SHORT).show()
        } catch (e:ColorNotReadyException){
            Toast.makeText(this, "Selecione uma cor", Toast.LENGTH_SHORT).show()
        }
        tag?.let {
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