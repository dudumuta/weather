package demo.kotlin.liuyang.com.weather.model

import com.google.gson.annotations.SerializedName

/**
 * Created by ly on 18/7/16.
 */
class Basic {
    @SerializedName("location")
    var country: String? = null
    @SerializedName("parent_city")
    var cityName: String? = null
    @SerializedName("cid")
    var weatherId: String? = null
}