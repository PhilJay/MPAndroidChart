package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Draws a y axis of charts with axes: the labels along the left or right edge (inside or outside the content
 * rectangle), horizontal grid lines, the zero line, the axis line and horizontal limit lines. Assign a subclass
 * to `chart.rendererLeftYAxis` or `chart.rendererRightYAxis` to change the drawing.
 *
 * @property yAxis The y axis this renderer draws.
 */
open class YAxisRenderer(viewPortHandler: ViewPortHandler, protected val yAxis: YAxis, transformer: Transformer?) :
    AxisRenderer(viewPortHandler, transformer, yAxis) {

    /**
     * Paint for the line at the value 0; color and width (dp) are copied from the axis when it is drawn.
     */
    protected val zeroLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    init {
        paintAxisLabels.color = Color.BLACK
        paintAxisLabels.textSize = Utils.convertDpToPixel(10f)
    }

    override fun renderAxisLabels(c: Canvas) {
        if (!yAxis.isEnabled || !yAxis.isDrawLabelsEnabled) return

        val positions = getTransformedPositions()

        paintAxisLabels.typeface = yAxis.typeface
        paintAxisLabels.textSize = Utils.convertDpToPixel(yAxis.textSize)
        paintAxisLabels.color = yAxis.textColor

        val xoffset = Utils.convertDpToPixel(yAxis.xOffset)
        val yoffset = Utils.calcTextHeight(paintAxisLabels, "A") / 2.5f + Utils.convertDpToPixel(yAxis.yOffset)

        val dependency = yAxis.axisDependency
        val labelPosition = yAxis.labelPosition

        val xPos: Float
        if (dependency == AxisDependency.LEFT) {
            if (labelPosition == YAxisLabelPosition.OUTSIDE_CHART) {
                paintAxisLabels.textAlign = Paint.Align.RIGHT
                xPos = viewPortHandler.offsetLeft - xoffset
            } else {
                paintAxisLabels.textAlign = Paint.Align.LEFT
                xPos = viewPortHandler.offsetLeft + xoffset
            }
        } else {
            if (labelPosition == YAxisLabelPosition.OUTSIDE_CHART) {
                paintAxisLabels.textAlign = Paint.Align.LEFT
                xPos = viewPortHandler.contentRight + xoffset
            } else {
                paintAxisLabels.textAlign = Paint.Align.RIGHT
                xPos = viewPortHandler.contentRight - xoffset
            }
        }

        drawYLabels(c, xPos, positions, yoffset)
    }

    override fun renderAxisLine(c: Canvas) {
        if (!yAxis.isEnabled || !yAxis.isDrawAxisLineEnabled) return

        paintAxisLine.color = yAxis.axisLineColor
        paintAxisLine.strokeWidth = Utils.convertDpToPixel(yAxis.axisLineWidth)

        if (yAxis.axisDependency == AxisDependency.LEFT) {
            c.drawLine(viewPortHandler.contentLeft, viewPortHandler.contentTop, viewPortHandler.contentLeft, viewPortHandler.contentBottom, paintAxisLine)
        } else {
            c.drawLine(viewPortHandler.contentRight, viewPortHandler.contentTop, viewPortHandler.contentRight, viewPortHandler.contentBottom, paintAxisLine)
        }
    }

    /**
     * Draws the axis labels at the horizontal pixel position [fixedPosition], plus the label x offset of the axis.
     * Skips the first or last entry when the axis disables drawing the bottom or top label.
     *
     * @param positions pixel positions of the axis entries as x, y pairs, from [getTransformedPositions]
     * @param offset vertical pixel offset that centers the text on its grid line
     */
    protected open fun drawYLabels(c: Canvas, fixedPosition: Float, positions: FloatArray, offset: Float) {
        val from = if (yAxis.isDrawBottomYLabelEntryEnabled) 0 else 1
        // The positions were built from the entry count, which another thread can change in between.
        val to = minOf(if (yAxis.isDrawTopYLabelEntryEnabled) yAxis.entryCount else yAxis.entryCount - 1, positions.size / 2)

        val xOffset = Utils.convertDpToPixel(yAxis.labelXOffset)

        for (i in from until to) {
            val text = yAxis.getFormattedLabel(i)
            c.drawText(text, fixedPosition + xOffset, positions[i * 2 + 1] + offset, paintAxisLabels)
        }
    }

    /**
     * Path reused for drawing grid lines.
     */
    protected val renderGridLinesPath = Path()

    /**
     * Draws one horizontal grid line per axis entry, clipped to [gridClippingRect], and then the zero line when the
     * axis enables it.
     */
    override fun renderGridLines(c: Canvas) {
        if (!yAxis.isEnabled) return

        if (yAxis.isDrawGridLinesEnabled) {
            val clipRestoreCount = c.save()
            c.clipRect(gridClippingRect)

            val positions = getTransformedPositions()

            paintGrid.color = yAxis.gridColor
            paintGrid.strokeWidth = Utils.convertDpToPixel(yAxis.gridLineWidth)
            paintGrid.pathEffect = yAxis.gridDashPathEffect

            val gridLinePath = renderGridLinesPath
            gridLinePath.reset()

            for (i in positions.indices step 2) {
                c.drawPath(linePath(gridLinePath, i, positions), paintGrid)
                gridLinePath.reset()
            }

            c.restoreToCount(clipRestoreCount)
        }

        if (yAxis.isDrawZeroLineEnabled) {
            drawZeroLine(c)
        }
    }

    /**
     * Backing rectangle of [gridClippingRect].
     */
    protected val gridClippingRectBuffer = RectF()

    /**
     * The content rectangle widened vertically by the grid line width, in pixels, so grid lines at the edges are
     * not cut in half.
     */
    open val gridClippingRect: RectF
        get() {
            gridClippingRectBuffer.set(viewPortHandler.contentRect)
            gridClippingRectBuffer.inset(0f, -Utils.convertDpToPixel(axis.gridLineWidth))
            return gridClippingRectBuffer
        }

    /**
     * Builds the grid line of the entry at pair index [i] of [positions] (pixel x, y pairs) into [p] and returns it.
     * The line runs from the left chart offset to the right edge of the content rectangle.
     */
    protected open fun linePath(p: Path, i: Int, positions: FloatArray): Path {
        p.moveTo(viewPortHandler.offsetLeft, positions[i + 1])
        p.lineTo(viewPortHandler.contentRight, positions[i + 1])
        return p
    }

    /**
     * Buffer reused by [getTransformedPositions]; resized to twice the entry count of the axis.
     */
    protected var getTransformedPositionsBuffer = FloatArray(2)

    /**
     * Returns the pixel positions of all axis entries as x, y pairs. Only the y values are filled; the x values
     * stay 0. The array is reused between calls.
     */
    protected open fun getTransformedPositions(): FloatArray {
        if (getTransformedPositionsBuffer.size != yAxis.entryCount * 2) {
            getTransformedPositionsBuffer = FloatArray(yAxis.entryCount * 2)
        }
        val positions = getTransformedPositionsBuffer

        for (i in positions.indices step 2) {
            positions[i + 1] = yAxis.entries[i / 2]
        }

        transformer?.pointValuesToPixel(positions)
        return positions
    }

    /**
     * Path reused for the zero line.
     */
    protected val drawZeroLinePath = Path()

    /**
     * Clip rectangle for the zero line, the content rectangle widened by the zero line width.
     */
    protected val zeroLineClippingRect = RectF()

    /**
     * Draws a horizontal line over the content width at the pixel position of the value 0, with the zero line
     * color and width (dp) of the axis. Does nothing without a transformer.
     */
    protected open fun drawZeroLine(c: Canvas) {
        val transformer = transformer ?: return

        val clipRestoreCount = c.save()
        zeroLineClippingRect.set(viewPortHandler.contentRect)
        zeroLineClippingRect.inset(0f, -Utils.convertDpToPixel(yAxis.zeroLineWidth))
        c.clipRect(zeroLineClippingRect)

        val pos = transformer.getPixelForValues(0f, 0f)

        zeroLinePaint.color = yAxis.zeroLineColor
        zeroLinePaint.strokeWidth = Utils.convertDpToPixel(yAxis.zeroLineWidth)

        val zeroLinePath = drawZeroLinePath
        zeroLinePath.reset()

        zeroLinePath.moveTo(viewPortHandler.contentLeft, pos.y.toFloat())
        zeroLinePath.lineTo(viewPortHandler.contentRight, pos.y.toFloat())

        c.drawPath(zeroLinePath, zeroLinePaint)

        c.restoreToCount(clipRestoreCount)
    }

    /**
     * Path reused for drawing limit lines.
     */
    protected val renderLimitLines = Path()

    /**
     * Buffer for the pixel position of the limit line being drawn.
     */
    protected val renderLimitLinesBuffer = FloatArray(2)

    /**
     * Clip rectangle for the limit line being drawn, the content rectangle widened by the line width.
     */
    protected val limitLineClippingRect = RectF()

    /**
     * Draws each enabled limit line as a horizontal line at the pixel y of its limit value and its label at the
     * left or right of the content rectangle, above or below the line.
     */
    override fun renderLimitLines(c: Canvas) {
        val limitLines = yAxis.limitLines
        if (limitLines.isEmpty()) return

        val pts = renderLimitLinesBuffer
        pts[0] = 0f
        pts[1] = 0f
        val limitLinePath = renderLimitLines
        limitLinePath.reset()

        for (l in limitLines) {
            if (!l.isEnabled) continue

            val clipRestoreCount = c.save()
            limitLineClippingRect.set(viewPortHandler.contentRect)
            limitLineClippingRect.inset(0f, -Utils.convertDpToPixel(l.lineWidth))
            c.clipRect(limitLineClippingRect)

            limitLinePaint.style = Paint.Style.STROKE
            limitLinePaint.color = l.lineColor
            limitLinePaint.strokeWidth = Utils.convertDpToPixel(l.lineWidth)
            limitLinePaint.pathEffect = l.dashPathEffect

            pts[1] = l.limit

            transformer?.pointValuesToPixel(pts)

            limitLinePath.moveTo(viewPortHandler.contentLeft, pts[1])
            limitLinePath.lineTo(viewPortHandler.contentRight, pts[1])

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

                val labelLineHeight = Utils.calcTextHeight(limitLinePaint, label).toFloat()
                val xOffset = Utils.convertDpToPixel(4f) + Utils.convertDpToPixel(l.xOffset)
                val yOffset = Utils.convertDpToPixel(l.lineWidth) + labelLineHeight + Utils.convertDpToPixel(l.yOffset)

                when (l.labelPosition) {
                    LimitLine.LimitLabelPosition.RIGHT_TOP -> {
                        limitLinePaint.textAlign = Paint.Align.RIGHT
                        c.drawText(label, viewPortHandler.contentRight - xOffset, pts[1] - yOffset + labelLineHeight, limitLinePaint)
                    }
                    LimitLine.LimitLabelPosition.RIGHT_BOTTOM -> {
                        limitLinePaint.textAlign = Paint.Align.RIGHT
                        c.drawText(label, viewPortHandler.contentRight - xOffset, pts[1] + yOffset, limitLinePaint)
                    }
                    LimitLine.LimitLabelPosition.LEFT_TOP -> {
                        limitLinePaint.textAlign = Paint.Align.LEFT
                        c.drawText(label, viewPortHandler.contentLeft + xOffset, pts[1] - yOffset + labelLineHeight, limitLinePaint)
                    }
                    LimitLine.LimitLabelPosition.LEFT_BOTTOM -> {
                        limitLinePaint.textAlign = Paint.Align.LEFT
                        c.drawText(label, viewPortHandler.offsetLeft + xOffset, pts[1] + yOffset, limitLinePaint)
                    }
                }
            }

            c.restoreToCount(clipRestoreCount)
        }
    }
}
