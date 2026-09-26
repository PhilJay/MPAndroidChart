package com.github.mikephil.charting.compose

import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import java.lang.ref.WeakReference

/**
 * A marker whose content is a composable, drawn at the highlighted entry like any other [IMarker].
 * The content lives in an invisible [ComposeView] that is added as a child of the chart so it takes
 * part in composition; the chart draws that view at the highlighted position. Content for a newly
 * highlighted entry composes on the next frame, so the marker shows one frame after the highlight.
 * The chart composables create one through their `marker` parameter; if you create one yourself,
 * call [detach] when the chart should drop it.
 *
 * @param chart chart that shows the marker, held weakly.
 * @param content composable that draws the marker for the highlighted entry.
 */
public class ComposeMarker(
    chart: Chart<*>,
    content: @Composable (entry: Entry<*>, highlight: Highlight) -> Unit,
) : IMarker {

    /** Extra offset in dp applied on top of the default position, which centers the content above the entry. */
    override val offset: MPPointF = MPPointF()
    private val drawingOffset = MPPointF()
    private val chartRef = WeakReference(chart)

    /** Composable that draws the marker for the highlighted entry. Changing it recomposes the marker. */
    public var content: @Composable (entry: Entry<*>, highlight: Highlight) -> Unit by mutableStateOf(content)

    private var shown by mutableStateOf<Pair<Entry<*>, Highlight>?>(null)
    private var composedFor: Pair<Entry<*>, Highlight>? = null

    private val view = ComposeView(chart.context).apply {
        visibility = View.INVISIBLE
        setContent {
            val current = shown ?: return@setContent
            this@ComposeMarker.content(current.first, current.second)
            SideEffect {
                composedFor = current
                chartRef.get()?.invalidate()
            }
        }
    }

    init {
        chart.addView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    /** Shifts the content by [offsetX] and [offsetY] dp from its default position above the entry. */
    public fun setOffset(offsetX: Float, offsetY: Float) {
        offset.x = offsetX
        offset.y = offsetY
    }

    /** Removes the marker's view from the chart and unsets it as the chart's marker. Does nothing once the chart is gone. */
    public fun detach() {
        val chart = chartRef.get() ?: return
        if (chart.marker === this) chart.marker = null
        chart.removeView(view)
    }

    /** Shows [content] for [e] and measures the view at its wrap content size. */
    override fun refreshContent(e: Entry<*>, highlight: Highlight) {
        val current = shown
        if (current?.first !== e || current.second !== highlight) shown = e to highlight
        val unspecified = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(unspecified, unspecified)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    /**
     * Returns the position of the content relative to [posX], [posY] in pixels: centered above the entry,
     * shifted by [offset] converted to pixels, and clamped so the marker stays inside the chart.
     */
    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        drawingOffset.x = Utils.convertDpToPixel(offset.x) - view.width / 2f
        drawingOffset.y = Utils.convertDpToPixel(offset.y) - view.height - Utils.convertDpToPixel(12f)
        val chart = chartRef.get()
        val width = view.width.toFloat()
        val height = view.height.toFloat()

        if (posX + drawingOffset.x < 0) {
            drawingOffset.x = -posX
        } else if (chart != null && posX + width + drawingOffset.x > chart.width) {
            drawingOffset.x = chart.width - posX - width
        }
        if (posY + drawingOffset.y < 0) {
            drawingOffset.y = -posY
        } else if (chart != null && posY + height + drawingOffset.y > chart.height) {
            drawingOffset.y = chart.height - posY - height
        }
        return drawingOffset
    }

    /**
     * Draws the composed content at [posX], [posY] plus the drawing offset, or skips the frame while the
     * content for a new entry is not composed yet.
     */
    override fun draw(canvas: Canvas, posX: Float, posY: Float) {
        // Content for a new entry composes on the next frame; the SideEffect redraws the chart then.
        if (composedFor !== shown) return
        val offset = getOffsetForDrawingAtPoint(posX, posY)
        val saveId = canvas.save()
        canvas.translate(posX + offset.x, posY + offset.y)
        view.draw(canvas)
        canvas.restoreToCount(saveId)
    }
}
