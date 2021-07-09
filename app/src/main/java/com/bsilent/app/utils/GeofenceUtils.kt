package com.bsilent.app.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.bsilent.app.broadcasts.PlaceReceiver
import com.bsilent.app.database.entities.Place
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

object GeofenceUtils {

    fun addGeofence(context: Context, place: Place){
        val geoClient = LocationServices.getGeofencingClient(context)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
        try {
            geoClient.addGeofences(
                getGeofencingRequest(place),
                getGeofencePendingIntent(context,place)
            ).addOnFailureListener {
                Log.e("geofence"," error: ${it.message} ${it.localizedMessage}")
            }.addOnSuccessListener {
                Log.d("geofence","geofence added successfully")
            }
        }catch (e: Exception){
            Log.e("geofence","exception : ${e.message} ${e.localizedMessage}")
        }


    }

    fun addAllGeofences(context: Context, places: List<Place>){
        places.forEach {
            addGeofence(context,it)
        }
    }
    fun removeGeofence(context: Context, place: Place){
        val geoClient = LocationServices.getGeofencingClient(context)

        geoClient.removeGeofences(
            getGeofencePendingIntent(context,place)
        )
    }
    fun removeAllGeofences(context: Context, places: List<Place>){
        places.forEach {
            removeGeofence(context,it)
        }
    }
    private fun getGeofencingRequest(place: Place): GeofencingRequest =
        GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(listOf(createGeofence(place)))
        }.build()


    private fun getGeofencePendingIntent(context: Context, place: Place): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            place.id!!.toInt(),
            Intent(context, PlaceReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun createGeofence(place: Place): Geofence = Geofence.Builder()
        .setCircularRegion(
            place.lat,
            place.lng,
            place.radius.toFloat()
        )
        .setExpirationDuration(1000 * 60 * 60 * 1 * 1L)
        .setRequestId(place.id!!.toString())
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
        .build()


}