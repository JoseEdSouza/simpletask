package com.dopae.simpletask.controller

import android.view.View
import com.dopae.simpletask.databinding.MenuEditTagBinding

class MenuEditTagController(binding: MenuEditTagBinding) {
    private val saveBtn = binding.imgBtnSave
    private val cancelBtn = binding.imgBtnCancel
    private val deleteBtn = binding.imgBtnDelete

    fun init(
        onSaveListener: View.OnClickListener,
        onCancelListener: View.OnClickListener,
        onDeleteListener: View.OnClickListener
    ) {
        saveBtn.setOnClickListener(onSaveListener)
        cancelBtn.setOnClickListener(onCancelListener)
        deleteBtn.setOnClickListener(onDeleteListener)
    }
}
