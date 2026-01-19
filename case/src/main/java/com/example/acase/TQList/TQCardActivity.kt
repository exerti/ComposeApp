package com.example.acase.TQList

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acase.R

class TQCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tqcard2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val recyclerView: androidx.recyclerview.widget.RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val weatherItems = generateWeatherData()
        val adapter = WeatherAdapter(weatherItems) { item ->
            Toast.makeText(this, "选择: ${item.city}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter
    }

    private fun generateWeatherData(): List<WeatherItem> {
        return listOf(
            WeatherItem(
                city = "北京",
                temperature = "26°C",
                weather = "晴",
                humidity = "45%",
                wind = "东北风3级"
            ),
            WeatherItem(
                city = "上海",
                temperature = "28°C",
                weather = "多云",
                humidity = "65%",
                wind = "东南风2级"
            ),
            WeatherItem(
                city = "广州",
                temperature = "32°C",
                weather = "雷阵雨",
                humidity = "80%",
                wind = "南风4级"
            ),
            WeatherItem(
                city = "深圳",
                temperature = "31°C",
                weather = "小雨",
                humidity = "78%",
                wind = "东南风3级"
            ),
            WeatherItem(
                city = "杭州",
                temperature = "27°C",
                weather = "阴",
                humidity = "60%",
                wind = "东风2级"
            ),
            WeatherItem(
                city = "成都",
                temperature = "25°C",
                weather = "多云",
                humidity = "70%",
                wind = "无风"
            )
        )
    }
}
