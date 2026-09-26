package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

/**
 * Decides the y value, in value space, down or up to which a line data set is filled. Only used when the data set
 * draws a fill. Usage: `IFillFormatter { _, _ -> 0f }` fills to the zero line.
 */
public fun interface IFillFormatter {

    /**
     * @param dataSet the line data set being drawn
     * @param dataProvider the chart drawing it, giving the axis range and the whole line data
     * @return the y value in value space the fill extends to
     */
    public fun getFillLinePosition(dataSet: ILineDataSet<*>, dataProvider: LineDataProvider): Float
}
