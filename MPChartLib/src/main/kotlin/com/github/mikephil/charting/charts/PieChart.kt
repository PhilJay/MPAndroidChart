package com.github.mikephil.charting.charts

import android.content.Context
import android.graphics.Paint
import android.text.TextPaint
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.annotation.Keep
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.highlight.PieHighlighter
import com.github.mikephil.charting.renderer.PieChartRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Chart that draws one [PieData] set as slices of a circle. Slice sizes are shares of the sum of all
 * y values; the first slice starts at [rotationAngle] and slices follow clockwise.
 */
@Keep
open class PieChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : PieRadarChartBase<PieData>(context, attrs, defStyle) {

    /** Bounds of the pie in pixels, already shrunk by the data set's selection shift. */
    val circleBox = RectF()

    /** Whether the label of each `PieEntry` is drawn inside its slice. */
    var isDrawEntryLabelsEnabled = true

    /** Size of each slice in degrees, in entry order. Recomputed by [notifyDataSetChanged]. */
    var drawAngles = FloatArray(1)
        private set

    /** End angle of each slice in degrees, measured from [rotationAngle], in entry order. */
    var absoluteAngles = FloatArray(1)
        private set

    /** Whether the hole in the center is drawn. */
    var isDrawHoleEnabled = true

    /** Whether the slices continue under the hole instead of stopping at its edge. */
    var isDrawSlicesUnderHoleEnabled = false

    /** Whether values are handed to the value formatter as a percentage of the total instead of as is. */
    var isUsePercentValuesEnabled = false

    /** Whether slice ends are rounded. Only applies with the hole drawn and slices not under the hole. */
    var isDrawRoundedSlicesEnabled = false

    /** Text drawn in the center of the pie while [isDrawCenterTextEnabled] is true. */
    var centerText: CharSequence = ""

    private val centerTextOffsetValue = MPPointF.getInstance(0f, 0f)

    /** Radius of the hole as a percentage of the pie radius. Default 50. */
    var holeRadius = 50f

    /**
     * Radius of the translucent ring around the hole as a percentage of the pie radius. Default 55.
     * The ring is only drawn while this is larger than [holeRadius].
     */
    var transparentCircleRadius = 55f

    /** Whether [centerText] is drawn. */
    var isDrawCenterTextEnabled = true

    /** Width of the box [centerText] wraps in, as a percentage of the hole diameter. Default 100. */
    var centerTextRadiusPercent = 100f

    /**
     * Angle in degrees the whole pie spans, clamped to 90 until 360. 360 is a full circle, 180 a half pie.
     * Default 360.
     */
    var maxAngle = 360f
        set(value) {
            field = value.coerceIn(90f, 360f)
        }

    /**
     * Smallest angle in degrees a slice is drawn with, clamped to 0 until half of [maxAngle]. Only applied
     * when all slices fit at that size; call [notifyDataSetChanged] after changing it. Default 0.
     */
    var minAngleForSlices = 0f
        set(value) {
            field = value.coerceIn(0f, maxAngle / 2f)
        }

    private val pieRenderer: PieChartRenderer
        get() = renderer as PieChartRenderer

    override fun init() {
        super.init()

        renderer = PieChartRenderer(this, animator, viewPortHandler)
        highlighter = PieHighlighter(this)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (data == null) return

        renderer.drawData(canvas)

        val highlighted = highlighted
        if (valuesToHighlight()) renderer.drawHighlighted(canvas, highlighted)

        renderer.drawExtras(canvas)

        renderer.drawValues(canvas)

        legendRenderer.renderLegend(canvas)

        drawDescription(canvas)

        drawMarkers(canvas)
    }

    override fun calculateOffsets() {
        super.calculateOffsets()

        val data = data ?: return

        val radius = diameter / 2f

        val c = centerOffsets

        val shift = Utils.convertDpToPixel(data.dataSet.selectionShift)

        circleBox.set(c.x - radius + shift, c.y - radius + shift, c.x + radius - shift, c.y + radius - shift)

        MPPointF.recycleInstance(c)
    }

    override fun calcMinMax() {
        calcAngles()
    }

    override fun getMarkerPosition(high: Highlight): FloatArray {
        val center = centerCircleBox
        var r = radius

        var off = r / 10f * 3.6f

        if (isDrawHoleEnabled) {
            off = (r - (r / 100f * holeRadius)) / 2f
        }

        r -= off

        val rotationAngle = rotationAngle

        val entryIndex = high.x.toInt()

        val offset = drawAngles[entryIndex] / 2

        val x = (r * cos(Math.toRadians(((rotationAngle + absoluteAngles[entryIndex] - offset) * animator.phaseY).toDouble())) + center.x).toFloat()
        val y = (r * sin(Math.toRadians(((rotationAngle + absoluteAngles[entryIndex] - offset) * animator.phaseY).toDouble())) + center.y).toFloat()

        MPPointF.recycleInstance(center)
        return floatArrayOf(x, y)
    }

    private fun calcAngles() {
        val data = data ?: return

        val entryCount = data.entryCount

        if (drawAngles.size != entryCount) {
            drawAngles = FloatArray(entryCount)
        } else {
            drawAngles.fill(0f)
        }
        if (absoluteAngles.size != entryCount) {
            absoluteAngles = FloatArray(entryCount)
        } else {
            absoluteAngles.fill(0f)
        }

        val yValueSum = data.yValueSum

        val dataSets = data.dataSets

        val hasMinAngle = minAngleForSlices != 0f && entryCount * minAngleForSlices <= maxAngle
        val minAngles = FloatArray(entryCount)

        var cnt = 0
        var offset = 0f
        var diff = 0f

        for (i in 0 until data.dataSetCount) {
            val set = dataSets[i]

            for (j in 0 until set.entryCount) {
                val drawAngle = calcAngle(abs(set.getEntryForIndex(j).y), yValueSum)

                if (hasMinAngle) {
                    val temp = drawAngle - minAngleForSlices
                    if (temp <= 0) {
                        minAngles[cnt] = minAngleForSlices
                        offset += -temp
                    } else {
                        minAngles[cnt] = drawAngle
                        diff += temp
                    }
                }

                drawAngles[cnt] = drawAngle

                absoluteAngles[cnt] = if (cnt == 0) drawAngles[cnt] else absoluteAngles[cnt - 1] + drawAngles[cnt]

                cnt++
            }
        }

        if (hasMinAngle) {
            for (i in 0 until entryCount) {
                minAngles[i] -= (minAngles[i] - minAngleForSlices) / diff * offset
                absoluteAngles[i] = if (i == 0) minAngles[0] else absoluteAngles[i - 1] + minAngles[i]
            }

            drawAngles = minAngles
        }
    }

    /** True when the slice at [index] is currently highlighted. */
    fun needsHighlight(index: Int): Boolean {
        if (!valuesToHighlight()) return false

        val highlighted = highlighted
        for (high in highlighted) {
            if (high.x.toInt() == index) return true
        }
        return false
    }

    private fun calcAngle(value: Float, yValueSum: Float): Float {
        return value / yValueSum * maxAngle
    }

    override fun getIndexForAngle(angle: Float): Int {
        val a = Utils.getNormalizedAngle(angle - rotationAngle)

        for (i in absoluteAngles.indices) {
            if (absoluteAngles[i] > a) return i
        }

        return -1
    }

    /**
     * Index of the data set that holds an entry at the given x value.
     *
     * @param xIndex The x value to look for.
     * @return The data set index, or -1 when there is no data or no entry has that x value.
     */
    fun getDataSetIndexForIndex(xIndex: Int): Int {
        val dataSets = data?.dataSets ?: return -1

        for (i in dataSets.indices) {
            if (dataSets[i].getEntryForXValue(xIndex.toFloat(), Float.NaN) != null) return i
        }

        return -1
    }

    /** Paint used for the hole in the middle of the pie. */
    var holePaint: Paint
        get() = pieRenderer.paintHole
        set(value) {
            pieRenderer.paintHole = value
        }

    /** Paint used for the center text. */
    var centerTextPaint: TextPaint
        get() = pieRenderer.paintCenterText
        set(value) {
            pieRenderer.paintCenterText = value
        }

    /** Fill color of the hole, used while [isDrawHoleEnabled] is true. */
    var holeColor: Int
        get() = pieRenderer.paintHole.color
        set(value) {
            pieRenderer.paintHole.color = value
        }

    override val requiredLegendOffset: Float
        get() = legendRenderer.labelPaint.textSize * 2f

    override val requiredBaseOffset: Float
        get() = 0f

    override val radius: Float
        get() = min(circleBox.width() / 2f, circleBox.height() / 2f)

    /** Center of [circleBox] in pixels, as a new point. */
    val centerCircleBox: MPPointF
        get() = MPPointF(circleBox.centerX(), circleBox.centerY())

    /** Typeface of [centerText], or null for the default. */
    var centerTextTypeface: Typeface?
        get() = pieRenderer.paintCenterText.typeface
        set(value) {
            pieRenderer.paintCenterText.typeface = value
        }

    /** Text size of [centerText] in dp. */
    var centerTextSize: Float
        get() = Utils.convertPixelsToDp(pieRenderer.paintCenterText.textSize)
        set(value) {
            pieRenderer.paintCenterText.textSize = Utils.convertDpToPixel(value)
        }

    /** Text size of [centerText] in pixels. */
    var centerTextSizePixels: Float
        get() = pieRenderer.paintCenterText.textSize
        set(value) {
            pieRenderer.paintCenterText.textSize = value
        }

    /**
     * Moves [centerText] away from the center of the pie.
     *
     * @param x Horizontal shift in dp, positive to the right. Default 0.
     * @param y Vertical shift in dp, positive downwards. Default 0.
     */
    fun setCenterTextOffset(x: Float, y: Float) {
        centerTextOffsetValue.x = x
        centerTextOffsetValue.y = y
    }

    /** Copy of the shift set with [setCenterTextOffset], in dp. */
    val centerTextOffset: MPPointF
        get() = MPPointF(centerTextOffsetValue.x, centerTextOffsetValue.y)

    /** Color of [centerText]. */
    var centerTextColor: Int
        get() = pieRenderer.paintCenterText.color
        set(value) {
            pieRenderer.paintCenterText.color = value
        }

    /** Color of the translucent ring around the hole. Setting it keeps the current [transparentCircleAlpha]. */
    var transparentCircleColor: Int
        get() = pieRenderer.paintTransparentCircle.color
        set(value) {
            val p = pieRenderer.paintTransparentCircle
            val alpha = p.alpha
            p.color = value
            p.alpha = alpha
        }

    /** Opacity of the translucent ring from 0 (invisible) to 255 (opaque). Default 105. */
    var transparentCircleAlpha: Int
        get() = pieRenderer.paintTransparentCircle.alpha
        set(value) {
            pieRenderer.paintTransparentCircle.alpha = value
        }

    /** Color of the entry labels drawn inside the slices. */
    var entryLabelColor: Int
        get() = pieRenderer.paintEntryLabels.color
        set(value) {
            pieRenderer.paintEntryLabels.color = value
        }

    /** Typeface of the entry labels drawn inside the slices, or null for the default. */
    var entryLabelTypeface: Typeface?
        get() = pieRenderer.paintEntryLabels.typeface
        set(value) {
            pieRenderer.paintEntryLabels.typeface = value
        }

    /** Text size in dp of the entry labels drawn inside the slices. */
    var entryLabelTextSize: Float
        get() = Utils.convertPixelsToDp(pieRenderer.paintEntryLabels.textSize)
        set(value) {
            pieRenderer.paintEntryLabels.textSize = Utils.convertDpToPixel(value)
        }

    override fun onDetachedFromWindow() {
        if (::renderer.isInitialized) {
            (renderer as? PieChartRenderer)?.releaseBitmap()
        }
        super.onDetachedFromWindow()
    }
}
