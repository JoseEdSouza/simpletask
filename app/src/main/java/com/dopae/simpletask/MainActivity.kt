package com.dopae.simpletask


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dopae.simpletask.dao.TaskDAOImp
import com.dopae.simpletask.databinding.ActivityMainBinding
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.view.TasksFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var floatingActionButton: FloatingActionButton
    private var lastFragmentId: Int = Int.MIN_VALUE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDAOTask()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        floatingActionButton = binding.floatingActionButton3
        bottomNavView = binding.bottomNavigation
        changeFragment(binding.bottomNavigation.selectedItemId)
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
        changeFabColor(itemId)
        changeBottomNavItemColor(itemId)
        lastFragmentId = itemId
        return true
    }


    private fun changeFabColor(itemId:Int){
        val color = when(itemId) {
            R.id.bottom_tasks -> R.color.green_1
            R.id.bottom_habits -> R.color.red_1
            else -> R.color.blue_1
        }
        floatingActionButton.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, color))
    }

    private fun changeBottomNavItemColor(itemId:Int){
        val color = when(itemId) {
            R.id.bottom_tasks -> R.color.color_item_task_bottom_nav
            R.id.bottom_habits -> R.color.color_item_habit_bottom_nav
            else -> R.color.color_item_tag_bottom_nav
        }
        bottomNavView.itemIconTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, color))
    }

    private fun replaceFragment(frag: Fragment) {
        supportFragmentManager.beginTransaction().replace(binding.frameContainer.id, frag).commit()

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
        val task1 = Task(1, "comprar presente", "", false, mutableSetOf())
        val task2 = Task(2, "comprar carro", "", false, mutableSetOf())
        val task3 = Task(3, "mercado claro", "comprar pão", false, mutableSetOf())
        val task4 = Task(4, "mercado anatel", "comprar pão", false, mutableSetOf())
        val task5 = Task(5, "mercado livre", "comprar pão", false, mutableSetOf())
        val task6 = Task(6, "mercado livre aa", "", false, mutableSetOf())
        val task7 = Task(7, "comprar iphone ", "fingir ser rica ", false, mutableSetOf())
        val dao = TaskDAOImp.getInstance()
        dao.add(task1)
        dao.add(task2)
        dao.add(task3)
        dao.add(task4)
        dao.add(task5)
        dao.add(task6)
        dao.add(task7)
    }
}