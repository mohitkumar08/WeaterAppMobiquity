package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.core.AppObjectController

class WeatherApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AppObjectController.initLibrary(this)
    }
}