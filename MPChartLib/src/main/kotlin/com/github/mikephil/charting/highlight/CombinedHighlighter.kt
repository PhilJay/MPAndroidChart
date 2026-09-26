package com.github.mikephil.charting.highlight

import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.dataprovider.CombinedDataProvider
import com.github.mikephil.charting.utils.MPPointD
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Highlighter for the combined chart.
 *
 * Strategy: collects candidates from every data object in the combined data and tags each with its
 * [Highlight.dataIndex]. Bar data is delegated to a [BarHighlighter] so stacked bars work; all other data
 * is handled like [ChartHighlighter]. A touch inside a bar selects that bar; otherwise the nearest candidate
 * by pixel distance wins.
 *
 * @param barChart the same chart as [chart]; when it has no bar data at construction time, bars are
 *   highlighted like any other data set.
 */
public open class CombinedHighlighter(chart: CombinedDataProvider, barChart: BarDataProvider) : ChartHighlighter<CombinedDataProvider>(chart) {

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

    override fun getHighlightForX(xVal: Float, x: Float, y: Float): Highlight? {
        return getTouchedBar(x, y) ?: super.getHighlightForX(xVal, x, y)
    }

    /** Returns the highlight of the bar whose rectangle contains the pixel position ([x], [y]), or null. */
    private fun getTouchedBar(x: Float, y: Float): Highlight? {
        val barHighlighter = barHighlighter ?: return null
        val barData = chart.barData ?: return null
        val high = barHighlighter.getHighlight(x, y) ?: return null
        val set = barData.getDataSetByIndex(high.dataSetIndex) ?: return null
        val entry = barData.getEntryForHighlight(high) as? BarEntry<*> ?: return null

        val bottom = if (entry.isStacked) -entry.negativeSum else min(0f, entry.y)
        val top = if (entry.isStacked) entry.positiveSum else max(0f, entry.y)

        val pos = chart.getTransformer(set.axisDependency).getValuesByTouchPoint(x, y)
        val inside = abs(pos.x - entry.x) <= barData.barWidth / 2f && pos.y >= bottom && pos.y <= top
        MPPointD.recycleInstance(pos)
        if (!inside) return null

        high.dataIndex = chart.combinedData?.allData?.indexOf(barData) ?: return null
        return high
    }
}
