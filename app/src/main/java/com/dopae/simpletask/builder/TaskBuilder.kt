package com.dopae.simpletask.builder


import com.dopae.simpletask.exception.NameNotReadyException
import com.dopae.simpletask.exception.TriggerNotReadyException
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.model.Trigger

class TaskBuilder {
    private var name = ""
    private var description  = ""
    private var concluded = false
    private val tags = mutableSetOf<Int>()
    private var trigger: Trigger? = null
    private var nameReadyToGo = false
    private var triggerReadyToGo = true


    fun setTrigger(trigger:Trigger?): TaskBuilder {
        this.trigger = trigger
        trigger?.let { triggerReadyToGo = it.valid  }
        return this
    }

    fun setName(name: String): TaskBuilder {
        this.name = name
        nameReadyToGo = name != ""
        return this
    }

    fun setDescription(description: String): TaskBuilder {
        this.description = description
        return this
    }

    fun addTags(tags: Set<Int>): TaskBuilder {
        this.tags.addAll(tags)
        return this
    }

    fun allReadyToGo(): TaskPreBuilder {
        if(!nameReadyToGo)
            throw NameNotReadyException()
        if(!triggerReadyToGo)
            throw TriggerNotReadyException()
        return TaskPreBuilder()
    }


    inner class TaskPreBuilder internal constructor() {
        fun build(): Task {
            return Task(
                name = name,
                description = description,
                id = 0,
                concluded = concluded,
                trigger = trigger
            ).addAllTags(tags)
        }
    }

}