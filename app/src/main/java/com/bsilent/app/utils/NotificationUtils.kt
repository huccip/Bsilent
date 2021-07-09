package com.bsilent.app.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bsilent.app.R
import com.bsilent.app.database.entities.Place
import com.bsilent.app.database.entities.Schedule
import com.bsilent.app.other.Constants
import com.bsilent.app.other.Constants.NOTIFICATION_CHANNEL_ID
import com.bsilent.app.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.bsilent.app.services.PlaceService
import com.bsilent.app.services.ScheduleService
import com.bsilent.app.utils.TimeUtils.timeToString
import java.util.*

object NotificationUtils {

    fun createNotification(context: Context, schedule: Schedule): Notification {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotifChannel(notificationManager)
        }
        val cancelPI = PendingIntent.getService(
            context,
            12,
            Intent(context, ScheduleService::class.java).also {
                it.action = Constants.CANCEL_SCHEDULE_SERVICE_ACTION
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val mode =
            if (schedule.silent)
                context.getString(R.string.mode_silent)
            else
                context.getString(R.string.mode_vibration)
        val startTime = timeToString(schedule.hour, schedule.min)
        val endTime = timeToString(schedule.hour, schedule.min, schedule.duration)
        val name = if (schedule.name.isEmpty())
            context.getString(R.string.no_name)
        else
            schedule.name.capitalize(Locale.getDefault())
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentTitle("$name - Bsilent")
            .setContentText("$mode : $startTime -> $endTime")
            .addAction(R.drawable.ic_check, context.getString(R.string.cancel), cancelPI)
            .build()
    }


    fun createNotification(context: Context, place: Place): Notification {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotifChannel(notificationManager)
        }
        val cancelPI = PendingIntent.getService(
            context,
            12,
            Intent(context, PlaceService::class.java).also {
                it.action = Constants.CANCEL_PLACE_SERVICE_ACTION
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val mode =
            if (place.silent)
                context.getString(R.string.mode_silent)
            else
                context.getString(R.string.mode_vibration)
        val name = if (place.name.isEmpty())
            context.getString(R.string.no_name)
        else
            place.name.capitalize(Locale.getDefault())

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentTitle("$name - Bsilent")
            .setContentText("$mode")
            .addAction(R.drawable.ic_check, context.getString(R.string.cancel), cancelPI)
            .build()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotifChannel(notificationManager: NotificationManager) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
    }


}