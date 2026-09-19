package com.xxmassdeveloper.mpchartexample.custom

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.DecimalFormat

class MyAxisValueFormatter : IAxisValueFormatter {

    private val format = DecimalFormat("###,###,###,##0.0")

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        return format.format(value.toDouble()) + " $"
    }
}
