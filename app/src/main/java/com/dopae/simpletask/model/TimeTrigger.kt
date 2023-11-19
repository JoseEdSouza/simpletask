package com.dopae.simpletask.model

import com.dopae.simpletask.utils.TriggerType
import java.util.Calendar
import java.util.Date

class TimeTrigger(private val dateTrigger: Date):Trigger {
    override val type = TriggerType.TIME
    override val valid: Boolean
        get(){
            val today = Date(System.currentTimeMillis())
            return dateTrigger >= today
        }

}