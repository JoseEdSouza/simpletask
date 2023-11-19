package com.dopae.simpletask.dao

import com.dopae.simpletask.model.Task

class TaskDAOImp private constructor() : DAO<Task> {

    init {
        tasks = mutableListOf()
    }

    companion object {
        private lateinit var tasks: MutableList<Task>
        private var instance: DAO<Task>? = null
        private var nextId: Int = 0
        fun getInstance(): DAO<Task> {
            if (instance == null)
                instance = TaskDAOImp()
            return instance!!
        }
    }

    override fun get(id: Int): Task? {
        for (t in tasks) {
            if (t.id == id)
                return t
        }
        return null
    }

    override fun remove(id: Int): Boolean {
        val task: Task = get(id) ?: return false
        return tasks.remove(task)
    }

    override fun update(id: Int, model: Task): Boolean {
        val task = get(id)
        return task?.let {
            it.name = model.name
            it.description = model.description
            it.concluded = model.concluded
            model.tags.forEach { tag -> it.addTag(tag) }
            true
        } ?: false
    }

    override fun add(model: Task): Boolean {
        model.id = nextId++
        return tasks.add(model)
    }

    override fun size(): Int = tasks.size

    override fun getByPosition(position: Int): Task = tasks[position]

}