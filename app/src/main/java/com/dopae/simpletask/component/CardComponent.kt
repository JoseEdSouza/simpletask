package com.dopae.simpletask.component

import android.view.View.OnClickListener

interface CardComponent {
    fun init()
    fun flipState()
    fun setActivated()
    fun setDeactivated()
    fun setOnClickListener(onClickListener: OnClickListener)
    val isActivated:Boolean
}