package com.github.mikephil.charting.interfaces.dataprovider

import com.github.mikephil.charting.data.BarData

/**
 * What the bar chart renderer and highlighter read from a chart that shows bars.
 * Implemented by [com.github.mikephil.charting.charts.BarChart] and the combined chart.
 */
interface BarDataProvider : BarLineScatterCandleBubbleDataProvider {

    /** The bar data of the chart, null when there is none. */
    val barData: BarData?

    /** True to draw a shadow bar behind each bar that spans the full axis height. */
    val isDrawBarShadowEnabled: Boolean

    /** True to draw value labels above bars, false to draw them inside near the top. */
    val isDrawValueAboveBarEnabled: Boolean

    /** True to highlight the whole stacked bar instead of only the touched stack value. */
    val isHighlightFullBarEnabled: Boolean
}
