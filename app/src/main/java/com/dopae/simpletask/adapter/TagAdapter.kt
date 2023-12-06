package com.dopae.simpletask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dopae.simpletask.component.TagAdapterComponent
import com.dopae.simpletask.databinding.TagAdapterBinding
import com.dopae.simpletask.model.Tag

class TagAdapter(private val tagList:List<Tag>) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {
    private lateinit var binding: TagAdapterBinding
    private var mListener: OnItemClickListener? = null
    private val positionIdMap = HashMap<Int, String>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        context = parent.context
        binding = TagAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return TagViewHolder(binding)
    }

    override fun getItemCount(): Int = tagList.size

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tagList[position]
        positionIdMap[position] = tag.id
        TagAdapterComponent(context, holder.binding, tag).init()

    }

    fun interface OnItemClickListener {
        fun onItemClick(id:String)
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
                val id = positionIdMap[absoluteAdapterPosition] ?: ""
                if (id != "") {
                    it.onItemClick(id)
                }
            }
        }

    }
}