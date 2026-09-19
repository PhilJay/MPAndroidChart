package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

/**
 * Fill position used by line data sets that set no formatter. The fill reaches the zero line when the data set or
 * the whole line data has values on both sides of zero, otherwise the axis minimum for a positive data set and the
 * axis maximum for a negative one. Returns 0 while the chart has no line data.
 */
class DefaultFillFormatter : IFillFormatter {

    override fun getFillLinePosition(dataSet: ILineDataSet<*>, dataProvider: LineDataProvider): Float {
        val chartMaxY = dataProvider.yChartMax
        val chartMinY = dataProvider.yChartMin
        val data = dataProvider.lineData ?: return 0f

        if (dataSet.yMax > 0 && dataSet.yMin < 0) return 0f

        val max = if (data.yMax > 0) 0f else chartMaxY
        val min = if (data.yMin < 0) 0f else chartMinY
        return if (dataSet.yMin >= 0) min else max
    }
}
