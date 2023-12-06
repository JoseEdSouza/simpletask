package com.dopae.simpletask

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dopae.simpletask.builder.TagBuilder
import com.dopae.simpletask.component.ColorTagComponent
import com.dopae.simpletask.component.MenuEditTagComponent
import com.dopae.simpletask.dao.TagDAOFirebase
import com.dopae.simpletask.dao.TaskDAOFirebase
import com.dopae.simpletask.databinding.ActivityEdtTagBinding
import com.dopae.simpletask.exception.ColorNotReadyException
import com.dopae.simpletask.exception.NameNotReadyException
import com.dopae.simpletask.model.Tag
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

class EdtTagActivity : AppCompatActivity() {
    private val daoTag = TagDAOFirebase.getInstance()
    private val daoTask = TaskDAOFirebase.getInstance()
    private lateinit var tag: Tag
    private lateinit var name: EditText
    private lateinit var binding: ActivityEdtTagBinding
    private lateinit var menu: MenuEditTagComponent
    private lateinit var colorController: ColorTagComponent
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
        binding = ActivityEdtTagBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val id = intent.extras!!.getString("ID")!!
        window.statusBarColor = ContextCompat.getColor(this, R.color.tag_theme)
        name = binding.editTextTagEdtName
        menu = MenuEditTagComponent(binding.bottomMenuEdtTag)
        menu.init({ save() }, { close() }, { delete() })
        colorController = ColorTagComponent(this, binding.constraintLayoutColorSelection)
        lifecycleScope.launch(Dispatchers.IO + handler) {
            tag = daoTag.get(id)!!
            withContext(Dispatchers.Main) {
                init()
            }
        }
    }

    private fun init(){
        name.setText(tag.name)
        colorController.setInfo(tag.color)
        colorController.init()
    }

    private fun close() {
        with(Dispatchers.IO){
            if(isActive)
                cancel()
        }
        setResult(Activity.RESULT_CANCELED)
        finish()
    }


    private fun delete() {
        val alertDialog = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setTitle(R.string.deleteTag)
            .setMessage(R.string.deleteTagConfirmation)
            .setPositiveButton("OK") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO+handler){
                    val taskList = daoTask.getAll()
                    taskList.forEach{
                        it.removeTag(tag.id)
                        daoTask.update(it.id,it)
                    }
                    daoTag.remove(tag.id)
                    withContext(Dispatchers.Main){
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
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
            lifecycleScope.launch(Dispatchers.IO+handler){
                launch {
                    daoTag.update(tag.id, it)
                }.join()
                withContext(Dispatchers.Main){
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }

    }
}