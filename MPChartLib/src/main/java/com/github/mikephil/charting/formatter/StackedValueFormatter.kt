package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

/**
 * A formatter specifically for stacked BarChart that allows to specify whether the all stack values
 * or just the top value should be drawn.
 */
/**
 * Constructor.
 *
 * @param drawWholeStack if true, all stack values of the stacked bar entry are drawn, else only top
 * @param appendix       a string that should be appended behind the value
 * @param decimals       the number of decimal digits to use
 */
open class StackedValueFormatter(private val drawWholeStack: Boolean, private val appendix: String, decimals: Int) : IValueFormatter {
    private val decimalFormat: DecimalFormat

    init {
        val b = StringBuffer()
        for (i in 0 until decimals) {
            if (i == 0) b.append(".")
            b.append("0")
        }

        this.decimalFormat = DecimalFormat("###,###,###,##0$b")
    }

    override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {
        if (!drawWholeStack && entry is BarEntry) {
            val barEntry = entry
            val vals = barEntry.yVals

            if (vals != null) {
                // find out if we are on top of the stack

                return if (vals[vals.size - 1] == value) {
                    // return the "sum" across all stack values

                    decimalFormat.format(barEntry.y.toDouble()) + appendix
                } else {
                    "" // return empty
                }
            }
        }

        // return the "proposed" value
        return decimalFormat.format(value.toDouble()) + appendix
    }
}
