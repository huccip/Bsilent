package com.bsilent.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.bsilent.app.database.dao.PlacesDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlacesViewModel(
    private val placesDao: PlacesDao,
    application: Application
) : AndroidViewModel(application) {


    val places = placesDao.getAll()

    private var _isEnabled = placesDao.isEnabled()
    var isEnabled = Transformations.map(_isEnabled) {
        var b = false
        for (i in it){
            if(i.isEnabled){
                b=true
                break
            }
        }
        b
    }


    fun disableAll(){
        viewModelScope.launch(Dispatchers.IO) {
            placesDao.setEnable(false)
        }
    }

    fun enableAll() {
         viewModelScope.launch(Dispatchers.IO) {
             placesDao.setEnable(true)
         }
    }
}