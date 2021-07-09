package com.bsilent.app.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bsilent.app.services.PlaceService
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class PlaceReceiver: BroadcastReceiver() {

    override fun onReceive(c: Context, intent: Intent) {

        val event = GeofencingEvent.fromIntent(intent)

        if(event.hasError()){
            val err = GeofenceStatusCodes.getStatusCodeString(event.errorCode)
            Log.e(PlaceReceiver::class.simpleName,"Error: $err")
            return
        }
        val transition = event.geofenceTransition

        c.startService(
            Intent(
                c,
                PlaceService::class.java
            ).also {
                it.putExtra("transition",transition)
            }
        )
    }

}