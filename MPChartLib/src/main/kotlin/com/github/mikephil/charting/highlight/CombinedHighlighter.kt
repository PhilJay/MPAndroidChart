package com.github.mikephil.charting.highlight

import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.dataprovider.CombinedDataProvider

/**
 * Highlighter for the combined chart.
 *
 * Strategy: collects candidates from every data object in the combined data and tags each with its
 * [Highlight.dataIndex]. Bar data is delegated to a [BarHighlighter] so stacked bars work; all other data
 * is handled like [ChartHighlighter]. The nearest candidate by pixel distance wins.
 *
 * @param barChart the same chart as [chart]; when it has no bar data at construction time, bars are
 *   highlighted like any other data set.
 */
open class CombinedHighlighter(chart: CombinedDataProvider, barChart: BarDataProvider) : ChartHighlighter<CombinedDataProvider>(chart) {

    /** Highlighter used for the bar data, null if the chart had no bar data when this was created. */
    protected val barHighlighter: BarHighlighter? = if (barChart.barData == null) null else BarHighlighter(barChart)

    override fun getHighlightsAtXValue(xVal: Float, x: Float, y: Float): List<Highlight> {
        highlightBuffer.clear()

        val dataObjects = chart.combinedData?.allData ?: return highlightBuffer

        for ((i, dataObject) in dataObjects.withIndex()) {
            if (barHighlighter != null && dataObject is BarData) {
                val high = barHighlighter.getHighlight(x, y)
                if (high != null) {
                    high.dataIndex = i
                    highlightBuffer.add(high)
                }
            } else {
                for (j in 0 until dataObject.dataSetCount) {
                    val dataSet = dataObject.getDataSetByIndex(j) ?: continue
                    if (!dataSet.isHighlightEnabled) continue

                    val highs = buildHighlights(dataSet, j, xVal, DataSet.Rounding.CLOSEST)
                    for (high in highs) {
                        high.dataIndex = i
                        highlightBuffer.add(high)
                    }
                }
            }
        }

        return highlightBuffer
    }
}
