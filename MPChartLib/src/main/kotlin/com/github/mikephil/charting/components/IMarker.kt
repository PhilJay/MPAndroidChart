package com.github.mikephil.charting.components

import android.graphics.Canvas
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

/**
 * Something drawn at a highlighted value, such as a popup with details. Assign it to
 * [com.github.mikephil.charting.charts.Chart.marker]; the chart then calls [refreshContent] followed by [draw] for
 * every highlighted entry. Positions and offsets are in px on the chart view.
 */
interface IMarker {

    /** Offset in px from the highlighted point to the top left corner of the marker. Use -width / 2 and -height / 2 to center it. */
    val offset: MPPointF

    /**
     * Offset to use when drawing at the given point, so the marker can adjust its position case by case, for example to
     * stay inside the chart. Return [offset] when no adjustment is needed.
     * @param posX x position in px at which the marker is about to be drawn
     * @param posY y position in px at which the marker is about to be drawn
     */
    fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF

    /**
     * Updates the content before the marker is drawn.
     * @param e the highlighted entry; cast it to the chart's entry type such as [com.github.mikephil.charting.data.BarEntry] if needed
     * @param highlight details of the highlight such as the data set index and, for stacked bars, the stack index
     */
    fun refreshContent(e: Entry<*>, highlight: Highlight)

    /**
     * Draws the marker.
     * @param canvas the chart's canvas
     * @param posX x position of the highlighted point in px
     * @param posY y position of the highlighted point in px
     */
    fun draw(canvas: Canvas, posX: Float, posY: Float)
}
