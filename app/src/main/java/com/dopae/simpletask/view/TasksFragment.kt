package com.dopae.simpletask.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dopae.simpletask.R
import com.dopae.simpletask.TaskDetailsActivity
import com.dopae.simpletask.adapter.TaskAdapter
import com.dopae.simpletask.dao.TaskDAOImp
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksFragment : Fragment() {


    private val taskDetailsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
            }

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)

        if (view is RecyclerView) {
            //setting recycler view
            with(view) {
                layoutManager = LinearLayoutManager(context)
                val mAdapter = TaskAdapter()
                mAdapter.setOnItemClickListener(object : TaskAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        startTaskDetailsActivity(position)
                    }
                })
                adapter = mAdapter

            }

            //setting fab button behavior when scrolling
            val fab =
                requireActivity().findViewById<FloatingActionButton>(R.id.fabAdd)
            view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                    if (dy > 0 && totalItemCount <= lastVisibleItem + 1) {
                        fab.hide()
                    } else {
                        fab.show()
                    }
                }
            })
        }
        return view
    }

    fun startTaskDetailsActivity(position: Int) {
        val intent = Intent(activity, TaskDetailsActivity::class.java)
        intent.putExtra("ID", position)
        taskDetailsLauncher.launch(intent)
    }


}

