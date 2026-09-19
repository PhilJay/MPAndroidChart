package com.xxmassdeveloper.mpchartexample.custom

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

class MyCustomXAxisValueFormatter(private val viewPortHandler: ViewPortHandler) : IAxisValueFormatter {

    private val format = DecimalFormat("###,###,###,##0.0")

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        val xScale = viewPortHandler.scaleX
        return when {
            xScale > 5 -> "4"
            xScale > 3 -> "3"
            xScale > 1 -> "2"
            else -> format.format(value.toDouble())
        }
    }
}
