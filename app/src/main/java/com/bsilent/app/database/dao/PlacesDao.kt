package com.bsilent.app.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bsilent.app.database.entities.Place

@Dao
interface PlacesDao {

    @Insert
    fun insert(place:Place)

    @Update
    fun update(place: Place)

    @Delete
    fun delete(place: Place)

    @Query("SELECT * FROM place")
    fun getAll():LiveData<List<Place>>

    @Query("SELECT * FROM place WHERE id = :key")
    fun get(key:Long):LiveData<Place>

}