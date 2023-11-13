package com.dopae.simpletask.dao


interface DAO<T> {
    fun get(id: Int): T?
    fun add(model: T): Boolean
    fun update(id: Int, model: T): Boolean
    fun remove(id: Int): Boolean
    fun size(): Int
    fun getByPosition(position: Int): T
}