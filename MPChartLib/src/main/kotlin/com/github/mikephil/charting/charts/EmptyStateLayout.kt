package com.github.mikephil.charting.charts

import android.graphics.Paint
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.sin

/**
 * Places the empty state's icon above its text as one block: centered vertically in the view, and horizontally
 * at the left edge, in the middle or at the right edge depending on [align]. Sizes are in pixels.
 */
internal class EmptyStateLayout(
    viewWidth: Float,
    viewHeight: Float,
    iconWidth: Float,
    iconHeight: Float,
    gap: Float,
    textWidth: Float,
    textHeight: Float,
    align: Paint.Align,
) {
    private val blockWidth = max(iconWidth, textWidth)

    /** Horizontal center of the block, which is where the icon is centered. */
    val centerX: Float = when (align) {
        Paint.Align.LEFT -> blockWidth / 2f
        Paint.Align.RIGHT -> viewWidth - blockWidth / 2f
        else -> viewWidth / 2f
    }

    /** Top of the block and of the icon. */
    val top: Float = (viewHeight - iconHeight - gap - textHeight) / 2f

    /** Top of the text line. */
    val textTop: Float = top + iconHeight + gap

    /** X to hand to `drawText` for a paint aligned with [align]. */
    val textX: Float = when (align) {
        Paint.Align.LEFT -> centerX - blockWidth / 2f
        Paint.Align.RIGHT -> centerX + blockWidth / 2f
        else -> centerX
    }
}

/** Share of the icon's alpha while loading at [timeMillis]: it breathes between 30 and 100 percent every 1.4 seconds. */
internal fun loadingPulse(timeMillis: Long): Float =
    (0.65f + 0.35f * sin(timeMillis % LOADING_PULSE_MILLIS / LOADING_PULSE_MILLIS.toDouble() * 2 * PI).toFloat()).coerceIn(0.3f, 1f)

internal const val LOADING_PULSE_MILLIS = 1400L
