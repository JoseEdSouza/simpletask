package com.dopae.simpletask.model

data class Task(
    var name: String,
    var description: String,
    var id: Int = 0,
    var concluded: Boolean = false,
    private val tagSet: MutableSet<Int> = mutableSetOf(),
    var trigger: Trigger? = null
) {
    val hasDescription: Boolean
        get() = description != ""

    val numTags: Int
        get() = tagSet.size

    val hasTrigger: Boolean
        get() = trigger == null

    fun flipStatus() {
        concluded = !concluded
    }

    val tags: List<Int>
        get() = tagSet.sorted().toList()

    fun addTag(tag: Int):Task {
        tagSet.add(tag)
        return this
    }

    fun removeTag(tag: Int): Task {
        tagSet.remove(tag)
        return this
    }

    fun addAllTags(tags: Collection<Int>): Task {
        tagSet.addAll(tags)
        return this
    }

    fun clearTags():Task{
        tagSet.clear()
        return this
    }
}
