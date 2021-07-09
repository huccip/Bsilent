package com.bsilent.app.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bsilent.app.broadcasts.ScheduleReceiver
import com.bsilent.app.database.entities.Schedule
import com.bsilent.app.database.entities.ScheduleWithDays
import com.bsilent.app.other.Constants
import com.bsilent.app.services.ScheduleService
import java.util.*

object AlarmUtils {

    fun startAlarm(context: Context,schedule: Schedule){
        val time = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY,schedule.hour)
            set(Calendar.MINUTE,schedule.min)
        }.timeInMillis

        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).setRepeating(
            AlarmManager.RTC_WAKEUP,
            time,
            AlarmManager.INTERVAL_DAY,
            createSchedulePendingIntent(context, schedule)
        )
    }

    fun cancelAlarm(context: Context,schedule: Schedule){
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            .cancel(createSchedulePendingIntent(context, schedule))
    }

    private fun createSchedulePendingIntent(
        context: Context,
        schedule: Schedule,
    ): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            schedule.id!!.toInt(),
            createScheduleIntent(context, schedule),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun createScheduleIntent(context: Context, schedule: Schedule) =
        Intent(context, ScheduleReceiver::class.java)
            .also {
                it.action = Constants.START_SCHEDULE_SERVICE_ACTION
                it.putExtra("schedule_id", schedule.id)
            }

}