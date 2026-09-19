package com.github.mikephil.charting.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.FSize
import com.github.mikephil.charting.utils.MPPointF
import java.lang.ref.WeakReference

/**
 * A marker that draws a drawable resource at the highlighted value. The chart sets [chartView] when the marker is
 * assigned to it.
 * @param context the context whose resources hold the drawable
 * @param drawableResourceId the drawable resource to draw
 */
open class MarkerImage(context: Context, drawableResourceId: Int) : IMarker {

    private val drawable: Drawable? = context.getDrawable(drawableResourceId)

    /** Offset in px from the highlighted point to the top left corner of the drawable. Default (0, 0). */
    override var offset = MPPointF()

    private val drawingOffset = MPPointF()

    private var weakChart: WeakReference<Chart<*>>? = null

    /** Size of the drawable in px; a width or height of 0 uses the drawable's intrinsic size. Default (0, 0). */
    var size = FSize()

    private val drawableBoundsCache = Rect()

    /**
     * Sets [offset].
     * @param offsetX horizontal offset in px
     * @param offsetY vertical offset in px
     */
    fun setOffset(offsetX: Float, offsetY: Float) {
        offset.x = offsetX
        offset.y = offsetY
    }

    /** The chart this marker is assigned to, held weakly, or null before it is assigned or after the chart was collected. */
    var chartView: Chart<*>?
        get() = weakChart?.get()
        set(value) {
            weakChart = value?.let { WeakReference(it) }
        }

    /** Returns [offset], shifted where needed so the drawable stays inside the bounds of [chartView]. */
    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        val offset = offset
        drawingOffset.x = offset.x
        drawingOffset.y = offset.y

        val chart = chartView

        var width = size.width
        var height = size.height

        if (width == 0f && drawable != null) width = drawable.intrinsicWidth.toFloat()
        if (height == 0f && drawable != null) height = drawable.intrinsicHeight.toFloat()

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

    /** Does nothing; the image has no content to update. */
    override fun refreshContent(e: Entry<*>, highlight: Highlight) {
    }

    /** Draws the drawable at [size], translated by [getOffsetForDrawingAtPoint]. Does nothing if the resource could not be loaded. */
    override fun draw(canvas: Canvas, posX: Float, posY: Float) {
        val drawable = drawable ?: return

        val offset = getOffsetForDrawingAtPoint(posX, posY)

        var width = size.width
        var height = size.height

        if (width == 0f) width = drawable.intrinsicWidth.toFloat()
        if (height == 0f) height = drawable.intrinsicHeight.toFloat()

        drawable.copyBounds(drawableBoundsCache)
        drawable.setBounds(
            drawableBoundsCache.left,
            drawableBoundsCache.top,
            drawableBoundsCache.left + width.toInt(),
            drawableBoundsCache.top + height.toInt()
        )

        val saveId = canvas.save()
        canvas.translate(posX + offset.x, posY + offset.y)
        drawable.draw(canvas)
        canvas.restoreToCount(saveId)

        drawable.bounds = drawableBoundsCache
    }
}
