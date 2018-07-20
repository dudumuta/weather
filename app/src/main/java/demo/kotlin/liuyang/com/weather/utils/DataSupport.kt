package demo.kotlin.liuyang.com.weather.utils

import demo.kotlin.liuyang.com.weather.model.City
import demo.kotlin.liuyang.com.weather.model.Country
import demo.kotlin.liuyang.com.weather.model.Province
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

/**
 * 网络请求
 * Created by ly on 18/7/16.
 */
object DataSupport {

    /**
     * 从管道中读取数据并转换为字节数组
     */
    private fun getBytesByInputStream(content: InputStream): ByteArray {
        var bytes: ByteArray? = null
        val bis = BufferedInputStream(content)
        val baos = ByteArrayOutputStream()
        val bos = BufferedOutputStream(baos)
        val buffer = ByteArray(1024 * 8)
        var length = 0

        try {
            while (true) {
                length = bis.read(buffer)
                if (length < 0) {
                    break
                }
                bos.write(buffer, 0, length)
            }
            bos.flush()
            bytes = baos.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                bis.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return bytes!!
    }

    /**
     * 从服务端获取数据
     */
    private fun getServerContent(urlStr: String): String {
        var url = URL(urlStr)
        var conn = url.openConnection() as HttpURLConnection
        conn.useCaches = false

        val content = conn.inputStream
        var responseBody = getBytesByInputStream(content)
        var str = String(responseBody, Charset.forName("utf-8"))
        return str
    }

    /**
     * 获取省列表
     */
    fun getProvinces(provinces : (List<Province>) -> Unit) {
        Thread() {
            var content = getServerContent("https://geekori.com/api/china")
            var provinces = Utility.handleProvinceResponse(content)
            provinces(provinces)
        }.start()
    }

    /**
     * 获取市列表
     */
    fun getCities(provinceCode : String, cities : (List<City>) -> Unit) {
        Thread() {
            var content = getServerContent("https://geekori.com/api/china/$provinceCode")
            var cities = Utility.handleCityResponse(content, provinceCode)
            cities(cities)
        }.start()
    }

    /**
     * 获取县列表
     */
    fun getCountries(provinceCode : String, cityCode : String, countries : (List<Country>) -> Unit) {
        Thread {
            var content = getServerContent("https://geekori.com/api/china/$provinceCode/$cityCode")
            var countries = Utility.handleCountryResponse(content, cityCode)
            countries(countries)
        }.start()
    }
}