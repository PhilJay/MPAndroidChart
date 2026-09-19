package com.github.mikephil.charting.interfaces.dataprovider

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData

/**
 * What the line chart renderer reads from a chart that shows lines.
 * Implemented by [com.github.mikephil.charting.charts.LineChart] and the combined chart.
 */
interface LineDataProvider : BarLineScatterCandleBubbleDataProvider {

    /** The line data of the chart, null when there is none. */
    val lineData: LineData?

    /** Returns the left or right y axis, used to find the axis minimum when filling below a line. */
    fun getAxis(axis: YAxis.AxisDependency): YAxis
}
