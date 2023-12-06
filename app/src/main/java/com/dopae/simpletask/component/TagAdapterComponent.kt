package com.dopae.simpletask.component

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import com.dopae.simpletask.databinding.TagAdapterBinding
import com.dopae.simpletask.model.Tag

class TagAdapterComponent(
    private val context: Context,
    val binding: TagAdapterBinding,
    private val tag: Tag
) {
    private val tagIcon = binding.imgViewTagIcon
    private val tagName = binding.txtViewTagText

    fun init() {
        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
        tagIcon.imageTintList = tag.color.getColorStateList(context)
        tagName.text = tag.name
    }

}