package com.example.weatherapp.repository.local

import android.content.Context
import androidx.room.*
import com.example.weatherapp.repository.local.dao.FavoriteLocationDao
import com.example.weatherapp.repository.local.entity.FavoriteLocation
import java.util.Date

const val DATABASE_NAME = "weather.db"

@Database(
    entities = [FavoriteLocation::class ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    DateConverter::class,
)
abstract class AppDatabase : RoomDatabase() {

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase? {
            if (null == INSTANCE) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, DATABASE_NAME
                        ).build()
                    }
                }
            }
            return INSTANCE
        }

    }
    abstract fun favoriteLocationDao(): FavoriteLocationDao
}

class DateConverter {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return if (dateLong == null) null else Date(dateLong)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return (date?.time)
    }
}
