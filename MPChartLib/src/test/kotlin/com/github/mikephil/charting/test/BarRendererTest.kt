package com.github.mikephil.charting.test

import android.graphics.Canvas
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import org.junit.Test

class BarRendererTest {

    private class FakeBarChart(override var barData: BarData?) : BarDataProvider {
        private val handler = ViewPortHandler()
        override val isDrawBarShadowEnabled = false
        override val isDrawValueAboveBarEnabled = true
        override val isHighlightFullBarEnabled = false
        override fun getTransformer(axis: YAxis.AxisDependency) = Transformer(handler)
        override fun isInverted(axis: YAxis.AxisDependency) = false
        override val lowestVisibleX = 0f
        override val highestVisibleX = 10f
        override val xChartMin = 0f
        override val xChartMax = 10f
        override val xRange = 10f
        override val yChartMin = 0f
        override val yChartMax = 10f
        override val maxHighlightDistance = 0f
        override val centerOfView = MPPointF.getInstance(0f, 0f)
        override val centerOffsets = MPPointF.getInstance(0f, 0f)
        override val contentRect = RectF()
        override val defaultValueFormatter: IValueFormatter = DefaultValueFormatter(0)
        override val data: BarData? get() = barData
        override val maxVisibleCount = 100
    }

    private fun barData(sets: Int, entries: Int) = BarData(
        List(sets) { s -> BarDataSet(List(entries) { BarEntry(it.toFloat(), it.toFloat()) }, "set $s") }
    )

    @Test
    fun drawingSurvivesDataChangedWithoutNotifying() {
        val chart = FakeBarChart(barData(2, 5))
        val renderer = BarChartRenderer(chart, ChartAnimator(), ViewPortHandler())
        renderer.initBuffers()

        val canvas = Canvas()
        renderer.drawData(canvas)

        chart.barData = barData(1, 9)
        renderer.drawData(canvas)
        renderer.drawValues(canvas)
    }
}
