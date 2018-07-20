package demo.kotlin.liuyang.com.weather.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**
 * 天气类
 * Created by ly on 18/7/16.
 */
class Weather {
    var status: String? = null
    var basic: Basic? = null
    var now: Now? = null
    var update: Update? = null
    var hourly: List<HourlyForecast>? = null
    @SerializedName("air_now_city")
    var air: AirQuality? = null
    var daily_forecast: List<DailyForecast>? = null
    var lifestyle: List<LifeStyle>? = null

    inner class Update {
        @SerializedName("loc")
        var updateTime: String? = null
    }

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}