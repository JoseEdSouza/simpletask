package com.dopae.simpletask.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dopae.simpletask.R
import com.dopae.simpletask.TaskDetailsActivity
import com.dopae.simpletask.adapter.TaskAdapter
import com.dopae.simpletask.dao.TagDAOFirebase
import com.dopae.simpletask.dao.TaskDAOFirebase
import com.dopae.simpletask.databinding.FragmentTasksBinding
import com.dopae.simpletask.model.Tag
import com.dopae.simpletask.model.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

class TasksFragment : Fragment() {
    private lateinit var binding: FragmentTasksBinding
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyContentTxtView: TextView
    private var taskList: List<Task>? = null
    private var tagList: List<Tag>? = null
    private val daoTask = TaskDAOFirebase.getInstance()
    private val daoTag = TagDAOFirebase.getInstance()
    private val handler = CoroutineExceptionHandler { context, err ->
        context.cancel()
        val errorMsg = when (err) {
            is FirebaseNetworkException -> R.string.noConnection
            is FirebaseFirestoreException -> R.string.loadContentError
            else -> R.string.unexpectedError
        }
        lifecycleScope.launch(Dispatchers.Main) {
            progressBar.visibility = View.GONE
            val snackbar = Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_SHORT)
            snackbar.setBackgroundTint(Color.RED)
            snackbar.show()
            emptyContentTxtView.text =
                ContextCompat.getString(requireContext(), errorMsg)
            emptyContentTxtView.visibility = View.VISIBLE
        }
    }

    private val taskDetailsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            refresh()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        avedInstanceState: Bundle?
    ): View {
        binding = FragmentTasksBinding.inflate(inflater, container, false)
        fab = requireActivity().requireViewById(R.id.fabAdd)
        progressBar = requireActivity().requireViewById(R.id.progressBarMain)
        emptyContentTxtView = requireActivity().requireViewById(R.id.txtViewMainEmptyContent)
        taskRecyclerView = binding.recyclerViewTasks
        emptyContentTxtView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO + handler) {
            delay(250L)
            supervisorScope {
                launch {
                    taskList = daoTask.getAll()
                }
                launch {
                    tagList = daoTag.getAll()
                }
            }
            withContext(Dispatchers.Main) {
                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                progressBar.visibility = View.GONE
                taskList?.let {
                    if (it.isNotEmpty())
                        init()
                    else {
                        delay(250L)
                        TransitionManager.beginDelayedTransition(binding.root,AutoTransition())
                        emptyContentTxtView.text =
                            ContextCompat.getString(requireContext(), R.string.emptyTasks)
                        emptyContentTxtView.visibility = View.VISIBLE
                    }
                }
            }
        }

        return binding.root
    }

    fun init() {
        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
        taskRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            val mAdapter = TaskAdapter(taskList!!, tagList?.sortedBy { it.id } ?: emptyList())
            mAdapter.setOnItemClickListener { id -> startTaskDetailsActivity(id) }
            adapter = mAdapter
        }

        taskRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    private fun refresh() {
        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
        val fragmentManager = (requireContext() as? AppCompatActivity)?.supportFragmentManager
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.frame_container, TasksFragment())
            ?.commit()
    }

    private fun startTaskDetailsActivity(id: String) {
        val intent = Intent(activity, TaskDetailsActivity::class.java)
        intent.putExtra("ID", id)
        taskDetailsLauncher.launch(intent)
    }
}

