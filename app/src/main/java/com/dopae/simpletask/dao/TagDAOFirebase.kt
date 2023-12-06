package com.dopae.simpletask.dao

import com.dopae.simpletask.model.Tag
import com.dopae.simpletask.utils.IdGenerator
import com.dopae.simpletask.utils.TagColor
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class TagDAOFirebase private constructor() : DAO<Tag> {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val idGenerator = IdGenerator(IdGenerator.IdRange.TAG)
    private val collection = "tags"

    companion object {
        private var instance: TagDAOFirebase? = null
        fun getInstance(): DAO<Tag> {
            if (instance == null)
                instance = TagDAOFirebase()
            return instance!!
        }
    }

    override suspend fun get(id: String): Tag? {
        val doc = db.collection(collection)
            .document(id)
            .get().await()
        return doc?.data?.let { fromMap(it) }
    }

    override suspend fun add(model: Tag) {
        val id = idGenerator.generateUniqueId()
        model.id = id
        db.collection(collection)
            .document(id)
            .set(toMap(model))
            .await()
    }

    override suspend fun update(id: String, model: Tag) {
        model.id = id
        db.collection(collection)
            .document(id)
            .set(toMap(model))
            .await()
    }

    override suspend fun remove(id: String) {
        db.collection(collection)
            .document(id)
            .delete()
            .await()
    }

    override suspend fun getAll(): List<Tag> {
        val docs = db.collection(collection)
            .whereEqualTo("uid", auth.currentUser!!.uid)
            .get()
            .await()
        return docs.mapNotNull { fromMap(it.data) }
    }

    private fun toMap(model: Tag): Map<String, Any?> {
        return mapOf(
            "id" to model.id,
            "uid" to auth.currentUser?.uid,
            "name" to model.name,
            "color" to model.color.name
        )
    }

    private fun fromMap(data: Map<String, Any>): Tag {
        return Tag(
            data["name"] as String,
            TagColor.valueOf(data["color"] as String),
            data["id"] as String
        )
    }

}