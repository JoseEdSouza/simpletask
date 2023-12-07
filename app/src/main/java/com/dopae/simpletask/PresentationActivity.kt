package com.dopae.simpletask

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import com.dopae.simpletask.databinding.ActivityPresentationBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class PresentationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPresentationBinding
    private lateinit var signupBtn: Button
    private lateinit var loginBtn: Button
    private val auth = Firebase.auth
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresentationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        signupBtn = binding.singupBtn
        loginBtn = binding.loginBtn
        init()
    }

    fun init() {
        loginBtn.setOnClickListener { startLoginActivity() }
        signupBtn.setOnClickListener { startSignupActivity() }
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        launcher.launch(intent)
    }

    private fun startSignupActivity() {
        val intent = Intent(this, SignupActivity::class.java)
        launcher.launch(intent)
    }

    private fun startMainActivity(){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
       auth.currentUser?.let{
           startMainActivity()
       }
    }

}