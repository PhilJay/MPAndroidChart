package com.github.mikephil.charting.data

import android.graphics.Color
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet

/**
 * Base for the data sets of the charts with an x and a y axis (bar, line, scatter, candle and bubble).
 * Adds the highlight color those charts share.
 *
 * @param T the entry type this set holds.
 */
abstract class BarLineScatterCandleBubbleDataSet<T : Entry<*>>(entries: List<T>, label: String) :
    DataSet<T>(entries, label), IBarLineScatterCandleBubbleDataSet<T> {

    override var highlightColor = Color.rgb(255, 187, 115)

    /** Copies the styling from this set into [dataSet]; entries are not copied here. */
    protected open fun copy(dataSet: BarLineScatterCandleBubbleDataSet<*>) {
        super.copy(dataSet)
        dataSet.highlightColor = highlightColor
    }
}
