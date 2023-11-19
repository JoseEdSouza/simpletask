package com.dopae.simpletask.dao

import com.dopae.simpletask.model.Habit


class HabitDAOImp private constructor() : DAO<Habit> {

    init {
        habits = mutableListOf()
    }

    companion object {
        private lateinit var habits: MutableList<Habit>
        private var nextId = 0
        private var instance: DAO<Habit>? = null
        fun getInstance(): DAO<Habit> {
            if (instance == null)
                instance = HabitDAOImp()
            return instance!!
        }
    }


    override fun get(id: Int): Habit? {
        for (h in habits) {
            if (h.id == id)
                return h
        }
        return null
    }

    override fun remove(id: Int): Boolean {
        val habit = get(id) ?: return false
        return habits.remove(habit)
    }

    override fun update(id: Int, model: Habit): Boolean {
        val habit = get(id)
        return habit?.let {
            it.name = model.name
            it.description = model.description
            it.frequency = model.frequency
            it.startDate = model.startDate
            it.actualStreak = model.actualStreak
            it.bestStreak = model.bestStreak
            true
        } ?: false
    }

    override fun add(model: Habit): Boolean {
        model.id = nextId++
        return habits.add(model)
    }

    override fun size(): Int = habits.size

    override fun getByPosition(position: Int): Habit = habits[position]

}