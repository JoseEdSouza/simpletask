package com.dopae.simpletask.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dopae.simpletask.R
import com.dopae.simpletask.adapter.TagAdapter
import com.dopae.simpletask.databinding.FragmentTagsBinding
import com.dopae.simpletask.databinding.TagAdapterBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TagsFragment : Fragment() {
    private lateinit var binding: FragmentTagsBinding
    private lateinit var tagRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTagsBinding.inflate(inflater, container, false)
        tagRecyclerView = binding.recyclerViewTags
        tagRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            val mAdapter = TagAdapter()
            mAdapter.setOnItemClickListener(object : TagAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // todo - add fun
                }
            })
            adapter = mAdapter
        }

        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fabAdd)
        tagRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

        return tagRecyclerView
    }
}