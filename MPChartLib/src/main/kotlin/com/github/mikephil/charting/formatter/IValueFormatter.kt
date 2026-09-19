package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Formats the values drawn next to the entries inside the chart. Set it on a data set or on the chart data.
 * Usage: `IValueFormatter { value, _, _, _ -> value.toInt().toString() }`.
 * Called for every drawn value on every frame, so avoid allocations and heavy work.
 */
fun interface IValueFormatter {

    /**
     * @param value the value to format
     * @param entry the entry the value belongs to, for example a [com.github.mikephil.charting.data.BarEntry] in a bar chart
     * @param dataSetIndex index of the entry's data set in the chart data
     * @param viewPortHandler current chart state such as scale and translation
     * @return the text to draw
     */
    fun getFormattedValue(value: Float, entry: Entry<*>, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String

    /**
     * Formats one value of a stacked bar entry. The bar renderers call this instead of [getFormattedValue] so a
     * formatter can tell the segments apart by position rather than by value. The default ignores the position.
     *
     * @param value the value to format, one of the entry's stack values
     * @param stackIndex position of [value] in the entry's stack values, counted from the first
     * @param entry the entry the value belongs to
     * @param dataSetIndex index of the entry's data set in the chart data
     * @param viewPortHandler current chart state such as scale and translation
     * @return the text to draw
     */
    fun getStackedFormattedValue(
        value: Float,
        stackIndex: Int,
        entry: Entry<*>,
        dataSetIndex: Int,
        viewPortHandler: ViewPortHandler
    ): String = getFormattedValue(value, entry, dataSetIndex, viewPortHandler)
}
