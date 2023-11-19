package com.dopae.simpletask.model

import com.dopae.simpletask.utils.Frequency
import java.util.Date

data class Habit(
    var name: String,
    var description: String,
    var frequency: Frequency,
    var startDate: Date,
    var id: Int = 0,
    var actualStreak:Int = 0,
    var bestStreak:Int = 0
)