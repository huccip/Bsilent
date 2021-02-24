package com.bsilent.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bsilent.app.database.dao.ScheduleDao

class ScheduleViewModel(
    private var scheduleDao: ScheduleDao,
    app:Application
) : AndroidViewModel(app) {

    var schedules = scheduleDao.getAll()

}