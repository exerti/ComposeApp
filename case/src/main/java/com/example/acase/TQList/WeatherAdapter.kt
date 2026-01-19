package com.example.acase.TQList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.acase.databinding.ItemWeatherCardBinding

class WeatherAdapter(
    private val items: List<WeatherItem>,
    private val onItemClick: (WeatherItem) -> Unit
) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    inner class WeatherViewHolder(private val binding: ItemWeatherCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WeatherItem) {
            binding.apply {
                tvCity.text = item.city
                tvTemperature.text = item.temperature
                tvWeather.text = item.weather
                tvHumidity.text = item.humidity
                tvWind.text = item.wind
                weatherIcon.setImageResource(item.weatherIconRes)

                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = ItemWeatherCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
