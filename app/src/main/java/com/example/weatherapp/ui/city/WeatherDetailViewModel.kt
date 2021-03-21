package com.example.weatherapp.ui.city

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.core.AppObjectController
import com.example.weatherapp.repository.local.entity.FavoriteLocation
import com.example.weatherapp.repository.server.five_day_forecast.Forecast
import com.example.weatherapp.repository.server.today_forecast.TodayForecastModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class WeatherDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val networkService = AppObjectController.weatherNetworkService
    val todayForecastFlow = MutableSharedFlow<TodayForecastModel>()
    val fiveDayForecastFlow = MutableSharedFlow<List<Forecast>>()

    fun getTodayForecast(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resp = networkService.getTodayWeather(latitude, longitude)
                todayForecastFlow.emit(resp)
                getFiveDayForecast(latitude,longitude)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
    private fun getFiveDayForecast(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resp = networkService.getFiveDayWeather(latitude, longitude)
                fiveDayForecastFlow.emit(resp.list)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

}