package com.github.mikephil.charting.charts

import com.github.mikephil.charting.animation.ChartAnimator
import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.animation.Easing.EasingFunction
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.github.mikephil.charting.listener.PieRadarChartTouchListener
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Base class of the round charts, [PieChart] and [RadarChart]. It adds rotation by touch and the angle
 * and distance helpers around the chart center. Angles are in degrees, 0 at 3 o'clock, increasing
 * clockwise.
 *
 * @param T The [ChartData] type this chart displays, fixed by each subclass.
 */
public abstract class PieRadarChartBase<T : ChartData<out IDataSet<out Entry<*>>>> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : Chart<T>(context, attrs, defStyle) {

    /**
     * Angle in degrees at which the first slice or entry starts, normalized to 0 until 360.
     * Default 270, the top of the chart. Setting it also stores the unnormalized value in [rawRotationAngle].
     */
    public var rotationAngle: Float = 270f
        set(value) {
            rawRotationAngle = value
            field = Utils.getNormalizedAngle(value)
        }

    /** The last value given to [rotationAngle] before normalizing, which may be negative or above 360. */
    public var rawRotationAngle: Float = 270f
        private set

    /** Whether the chart can be rotated by dragging. */
    public var isRotationEnabled: Boolean = true

    /** Smallest space in dp between the view edge and the chart on every side. Default 0. */
    public var minOffset: Float = 0f

    override fun init() {
        super.init()
        chartTouchListener = PieRadarChartTouchListener(this)
    }

    override fun calcMinMax() {
    }

    override val maxVisibleCount: Int
        get() = data?.entryCount ?: 0

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isTouchEnabled && ::chartTouchListener.isInitialized) {
            chartTouchListener.onTouch(this, event)
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun computeScroll() {
        (chartTouchListener as? PieRadarChartTouchListener)?.computeScroll()
    }

    override fun notifyDataSetChanged() {
        val data = data ?: return

        data.notifyDataChanged()
        calcMinMax()
        renderer.initBuffers()

        legendRenderer.computeLegend(data)

        calculateOffsets()
        invalidate()
    }

    public override fun calculateOffsets() {
        var legendLeft = 0f
        var legendRight = 0f
        var legendBottom = 0f
        var legendTop = 0f

        val legend = legend
        if (legend.isEnabled && !legend.isDrawInsideEnabled) {
            val fullLegendWidth = min(legend.neededWidth, viewPortHandler.chartWidth * legend.maxSizePercent)

            when (legend.orientation) {
                Legend.LegendOrientation.VERTICAL -> {
                    var xLegendOffset = 0f

                    if (legend.horizontalAlignment == Legend.LegendHorizontalAlignment.LEFT || legend.horizontalAlignment == Legend.LegendHorizontalAlignment.RIGHT) {
                        if (legend.verticalAlignment == Legend.LegendVerticalAlignment.CENTER) {
                            val spacing = Utils.convertDpToPixel(13f)
                            xLegendOffset = fullLegendWidth + spacing
                        } else {
                            val spacing = Utils.convertDpToPixel(8f)

                            val legendWidth = fullLegendWidth + spacing
                            val legendHeight = legend.neededHeight + legend.textHeightMax

                            val center = center

                            val bottomX = if (legend.horizontalAlignment == Legend.LegendHorizontalAlignment.RIGHT) width - legendWidth + 15f else legendWidth - 15f
                            val bottomY = legendHeight + 15f
                            val distLegend = distanceToCenter(bottomX, bottomY)

                            val reference = getPosition(center, radius, getAngleForPoint(bottomX, bottomY))

                            val distReference = distanceToCenter(reference.x, reference.y)
                            val minOffset = Utils.convertDpToPixel(5f)

                            if (bottomY >= center.y && height - legendWidth > width) {
                                xLegendOffset = legendWidth
                            } else if (distLegend < distReference) {
                                val diff = distReference - distLegend
                                xLegendOffset = minOffset + diff
                            }

                            MPPointF.recycleInstance(center)
                            MPPointF.recycleInstance(reference)
                        }
                    }

                    when (legend.horizontalAlignment) {
                        Legend.LegendHorizontalAlignment.LEFT -> legendLeft = xLegendOffset
                        Legend.LegendHorizontalAlignment.RIGHT -> legendRight = xLegendOffset
                        Legend.LegendHorizontalAlignment.CENTER -> {
                            when (legend.verticalAlignment) {
                                Legend.LegendVerticalAlignment.TOP -> legendTop = min(legend.neededHeight, viewPortHandler.chartHeight * legend.maxSizePercent)
                                Legend.LegendVerticalAlignment.BOTTOM -> legendBottom = min(legend.neededHeight, viewPortHandler.chartHeight * legend.maxSizePercent)
                                Legend.LegendVerticalAlignment.CENTER -> {}
                            }
                        }
                    }
                }
                Legend.LegendOrientation.HORIZONTAL -> {
                    if (legend.verticalAlignment == Legend.LegendVerticalAlignment.TOP || legend.verticalAlignment == Legend.LegendVerticalAlignment.BOTTOM) {
                        val yOffset = requiredLegendOffset

                        val yLegendOffset = min(legend.neededHeight + yOffset, viewPortHandler.chartHeight * legend.maxSizePercent)

                        when (legend.verticalAlignment) {
                            Legend.LegendVerticalAlignment.TOP -> legendTop = yLegendOffset
                            Legend.LegendVerticalAlignment.BOTTOM -> legendBottom = yLegendOffset
                            Legend.LegendVerticalAlignment.CENTER -> {}
                        }
                    }
                }
            }

            legendLeft += requiredBaseOffset
            legendRight += requiredBaseOffset
            legendTop += requiredVerticalBaseOffset
            legendBottom += requiredVerticalBaseOffset
        }

        var minOffsetSides = Utils.convertDpToPixel(minOffset)
        var minOffsetTopBottom = minOffsetSides

        if (this is RadarChart) {
            val x = xAxis
            if (x.isEnabled && x.isDrawLabelsEnabled) {
                minOffsetSides = max(minOffsetSides, requiredBaseOffset)
                minOffsetTopBottom = max(minOffsetTopBottom, requiredVerticalBaseOffset)
            }
        }

        legendTop += Utils.convertDpToPixel(extraTopOffset)
        legendRight += Utils.convertDpToPixel(extraRightOffset)
        legendBottom += Utils.convertDpToPixel(extraBottomOffset)
        legendLeft += Utils.convertDpToPixel(extraLeftOffset)

        val offsetLeft = max(minOffsetSides, legendLeft)
        val offsetTop = max(minOffsetTopBottom, legendTop)
        val offsetRight = max(minOffsetSides, legendRight)
        val offsetBottom = max(minOffsetTopBottom, max(requiredVerticalBaseOffset, legendBottom))

        viewPortHandler.restrainViewPort(offsetLeft, offsetTop, offsetRight, offsetBottom)

        if (isLogEnabled) {
            Log.i(LOG_TAG, "offsetLeft: $offsetLeft, offsetTop: $offsetTop, offsetRight: $offsetRight, offsetBottom: $offsetBottom")
        }
    }

    /**
     * Angle in degrees of a pixel position around the content center: 0 at 3 o'clock, 90 at 6 o'clock,
     * 180 at 9 o'clock, 270 at 12 o'clock. A point exactly at 3 o'clock yields 360, the center itself 90.
     *
     * @param x Pixel x position.
     * @param y Pixel y position.
     */
    public fun getAngleForPoint(x: Float, y: Float): Float {
        val c = centerOffsets

        val tx = (x - c.x).toDouble()
        val ty = (y - c.y).toDouble()
        val length = sqrt(tx * tx + ty * ty)
        if (length == 0.0) {
            MPPointF.recycleInstance(c)
            return 90f
        }
        val r = acos(ty / length)

        var angle = Math.toDegrees(r).toFloat()

        if (x > c.x) angle = 360f - angle

        angle += 90f

        if (angle >= 360f) angle -= 360f

        MPPointF.recycleInstance(c)

        return angle
    }

    /**
     * Pixel position at a distance and angle from a center point.
     *
     * @param center Center point in pixels.
     * @param dist Distance from the center in pixels.
     * @param angle Angle in degrees, 0 at 3 o'clock, increasing clockwise.
     * @return A new point in pixels.
     */
    public fun getPosition(center: MPPointF, dist: Float, angle: Float): MPPointF {
        val p = MPPointF(0f, 0f)
        getPosition(center, dist, angle, p)
        return p
    }

    /** Like [getPosition], but writes the position into [outputPoint] instead of allocating. */
    public fun getPosition(center: MPPointF, dist: Float, angle: Float, outputPoint: MPPointF) {
        outputPoint.x = (center.x + dist * cos(Math.toRadians(angle.toDouble()))).toFloat()
        outputPoint.y = (center.y + dist * sin(Math.toRadians(angle.toDouble()))).toFloat()
    }

    /**
     * Distance in pixels between a pixel position and the content center.
     *
     * @param x Pixel x position.
     * @param y Pixel y position.
     */
    public fun distanceToCenter(x: Float, y: Float): Float {
        val c = centerOffsets

        val xDist = if (x > c.x) x - c.x else c.x - x
        val yDist = if (y > c.y) y - c.y else c.y - y

        val dist = sqrt(xDist.toDouble().pow(2.0) + yDist.toDouble().pow(2.0)).toFloat()

        MPPointF.recycleInstance(c)

        return dist
    }

    /**
     * Index of the entry lying under an angle around the center, taking [rotationAngle] into account.
     *
     * @param angle Angle in degrees, 0 at 3 o'clock, increasing clockwise, as [getAngleForPoint] returns it.
     * @return The entry index, or -1 when no entry lies under that angle.
     */
    public abstract fun getIndexForAngle(angle: Float): Int

    private val diameterRect = RectF()

    /** Largest circle diameter in pixels that fits the content area after the extra offsets. */
    public val diameter: Float
        get() {
            val content = diameterRect
            content.set(viewPortHandler.contentRect)
            content.left += Utils.convertDpToPixel(extraLeftOffset)
            content.top += Utils.convertDpToPixel(extraTopOffset)
            content.right -= Utils.convertDpToPixel(extraRightOffset)
            content.bottom -= Utils.convertDpToPixel(extraBottomOffset)
            return min(content.width(), content.height())
        }

    /** Radius of the drawn chart in pixels. */
    public abstract val radius: Float

    /** Space in pixels the legend needs between itself and the chart. */
    protected abstract val requiredLegendOffset: Float

    /** Space in pixels the chart needs left and right of itself before the legend is considered. */
    protected abstract val requiredBaseOffset: Float

    /**
     * Space in pixels the chart needs above and below itself before the legend is considered. The same as
     * [requiredBaseOffset] unless a chart needs less room there, as a radar chart does: its labels reach as far
     * sideways as they are wide, but only as far up and down as they are tall.
     */
    protected open val requiredVerticalBaseOffset: Float
        get() = requiredBaseOffset

    override val yChartMax: Float
        get() = 0f

    override val yChartMin: Float
        get() = 0f

    /**
     * Animates [rotationAngle] from one angle to another and redraws on every step. A running spin is cancelled
     * first.
     *
     * @param durationMillis Animation duration in milliseconds.
     * @param fromAngle Start angle in degrees, applied at once.
     * @param toAngle End angle in degrees. Values outside 0 until 360 keep spinning through full turns.
     * @param easing Easing of the animation.
     * @param onEnd Called once when the spin ends: when it finishes, when [stopAnimations] or leaving the window ends
     * it at [toAngle], and when a new spin cancels it.
     */
    @JvmOverloads
    public fun spin(durationMillis: Int, fromAngle: Float, toAngle: Float, easing: EasingFunction = Easing.Linear, onEnd: () -> Unit = {}) {
        rotationAngle = fromAngle
        animator.start(
            key = ChartAnimator.SPIN,
            from = fromAngle,
            to = toAngle,
            durationMillis = durationMillis,
            easing = easing,
            onUpdate = { rotationAngle = it },
            onEnd = { onEnd() }
        )
    }
}
