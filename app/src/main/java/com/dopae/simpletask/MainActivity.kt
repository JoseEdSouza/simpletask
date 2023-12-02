package com.dopae.simpletask


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.dopae.simpletask.component.MenuNavigationComponent
import com.dopae.simpletask.dao.TagDAOImp
import com.dopae.simpletask.dao.TaskDAOImp
import com.dopae.simpletask.databinding.ActivityMainBinding
import com.dopae.simpletask.model.Tag
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.utils.TagColor
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var menu: MenuNavigationComponent
    private val addActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                menu.reload()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDAOTask()
        initDAOTag()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        floatingActionButton = binding.fabAdd
        bottomNavView = binding.bottomNavigation
        menu = MenuNavigationComponent(
            this,
            supportFragmentManager,
            binding.frameContainer,
            bottomNavView,
            floatingActionButton,
            addActivityLauncher
        )
        menu.init()

        if (!checkNotificationPerm(this)) {
            askNotificationsPerm(this)
        }

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
        return NotificationManagerCompat.from(context).areNotificationsEnabled()

    }

    private fun initDAOTask() {
        val tasks = listOf(
            Task(name = "comprar presente", description = ""),
        )
        val dao = TaskDAOImp.getInstance()
        tasks.forEach { dao.add(it) }
    }

    private fun initDAOTag() {
        val tags = listOf(
            Tag("estudos", TagColor.YELLOW),
            Tag("faculdade", TagColor.BLUE),
            Tag("Trbalho", TagColor.LILAS)
        )
        val dao = TagDAOImp.getInstance()
        tags.forEach { dao.add(it) }
    }


}