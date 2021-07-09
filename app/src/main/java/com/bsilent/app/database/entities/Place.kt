package com.bsilent.app.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Place(
    @PrimaryKey(autoGenerate = true) var id:Long? = null,
    var lat:Double = 0.0,
    var lng:Double = 0.0,
    var name:String = "",
    var img:String = "",
    var radius:Int = 0,
    var isEnabled:Boolean = true,
    var silent:Boolean = true
) : Parcelable