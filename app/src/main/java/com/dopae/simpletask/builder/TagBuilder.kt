package com.dopae.simpletask.builder

import com.dopae.simpletask.exception.ColorNotReadyException
import com.dopae.simpletask.exception.NameNotReadyException
import com.dopae.simpletask.model.Tag
import com.dopae.simpletask.utils.TagColor

class TagBuilder {
    private var name = ""
    private var color: TagColor? = null
    private var nameReadyToGo = false
    private var colorReadyToGo = false

    fun setName(name:String):TagBuilder{
        this.name = name
        nameReadyToGo = name != ""
        return this
    }

    fun setColor(color:TagColor?):TagBuilder{
        this.color = color
        colorReadyToGo = color != null
        return this
    }

    fun allReadyToGo():TagPreBuilder{
        if(!nameReadyToGo)
            throw NameNotReadyException()
        if(!colorReadyToGo)
            throw ColorNotReadyException()
        return TagPreBuilder()
    }

    inner class TagPreBuilder internal constructor(){
        fun build(): Tag {
            return Tag(name,color!!)
        }
    }
}