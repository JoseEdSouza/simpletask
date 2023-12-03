package com.dopae.simpletask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dopae.simpletask.databinding.ActivityUserDetailsBinding

class UserDetailsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUserDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}