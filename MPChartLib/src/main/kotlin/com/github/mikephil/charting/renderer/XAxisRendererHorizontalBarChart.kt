package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.utils.FSize
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.roundToInt

/**
 * Draws the x axis of a horizontal bar chart, where the x axis runs vertically: the labels left or right of the
 * content rectangle, horizontal grid lines, the axis line along the left or right edge and horizontal limit
 * lines. The x axis positions TOP and BOTTOM map to the right and left edge.
 *
 * @property chart The bar chart whose x axis is drawn.
 */
public open class XAxisRendererHorizontalBarChart(
    viewPortHandler: ViewPortHandler,
    xAxis: XAxis,
    transformer: Transformer?,
    protected val chart: BarChart
) : XAxisRenderer(viewPortHandler, xAxis, transformer) {

    /** The labels run down the side here, so their width places no limit on the interval. */
    override fun minimumInterval(range: Double): Double = 0.0

    /**
     * Takes the visible range from the top and bottom edge of the content rectangle, because the x axis runs
     * vertically here.
     */
    override fun computeAxis(min: Float, max: Float, inverted: Boolean) {
        var newMin = min
        var newMax = max

        val transformer = transformer
        if (transformer != null && viewPortHandler.contentWidth > 10 && !viewPortHandler.isFullyZoomedOutY) {
            val p1 = computeAxisPoint1
            val p2 = computeAxisPoint2
            transformer.getValuesByTouchPoint(viewPortHandler.contentLeft, viewPortHandler.contentBottom, p1)
            transformer.getValuesByTouchPoint(viewPortHandler.contentLeft, viewPortHandler.contentTop, p2)

            if (inverted) {
                newMin = p2.y.toFloat()
                newMax = p1.y.toFloat()
            } else {
                newMin = p1.y.toFloat()
                newMax = p2.y.toFloat()
            }

        }

        computeAxisValues(newMin, newMax)
    }

    /**
     * Also adds 3.5 times the axis x offset to the stored label widths, to keep the labels clear of the bars.
     */
    override fun computeSize() {
        paintAxisLabels.typeface = xAxis.typeface
        paintAxisLabels.textSize = Utils.convertDpToPixel(xAxis.textSize)

        val longest = xAxis.longestLabel

        val labelSize = Utils.calcTextSize(paintAxisLabels, longest)

        val labelWidth = (labelSize.width + Utils.convertDpToPixel(xAxis.xOffset) * 3.5f).toInt().toFloat()
        val labelHeight = labelSize.height

        val labelRotatedSize = Utils.getSizeOfRotatedRectangleByDegrees(labelSize.width, labelHeight, xAxis.labelRotationAngle)

        xAxis.labelWidth = labelWidth.roundToInt()
        xAxis.labelHeight = labelHeight.roundToInt()
        xAxis.labelRotatedWidth = (labelRotatedSize.width + Utils.convertDpToPixel(xAxis.xOffset) * 3.5f).toInt()
        xAxis.labelRotatedHeight = labelRotatedSize.height.roundToInt()

        FSize.recycleInstance(labelRotatedSize)
    }

    override fun renderAxisLabels(c: Canvas) {
        if (!xAxis.isEnabled || !xAxis.isDrawLabelsEnabled) return

        val xoffset = Utils.convertDpToPixel(xAxis.xOffset)

        paintAxisLabels.typeface = xAxis.typeface
        paintAxisLabels.textSize = Utils.convertDpToPixel(xAxis.textSize)
        paintAxisLabels.color = xAxis.textColor

        val pointF = MPPointF.getInstance(0f, 0f)

        when (xAxis.position) {
            XAxisPosition.TOP -> {
                pointF.x = 0.0f
                pointF.y = 0.5f
                drawLabels(c, viewPortHandler.contentRight + xoffset, pointF)
            }
            XAxisPosition.TOP_INSIDE -> {
                pointF.x = 1.0f
                pointF.y = 0.5f
                drawLabels(c, viewPortHandler.contentRight - xoffset, pointF)
            }
            XAxisPosition.BOTTOM -> {
                pointF.x = 1.0f
                pointF.y = 0.5f
                drawLabels(c, viewPortHandler.contentLeft - xoffset, pointF)
            }
            XAxisPosition.BOTTOM_INSIDE -> {
                pointF.x = 1.0f
                pointF.y = 0.5f
                drawLabels(c, viewPortHandler.contentLeft + xoffset, pointF)
            }
            XAxisPosition.BOTH_SIDED -> {
                pointF.x = 0.0f
                pointF.y = 0.5f
                drawLabels(c, viewPortHandler.contentRight + xoffset, pointF)
                pointF.x = 1.0f
                pointF.y = 0.5f
                drawLabels(c, viewPortHandler.contentLeft - xoffset, pointF)
            }
        }

        MPPointF.recycleInstance(pointF)
    }

    /**
     * Draws the labels at the horizontal pixel position [pos], one per axis entry inside the visible y range.
     */
    override fun drawLabels(c: Canvas, pos: Float, anchor: MPPointF) {
        val labelRotationAngleDegrees = xAxis.labelRotationAngle
        val centeringEnabled = xAxis.isCenterAxisLabelsEnabled

        val positions = FloatArray(xAxis.entryCount * 2)

        for (i in positions.indices step 2) {
            positions[i + 1] = if (centeringEnabled) xAxis.centeredEntries[i / 2] else xAxis.entries[i / 2]
        }

        transformer?.pointValuesToPixel(positions)

        for (i in positions.indices step 2) {
            val y = positions[i + 1]

            if (viewPortHandler.isInBoundsY(y)) {
                val label = xAxis.valueFormatter.getFormattedValue(xAxis.entries[i / 2], xAxis)
                drawLabel(c, label, pos, y, anchor, labelRotationAngleDegrees)
            }
        }
    }

    /**
     * Widened vertically instead of horizontally, because the grid lines are horizontal here.
     */
    override val gridClippingRect: RectF
        get() {
            gridClippingRectBuffer.set(viewPortHandler.contentRect)
            gridClippingRectBuffer.inset(0f, -Utils.convertDpToPixel(axis.gridLineWidth))
            return gridClippingRectBuffer
        }

    /**
     * Draws one horizontal grid line at the pixel position [y] over the content width. [x] is not used here.
     */
    override fun drawGridLine(c: Canvas, x: Float, y: Float, gridLinePath: Path) {
        gridLinePath.moveTo(viewPortHandler.contentRight, y)
        gridLinePath.lineTo(viewPortHandler.contentLeft, y)

        c.drawPath(gridLinePath, paintGrid)

        gridLinePath.reset()
    }

    /**
     * Draws the axis line along the right edge for the TOP positions and along the left edge for the BOTTOM
     * positions.
     */
    override fun renderAxisLine(c: Canvas) {
        if (!xAxis.isDrawAxisLineEnabled || !xAxis.isEnabled) return

        paintAxisLine.color = xAxis.axisLineColor
        paintAxisLine.strokeWidth = Utils.convertDpToPixel(xAxis.axisLineWidth)

        if (xAxis.position == XAxisPosition.TOP || xAxis.position == XAxisPosition.TOP_INSIDE || xAxis.position == XAxisPosition.BOTH_SIDED) {
            c.drawLine(viewPortHandler.contentRight, viewPortHandler.contentTop, viewPortHandler.contentRight, viewPortHandler.contentBottom, paintAxisLine)
        }

        if (xAxis.position == XAxisPosition.BOTTOM || xAxis.position == XAxisPosition.BOTTOM_INSIDE || xAxis.position == XAxisPosition.BOTH_SIDED) {
            c.drawLine(viewPortHandler.contentLeft, viewPortHandler.contentTop, viewPortHandler.contentLeft, viewPortHandler.contentBottom, paintAxisLine)
        }
    }

    private val renderLimitLinesPathBuffer = Path()

    /**
     * Draws each enabled limit line as a horizontal line at the pixel y of its limit value and its label at the
     * left or right of the content rectangle. A label that would stick out of the content rectangle is moved inside
     * it; the label of a line outside the content rectangle is not drawn.
     */
    override fun renderLimitLines(c: Canvas) {
        val limitLines = xAxis.limitLines
        if (limitLines.isEmpty()) return

        val pts = renderLimitLinesBuffer
        pts[0] = 0f
        pts[1] = 0f

        val limitLinePath = renderLimitLinesPathBuffer
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
            val lineVisible = pts[1] >= limitLineClippingRect.top && pts[1] <= limitLineClippingRect.bottom

            if (label.isNotEmpty() && lineVisible) {
                limitLinePaint.style = l.textStyle
                limitLinePaint.pathEffect = null
                limitLinePaint.color = l.textColor
                limitLinePaint.strokeWidth = 0.5f
                limitLinePaint.textSize = Utils.convertDpToPixel(l.textSize)

                val labelLineHeight = Utils.calcTextHeight(limitLinePaint, label).toFloat()
                val xOffset = Utils.convertDpToPixel(4f) + Utils.convertDpToPixel(l.xOffset)
                val yOffset = Utils.convertDpToPixel(l.lineWidth) + labelLineHeight + Utils.convertDpToPixel(l.yOffset)
                val topBaseline = pts[1] - yOffset + labelLineHeight
                val bottomBaseline = pts[1] + yOffset
                val baseline = when (l.labelPosition) {
                    LimitLine.LimitLabelPosition.RIGHT_TOP, LimitLine.LimitLabelPosition.LEFT_TOP -> topBaseline
                    LimitLine.LimitLabelPosition.RIGHT_BOTTOM, LimitLine.LimitLabelPosition.LEFT_BOTTOM -> bottomBaseline
                }.coerceIn(viewPortHandler.contentTop + labelLineHeight, maxOf(viewPortHandler.contentBottom, viewPortHandler.contentTop + labelLineHeight))

                when (l.labelPosition) {
                    LimitLine.LimitLabelPosition.RIGHT_TOP, LimitLine.LimitLabelPosition.RIGHT_BOTTOM -> {
                        limitLinePaint.textAlign = Paint.Align.RIGHT
                        c.drawText(label, viewPortHandler.contentRight - xOffset, baseline, limitLinePaint)
                    }
                    LimitLine.LimitLabelPosition.LEFT_TOP -> {
                        limitLinePaint.textAlign = Paint.Align.LEFT
                        c.drawText(label, viewPortHandler.contentLeft + xOffset, baseline, limitLinePaint)
                    }
                    LimitLine.LimitLabelPosition.LEFT_BOTTOM -> {
                        limitLinePaint.textAlign = Paint.Align.LEFT
                        c.drawText(label, viewPortHandler.offsetLeft + xOffset, baseline, limitLinePaint)
                    }
                }
            }

            c.restoreToCount(clipRestoreCount)
        }
    }
}
