package com.dopae.simpletask

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dopae.simpletask.builder.TagBuilder
import com.dopae.simpletask.component.ColorTagComponent
import com.dopae.simpletask.component.MenuAddEditComponent
import com.dopae.simpletask.dao.TagDAOFirebase
import com.dopae.simpletask.databinding.ActivityAddTagBinding
import com.dopae.simpletask.databinding.ColorTagBinding
import com.dopae.simpletask.exception.ColorNotReadyException
import com.dopae.simpletask.exception.NameNotReadyException
import com.dopae.simpletask.model.Tag
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddTagActivity : AppCompatActivity() {
    private val dao = TagDAOFirebase.getInstance()
    private lateinit var binding: ActivityAddTagBinding
    private lateinit var menu: MenuAddEditComponent
    private lateinit var progressBar: ProgressBar
    private lateinit var saveBtn: ImageButton
    private lateinit var name: EditText
    private lateinit var colorsOptions: ColorTagBinding
    private lateinit var colorController: ColorTagComponent
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
        binding = ActivityAddTagBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.tag_theme)
        progressBar = binding.bottomMenuAddTag.progressBar
        saveBtn = binding.bottomMenuAddTag.imgBtnSave
        name = binding.editTextTagAddName
        menu = MenuAddEditComponent(binding.bottomMenuAddTag)
        menu.init({ save() }, { close() })
        colorsOptions = binding.constraintLayoutColorSelection
        colorController = ColorTagComponent(this, colorsOptions)
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
        } catch (e: NameNotReadyException) {
            Toast.makeText(this, R.string.noNameProvidedError, Toast.LENGTH_SHORT).show()
        } catch (e: ColorNotReadyException) {
            Toast.makeText(this, R.string.noColorProvidedError, Toast.LENGTH_SHORT).show()
        }
        tag?.let {
            progressBar.visibility = View.VISIBLE
            saveBtn.visibility = View.INVISIBLE
            lifecycleScope.launch(Dispatchers.IO + handler) {
                launch {
                    dao.add(it)
                }
                withContext(Dispatchers.Main) {
                    setResult(RESULT_OK)
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