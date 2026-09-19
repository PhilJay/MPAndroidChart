package com.github.mikephil.charting.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.lang.ref.WeakReference

/**
 * A marker built from a layout resource, inflated once and drawn at the highlighted value. Subclass it and override
 * [refreshContent] to update the views before each draw. The chart sets [chartView] when the marker is assigned to it.
 * @param context the context used to inflate the layout
 * @param layoutResource the layout resource that becomes the marker's content
 */
@SuppressLint("ViewConstructor")
open class MarkerView(context: Context, layoutResource: Int) : RelativeLayout(context), IMarker {

    /** Offset in px from the highlighted point to the top left corner of the view. Default (0, 0). */
    override var offset = MPPointF()

    private val drawingOffset = MPPointF()

    private var weakChart: WeakReference<Chart<*>>? = null

    init {
        val inflated = LayoutInflater.from(getContext()).inflate(layoutResource, this)
        inflated.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        inflated.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        inflated.layout(0, 0, inflated.measuredWidth, inflated.measuredHeight)
    }

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

    /** Returns [offset], shifted where needed so the view stays inside the bounds of [chartView]. */
    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        val offset = offset
        drawingOffset.x = offset.x
        drawingOffset.y = offset.y

        val chart = chartView
        val width = width.toFloat()
        val height = height.toFloat()

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

    /** Measures and lays the view out again. Override it to update the child views first, then call super. */
    override fun refreshContent(e: Entry<*>, highlight: Highlight) {
        measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        layout(0, 0, measuredWidth, measuredHeight)
    }

    /** Draws the view translated by [getOffsetForDrawingAtPoint]. */
    override fun draw(canvas: Canvas, posX: Float, posY: Float) {
        val offset = getOffsetForDrawingAtPoint(posX, posY)
        val saveId = canvas.save()
        canvas.translate(posX + offset.x, posY + offset.y)
        draw(canvas)
        canvas.restoreToCount(saveId)
    }
}
