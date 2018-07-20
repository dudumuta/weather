package demo.kotlin.liuyang.com.weather.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import com.google.gson.Gson
import demo.kotlin.liuyang.com.weather.R
import demo.kotlin.liuyang.com.weather.model.Weather

/**
 * Created by ly on 18/7/16.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val weather = getSharedPreferences("WeatherInfo", Context.MODE_PRIVATE)
        if (!TextUtils.isEmpty(weather.getString("weather", null)) && !TextUtils.isEmpty(weather.getString("air", null))) {
            val intent = Intent(this, WeatherActivity::class.java)
            val gson = Gson()
            val weather = gson.fromJson<Weather>(weather.getString("weather", null), Weather::class.java)
            intent.putExtra("city", weather.basic?.cityName)
            startActivity(intent)
            finish()
        }
    }
}