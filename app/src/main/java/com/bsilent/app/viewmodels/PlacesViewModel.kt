package com.bsilent.app.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.bsilent.app.database.dao.PlacesDao
import com.bsilent.app.database.entities.Place
import com.bsilent.app.database.entities.ScheduleWithDays
import com.bsilent.app.utils.GeofenceUtils
import com.bsilent.app.utils.GeofenceUtils.addAllGeofences
import com.bsilent.app.utils.GeofenceUtils.addGeofence
import com.bsilent.app.utils.GeofenceUtils.removeAllGeofences
import com.bsilent.app.utils.GeofenceUtils.removeGeofence
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val placesDao: PlacesDao,
    private val filesDir:File
) : ViewModel() {


    val places = placesDao.getAll()

    val selected = MutableLiveData(mutableListOf<Place>())

    private val _isEnabled = placesDao.isEnabled()
    var isEnabled = _isEnabled.map {
        var b = false
        for (i in it) {
            b = true
            break
        }
        b
    }

    val dialogPlaceOld = MutableLiveData<Place?>(null)
    val dialogPlace = MutableLiveData<Place?>(null)
    val showDialogPlace = MutableLiveData(false)

    fun disableAll(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            placesDao.setAllEnable(false)
            places.value?.let{
                removeAllGeofences(context,it)
            }

        }
    }

    fun enableAll(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            placesDao.setAllEnable(true)
            places.value?.let{
                addAllGeofences(context,it)
            }
        }
    }

    fun setEnable(context: Context,place: Place, e: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            place.isEnabled = e
            placesDao.update(place)
            if(e){
                addGeofence(context,place)
            }else{
                removeGeofence(context,place)
            }
        }
    }

    fun updatePlace(context: Context, bmp: Bitmap) {
        viewModelScope.launch(Dispatchers.IO){
            val img = saveImage(bmp)
            dialogPlaceOld.value?.let {
                removeGeofence(context,it)
            }
            dialogPlace.value?.let {
                it.img=img
                placesDao.update(it)
                addGeofence(context,it)
            }

        }
    }
    private fun saveImage(bitmap: Bitmap): String {
        dialogPlace.value?.let {
            val oldFile = File(it.img)
            oldFile.delete()
        }

        val file = File(filesDir, "${UUID.randomUUID()}_${dialogPlace.value?.name}.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()
        return file.path
    }

    fun delete(context: Context, place: Place?) {
        place?.let {
            viewModelScope.launch(Dispatchers.IO) {
                File(it.img).deleteOnExit()
                removeGeofence(context,it)
                placesDao.delete(it)
            }

        }
    }

    fun addOrRemoveSelected(place: Place) {
        selected.value?.let{
            if(it.contains(place)){
                it.remove(place)
            }else {
                selected.value?.add(place)
            }
        }
    }

    fun deleteSelected(context: Context) {
        viewModelScope.launch(Dispatchers.IO){
            selected.value?.let {
                if(it.isEmpty())
                    return@let
                placesDao.delete(*it.toTypedArray())

//                it.forEach { p->
//                    removeGeofence(context,p)
//                }
                selected.value = mutableListOf()
            }

        }
    }
}