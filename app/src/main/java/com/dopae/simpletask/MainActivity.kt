package com.dopae.simpletask


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavView: BottomNavigationView
    private var lastFragmentId: Int = Int.MIN_VALUE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        bottomNavView = findViewById(R.id.bottom_navigation)
        changeFragment(R.id.bottom_tasks)
        bottomNavView.setOnItemSelectedListener {
            changeFragment(it.itemId)
        }

        if (!checkNotificationPerm(this)) {
            askNotificationsPerm(this)
        }

    }

    private fun changeFragment(itemId: Int): Boolean {
        if (lastFragmentId == itemId)
            return false
        when (itemId) {
            R.id.bottom_tasks -> replaceFragment(TasksFragment())
            else -> Toast.makeText(
                this,
                bottomNavView.menu.findItem(itemId).title,
                Toast.LENGTH_SHORT
            ).show()
        }
        lastFragmentId = itemId
        return true
    }

    private fun replaceFragment(frag: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, frag).commit()
    }

    private fun askNotificationsPerm(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(R.string.ask_permission_notifications_title)
            .setMessage(R.string.ask_permission_notifications_msg)
            .setPositiveButton(R.string.settings) { _, _ ->
                val intent = Intent()
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
                this.startActivity(intent)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun checkNotificationPerm(context: Context): Boolean {
        return NotificationManagerCompat.from(this).areNotificationsEnabled()

    }


}