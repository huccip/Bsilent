package com.bsilent.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bsilent.app.database.dao.ScheduleDao
import com.bsilent.app.database.entities.Schedule
import kotlinx.coroutines.*

class ScheduleViewModel(
    private var scheduleDao: ScheduleDao,
    app:Application
) : AndroidViewModel(app) {

    var schedules = scheduleDao.getAll()

    var job = Job()
    var uiScope = CoroutineScope(job + Dispatchers.Main)
    fun insert(schedule:Schedule){
        uiScope.launch {
            withContext(Dispatchers.IO) {
                scheduleDao.insert(schedule)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}