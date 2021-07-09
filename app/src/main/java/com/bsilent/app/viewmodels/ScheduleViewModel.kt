package com.bsilent.app.viewmodels

import android.content.Context
import androidx.lifecycle.*
import com.bsilent.app.database.dao.ScheduleDao
import com.bsilent.app.database.entities.ScheduleWithDays
import com.bsilent.app.utils.AlarmUtils
import com.bsilent.app.utils.AlarmUtils.cancelAlarm
import com.bsilent.app.utils.AlarmUtils.startAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private var scheduleDao: ScheduleDao
) : ViewModel() {

    var schedules = scheduleDao.getAll()

    var selected = MutableLiveData<MutableList<Long>>(mutableListOf())


    fun insert(schedule: ScheduleWithDays, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val id = scheduleDao.insert(schedule.schedule)
                schedule.schedule.id = id[0]

                schedule.days.forEach {
                    it.scheduleId = id[0]
                    scheduleDao.insertDays(it)
                }
                startAlarm(context, schedule.schedule)
            }
        }
    }

    fun updateEnable(schedule: ScheduleWithDays, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleDao.update(schedule.schedule)
            if (schedule.schedule.isEnabled) {
                startAlarm(context, schedule.schedule)
            } else {
                cancelAlarm(context, schedule.schedule)
            }
        }
    }

    fun update(schedule: ScheduleWithDays, old: ScheduleWithDays, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleDao.update(schedule.schedule.apply { isEnabled = true })
            schedule.schedule.id?.let {
                scheduleDao.removeDays(it)
            }
            cancelAlarm(context, old.schedule)

            scheduleDao.insertDays(*schedule.days.toTypedArray())

            startAlarm(context, schedule.schedule)
        }
    }

    fun clearSelected(){
        selected.value = mutableListOf()
    }


    fun deleteSelected(context: Context){
        if(selected.value ==null || selected.value?.isEmpty()==true)
            return

        viewModelScope.launch(Dispatchers.IO){
            selected.value?.let {

                for(s in it){
                    val sched = scheduleDao.getSchedule(s)
                    cancelAlarm(context, sched)
                }

                scheduleDao.deleteById(*it.toLongArray())
                scheduleDao.removeDays(*it.toLongArray())

                selected.value = mutableListOf()
            }

        }
    }

    fun addOrRemoveSelected(schedule: ScheduleWithDays) {

        selected.value?.let{
            if(it.contains(schedule.schedule.id)){
                it.remove(schedule.schedule.id)
            }else {
                selected.value?.add(schedule.schedule.id!!)
            }
        }
    }
}