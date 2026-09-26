package com.github.mikephil.charting.devicetest

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.utils.MPPointD
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** One rectangle a renderer drew: its horizontal span in pixels and the color it was painted with. */
data class RectDraw(val left: Float, val right: Float, val color: Int)

/** One text a renderer drew: what it said and the pixel position it was drawn at. */
data class TextDraw(val text: String, val x: Float, val y: Float)

/**
 * The colors of each bar [chart] drew into this canvas, bottom section first, keyed by the x value of the bar.
 * Rectangles without width are the room a short stack leaves unused and are left out.
 */
fun RecordingCanvas.barColorsByX(chart: BarLineChartBase<*>): Map<Int, List<Int>> {
    val transformer = chart.getTransformer(YAxis.AxisDependency.LEFT)
    val point = MPPointD.getInstance(0.0, 0.0)
    val byBar = LinkedHashMap<Int, MutableList<Int>>()

    for (rect in rects) {
        if (rect.left == rect.right) continue

        transformer.getValuesByTouchPoint((rect.left + rect.right) / 2f, 0f, point)
        byBar.getOrPut(point.x.roundToInt()) { mutableListOf() }.add(rect.color)
    }

    MPPointD.recycleInstance(point)
    return byBar
}

/** Canvas that records what the renderers asked it to draw, so tests can assert on draw calls. */
class RecordingCanvas(bitmap: Bitmap) : Canvas(bitmap) {

    var bitmapDraws = 0
        private set

    /** Every rectangle that was drawn from four coordinates, which is how the bar renderers draw a bar. */
    val rects = mutableListOf<RectDraw>()

    val lineColors = mutableListOf<Int>()

    /** Top left corner of every bitmap that was drawn, in the order the renderer drew them. */
    val bitmapCorners = mutableListOf<Pair<Float, Float>>()

    /** Center of every circle that was drawn, in the order the renderer drew them. */
    val circleCenters = mutableListOf<Pair<Float, Float>>()

    /** Radius in pixels of every circle that was drawn, in the same order as [circleCenters]. */
    val circleRadii = mutableListOf<Float>()

    /** Every text that was drawn, in the order the renderer drew it. */
    val texts = mutableListOf<TextDraw>()

    /** How many floats the renderers passed to `drawLines`, which is four per segment. */
    var lineFloatsDrawn = 0
        private set

    /** Smallest y passed to `drawLines`, which is the top of everything the renderers drew with it. */
    var topLineY = Float.POSITIVE_INFINITY
        private set

    /** Largest y passed to `drawLines`, which is the bottom of everything the renderers drew with it. */
    var bottomLineY = Float.NEGATIVE_INFINITY
        private set

    override fun drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        rects.add(RectDraw(left, right, paint.color))
        super.drawRect(left, top, right, bottom, paint)
    }

    override fun drawCircle(cx: Float, cy: Float, radius: Float, paint: Paint) {
        circleCenters.add(cx to cy)
        circleRadii.add(radius)
        super.drawCircle(cx, cy, radius, paint)
    }

    override fun drawText(text: String, x: Float, y: Float, paint: Paint) {
        texts.add(TextDraw(text, x, y))
        super.drawText(text, x, y, paint)
    }

    override fun drawBitmap(bitmap: Bitmap, left: Float, top: Float, paint: Paint?) {
        bitmapDraws++
        bitmapCorners.add(left to top)
        super.drawBitmap(bitmap, left, top, paint)
    }

    override fun drawLines(pts: FloatArray, offset: Int, count: Int, paint: Paint) {
        lineFloatsDrawn += count
        lineColors.add(paint.color)

        var y = offset + 1
        while (y < offset + count) {
            topLineY = min(topLineY, pts[y])
            bottomLineY = max(bottomLineY, pts[y])
            y += 2
        }

        super.drawLines(pts, offset, count, paint)
    }
}
