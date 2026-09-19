package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Path
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.HighlightLineSpan
import com.github.mikephil.charting.interfaces.datasets.ILineScatterCandleRadarDataSet
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Base class of the renderers that mark a selected entry with vertical and horizontal highlight lines.
 */
abstract class LineScatterCandleRadarRenderer(animator: ChartAnimator, viewPortHandler: ViewPortHandler) : BarLineScatterCandleBubbleRenderer(animator, viewPortHandler) {

    private val highlightLinePath = Path()

    /**
     * Draws the highlight lines of [set] through the pixel position ([x], [y]): a vertical line over the content
     * height and a horizontal line over the content width, each only when the data set enables that indicator and
     * each as far as its span allows. Uses the highlight color, line width (dp) and dash effect of the data set
     * on [highlightPaint].
     */
    protected fun drawHighlightLines(c: Canvas, x: Float, y: Float, set: ILineScatterCandleRadarDataSet<*>) {
        highlightPaint.color = set.highlightColor
        highlightPaint.strokeWidth = Utils.convertDpToPixel(set.highlightLineWidth)
        highlightPaint.pathEffect = set.dashPathEffectHighlight

        if (set.isVerticalHighlightIndicatorEnabled) {
            val from = when (set.verticalHighlightIndicatorSpan) {
                HighlightLineSpan.FROM_ENTRY -> y
                else -> viewPortHandler.contentBottom
            }
            val to = when (set.verticalHighlightIndicatorSpan) {
                HighlightLineSpan.TO_ENTRY -> y
                else -> viewPortHandler.contentTop
            }

            highlightLinePath.reset()
            highlightLinePath.moveTo(x, from)
            highlightLinePath.lineTo(x, to)
            c.drawPath(highlightLinePath, highlightPaint)
        }

        if (set.isHorizontalHighlightIndicatorEnabled) {
            val from = when (set.horizontalHighlightIndicatorSpan) {
                HighlightLineSpan.FROM_ENTRY -> x
                else -> viewPortHandler.contentLeft
            }
            val to = when (set.horizontalHighlightIndicatorSpan) {
                HighlightLineSpan.TO_ENTRY -> x
                else -> viewPortHandler.contentRight
            }

            highlightLinePath.reset()
            highlightLinePath.moveTo(from, y)
            highlightLinePath.lineTo(to, y)
            c.drawPath(highlightLinePath, highlightPaint)
        }
    }
}
