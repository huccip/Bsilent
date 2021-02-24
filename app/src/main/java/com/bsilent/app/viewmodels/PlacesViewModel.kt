package com.bsilent.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bsilent.app.database.dao.PlacesDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlacesViewModel(
    private val placesDao: PlacesDao,
    private val app: Application
) : AndroidViewModel(app) {

    private val viewModelJob = Job()

    val places = placesDao.getAll()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun isActivated():Boolean{
        places.value?.let {
            for(place in it){
                if(place.isEnabled) return true
            }
        }
        return false
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}