package com.github.mikephil.charting.interfaces.dataprovider

import com.github.mikephil.charting.data.CombinedData

/**
 * What the combined chart renderer and highlighter read from a chart that mixes line, bar, bubble,
 * candle and scatter data. Implemented by [com.github.mikephil.charting.charts.CombinedChart].
 */
interface CombinedDataProvider : LineDataProvider, BarDataProvider, BubbleDataProvider, CandleDataProvider, ScatterDataProvider {

    /** The combined data of the chart, null before data is set. */
    val combinedData: CombinedData?
}
