package com.dopae.simpletask.dao

import com.dopae.simpletask.model.Task
import com.dopae.simpletask.model.TimeTrigger
import com.dopae.simpletask.utils.IdGenerator
import com.dopae.simpletask.utils.TriggerType
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.lang.Exception
import java.util.Date

class TaskDAOFirebase : DAO2<Task> {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val idGenerator = IdGenerator(IdGenerator.IdRange.TASK)
    private val collection = "tasks"

    companion object {
        private var instance: DAO2<Task>? = null
        fun getInstance(): DAO2<Task> {
            if (instance == null)
                instance = TaskDAOFirebase()
            return instance!!
        }
    }

    override fun get(id: String, listener: OnFetchDataListener<Task?>) {
        val taskDbRef = db.collection(collection)
        taskDbRef
            .whereEqualTo("uid", auth.currentUser!!.uid)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val fetchTask = it.result.firstOrNull()?.let { res ->
                        fromMap(res.data)
                    }
                    listener.onSuccess(fetchTask)
                } else {
                    listener.onFailure(null)
                }

            }
            .addOnFailureListener {
                listener.onFailure(it)
            }
    }

    override fun add(model: Task, listener: OnFetchDataListener<Task?>) {
        val taskDbRef = db.collection(collection).document(idGenerator.generateUniqueId())
        taskDbRef
            .set(toMap(model))
            .addOnCompleteListener {
                listener.onSuccess(null)
            }
            .addOnFailureListener {
                listener.onFailure(it)
            }
    }

    override fun update(id: String, model: Task, listener: OnFetchDataListener<Task?>) {
        val taskDbRef = db.collection(collection).document(id)
        taskDbRef
            .set(toMap(model))
            .addOnCompleteListener {
                listener.onSuccess(null)
            }
            .addOnFailureListener {
                listener.onFailure(it)
            }
    }

    override fun remove(id: String, listener: OnFetchDataListener<Task?>) {
        val taskDbRef = db.collection(collection).document(id)
        taskDbRef
            .delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    listener.onSuccess(null)
                } else {
                    listener.onFailure(null)
                }
            }
            .addOnFailureListener { e ->
                listener.onFailure(e)
            }
    }

    override fun size(listener: OnFetchDataListener<Int>) {
        val taskDbRef = db.collection(collection)
        taskDbRef
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val size = it.result?.size() ?: 0
                    listener.onSuccess(size)
                } else {
                    listener.onFailure(null)
                }
            }
            .addOnFailureListener {
                listener.onFailure(it)
            }
    }

    override fun getAll(listener: OnFetchDataListener<List<Task>?>) {
        val taskDbRef = db.collection(collection)
        taskDbRef
            .whereEqualTo("uid", auth.currentUser!!.uid)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val taskList =
                        it.result.documents.mapNotNull { doc -> doc.data?.let { it1 -> fromMap(it1) } }
                    listener.onSuccess(taskList)
                } else {
                    listener.onFailure(null)
                }
            }
            .addOnFailureListener {
                listener.onFailure(it)
            }
    }

    private fun toMap(task: Task?): Map<String, Any?> {
        if (task == null) {
            return emptyMap()
        }
        return mapOf(
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
        val tags = (data["tags"] as? Array<*>)?.mapNotNull { it as? Int } ?: emptyList()

        return Task(
            data["name"] as String,
            data["description"] as String,
            data["id"] as Int,
            data["concluded"] as Boolean,
            trigger = trigger
        ).addAllTags(tags)
    }
}


