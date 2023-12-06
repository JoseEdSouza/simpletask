package com.dopae.simpletask


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.dopae.simpletask.component.MenuNavigationComponent
import com.dopae.simpletask.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var logoutBtn: ImageButton
    private lateinit var emptyContentTxtView:TextView
    private lateinit var menu: MenuNavigationComponent
    private val auth = Firebase.auth
    private val addActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            menu.reload()
            if (it.resultCode == Activity.RESULT_OK) {
                emptyContentTxtView.visibility = View.INVISIBLE

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        logoutBtn = binding.imgBtnLogout
        floatingActionButton = binding.fabAdd
        bottomNavView = binding.bottomNavigation
        progressBar = binding.progressBarMain
        emptyContentTxtView = binding.txtViewMainEmptyContent
        menu = MenuNavigationComponent(
            this,
            binding.root,
            supportFragmentManager,
            binding.frameContainer,
            bottomNavView,
            floatingActionButton,
            progressBar,
            emptyContentTxtView,
            addActivityLauncher
        )
        init()
    }

    private fun init() {
        if (!checkNotificationPerm(this)) {
            askNotificationsPerm(this)
        }
        logoutBtn.setOnClickListener { logout() }
        menu.init()
    }

    private fun logout() {
        val alertDialog = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setTitle(R.string.logout)
            .setMessage(R.string.askLogoutMessage)
            .setPositiveButton(R.string.leave) { _, _ ->
                auth.signOut()
                val intent = Intent(this, PresentationActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton(R.string.cancel, null)
        alertDialog.show()
    }

    private fun askNotificationsPerm(context: Context) {
        val alertDialog = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setTitle(R.string.askPermissionNotificationsTitle)
            .setMessage(R.string.askPermissionNotificationMessage)
            .setPositiveButton(R.string.settings) { _, _ ->
                val intent = Intent()
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
                startActivity(intent)
            }
            .setNegativeButton(R.string.cancel, null)
        alertDialog.show()
    }

    private fun checkNotificationPerm(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()

    }
}