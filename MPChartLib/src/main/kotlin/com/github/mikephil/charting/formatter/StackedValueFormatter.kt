package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

/**
 * Formats the values of a stacked bar chart, labelling either every stack value or only the total on top of each bar.
 * With [drawWholeStack] false, a stacked [BarEntry] gets its total ([BarEntry.y]) at the topmost stack value and an
 * empty string for the others. The bar renderers pass the position in the stack, so a value that appears twice in
 * one stack is handled correctly; a caller that formats a value itself, without the position, falls back to
 * comparing values. Entries without a stack and [drawWholeStack] true format each value as it is.
 * @param drawWholeStack true to label every stack value, false to label only the total on top of the bar
 * @param appendix text appended after each formatted value
 * @param decimals number of decimal digits
 */
public class StackedValueFormatter(
    private val drawWholeStack: Boolean,
    private val appendix: String,
    decimals: Int
) : IValueFormatter {

    private val format = DecimalFormat("###,###,###,##0" + decimalPattern(decimals))

    override fun getFormattedValue(value: Float, entry: Entry<*>, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {
        if (!drawWholeStack && entry is BarEntry<*>) {
            val vals = entry.stackValues
            if (vals != null) {
                return if (value == vals.lastOrNull()) format.format(entry.y.toDouble()) + appendix else ""
            }
        }
        return format.format(value.toDouble()) + appendix
    }

    override fun getStackedFormattedValue(
        value: Float,
        stackIndex: Int,
        entry: Entry<*>,
        dataSetIndex: Int,
        viewPortHandler: ViewPortHandler
    ): String {
        if (!drawWholeStack && entry is BarEntry<*>) {
            val vals = entry.stackValues
            if (vals != null) {
                return if (stackIndex == vals.lastIndex) format.format(entry.y.toDouble()) + appendix else ""
            }
        }
        return format.format(value.toDouble()) + appendix
    }
}
