package com.example.acase.TQList

data class WeatherItem(
    val city: String,
    val temperature: String,
    val weather: String,
    val humidity: String,
    val wind: String,
    val weatherIconRes: Int = android.R.drawable.ic_menu_gallery
)
