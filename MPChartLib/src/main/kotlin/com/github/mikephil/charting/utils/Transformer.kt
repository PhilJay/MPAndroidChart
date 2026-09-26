package com.github.mikephil.charting.utils

import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF

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
public open class Transformer(protected val viewPortHandler: ViewPortHandler) {

    /** Matrix that scales chart values to unzoomed pixel distances. */
    protected val matrixValueToPx: Matrix = Matrix()

    /** Matrix that moves the scaled values into the content rectangle and flips the y axis. */
    protected val matrixOffset: Matrix = Matrix()

    /**
     * Builds the value matrix from the visible value range and the content size.
     *
     * @param xChartMin the smallest x value of the chart.
     * @param deltaX the x range of the chart.
     * @param deltaY the y range of the axis.
     * @param yChartMin the smallest y value of the axis.
     */
    public fun prepareMatrixValuePx(xChartMin: Float, deltaX: Float, deltaY: Float, yChartMin: Float) {
        var scaleX = viewPortHandler.contentWidth / deltaX
        var scaleY = viewPortHandler.contentHeight / deltaY

        if (!scaleX.isFinite()) scaleX = 0f
        if (!scaleY.isFinite()) scaleY = 0f

        matrixValueToPx.reset()
        matrixValueToPx.postTranslate(-xChartMin, -yChartMin)
        matrixValueToPx.postScale(scaleX, -scaleY)
    }

    /**
     * Builds the offset matrix from the current content offsets.
     *
     * @param inverted true if the y axis is inverted, so larger values are drawn further down.
     */
    public open fun prepareMatrixOffset(inverted: Boolean) {
        matrixOffset.reset()
        if (!inverted) {
            matrixOffset.postTranslate(viewPortHandler.offsetLeft, viewPortHandler.chartHeight - viewPortHandler.offsetBottom)
        } else {
            matrixOffset.setTranslate(viewPortHandler.offsetLeft, -viewPortHandler.offsetTop)
            matrixOffset.postScale(1.0f, -1.0f)
        }
    }

    /** Converts a path built from chart values to pixel space in place. */
    public fun pathValueToPixel(path: Path) {
        path.transform(matrixValueToPx)
        path.transform(viewPortHandler.matrixTouch)
        path.transform(matrixOffset)
    }

    /** Converts several paths built from chart values to pixel space in place. */
    public fun pathValuesToPixel(paths: List<Path>) {
        for (path in paths) pathValueToPixel(path)
    }

    /**
     * Converts chart values to pixel positions in place.
     *
     * @param pts x, y pairs in value space; replaced by pixel positions.
     */
    public fun pointValuesToPixel(pts: FloatArray) {
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
    public fun pointValuesToPixel(pts: FloatArray, count: Int) {
        val points = count / 2
        matrixValueToPx.mapPoints(pts, 0, pts, 0, points)
        viewPortHandler.matrixTouch.mapPoints(pts, 0, pts, 0, points)
        matrixOffset.mapPoints(pts, 0, pts, 0, points)
    }

    /** Converts a rectangle in value space to pixel space in place. */
    public fun rectValueToPixel(r: RectF) {
        matrixValueToPx.mapRect(r)
        viewPortHandler.matrixTouch.mapRect(r)
        matrixOffset.mapRect(r)
    }

    /**
     * Multiplies the top and bottom of [r] by the y animation phase, then converts it to pixel space in place.
     * Used for vertical bars.
     */
    public fun rectToPixelPhase(r: RectF, phaseY: Float) {
        r.top *= phaseY
        r.bottom *= phaseY
        rectValueToPixel(r)
    }

    /**
     * Multiplies the left and right of [r] by the y animation phase, then converts it to pixel space in place.
     * Used for horizontal bars, where the value runs along the x axis.
     */
    public fun rectToPixelPhaseHorizontal(r: RectF, phaseY: Float) {
        r.left *= phaseY
        r.right *= phaseY
        rectValueToPixel(r)
    }

    /** Converts a rectangle in value space to pixel space in place. Same as [rectValueToPixel]. */
    public fun rectValueToPixelHorizontal(r: RectF) {
        rectValueToPixel(r)
    }

    /**
     * Multiplies the left and right of [r] by the y animation phase, then converts it to pixel space in place.
     * Same as [rectToPixelPhaseHorizontal].
     */
    public fun rectValueToPixelHorizontal(r: RectF, phaseY: Float) {
        r.left *= phaseY
        r.right *= phaseY
        rectValueToPixel(r)
    }

    /** Converts several rectangles in value space to pixel space in place. */
    public fun rectValuesToPixel(rects: List<RectF>) {
        val m = valueToPixelMatrix
        for (rect in rects) m.mapRect(rect)
    }

    private val pixelToValueMatrixBuffer = Matrix()

    /**
     * Converts pixel positions to chart values in place.
     *
     * @param pixels x, y pairs in pixels; replaced by values.
     */
    public fun pixelsToValue(pixels: FloatArray) {
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
    public fun getValuesByTouchPoint(x: Float, y: Float): MPPointD {
        val result = MPPointD(0.0, 0.0)
        getValuesByTouchPoint(x, y, result)
        return result
    }

    /**
     * Converts a pixel position, for example a touch, to the x and y value at that point and writes it
     * into [outputPoint].
     */
    public fun getValuesByTouchPoint(x: Float, y: Float, outputPoint: MPPointD) {
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
    public fun getPixelForValues(x: Float, y: Float): MPPointD {
        ptsBuffer[0] = x
        ptsBuffer[1] = y
        pointValuesToPixel(ptsBuffer)
        return MPPointD(ptsBuffer[0].toDouble(), ptsBuffer[1].toDouble())
    }

    /** The value matrix built by [prepareMatrixValuePx]. Do not modify it. */
    public val valueMatrix: Matrix
        get() = matrixValueToPx

    /** The offset matrix built by [prepareMatrixOffset]. Do not modify it. */
    public val offsetMatrix: Matrix
        get() = matrixOffset

    private val mBuffer1 = Matrix()

    /**
     * The combined value, touch and offset matrix that maps values to pixels.
     * Returns a buffer that is overwritten on the next access; do not keep a reference to it.
     */
    public val valueToPixelMatrix: Matrix
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
    public val pixelToValueMatrix: Matrix
        get() {
            valueToPixelMatrix.invert(mBuffer2)
            return mBuffer2
        }
}
