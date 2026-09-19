package com.github.mikephil.charting.components

import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint

/**
 * A line at a fixed value on an axis with an optional label, for example a target or a threshold. Add it with
 * [AxisBase.addLimitLine]: on a [YAxis] it runs horizontally, on an [XAxis] vertically.
 * @property limit the value in value space at which the line is drawn.
 * @property label text drawn next to the line; an empty string draws no label.
 */
open class LimitLine(val limit: Float, var label: String = "") : ComponentBase() {

    /** Width of the line in dp, clamped to 0.2..12. Thicker lines cost more performance. Default 1. */
    var lineWidth = 1f
        set(value) {
            field = value.coerceIn(0.2f, 12f)
        }

    /** Color of the line. Default a light red. */
    var lineColor = Color.rgb(237, 91, 91)

    /** Paint style used for the label text. Default [Paint.Style.FILL_AND_STROKE]. */
    var textStyle = Paint.Style.FILL_AND_STROKE

    /** Dash pattern of the line, or null for a solid line. See [enableDashedLine]. */
    var dashPathEffect: DashPathEffect? = null

    /** Where the label is drawn relative to the line. Not supported by the radar chart. Default [LimitLabelPosition.RIGHT_TOP]. */
    var labelPosition = LimitLabelPosition.RIGHT_TOP

    /**
     * Placement of the label. For a horizontal line on a y-axis, LEFT and RIGHT pick the end of the line and TOP and
     * BOTTOM the side of it. For a vertical line on an x-axis, LEFT and RIGHT pick the side of the line and TOP and
     * BOTTOM the end.
     */
    enum class LimitLabelPosition {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    /**
     * Draws the line dashed.
     * @param lineLength length of each dash in px
     * @param spaceLength length of the gap between two dashes in px
     * @param phase offset into the dash pattern in px, normally 0
     */
    fun enableDashedLine(lineLength: Float, spaceLength: Float, phase: Float) {
        dashPathEffect = DashPathEffect(floatArrayOf(lineLength, spaceLength), phase)
    }

    /** Draws the line solid again. */
    fun disableDashedLine() {
        dashPathEffect = null
    }

    /** Whether the line is drawn dashed. */
    val isDashedLineEnabled: Boolean
        get() = dashPathEffect != null
}
