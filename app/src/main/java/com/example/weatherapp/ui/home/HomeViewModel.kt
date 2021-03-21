package com.example.weatherapp.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.core.AppObjectController
import com.example.weatherapp.repository.local.entity.FavoriteLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val favoriteLocationList = MutableSharedFlow<List<FavoriteLocation>>()
    private val locationDao = AppObjectController.appDatabase.favoriteLocationDao()

    fun getFavoriteLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            favoriteLocationList.emit(locationDao.getAllFavoriteLocations())
        }
    }
    fun addLocationInFavorite(favoriteLocation: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            locationDao.addLocationInFavorite(favoriteLocation)
            favoriteLocationList.emit(locationDao.getAllFavoriteLocations())
        }
    }

}