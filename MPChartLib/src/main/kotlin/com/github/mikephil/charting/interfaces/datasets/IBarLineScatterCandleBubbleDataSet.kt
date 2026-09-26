package com.github.mikephil.charting.interfaces.datasets

import com.github.mikephil.charting.data.Entry

/**
 * Styling shared by data sets of charts with an x axis and y axes: bar, line, scatter, candle and bubble.
 */
public interface IBarLineScatterCandleBubbleDataSet<T : Entry<*>> : IDataSet<T> {

    /** Color of the highlight indicator drawn at a selected entry. Default a light orange, rgb(255, 187, 115). */
    public val highlightColor: Int
}
