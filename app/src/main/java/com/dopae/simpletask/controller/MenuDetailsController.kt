package com.dopae.simpletask.controller

import android.view.View.OnClickListener
import com.dopae.simpletask.databinding.MenuDetailsBinding

class MenuDetailsController(binding: MenuDetailsBinding) {
    private val concludedBtn = binding.imgBtnConcluded
    private val editBtn = binding.imgBtnEdit
    private val deleteBtn = binding.imgBtnDelete

    fun init(
        onConcludedClickListener: OnClickListener,
        onEditClickListener: OnClickListener,
        onDeleteClickListener: OnClickListener
    ) {
        concludedBtn.setOnClickListener(onConcludedClickListener)
        editBtn.setOnClickListener(onEditClickListener)
        deleteBtn.setOnClickListener(onDeleteClickListener)
    }
}