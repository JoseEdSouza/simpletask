package com.dopae.simpletask.dao
import com.dopae.simpletask.model.Tag


class TagDAOImp private constructor() : DAO<Tag> {

    init {
        tags = mutableListOf()
    }

    companion object {
        private lateinit var tags: MutableList<Tag>
        private var nextId = 0
        private var instance: DAO<Tag>? = null
        fun getInstance(): DAO<Tag> {
            if (instance == null)
                instance = TagDAOImp()
            return instance!!
        }
    }


    override fun get(id: Int): Tag? {
        for (t in tags) {
            if (t.id == id)
                return t
        }
        return null
    }

    override fun remove(id: Int): Boolean {
        val tag = get(id) ?: return false
        return tags.remove(tag)
    }

    override fun update(id: Int, model: Tag): Boolean {
        val tag = get(id)
        return tag?.let {
            it.name = model.name
            it.color = model.color
            true
        } ?: false
    }

    override fun add(model: Tag): Boolean {
        model.id = nextId++
        return tags.add(model)
    }

    override fun size(): Int = tags.size

    override fun getByPosition(position: Int): Tag = tags[position]
}