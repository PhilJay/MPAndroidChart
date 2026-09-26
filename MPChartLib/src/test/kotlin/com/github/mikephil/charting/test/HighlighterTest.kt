package com.github.mikephil.charting.test

import android.graphics.RectF
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.highlight.BarHighlighter
import com.github.mikephil.charting.highlight.CombinedHighlighter
import com.github.mikephil.charting.interfaces.dataprovider.CombinedDataProvider
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import org.junit.Assert.assertEquals
import org.junit.Test

// On the JVM the transformer matrices do nothing, so pixels equal values here.
class HighlighterTest {

    private class FakeChart(override val combinedData: CombinedData) : CombinedDataProvider {
        private val handler = ViewPortHandler()
        override val barData: BarData? get() = combinedData.barData
        override val lineData: LineData? get() = combinedData.lineData
        override val scatterData: ScatterData? get() = combinedData.scatterData
        override val candleData: CandleData? get() = combinedData.candleData
        override val bubbleData: BubbleData? get() = combinedData.bubbleData
        override val data: CombinedData get() = combinedData
        override val isDrawBarShadowEnabled = false
        override val isDrawValueAboveBarEnabled = true
        override val isHighlightFullBarEnabled = false
        override fun getTransformer(axis: YAxis.AxisDependency) = Transformer(handler)
        override fun isInverted(axis: YAxis.AxisDependency) = false
        override fun getAxis(axis: YAxis.AxisDependency) = YAxis(axis)
        override val lowestVisibleX = 0f
        override val highestVisibleX = 10f
        override val xChartMin = 0f
        override val xChartMax = 10f
        override val xRange = 10f
        override val yChartMin = 0f
        override val yChartMax = 10f
        override val maxHighlightDistance = 500f
        override val centerOfView = MPPointF.getInstance(0f, 0f)
        override val centerOffsets = MPPointF.getInstance(0f, 0f)
        override val contentRect = RectF()
        override val defaultValueFormatter: IValueFormatter = DefaultValueFormatter(0)
        override val maxVisibleCount = 100
    }

    @Test
    fun tapOnGroupedBarSelectsItEvenWhenTheOtherBarUsesAnotherAxis() {
        val left = BarDataSet(listOf(BarEntry(0.8f, 1.5f)), "left").apply { axisDependency = YAxis.AxisDependency.LEFT }
        val right = BarDataSet(listOf(BarEntry(1.2f, 8f)), "right").apply { axisDependency = YAxis.AxisDependency.RIGHT }
        val chart = FakeChart(CombinedData().apply { barData = BarData(left, right).apply { barWidth = 0.35f } })

        val high = BarHighlighter(chart).getHighlight(1.2f, 1f)

        assertEquals(1, high?.dataSetIndex)
    }

    @Test
    fun tapInsideBarSelectsTheBarOverANearerLinePoint() {
        val combined = CombinedData().apply {
            lineData = LineData(LineDataSet(listOf(Entry(1f, 5f)), "line"))
            barData = BarData(BarDataSet(listOf(BarEntry(1f, 10f)), "bar"))
        }
        val chart = FakeChart(combined)

        val insideBar = CombinedHighlighter(chart, chart).getHighlight(1.1f, 4f)
        val besideBar = CombinedHighlighter(chart, chart).getHighlight(1.6f, 5f)

        assertEquals(combined.allData.indexOfFirst { it is BarData }, insideBar?.dataIndex)
        assertEquals(combined.allData.indexOfFirst { it is LineData }, besideBar?.dataIndex)
    }
}
