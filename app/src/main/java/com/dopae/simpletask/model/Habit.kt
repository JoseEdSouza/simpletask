package com.dopae.simpletask.model

import com.dopae.simpletask.utils.Frequency
import java.util.Date

class Habit(var id:Int,var name:String, var description:String, var frequency: Frequency,var startDate: Date)