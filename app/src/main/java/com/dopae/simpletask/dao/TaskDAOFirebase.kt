package com.dopae.simpletask.dao

import com.dopae.simpletask.model.Task
import com.dopae.simpletask.model.TimeTrigger
import com.dopae.simpletask.model.Trigger
import com.dopae.simpletask.utils.IdGenerator
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class TaskDAOFirebase:DAO<Task> {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val idGenerator = IdGenerator(IdGenerator.IdRange.TASK)
    private val collection = "tasks"

    companion object {
        private var instance: TaskDAOFirebase? = null
        fun getInstance():DAO<Task>{
            if (instance == null)
                instance = TaskDAOFirebase()
            return instance!!
        }
    }

     override suspend fun get(id: String): Task? {
        val doc = db.collection(collection)
            .document(id)
            .get().await()
        return doc?.data?.let { fromMap(it) }
    }

     override suspend fun add(model:Task){
         val id = idGenerator.generateUniqueId()
         model.id = id
         db.collection(collection)
            .document(id)
            .set(toMap(model))
            .await()
    }

    override suspend fun update(id:String, model: Task){
        model.id = id
        db.collection(collection)
            .document(id)
            .set(toMap(model))
            .await()
    }

     override suspend fun remove(id:String){
        db.collection(collection)
            .document(id)
            .delete()
            .await()
    }

    override suspend fun getAll(): List<Task> {
        val docs = db.collection(collection)
            .whereEqualTo("uid", auth.currentUser!!.uid)
            .get()
            .await()
        return docs.mapNotNull { fromMap(it.data) }
    }

    private fun toMap(model: Task): Map<String, Any?> {
        return mapOf(
            "id" to model.id,
            "uid" to auth.currentUser?.uid,
            "name" to model.name,
            "description" to model.description,
            "concluded" to model.concluded,
            "tags" to model.tags ,
            "trigger" to model.trigger?.data
        )
    }

    private fun fromMap(data: Map<String, Any>): Task {
        val trigger:Trigger? = data["trigger"].let {
            when(it){
                is Timestamp -> TimeTrigger(it.toDate())
                else -> null
            }
        }
        val tags = (data["tags"] as? ArrayList<*>)?.filterIsInstance<String>() ?: emptyList()
        return Task(
            name = data["name"] as String,
            description = data["description"] as String,
            id =  data["id"] as String,
            concluded =  data["concluded"] as Boolean,
            trigger = trigger
        ).addAllTags(tags)
    }
}


