package com.xxmassdeveloper.mpchartexample.custom

import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class MyFillFormatter(private val fillPos: Float) : IFillFormatter {

    override fun getFillLinePosition(dataSet: ILineDataSet<*>, dataProvider: LineDataProvider): Float = fillPos
}
