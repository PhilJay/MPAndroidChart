package com.github.mikephil.charting.interfaces.dataprovider

import com.github.mikephil.charting.data.BubbleData

/**
 * What the bubble chart renderer reads from a chart that shows bubbles.
 * Implemented by [com.github.mikephil.charting.charts.BubbleChart] and the combined chart.
 */
interface BubbleDataProvider : BarLineScatterCandleBubbleDataProvider {

    /** The bubble data of the chart, null when there is none. */
    val bubbleData: BubbleData?
}
