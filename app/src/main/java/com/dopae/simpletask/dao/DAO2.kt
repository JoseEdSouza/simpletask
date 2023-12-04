package com.dopae.simpletask.dao

interface DAO2<T> {
    fun get(id: String,listener: OnFetchDataListener<T?>)
    fun add(model: T,listener: OnFetchDataListener<T?>)
    fun update(id: String, model: T,listener: OnFetchDataListener<T?>)
    fun remove(id: String,listener: OnFetchDataListener<T?>)
    fun size(listener: OnFetchDataListener<Int>)
    fun getAll(listener: OnFetchDataListener<List<T>?>)
}