package com.dopae.simpletask.model

data class Task(
    var id: Int,
    var name: String,
    var description: String,
    var concluded: Boolean = false,
    val tags: MutableSet<Int> = mutableSetOf(),
    var trigger: Trigger? = null
) {
    fun addTag(tag: Tag) = tags.add(tag.id)
    fun removeTag(tag: Tag) = tags.remove(tag.id)
    fun addAllTags(tags: Collection<Tag>) {
        tags.forEach {
            this.tags.add(it.id)
        }
    }


}
