package com.dopae.simpletask.dao

interface OnFetchDataListener<T> {
    fun onSuccess(data: T)
    fun onFailure(e:Exception?)
}