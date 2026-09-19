package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

/**
 * Appends " %" to each value, formatted with one decimal digit by default, for example "12.5 %". Meant for pie charts.
 * Works as value formatter and as axis formatter.
 * @property format the number format applied before the percent sign.
 */
open class PercentFormatter(protected val format: DecimalFormat = DecimalFormat("###,###,##0.0")) : IValueFormatter, IAxisValueFormatter {

    override fun getFormattedValue(value: Float, entry: Entry<*>, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {
        return format.format(value.toDouble()) + " %"
    }

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        return format.format(value.toDouble()) + " %"
    }

    /** Number of decimals [format] produces. */
    val decimalDigits: Int
        get() = format.maximumFractionDigits
}
