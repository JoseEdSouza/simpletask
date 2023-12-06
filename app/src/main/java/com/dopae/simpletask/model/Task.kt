package com.dopae.simpletask.model

data class Task(
    var name: String,
    var description: String,
    var id: String = "",
    var concluded: Boolean = false,
    private val tagSet: MutableSet<String> = mutableSetOf(),
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

    val tags: List<String>
        get() = tagSet.sorted().toList()

    fun addTag(tagId: String):Task {
        tagSet.add(tagId)
        return this
    }

    fun removeTag(tagId: String): Task {
        tagSet.remove(tagId)
        return this
    }

    fun addAllTags(tags: Collection<String>): Task {
        tagSet.addAll(tags)
        return this
    }

    fun setTags(tags: Collection<String>):Task{
        tagSet.clear()
        tagSet.addAll(tags)
        return this
    }

}
