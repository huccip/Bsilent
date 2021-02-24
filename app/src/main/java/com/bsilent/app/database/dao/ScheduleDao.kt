package com.bsilent.app.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bsilent.app.database.entities.Schedule

@Dao
interface ScheduleDao {

    @Insert
    fun insert(schedule: Schedule)

    @Delete
    fun delete(schedule: Schedule)

    @Update
    fun update(schedule: Schedule)

    @Query("SELECT * FROM Schedule")
    fun getAll():LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE id = :key")
    fun get(key:Long):LiveData<List<Schedule>>

}