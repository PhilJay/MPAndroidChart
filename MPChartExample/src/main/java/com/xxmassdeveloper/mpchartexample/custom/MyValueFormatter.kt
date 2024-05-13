package com.xxmassdeveloper.mpchartexample.custom

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

class MyValueFormatter : IValueFormatter {
    private val decimalFormat = DecimalFormat("###,###,###,##0.0")

    override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String? {
        return decimalFormat.format(value.toDouble()) + " $"
    }
}
