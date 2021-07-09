package com.bsilent.app.utils

import android.content.Context
import com.bsilent.app.R
import com.bsilent.app.database.entities.Day
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimeUtils {

    fun dayToStr(day:Int,c:Context):String = when(day){
        0 ->  c.getString(R.string.sun)
        1 ->  c.getString(R.string.mon)
        2 ->  c.getString(R.string.tuesd)
        3 ->  c.getString(R.string.wed)
        4 ->  c.getString(R.string.thu)
        5 ->  c.getString(R.string.frid)
        else ->  c.getString(R.string.sat)
    }

    fun daysListToStr(days:List<Day>,c:Context) : String{
        if(days.size==7){
            return c.getString(R.string.evr_day)
        }
        if(days.isEmpty())
            return ""
        var res = ""
        res += dayToStr(days.first().day,c)
        if(days.size>1) {
            for (i in 1 until days.size) {
                res += ", ${dayToStr(days[i].day, c)}"
            }
        }
        return res
    }

    fun timeToString(hour: Int, min: Int, duration: Int = 0) : String =
        SimpleDateFormat("HH:mm").format(
            Date(
                TimeUnit.MINUTES.toMillis(((hour + duration) * 60 + min).toLong())
            )
        )
}