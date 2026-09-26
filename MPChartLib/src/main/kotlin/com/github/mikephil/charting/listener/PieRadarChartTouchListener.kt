package com.github.mikephil.charting.listener

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.PieRadarChartBase
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import kotlin.math.abs

/**
 * Touch handling for pie and radar charts: tap to highlight, long press and single finger rotation
 * with optional deceleration after the finger lifts.
 *
 * Rotation starts once the finger moved more than 8 dp from where it touched down, and only when the chart
 * has rotation enabled. Angles are in degrees, clockwise from 3 o'clock.
 */
public open class PieRadarChartTouchListener(chart: PieRadarChartBase<*>) : ChartTouchListener<PieRadarChartBase<*>>(chart) {

    private val touchStartPoint = MPPointF.getInstance(0f, 0f)

    private var startAngle = 0f

    private val velocitySamples = mutableListOf<AngularVelocitySample>()

    private var decelerationLastTime = 0L
    private var decelerationAngularVelocity = 0f

    private var touchStartedOnChart = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(event)) return true

        if (chart.isRotationEnabled) {
            val x = event.x
            val y = event.y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startAction(event)

                    stopDeceleration()

                    resetVelocity()

                    if (chart.isDragDecelerationEnabled) sampleVelocity(x, y)

                    setGestureStartAngle(x, y)
                    touchStartPoint.x = x
                    touchStartPoint.y = y
                    touchStartedOnChart = isOnChart(x, y)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (chart.isDragDecelerationEnabled) sampleVelocity(x, y)

                    if (touchMode == NONE && touchStartedOnChart &&
                        distance(x, touchStartPoint.x, y, touchStartPoint.y) > Utils.convertDpToPixel(8f)
                    ) {
                        lastGesture = ChartGesture.ROTATE
                        touchMode = ROTATE
                        chart.disableScroll()
                    } else if (touchMode == ROTATE) {
                        updateGestureRotation(x, y)
                        chart.invalidate()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (chart.isDragDecelerationEnabled && touchStartedOnChart) {
                        stopDeceleration()

                        sampleVelocity(x, y)

                        decelerationAngularVelocity = calculateVelocity()

                        if (decelerationAngularVelocity != 0f) {
                            decelerationLastTime = AnimationUtils.currentAnimationTimeMillis()
                            Utils.postInvalidateOnAnimation(chart)
                        }
                    }

                    chart.enableScroll()

                    touchMode = NONE

                    endAction(event)
                }
                MotionEvent.ACTION_CANCEL -> {
                    chart.enableScroll()
                    touchMode = NONE
                    endAction(event)
                }
            }
        }

        return true
    }

    override fun onLongPress(me: MotionEvent) {
        lastGesture = ChartGesture.LONG_PRESS
        chart.notifyGesture { it.onChartLongPressed(me) }
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean = true

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        lastGesture = ChartGesture.SINGLE_TAP

        chart.notifyGesture { it.onChartSingleTapped(e) }

        if (!chart.isHighlightPerTapEnabled) return false

        val high = chart.getHighlightByTouchPoint(e.x, e.y)
        performHighlight(high, e)

        return true
    }

    private fun resetVelocity() {
        velocitySamples.clear()
    }

    private fun sampleVelocity(touchLocationX: Float, touchLocationY: Float) {
        val currentTime = AnimationUtils.currentAnimationTimeMillis()

        velocitySamples.add(AngularVelocitySample(currentTime, chart.getAngleForPoint(touchLocationX, touchLocationY)))

        var i = 0
        var count = velocitySamples.size
        while (i < count - 2) {
            if (currentTime - velocitySamples[i].time > 1000) {
                velocitySamples.removeAt(0)
                i--
                count--
            } else {
                break
            }
            i++
        }
    }

    private fun calculateVelocity(): Float {
        if (velocitySamples.isEmpty()) return 0f

        val firstSample = velocitySamples[0]
        val lastSample = velocitySamples[velocitySamples.size - 1]

        var beforeLastSample = firstSample
        for (i in velocitySamples.size - 1 downTo 0) {
            beforeLastSample = velocitySamples[i]
            if (beforeLastSample.angle != lastSample.angle) break
        }

        var timeDelta = (lastSample.time - firstSample.time) / 1000f
        if (timeDelta == 0f) timeDelta = 0.1f

        var clockwise = lastSample.angle >= beforeLastSample.angle

        if (abs(lastSample.angle - beforeLastSample.angle) > 270.0) {
            clockwise = !clockwise
        }

        if (lastSample.angle - firstSample.angle > 180.0) {
            firstSample.angle += 360f
        } else if (firstSample.angle - lastSample.angle > 180.0) {
            lastSample.angle += 360f
        }

        var velocity = abs((lastSample.angle - firstSample.angle) / timeDelta)

        if (!clockwise) velocity = -velocity

        return velocity
    }

    /** True when the pixel position lies on the drawn chart: inside its radius and, for a pie chart, outside its hole. */
    private fun isOnChart(x: Float, y: Float): Boolean {
        val radius = chart.radius
        val distance = chart.distanceToCenter(x, y)
        if (distance > radius) return false

        val pie = chart as? PieChart ?: return true
        return !pie.isDrawHoleEnabled || distance > radius * (pie.holeRadius / 100f)
    }

    /**
     * Remembers the angle between the touch position ([x], [y]) in pixels and the chart's current rotation,
     * so later rotation updates keep that offset.
     */
    public fun setGestureStartAngle(x: Float, y: Float) {
        startAngle = chart.getAngleForPoint(x, y) - chart.rawRotationAngle
    }

    /**
     * Rotates the chart so the angle remembered by [setGestureStartAngle] follows the touch position ([x], [y])
     * in pixels.
     */
    public fun updateGestureRotation(x: Float, y: Float) {
        chart.rotationAngle = chart.getAngleForPoint(x, y) - startAngle
    }

    /** Stops a running rotation deceleration by setting its angular velocity to 0. */
    public fun stopDeceleration() {
        decelerationAngularVelocity = 0f
    }

    /**
     * Advances the rotation deceleration by one frame. Called by the chart from `View.computeScroll`.
     * Does nothing when no deceleration is running.
     */
    public fun computeScroll() {
        if (decelerationAngularVelocity == 0f) return

        val currentTime = AnimationUtils.currentAnimationTimeMillis()

        decelerationAngularVelocity *= chart.dragDecelerationFrictionCoef

        val timeInterval = (currentTime - decelerationLastTime).toFloat() / 1000f

        chart.rotationAngle = chart.rotationAngle + decelerationAngularVelocity * timeInterval

        decelerationLastTime = currentTime

        if (abs(decelerationAngularVelocity) >= 0.001) {
            Utils.postInvalidateOnAnimation(chart)
        } else {
            stopDeceleration()
        }
    }

    private class AngularVelocitySample(val time: Long, var angle: Float)
}
