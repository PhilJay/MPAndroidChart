package com.xxmassdeveloper.mpchartexample.custom

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter

/**
 * Created by Philipp Jahoda on 14/09/15.
 */
class YearXAxisFormatter : IAxisValueFormatter {
    private val mMonths = arrayOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    )

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        val percent = value / axis.mAxisRange
        return mMonths[(mMonths.size * percent).toInt()]
    }
}