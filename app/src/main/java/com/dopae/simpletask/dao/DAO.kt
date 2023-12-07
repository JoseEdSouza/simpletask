package com.dopae.simpletask.dao

interface DAO<T> {
    suspend fun get(id: String): T?
    suspend fun add(model: T)
    suspend fun update(id: String, model: T)
    suspend fun remove(id: String)
    suspend fun getAll(): List<T>
}