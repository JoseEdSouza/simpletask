package com.dopae.simpletask.component

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.dopae.simpletask.AddTagActivity
import com.dopae.simpletask.AddTaskActivity
import com.dopae.simpletask.R
import com.dopae.simpletask.fragment.TagsFragment
import com.dopae.simpletask.fragment.TasksFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MenuNavigationComponent(
    private val context: Context,
    private val root: ViewGroup,
    private val supportFragmentManager: FragmentManager,
    private val frameContainer: FrameLayout,
    private val bottomNavView: BottomNavigationView,
    private val floatingActionButton: FloatingActionButton,
    private val progressBar: ProgressBar,
    private val emptyContent: TextView,
    private val launcher:ActivityResultLauncher<Intent>
) {

    private var lastFragmentId = 0

    fun init() {
        progressBar.visibility = View.GONE
        swapFragment(bottomNavView.selectedItemId)
        bottomNavView.setOnItemSelectedListener {
            swapFragment(it.itemId)
        }
        floatingActionButton.setOnClickListener { startAddActivity() }
    }

    fun reload() {
        swapFragment(lastFragmentId)
    }

    private fun swapFragment(itemId: Int): Boolean {
        TransitionManager.beginDelayedTransition(root, AutoTransition())
        emptyContent.visibility = View.INVISIBLE
        lastFragmentId = itemId
        when (itemId) {
            R.id.bottom_tasks -> replaceFragment(TasksFragment())
            R.id.bottom_tags -> replaceFragment(TagsFragment())
            else -> Toast.makeText(
                context,
                bottomNavView.menu.findItem(itemId).title,
                Toast.LENGTH_SHORT
            ).show()
        }
        changeFabColor(itemId)
        changeBottomNavItemColor(itemId)
        return true
    }

    private fun replaceFragment(frag: Fragment) {
        TransitionManager.beginDelayedTransition(frameContainer, AutoTransition())
        supportFragmentManager.beginTransaction().replace(frameContainer.id, frag).commit()
    }

    private fun changeFabColor(itemId: Int) {
        val color = when (itemId) {
            R.id.bottom_tasks -> R.color.task_theme
            else -> R.color.blue_1
        }
        floatingActionButton.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, color))
    }

    private fun changeBottomNavItemColor(itemId: Int) {
        val color = when (itemId) {
            R.id.bottom_tasks -> R.color.color_item_task_bottom_nav
            else ->  R.color.color_item_tag_bottom_nav
        }
        bottomNavView.itemIconTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, color))
    }

    private fun startAddActivity() {
        val act = when (lastFragmentId) {
            R.id.bottom_tasks -> AddTaskActivity::class.java
            R.id.bottom_tags -> AddTagActivity::class.java
            else -> AddTaskActivity::class.java // todo - add habit
        }
        val intent = Intent(context, act)
        launcher.launch(intent)
    }
}