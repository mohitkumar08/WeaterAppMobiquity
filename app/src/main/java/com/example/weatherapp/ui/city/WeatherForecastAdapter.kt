package com.example.weatherapp.ui.city

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.core.DATE_FORMAT
import com.example.weatherapp.core.DEGREE_CODE
import com.example.weatherapp.core.TIME_FORMAT
import com.example.weatherapp.core.setImage
import com.example.weatherapp.databinding.ForecastItemLayoutBinding
import com.example.weatherapp.repository.server.five_day_forecast.Forecast
import java.util.*

class WeatherForecastAdapter :
    RecyclerView.Adapter<WeatherForecastAdapter.WeatherDetailViewHolder>() {
    private var items: ArrayList<Forecast> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    fun addItems(newList: List<Forecast>) {
        if (newList.isEmpty()) {
            return
        }
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeatherForecastAdapter.WeatherDetailViewHolder {
        val binding =
            ForecastItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherDetailViewHolder(binding)
    }

    //
    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: WeatherDetailViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class WeatherDetailViewHolder(private val binding: ForecastItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(forecast: Forecast) {
            with(binding) {
                temperature.text = forecast.weatherDetail.temp.toString().plus(DEGREE_CODE)
                date.text = DATE_FORMAT.format(forecast.dtTxt)
                time.text = TIME_FORMAT.format(forecast.dtTxt)
                imageView.setImage(forecast.weather[0].getUrl())
            }
        }
    }
}
