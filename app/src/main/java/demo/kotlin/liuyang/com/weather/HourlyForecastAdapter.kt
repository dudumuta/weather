package demo.kotlin.liuyang.com.weather

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import demo.kotlin.liuyang.com.weather.model.HourlyForecast


/**
 * Created by ly on 18/7/18.
 */
class HourlyForecastAdapter(context: Context, dataset: List<HourlyForecast>) : RecyclerView.Adapter<HourlyForecastHolder>() {
    private var mContext: Context? = null
    private var mDataset: List<HourlyForecast>? = null
    private var mInflater: LayoutInflater? = null

    init {
        this.mContext = context
        this.mDataset = dataset
        mInflater = LayoutInflater.from (context)
    }

    override fun onBindViewHolder(holder: HourlyForecastHolder?, position: Int) {
        holder?.time?.text = (mDataset?.get(position)?.time?.split(" "))?.get(1)
        Glide.with(mContext).load("https://cdn.heweather.com/cond_icon/${mDataset?.get(position)?.cond_code}.png").into(holder?.icon)
        holder?.tmp?.text = mDataset?.get(position)?.tmp + "℃"
    }

    override fun getItemCount(): Int {
        return mDataset?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): HourlyForecastHolder {
        val view = mInflater?.inflate(R.layout.weather_item, parent, false)
        return HourlyForecastHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

}