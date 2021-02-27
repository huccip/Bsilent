package com.bsilent.app.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Schedule(
    @PrimaryKey(autoGenerate = true) var id:Long? = null,
    var startTime:Long = 0,
    var endTime:Long = 0,
    var isEnabled:Boolean = true,
    var silent:Boolean = true,
    var monday:Boolean = true,
    var tuesday:Boolean = true,
    var wednesday:Boolean = true,
    var thursday:Boolean = true,
    var friday:Boolean = true,
    var saturday:Boolean = true,
    var sunday:Boolean = true

    )