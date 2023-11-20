package com.dopae.simpletask.controller

import android.view.View.OnClickListener
import com.dopae.simpletask.databinding.MenuAddEditBinding

class MenuAddEditController(binding: MenuAddEditBinding) {
    private val saveBtn = binding.imgBtnSave
    private val cancelBtn = binding.imgBtnCancel

    fun init(onSaveListener: OnClickListener,onCancelListener: OnClickListener) {
        saveBtn.setOnClickListener(onSaveListener)
        cancelBtn.setOnClickListener(onCancelListener)
    }
}