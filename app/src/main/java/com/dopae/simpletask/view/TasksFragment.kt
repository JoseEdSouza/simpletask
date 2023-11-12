package com.dopae.simpletask.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dopae.simpletask.R
import com.dopae.simpletask.adapter.TaskAdapter
import com.dopae.simpletask.dao.TaskDAOImp
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)

        if (view is RecyclerView) {
            //setting recycler view
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = TaskAdapter(TaskDAOImp.getInstance())
            }

            //setting fab button behavior when scrolling
            val fab =
                requireActivity().findViewById<FloatingActionButton>(R.id.floatingActionButton3)
            view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                    if (dy > 0 && totalItemCount <= lastVisibleItem) {
                        fab.hide()
                    } else {
                        fab.show()
                    }
                }
            })
        }

        return view
    }


}

