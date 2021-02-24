package com.bsilent.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bsilent.app.database.dao.PlacesDao
import com.bsilent.app.database.dao.ScheduleDao
import com.bsilent.app.database.entities.Place

@Database(entities = [Place::class],version = 1,exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract val placesDao:PlacesDao
    abstract val scheduleDao:ScheduleDao

    companion object{
        @Volatile
        private var INSTANCE:AppDatabase? = null

        fun getInstance(context:Context):AppDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance==null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java,
                        "app_db"
                        )
                        .build()
                }
                INSTANCE = instance

                return instance
            }
        }
    }
}