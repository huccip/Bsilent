package com.bsilent.app.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bsilent.app.database.entities.Schedule

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(schedule: Schedule)

    @Delete
    fun delete(schedule: Schedule)

    @Update
    fun update(schedule: Schedule)

    @Query("SELECT * FROM schedule")
    fun getAll():LiveData<List<Schedule>>

    @Query("SELECT * FROM schedule WHERE id = :key")
    fun get(key:Long):LiveData<Schedule>

}