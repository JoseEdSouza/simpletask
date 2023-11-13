package com.dopae.simpletask.model

data class Task(
    val id: Int,
    var name: String,
    var description: String,
    var concluded: Boolean = false,
    private val tagSet: MutableSet<Int> = mutableSetOf(),
    var trigger: Trigger? = null
) {
    val hasDescription: Boolean
        get() = description == ""

    val numTags: Int
        get() = tagSet.size

    val hasTrigger: Boolean
        get() = trigger == null

    fun flipStatus() {
        concluded = !concluded
    }

    val tags: Iterator<Int>
        get() = tagSet.sorted().iterator()

    fun addTag(tag: Int) {
        tagSet.add(tag)
    }

    fun removeTag(tag: Tag) = tagSet.remove(tag.id)
    fun addAllTags(tags: Collection<Int>) {
        tags.forEach {
            this.tagSet.add(it)
        }
    }


}
