package com.dopae.simpletask.controller

import android.view.View.OnClickListener

interface CardController {
    fun init()
    fun changeState()
    fun setOnClickListener(onClickListener: OnClickListener)
    val isActivated:Boolean
}