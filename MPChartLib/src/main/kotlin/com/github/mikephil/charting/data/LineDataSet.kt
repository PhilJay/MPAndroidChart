package com.github.mikephil.charting.data

import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.util.Log
import com.github.mikephil.charting.formatter.DefaultFillFormatter
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate

/**
 * One line in a line chart, with its drawing mode, circle markers, dash pattern and fill settings.
 *
 * @param D the payload type of the entries.
 * @param entries the points of the line, sorted by x; an `ArrayList` is kept by reference, other lists are copied.
 * @param label name of the set, shown in the legend.
 */
open class LineDataSet<D>(entries: List<Entry<D>>, label: String) : LineRadarDataSet<Entry<D>>(entries, label), ILineDataSet<D> {

    override var mode = Mode.LINEAR

    /** Colors of the circles drawn at the entries, one per entry index; the renderer wraps around the list. */
    var circleColors: List<Int> = listOf(Color.rgb(140, 234, 255))

    override var circleHoleColor = Color.WHITE

    override var isDrawHighlightCircleEnabled = false

    override var highlightCircleRadius = 5f

    /** Radius of the circles drawn at the entries in dp. Default 4. Values below 1 are ignored and logged. */
    override var circleRadius = 4f
        set(value) {
            if (value >= 1f) {
                field = value
            } else {
                Log.e("LineDataSet<D>", "Circle radius cannot be < 1")
            }
        }

    /** Radius of the hole inside each circle in dp. Default 2. Values below 0.5 are ignored and logged. */
    override var circleHoleRadius = 2f
        set(value) {
            if (value >= 0.5f) {
                field = value
            } else {
                Log.e("LineDataSet<D>", "Circle radius cannot be < 0.5")
            }
        }

    /** How strongly [Mode.CUBIC_BEZIER] curves the line, clamped to 0.05 (almost straight) to 1. Default 0.2. */
    override var cubicIntensity = 0.2f
        set(value) {
            field = value.coerceIn(0.05f, 1f)
        }

    /** Dash pattern of the line, or null for a solid line. Set through [enableDashedLine]. */
    override var dashPathEffect: DashPathEffect? = null
        protected set

    override var fillFormatter: IFillFormatter = DefaultFillFormatter()

    override var isDrawCirclesEnabled = true

    override var isDrawCircleHoleEnabled = true

    override fun copy(): DataSet<Entry<D>> {
        val copiedEntries = entries.mapTo(mutableListOf()) { it.copy() }
        val copied = LineDataSet(copiedEntries, label)
        copy(copied)
        return copied
    }

    /** Copies the styling from this set into [lineDataSet]; entries are not copied here. */
    protected fun copy(lineDataSet: LineDataSet<D>) {
        super.copy(lineDataSet)
        lineDataSet.circleColors = circleColors
        lineDataSet.circleHoleColor = circleHoleColor
        lineDataSet.circleHoleRadius = circleHoleRadius
        lineDataSet.circleRadius = circleRadius
        lineDataSet.cubicIntensity = cubicIntensity
        lineDataSet.dashPathEffect = dashPathEffect
        lineDataSet.isDrawCircleHoleEnabled = isDrawCircleHoleEnabled
        lineDataSet.isDrawCirclesEnabled = isDrawCirclesEnabled
        lineDataSet.fillFormatter = fillFormatter
        lineDataSet.mode = mode
        lineDataSet.isDrawHighlightCircleEnabled = isDrawHighlightCircleEnabled
        lineDataSet.highlightCircleRadius = highlightCircleRadius
    }

    /**
     * Draws the line dashed, like "- - - -". The renderer draws a dashed line through its own bitmap layer,
     * so hardware acceleration can stay on.
     *
     * @param lineLength length of each dash in px.
     * @param spaceLength length of the gap between dashes in px.
     * @param phase offset into the pattern in px, normally 0.
     */
    fun enableDashedLine(lineLength: Float, spaceLength: Float, phase: Float) {
        dashPathEffect = DashPathEffect(floatArrayOf(lineLength, spaceLength), phase)
    }

    /** Draws the line solid again. */
    fun disableDashedLine() {
        dashPathEffect = null
    }

    override val isDashedLineEnabled: Boolean
        get() = dashPathEffect != null

    /**
     * Returns the circle color at [index], wrapping around when the index is past the end of [circleColors].
     */
    override fun getCircleColor(index: Int): Int = circleColors[index % circleColors.size]

    override val circleColorCount: Int
        get() = circleColors.size

    /** Replaces [circleColors] with the given ARGB colors. */
    fun setCircleColors(vararg colors: Int) {
        circleColors = colors.toList()
    }

    /**
     * Replaces [circleColors] with colors resolved from resource ids.
     *
     * @param colorResIds color resource ids, for example `R.color.red`.
     * @param context used to resolve the ids.
     */
    fun setCircleColors(colorResIds: List<Int>, context: Context) {
        circleColors = colorResIds.map { context.getColor(it) }
    }

    /** The first circle color. Assigning replaces the whole [circleColors] list with this one color. */
    var circleColor: Int
        get() = circleColors[0]
        set(value) {
            circleColors = listOf(value)
        }

    /** Removes all circle colors. Add at least one before circles are drawn. */
    fun resetCircleColors() {
        circleColors = emptyList()
    }

    /** How the segments between entries are drawn. */
    enum class Mode {
        /** Straight segments from point to point. */
        LINEAR,
        /** Horizontal steps, the y value changes at each entry. */
        STEPPED,
        /** Smooth curve through the points, bent by [cubicIntensity]. */
        CUBIC_BEZIER,
        /** Smooth curve whose control points are horizontal, so the curve never overshoots in y. */
        HORIZONTAL_BEZIER
    }
}
