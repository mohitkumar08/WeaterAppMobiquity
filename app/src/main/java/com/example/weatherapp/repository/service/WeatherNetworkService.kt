package com.example.weatherapp.repository.service

import com.example.weatherapp.repository.server.five_day_forecast.FiveDayForecastModel
import com.example.weatherapp.repository.server.today_forecast.TodayForecastModel
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherNetworkService {

    @GET("weather")
    suspend fun getTodayWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") unit: String = "metric"
    ): TodayForecastModel

    @GET("forecast")
    suspend fun getFiveDayWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") unit: String = "metric"
    ): FiveDayForecastModel
}
