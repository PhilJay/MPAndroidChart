package com.github.mikephil.charting.listener

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.AnimationUtils
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Touch handling for bar, line, scatter, candle and bubble charts: tap to highlight, double tap to zoom in,
 * single finger drag to pan (or to move the highlight when the chart cannot pan), two finger pinch to zoom,
 * and fling with deceleration.
 *
 * Which gestures work depends on the chart's drag, scale, pinch zoom and highlight settings. Zoom and drag
 * change the chart's touch matrix directly and are applied through the [com.github.mikephil.charting.utils.ViewPortHandler].
 *
 * @param touchMatrix the chart's touch matrix, see [matrix].
 * @property dragTriggerDist distance in dp a finger must move before the touch counts as a drag; charts
 *   create the listener with 3 dp.
 */
open class BarLineChartTouchListener(
    chart: BarLineChartBase<*>,
    touchMatrix: Matrix,
    var dragTriggerDist: Float
) : ChartTouchListener<BarLineChartBase<*>>(chart) {

    /** The touch matrix that holds the chart's zoom and drag state. */
    var matrix = touchMatrix
        private set

    private val savedMatrix = Matrix()

    private val touchStartPoint = MPPointF.getInstance(0f, 0f)

    private val touchPointCenter = MPPointF.getInstance(0f, 0f)

    private var savedXDist = 1f
    private var savedYDist = 1f
    private var savedDist = 1f

    private val savedMatrixValues = FloatArray(9)

    private var closestDataSetToTouch: IDataSet<*>? = null

    private var velocityTracker: VelocityTracker? = null

    private var decelerationLastTime = 0L
    private val decelerationCurrentPoint = MPPointF.getInstance(0f, 0f)
    private val decelerationVelocity = MPPointF.getInstance(0f, 0f)

    private val minScalePointerDistance = Utils.convertDpToPixel(3.5f)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val tracker = velocityTracker ?: VelocityTracker.obtain().also { velocityTracker = it }
        tracker.addMovement(event)

        if (event.actionMasked == MotionEvent.ACTION_CANCEL) {
            tracker.recycle()
            velocityTracker = null
        }

        if (touchMode == NONE) {
            gestureDetector.onTouchEvent(event)
        }

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                startAction(event)
                stopDeceleration()
                saveTouchStart(event)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (chart.isScaleEnabled && event.pointerCount >= 2) {
                    chart.disableScroll()

                    saveTouchStart(event)

                    savedXDist = getXDist(event)
                    savedYDist = getYDist(event)
                    savedDist = spacing(event)

                    if (savedDist > 10f) {
                        touchMode = if (chart.isPinchZoomEnabled) {
                            PINCH_ZOOM
                        } else if (chart.isScaleXEnabled != chart.isScaleYEnabled) {
                            if (chart.isScaleXEnabled) X_ZOOM else Y_ZOOM
                        } else {
                            if (savedXDist > savedYDist) X_ZOOM else Y_ZOOM
                        }
                    }

                    midPoint(touchPointCenter, event)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchMode == DRAG) {
                    chart.disableScroll()

                    val x = if (chart.isDragXEnabled) event.x - touchStartPoint.x else 0f
                    val y = if (chart.isDragYEnabled) event.y - touchStartPoint.y else 0f

                    performDrag(event, x, y)
                } else if (touchMode == X_ZOOM || touchMode == Y_ZOOM || touchMode == PINCH_ZOOM) {
                    chart.disableScroll()

                    if (chart.isScaleXEnabled || chart.isScaleYEnabled) performZoom(event)
                } else if (touchMode == NONE && abs(distance(event.x, touchStartPoint.x, event.y, touchStartPoint.y)) > Utils.convertDpToPixel(dragTriggerDist)) {
                    val shouldPan = chart.isDragEnabled && (!chart.isFullyZoomedOut || !chart.hasNoDragOffset)

                    if (shouldPan) {
                        val distanceX = abs(event.x - touchStartPoint.x)
                        val distanceY = abs(event.y - touchStartPoint.y)

                        if ((chart.isDragXEnabled || distanceY >= distanceX) && (chart.isDragYEnabled || distanceY <= distanceX)) {
                            lastGesture = ChartGesture.DRAG
                            touchMode = DRAG
                        }
                    } else if (chart.isHighlightPerDragEnabled) {
                        lastGesture = ChartGesture.DRAG
                        performHighlightDrag(event)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                val pointerId = event.getPointerId(0)
                tracker.computeCurrentVelocity(1000, Utils.maximumFlingVelocity.toFloat())
                val velocityY = tracker.getYVelocity(pointerId)
                val velocityX = tracker.getXVelocity(pointerId)

                if (abs(velocityX) > Utils.minimumFlingVelocity || abs(velocityY) > Utils.minimumFlingVelocity) {
                    if (touchMode == DRAG && chart.isDragDecelerationEnabled) {
                        stopDeceleration()

                        decelerationLastTime = AnimationUtils.currentAnimationTimeMillis()

                        decelerationCurrentPoint.x = event.x
                        decelerationCurrentPoint.y = event.y

                        decelerationVelocity.x = velocityX
                        decelerationVelocity.y = velocityY

                        Utils.postInvalidateOnAnimation(chart)
                    }
                }

                if (touchMode == X_ZOOM || touchMode == Y_ZOOM || touchMode == PINCH_ZOOM || touchMode == POST_ZOOM) {
                    chart.calculateOffsets()
                    chart.postInvalidate()
                }

                touchMode = NONE
                chart.enableScroll()

                velocityTracker?.recycle()
                velocityTracker = null

                endAction(event)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                Utils.velocityTrackerPointerUpCleanUpIfNecessary(event, tracker)
                touchMode = POST_ZOOM
            }
            MotionEvent.ACTION_CANCEL -> {
                chart.enableScroll()
                touchMode = NONE
                endAction(event)
            }
        }

        if (chart.isDragEnabled || chart.isScaleEnabled) {
            matrix = chart.viewPortHandler.refresh(matrix, chart, true)
        }

        return true
    }

    private fun saveTouchStart(event: MotionEvent) {
        savedMatrix.set(matrix)
        touchStartPoint.x = event.x
        touchStartPoint.y = event.y

        closestDataSetToTouch = chart.getDataSetByTouchPoint(event.x, event.y)
    }

    private fun performDrag(event: MotionEvent, distanceX: Float, distanceY: Float) {
        lastGesture = ChartGesture.DRAG

        matrix.set(savedMatrix)


        var dX = distanceX
        var dY = distanceY

        if (inverted()) {
            if (chart is HorizontalBarChart) dX = -dX else dY = -dY
        }

        matrix.postTranslate(dX, dY)

        chart.notifyGesture { it.onChartTranslate(event, dX, dY) }
    }

    /**
     * Limits [scale] so that applying it on top of [current] keeps the chart between [min] and [max]. Without this
     * the view port clamps the scale afterwards but keeps the translation of the larger scale, and the chart jumps.
     */
    private fun limitScale(scale: Float, current: Float, min: Float, max: Float): Float {
        if (current <= 0f) return scale
        return scale.coerceIn(min / current, max / current)
    }

    private fun performZoom(event: MotionEvent) {
        if (event.pointerCount >= 2) {
    
            val totalDist = spacing(event)

            if (totalDist > minScalePointerDistance) {
                val t = getTrans(touchPointCenter.x, touchPointCenter.y)
                val h = chart.viewPortHandler

                savedMatrix.getValues(savedMatrixValues)
                val savedScaleX = savedMatrixValues[Matrix.MSCALE_X]
                val savedScaleY = savedMatrixValues[Matrix.MSCALE_Y]

                if (touchMode == PINCH_ZOOM) {
                    lastGesture = ChartGesture.PINCH_ZOOM

                    val scale = totalDist / savedDist

                    val isZoomingOut = scale < 1
                    val canZoomMoreX = if (isZoomingOut) h.canZoomOutMoreX else h.canZoomInMoreX
                    val canZoomMoreY = if (isZoomingOut) h.canZoomOutMoreY else h.canZoomInMoreY

                    val scaleX = if (chart.isScaleXEnabled) scale else 1f
                    val scaleY = if (chart.isScaleYEnabled) scale else 1f

                    if (canZoomMoreY || canZoomMoreX) {
                        matrix.set(savedMatrix)
                        matrix.postScale(
                            limitScale(scaleX, savedScaleX, h.minScaleX, h.maxScaleX),
                            limitScale(scaleY, savedScaleY, h.minScaleY, h.maxScaleY),
                            t.x,
                            t.y
                        )

                        chart.notifyGesture { it.onChartScale(event, scaleX, scaleY) }
                    }
                } else if (touchMode == X_ZOOM && chart.isScaleXEnabled) {
                    lastGesture = ChartGesture.X_ZOOM

                    val xDist = getXDist(event)
                    val scaleX = xDist / savedXDist

                    val isZoomingOut = scaleX < 1
                    val canZoomMoreX = if (isZoomingOut) h.canZoomOutMoreX else h.canZoomInMoreX

                    if (canZoomMoreX) {
                        matrix.set(savedMatrix)
                        matrix.postScale(limitScale(scaleX, savedScaleX, h.minScaleX, h.maxScaleX), 1f, t.x, t.y)

                        chart.notifyGesture { it.onChartScale(event, scaleX, 1f) }
                    }
                } else if (touchMode == Y_ZOOM && chart.isScaleYEnabled) {
                    lastGesture = ChartGesture.Y_ZOOM

                    val yDist = getYDist(event)
                    val scaleY = yDist / savedYDist

                    val isZoomingOut = scaleY < 1
                    val canZoomMoreY = if (isZoomingOut) h.canZoomOutMoreY else h.canZoomInMoreY

                    if (canZoomMoreY) {
                        matrix.set(savedMatrix)
                        matrix.postScale(1f, limitScale(scaleY, savedScaleY, h.minScaleY, h.maxScaleY), t.x, t.y)

                        chart.notifyGesture { it.onChartScale(event, 1f, scaleY) }
                    }
                }

                MPPointF.recycleInstance(t)
            }
        }
    }

    private fun performHighlightDrag(e: MotionEvent) {
        val h = chart.getHighlightByTouchPoint(e.x, e.y)

        if (h != null && !h.equalTo(lastHighlighted)) {
            lastHighlighted = h
            chart.highlightValue(h, true)
        }
    }

    /**
     * Converts the pixel position ([x], [y]) on the view into the pivot used for zooming: relative to the
     * content rectangle, with y measured from the bottom unless the touched axis is inverted.
     *
     * @return a pooled [MPPointF]; recycle it with [MPPointF.recycleInstance] when done.
     */
    fun getTrans(x: Float, y: Float): MPPointF {
        val vph = chart.viewPortHandler

        val xTrans = x - vph.offsetLeft
        val yTrans = if (inverted()) {
            -(y - vph.offsetTop)
        } else {
            -(chart.measuredHeight - y - vph.offsetBottom)
        }

        return MPPointF.getInstance(xTrans, yTrans)
    }

    private fun inverted(): Boolean {
        val closest = closestDataSetToTouch
        return (closest == null && chart.isAnyAxisInverted) || (closest != null && chart.isInverted(closest.axisDependency))
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        lastGesture = ChartGesture.DOUBLE_TAP

        chart.notifyGesture { it.onChartDoubleTapped(e) }

        if (chart.isDoubleTapToZoomEnabled && (chart.data?.entryCount ?: 0) > 0) {
            val trans = getTrans(e.x, e.y)

            val scaleX = if (chart.isScaleXEnabled) 1.4f else 1f
            val scaleY = if (chart.isScaleYEnabled) 1.4f else 1f

            chart.zoom(scaleX, scaleY, trans.x, trans.y)

            if (chart.isLogEnabled) {
                Log.i("BarlineChartTouch", "Double-Tap, Zooming In, x: ${trans.x}, y: ${trans.y}")
            }

            chart.notifyGesture { it.onChartScale(e, scaleX, scaleY) }

            MPPointF.recycleInstance(trans)
        }

        return super.onDoubleTap(e)
    }

    override fun onLongPress(e: MotionEvent) {
        lastGesture = ChartGesture.LONG_PRESS
        chart.notifyGesture { it.onChartLongPressed(e) }
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        lastGesture = ChartGesture.SINGLE_TAP

        chart.notifyGesture { it.onChartSingleTapped(e) }

        if (!chart.isHighlightPerTapEnabled) return false

        val h = chart.getHighlightByTouchPoint(e.x, e.y)
        performHighlight(h, e)

        return super.onSingleTapUp(e)
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        lastGesture = ChartGesture.FLING

        chart.notifyGesture { it.onChartFling(e1, e2, velocityX, velocityY) }

        return super.onFling(e1, e2, velocityX, velocityY)
    }

    /** Stops a running fling deceleration by setting its velocity to 0. */
    fun stopDeceleration() {
        decelerationVelocity.x = 0f
        decelerationVelocity.y = 0f
    }

    /**
     * Advances the fling deceleration by one frame: applies friction, drags the chart by the distance covered
     * and requests the next frame until the velocity is nearly 0. Called by the chart from `View.computeScroll`.
     * Does nothing when no deceleration is running.
     */
    fun computeScroll() {
        if (decelerationVelocity.x == 0f && decelerationVelocity.y == 0f) return

        val currentTime = AnimationUtils.currentAnimationTimeMillis()

        decelerationVelocity.x *= chart.dragDecelerationFrictionCoef
        decelerationVelocity.y *= chart.dragDecelerationFrictionCoef

        val timeInterval = (currentTime - decelerationLastTime).toFloat() / 1000f

        val distanceX = decelerationVelocity.x * timeInterval
        val distanceY = decelerationVelocity.y * timeInterval

        decelerationCurrentPoint.x += distanceX
        decelerationCurrentPoint.y += distanceY

        val event = MotionEvent.obtain(currentTime, currentTime, MotionEvent.ACTION_MOVE, decelerationCurrentPoint.x, decelerationCurrentPoint.y, 0)

        val dragDistanceX = if (chart.isDragXEnabled) decelerationCurrentPoint.x - touchStartPoint.x else 0f
        val dragDistanceY = if (chart.isDragYEnabled) decelerationCurrentPoint.y - touchStartPoint.y else 0f

        performDrag(event, dragDistanceX, dragDistanceY)

        event.recycle()
        matrix = chart.viewPortHandler.refresh(matrix, chart, false)

        decelerationLastTime = currentTime

        if (abs(decelerationVelocity.x) >= 0.01 || abs(decelerationVelocity.y) >= 0.01) {
            Utils.postInvalidateOnAnimation(chart)
        } else {
            chart.calculateOffsets()
            chart.postInvalidate()

            stopDeceleration()
        }
    }

    private companion object {

        fun midPoint(point: MPPointF, event: MotionEvent) {
            val x = event.getX(0) + event.getX(1)
            val y = event.getY(0) + event.getY(1)
            point.x = x / 2f
            point.y = y / 2f
        }

        fun spacing(event: MotionEvent): Float {
            val x = event.getX(0) - event.getX(1)
            val y = event.getY(0) - event.getY(1)
            return sqrt(x * x + y * y)
        }

        fun getXDist(e: MotionEvent): Float = abs(e.getX(0) - e.getX(1))

        fun getYDist(e: MotionEvent): Float = abs(e.getY(0) - e.getY(1))
    }
}
