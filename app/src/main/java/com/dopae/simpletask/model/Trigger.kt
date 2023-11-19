package com.dopae.simpletask.model

import com.dopae.simpletask.utils.TriggerType

interface Trigger {
    val type:TriggerType
    val valid:Boolean

}