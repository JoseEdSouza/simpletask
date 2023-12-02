package com.dopae.simpletask.component

import android.view.View.OnClickListener
import com.dopae.simpletask.databinding.MenuAddEditBinding

class MenuAddEditComponent(binding: MenuAddEditBinding) {
    private val saveBtn = binding.imgBtnSave
    private val cancelBtn = binding.imgBtnCancel

    fun init(onSaveListener: OnClickListener,onCancelListener: OnClickListener) {
        saveBtn.setOnClickListener(onSaveListener)
        cancelBtn.setOnClickListener(onCancelListener)
    }
}