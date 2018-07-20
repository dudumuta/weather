package demo.kotlin.liuyang.com.weather.utils

import android.text.TextUtils
import com.google.gson.Gson
import demo.kotlin.liuyang.com.weather.model.City
import demo.kotlin.liuyang.com.weather.model.Country
import demo.kotlin.liuyang.com.weather.model.Province
import demo.kotlin.liuyang.com.weather.model.Weather
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * 数据解析
 * Created by ly on 18/7/16.
 */
object Utility {

    /**
     * 解析省类数据
     */
    fun handleProvinceResponse(response: String): List<Province> {
        var provinces = mutableListOf<Province>()
        if (!TextUtils.isEmpty(response)) {
            try {
                val allProvinces = JSONArray(response)
                (0 until allProvinces.length())
                        .map { allProvinces.getJSONObject(it) }
                        .mapTo(provinces) { Province(provinceName = it.getString("name"), provinceCode = it.getString("id")) }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        return provinces
    }

    /**
     * 解析市类数据
     */
    fun handleCityResponse(response: String, provinceCode: String): List<City> {
        var cities = mutableListOf<City>()
        if (!TextUtils.isEmpty(response)) {
            try {
                val allCities = JSONArray(response)
                (0 until allCities.length())
                        .map { allCities.getJSONObject(it) }
                        .mapTo(cities) { City(cityName = it.getString("name"), cityCode = it.getString("id"), provinceCode = provinceCode) }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return cities
    }

    /**
     * 解析县类数据
     */
    fun handleCountryResponse(response: String, cityCode: String): List<Country> {
        var countries = mutableListOf<Country>()
        if (!TextUtils.isEmpty(response)) {
            try {
                val allCountries = JSONArray(response)
                (0 until allCountries.length())
                        .map { allCountries.getJSONObject(it) }
                        .mapTo(countries) { Country(countryName = it.getString("name"), countryCode = it.getString("id"), cityCode = cityCode) }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return countries
    }

    /**
     * 解析天气类
     */
    fun handleWeatherResponse(response: String?): Weather {
        response ?: return Weather()
        val jsonObject = JSONObject(response)
        val jsonArray = JSONArray(jsonObject.getString("HeWeather6"))
        val result = jsonArray.getJSONObject(0).toString()
        val gson = Gson()
        return gson.fromJson(result, Weather::class.java)
    }
}