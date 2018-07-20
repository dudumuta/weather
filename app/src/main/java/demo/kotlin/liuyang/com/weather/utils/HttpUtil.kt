package demo.kotlin.liuyang.com.weather.utils

import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by ly on 18/7/17.
 */
object HttpUtil {
    fun sendOkHttpRequest(address : String, callback : okhttp3.Callback) {
        val client = OkHttpClient()
        val request = Request.Builder().url(address).build()
        client.newCall(request).enqueue(callback)
    }
}