package com.example.weatherapp.ui.city

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.example.weatherapp.R
import com.example.weatherapp.core.DATE_FORMAT_TOP
import com.example.weatherapp.core.DEGREE_CODE
import com.example.weatherapp.core.DETAIL_OF_LOCATION_OBJ
import com.example.weatherapp.core.MarginItemDecoration
import com.example.weatherapp.databinding.WeatherDetailActivityBinding
import com.example.weatherapp.repository.local.entity.FavoriteLocation
import kotlinx.coroutines.flow.collectLatest
import java.util.*

class WeatherDetailActivity : AppCompatActivity() {

    companion object {
        fun weatherDetailsActivity(activity: Activity, favoriteLocation: FavoriteLocation) {
            Intent(activity, WeatherDetailActivity::class.java).apply {
                putExtra(DETAIL_OF_LOCATION_OBJ, favoriteLocation)
            }.also {
                activity.startActivity(it)
            }
        }
    }


    private lateinit var binding: WeatherDetailActivityBinding

    private val viewModel: WeatherDetailViewModel by lazy {
        ViewModelProvider(this).get(WeatherDetailViewModel::class.java)
    }
    private val weatherForecastAdapter: WeatherForecastAdapter by lazy { WeatherForecastAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.weather_detail_activity)
        binding.handler = this
        val favLoc = intent.getParcelableExtra<FavoriteLocation>(DETAIL_OF_LOCATION_OBJ)
        if (favLoc == null) {
            this.finish()
            return
        }
        initRV()
        addObservable()
        viewModel.getTodayForecast(favLoc.latitude, favLoc.longitude)
    }

    private fun initRV() {
        binding.rvForecast.apply {
            layoutManager = LinearLayoutManager(applicationContext, HORIZONTAL, false).apply {
                isSmoothScrollbarEnabled = true
            }
            itemAnimator = null
            addItemDecoration(MarginItemDecoration(resources.getDimension(R.dimen._4sdp).toInt()))
            adapter = weatherForecastAdapter
        }
    }

    private fun addObservable() {
        lifecycleScope.launchWhenStarted {
            viewModel.todayForecastFlow.collectLatest {
                with(binding) {
                    tvCity.text = it.name
                    tvDate.text = DATE_FORMAT_TOP.format(Date()).toUpperCase(Locale.getDefault())

                    country.text=it.sys.country
                    tvTemperature.text = it.weatherDetail.temp.toString().plus(DEGREE_CODE)
                    weatherMain.text = it.weather[0].main
                    weatherDetail.text = it.weather[0].description

                    tempFeels.text = it.weatherDetail.feelsLike.toString().plus(DEGREE_CODE)
                    tempMin.text = it.weatherDetail.tempMin.toString().plus(DEGREE_CODE)
                    tempMax.text = it.weatherDetail.tempMax.toString().plus(DEGREE_CODE)

                    windSpeed.text = it.wind.speed.toString().plus(" km/h")

                    tvPressure.text = it.weatherDetail.pressure.toString().plus(" kPa")

                    tvHumidity.text = it.weatherDetail.humidity.toString().plus(" %")

                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.fiveDayForecastFlow.collectLatest {
                weatherForecastAdapter.addItems(it)
            }
        }
    }
}