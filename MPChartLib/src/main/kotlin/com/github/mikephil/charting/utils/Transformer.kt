package com.github.mikephil.charting.utils

import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet

/**
 * Converts chart values to pixel positions and back for one y axis of a bar, line, scatter, candle
 * or bubble chart.
 *
 * Three matrices are applied in order: the value matrix from [prepareMatrixValuePx], the touch
 * matrix of the [ViewPortHandler] holding zoom and drag, and the offset matrix from [prepareMatrixOffset].
 * Functions without an output parameter change the passed array, path or rectangle in place.
 * The charts call the prepare functions themselves; use [com.github.mikephil.charting.charts.BarLineChartBase.getTransformer]
 * to get the transformer of an axis.
 */
open class Transformer(protected val viewPortHandler: ViewPortHandler) {

    /** Matrix that scales chart values to unzoomed pixel distances. */
    protected val matrixValueToPx = Matrix()

    /** Matrix that moves the scaled values into the content rectangle and flips the y axis. */
    protected val matrixOffset = Matrix()

    /**
     * Builds the value matrix from the visible value range and the content size.
     *
     * @param xChartMin the smallest x value of the chart.
     * @param deltaX the x range of the chart.
     * @param deltaY the y range of the axis.
     * @param yChartMin the smallest y value of the axis.
     */
    fun prepareMatrixValuePx(xChartMin: Float, deltaX: Float, deltaY: Float, yChartMin: Float) {
        var scaleX = viewPortHandler.contentWidth / deltaX
        var scaleY = viewPortHandler.contentHeight / deltaY

        if (scaleX.isInfinite()) scaleX = 0f
        if (scaleY.isInfinite()) scaleY = 0f

        matrixValueToPx.reset()
        matrixValueToPx.postTranslate(-xChartMin, -yChartMin)
        matrixValueToPx.postScale(scaleX, -scaleY)
    }

    /**
     * Builds the offset matrix from the current content offsets.
     *
     * @param inverted true if the y axis is inverted, so larger values are drawn further down.
     */
    open fun prepareMatrixOffset(inverted: Boolean) {
        matrixOffset.reset()
        if (!inverted) {
            matrixOffset.postTranslate(viewPortHandler.offsetLeft, viewPortHandler.chartHeight - viewPortHandler.offsetBottom)
        } else {
            matrixOffset.setTranslate(viewPortHandler.offsetLeft, -viewPortHandler.offsetTop)
            matrixOffset.postScale(1.0f, -1.0f)
        }
    }

    private var valuePointsForGenerateTransformedValuesScatter = FloatArray(1)

    /**
     * Converts the entries of a scatter data set to pixel positions as x, y pairs.
     *
     * @param phaseX animation progress on the x axis from 0 to 1, limits how many entries are included.
     * @param phaseY animation progress on the y axis from 0 to 1, y values are multiplied by it.
     * @param from index of the first entry.
     * @param to index of the last entry.
     * @return a buffer reused between calls; do not keep a reference to it.
     */
    fun generateTransformedValuesScatter(data: IScatterDataSet<*>, phaseX: Float, phaseY: Float, from: Int, to: Int): FloatArray {
        val count = (((to - from) * phaseX + 1).toInt() * 2).coerceAtLeast(2)
        if (valuePointsForGenerateTransformedValuesScatter.size != count) {
            valuePointsForGenerateTransformedValuesScatter = FloatArray(count)
        }
        val valuePoints = valuePointsForGenerateTransformedValuesScatter
        for (j in 0 until count step 2) {
            val e = data.getEntryForIndex(j / 2 + from)
            valuePoints[j] = e.x
            valuePoints[j + 1] = e.y * phaseY
        }
        valueToPixelMatrix.mapPoints(valuePoints)
        return valuePoints
    }

    private var valuePointsForGenerateTransformedValuesBubble = FloatArray(1)

    /**
     * Converts the entries of a bubble data set to pixel positions as x, y pairs.
     *
     * @param phaseY animation progress on the y axis from 0 to 1, y values are multiplied by it.
     * @param from index of the first entry.
     * @param to index of the last entry.
     * @return a buffer reused between calls; do not keep a reference to it.
     */
    fun generateTransformedValuesBubble(data: IBubbleDataSet<*>, phaseY: Float, from: Int, to: Int): FloatArray {
        val count = ((to - from + 1) * 2).coerceAtLeast(2)
        if (valuePointsForGenerateTransformedValuesBubble.size != count) {
            valuePointsForGenerateTransformedValuesBubble = FloatArray(count)
        }
        val valuePoints = valuePointsForGenerateTransformedValuesBubble
        for (j in 0 until count step 2) {
            val e = data.getEntryForIndex(j / 2 + from)
            valuePoints[j] = e.x
            valuePoints[j + 1] = e.y * phaseY
        }
        valueToPixelMatrix.mapPoints(valuePoints)
        return valuePoints
    }

    private var valuePointsForGenerateTransformedValuesLine = FloatArray(1)

    /**
     * Converts the entries of a line data set to pixel positions as x, y pairs.
     *
     * @param phaseX animation progress on the x axis from 0 to 1, limits how many entries are included.
     * @param phaseY animation progress on the y axis from 0 to 1, y values are multiplied by it.
     * @param min index of the first entry.
     * @param max index of the last entry.
     * @return a buffer reused between calls; do not keep a reference to it.
     */
    fun generateTransformedValuesLine(data: ILineDataSet<*>, phaseX: Float, phaseY: Float, min: Int, max: Int): FloatArray {
        val count = ((((max - min) * phaseX).toInt() + 1) * 2).coerceAtLeast(2)
        if (valuePointsForGenerateTransformedValuesLine.size != count) {
            valuePointsForGenerateTransformedValuesLine = FloatArray(count)
        }
        val valuePoints = valuePointsForGenerateTransformedValuesLine
        for (j in 0 until count step 2) {
            val e = data.getEntryForIndex(j / 2 + min)
            valuePoints[j] = e.x
            valuePoints[j + 1] = e.y * phaseY
        }
        valueToPixelMatrix.mapPoints(valuePoints)
        return valuePoints
    }

    private var valuePointsForGenerateTransformedValuesCandle = FloatArray(1)

    /**
     * Converts the entries of a candle data set to pixel positions as x, high pairs.
     *
     * @param phaseX animation progress on the x axis from 0 to 1, limits how many entries are included.
     * @param phaseY animation progress on the y axis from 0 to 1, high values are multiplied by it.
     * @param from index of the first entry.
     * @param to index of the last entry.
     * @return a buffer reused between calls; do not keep a reference to it.
     */
    fun generateTransformedValuesCandle(data: ICandleDataSet<*>, phaseX: Float, phaseY: Float, from: Int, to: Int): FloatArray {
        val count = (((to - from) * phaseX + 1).toInt() * 2).coerceAtLeast(2)
        if (valuePointsForGenerateTransformedValuesCandle.size != count) {
            valuePointsForGenerateTransformedValuesCandle = FloatArray(count)
        }
        val valuePoints = valuePointsForGenerateTransformedValuesCandle
        for (j in 0 until count step 2) {
            val e = data.getEntryForIndex(j / 2 + from)
            valuePoints[j] = e.x
            valuePoints[j + 1] = e.high * phaseY
        }
        valueToPixelMatrix.mapPoints(valuePoints)
        return valuePoints
    }

    /** Converts a path built from chart values to pixel space in place. */
    fun pathValueToPixel(path: Path) {
        path.transform(matrixValueToPx)
        path.transform(viewPortHandler.matrixTouch)
        path.transform(matrixOffset)
    }

    /** Converts several paths built from chart values to pixel space in place. */
    fun pathValuesToPixel(paths: List<Path>) {
        for (path in paths) pathValueToPixel(path)
    }

    /**
     * Converts chart values to pixel positions in place.
     *
     * @param pts x, y pairs in value space; replaced by pixel positions.
     */
    fun pointValuesToPixel(pts: FloatArray) {
        matrixValueToPx.mapPoints(pts)
        viewPortHandler.matrixTouch.mapPoints(pts)
        matrixOffset.mapPoints(pts)
    }

    /**
     * Converts the first [count] floats of [pts] to pixel positions in place and leaves the rest untouched, so a
     * buffer that is longer than the data in it can be converted in one pass.
     *
     * @param pts x, y pairs in value space; the first [count] floats are replaced by pixel positions.
     * @param count how many floats to convert, an even number no larger than the size of [pts].
     */
    fun pointValuesToPixel(pts: FloatArray, count: Int) {
        val points = count / 2
        matrixValueToPx.mapPoints(pts, 0, pts, 0, points)
        viewPortHandler.matrixTouch.mapPoints(pts, 0, pts, 0, points)
        matrixOffset.mapPoints(pts, 0, pts, 0, points)
    }

    /** Converts a rectangle in value space to pixel space in place. */
    fun rectValueToPixel(r: RectF) {
        matrixValueToPx.mapRect(r)
        viewPortHandler.matrixTouch.mapRect(r)
        matrixOffset.mapRect(r)
    }

    /**
     * Multiplies the top and bottom of [r] by the y animation phase, then converts it to pixel space in place.
     * Used for vertical bars.
     */
    fun rectToPixelPhase(r: RectF, phaseY: Float) {
        r.top *= phaseY
        r.bottom *= phaseY
        rectValueToPixel(r)
    }

    /**
     * Multiplies the left and right of [r] by the y animation phase, then converts it to pixel space in place.
     * Used for horizontal bars, where the value runs along the x axis.
     */
    fun rectToPixelPhaseHorizontal(r: RectF, phaseY: Float) {
        r.left *= phaseY
        r.right *= phaseY
        rectValueToPixel(r)
    }

    /** Converts a rectangle in value space to pixel space in place. Same as [rectValueToPixel]. */
    fun rectValueToPixelHorizontal(r: RectF) {
        rectValueToPixel(r)
    }

    /**
     * Multiplies the left and right of [r] by the y animation phase, then converts it to pixel space in place.
     * Same as [rectToPixelPhaseHorizontal].
     */
    fun rectValueToPixelHorizontal(r: RectF, phaseY: Float) {
        r.left *= phaseY
        r.right *= phaseY
        rectValueToPixel(r)
    }

    /** Converts several rectangles in value space to pixel space in place. */
    fun rectValuesToPixel(rects: List<RectF>) {
        val m = valueToPixelMatrix
        for (rect in rects) m.mapRect(rect)
    }

    private val pixelToValueMatrixBuffer = Matrix()

    /**
     * Converts pixel positions to chart values in place.
     *
     * @param pixels x, y pairs in pixels; replaced by values.
     */
    fun pixelsToValue(pixels: FloatArray) {
        val tmp = pixelToValueMatrixBuffer
        tmp.reset()
        matrixOffset.invert(tmp)
        tmp.mapPoints(pixels)
        viewPortHandler.matrixTouch.invert(tmp)
        tmp.mapPoints(pixels)
        matrixValueToPx.invert(tmp)
        tmp.mapPoints(pixels)
    }

    private val ptsBuffer = FloatArray(2)

    /**
     * Converts a pixel position, for example a touch, to the x and y value at that point.
     *
     * @return a new [MPPointD] that is not taken from the pool; it may still be given to [MPPointD.recycleInstance].
     */
    fun getValuesByTouchPoint(x: Float, y: Float): MPPointD {
        val result = MPPointD(0.0, 0.0)
        getValuesByTouchPoint(x, y, result)
        return result
    }

    /**
     * Converts a pixel position, for example a touch, to the x and y value at that point and writes it
     * into [outputPoint].
     */
    fun getValuesByTouchPoint(x: Float, y: Float, outputPoint: MPPointD) {
        ptsBuffer[0] = x
        ptsBuffer[1] = y
        pixelsToValue(ptsBuffer)
        outputPoint.x = ptsBuffer[0].toDouble()
        outputPoint.y = ptsBuffer[1].toDouble()
    }

    /**
     * Converts an x and y value to its pixel position.
     *
     * @return a new [MPPointD] that is not taken from the pool; it may still be given to [MPPointD.recycleInstance].
     */
    fun getPixelForValues(x: Float, y: Float): MPPointD {
        ptsBuffer[0] = x
        ptsBuffer[1] = y
        pointValuesToPixel(ptsBuffer)
        return MPPointD(ptsBuffer[0].toDouble(), ptsBuffer[1].toDouble())
    }

    /** The value matrix built by [prepareMatrixValuePx]. Do not modify it. */
    val valueMatrix: Matrix
        get() = matrixValueToPx

    /** The offset matrix built by [prepareMatrixOffset]. Do not modify it. */
    val offsetMatrix: Matrix
        get() = matrixOffset

    private val mBuffer1 = Matrix()

    /**
     * The combined value, touch and offset matrix that maps values to pixels.
     * Returns a buffer that is overwritten on the next access; do not keep a reference to it.
     */
    val valueToPixelMatrix: Matrix
        get() {
            mBuffer1.set(matrixValueToPx)
            mBuffer1.postConcat(viewPortHandler.matrixTouch)
            mBuffer1.postConcat(matrixOffset)
            return mBuffer1
        }

    private val mBuffer2 = Matrix()

    /**
     * The inverse of [valueToPixelMatrix], mapping pixels to values.
     * Returns a buffer that is overwritten on the next access; do not keep a reference to it.
     */
    val pixelToValueMatrix: Matrix
        get() {
            valueToPixelMatrix.invert(mBuffer2)
            return mBuffer2
        }
}
