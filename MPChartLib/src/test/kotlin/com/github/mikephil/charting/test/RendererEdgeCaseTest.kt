package com.github.mikephil.charting.test

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.CombinedDataProvider
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.renderer.BubbleChartRenderer
import com.github.mikephil.charting.renderer.CandleStickChartRenderer
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import org.junit.Assert.assertNull
import org.junit.Test

class RendererEdgeCaseTest {

    /** Without a real matrix every value is its own pixel, and the content rectangle is the single point (0, 0). */
    private class FakeChart(override val data: CombinedData) : CombinedDataProvider {
        private val handler = ViewPortHandler()
        override val combinedData get() = data
        override val lineData: LineData? get() = data.lineData
        override val barData: BarData? get() = data.barData
        override val bubbleData: BubbleData? get() = data.bubbleData
        override val candleData: CandleData? get() = data.candleData
        override val scatterData: ScatterData? get() = data.scatterData
        override val isDrawBarShadowEnabled = false
        override val isDrawValueAboveBarEnabled = true
        override val isHighlightFullBarEnabled = false
        override fun getAxis(axis: YAxis.AxisDependency) = YAxis(axis)
        override fun getTransformer(axis: YAxis.AxisDependency) = Transformer(handler)
        override fun isInverted(axis: YAxis.AxisDependency) = false
        override val lowestVisibleX = -10f
        override val highestVisibleX = 10f
        override val xChartMin = -10f
        override val xChartMax = 10f
        override val xRange = 20f
        override val yChartMin = 0f
        override val yChartMax = 10f
        override val maxHighlightDistance = 0f
        override val centerOfView = MPPointF.getInstance(0f, 0f)
        override val centerOffsets = MPPointF.getInstance(0f, 0f)
        override val contentRect = RectF()
        override val defaultValueFormatter: IValueFormatter = DefaultValueFormatter(0)
        override val maxVisibleCount = 100
    }

    private val canvas = Canvas()

    @Test
    fun drawingSkipsWhatCannotBeDrawn() {
        val emptyCandles = FakeChart(CombinedData().apply { candleData = CandleData(CandleDataSet(emptyList<CandleEntry<Nothing>>(), "candles")) })
        CandleStickChartRenderer(emptyCandles, ChartAnimator(), ViewPortHandler()).drawData(canvas)

        val line = LineDataSet(listOf(Entry(0f, 0f)), "line").apply { resetCircleColors() }
        val noCircleColors = FakeChart(CombinedData().apply { lineData = LineData(line) })
        LineChartRenderer(noCircleColors, ChartAnimator(), ViewPortHandler()).drawExtras(canvas)

        val stacked = BarEntry(0f, listOf(1f, 2f))
        val bars = FakeChart(CombinedData().apply { barData = BarData(BarDataSet(listOf(stacked), "bars")) })
        val barRenderer = BarChartRenderer(bars, ChartAnimator(), ViewPortHandler())
        barRenderer.drawData(canvas)
        stacked.stackValues = listOf(1f, 2f, 3f)
        barRenderer.drawData(canvas)
    }

    @Test
    fun highlightsThatPointPastTheDataAreIgnored() {
        val bars = FakeChart(CombinedData().apply { barData = BarData(BarDataSet(listOf(BarEntry(0f, listOf(1f, 2f))), "bars")) })
        val stackPastTheEnd = Highlight(0f, 3f, 0).apply { stackIndex = 5 }
        BarChartRenderer(bars, ChartAnimator(), ViewPortHandler()).drawHighlighted(canvas, listOf(stackPastTheEnd))

        val bubbleSet = BubbleDataSet(listOf(BubbleEntry(-1f, 0f, 1f)), "bubbles").apply { colors = listOf(Color.RED, Color.GREEN, Color.BLUE) }
        val bubbles = FakeChart(CombinedData().apply { bubbleData = BubbleData(bubbleSet) })
        BubbleChartRenderer(bubbles, ChartAnimator(), ViewPortHandler()).drawHighlighted(canvas, listOf(Highlight(-1f, 0f, 0)))

        val pie = PieData(PieDataSet(listOf(PieEntry(1f), PieEntry(2f)), ""))
        assertNull(pie.getEntryForHighlight(Highlight(2f, 0, -1)))
        assertNull(pie.getEntryForHighlight(Highlight(-1f, 0, -1)))
        assertNull(PieData().getEntryForHighlight(Highlight(0f, 0, -1)))

        val radar = RadarData(listOf(RadarDataSet(listOf(RadarEntry(1f)), "short"), RadarDataSet(listOf(RadarEntry(1f), RadarEntry(2f)), "long")))
        assertNull(radar.getEntryForHighlight(Highlight(1f, 0, -1)))
    }
}
