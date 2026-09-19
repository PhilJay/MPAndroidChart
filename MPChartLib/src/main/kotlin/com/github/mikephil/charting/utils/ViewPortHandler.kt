package com.github.mikephil.charting.utils

import android.graphics.Matrix
import android.graphics.RectF
import android.view.View
import kotlin.math.max
import kotlin.math.min

/**
 * Keeps the current viewport of a chart: the chart size, the content rectangle the data is drawn in,
 * and the touch matrix that holds the current zoom and drag state with its limits.
 *
 * The content rectangle is the chart size minus the offsets for axes, legend and extra offsets.
 * All sizes and positions are in pixels. The functions that return a [Matrix] build a new matrix
 * from [matrixTouch] with the change applied; pass the result to [refresh] to make it take effect.
 * The overloads with an `outputMatrix` parameter write into the given matrix instead of allocating.
 */
open class ViewPortHandler {

    /** Matrix holding the current scale and translation applied by touch gestures and zoom calls. */
    val matrixTouch = Matrix()

    /** Rectangle in pixels in which the chart data is drawn, the chart area without the offsets. */
    val contentRect = RectF()

    /** Full width of the chart view in pixels. */
    var chartWidth = 0f
        protected set

    /** Full height of the chart view in pixels. */
    var chartHeight = 0f
        protected set

    /** Smallest allowed y scale factor, at least 1. Default 1. */
    var minScaleY = 1f
        private set

    /** Largest allowed y scale factor. Default [Float.MAX_VALUE], meaning unlimited. */
    var maxScaleY = Float.MAX_VALUE
        private set

    /** Smallest allowed x scale factor, at least 1. Default 1. */
    var minScaleX = 1f
        private set

    /** Largest allowed x scale factor. Default [Float.MAX_VALUE], meaning unlimited. */
    var maxScaleX = Float.MAX_VALUE
        private set

    /** Current x scale factor, 1 when not zoomed. Updated by [limitTransAndScale]. */
    var scaleX = 1f
        private set

    /** Current y scale factor, 1 when not zoomed. Updated by [limitTransAndScale]. */
    var scaleY = 1f
        private set

    /** Current drag translation on the x axis in pixels. Updated by [limitTransAndScale]. */
    var transX = 0f
        private set

    /** Current drag translation on the y axis in pixels. Updated by [limitTransAndScale]. */
    var transY = 0f
        private set

    /** Distance in dp the chart may be dragged beyond its data bounds on the x axis. Default 0. */
    var dragOffsetX = 0f

    /** Distance in dp the chart may be dragged beyond its data bounds on the y axis. Default 0. */
    var dragOffsetY = 0f

    /**
     * Sets the chart size in pixels and recomputes [contentRect] keeping the current offsets.
     */
    fun setChartDimens(width: Float, height: Float) {
        val offsetLeft = offsetLeft
        val offsetTop = offsetTop
        val offsetRight = offsetRight
        val offsetBottom = offsetBottom

        chartHeight = height
        chartWidth = width

        restrainViewPort(offsetLeft, offsetTop, offsetRight, offsetBottom)
    }

    /** True once the chart has a width and height greater than zero. */
    fun hasChartDimens(): Boolean = chartHeight > 0 && chartWidth > 0

    /**
     * Sets [contentRect] to the chart size minus the given offsets in pixels.
     */
    fun restrainViewPort(offsetLeft: Float, offsetTop: Float, offsetRight: Float, offsetBottom: Float) {
        contentRect.set(offsetLeft, offsetTop, chartWidth - offsetRight, chartHeight - offsetBottom)
    }

    /** Space in pixels between the left chart edge and the content rectangle. */
    val offsetLeft: Float
        get() = contentRect.left

    /** Space in pixels between the content rectangle and the right chart edge. */
    val offsetRight: Float
        get() = chartWidth - contentRect.right

    /** Space in pixels between the top chart edge and the content rectangle. */
    val offsetTop: Float
        get() = contentRect.top

    /** Space in pixels between the content rectangle and the bottom chart edge. */
    val offsetBottom: Float
        get() = chartHeight - contentRect.bottom

    /** Top edge of the content rectangle in pixels. */
    val contentTop: Float
        get() = contentRect.top

    /** Left edge of the content rectangle in pixels. */
    val contentLeft: Float
        get() = contentRect.left

    /** Right edge of the content rectangle in pixels. */
    val contentRight: Float
        get() = contentRect.right

    /** Bottom edge of the content rectangle in pixels. */
    val contentBottom: Float
        get() = contentRect.bottom

    /** Width of the content rectangle in pixels. */
    val contentWidth: Float
        get() = contentRect.width()

    /** Height of the content rectangle in pixels. */
    val contentHeight: Float
        get() = contentRect.height()

    /** Center of the content rectangle in pixels, as a new [MPPointF] that is not pooled. */
    val contentCenter: MPPointF
        get() = MPPointF(contentRect.centerX(), contentRect.centerY())

    /** The smaller of the content width and height in pixels. */
    val smallestContentExtension: Float
        get() = min(contentRect.width(), contentRect.height())

    /**
     * Returns a new matrix zoomed in by 1.4 around the pixel position ([x], [y]).
     */
    fun zoomIn(x: Float, y: Float): Matrix {
        val save = Matrix()
        zoomIn(x, y, save)
        return save
    }

    /**
     * Writes into [outputMatrix] the touch matrix zoomed in by 1.4 around the pixel position ([x], [y]).
     */
    fun zoomIn(x: Float, y: Float, outputMatrix: Matrix) {
        outputMatrix.reset()
        outputMatrix.set(matrixTouch)
        outputMatrix.postScale(1.4f, 1.4f, x, y)
    }

    /**
     * Returns a new matrix zoomed out by 0.7 around the pixel position ([x], [y]).
     */
    fun zoomOut(x: Float, y: Float): Matrix {
        val save = Matrix()
        zoomOut(x, y, save)
        return save
    }

    /**
     * Writes into [outputMatrix] the touch matrix zoomed out by 0.7 around the pixel position ([x], [y]).
     */
    fun zoomOut(x: Float, y: Float, outputMatrix: Matrix) {
        outputMatrix.reset()
        outputMatrix.set(matrixTouch)
        outputMatrix.postScale(0.7f, 0.7f, x, y)
    }

    /**
     * Writes the current touch matrix into [outputMatrix] with the scale taken back to 1 around the content
     * center, keeping the translation. [fitScreen] also resets the scale minima.
     */
    fun resetZoom(outputMatrix: Matrix) {
        outputMatrix.reset()
        outputMatrix.set(matrixTouch)
        outputMatrix.postScale(1f / scaleX, 1f / scaleY, contentRect.centerX(), contentRect.centerY())
    }

    /**
     * Returns a new matrix with the touch matrix scaled by the given factors around the origin.
     */
    fun zoom(scaleX: Float, scaleY: Float): Matrix {
        val save = Matrix()
        zoom(scaleX, scaleY, save)
        return save
    }

    /**
     * Writes into [outputMatrix] the touch matrix scaled by the given factors around the origin.
     */
    fun zoom(scaleX: Float, scaleY: Float, outputMatrix: Matrix) {
        outputMatrix.reset()
        outputMatrix.set(matrixTouch)
        outputMatrix.postScale(scaleX, scaleY)
    }

    /**
     * Returns a new matrix with the touch matrix scaled by the given factors around the pixel position ([x], [y]).
     */
    fun zoom(scaleX: Float, scaleY: Float, x: Float, y: Float): Matrix {
        val save = Matrix()
        zoom(scaleX, scaleY, x, y, save)
        return save
    }

    /**
     * Writes into [outputMatrix] the touch matrix scaled by the given factors around the pixel position ([x], [y]).
     */
    fun zoom(scaleX: Float, scaleY: Float, x: Float, y: Float, outputMatrix: Matrix) {
        outputMatrix.reset()
        outputMatrix.set(matrixTouch)
        outputMatrix.postScale(scaleX, scaleY, x, y)
    }

    /**
     * Returns a new matrix that holds only the given absolute scale factors; the current translation is dropped.
     */
    fun setZoom(scaleX: Float, scaleY: Float): Matrix {
        val save = Matrix()
        setZoom(scaleX, scaleY, save)
        return save
    }

    /**
     * Writes into [outputMatrix] a matrix that holds only the given absolute scale factors; the current
     * translation is dropped.
     */
    fun setZoom(scaleX: Float, scaleY: Float, outputMatrix: Matrix) {
        outputMatrix.reset()
        outputMatrix.set(matrixTouch)
        outputMatrix.setScale(scaleX, scaleY)
    }

    /**
     * Returns a new matrix that holds only the given absolute scale factors around the pixel position ([x], [y]);
     * the current translation is dropped.
     */
    fun setZoom(scaleX: Float, scaleY: Float, x: Float, y: Float): Matrix {
        val save = Matrix()
        save.set(matrixTouch)
        save.setScale(scaleX, scaleY, x, y)
        return save
    }

    private val valsBufferForFitScreen = FloatArray(9)

    /**
     * Returns a new matrix with scale 1 and no translation, so the data fits the content rectangle exactly.
     * Also resets [minScaleX] and [minScaleY] to 1.
     */
    fun fitScreen(): Matrix {
        val save = Matrix()
        fitScreen(save)
        return save
    }

    /**
     * Writes into [outputMatrix] the touch matrix with scale 1 and no translation, so the data fits the content
     * rectangle exactly. Also resets [minScaleX] and [minScaleY] to 1.
     */
    fun fitScreen(outputMatrix: Matrix) {
        minScaleX = 1f
        minScaleY = 1f

        outputMatrix.set(matrixTouch)

        val vals = valsBufferForFitScreen
        vals.fill(0f)
        outputMatrix.getValues(vals)

        vals[Matrix.MTRANS_X] = 0f
        vals[Matrix.MTRANS_Y] = 0f
        vals[Matrix.MSCALE_X] = 1f
        vals[Matrix.MSCALE_Y] = 1f

        outputMatrix.setValues(vals)
    }

    /**
     * Returns a new matrix translated so that the pixel position in [transformedPts] moves to the top left
     * of the content rectangle.
     *
     * @param transformedPts x at index 0 and y at index 1, in pixels.
     */
    fun translate(transformedPts: FloatArray): Matrix {
        val save = Matrix()
        translate(transformedPts, save)
        return save
    }

    /**
     * Writes into [outputMatrix] the touch matrix translated so that the pixel position in [transformedPts]
     * moves to the top left of the content rectangle.
     *
     * @param transformedPts x at index 0 and y at index 1, in pixels.
     */
    fun translate(transformedPts: FloatArray, outputMatrix: Matrix) {
        outputMatrix.reset()
        outputMatrix.set(matrixTouch)
        val x = transformedPts[0] - offsetLeft
        val y = transformedPts[1] - offsetTop
        outputMatrix.postTranslate(-x, -y)
    }

    private val centerViewPortMatrixBuffer = Matrix()

    /**
     * Moves the viewport so that the pixel position in [transformedPts] sits at the top left of the content
     * rectangle, applies the limits and redraws [view]. Positions outside the data bounds are clamped.
     *
     * @param transformedPts x at index 0 and y at index 1, in pixels.
     */
    fun centerViewPort(transformedPts: FloatArray, view: View) {
        val save = centerViewPortMatrixBuffer
        save.reset()
        save.set(matrixTouch)
        val x = transformedPts[0] - offsetLeft
        val y = transformedPts[1] - offsetTop
        save.postTranslate(-x, -y)
        refresh(save, view, true)
    }

    private val matrixBuffer = FloatArray(9)

    /**
     * Makes [newMatrix] the current touch matrix after clamping its scale and translation to the limits.
     *
     * @param chart the view to redraw.
     * @param invalidate true to redraw [chart] right away.
     * @return [newMatrix], updated to the clamped values.
     */
    fun refresh(newMatrix: Matrix, chart: View, invalidate: Boolean): Matrix {
        matrixTouch.set(newMatrix)
        limitTransAndScale(matrixTouch, contentRect)
        if (invalidate) chart.invalidate()
        newMatrix.set(matrixTouch)
        return newMatrix
    }

    /**
     * Clamps the scale of [matrix] to the min and max scale limits and its translation so the data cannot be
     * dragged out of [content] further than [dragOffsetX] and [dragOffsetY] allow. Updates [scaleX], [scaleY],
     * [transX] and [transY].
     *
     * @param content the content rectangle in pixels; null is treated as an empty rectangle.
     */
    fun limitTransAndScale(matrix: Matrix, content: RectF?) {
        matrix.getValues(matrixBuffer)

        val curTransX = matrixBuffer[Matrix.MTRANS_X]
        val curScaleX = matrixBuffer[Matrix.MSCALE_X]
        val curTransY = matrixBuffer[Matrix.MTRANS_Y]
        val curScaleY = matrixBuffer[Matrix.MSCALE_Y]

        scaleX = min(max(minScaleX, curScaleX), maxScaleX)
        scaleY = min(max(minScaleY, curScaleY), maxScaleY)

        val width = content?.width() ?: 0f
        val height = content?.height() ?: 0f

        val transOffsetX = Utils.convertDpToPixel(dragOffsetX)
        val transOffsetY = Utils.convertDpToPixel(dragOffsetY)

        val maxTransX = -width * (scaleX - 1f)
        transX = min(max(curTransX, maxTransX - transOffsetX), transOffsetX)

        val maxTransY = height * (scaleY - 1f)
        transY = max(min(curTransY, maxTransY + transOffsetY), -transOffsetY)

        matrixBuffer[Matrix.MTRANS_X] = transX
        matrixBuffer[Matrix.MSCALE_X] = scaleX
        matrixBuffer[Matrix.MTRANS_Y] = transY
        matrixBuffer[Matrix.MSCALE_Y] = scaleY

        matrix.setValues(matrixBuffer)
    }

    /**
     * Sets the smallest allowed x scale factor and clamps the current touch matrix to it.
     *
     * @param xScale values below 1 are raised to 1.
     */
    fun setMinimumScaleX(xScale: Float) {
        minScaleX = max(xScale, 1f)
        limitTransAndScale(matrixTouch, contentRect)
    }

    /**
     * Sets the largest allowed x scale factor and clamps the current touch matrix to it.
     *
     * @param xScale 0 removes the limit.
     */
    fun setMaximumScaleX(xScale: Float) {
        maxScaleX = if (xScale == 0f) Float.MAX_VALUE else xScale
        limitTransAndScale(matrixTouch, contentRect)
    }

    /**
     * Sets both x scale limits and clamps the current touch matrix to them.
     *
     * @param minScaleX values below 1 are raised to 1.
     * @param maxScaleX 0 removes the limit.
     */
    fun setMinMaxScaleX(minScaleX: Float, maxScaleX: Float) {
        this.minScaleX = max(minScaleX, 1f)
        this.maxScaleX = if (maxScaleX == 0f) Float.MAX_VALUE else maxScaleX
        limitTransAndScale(matrixTouch, contentRect)
    }

    /**
     * Sets the smallest allowed y scale factor and clamps the current touch matrix to it.
     *
     * @param yScale values below 1 are raised to 1.
     */
    fun setMinimumScaleY(yScale: Float) {
        minScaleY = max(yScale, 1f)
        limitTransAndScale(matrixTouch, contentRect)
    }

    /**
     * Sets the largest allowed y scale factor and clamps the current touch matrix to it.
     *
     * @param yScale 0 removes the limit.
     */
    fun setMaximumScaleY(yScale: Float) {
        maxScaleY = if (yScale == 0f) Float.MAX_VALUE else yScale
        limitTransAndScale(matrixTouch, contentRect)
    }

    /**
     * Sets both y scale limits and clamps the current touch matrix to them.
     *
     * @param minScaleY values below 1 are raised to 1.
     * @param maxScaleY 0 removes the limit.
     */
    fun setMinMaxScaleY(minScaleY: Float, maxScaleY: Float) {
        this.minScaleY = max(minScaleY, 1f)
        this.maxScaleY = if (maxScaleY == 0f) Float.MAX_VALUE else maxScaleY
        limitTransAndScale(matrixTouch, contentRect)
    }

    /** True if the pixel x position lies within the content rectangle, with a 1 pixel tolerance on both sides. */
    fun isInBoundsX(x: Float): Boolean = isInBoundsLeft(x) && isInBoundsRight(x)

    /** True if the pixel y position lies within the content rectangle. */
    fun isInBoundsY(y: Float): Boolean = isInBoundsTop(y) && isInBoundsBottom(y)

    /** True if the pixel position lies within the content rectangle. */
    fun isInBounds(x: Float, y: Float): Boolean = isInBoundsX(x) && isInBoundsY(y)

    /** True if the pixel x position is not more than 1 pixel left of the content rectangle. */
    fun isInBoundsLeft(x: Float): Boolean = contentRect.left <= x + 1

    /** True if the pixel x position is not more than 1 pixel right of the content rectangle. */
    fun isInBoundsRight(x: Float): Boolean {
        val rounded = (x * 100f).toInt() / 100f
        return contentRect.right >= rounded - 1
    }

    /** True if the pixel y position is not above the content rectangle. */
    fun isInBoundsTop(y: Float): Boolean = contentRect.top <= y

    /** True if the pixel y position is not below the content rectangle. */
    fun isInBoundsBottom(y: Float): Boolean {
        val rounded = (y * 100f).toInt() / 100f
        return contentRect.bottom >= rounded
    }

    /** True if the chart is at scale 1 on both axes and no minimum scale above 1 is set. */
    val isFullyZoomedOut: Boolean
        get() = isFullyZoomedOutX && isFullyZoomedOutY

    /** True if the chart is at scale 1 on the y axis and no minimum y scale above 1 is set. */
    val isFullyZoomedOutY: Boolean
        get() = !(scaleY > minScaleY || minScaleY > 1f)

    /** True if the chart is at scale 1 on the x axis and no minimum x scale above 1 is set. */
    val isFullyZoomedOutX: Boolean
        get() = !(scaleX > minScaleX || minScaleX > 1f)

    /** True if neither [dragOffsetX] nor [dragOffsetY] is set. */
    val hasNoDragOffset: Boolean
        get() = dragOffsetX <= 0 && dragOffsetY <= 0

    /** True if the x scale is above [minScaleX]. */
    val canZoomOutMoreX: Boolean
        get() = scaleX > minScaleX

    /** True if the x scale is below [maxScaleX]. */
    val canZoomInMoreX: Boolean
        get() = scaleX < maxScaleX

    /** True if the y scale is above [minScaleY]. */
    val canZoomOutMoreY: Boolean
        get() = scaleY > minScaleY

    /** True if the y scale is below [maxScaleY]. */
    val canZoomInMoreY: Boolean
        get() = scaleY < maxScaleY
}
