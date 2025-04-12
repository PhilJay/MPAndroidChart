package com.github.mikephil.charting.utils

import android.graphics.Matrix
import android.graphics.RectF
import android.view.View
import kotlin.math.max
import kotlin.math.min

/**
 * Class that contains information about the charts current viewport settings, including offsets, scale & translation levels, ...
 * Constructor - don't forget calling setChartDimens(...)
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class ViewPortHandler {
    /**
     * Returns the charts-touch matrix used for translation and scale on touch.
     */
    val matrixTouch: Matrix = Matrix()

    /**
     * this rectangle defines the area in which graph values can be drawn
     */
    var contentRect: RectF = RectF()
        protected set

    var chartWidth: Float = 0f
        protected set
    var chartHeight: Float = 0f
        protected set

    /**
     * minimum scale value on the y-axis
     */
    var minScaleY: Float = 1f
        private set

    /**
     * maximum scale value on the y-axis
     */
    var maxScaleY: Float = Float.MAX_VALUE
        private set

    /**
     * minimum scale value on the x-axis
     */
    var minScaleX: Float = 1f
        private set

    /**
     * maximum scale value on the x-axis
     */
    var maxScaleX: Float = Float.MAX_VALUE
        private set

    /**
     * contains the current scale factor of the x-axis
     */
    var scaleX: Float = 1f
        private set

    /**
     * returns the current y-scale factor
     */
    var scaleY: Float = 1f
        private set

    /**
     * Returns the translation (drag / pan) distance on the x-axis
     */
    var transX: Float = 0f
        private set

    /**
     * Returns the translation (drag / pan) distance on the y-axis
     */
    var transY: Float = 0f
        private set

    /**
     * offset that allows the chart to be dragged over its bounds on the x-axis
     */
    private var transOffsetX = 0f

    /**
     * offset that allows the chart to be dragged over its bounds on the x-axis
     */
    private var transOffsetY = 0f

    /**
     * Sets the width and height of the chart.
     */
    fun setChartDimens(width: Float, height: Float) {
        val offsetLeft = this.offsetLeft()
        val offsetTop = this.offsetTop()
        val offsetRight = this.offsetRight()
        val offsetBottom = this.offsetBottom()

        chartHeight = height
        chartWidth = width

        restrainViewPort(offsetLeft, offsetTop, offsetRight, offsetBottom)
    }

    fun hasChartDimens(): Boolean = chartHeight > 0 && chartWidth > 0

    fun restrainViewPort(offsetLeft: Float, offsetTop: Float, offsetRight: Float, offsetBottom: Float) {
        contentRect[offsetLeft, offsetTop, chartWidth - offsetRight] = (chartHeight - offsetBottom)
    }

    fun offsetLeft(): Float = contentRect.left

    fun offsetRight(): Float = chartWidth - contentRect.right


    fun offsetTop(): Float = contentRect.top

    fun offsetBottom(): Float = chartHeight - contentRect.bottom

    fun contentTop(): Float = contentRect.top

    fun contentLeft(): Float = contentRect.left

    fun contentRight(): Float = contentRect.right

    fun contentBottom(): Float = contentRect.bottom

    fun contentWidth(): Float = contentRect.width()

    fun contentHeight(): Float = contentRect.height()

    val contentCenter: MPPointF
        get() = MPPointF.getInstance(contentRect.centerX(), contentRect.centerY())

    /**
     * Returns the smallest extension of the content rect (width or height).
     */
    val smallestContentExtension: Float
        get() = min(contentRect.width().toDouble(), contentRect.height().toDouble()).toFloat()

    /** CODE BELOW THIS RELATED TO SCALING AND GESTURES  */
    /**
     * Zooms in by 1.4f, x and y are the coordinates (in pixels) of the zoom
     * center.
     */
    fun zoomIn(x: Float, y: Float): Matrix {
        val save = Matrix()
        zoomIn(x, y, save)
        return save
    }

    fun zoomIn(x: Float, y: Float, outputMatrix: Matrix) {
        outputMatrix.reset()
        outputMatrix.set(matrixTouch)
        outputMatrix.postScale(1.4f, 1.4f, x, y)
    }

    /**
     * Zooms out by 0.7f, x and y are the coordinates (in pixels) of the zoom
     * center.
     */
    fun zoomOut(x: Float, y: Float): Matrix {
        val save = Matrix()
        zoomOut(x, y, save)
        return save
    }

    fun zoomOut(x: Float, y: Float, outputMatrix: Matrix) {
        outputMatrix.reset()
        outputMatrix.set(matrixTouch)
        outputMatrix.postScale(0.7f, 0.7f, x, y)
    }

    /**
     * Zooms out to original size.
     * @param outputMatrix
     */
    fun resetZoom(outputMatrix: Matrix) {
        outputMatrix.reset()
        outputMatrix.set(matrixTouch)
        outputMatrix.postScale(1.0f, 1.0f, 0.0f, 0.0f)
    }

    /**
     * Post-scales by the specified scale factors.
     *
     * @param scaleX
     * @param scaleY
     * @return
     */
    fun zoom(scaleX: Float, scaleY: Float): Matrix {
        val save = Matrix()
        zoom(scaleX, scaleY, save)
        return save
    }

    fun zoom(scaleX: Float, scaleY: Float, outputMatrix: Matrix) {
        outputMatrix.reset()
        outputMatrix.set(matrixTouch)
        outputMatrix.postScale(scaleX, scaleY)
    }

    /**
     * Post-scales by the specified scale factors. x and y is pivot.
     *
     * @param scaleX
     * @param scaleY
     * @param x
     * @param y
     * @return
     */
    fun zoom(scaleX: Float, scaleY: Float, x: Float, y: Float): Matrix {
        val save = Matrix()
        zoom(scaleX, scaleY, x, y, save)
        return save
    }

    fun zoom(scaleX: Float, scaleY: Float, x: Float, y: Float, outputMatrix: Matrix) {
        outputMatrix.reset()
        outputMatrix.set(matrixTouch)
        outputMatrix.postScale(scaleX, scaleY, x, y)
    }

    /**
     * Sets the scale factor to the specified values.
     *
     * @param scaleX
     * @param scaleY
     * @return
     */
    fun setZoom(scaleX: Float, scaleY: Float): Matrix {
        val save = Matrix()
        setZoom(scaleX, scaleY, save)
        return save
    }

    fun setZoom(scaleX: Float, scaleY: Float, outputMatrix: Matrix) {
        outputMatrix.reset()
        outputMatrix.set(matrixTouch)
        outputMatrix.setScale(scaleX, scaleY)
    }

    /**
     * Sets the scale factor to the specified values. x and y is pivot.
     */
    fun setZoom(scaleX: Float, scaleY: Float, x: Float, y: Float): Matrix {
        val save = Matrix()
        save.set(matrixTouch)

        save.setScale(scaleX, scaleY, x, y)

        return save
    }

    protected var valsBufferForFitScreen: FloatArray = FloatArray(9)

    /**
     * Resets all zooming and dragging and makes the chart fit exactly it's
     * bounds.
     */
    fun fitScreen(): Matrix {
        val save = Matrix()
        fitScreen(save)
        return save
    }

    /**
     * Resets all zooming and dragging and makes the chart fit exactly it's
     * bounds.  Output Matrix is available for those who wish to cache the object.
     */
    fun fitScreen(outputMatrix: Matrix) {
        minScaleX = 1f
        minScaleY = 1f

        outputMatrix.set(matrixTouch)

        val vals = valsBufferForFitScreen
        for (i in 0..8) {
            vals[i] = 0f
        }

        outputMatrix.getValues(vals)

        // reset all translations and scaling
        vals[Matrix.MTRANS_X] = 0f
        vals[Matrix.MTRANS_Y] = 0f
        vals[Matrix.MSCALE_X] = 1f
        vals[Matrix.MSCALE_Y] = 1f

        outputMatrix.setValues(vals)
    }

    /**
     * Post-translates to the specified points.  Less Performant.
     *
     * @param transformedPts
     * @return
     */
    fun translate(transformedPts: FloatArray): Matrix {
        val save = Matrix()
        translate(transformedPts, save)
        return save
    }

    /**
     * Post-translates to the specified points.  Output matrix allows for caching objects.
     *
     * @param transformedPts
     * @return
     */
    fun translate(transformedPts: FloatArray, outputMatrix: Matrix) {
        outputMatrix.reset()
        outputMatrix.set(matrixTouch)
        val x = transformedPts[0] - offsetLeft()
        val y = transformedPts[1] - offsetTop()
        outputMatrix.postTranslate(-x, -y)
    }

    protected var mCenterViewPortMatrixBuffer: Matrix = Matrix()

    /**
     * Centers the viewport around the specified position (x-index and y-value)
     * in the chart. Centering the viewport outside the bounds of the chart is
     * not possible. Makes most sense in combination with the
     * setScaleMinima(...) method.
     *
     * @param transformedPts the position to center view viewport to
     * @param view
     * @return save
     */
    fun centerViewPort(transformedPts: FloatArray, view: View) {
        val save = mCenterViewPortMatrixBuffer
        save.reset()
        save.set(matrixTouch)

        val x = transformedPts[0] - offsetLeft()
        val y = transformedPts[1] - offsetTop()

        save.postTranslate(-x, -y)

        refresh(save, view, true)
    }

    /**
     * buffer for storing the 9 matrix values of a 3x3 matrix
     */
    protected val matrixBuffer: FloatArray = FloatArray(9)

    /**
     * call this method to refresh the graph with a given matrix
     *
     * @param newMatrix
     * @return
     */
    fun refresh(newMatrix: Matrix, chart: View, invalidate: Boolean): Matrix {
        matrixTouch.set(newMatrix)

        // make sure scale and translation are within their bounds
        limitTransAndScale(matrixTouch, contentRect)

        if (invalidate) chart.invalidate()

        newMatrix.set(matrixTouch)
        return newMatrix
    }

    /**
     * limits the maximum scale and X translation of the given matrix
     *
     * @param matrix
     */
    fun limitTransAndScale(matrix: Matrix, content: RectF?) {
        matrix.getValues(matrixBuffer)

        val curTransX = matrixBuffer[Matrix.MTRANS_X]
        val curScaleX = matrixBuffer[Matrix.MSCALE_X]

        val curTransY = matrixBuffer[Matrix.MTRANS_Y]
        val curScaleY = matrixBuffer[Matrix.MSCALE_Y]

        // min scale-x is 1f
        scaleX = min(max(minScaleX.toDouble(), curScaleX.toDouble()), maxScaleX.toDouble()).toFloat()

        // min scale-y is 1f
        scaleY = min(max(minScaleY.toDouble(), curScaleY.toDouble()), maxScaleY.toDouble()).toFloat()

        var width = 0f
        var height = 0f

        if (content != null) {
            width = content.width()
            height = content.height()
        }

        val maxTransX = -width * (scaleX - 1f)
        transX = min(max(curTransX.toDouble(), (maxTransX - transOffsetX).toDouble()), transOffsetX.toDouble()).toFloat()

        val maxTransY = height * (scaleY - 1f)
        transY = max(min(curTransY.toDouble(), (maxTransY + transOffsetY).toDouble()), -transOffsetY.toDouble()).toFloat()

        matrixBuffer[Matrix.MTRANS_X] = transX
        matrixBuffer[Matrix.MSCALE_X] = scaleX

        matrixBuffer[Matrix.MTRANS_Y] = transY
        matrixBuffer[Matrix.MSCALE_Y] = scaleY

        matrix.setValues(matrixBuffer)
    }

    /**
     * Sets the minimum scale factor for the x-axis
     */
    fun setMinimumScaleX(xScale: Float) {
        var xScaleLocal = xScale
        if (xScaleLocal < 1f)
            xScaleLocal = 1f

        minScaleX = xScaleLocal

        limitTransAndScale(matrixTouch, contentRect)
    }

    /**
     * Sets the maximum scale factor for the x-axis
     */
    fun setMaximumScaleX(xScale: Float) {
        var xScaleLocal = xScale
        if (xScaleLocal == 0f)
            xScaleLocal = Float.MAX_VALUE

        maxScaleX = xScaleLocal

        limitTransAndScale(matrixTouch, contentRect)
    }

    /**
     * Sets the minimum and maximum scale factors for the x-axis
     *
     * @param minScaleX
     * @param maxScaleX
     */
    fun setMinMaxScaleX(minScaleX: Float, maxScaleX: Float) {
        var minScaleXLocal = minScaleX
        var maxScaleXLocal = maxScaleX
        if (minScaleXLocal < 1f) minScaleXLocal = 1f

        if (maxScaleXLocal == 0f)
            maxScaleXLocal = Float.MAX_VALUE

        this.minScaleX = minScaleXLocal
        this.maxScaleX = maxScaleXLocal

        limitTransAndScale(matrixTouch, contentRect)
    }

    /**
     * Sets the minimum scale factor for the y-axis
     */
    fun setMinimumScaleY(yScale: Float) {
        var yScaleLocal = yScale
        if (yScaleLocal < 1f)
            yScaleLocal = 1f

        minScaleY = yScaleLocal

        limitTransAndScale(matrixTouch, contentRect)
    }

    /**
     * Sets the maximum scale factor for the y-axis
     */
    fun setMaximumScaleY(yScale: Float) {
        var yScaleLocal = yScale
        if (yScaleLocal == 0f)
            yScaleLocal = Float.MAX_VALUE

        maxScaleY = yScaleLocal

        limitTransAndScale(matrixTouch, contentRect)
    }

    fun setMinMaxScaleY(minScaleY: Float, maxScaleY: Float) {
        var minScaleYLocal = minScaleY
        var maxScaleYLocal = maxScaleY
        if (minScaleYLocal < 1f) minScaleYLocal = 1f

        if (maxScaleYLocal == 0f) maxScaleYLocal = Float.MAX_VALUE

        this.minScaleY = minScaleYLocal
        this.maxScaleY = maxScaleYLocal

        limitTransAndScale(matrixTouch, contentRect)
    }

    /**
     * BELOW METHODS FOR BOUNDS CHECK
     */
    fun isInBoundsX(x: Float): Boolean = isInBoundsLeft(x) && isInBoundsRight(x)

    fun isInBoundsY(y: Float): Boolean = isInBoundsTop(y) && isInBoundsBottom(y)

    fun isInBounds(x: Float, y: Float): Boolean = isInBoundsX(x) && isInBoundsY(y)

    fun isInBoundsLeft(x: Float): Boolean = contentRect.left <= x + 1

    fun isInBoundsRight(x: Float): Boolean {
        var xLocal = x
        xLocal = ((xLocal * 100f).toInt()).toFloat() / 100f
        return contentRect.right >= xLocal - 1
    }

    fun isInBoundsTop(y: Float): Boolean = contentRect.top <= y

    fun isInBoundsBottom(y: Float): Boolean {
        var yLocal = y
        yLocal = ((yLocal * 100f).toInt()).toFloat() / 100f
        return contentRect.bottom >= yLocal
    }

    /**
     * if the chart is fully zoomed out, return true
     */
    val isFullyZoomedOut: Boolean
        get() = isFullyZoomedOutX && isFullyZoomedOutY

    /**
     * Returns true if the chart is fully zoomed out on it's y-axis (vertical).
     */
    val isFullyZoomedOutY: Boolean
        get() = !(scaleY > minScaleY || minScaleY > 1f)

    /**
     * Returns true if the chart is fully zoomed out on it's x-axis
     * (horizontal).
     */
    val isFullyZoomedOutX: Boolean
        get() = !(scaleX > minScaleX || minScaleX > 1f)

    /**
     * Set an offset in dp that allows the user to drag the chart over it's
     * bounds on the x-axis.
     */
    fun setDragOffsetX(offset: Float) {
        transOffsetX = Utils.convertDpToPixel(offset)
    }

    /**
     * Set an offset in dp that allows the user to drag the chart over it's
     * bounds on the y-axis.
     */
    fun setDragOffsetY(offset: Float) {
        transOffsetY = Utils.convertDpToPixel(offset)
    }

    /**
     * Returns true if both drag offsets (x and y) are zero or smaller.
     */
    fun hasNoDragOffset(): Boolean = transOffsetX <= 0 && transOffsetY <= 0

    /**
     * Returns true if the chart is not yet fully zoomed out on the x-axis
     *
     * @return
     */
    fun canZoomOutMoreX(): Boolean = scaleX > minScaleX

    /**
     * Returns true if the chart is not yet fully zoomed in on the x-axis
     */
    fun canZoomInMoreX(): Boolean = scaleX < maxScaleX

    /**
     * Returns true if the chart is not yet fully zoomed out on the y-axis
     */
    fun canZoomOutMoreY(): Boolean = scaleY > minScaleY

    /**
     * Returns true if the chart is not yet fully zoomed in on the y-axis
     */
    fun canZoomInMoreY(): Boolean = scaleY < maxScaleY
}
