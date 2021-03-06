package demo.kotlin.liuyang.com.weather.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import demo.kotlin.liuyang.com.weather.R
import demo.kotlin.liuyang.com.weather.model.DailyForecast
import demo.kotlin.liuyang.com.weather.model.LifeStyle
import demo.kotlin.liuyang.com.weather.model.SunInfo
import demo.kotlin.liuyang.com.weather.utils.TimeUtils

/**
 * 天级预报、生活指数及日出
 * Created by ly on 18/7/19.
 */
class MixInfoAdapter(context: Context, dataSet: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mContext: Context? = null
    private var mDataset: List<Any>? = null
    private var mInflater: LayoutInflater? = null

    init {
        mContext = context
        mDataset = dataSet
        mInflater = LayoutInflater.from(context)
    }

    override fun getItemCount(): Int {
        return mDataset?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is DayWeatherHolder -> {
                if (mDataset?.get(position) is DailyForecast) {
                    holder.week?.text = TimeUtils.getChineseWeekDay((mDataset?.get(position) as DailyForecast).date)
                    holder.min?.text = (mDataset?.get(position) as DailyForecast).tmp_min
                    holder.max?.text = (mDataset?.get(position) as DailyForecast).tmp_max
                    Glide.with(mContext).load("https://cdn.heweather.com/cond_icon/${(mDataset?.get(position) as DailyForecast).cond_code_d}.png").into(holder.icon)
                }
            }
            is SunRiseSet -> {
                if (mDataset?.get(position) is SunInfo) {
                    holder.rise?.text = (mDataset?.get(position) as SunInfo).sr
                    holder.set?.text = (mDataset?.get(position) as SunInfo).ss
                }
            }
            is LifeStyleHolder -> {
                if (mDataset?.get(position) is LifeStyle) {
                    var title = when ((mDataset?.get(position) as LifeStyle).type) {
                        "comf" -> "舒适度指数"
                        "cw" -> "洗车指数"
                        "drsg" -> "穿衣指数"
                        "flu" -> "感冒指数"
                        "sport" -> "运动指数"
                        "trav" -> "旅游指数"
                        "uv" -> "紫外线指数"
                        "air" -> "空气污染扩散条件指数"
                        "ac" -> "空调开启指数"
                        "ag" -> "过敏指数"
                        else -> ""
                    }

                    if (!TextUtils.isEmpty(title)) {
                        holder.title?.text = title
                        holder.info?.text = (mDataset?.get(position) as LifeStyle).txt
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DayForecast -> DayWeatherHolder(mInflater?.inflate(R.layout.day_weather_item, parent, false))
            SunRiseSet -> SunRiseSet(mInflater?.inflate(R.layout.weather_item_x, parent, false))
            LifeStyle -> LifeStyleHolder(mInflater?.inflate(R.layout.weather_other_info, parent, false))
            else -> DayWeatherHolder(mInflater?.inflate(R.layout.day_weather_item, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (mDataset?.get(position)) {
            is DailyForecast -> DayForecast
            is SunInfo -> SunRiseSet
            is LifeStyle -> LifeStyle
            else -> DayForecast
        }
    }

    companion object {
        val DayForecast = 0
        val SunRiseSet = 1
        val LifeStyle = 2
    }

    inner class DayWeatherHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val week: TextView? = itemView?.findViewById(R.id.day_weather_week)
        val icon: ImageView? = itemView?.findViewById(R.id.day_weather_icon)
        val min: TextView? = itemView?.findViewById(R.id.day_weather_min)
        val max: TextView? = itemView?.findViewById(R.id.day_weather_max)
    }

    inner class SunRiseSet(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val rise: TextView? = itemView?.findViewById(R.id.sun_rise)
        val set: TextView? = itemView?.findViewById(R.id.sun_set)
    }

    inner class LifeStyleHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val title: TextView? = itemView?.findViewById(R.id.other_title)
        val info: TextView? = itemView?.findViewById(R.id.other_info)
    }
}