package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

/**
 * Default formatter that calculates the position of the filled line.
 */
open class DefaultFillFormatter : IFillFormatter {

    override fun getFillLinePosition(dataSet: ILineDataSet?, dataProvider: LineDataProvider?): Float {
        val fillMin: Float
        val chartMaxY = dataProvider!!.yChartMax
        val chartMinY = dataProvider.yChartMin
        val data = dataProvider.lineData
        fillMin = if (dataSet!!.yMax > 0 && dataSet.yMin < 0) {
            0f
        } else {
            val max: Float = if (data.yMax > 0) 0f
            else
                chartMaxY
            val min: Float = if (data.yMin < 0)
                0f
            else
                chartMinY
            if (dataSet.yMin >= 0)
                min
            else
                max
        }
        return fillMin
    }
}
