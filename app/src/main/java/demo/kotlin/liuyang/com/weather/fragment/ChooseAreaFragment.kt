package demo.kotlin.liuyang.com.weather.fragment

import android.app.Fragment
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import demo.kotlin.liuyang.com.weather.R
import demo.kotlin.liuyang.com.weather.activity.WeatherActivity
import demo.kotlin.liuyang.com.weather.model.City
import demo.kotlin.liuyang.com.weather.model.Country
import demo.kotlin.liuyang.com.weather.model.Province
import demo.kotlin.liuyang.com.weather.utils.DataSupport
import kotlinx.android.synthetic.main.choose_area.*

/**
 * Created by ly on 18/7/16.
 */
class ChooseAreaFragment : Fragment() {
    private var progressDialog: ProgressDialog? = null

    private var list: ListView? = null

    private var adapter: ArrayAdapter<String>? = null
    private var handler = MyHandler()

    private val dataList = ArrayList<String>()//数据源

    private var provinceList: List<Province>? = null
    private var cityList: List<City>? = null
    private var countryList: List<Country>? = null

    private var selectedProvince: Province? = null
    private var selectedCity: City? = null
    private var selectedCountry: Country? = null

    private var currentLevel: Int = 0

    private var superCities = arrayListOf<String>("北京市", "天津市", "重庆市", "上海市", "香港特别行政区", "澳门特别行政区", "台湾")

    companion object {
        val LEVEL_PROVINCE = 0
        val LEVEL_CITY = 1
        val LEVEL_COUNTRY = 2
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.choose_area, container, false)
        list = view.findViewById(R.id.list_view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, dataList)
        list_view.adapter = adapter!!

        list_view.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            when (currentLevel) {
                LEVEL_PROVINCE -> {
                    selectedProvince = provinceList!![position]
                    if (superCities.contains(selectedProvince?.provinceName)) {
                        val intent = Intent(activity, WeatherActivity::class.java)
                        intent.putExtra("city", selectedProvince?.provinceName)
                        startActivity(intent)
                    } else {
                        queryCities()
                    }
                }
                LEVEL_CITY -> {
                    selectedCity = cityList!![position]
                    val intent = Intent(activity, WeatherActivity::class.java)
                    intent.putExtra("city", selectedCity?.cityName)
                    startActivity(intent)
                }
                LEVEL_COUNTRY -> {
                    //todo 目前数据暂未对县开放
                }
            }
        }

        back_btn.setOnClickListener {
            if (currentLevel == LEVEL_COUNTRY) {
                queryCities()
            } else if (currentLevel == LEVEL_CITY) {
                queryProvinces()
            }
        }

        queryProvinces()
    }

    class MyHandler : Handler() {
        override fun handleMessage(msg: Message?) {
            var activity = msg?.obj as ChooseAreaFragment
            when (msg.arg1) {
                LEVEL_PROVINCE -> {
                    if (activity.provinceList?.isNotEmpty() == true) {
                        activity.dataList.clear()
                        for (province in activity.provinceList!!) {
                            activity.dataList.add(province.provinceName)
                        }
                        activity.adapter?.notifyDataSetChanged()
                        activity.list!!.setSelection(0)
                        activity.currentLevel = LEVEL_PROVINCE
                    }
                }
                LEVEL_CITY -> {
                    if (activity.cityList?.isNotEmpty() == true) {
                        activity.dataList.clear()
                        for (city in activity.cityList!!) {
                            activity.dataList.add(city.cityName)
                        }
                        activity.adapter?.notifyDataSetChanged()
                        activity.list!!.setSelection(0)
                        activity.currentLevel = LEVEL_CITY
                    }
                }
                else -> {
                    if (activity.countryList?.isNotEmpty() == true) {
                        activity.dataList.clear()
                        for (country in activity.countryList!!) {
                            activity.dataList.add(country.countryName)
                        }
                        activity.adapter?.notifyDataSetChanged()
                        activity.list_view.setSelection(0)
                        activity.currentLevel = LEVEL_COUNTRY
                    }
                }
            }
        }
    }

    private fun queryProvinces() {
        title_text.text = "中国"
        back_btn.visibility = View.GONE
        DataSupport.getProvinces {
            provinceList = it
            var msg = Message()
            msg.obj = this@ChooseAreaFragment
            msg.arg1 = LEVEL_PROVINCE
            handler.sendMessage(msg)
        }
    }

    private fun queryCities() {
        title_text.text = selectedProvince?.provinceName
        back_btn.visibility = View.VISIBLE
        DataSupport.getCities(selectedProvince!!.provinceCode) {
            cityList = it
            var msg = Message()
            msg.obj = this@ChooseAreaFragment
            msg.arg1 = LEVEL_CITY
            handler.sendMessage(msg)
        }
    }

    private fun queryCountries() {
        title_text.text = selectedCity?.cityName
        back_btn.visibility = View.VISIBLE
        DataSupport.getCountries(selectedProvince!!.provinceCode, selectedCity!!.cityCode) {
            countryList = it
            var msg = Message()
            msg.obj = this@ChooseAreaFragment
            msg.arg1 = LEVEL_COUNTRY
            handler.sendMessage(msg)
        }
    }
}