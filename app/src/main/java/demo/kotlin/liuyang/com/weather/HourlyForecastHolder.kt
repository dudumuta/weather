package demo.kotlin.liuyang.com.weather

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by ly on 18/7/18.
 */
class HourlyForecastHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var time : TextView? = itemView?.findViewById(R.id.item_time)
    var icon : ImageView? = itemView?.findViewById(R.id.item_icon)
    var tmp : TextView? = itemView?.findViewById(R.id.item_tmp)
}