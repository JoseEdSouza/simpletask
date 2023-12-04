package com.dopae.simpletask.utils

import kotlin.random.Random

class IdGenerator(private val range: IdRange) {
    enum class IdRange(val start: Int, val end: Int, val letter: Char) {
        TASK(0, 1000, 'a'),
        TAG(1001, 2000, 'b'),
        HABIT(2001, 3000, 'c')
    }

    fun generateUniqueId(): String {
        val timestamp = System.currentTimeMillis()
        val randomComponent = Random.nextInt(range.start, range.end)
        return "$timestamp$randomComponent${range.letter}"
    }

}