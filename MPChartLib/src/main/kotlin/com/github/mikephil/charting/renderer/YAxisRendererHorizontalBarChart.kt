package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Draws a y axis of a horizontal bar chart, where the y axis runs horizontally: the left axis along the top
 * edge and the right axis along the bottom edge, with vertical grid lines, a vertical zero line and vertical
 * limit lines.
 */
open class YAxisRendererHorizontalBarChart(viewPortHandler: ViewPortHandler, yAxis: YAxis, transformer: Transformer?) :
    YAxisRenderer(viewPortHandler, yAxis, transformer) {

    init {
        limitLinePaint.textAlign = Paint.Align.LEFT
    }

    /**
     * Takes the visible range from the left and right edge of the content rectangle, because the y axis runs
     * horizontally here.
     */
    override fun computeAxis(min: Float, max: Float, inverted: Boolean) {
        var yMin = min
        var yMax = max

        val transformer = transformer
        if (transformer != null && viewPortHandler.contentHeight > 10 && !viewPortHandler.isFullyZoomedOutX) {
            val p1 = computeAxisPoint1
            val p2 = computeAxisPoint2
            transformer.getValuesByTouchPoint(viewPortHandler.contentLeft, viewPortHandler.contentTop, p1)
            transformer.getValuesByTouchPoint(viewPortHandler.contentRight, viewPortHandler.contentTop, p2)

            if (!inverted) {
                yMin = p1.x.toFloat()
                yMax = p2.x.toFloat()
            } else {
                yMin = p2.x.toFloat()
                yMax = p1.x.toFloat()
            }

        }

        computeAxisValues(yMin, yMax)
    }

    /**
     * Draws the labels centered above the content rectangle for the left axis and below it for the right axis.
     */
    override fun renderAxisLabels(c: Canvas) {
        if (!yAxis.isEnabled || !yAxis.isDrawLabelsEnabled) return

        val positions = getTransformedPositions()

        paintAxisLabels.typeface = yAxis.typeface
        paintAxisLabels.textSize = Utils.convertDpToPixel(yAxis.textSize)
        paintAxisLabels.color = yAxis.textColor
        paintAxisLabels.textAlign = Paint.Align.CENTER

        val baseYOffset = Utils.convertDpToPixel(2.5f)
        val textHeight = Utils.calcTextHeight(paintAxisLabels, "Q").toFloat()

        val dependency = yAxis.axisDependency

        val yPos = if (dependency == AxisDependency.LEFT) {
            viewPortHandler.contentTop - baseYOffset
        } else {
            viewPortHandler.contentBottom + textHeight + baseYOffset
        }

        drawYLabels(c, yPos, positions, Utils.convertDpToPixel(yAxis.yOffset))
    }

    /**
     * Draws the axis line along the top edge for the left axis and along the bottom edge for the right axis.
     */
    override fun renderAxisLine(c: Canvas) {
        if (!yAxis.isEnabled || !yAxis.isDrawAxisLineEnabled) return

        paintAxisLine.color = yAxis.axisLineColor
        paintAxisLine.strokeWidth = Utils.convertDpToPixel(yAxis.axisLineWidth)

        if (yAxis.axisDependency == AxisDependency.LEFT) {
            c.drawLine(viewPortHandler.contentLeft, viewPortHandler.contentTop, viewPortHandler.contentRight, viewPortHandler.contentTop, paintAxisLine)
        } else {
            c.drawLine(viewPortHandler.contentLeft, viewPortHandler.contentBottom, viewPortHandler.contentRight, viewPortHandler.contentBottom, paintAxisLine)
        }
    }

    /**
     * Draws the labels at the vertical pixel position [fixedPosition] minus [offset], one per entry along the
     * horizontal direction.
     */
    override fun drawYLabels(c: Canvas, fixedPosition: Float, positions: FloatArray, offset: Float) {
        paintAxisLabels.typeface = yAxis.typeface
        paintAxisLabels.textSize = Utils.convertDpToPixel(yAxis.textSize)
        paintAxisLabels.color = yAxis.textColor

        val from = if (yAxis.isDrawBottomYLabelEntryEnabled) 0 else 1
        // The positions were built from the entry count, which another thread can change in between.
        val to = minOf(if (yAxis.isDrawTopYLabelEntryEnabled) yAxis.entryCount else yAxis.entryCount - 1, positions.size / 2)

        val xOffset = Utils.convertDpToPixel(yAxis.labelXOffset)

        for (i in from until to) {
            val text = yAxis.getFormattedLabel(i)
            c.drawText(text, positions[i * 2], fixedPosition - offset + xOffset, paintAxisLabels)
        }
    }

    /**
     * Fills the x values of the pairs instead of the y values.
     */
    override fun getTransformedPositions(): FloatArray {
        if (getTransformedPositionsBuffer.size != yAxis.entryCount * 2) {
            getTransformedPositionsBuffer = FloatArray(yAxis.entryCount * 2)
        }
        val positions = getTransformedPositionsBuffer

        for (i in positions.indices step 2) {
            positions[i] = yAxis.entries[i / 2]
        }

        transformer?.pointValuesToPixel(positions)
        return positions
    }

    /**
     * Widened horizontally instead of vertically, because the grid lines are vertical here.
     */
    override val gridClippingRect: RectF
        get() {
            gridClippingRectBuffer.set(viewPortHandler.contentRect)
            gridClippingRectBuffer.inset(-Utils.convertDpToPixel(axis.gridLineWidth), 0f)
            return gridClippingRectBuffer
        }

    /**
     * Builds a vertical grid line at the pixel x of the entry over the content height.
     */
    override fun linePath(p: Path, i: Int, positions: FloatArray): Path {
        p.moveTo(positions[i], viewPortHandler.contentTop)
        p.lineTo(positions[i], viewPortHandler.contentBottom)
        return p
    }

    private val drawZeroLinePathBuffer = Path()

    /**
     * Draws a vertical line over the content height at the pixel position of the value 0.
     */
    override fun drawZeroLine(c: Canvas) {
        val transformer = transformer ?: return

        val clipRestoreCount = c.save()
        zeroLineClippingRect.set(viewPortHandler.contentRect)
        zeroLineClippingRect.inset(-Utils.convertDpToPixel(yAxis.zeroLineWidth), 0f)
        c.clipRect(zeroLineClippingRect)

        val pos = transformer.getPixelForValues(0f, 0f)

        zeroLinePaint.color = yAxis.zeroLineColor
        zeroLinePaint.strokeWidth = Utils.convertDpToPixel(yAxis.zeroLineWidth)

        val zeroLinePath = drawZeroLinePathBuffer
        zeroLinePath.reset()

        zeroLinePath.moveTo(pos.x.toFloat() - 1, viewPortHandler.contentTop)
        zeroLinePath.lineTo(pos.x.toFloat() - 1, viewPortHandler.contentBottom)

        c.drawPath(zeroLinePath, zeroLinePaint)

        c.restoreToCount(clipRestoreCount)
    }

    private val renderLimitLinesPathBuffer = Path()

    private val limitLinesBuffer = FloatArray(4)

    /**
     * Draws each enabled limit line as a vertical line at the pixel x of its limit value and its label at the top
     * or bottom of the content rectangle.
     */
    override fun renderLimitLines(c: Canvas) {
        val limitLines = yAxis.limitLines
        if (limitLines.isEmpty()) return

        val pts = limitLinesBuffer
        pts.fill(0f)
        val limitLinePath = renderLimitLinesPathBuffer
        limitLinePath.reset()

        for (l in limitLines) {
            if (!l.isEnabled) continue

            val clipRestoreCount = c.save()
            limitLineClippingRect.set(viewPortHandler.contentRect)
            limitLineClippingRect.inset(-Utils.convertDpToPixel(l.lineWidth), 0f)
            c.clipRect(limitLineClippingRect)

            pts[0] = l.limit
            pts[2] = l.limit

            transformer?.pointValuesToPixel(pts)

            pts[1] = viewPortHandler.contentTop
            pts[3] = viewPortHandler.contentBottom

            limitLinePath.moveTo(pts[0], pts[1])
            limitLinePath.lineTo(pts[2], pts[3])

            limitLinePaint.style = Paint.Style.STROKE
            limitLinePaint.color = l.lineColor
            limitLinePaint.pathEffect = l.dashPathEffect
            limitLinePaint.strokeWidth = Utils.convertDpToPixel(l.lineWidth)

            c.drawPath(limitLinePath, limitLinePaint)
            limitLinePath.reset()

            val label = l.label

            if (label.isNotEmpty()) {
                limitLinePaint.style = l.textStyle
                limitLinePaint.pathEffect = null
                limitLinePaint.color = l.textColor
                limitLinePaint.typeface = l.typeface
                limitLinePaint.strokeWidth = 0.5f
                limitLinePaint.textSize = Utils.convertDpToPixel(l.textSize)

                val xOffset = Utils.convertDpToPixel(l.lineWidth) + Utils.convertDpToPixel(l.xOffset)
                val yOffset = Utils.convertDpToPixel(2f) + Utils.convertDpToPixel(l.yOffset)

                when (l.labelPosition) {
                    LimitLine.LimitLabelPosition.RIGHT_TOP -> {
                        val labelLineHeight = Utils.calcTextHeight(limitLinePaint, label).toFloat()
                        limitLinePaint.textAlign = Paint.Align.LEFT
                        c.drawText(label, pts[0] + xOffset, viewPortHandler.contentTop + yOffset + labelLineHeight, limitLinePaint)
                    }
                    LimitLine.LimitLabelPosition.RIGHT_BOTTOM -> {
                        limitLinePaint.textAlign = Paint.Align.LEFT
                        c.drawText(label, pts[0] + xOffset, viewPortHandler.contentBottom - yOffset, limitLinePaint)
                    }
                    LimitLine.LimitLabelPosition.LEFT_TOP -> {
                        limitLinePaint.textAlign = Paint.Align.RIGHT
                        val labelLineHeight = Utils.calcTextHeight(limitLinePaint, label).toFloat()
                        c.drawText(label, pts[0] - xOffset, viewPortHandler.contentTop + yOffset + labelLineHeight, limitLinePaint)
                    }
                    LimitLine.LimitLabelPosition.LEFT_BOTTOM -> {
                        limitLinePaint.textAlign = Paint.Align.RIGHT
                        c.drawText(label, pts[0] - xOffset, viewPortHandler.contentBottom - yOffset, limitLinePaint)
                    }
                }
            }

            c.restoreToCount(clipRestoreCount)
        }
    }
}
