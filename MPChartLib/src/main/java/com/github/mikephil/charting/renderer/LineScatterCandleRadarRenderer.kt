package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Path
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.datasets.ILineScatterCandleRadarDataSet
import com.github.mikephil.charting.utils.ViewPortHandler

abstract class LineScatterCandleRadarRenderer(animator: ChartAnimator?, viewPortHandler: ViewPortHandler?) :
    BarLineScatterCandleBubbleRenderer(animator, viewPortHandler) {
    /**
     * path that is used for drawing highlight-lines (drawLines(...) cannot be used because of dashes)
     */
    private val highlightLinePath = Path()

    /**
     * Draws vertical & horizontal highlight-lines if enabled.
     *
     * @param c
     * @param x x-position of the highlight line intersection
     * @param y y-position of the highlight line intersection
     * @param set the currently drawn dataset
     */
    protected fun drawHighlightLines(c: Canvas, x: Float, y: Float, set: ILineScatterCandleRadarDataSet<*>) {
        // set color and stroke-width

        highlightPaint.color = set.highLightColor
        highlightPaint.strokeWidth = set.highlightLineWidth

        // draw highlighted lines (if enabled)
        highlightPaint.setPathEffect(set.dashPathEffectHighlight)

        // draw vertical highlight lines
        if (set.isVerticalHighlightIndicatorEnabled) {
            // create vertical path

            highlightLinePath.reset()
            highlightLinePath.moveTo(x, viewPortHandler.contentTop())
            highlightLinePath.lineTo(x, viewPortHandler.contentBottom())

            c.drawPath(highlightLinePath, highlightPaint)
        }

        // draw horizontal highlight lines
        if (set.isHorizontalHighlightIndicatorEnabled) {
            // create horizontal path

            highlightLinePath.reset()
            highlightLinePath.moveTo(viewPortHandler.contentLeft(), y)
            highlightLinePath.lineTo(viewPortHandler.contentRight(), y)

            c.drawPath(highlightLinePath, highlightPaint)
        }
    }
}
