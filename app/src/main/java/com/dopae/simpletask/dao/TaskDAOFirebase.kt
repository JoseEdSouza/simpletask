package com.dopae.simpletask.dao

import com.dopae.simpletask.model.Task
import com.dopae.simpletask.model.TimeTrigger
import com.dopae.simpletask.utils.IdGenerator
import com.dopae.simpletask.utils.TriggerType
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class TaskDAOFirebase {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val idGenerator = IdGenerator(IdGenerator.IdRange.TASK)
    private val collection = "tasks"

    companion object {
        private var instance: TaskDAOFirebase? = null
        fun getInstance():TaskDAOFirebase{
            if (instance == null)
                instance = TaskDAOFirebase()
            return instance!!
        }
    }

     suspend fun get(id: String): Task? {
        val doc = db.collection(collection)
            .document(id)
            .get().await()
        return doc?.data?.let { fromMap(it) }
    }

     suspend fun add(model:Task){
         val id = idGenerator.generateUniqueId()
         model.id = id
         db.collection(collection)
            .document(id)
            .set(toMap(model))
            .await()
    }

    suspend fun update(id:String, model: Task){
        db.collection(collection)
            .document(id)
            .set(toMap(model))
            .await()
    }

     suspend fun delete(id:String){
        db.collection(collection)
            .document(id)
            .delete()
            .await()
    }

     suspend fun getAll():List<Task>{
        val docs = db.collection(collection)
            .whereEqualTo("uid",auth.currentUser!!.uid)
            .get()
            .await()
        return docs.mapNotNull { fromMap(it.data) }
    }

    private fun toMap(task: Task?): Map<String, Any?> {
        if (task == null) {
            return emptyMap()
        }
        return mapOf(
            "id" to task.id,
            "uid" to auth.currentUser?.uid,
            "name" to task.name,
            "description" to task.description,
            "concluded" to task.concluded,
            "trigger" to task.trigger?.let {
                mapOf(
                    "data" to it.data
                )
            }
        )
    }

    private fun fromMap(data: Map<String, Any>): Task {
        val triggerData = data["trigger"] as Map<*, *>
        val type = if (triggerData["data"] is Date) TriggerType.TIME else TriggerType.LOCAL
        val trigger =
            if (type == TriggerType.TIME) TimeTrigger(triggerData["data"] as Date) else null
        val tags = (data["tags"] as? Array<*>)?.mapNotNull { it as? String} ?: emptyList()

        return Task(
            data["name"] as String,
            data["description"] as String,
            data["id"] as String,
            data["concluded"] as Boolean,
            trigger = trigger
        ).addAllTags(tags)
    }
}


