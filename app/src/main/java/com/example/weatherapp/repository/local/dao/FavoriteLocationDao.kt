package com.example.weatherapp.repository.local.dao

import androidx.room.*
import com.example.weatherapp.repository.local.entity.FavoriteLocation

@Dao
interface FavoriteLocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLocationInFavorite(loc: FavoriteLocation)

    @Query(value = "SELECT * FROM favorite_location")
    suspend fun getAllFavoriteLocations(): List<FavoriteLocation>
}