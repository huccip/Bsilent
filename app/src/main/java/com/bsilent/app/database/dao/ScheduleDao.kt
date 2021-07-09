package com.bsilent.app.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bsilent.app.database.entities.Day
import com.bsilent.app.database.entities.Schedule
import com.bsilent.app.database.entities.ScheduleWithDays

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg schedule: Schedule):List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDays(vararg days:Day)

    @Query("SELECT * FROM schedule WHERE id = :key")
    fun getSchedule(key:Long):Schedule

    @Delete
    fun delete(schedule: Schedule)

    @Query("DELETE FROM schedule WHERE id = :id")
    fun deleteById(vararg id: Long)


    @Update
    fun update(schedule: Schedule)

    @Transaction
    @Query("SELECT * FROM schedule")
    fun getAll():LiveData<List<ScheduleWithDays>>

    @Transaction
    @Query("SELECT * FROM schedule WHERE id = :key")
    fun get(key:Long):ScheduleWithDays

    @Query("DELETE FROM day WHERE scheduleId = :id")
    fun removeDays(vararg id: Long)


}