package com.dopae.simpletask

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.dopae.simpletask.builder.TagBuilder
import com.dopae.simpletask.controller.ColorTagController
import com.dopae.simpletask.controller.MenuEditTagController
import com.dopae.simpletask.dao.TagDAOImp
import com.dopae.simpletask.databinding.ActivityEdtTagBinding
import com.dopae.simpletask.exception.ColorNotReadyException
import com.dopae.simpletask.exception.NameNotReadyException
import com.dopae.simpletask.model.Tag
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EdtTagActivity : AppCompatActivity() {
    private val dao = TagDAOImp.getInstance()
    private lateinit var tag: Tag
    private lateinit var name: EditText
    private lateinit var binding: ActivityEdtTagBinding
    private lateinit var menu: MenuEditTagController
    private lateinit var colorController: ColorTagController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEdtTagBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.extras!!.getInt("ID")
        tag = dao.get(id)!!
        window.statusBarColor = ContextCompat.getColor(this, R.color.tag_theme)
        name = binding.editTextTagEdtName
        menu = MenuEditTagController(binding.bottomMenuEdtTag)
        menu.init({ save() }, { close() }, { delete() })
        colorController = ColorTagController(this, binding.constraintLayoutColorSelection)
        init()
    }

    private fun init(){
        name.setText(tag.name)
        colorController.setInfo(tag.color)
        colorController.init()
    }

    private fun close() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }


    private fun delete() {
        val alertDialog = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setTitle(R.string.deleteTag)
            .setMessage(R.string.deleteTagConfirmation)
            .setPositiveButton("OK") { _, _ ->
                dao.remove(tag.id)
                setResult(Activity.RESULT_OK)
                finish()
            }
            .setNegativeButton(R.string.cancel, null)
        alertDialog.show()

    }

    private fun save() {
        val name = name.text.toString()
        val color = colorController.info
        var newTag: Tag? = null
        try {
            newTag = TagBuilder()
                .setName(name)
                .setColor(color)
                .allReadyToGo()
                .build()
        } catch (e: NameNotReadyException) {
            Toast.makeText(this, "Adicione um nome", Toast.LENGTH_SHORT).show()
        } catch (e: ColorNotReadyException) {
            Toast.makeText(this, "Selecione uma cor", Toast.LENGTH_SHORT).show()
        }
        newTag?.let {
            dao.update(tag.id,it)
            setResult(Activity.RESULT_OK)
            finish()
        }

    }
}