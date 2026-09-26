package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
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
 * Draws the x axis of charts with axes: the labels above or below the content rectangle, vertical grid lines,
 * the axis line along the top or bottom edge and vertical limit lines. Assign a subclass to
 * `chart.rendererXAxis` to change the drawing.
 *
 * @property xAxis The x axis this renderer draws.
 */
public open class XAxisRenderer(viewPortHandler: ViewPortHandler, protected val xAxis: XAxis, transformer: Transformer?) :
    AxisRenderer(viewPortHandler, transformer, xAxis) {

    init {
        paintAxisLabels.color = Color.BLACK
        paintAxisLabels.textAlign = Paint.Align.CENTER
        paintAxisLabels.textSize = Utils.convertDpToPixel(10f)
    }

    /**
     * Copies the grid color, grid line width (dp) and dash effect of the axis into [paintGrid].
     */
    protected fun setupGridPaint() {
        paintGrid.color = xAxis.gridColor
        paintGrid.strokeWidth = Utils.convertDpToPixel(xAxis.gridLineWidth)
        paintGrid.pathEffect = xAxis.gridDashPathEffect
    }

    /**
     * Like [AxisRenderer.computeAxis], but along x: when zoomed in horizontally the range is taken from the left and
     * right edge of the content rectangle.
     */
    override fun computeAxis(min: Float, max: Float, inverted: Boolean) {
        var newMin = min
        var newMax = max

        val transformer = transformer
        if (transformer != null && viewPortHandler.contentWidth > 10 && !viewPortHandler.isFullyZoomedOutX) {
            val p1 = computeAxisPoint1
            val p2 = computeAxisPoint2
            transformer.getValuesByTouchPoint(viewPortHandler.contentLeft, viewPortHandler.contentTop, p1)
            transformer.getValuesByTouchPoint(viewPortHandler.contentRight, viewPortHandler.contentTop, p2)

            if (inverted) {
                newMin = p2.x.toFloat()
                newMax = p1.x.toFloat()
            } else {
                newMin = p1.x.toFloat()
                newMax = p2.x.toFloat()
            }

        }

        computeAxisValues(newMin, newMax)
    }

    /**
     * Computes the label values and the label size with [computeSize]. The size is measured first as well, because
     * the interval between two labels is kept at least as wide as a label.
     */
    override fun computeAxisValues(min: Float, max: Float) {
        computeSize()
        super.computeAxisValues(min, max)
        computeSize()
    }

    /** Keeps the labels at least one label width apart, so that neighbouring labels do not overlap. */
    override fun minimumInterval(range: Double): Double {
        val contentWidth = viewPortHandler.contentWidth
        if (contentWidth <= 0f) return 0.0
        return xAxis.labelRotatedWidth * range / contentWidth
    }

    /**
     * Measures the longest label of the axis with its typeface and text size and stores the plain and rotated label
     * width and height (pixels) on the axis, which the chart uses to reserve space for the labels.
     */
    protected open fun computeSize() {
        val longest = xAxis.longestLabel

        paintAxisLabels.typeface = xAxis.typeface
        paintAxisLabels.textSize = Utils.convertDpToPixel(xAxis.textSize)

        val labelSize = Utils.calcTextSize(paintAxisLabels, longest)

        val labelWidth = labelSize.width
        val labelHeight = Utils.calcTextHeight(paintAxisLabels, "Q").toFloat()

        val labelRotatedSize = Utils.getSizeOfRotatedRectangleByDegrees(labelWidth, labelHeight, xAxis.labelRotationAngle)

        xAxis.labelWidth = labelWidth.roundToInt()
        xAxis.labelHeight = labelHeight.roundToInt()
        xAxis.labelRotatedWidth = labelRotatedSize.width.roundToInt()
        xAxis.labelRotatedHeight = labelRotatedSize.height.roundToInt()

        FSize.recycleInstance(labelRotatedSize)
        FSize.recycleInstance(labelSize)
    }

    override fun renderAxisLabels(c: Canvas) {
        if (!xAxis.isEnabled || !xAxis.isDrawLabelsEnabled) return

        val yoffset = Utils.convertDpToPixel(xAxis.yOffset)

        paintAxisLabels.typeface = xAxis.typeface
        paintAxisLabels.textSize = Utils.convertDpToPixel(xAxis.textSize)
        paintAxisLabels.color = xAxis.textColor

        val pointF = MPPointF.getInstance(0f, 0f)
        when (xAxis.position) {
            XAxisPosition.TOP -> {
                pointF.x = 0.5f
                pointF.y = 1.0f
                drawLabels(c, viewPortHandler.contentTop - yoffset, pointF)
            }
            XAxisPosition.TOP_INSIDE -> {
                pointF.x = 0.5f
                pointF.y = 1.0f
                drawLabels(c, viewPortHandler.contentTop + yoffset + xAxis.labelRotatedHeight, pointF)
            }
            XAxisPosition.BOTTOM -> {
                pointF.x = 0.5f
                pointF.y = 0.0f
                drawLabels(c, viewPortHandler.contentBottom + yoffset, pointF)
            }
            XAxisPosition.BOTTOM_INSIDE -> {
                pointF.x = 0.5f
                pointF.y = 0.0f
                drawLabels(c, viewPortHandler.contentBottom - yoffset - xAxis.labelRotatedHeight, pointF)
            }
            XAxisPosition.BOTH_SIDED -> {
                pointF.x = 0.5f
                pointF.y = 1.0f
                drawLabels(c, viewPortHandler.contentTop - yoffset, pointF)
                pointF.x = 0.5f
                pointF.y = 0.0f
                drawLabels(c, viewPortHandler.contentBottom + yoffset, pointF)
            }
        }
        MPPointF.recycleInstance(pointF)
    }

    override fun renderAxisLine(c: Canvas) {
        if (!xAxis.isDrawAxisLineEnabled || !xAxis.isEnabled) return

        paintAxisLine.color = xAxis.axisLineColor
        paintAxisLine.strokeWidth = Utils.convertDpToPixel(xAxis.axisLineWidth)
        paintAxisLine.pathEffect = xAxis.axisLineDashPathEffect

        if (xAxis.position == XAxisPosition.TOP || xAxis.position == XAxisPosition.TOP_INSIDE || xAxis.position == XAxisPosition.BOTH_SIDED) {
            c.drawLine(viewPortHandler.contentLeft, viewPortHandler.contentTop, viewPortHandler.contentRight, viewPortHandler.contentTop, paintAxisLine)
        }

        if (xAxis.position == XAxisPosition.BOTTOM || xAxis.position == XAxisPosition.BOTTOM_INSIDE || xAxis.position == XAxisPosition.BOTH_SIDED) {
            c.drawLine(viewPortHandler.contentLeft, viewPortHandler.contentBottom, viewPortHandler.contentRight, viewPortHandler.contentBottom, paintAxisLine)
        }
    }

    /**
     * Draws all axis labels inside the visible x range at the vertical pixel position [pos]. When the axis avoids
     * first and last clipping, the outer labels are shifted inwards so they stay inside the chart.
     *
     * @param anchor which point of a label sits on its position, as fractions of the label size: (0.5, 1) is the
     * bottom center, (0.5, 0) the top center
     */
    protected open fun drawLabels(c: Canvas, pos: Float, anchor: MPPointF) {
        val labelRotationAngleDegrees = xAxis.labelRotationAngle
        val centeringEnabled = xAxis.isCenterAxisLabelsEnabled

        val positions = FloatArray(xAxis.entryCount * 2)

        for (i in positions.indices step 2) {
            positions[i] = if (centeringEnabled) xAxis.centeredEntries[i / 2] else xAxis.entries[i / 2]
        }

        transformer?.pointValuesToPixel(positions)

        for (i in positions.indices step 2) {
            var x = positions[i]

            if (viewPortHandler.isInBoundsX(x)) {
                val label = xAxis.valueFormatter.getFormattedValue(xAxis.entries[i / 2], xAxis)

                if (xAxis.isAvoidFirstLastClippingEnabled) {
                    if (i / 2 == xAxis.entryCount - 1 && xAxis.entryCount > 1) {
                        val width = Utils.calcTextWidth(paintAxisLabels, label).toFloat()
                        if (width > viewPortHandler.offsetRight * 2 && x + width > viewPortHandler.chartWidth) {
                            x -= width / 2
                        }
                    } else if (i == 0) {
                        val width = Utils.calcTextWidth(paintAxisLabels, label).toFloat()
                        x += width / 2
                    }
                }

                drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees)
            }
        }
    }

    /**
     * Draws one label with [paintAxisLabels] at the pixel position ([x], [y]), rotated by [angleDegrees] around
     * [anchor] (fractions of the label size).
     */
    protected open fun drawLabel(c: Canvas, formattedLabel: String, x: Float, y: Float, anchor: MPPointF, angleDegrees: Float) {
        Utils.drawXAxisValue(c, formattedLabel, x, y, paintAxisLabels, anchor, angleDegrees)
    }

    /**
     * Path reused for drawing grid lines.
     */
    protected val renderGridLinesPath: Path = Path()

    /**
     * Pixel positions of the grid lines as x, y pairs; resized to twice the entry count of the axis.
     */
    protected var renderGridLinesBuffer: FloatArray = FloatArray(2)

    /**
     * Draws one vertical grid line per axis entry, clipped to [gridClippingRect].
     */
    override fun renderGridLines(c: Canvas) {
        if (!xAxis.isDrawGridLinesEnabled || !xAxis.isEnabled) return

        val clipRestoreCount = c.save()
        c.clipRect(gridClippingRect)

        if (renderGridLinesBuffer.size != axis.entryCount * 2) {
            renderGridLinesBuffer = FloatArray(xAxis.entryCount * 2)
        }
        val positions = renderGridLinesBuffer

        for (i in positions.indices step 2) {
            positions[i] = xAxis.entries[i / 2]
            positions[i + 1] = xAxis.entries[i / 2]
        }

        transformer?.pointValuesToPixel(positions)

        setupGridPaint()

        val gridLinePath = renderGridLinesPath
        gridLinePath.reset()

        for (i in positions.indices step 2) {
            drawGridLine(c, positions[i], positions[i + 1], gridLinePath)
        }

        c.restoreToCount(clipRestoreCount)
    }

    /**
     * Backing rectangle of [gridClippingRect].
     */
    protected val gridClippingRectBuffer: RectF = RectF()

    /**
     * The content rectangle widened horizontally by the grid line width, in pixels, so grid lines at the edges are
     * not cut in half.
     */
    public open val gridClippingRect: RectF
        get() {
            gridClippingRectBuffer.set(viewPortHandler.contentRect)
            gridClippingRectBuffer.inset(-Utils.convertDpToPixel(axis.gridLineWidth), 0f)
            return gridClippingRectBuffer
        }

    /**
     * Draws one vertical grid line at the pixel position [x] over the content height, using [gridLinePath] as a
     * scratch path. [y] is not used here.
     */
    protected open fun drawGridLine(c: Canvas, x: Float, y: Float, gridLinePath: Path) {
        gridLinePath.moveTo(x, viewPortHandler.contentBottom)
        gridLinePath.lineTo(x, viewPortHandler.contentTop)

        c.drawPath(gridLinePath, paintGrid)

        gridLinePath.reset()
    }

    /**
     * Buffer for the pixel position of the limit line being drawn.
     */
    protected val renderLimitLinesBuffer: FloatArray = FloatArray(2)

    /**
     * Clip rectangle for the limit line being drawn, the content rectangle widened by the line width.
     */
    protected val limitLineClippingRect: RectF = RectF()

    /**
     * Draws each enabled limit line as a vertical line at the pixel x of its limit value and its label at the top
     * or bottom of the content rectangle.
     */
    override fun renderLimitLines(c: Canvas) {
        val limitLines = xAxis.limitLines
        if (limitLines.isEmpty()) return

        val position = renderLimitLinesBuffer
        position[0] = 0f
        position[1] = 0f

        for (l in limitLines) {
            if (!l.isEnabled) continue

            val clipRestoreCount = c.save()
            limitLineClippingRect.set(viewPortHandler.contentRect)
            limitLineClippingRect.inset(-Utils.convertDpToPixel(l.lineWidth), 0f)
            c.clipRect(limitLineClippingRect)

            position[0] = l.limit
            position[1] = 0f

            transformer?.pointValuesToPixel(position)

            renderLimitLineLine(c, l, position)
            renderLimitLineLabel(c, l, position, 2f + Utils.convertDpToPixel(l.yOffset))

            c.restoreToCount(clipRestoreCount)
        }
    }

    private val limitLineSegmentsBuffer = FloatArray(4)

    private val limitLinePath = Path()

    /**
     * Draws the line of [limitLine] over the content height with its color, width (dp) and dash effect.
     *
     * @param position pixel position of the limit value, x at index 0
     */
    public fun renderLimitLineLine(c: Canvas, limitLine: LimitLine, position: FloatArray) {
        limitLineSegmentsBuffer[0] = position[0]
        limitLineSegmentsBuffer[1] = viewPortHandler.contentTop
        limitLineSegmentsBuffer[2] = position[0]
        limitLineSegmentsBuffer[3] = viewPortHandler.contentBottom

        limitLinePath.reset()
        limitLinePath.moveTo(limitLineSegmentsBuffer[0], limitLineSegmentsBuffer[1])
        limitLinePath.lineTo(limitLineSegmentsBuffer[2], limitLineSegmentsBuffer[3])

        limitLinePaint.style = Paint.Style.STROKE
        limitLinePaint.color = limitLine.lineColor
        limitLinePaint.strokeWidth = Utils.convertDpToPixel(limitLine.lineWidth)
        limitLinePaint.pathEffect = limitLine.dashPathEffect

        c.drawPath(limitLinePath, limitLinePaint)
    }

    /**
     * Draws the label of [limitLine] next to its line, at the corner given by its label position. Does nothing for
     * an empty label.
     *
     * @param position pixel position of the limit value, x at index 0
     * @param yOffset distance in pixels between the label and the top or bottom content edge
     */
    public fun renderLimitLineLabel(c: Canvas, limitLine: LimitLine, position: FloatArray, yOffset: Float) {
        val label = limitLine.label

        if (label.isNotEmpty()) {
            limitLinePaint.style = limitLine.textStyle
            limitLinePaint.pathEffect = null
            limitLinePaint.color = limitLine.textColor
            limitLinePaint.strokeWidth = 0.5f
            limitLinePaint.textSize = Utils.convertDpToPixel(limitLine.textSize)
            limitLinePaint.typeface = limitLine.typeface

            val xOffset = Utils.convertDpToPixel(limitLine.lineWidth) + Utils.convertDpToPixel(limitLine.xOffset)

            when (limitLine.labelPosition) {
                LimitLine.LimitLabelPosition.RIGHT_TOP -> {
                    val labelLineHeight = Utils.calcTextHeight(limitLinePaint, label).toFloat()
                    limitLinePaint.textAlign = Paint.Align.LEFT
                    c.drawText(label, position[0] + xOffset, viewPortHandler.contentTop + yOffset + labelLineHeight, limitLinePaint)
                }
                LimitLine.LimitLabelPosition.RIGHT_BOTTOM -> {
                    limitLinePaint.textAlign = Paint.Align.LEFT
                    c.drawText(label, position[0] + xOffset, viewPortHandler.contentBottom - yOffset, limitLinePaint)
                }
                LimitLine.LimitLabelPosition.LEFT_TOP -> {
                    limitLinePaint.textAlign = Paint.Align.RIGHT
                    val labelLineHeight = Utils.calcTextHeight(limitLinePaint, label).toFloat()
                    c.drawText(label, position[0] - xOffset, viewPortHandler.contentTop + yOffset + labelLineHeight, limitLinePaint)
                }
                LimitLine.LimitLabelPosition.LEFT_BOTTOM -> {
                    limitLinePaint.textAlign = Paint.Align.RIGHT
                    c.drawText(label, position[0] - xOffset, viewPortHandler.contentBottom - yOffset, limitLinePaint)
                }
            }
        }
    }
}
