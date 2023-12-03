package com.dopae.simpletask

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.dopae.simpletask.databinding.ActivityResetPasswordBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var edtTxtEmail: EditText
    private lateinit var resetBtn: Button
    private lateinit var backBtn: Button
    private lateinit var progressBar: ProgressBar
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        edtTxtEmail = binding.editTextResetEmail
        resetBtn = binding.resetBtn
        backBtn = binding.backBtn
        progressBar = binding.progressBarReset
        init()
    }

    private fun init() {
        progressBar.visibility = View.GONE
        backBtn.setOnClickListener { close() }
        resetBtn.setOnClickListener { resetPassword() }
    }

    private fun resetPassword() {
        val email = edtTxtEmail.text.toString()
        if (email.isEmpty()) {
            Toast.makeText(
                this,
                R.string.fillAllFields,
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        progressBar.visibility = View.VISIBLE
        val sendResetRequestThread = Thread() {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(
                                this,
                                R.string.sendEmailSuccess,
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }

                    } else {
                        runOnUiThread {
                            val snackbar = Snackbar.make(
                                binding.root,
                                R.string.sendEmailError,
                                Snackbar.LENGTH_SHORT
                            )
                            snackbar.setBackgroundTint(Color.RED)
                            snackbar.show()
                            progressBar.visibility = View.GONE
                        }
                    }
                }
                .addOnFailureListener {
                    val errorMsg = when (it) {
                        is FirebaseAuthInvalidUserException -> R.string.userNotFound
                        is FirebaseAuthInvalidCredentialsException -> R.string.invalidEmail
                        is FirebaseNetworkException -> R.string.noConnection
                        is FirebaseAuthException -> R.string.sendEmailError
                        else -> R.string.unexpectedError
                    }
                    runOnUiThread {
                        val snackbar = Snackbar.make(
                            binding.root,
                            errorMsg,
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.setBackgroundTint(Color.RED)
                        snackbar.show()
                        progressBar.visibility = View.GONE
                    }
                }
        }
        sendResetRequestThread.start()
    }

    private fun close() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}