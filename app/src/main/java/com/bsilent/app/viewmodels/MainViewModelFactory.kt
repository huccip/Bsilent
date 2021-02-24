package com.bsilent.app.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bsilent.app.database.dao.PlacesDao
import com.bsilent.app.database.dao.ScheduleDao

class MainViewModelFactory(
    private val database: PlacesDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlacesViewModel::class.java)) {
            return PlacesViewModel(database, application) as T
        }
        throw IllegalArgumentException("Unknow ViewModel class")
    }

}

class ScheduleModelFactory(
    private val database: ScheduleDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            return ScheduleViewModel(database, application) as T
        }
        throw IllegalArgumentException("Unknow ViewModel class")
    }

}
