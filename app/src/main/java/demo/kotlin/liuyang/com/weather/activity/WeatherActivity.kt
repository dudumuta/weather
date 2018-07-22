package demo.kotlin.liuyang.com.weather.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import demo.kotlin.liuyang.com.weather.R
import demo.kotlin.liuyang.com.weather.adapter.HourlyForecastAdapter
import demo.kotlin.liuyang.com.weather.adapter.MixInfoAdapter
import demo.kotlin.liuyang.com.weather.model.HourlyForecast
import demo.kotlin.liuyang.com.weather.model.Weather
import demo.kotlin.liuyang.com.weather.utils.HttpUtil
import demo.kotlin.liuyang.com.weather.utils.Util
import demo.kotlin.liuyang.com.weather.utils.Util.getStatusBarHeight
import demo.kotlin.liuyang.com.weather.utils.Utility
import kotlinx.android.synthetic.main.activity_weather.*
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

/**
 * Created by ly on 18/7/16.
 */
class WeatherActivity : AppCompatActivity() {
    var mHourlyForecastDataSet = ArrayList<HourlyForecast>()
    var mHourAdapter: HourlyForecastAdapter? = null
    var mMixAdapter: MixInfoAdapter? = null
    var mMixDataSet: ArrayList<Any> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
//        setStatusBarWidth()
        Util.fullScreen(this)
//        setStatusBarWidth()
        nav.setOnClickListener { drawer_layout.openDrawer(GravityCompat.START) }
        initHourForecast()
        initMixInfo()
        loadWeatherInfo(intent)
    }

    private fun initHourForecast() {
        weather_hour.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mHourAdapter = HourlyForecastAdapter(this, mHourlyForecastDataSet)
        weather_hour.adapter = mHourAdapter
    }

    private fun initMixInfo() {
        weather_day.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mMixAdapter = MixInfoAdapter(this, mMixDataSet)
        weather_day.adapter = mMixAdapter
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        loadWeatherInfo(intent)
    }

    private fun setStatusBarWidth() {
        val rootView = this.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        rootView.setPadding(0, Util.getStatusBarHeight(this), 0, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.window.statusBarColor = this.resources.getColor(R.color.transparent)
        } else {
            val decorView = this.window.decorView as ViewGroup
            val statusBar = View(this)
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(this))
            statusBar.setBackgroundColor(this.resources.getColor(R.color.transparent))
            decorView.addView(statusBar, lp)
        }
    }

    private fun loadWeatherInfo(intent: Intent?) {
        var weather = getSharedPreferences("WeatherInfo", Context.MODE_PRIVATE)
        val city = intent?.getStringExtra("city")
        if (!TextUtils.isEmpty(city)) {
            drawer_layout.closeDrawers()
            //天气请求
            requestWeather("weather", city) {
                showWeatherInfo(it)

                val editor = weather.edit()
                editor.putString("weather", it.toString())
                editor.apply()
            }

            //空气质量请求
            requestWeather("air", city) {
                showAirInfo(it)

                val editor = weather.edit()
                editor.putString("air", it.toString())
                editor.apply()
            }

            //小时预报
            requestForecast(city) {
                showHourInfo(it)
            }

            requestDailyForecast(city) {
                mMixDataSet.clear()
                var dailyForecastList = it.daily_forecast
                requestLifeStyle(city) {
                    var lifeStyle = it.lifestyle
                    requestSunInfo(city) {
                        dailyForecastList?.let { mMixDataSet.addAll(it) }
                        it.sunInfo?.let { mMixDataSet.add(it[0]) }
                        lifeStyle?.let { mMixDataSet.addAll(it) }
                        mMixAdapter?.notifyDataSetChanged()
                    }
                }

            }
        }
    }

    private fun showWeatherInfo(weather: Weather) {
        weather_now.text = weather.now?.tmp + "℃"
        weather_title.text = weather.basic?.country
        weather_update_time.text = weather.update?.updateTime?.let { it.split(" ")[1] }
        Glide.with(this@WeatherActivity).load("https://cdn.heweather.com/cond_icon/${weather.now?.cond_code}.png").into(weather_icon)
    }

    private fun showAirInfo(weather: Weather) {
        var airStr = if (TextUtils.isEmpty(weather.air?.qlty)) "" else "空气质量: " + weather.air?.qlty
        if (TextUtils.isEmpty(airStr)) {
            weather_air.visibility = View.GONE
        } else {
            weather_air.visibility = View.VISIBLE
            weather_air.text = airStr
            weather_pm.text = "PM2.5: " + weather.air?.pm25
        }
    }

    private fun showHourInfo(weather: Weather) {
        if (weather.hourly == null || weather.hourly?.size == 0) {
            return
        }
        mHourlyForecastDataSet.clear()
        weather.hourly?.let { mHourlyForecastDataSet.addAll(it) }
        mHourAdapter?.notifyDataSetChanged()
    }

    /**
     * 获取实况天气
     */
    private fun requestWeather(apiName: String, cityName: String?, function: (weather: Weather) -> Unit) {
        val weatherUrl = "https://free-api.heweather.com/s6/$apiName/now?location=$cityName&key=dd72a6d6914940beaf906f9c8a3f2595"
        HttpUtil.sendOkHttpRequest(weatherUrl, object : okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                runOnUiThread {
                    Toast.makeText(this@WeatherActivity, "请求失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                val responseText = response?.body()?.string()
                val weather = Utility.handleWeatherResponse(responseText)

                if ("ok" != weather.status) {
                    Toast.makeText(this@WeatherActivity, "城市名称有误", Toast.LENGTH_SHORT).show()
                    return
                }

                runOnUiThread {
                    function(weather)
                }
            }
        })
    }

    /**
     * 获取小时级预报
     */
    private fun requestForecast(cityName: String?, function: (weather: Weather) -> Unit) {
        var url = "https://free-api.heweather.com/s6/weather/hourly?location=$cityName&key=dd72a6d6914940beaf906f9c8a3f2595"
        HttpUtil.sendOkHttpRequest(url, object : okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                runOnUiThread {
                    Toast.makeText(this@WeatherActivity, "请求失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                val responseText = response?.body()?.string()
                val weather = Utility.handleWeatherResponse(responseText)

                if ("ok" != weather.status) {
                    Toast.makeText(this@WeatherActivity, "城市名称有误", Toast.LENGTH_SHORT).show()
                    return
                }

                runOnUiThread {
                    function(weather)
                }
            }
        })
    }

    /**
     * 获取天级预报
     */
    private fun requestDailyForecast(cityName: String?, function: (weather: Weather) -> Unit) {
        val url = "https://free-api.heweather.com/s6/weather/forecast?location=$cityName&key=dd72a6d6914940beaf906f9c8a3f2595"
        HttpUtil.sendOkHttpRequest(url, object : okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                runOnUiThread {
                    Toast.makeText(this@WeatherActivity, "请求失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                val responseText = response?.body()?.string()
                val weather = Utility.handleWeatherResponse(responseText)

                if ("ok" != weather.status) {
                    Toast.makeText(this@WeatherActivity, "城市名称有误", Toast.LENGTH_SHORT).show()
                    return
                }

                runOnUiThread {
                    function(weather)
                }
            }
        })
    }

    private fun requestLifeStyle(cityName: String?, function: (weather: Weather) -> Unit) {
        val url = "https://free-api.heweather.com/s6/weather/lifestyle?location=$cityName&key=dd72a6d6914940beaf906f9c8a3f2595"
        HttpUtil.sendOkHttpRequest(url, object : okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                runOnUiThread {
                    Toast.makeText(this@WeatherActivity, "请求失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                val responseText = response?.body()?.string()
                val weather = Utility.handleWeatherResponse(responseText)

                if ("ok" != weather.status) {
                    Toast.makeText(this@WeatherActivity, "城市名称有误", Toast.LENGTH_SHORT).show()
                    return
                }

                runOnUiThread {
                    function(weather)
                }
            }
        })
    }

    private fun requestSunInfo(cityName: String?, function: (weather: Weather) -> Unit) {
        val url = "https://free-api.heweather.com/s6/solar/sunrise-sunset?location=$cityName&key=dd72a6d6914940beaf906f9c8a3f2595"
        HttpUtil.sendOkHttpRequest(url, object : okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                runOnUiThread {
                    Toast.makeText(this@WeatherActivity, "请求失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                val responseText = response?.body()?.string()
                val weather = Utility.handleWeatherResponse(responseText)

                if ("ok" != weather.status) {
                    Toast.makeText(this@WeatherActivity, "城市名称有误", Toast.LENGTH_SHORT).show()
                    return
                }

                runOnUiThread {
                    function(weather)
                }
            }
        })
    }
}