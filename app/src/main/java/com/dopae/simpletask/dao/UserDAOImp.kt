package com.dopae.simpletask.dao

import com.dopae.simpletask.model.User

class UserDAOImp private constructor():DAO<User>{
    init {
        users = mutableListOf()
    }

    companion object{
        private lateinit var users: MutableList<User>
        private var instance:DAO<User>? = null
        fun getInstance():DAO<User>{
            if(instance == null)
                instance = UserDAOImp()
            return instance!!
        }
    }

    override fun get(id: Int): User? {
        for(u in users){
            if (u.id == id)
                return u
        }
        return null
    }

    override fun remove(id: Int): Boolean {
        val user = get(id) ?: return false
        return users.remove(user)
    }

    override fun update(id: Int, model: User): Boolean {
        val user = get(id)
        return user?.let{
            it.name = model.name
            it.email = model.email
            it.password = it.password
            true
        } ?: false
    }

    override fun add(model: User): Boolean {
        get(model.id) ?: return users.add(model)
        return false
    }


}