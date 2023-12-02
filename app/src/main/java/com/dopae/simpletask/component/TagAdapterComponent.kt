package com.dopae.simpletask.component

import android.content.Context
import com.dopae.simpletask.databinding.TagAdapterBinding
import com.dopae.simpletask.model.Tag

class TagAdapterComponent(
    private val context: Context,
    binding: TagAdapterBinding,
    private val tag: Tag
) {
    private val tagIcon = binding.imgViewTagIcon
    private val tagName = binding.txtViewTagText

    fun init() {
        tagIcon.imageTintList = tag.color.getColorStateList(context)
        tagName.text = tag.name
    }

}