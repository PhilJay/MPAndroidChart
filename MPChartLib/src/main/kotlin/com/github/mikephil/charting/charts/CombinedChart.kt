package com.github.mikephil.charting.charts

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.Keep
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.highlight.CombinedHighlighter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.CombinedDataProvider
import com.github.mikephil.charting.renderer.CombinedChartRenderer

/**
 * Chart that draws line, bar, scatter, candle and bubble data from one [CombinedData] in the same
 * content area. It implements [CombinedDataProvider] so each contained renderer finds its own data.
 */
@Keep
open class CombinedChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : BarLineChartBase<CombinedData>(context, attrs, defStyle), CombinedDataProvider {

    /** Whether bar value labels sit above the bar top instead of below it. Default true. */
    override var isDrawValueAboveBarEnabled = true

    /** Whether a tap highlights the whole stacked bar instead of the single tapped stack value. Default true. */
    override var isHighlightFullBarEnabled = true

    /** Whether a grey area up to the axis maximum is drawn behind each bar. Costs performance. Default false. */
    override var isDrawBarShadowEnabled = false

    private var drawOrderValue: List<DrawOrder>? = null

    /**
     * Order in which the data kinds are drawn, first at the back. Defaults to bar, bubble, line, candle,
     * scatter. Setting an empty list is ignored.
     */
    var drawOrder: List<DrawOrder>
        get() = drawOrderValue ?: listOf(DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.LINE, DrawOrder.CANDLE, DrawOrder.SCATTER)
        set(value) {
            if (value.isEmpty()) return
            drawOrderValue = value
        }

    /** The data kinds a [CombinedChart] can draw, used to set its [drawOrder]. */
    enum class DrawOrder {
        BAR, BUBBLE, LINE, CANDLE, SCATTER
    }

    override fun init() {
        super.init()

        highlighter = CombinedHighlighter(this, this)

        renderer = CombinedChartRenderer(this, animator, viewPortHandler)
    }

    override val combinedData: CombinedData?
        get() = data

    /**
     * Same as [Chart.data], and additionally rebuilds the highlighter and the renderers for the data
     * kinds contained in the new value.
     */
    override var data: CombinedData?
        get() = super.data
        set(value) {
            super.data = value
            highlighter = CombinedHighlighter(this, this)
            (renderer as CombinedChartRenderer).createRenderers()
            renderer.initBuffers()
        }

    override fun getHighlightByTouchPoint(x: Float, y: Float): Highlight? {
        if (data == null) {
            Log.e(LOG_TAG, "Can't select by touch. No data set.")
            return null
        }

        val h = highlighter.getHighlight(x, y)
        if (h == null || !isHighlightFullBarEnabled) return h

        return Highlight(h.x, h.y, h.xPx, h.yPx, h.dataSetIndex, -1, h.axis)
    }

    override val lineData: LineData?
        get() = data?.lineData

    override val barData: BarData?
        get() = data?.barData

    override val scatterData: ScatterData?
        get() = data?.scatterData

    override val candleData: CandleData?
        get() = data?.candleData

    override val bubbleData: BubbleData?
        get() = data?.bubbleData

    override fun drawMarkers(canvas: Canvas) {
        val marker = marker ?: return
        if (!isDrawMarkersEnabled || !valuesToHighlight()) return

        val data = data ?: return
        val highlighted = highlighted

        for (highlight in highlighted) {
            val set = data.getDataSetByHighlight(highlight) ?: continue

            val e = data.getEntryForHighlight(highlight) ?: continue

            val entryIndex = set.getEntryIndex(e)

            if (entryIndex > set.entryCount * animator.phaseX) continue

            val pos = getMarkerPosition(highlight)

            if (!viewPortHandler.isInBounds(pos[0], pos[1])) continue

            marker.refreshContent(e, highlight)
            marker.draw(canvas, pos[0], pos[1])
        }
    }
}
