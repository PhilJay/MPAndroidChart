package com.xxmassdeveloper.mpchartexample.custom

import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

/**
 * Created by Philipp Jahoda on 12/09/15.
 */
class MyFillFormatter(private val fillPos: Float) : IFillFormatter {
    override fun getFillLinePosition(dataSet: ILineDataSet, dataProvider: LineDataProvider): Float {
        // your logic could be here
        return fillPos
    }
}