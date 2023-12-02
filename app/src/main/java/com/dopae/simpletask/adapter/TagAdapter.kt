package com.dopae.simpletask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dopae.simpletask.component.TagAdapterComponent
import com.dopae.simpletask.dao.TagDAOImp
import com.dopae.simpletask.databinding.TagAdapterBinding

class TagAdapter : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {
    private lateinit var binding: TagAdapterBinding
    private val dao = TagDAOImp.getInstance()
    private var mListener: OnItemClickListener? = null
    private val positionIdMap = HashMap<Int, Int>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        context = parent.context
        binding = TagAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return TagViewHolder(binding)
    }

    override fun getItemCount(): Int = dao.size()

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = dao.getByPosition(position)
        positionIdMap[position] = tag.id
        TagAdapterComponent(context, holder.binding, tag).init()

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }


    inner class TagViewHolder(
        val binding: TagAdapterBinding
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mListener?.let {
                val id = positionIdMap[absoluteAdapterPosition] ?: RecyclerView.NO_POSITION
                if (id != RecyclerView.NO_POSITION) {
                    it.onItemClick(id)
                }
            }
        }

    }
}