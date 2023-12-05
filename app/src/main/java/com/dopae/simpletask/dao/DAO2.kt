package com.dopae.simpletask.dao

interface DAO2<T> {
    suspend fun get(id: String): T?
    suspend fun add(model: T)
    suspend fun update(id: String, model: T)
    suspend fun delete(id: String)
    suspend fun getAll(): List<T>
}