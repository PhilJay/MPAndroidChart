package com.xxmassdeveloper.mpchartexample.custom

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter

class YearXAxisFormatter : IAxisValueFormatter {

    private val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        val percent = value / axis.axisRange
        return months[(months.size * percent).toInt().coerceIn(months.indices)]
    }
}
