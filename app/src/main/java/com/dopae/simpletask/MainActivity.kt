package com.dopae.simpletask


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    private val addActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDAOTask()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()



        floatingActionButton = binding.fabAdd
        bottomNavView = binding.bottomNavigation
        swapFragment(binding.bottomNavigation.selectedItemId)
        bottomNavView.setOnItemSelectedListener {
            swapFragment(it.itemId)
        }

        if (!checkNotificationPerm(this)) {
            askNotificationsPerm(this)
        }

    }

    private fun swapFragment(itemId: Int): Boolean {
        if (lastFragmentId == itemId)
            return false
        lastFragmentId = itemId
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
        return true
    }

    private fun replaceFragment(frag: Fragment) {
        TransitionManager.beginDelayedTransition(binding.frameContainer, AutoTransition())
        supportFragmentManager.beginTransaction().replace(binding.frameContainer.id, frag).commit()
    }

    private fun changeFabColor(itemId: Int) {
        val color = when (itemId) {
            R.id.bottom_tasks -> R.color.green_1
            R.id.bottom_habits -> R.color.red_1
            else -> R.color.blue_1
        }
        floatingActionButton.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, color))
    }

    private fun changeBottomNavItemColor(itemId: Int) {
        val color = when (itemId) {
            R.id.bottom_tasks -> R.color.color_item_task_bottom_nav
            R.id.bottom_habits -> R.color.color_item_habit_bottom_nav
            else -> R.color.color_item_tag_bottom_nav
        }
        bottomNavView.itemIconTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, color))
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
        var lastId = 0
        val tasks = listOf(
            Task(++lastId, "comprar presente", ""),
            Task(++lastId, "comprar carro", ""),
            Task(++lastId, "mercado claro", "comprar pão"),
            Task(++lastId, "mercado anatel", "comprar pão"),
            Task(++lastId, "mercado livre", "comprar pão"),
            Task(++lastId, "mercado livre aa", ""),
            Task(++lastId, "comprar iphone ", "fingir ser rica "),
            Task(++lastId, "comprar iphone teste teste teste", ""),
            Task(++lastId, "comprar iphone teste teste teste teste teste teste teste", ""),
            Task(++lastId, "comprar iphone a   b   c   d   e   f   g", ""),
            )
        val dao = TaskDAOImp.getInstance()
        tasks.forEach{ dao.add(it) }
    }

    fun startAddActivity(view: View) {
        val intent = Intent(this, AddTaskActivity::class.java)
        addActivityLauncher.launch(intent)
    }


}