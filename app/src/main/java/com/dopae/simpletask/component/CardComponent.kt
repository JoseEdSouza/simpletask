package com.dopae.simpletask.component

import android.view.View.OnClickListener

interface CardComponent {
    fun init()
    fun changeState()
    fun setOnClickListener(onClickListener: OnClickListener)
    val isActivated:Boolean
}