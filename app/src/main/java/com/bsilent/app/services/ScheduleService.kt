package com.bsilent.app.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import android.util.Log
import com.bsilent.app.database.dao.ScheduleDao
import com.bsilent.app.database.entities.Schedule
import com.bsilent.app.other.Constants
import com.bsilent.app.utils.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleService : Service() {

    @Inject
    lateinit var scheduleDao: ScheduleDao

    private var isFirst = true
    private val job = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO+ job)

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            Constants.START_SCHEDULE_SERVICE_ACTION -> {
                val scheduleId = intent.getLongExtra("schedule_id",-1)

                serviceScope.launch {
                    val schedule = scheduleDao.get(scheduleId)

                    val today = Calendar.getInstance().apply {
                        timeInMillis = System.currentTimeMillis()
                    }.get(Calendar.DAY_OF_WEEK) - 1
                    val days = schedule.days.map { it.day }
                    if(isFirst){
                        if(schedule.schedule.isEnabled && today in days) {
                            isFirst = false
                            startForegroundService(schedule.schedule)

                            val audioManager =
                                getSystemService(Context.AUDIO_SERVICE) as AudioManager

                            audioManager.ringerMode =
                                if (schedule.schedule.silent) AudioManager.RINGER_MODE_SILENT
                                else AudioManager.RINGER_MODE_VIBRATE

                            delay((schedule.schedule.duration * 60 * 60 * 1000).toLong())
                            withContext(Dispatchers.Main) {
                                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                                killService()
                            }
                        }else{
                            killService()
                        }
                    }
                }
            }
            Constants.CANCEL_SCHEDULE_SERVICE_ACTION -> {
                job.cancel()
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                killService()
            }
        }


        return super.onStartCommand(intent, flags, startId)
    }

    private fun killService() {
        isFirst = true
        stopForeground(true)
        stopSelf()
    }

    private fun startForegroundService(schedule: Schedule) {
        startForeground(
            Constants.NOTIFICATION_ID,
            NotificationUtils.createNotification(
                this,
                schedule
            )
        )
    }
}