package com.example.weatherapp.repository.local.entity


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(
    tableName = "favorite_location")
class FavoriteLocation(
    @PrimaryKey()
    val cityName: String = "",
    @ColumnInfo
    val latitude: Double = 0.0,
    @ColumnInfo
    val longitude: Double = 0.0,
    @ColumnInfo
    val added: Date = Date()
) : Parcelable {


}