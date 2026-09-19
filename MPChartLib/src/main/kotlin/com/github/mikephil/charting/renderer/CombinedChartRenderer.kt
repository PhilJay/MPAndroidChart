package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.ViewPortHandler
import java.lang.ref.WeakReference

/**
 * Draws a [CombinedChart] by delegating to one renderer per data type, in the chart's draw order. Bar, bubble,
 * line, candle and scatter renderers are created for the data types present in the combined data.
 */
open class CombinedChartRenderer(
    chart: CombinedChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : DataRenderer(animator, viewPortHandler) {

    /**
     * The renderers called in order, one per data type present; rebuilt by [createRenderers].
     */
    var subRenderers: MutableList<DataRenderer> = ArrayList(5)

    /**
     * Weak reference to the combined chart, so the renderer does not keep the view alive.
     */
    protected val chart: WeakReference<CombinedChart> = WeakReference(chart)

    private val highlightBuffer = ArrayList<Highlight>()

    init {
        createRenderers()
    }

    /**
     * Rebuilds [subRenderers] from the draw order and the data of the chart. Call it after changing the draw order
     * or after setting data with a different set of types. Does nothing when the chart has been garbage collected.
     */
    open fun createRenderers() {
        subRenderers.clear()

        val chart = chart.get() ?: return

        for (order in chart.drawOrder) {
            when (order) {
                DrawOrder.BAR -> if (chart.barData != null) {
                    subRenderers.add(BarChartRenderer(chart, animator, viewPortHandler))
                }
                DrawOrder.BUBBLE -> if (chart.bubbleData != null) {
                    subRenderers.add(BubbleChartRenderer(chart, animator, viewPortHandler))
                }
                DrawOrder.LINE -> if (chart.lineData != null) {
                    subRenderers.add(LineChartRenderer(chart, animator, viewPortHandler))
                }
                DrawOrder.CANDLE -> if (chart.candleData != null) {
                    subRenderers.add(CandleStickChartRenderer(chart, animator, viewPortHandler))
                }
                DrawOrder.SCATTER -> if (chart.scatterData != null) {
                    subRenderers.add(ScatterChartRenderer(chart, animator, viewPortHandler))
                }
            }
        }
    }

    override fun initBuffers() {
        for (renderer in subRenderers) renderer.initBuffers()
    }

    override fun drawData(c: Canvas) {
        for (renderer in subRenderers) renderer.drawData(c)
    }

    override fun drawValues(c: Canvas) {
        for (renderer in subRenderers) renderer.drawValues(c)
    }

    override fun drawExtras(c: Canvas) {
        for (renderer in subRenderers) renderer.drawExtras(c)
    }

    /**
     * Forwards each highlight to the renderer of the data it belongs to, matched by `Highlight.dataIndex`; a
     * highlight with data index -1 is passed to every renderer.
     */
    override fun drawHighlighted(c: Canvas, indices: List<Highlight>) {
        val chart = chart.get() ?: return
        val combinedData = chart.combinedData

        for (renderer in subRenderers) {
            val data: ChartData<*>? = when (renderer) {
                is BarChartRenderer -> combinedData?.barData
                is LineChartRenderer -> combinedData?.lineData
                is CandleStickChartRenderer -> combinedData?.candleData
                is ScatterChartRenderer -> combinedData?.scatterData
                is BubbleChartRenderer -> combinedData?.bubbleData
                else -> null
            }

            val dataIndex = if (data == null || combinedData == null) -1 else combinedData.getDataIndex(data)

            highlightBuffer.clear()

            for (h in indices) {
                if (h.dataIndex == dataIndex || h.dataIndex == -1) {
                    highlightBuffer.add(h)
                }
            }

            renderer.drawHighlighted(c, highlightBuffer)
        }
    }

    /**
     * Returns the renderer at [index] in [subRenderers], or null when the index is out of range.
     */
    fun getSubRenderer(index: Int): DataRenderer? {
        return if (index >= subRenderers.size || index < 0) null else subRenderers[index]
    }
}
