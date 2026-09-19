package com.github.mikephil.charting.interfaces.dataprovider

import com.github.mikephil.charting.data.ScatterData

/**
 * What the scatter chart renderer reads from a chart that shows scatter shapes.
 * Implemented by [com.github.mikephil.charting.charts.ScatterChart] and the combined chart.
 */
interface ScatterDataProvider : BarLineScatterCandleBubbleDataProvider {

    /** The scatter data of the chart, null when there is none. */
    val scatterData: ScatterData?
}
