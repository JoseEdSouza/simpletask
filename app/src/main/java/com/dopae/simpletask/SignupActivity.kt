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
import com.dopae.simpletask.builder.UserBuilder
import com.dopae.simpletask.databinding.ActivitySignupBinding
import com.dopae.simpletask.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var backBtn: Button
    private lateinit var signupBtn: Button
    private lateinit var edtTxtEmail: EditText
    private lateinit var edtTxtPassword: EditText
    private lateinit var edtTxtConfirmPassword: EditText
    private lateinit var progressBar: ProgressBar
    private val auth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        backBtn = binding.backBtn
        signupBtn = binding.singupBtn
        edtTxtEmail = binding.editTextSignupEmail
        edtTxtPassword = binding.editTextSignupPassword
        edtTxtConfirmPassword = binding.editTextSignupConfirmPassword
        progressBar = binding.progressBarSignup
        init()
    }

    private fun init() {
        progressBar.visibility = View.GONE
        backBtn.setOnClickListener { close() }
        signupBtn.setOnClickListener { signup() }
    }

    private fun signup() {
        val user: User
        val email = edtTxtEmail.text.toString()
        val password = edtTxtPassword.text.toString()
        val confirmPassword = edtTxtConfirmPassword.text.toString()
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, R.string.fillAllFields, Toast.LENGTH_SHORT).show()
            return
        }
        try {
            user = UserBuilder()
                .setEmail(email)
                .setPassword(password)
                .setConfirmPassword(confirmPassword)
                .allReadyToGo()
                .build()
        } catch (e: Exception) {
            val errorMsg = e.message ?: ""
            val snackbar = Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_SHORT)
            snackbar.setBackgroundTint(Color.RED)
            snackbar.show()
            return
        }
        progressBar.visibility = View.VISIBLE
        val signupThread = Thread {
            auth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        startMainActivity()
                    } else {
                        runOnUiThread {
                            val snackbar =
                                Snackbar.make(
                                    binding.root,
                                    R.string.signupError,
                                    Snackbar.LENGTH_SHORT
                                )
                            snackbar.setBackgroundTint(Color.RED)
                            snackbar.show()
                            progressBar.visibility = View.GONE
                        }
                    }

                }
                .addOnFailureListener {
                    runOnUiThread {
                        val errorMsg = when (it) {
                            is FirebaseAuthWeakPasswordException -> R.string.weakPassword
                            is FirebaseAuthInvalidCredentialsException -> R.string.invalidEmail
                            is FirebaseAuthUserCollisionException -> R.string.alreadyExists
                            is FirebaseNetworkException -> R.string.noConnection
                            else -> R.string.signupError
                        }
                        val snackbar = Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.RED)
                        snackbar.show()
                        progressBar.visibility = View.GONE
                    }

                }
        }
        signupThread.start()
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