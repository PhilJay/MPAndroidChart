package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.drawable.Drawable
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Base class of the line and radar renderers, which can fill the area enclosed by their line.
 */
public abstract class LineRadarRenderer(animator: ChartAnimator, viewPortHandler: ViewPortHandler) : LineScatterCandleRadarRenderer(animator, viewPortHandler) {

    /**
     * Draws [drawable] stretched over the content rectangle, clipped to [filledPath] (pixels).
     */
    protected fun drawFilledPath(c: Canvas, filledPath: Path, drawable: Drawable) {
        val save = c.save()
        c.clipPath(filledPath)

        drawable.setBounds(
            viewPortHandler.contentLeft.toInt(),
            viewPortHandler.contentTop.toInt(),
            viewPortHandler.contentRight.toInt(),
            viewPortHandler.contentBottom.toInt()
        )
        drawable.draw(c)

        c.restoreToCount(save)
    }

    /**
     * Fills [filledPath] (pixels) with [fillColor] at [fillAlpha] (0 to 255). The alpha of the color itself is
     * ignored.
     */
    protected fun drawFilledPath(c: Canvas, filledPath: Path, fillColor: Int, fillAlpha: Int) {
        val color = (fillAlpha shl 24) or (fillColor and 0xffffff)

        val save = c.save()
        c.clipPath(filledPath)
        c.drawColor(color)
        c.restoreToCount(save)
    }
}
