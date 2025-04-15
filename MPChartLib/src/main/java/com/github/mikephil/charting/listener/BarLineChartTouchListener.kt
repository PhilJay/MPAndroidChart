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
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * TouchListener for Bar-, Line-, Scatter- and CandleStickChart with handles all
 * touch interaction. Longpress == Zoom out. Double-Tap == Zoom in.
 */
@Suppress("MemberVisibilityCanBePrivate")
class BarLineChartTouchListener(
    chart: BarLineChartBase<out BarLineScatterCandleBubbleData<out IBarLineScatterCandleBubbleDataSet<out Entry>?>?>,
    touchMatrix: Matrix,
    dragTriggerDistance: Float
) :
    ChartTouchListener<BarLineChartBase<out BarLineScatterCandleBubbleData<out IBarLineScatterCandleBubbleDataSet<out Entry>?>?>?>(chart) {
    /**
     * returns the matrix object the listener holds
     *
     * @return
     */
    /**
     * the original touch-matrix from the chart
     */
    var matrix: Matrix = Matrix()
        private set

    /**
     * matrix for saving the original matrix state
     */
    private val savedMatrix = Matrix()

    /**
     * point where the touch action started
     */
    private val touchStartPoint: MPPointF = MPPointF.getInstance(0f, 0f)

    /**
     * center between two pointers (fingers on the display)
     */
    private val touchPointCenter: MPPointF = MPPointF.getInstance(0f, 0f)

    private var savedXDist = 1f
    private var savedYDist = 1f
    private var savedDist = 1f

    private var mClosestDataSetToTouch: IDataSet<*>? = null

    /**
     * used for tracking velocity of dragging
     */
    private var mVelocityTracker: VelocityTracker? = null

    private var mDecelerationLastTime: Long = 0
    private val mDecelerationCurrentPoint: MPPointF = MPPointF.getInstance(0f, 0f)
    private val mDecelerationVelocity: MPPointF = MPPointF.getInstance(0f, 0f)

    /**
     * the distance of movement that will be counted as a drag
     */
    private var dragTriggerDist: Float

    /**
     * the minimum distance between the pointers that will trigger a zoom gesture
     */
    private val minScalePointerDistance: Float


    private val matrixBuffer = FloatArray(9)
    private val tempMatrix = Matrix()

    /**
     * Constructor with initialization parameters.
     *
     * @param chart               instance of the chart
     * @param touchMatrix         the touch-matrix of the chart
     * @param dragTriggerDistance the minimum movement distance that will be interpreted as a "drag" gesture in dp (3dp equals
     * to about 9 pixels on a 5.5" FHD screen)
     */
    init {
        this.matrix = touchMatrix

        this.dragTriggerDist = Utils.convertDpToPixel(dragTriggerDistance)

        this.minScalePointerDistance = Utils.convertDpToPixel(3.5f)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)

        if (event.actionMasked == MotionEvent.ACTION_CANCEL) {
            if (mVelocityTracker != null) {
                mVelocityTracker!!.recycle()
                mVelocityTracker = null
            }
        }

        if (mTouchMode == NONE || mChart!!.isFlingEnabled) {
            mGestureDetector.onTouchEvent(event)
        }

        if (!mChart!!.isDragEnabled && (!mChart!!.isScaleXEnabled && !mChart!!.isScaleYEnabled)) return true

        // Handle touch events here...
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                startAction(event)

                stopDeceleration()

                saveTouchStart(event)
            }

            MotionEvent.ACTION_POINTER_DOWN -> if (event.pointerCount >= 2) {
                mChart!!.disableScroll()

                saveTouchStart(event)

                // get the distance between the pointers on the x-axis
                savedXDist = getXDist(event)

                // get the distance between the pointers on the y-axis
                savedYDist = getYDist(event)

                // get the total distance between the pointers
                savedDist = spacing(event)

                if (savedDist > 10f) {
                    mTouchMode = if (mChart!!.isPinchZoomEnabled) {
                        PINCH_ZOOM
                    } else {
                        if (mChart!!.isScaleXEnabled != mChart!!.isScaleYEnabled) {
                            if (mChart!!.isScaleXEnabled) X_ZOOM else Y_ZOOM
                        } else {
                            if (savedXDist > savedYDist) X_ZOOM else Y_ZOOM
                        }
                    }
                }

                // determine the touch-pointer center
                midPoint(touchPointCenter, event)
            }

            MotionEvent.ACTION_MOVE -> if (mTouchMode == DRAG) {
                mChart!!.disableScroll()

                val x = if (mChart!!.isDragXEnabled) event.x - touchStartPoint.x else 0f
                val y = if (mChart!!.isDragYEnabled) event.y - touchStartPoint.y else 0f

                performDrag(event, x, y)
            } else if (mTouchMode == X_ZOOM || mTouchMode == Y_ZOOM || mTouchMode == PINCH_ZOOM) {
                mChart!!.disableScroll()

                if (mChart!!.isScaleXEnabled || mChart!!.isScaleYEnabled) performZoom(event)
            } else if (mTouchMode == NONE
                && abs(
                    distance(
                        event.x, touchStartPoint.x, event.y,
                        touchStartPoint.y
                    ).toDouble()
                ) > dragTriggerDist
            ) {
                if (mChart!!.isDragEnabled) {
                    val shouldPan = !mChart!!.isFullyZoomedOut ||
                            !mChart!!.hasNoDragOffset()

                    if (shouldPan) {
                        val distanceX = abs((event.x - touchStartPoint.x).toDouble()).toFloat()
                        val distanceY = abs((event.y - touchStartPoint.y).toDouble()).toFloat()

                        // Disable dragging in a direction that's disallowed
                        if ((mChart!!.isDragXEnabled || distanceY >= distanceX) &&
                            (mChart!!.isDragYEnabled || distanceY <= distanceX)
                        ) {
                            mLastGesture = ChartGesture.DRAG
                            mTouchMode = DRAG
                        }
                    } else {
                        if (mChart!!.isHighlightPerDragEnabled) {
                            mLastGesture = ChartGesture.DRAG

                            if (mChart!!.isHighlightPerDragEnabled) performHighlightDrag(event)
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                val velocityTracker = mVelocityTracker
                val pointerId = event.getPointerId(0)
                velocityTracker!!.computeCurrentVelocity(1000, Utils.getMaximumFlingVelocity().toFloat())
                val velocityY = velocityTracker.getYVelocity(pointerId)
                val velocityX = velocityTracker.getXVelocity(pointerId)

                if (abs(velocityX.toDouble()) > Utils.getMinimumFlingVelocity() ||
                    abs(velocityY.toDouble()) > Utils.getMinimumFlingVelocity()
                ) {
                    if (mTouchMode == DRAG && mChart!!.isDragDecelerationEnabled) {
                        stopDeceleration()

                        mDecelerationLastTime = AnimationUtils.currentAnimationTimeMillis()

                        mDecelerationCurrentPoint.x = event.x
                        mDecelerationCurrentPoint.y = event.y

                        mDecelerationVelocity.x = velocityX
                        mDecelerationVelocity.y = velocityY

                        Utils.postInvalidateOnAnimation(mChart) // This causes computeScroll to fire, recommended for this by
                        // Google
                    }
                }

                if (mTouchMode == X_ZOOM || mTouchMode == Y_ZOOM || mTouchMode == PINCH_ZOOM || mTouchMode == POST_ZOOM) {
                    // Range might have changed, which means that Y-axis labels
                    // could have changed in size, affecting Y-axis size.
                    // So we need to recalculate offsets.

                    mChart!!.calculateOffsets()
                    mChart!!.postInvalidate()
                }

                mTouchMode = NONE
                mChart!!.enableScroll()

                mVelocityTracker?.recycle()
                mVelocityTracker = null

                endAction(event)
            }

            MotionEvent.ACTION_POINTER_UP -> {
                Utils.velocityTrackerPointerUpCleanUpIfNecessary(event, mVelocityTracker)

                mTouchMode = POST_ZOOM
            }

            MotionEvent.ACTION_CANCEL -> {
                mTouchMode = NONE
                endAction(event)
            }
        }

        // perform the transformation, update the chart
        matrix = mChart!!.viewPortHandler.refresh(matrix, mChart!!, true)

        return true // indicate event was handled
    }

    /** BELOW CODE PERFORMS THE ACTUAL TOUCH ACTIONS  */
    /**
     * Saves the current Matrix state and the touch-start point.
     *
     * @param event
     */
    private fun saveTouchStart(event: MotionEvent) {
        savedMatrix.set(matrix)
        touchStartPoint.x = event.x
        touchStartPoint.y = event.y

        mClosestDataSetToTouch = mChart!!.getDataSetByTouchPoint(event.x, event.y)
    }

    /**
     * Performs all necessary operations needed for dragging.
     *
     * @param event
     */
    private fun performDrag(event: MotionEvent, distanceX: Float, distanceY: Float) {
        var distanceXLocal = distanceX
        var distanceYLocal = distanceY
        mLastGesture = ChartGesture.DRAG

        matrix.set(savedMatrix)

        val l = mChart!!.onChartGestureListener

        // check if axis is inverted
        if (inverted()) {
            // if there is an inverted horizontalbarchart

            if (mChart is HorizontalBarChart) {
                distanceXLocal = -distanceXLocal
            } else {
                distanceYLocal = -distanceYLocal
            }
        }

        matrix.postTranslate(distanceXLocal, distanceYLocal)

        l?.onChartTranslate(event, distanceXLocal, distanceYLocal)
    }

    /**
     * Performs the all operations necessary for pinch and axis zoom.
     *
     * @param event
     */
    private fun performZoom(event: MotionEvent) {
        if (event.pointerCount >= 2) { // two finger zoom

            val l = mChart!!.onChartGestureListener

            // get the distance between the pointers of the touch event
            val totalDist = spacing(event)

            if (totalDist > minScalePointerDistance) {
                // get the translation

                val t = getTrans(touchPointCenter.x, touchPointCenter.y)
                val h = mChart!!.viewPortHandler

                // take actions depending on the activated touch mode
                if (mTouchMode == PINCH_ZOOM) {
                    mLastGesture = ChartGesture.PINCH_ZOOM

                    val scale = totalDist / savedDist // total scale

                    val isZoomingOut = (scale < 1)

                    val canZoomMoreX = if (isZoomingOut) h.canZoomOutMoreX() else h.canZoomInMoreX()

                    val canZoomMoreY = if (isZoomingOut) h.canZoomOutMoreY() else h.canZoomInMoreY()

                    val scaleX = if (mChart!!.isScaleXEnabled) scale else 1f
                    val scaleY = if (mChart!!.isScaleYEnabled) scale else 1f

                    if (canZoomMoreY || canZoomMoreX) {
                        matrix.set(savedMatrix)
                        matrix.postScale(getLimitedScaleX(scaleX, t), getLimitedScaleY(scaleY, t), t.x, t.y)

                        l?.onChartScale(event, scaleX, scaleY)
                    }
                } else if (mTouchMode == X_ZOOM && mChart!!.isScaleXEnabled) {
                    mLastGesture = ChartGesture.X_ZOOM

                    val xDist = getXDist(event)
                    val scaleX = xDist / savedXDist // x-axis scale

                    val isZoomingOut = (scaleX < 1)
                    val canZoomMoreX = if (isZoomingOut) h.canZoomOutMoreX() else h.canZoomInMoreX()

                    if (canZoomMoreX) {
                        matrix.set(savedMatrix)
                        matrix.postScale(getLimitedScaleX(scaleX, t), 1f, t.x, t.y)

                        l?.onChartScale(event, scaleX, 1f)
                    }
                } else if (mTouchMode == Y_ZOOM && mChart!!.isScaleYEnabled) {
                    mLastGesture = ChartGesture.Y_ZOOM

                    val yDist = getYDist(event)
                    val scaleY = yDist / savedYDist // y-axis scale

                    val isZoomingOut = (scaleY < 1)
                    val canZoomMoreY = if (isZoomingOut) h.canZoomOutMoreY() else h.canZoomInMoreY()

                    if (canZoomMoreY) {
                        matrix.set(savedMatrix)
                        matrix.postScale(1f, getLimitedScaleY(scaleY, t), t.x, t.y)

                        l?.onChartScale(event, 1f, scaleY)
                    }
                }

                MPPointF.recycleInstance(t)
            }
        }
    }

    /**
     * limit scaleX range
     * @param scaleX
     * @param t
     * @return
     */
    private fun getLimitedScaleX(scaleX: Float, t: MPPointF): Float {
        val h = mChart!!.viewPortHandler
        tempMatrix.set(savedMatrix)
        tempMatrix.postScale(scaleX, 1f, t.x, t.y)

        savedMatrix.getValues(matrixBuffer)
        val lastScaleX = matrixBuffer[Matrix.MSCALE_X]

        tempMatrix.getValues(matrixBuffer)
        val calScaleX = matrixBuffer[Matrix.MSCALE_X]

        var resultScaleX = scaleX

        if (calScaleX < h.minScaleX) {
            resultScaleX = h.minScaleX / lastScaleX
        } else if (calScaleX > h.maxScaleX) {
            resultScaleX = h.maxScaleX / lastScaleX
        }
        return resultScaleX
    }

    /**
     * limit scaleY range
     * @param scaleY
     * @param t
     * @return
     */
    private fun getLimitedScaleY(scaleY: Float, t: MPPointF): Float {
        val h = mChart!!.viewPortHandler
        tempMatrix.set(savedMatrix)
        tempMatrix.postScale(1f, scaleY, t.x, t.y)

        savedMatrix.getValues(matrixBuffer)
        val lastScaleY = matrixBuffer[Matrix.MSCALE_Y]

        tempMatrix.getValues(matrixBuffer)
        val calScaleY = matrixBuffer[Matrix.MSCALE_Y]

        var resultScaleY = scaleY

        if (calScaleY < h.minScaleY) {
            resultScaleY = h.minScaleY / lastScaleY
        } else if (calScaleY > h.maxScaleY) {
            resultScaleY = h.maxScaleY / lastScaleY
        }
        return resultScaleY
    }

    /**
     * Highlights upon dragging, generates callbacks for the selection-listener.
     *
     * @param e
     */
    private fun performHighlightDrag(e: MotionEvent) {
        val h = mChart!!.getHighlightByTouchPoint(e.x, e.y)

        if (h != null && !h.equalTo(mLastHighlighted)) {
            mLastHighlighted = h
            mChart!!.highlightValue(h, true)
        }
    }


    /**
     * Returns a recyclable MPPointF instance.
     * returns the correct translation depending on the provided x and y touch
     * points
     *
     * @param x
     * @param y
     * @return
     */
    fun getTrans(x: Float, y: Float): MPPointF {
        val vph = mChart!!.viewPortHandler

        val xTrans = x - vph.offsetLeft()
        var yTrans = 0f

        // check if axis is inverted
        yTrans = if (inverted()) {
            -(y - vph.offsetTop())
        } else {
            -(mChart!!.measuredHeight - y - vph.offsetBottom())
        }

        return MPPointF.getInstance(xTrans, yTrans)
    }

    /**
     * Returns true if the current touch situation should be interpreted as inverted, false if not.
     *
     * @return
     */
    private fun inverted(): Boolean {
        return (mClosestDataSetToTouch == null && mChart!!.isAnyAxisInverted) || (mClosestDataSetToTouch != null
                && mChart!!.isInverted(mClosestDataSetToTouch!!.axisDependency))
    }

    /**
     * ################ ################ ################ ################
     */
    /** GETTERS AND GESTURE RECOGNITION BELOW  */

    /**
     * Sets the minimum distance that will be interpreted as a "drag" by the chart in dp.
     * Default: 3dp
     *
     * @param dragTriggerDistance
     */
    fun setDragTriggerDist(dragTriggerDistance: Float) {
        this.dragTriggerDist = Utils.convertDpToPixel(dragTriggerDistance)
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        mLastGesture = ChartGesture.DOUBLE_TAP

        val l = mChart!!.onChartGestureListener

        l?.onChartDoubleTapped(e)

        // check if double-tap zooming is enabled
        if (mChart!!.isDoubleTapToZoomEnabled && mChart!!.data!!.entryCount > 0) {
            val trans = getTrans(e.x, e.y)

            val scaleX = if (mChart!!.isScaleXEnabled) 1.4f else 1f
            val scaleY = if (mChart!!.isScaleYEnabled) 1.4f else 1f

            mChart!!.zoom(scaleX, scaleY, trans.x, trans.y)

            if (mChart!!.isLogEnabled) Log.i(
                "BarlineChartTouch", ("Double-Tap, Zooming In, x: " + trans.x + ", y: "
                        + trans.y)
            )

            l?.onChartScale(e, scaleX, scaleY)

            MPPointF.recycleInstance(trans)
        }

        return super.onDoubleTap(e)
    }

    override fun onLongPress(e: MotionEvent) {
        mLastGesture = ChartGesture.LONG_PRESS

        val l = mChart!!.onChartGestureListener

        l?.onChartLongPressed(e)
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        mLastGesture = ChartGesture.SINGLE_TAP

        val l = mChart!!.onChartGestureListener

        l?.onChartSingleTapped(e)

        if (!mChart!!.isHighlightPerTapEnabled) {
            return false
        }

        val h = mChart!!.getHighlightByTouchPoint(e.x, e.y)
        performHighlight(h, e)

        return super.onSingleTapUp(e)
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        mLastGesture = ChartGesture.FLING

        val l = mChart!!.onChartGestureListener

        l?.onChartFling(e1, e2, velocityX, velocityY)

        return super.onFling(e1, e2, velocityX, velocityY)
    }

    fun stopDeceleration() {
        mDecelerationVelocity.x = 0f
        mDecelerationVelocity.y = 0f
    }

    fun computeScroll() {
        if (mDecelerationVelocity.x == 0f && mDecelerationVelocity.y == 0f) return  // There's no deceleration in progress


        val currentTime = AnimationUtils.currentAnimationTimeMillis()

        mDecelerationVelocity.x *= mChart!!.dragDecelerationFrictionCoef
        mDecelerationVelocity.y *= mChart!!.dragDecelerationFrictionCoef

        val timeInterval = (currentTime - mDecelerationLastTime).toFloat() / 1000f

        val distanceX = mDecelerationVelocity.x * timeInterval
        val distanceY = mDecelerationVelocity.y * timeInterval

        mDecelerationCurrentPoint.x += distanceX
        mDecelerationCurrentPoint.y += distanceY

        val event = MotionEvent.obtain(
            currentTime, currentTime, MotionEvent.ACTION_MOVE, mDecelerationCurrentPoint.x,
            mDecelerationCurrentPoint.y, 0
        )

        val dragDistanceX = if (mChart!!.isDragXEnabled) mDecelerationCurrentPoint.x - touchStartPoint.x else 0f
        val dragDistanceY = if (mChart!!.isDragYEnabled) mDecelerationCurrentPoint.y - touchStartPoint.y else 0f

        performDrag(event, dragDistanceX, dragDistanceY)

        event.recycle()
        matrix = mChart!!.viewPortHandler.refresh(matrix, mChart!!, false)

        mDecelerationLastTime = currentTime

        if (abs(mDecelerationVelocity.x.toDouble()) >= 0.01 || abs(mDecelerationVelocity.y.toDouble()) >= 0.01) Utils.postInvalidateOnAnimation(mChart) // This causes computeScroll to fire, recommended for this by Google
        else {
            // Range might have changed, which means that Y-axis labels
            // could have changed in size, affecting Y-axis size.
            // So we need to recalculate offsets.
            mChart!!.calculateOffsets()
            mChart!!.postInvalidate()

            stopDeceleration()
        }
    }

    companion object {
        /**
         * ################ ################ ################ ################
         */
        /** DOING THE MATH BELOW ;-)  */
        /**
         * Determines the center point between two pointer touch points.
         *
         * @param point
         * @param event
         */
        private fun midPoint(point: MPPointF, event: MotionEvent) {
            val x = event.getX(0) + event.getX(1)
            val y = event.getY(0) + event.getY(1)
            point.x = (x / 2f)
            point.y = (y / 2f)
        }

        /**
         * returns the distance between two pointer touch points
         *
         * @param event
         * @return
         */
        private fun spacing(event: MotionEvent): Float {
            val x = event.getX(0) - event.getX(1)
            val y = event.getY(0) - event.getY(1)
            return sqrt((x * x + y * y).toDouble()).toFloat()
        }

        /**
         * calculates the distance on the x-axis between two pointers (fingers on
         * the display)
         *
         * @param e
         * @return
         */
        private fun getXDist(e: MotionEvent): Float {
            val x = abs((e.getX(0) - e.getX(1)).toDouble()).toFloat()
            return x
        }

        /**
         * calculates the distance on the y-axis between two pointers (fingers on
         * the display)
         *
         * @param e
         * @return
         */
        private fun getYDist(e: MotionEvent): Float {
            val y = abs((e.getY(0) - e.getY(1)).toDouble()).toFloat()
            return y
        }
    }
}
