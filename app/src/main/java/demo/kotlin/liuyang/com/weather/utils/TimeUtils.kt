package demo.kotlin.liuyang.com.weather.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ly on 18/7/19.
 */

object TimeUtils {
    /**
     * func:通过具体日期来获得星期几（中式）
     * @param date 标准日期
     * @return  星期几
     */
    fun getChineseWeekDay(date: String?): String {
        var weekTime = "星期"
        val format = SimpleDateFormat("yyyy-MM-dd")
        val c = Calendar.getInstance()
        try {

            c.time = format.parse(date)

        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        when (c.get(Calendar.DAY_OF_WEEK)) {
            1 ->

                weekTime += "日"
            2 ->

                weekTime += "一"
            3 ->

                weekTime += "二"
            4 ->

                weekTime += "三"
            5 ->

                weekTime += "四"
            6 ->

                weekTime += "五"
            7 ->

                weekTime += "六"

            else -> throw IllegalArgumentException("Illegal date format")
        }
        return weekTime

    }

    /**
     * func:通过具体日期来获得星期几（英式）
     * @param date 标准日期
     * @return  星期几
     */
    fun getEnglishWeekDay(date: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        val c = Calendar.getInstance()
        try {

            c.time = format.parse(date)

        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        when (c.get(Calendar.DAY_OF_WEEK)) {
            1 ->

                return "Sunday"
            2 ->

                return "Monday"
            3 ->

                return "Tuesday"
            4 ->

                return "Wednesday"
            5 ->

                return "Thursday"
            6 ->

                return "Friday"
            7 ->

                return "Saturday"

            else -> throw IllegalArgumentException("Illegal date format")
        }

    }
}
