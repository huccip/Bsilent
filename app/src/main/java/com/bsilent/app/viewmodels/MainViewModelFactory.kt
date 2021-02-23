package com.bsilent.app.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bsilent.app.database.dao.PlacesDao
import java.lang.IllegalArgumentException

class MainViewModelFactory(
    private val database:PlacesDao,
    private val application: Application
) :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PlacesViewModel::class.java)){
        return PlacesViewModel(database,application) as T}
//        else if()
        throw IllegalArgumentException("Unknow ViewModel class")
    }

}