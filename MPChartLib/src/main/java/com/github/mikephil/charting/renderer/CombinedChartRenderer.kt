package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.ViewPortHandler
import java.lang.ref.WeakReference

/**
 * Renderer class that is responsible for rendering multiple different data-types.
 */
class CombinedChartRenderer(chart: CombinedChart, animator: ChartAnimator?, viewPortHandler: ViewPortHandler?) : DataRenderer(animator, viewPortHandler) {
    /**
     * all rederers for the different kinds of data this combined-renderer can draw
     */
    protected var mRenderers: MutableList<DataRenderer> = ArrayList(5)

    protected var mChart: WeakReference<Chart<*>> = WeakReference(chart)

    /**
     * Creates the renderers needed for this combined-renderer in the required order. Also takes the DrawOrder into
     * consideration.
     */
    fun createRenderers() {
        mRenderers.clear()

        val chart = mChart.get() as CombinedChart? ?: return

        val orders = chart.drawOrder

        for (order in orders) {
            when (order) {
                DrawOrder.BAR -> if (chart.barData != null) mRenderers.add(BarChartRenderer(chart, animator, viewPortHandler))
                DrawOrder.BUBBLE -> if (chart.bubbleData != null) mRenderers.add(BubbleChartRenderer(chart, animator, viewPortHandler))
                DrawOrder.LINE -> mRenderers.add(LineChartRenderer(chart, animator, viewPortHandler))
                DrawOrder.CANDLE -> if (chart.candleData != null) mRenderers.add(CandleStickChartRenderer(chart, animator, viewPortHandler))
                DrawOrder.SCATTER -> if (chart.scatterData != null) mRenderers.add(ScatterChartRenderer(chart, animator, viewPortHandler))
            }
        }
    }

    override fun initBuffers() {
        for (renderer in mRenderers) renderer.initBuffers()
    }

    override fun drawData(c: Canvas) {
        for (renderer in mRenderers) renderer.drawData(c)
    }

    override fun drawValues(c: Canvas) {
        for (renderer in mRenderers) renderer.drawValues(c)
    }

    override fun drawExtras(c: Canvas) {
        for (renderer in mRenderers) renderer.drawExtras(c)
    }

    protected var mHighlightBuffer: MutableList<Highlight> = ArrayList()

    init {
        createRenderers()
    }

    override fun drawHighlighted(c: Canvas, indices: Array<Highlight>) {
        val chart = mChart.get() ?: return

        for (renderer in mRenderers) {
            var data: ChartData<*>? = null

            if (renderer is BarChartRenderer) data = renderer.chart.barData
            else if (renderer is LineChartRenderer) data = renderer.chart.lineData
            else if (renderer is CandleStickChartRenderer) data = renderer.chart.candleData
            else if (renderer is ScatterChartRenderer) data = renderer.chart.scatterData
            else if (renderer is BubbleChartRenderer) data = renderer.chart.bubbleData

            val dataIndex = if (data == null)
                -1
            else
                (chart.data as CombinedData).allData.indexOf(data)

            mHighlightBuffer.clear()

            for (h in indices) {
                if (h.dataIndex == dataIndex || h.dataIndex == -1) mHighlightBuffer.add(h)
            }

            renderer.drawHighlighted(c, mHighlightBuffer.toTypedArray<Highlight>())
        }
    }

    /**
     * Returns the sub-renderer object at the specified index.
     *
     * @param index
     * @return
     */
    fun getSubRenderer(index: Int): DataRenderer? {
        return if (index >= mRenderers.size || index < 0) null
        else mRenderers[index]
    }

    val subRenderers: List<DataRenderer>
        /**
         * Returns all sub-renderers.
         *
         * @return
         */
        get() = mRenderers

    fun setSubRenderers(renderers: MutableList<DataRenderer>) {
        this.mRenderers = renderers
    }
}
