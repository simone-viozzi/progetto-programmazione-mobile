package com.example.receiptApp.pages.graphs

import android.content.Context
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import com.github.aachartmodel.aainfographics.aatools.AALinearGradientDirection
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

class GraphBuilder(
    private val context: Context
){

    val year_labels = arrayOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )

    /**
     * Generate_month_labels
     *
     * This function generate all the label needed for a month graph
     * in string array format. this function generate labels for each day
     * in the month passed as date.
     *
     * @return
     */

    fun generate_month_labels(
        date: Date = Date(),
        format: String = "dd/MM/yyyy"
    ): Array<String>{
        // https://stackoverflow.com/questions/39143366/get-all-dates-in-calendar-in-current-month

        val df = SimpleDateFormat(format)

        val today_cal = Calendar.getInstance()
        today_cal.time = date

        val days_n: Int = today_cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        var date_array: Array<String> = Array<String>(days_n){""}

        val month = today_cal.get(MONTH)
        val year = today_cal.get(YEAR)

        val start = Calendar.getInstance()
        val end = Calendar.getInstance()

        start.set(MONTH, month)  // this month
        start.set(YEAR, year)
        start.set(DAY_OF_MONTH, 1)
        start.getTime()

        end.set(MONTH, month)  // next month
        end.set(YEAR, year)
        end.set(DAY_OF_MONTH, days_n)
        end.getTime()

        var idx = 0
        while(start.before(end)){
            date_array[idx] = df.format(start.time)
            start.add(DATE, 1)
            idx++
        }

        return date_array
    }

    fun multi_histogram(

    ): AAChartModel {

        return AAChartModel.Builder(context)
            .setChartType(AAChartType.Column)
            .setBackgroundColor("#ffffff")
            .setDataLabelsEnabled(false)
            .setYAxisGridLineWidth(0f)
            .setLegendEnabled(false)
            .setTouchEventEnabled(true)
            .setSeries(
                AASeriesElement()
                    .name("Tokyo")
                    .data(arrayOf(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6)),
                AASeriesElement()
                    .name("NewYork")
                    .data(arrayOf(0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5)),
                AASeriesElement()
                    .name("London")
                    .data(arrayOf(0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0)),
                AASeriesElement()
                    .name("Berlin")
                    .data(arrayOf(3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8))
            )
            .build()
    }

    fun pie(
        categories: Array<String>,
        values_name: String = "series 1",
        values: Array<Double>,
    ): AAChartModel {

        var pieData = Array<Any>(categories.size){
            arrayOf(categories[it], values[it])
        }

        return AAChartModel.Builder(context)
            .setChartType(AAChartType.Pie)
            .setBackgroundColor("#ffffff")
            .setColorsTheme(arrayOf("#6200ee", "#3700B3", "#03DAC5", "#018786"))
            .setDataLabelsEnabled(false)
            .setYAxisGridLineWidth(0f)
            .setLegendEnabled(false)
            .setTouchEventEnabled(true)
            .setSeries(
                AASeriesElement()
                    .name(values_name)
                    .data(pieData)
            )
            .build()
    }

    fun category_graph(
        categories: Array<String>,
        values_name: String = "series 1",
        values: Array<Double>,
    ): AAChartModel {

        val stopsArr: Array<Any> = arrayOf(
            arrayOf(0.00, "#FFFFFF"),
            arrayOf(0.40, "#6200ee"),
            arrayOf(1.00, "#6200ee")
        )//颜色字符串设置支持十六进制类型和 rgba 类型

        val linearGradientColor = AAGradientColor.linearGradient(
            AALinearGradientDirection.ToTop,
            stopsArr
        )

        return AAChartModel.Builder(context)
            .setChartType(AAChartType.Column)
            .setBackgroundColor("#ffffff")
            .setDataLabelsEnabled(false)
            .setYAxisGridLineWidth(0f)
            .setLegendEnabled(false)
            .setTouchEventEnabled(true)
            .setCategories(*categories) // note: * + array -> vararg
            .setSeries(
                AASeriesElement()
                    .name(values_name)
                    .data(values as Array<Any>)
                    .color(linearGradientColor)
            )
            .build()
    }


}