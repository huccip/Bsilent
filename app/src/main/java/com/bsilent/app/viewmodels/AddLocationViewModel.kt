package com.bsilent.app.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bsilent.app.database.dao.PlacesDao
import com.bsilent.app.database.entities.Place
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

class AddLocationViewModel(
    private val placesDao: PlacesDao,
    var app: Application
) : AndroidViewModel(app) {

    private var _isInserted = MutableLiveData<Boolean>(false)
    val isInserted: LiveData<Boolean> get() = _isInserted

    var latlng = MutableLiveData<LatLng>(LatLng(0.0, 0.0))
    var radius = MutableLiveData(70)
    var isSilent = MutableLiveData(true)
    var placeName = MutableLiveData("")


    init {
        _isInserted.value = false
    }

    fun savePlace(bitmap: Bitmap) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var img = saveImage(bitmap)
                placesDao.insert(
                    Place(
                        lat = latlng.value?.latitude ?: 0.0,
                        lng = latlng.value?.longitude ?: 0.0,
                        radius = radius.value?:70,
                        silent = isSilent.value?:true,
                        img = img,
                        name = placeName.value?:"Place"
                        )
                )
                _isInserted.value = true
            }

        }

    }

    private fun saveImage(bitmap: Bitmap) :String{

        val file = File(app.filesDir,"${UUID.randomUUID()}_${placeName.value}.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
        stream.flush()
        stream.close()
        return file.path
    }
}