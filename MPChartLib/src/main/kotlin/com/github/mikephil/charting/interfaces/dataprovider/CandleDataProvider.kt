package com.github.mikephil.charting.interfaces.dataprovider

import com.github.mikephil.charting.data.CandleData

/**
 * What the candle stick renderer reads from a chart that shows candles.
 * Implemented by [com.github.mikephil.charting.charts.CandleStickChart] and the combined chart.
 */
public interface CandleDataProvider : BarLineScatterCandleBubbleDataProvider {

    /** The candle data of the chart, null when there is none. */
    public val candleData: CandleData?
}
