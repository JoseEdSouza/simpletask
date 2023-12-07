package com.dopae.simpletask

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dopae.simpletask.builder.TagBuilder
import com.dopae.simpletask.component.ColorTagComponent
import com.dopae.simpletask.component.MenuEditTagComponent
import com.dopae.simpletask.dao.TagDAOFirebase
import com.dopae.simpletask.dao.TaskDAOFirebase
import com.dopae.simpletask.databinding.ActivityEdtTagBinding
import com.dopae.simpletask.databinding.ColorTagBinding
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
    private lateinit var binding: ActivityEdtTagBinding
    private lateinit var tag: Tag
    private lateinit var edtTxtName: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var colorLayout: ColorTagBinding
    private lateinit var menu: MenuEditTagComponent
    private lateinit var colorComponent: ColorTagComponent
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.tag_theme)
        val id = intent.extras!!.getString("ID")!!
        colorLayout = binding.constraintLayoutColorSelection
        progressBar = binding.progressBarEdtTag
        edtTxtName = binding.editTextTagEdtName
        menu = MenuEditTagComponent(binding.bottomMenuEdtTag)
        menu.init({ save() }, { close() }, { delete() })
        colorComponent = ColorTagComponent(this, colorLayout)
        flipLoading()
        lifecycleScope.launch(Dispatchers.IO + handler) {
            tag = daoTag.get(id)!!
            withContext(Dispatchers.Main) {
                init()
            }
        }
    }

    private fun init(){
        flipLoading()
        edtTxtName.setText(tag.name)
        colorComponent.setInfo(tag.color)
        colorComponent.init()
    }

    private fun flipLoading(){
        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
        when(progressBar.visibility){
            View.GONE -> {
                progressBar.visibility = View.VISIBLE
                edtTxtName.visibility = View.GONE
                colorLayout.linearLayoutTagColors.visibility = View.GONE
            }
            else -> {
                progressBar.visibility = View.GONE
                edtTxtName.visibility = View.VISIBLE
                colorLayout.linearLayoutTagColors.visibility = View.VISIBLE
            }
        }
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
        val name = edtTxtName.text.toString()
        val color = colorComponent.info
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