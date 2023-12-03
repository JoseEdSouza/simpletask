package com.dopae.simpletask

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.dopae.simpletask.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var edtTxtEmail: EditText
    private lateinit var edtTxtPassword: EditText
    private lateinit var backBtn: Button
    private lateinit var loginBtn: Button
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        edtTxtEmail = binding.editTextLoginEmail
        edtTxtPassword = binding.editTextTextPassword
        backBtn = binding.backBtn
        loginBtn = binding.loginBtn
        progressBar = binding.progressBarLogin
        init()
    }

    private fun init() {
        progressBar.visibility = View.GONE
        backBtn.setOnClickListener { close() }
        loginBtn.setOnClickListener { login() }

    }

    private fun login() {
        val email = edtTxtEmail.text.toString()
        val password = edtTxtPassword.text.toString()
        val auth = FirebaseAuth.getInstance()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.fillAllFields, Toast.LENGTH_SHORT).show()
            return
        }
        progressBar.visibility = View.VISIBLE
        val loginThread = Thread {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        startMainActivity()
                    else {
                        runOnUiThread {
                            val snackbar =
                                Snackbar.make(
                                    binding.root,
                                    R.string.failedLogin,
                                    Snackbar.LENGTH_SHORT
                                )
                            snackbar.setBackgroundTint(Color.RED)
                            snackbar.show()
                        }
                    }

                }
                .addOnFailureListener {
                    runOnUiThread {
                        val errorMsg = when (it) {
                            is FirebaseAuthInvalidUserException -> R.string.userNotFound
                            is FirebaseAuthInvalidCredentialsException -> R.string.failedLogin
                            is FirebaseNetworkException -> R.string.noConnection
                            else -> R.string.loginError
                        }
                        val snackbar =
                            Snackbar.make(
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
        loginThread.start()
    }

    private fun startMainActivity() {
        setResult(Activity.RESULT_OK)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun close() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}