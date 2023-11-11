package com.dopae.simpletask.model

data class Task (var id:Int,var name:String, var description:String, var concluded:Boolean,var tags: List<Tag>)
