package com.bsilent.app.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bsilent.app.database.dao.PlacesDao
import com.bsilent.app.database.entities.Place
import com.bsilent.app.utils.GeofenceUtils.addGeofence
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddLocationViewModel @Inject constructor(
    private val placesDao: PlacesDao,
    private val filesDir: File
) : ViewModel() {

    private var _isInserted = MutableLiveData(false)
    val isInserted: LiveData<Boolean> get() = _isInserted

    var latlng = MutableLiveData(LatLng(0.0, 0.0))
    var radius = MutableLiveData(70)
    var isSilent = MutableLiveData(true)
    var placeName = MutableLiveData("")

    init {
        _isInserted.value = false
    }

    fun savePlace(context: Context, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {

            val img = saveImage(bitmap)
            val place = Place(
                lat = latlng.value?.latitude ?: 0.0,
                lng = latlng.value?.longitude ?: 0.0,
                radius = radius.value ?: 70,
                silent = isSilent.value ?: true,
                img = img,
                name = placeName.value ?: "Place",
                isEnabled = true
            )
            placesDao.insert(place)

            withContext(Dispatchers.Main) {
                addGeofence(context,place)
                _isInserted.value = true
            }

        }

    }

    private fun saveImage(bitmap: Bitmap): String {

        val file = File(filesDir, "${UUID.randomUUID()}_${placeName.value}.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()
        return file.path
    }

}