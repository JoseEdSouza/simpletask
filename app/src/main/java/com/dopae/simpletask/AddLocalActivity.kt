package com.dopae.simpletask

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.dopae.simpletask.component.MenuAddEditComponent
import com.dopae.simpletask.databinding.ActivityAddLocalBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView

class AddLocalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddLocalBinding
    private lateinit var menu: MenuAddEditComponent
    private lateinit var mapView :MapView
    private lateinit var radioGroup: RadioGroup


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLocalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.task_theme)
        menu = MenuAddEditComponent(binding.menuAddEdtLocal)
        menu.init({ save() }, { close() })
    }

    private fun close() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun save() {
        //todo
    }
}