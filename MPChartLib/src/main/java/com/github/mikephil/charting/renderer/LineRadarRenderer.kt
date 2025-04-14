package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

abstract class LineRadarRenderer(animator: ChartAnimator?, viewPortHandler: ViewPortHandler?) :
    LineScatterCandleRadarRenderer(animator, viewPortHandler) {
    /**
     * Draws the provided path in filled mode with the provided drawable.
     *
     * @param c
     * @param filledPath
     * @param drawable
     */
    protected fun drawFilledPath(c: Canvas, filledPath: Path, drawable: Drawable) {
        if (clipPathSupported()) {
            val save = c.save()
            c.clipPath(filledPath)

            drawable.setBounds(
                viewPortHandler.contentLeft().toInt(),
                viewPortHandler.contentTop().toInt(),
                viewPortHandler.contentRight().toInt(),
                viewPortHandler.contentBottom().toInt()
            )
            drawable.draw(c)

            c.restoreToCount(save)
        } else {
            throw RuntimeException(
                "Fill-drawables not (yet) supported below API level 18, " +
                        "this code was run on API level " + Utils.getSDKInt() + "."
            )
        }
    }

    /**
     * Draws the provided path in filled mode with the provided color and alpha.
     * Special thanks to Angelo Suzuki (https://github.com/tinsukE) for this.
     *
     * @param c
     * @param filledPath
     * @param fillColor
     * @param fillAlpha
     */
    protected fun drawFilledPath(c: Canvas, filledPath: Path, fillColor: Int, fillAlpha: Int) {
        val color = (fillAlpha shl 24) or (fillColor and 0xffffff)

        if (clipPathSupported()) {
            val save = c.save()

            c.clipPath(filledPath)

            c.drawColor(color)
            c.restoreToCount(save)
        } else {
            // save

            val previous = paintRender.style
            val previousColor = paintRender.color

            // set
            paintRender.style = Paint.Style.FILL
            paintRender.color = color

            c.drawPath(filledPath, paintRender)

            // restore
            paintRender.color = previousColor
            paintRender.style = previous
        }
    }

    /**
     * Clip path with hardware acceleration only working properly on API level 18 and above.
     *
     * @return
     */
    private fun clipPathSupported(): Boolean {
        return Utils.getSDKInt() >= 18
    }
}
