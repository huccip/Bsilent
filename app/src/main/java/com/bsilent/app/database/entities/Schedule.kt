package com.bsilent.app.database.entities

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Schedule(
    @PrimaryKey(autoGenerate = true) var id:Long? = null,
    var name:String = "",
    var hour:Int = 0,
    var min:Int = 0,
    var duration:Int = 1,
    var isEnabled:Boolean = true,
    var silent:Boolean = true
    ) : Parcelable

data class ScheduleWithDays(
    @Embedded
    var schedule: Schedule,
    @Relation(
        parentColumn = "id",
        entityColumn = "scheduleId"
    )
    var days: List<Day>
){
    @Ignore
    var isSelected = false
}

@Entity(primaryKeys = ["scheduleId","day"])
data class Day(
    var scheduleId:Long = -1,
    var day: Int = -1
)