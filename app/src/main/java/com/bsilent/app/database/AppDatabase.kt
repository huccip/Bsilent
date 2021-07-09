package com.bsilent.app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bsilent.app.database.dao.PlacesDao
import com.bsilent.app.database.dao.ScheduleDao
import com.bsilent.app.database.entities.Day
import com.bsilent.app.database.entities.Place
import com.bsilent.app.database.entities.Schedule

@Database(entities = [Place::class, Schedule::class, Day::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract val placesDao: PlacesDao
    abstract val scheduleDao: ScheduleDao

}