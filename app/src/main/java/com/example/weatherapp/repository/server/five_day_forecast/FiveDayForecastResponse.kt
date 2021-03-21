package com.example.weatherapp.repository.server.five_day_forecast


import com.example.weatherapp.repository.server.today_forecast.Weather
import com.example.weatherapp.repository.server.today_forecast.WeatherDetail
import com.example.weatherapp.repository.server.today_forecast.Wind
import com.google.gson.annotations.SerializedName
import java.util.*

data class FiveDayForecastModel(
    @SerializedName("cnt")
    val cnt: Int,
    @SerializedName("cod")
    val cod: String,
    @SerializedName("list")
    val list: List<Forecast>,
    @SerializedName("message")
    val message: Int
)


data class Forecast (
    @SerializedName("dt")
    val date: Int,
    @SerializedName("dt_txt")
    val dtTxt: Date,
    @SerializedName("main")
    val weatherDetail: WeatherDetail,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("wind")
    val wind: Wind
)
