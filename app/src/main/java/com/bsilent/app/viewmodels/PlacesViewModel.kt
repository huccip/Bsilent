package com.bsilent.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bsilent.app.database.dao.PlacesDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class PlacesViewModel(
    private val placesDao: PlacesDao,
    private val app: Application
) : AndroidViewModel(app) {

    private val viewModelJob = Job()

    val places = placesDao.getAll()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}