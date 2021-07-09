package com.bsilent.app.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import android.util.Log
import com.bsilent.app.database.entities.Place
import com.bsilent.app.database.entities.Schedule
import com.bsilent.app.other.Constants
import com.bsilent.app.utils.NotificationUtils
import com.google.android.gms.location.Geofence

class PlaceService: Service() {

    private var isFirst = true

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if(intent.action == Constants.CANCEL_PLACE_SERVICE_ACTION){
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            killService()
            return super.onStartCommand(intent, flags, startId)
        }
        val transition = intent.getIntExtra("transition",-1)
        if(transition == Geofence.GEOFENCE_TRANSITION_ENTER){
            startForegroundService(
                Place(
                name = "test"
            ))
            audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT

        }else if(transition == Geofence.GEOFENCE_TRANSITION_EXIT){
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            killService()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun killService() {
        isFirst = true
        stopForeground(true)
        stopSelf()
    }

    private fun startForegroundService(place: Place) {
        startForeground(
            Constants.NOTIFICATION_ID,
            NotificationUtils.createNotification(
                this,
                place
            )
        )
    }
}