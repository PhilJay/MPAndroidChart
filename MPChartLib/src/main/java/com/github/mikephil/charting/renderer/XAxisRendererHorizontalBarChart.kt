package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.utils.FSize
import com.github.mikephil.charting.utils.MPPointD
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import androidx.core.graphics.withSave

@Suppress("MemberVisibilityCanBePrivate")
open class XAxisRendererHorizontalBarChart(
    viewPortHandler: ViewPortHandler, xAxis: XAxis,
    trans: Transformer?
) : XAxisRenderer(viewPortHandler, xAxis, trans) {

    protected var renderLimitLinesPathBuffer: Path = Path()

    override fun computeAxis(min: Float, max: Float, inverted: Boolean) {
        // calculate the starting and entry point of the y-labels (depending on
        // zoom / content rect bounds)

        var minLocal = min
        var maxLocal = max
        if (viewPortHandler.contentWidth() > 10 && !viewPortHandler.isFullyZoomedOutY) {
            val p1 = transformer!!.getValuesByTouchPoint(viewPortHandler.contentLeft(), viewPortHandler.contentBottom())
            val p2 = transformer!!.getValuesByTouchPoint(viewPortHandler.contentLeft(), viewPortHandler.contentTop())

            if (inverted) {
                minLocal = p2.y.toFloat()
                maxLocal = p1.y.toFloat()
            } else {
                minLocal = p1.y.toFloat()
                maxLocal = p2.y.toFloat()
            }

            MPPointD.recycleInstance(p1)
            MPPointD.recycleInstance(p2)
        }

        computeAxisValues(minLocal, maxLocal)
    }

    override fun computeSize() {
        paintAxisLabels!!.setTypeface(xAxis.typeface)
        paintAxisLabels!!.textSize = xAxis.textSize

        val longest = xAxis.longestLabel

        val labelSize = Utils.calcTextSize(paintAxisLabels, longest)

        val labelWidth = (labelSize.width + xAxis.xOffset * 3.5f).toInt().toFloat()
        val labelHeight = labelSize.height

        val labelRotatedSize = Utils.getSizeOfRotatedRectangleByDegrees(
            labelWidth,
            labelHeight,
            xAxis.labelRotationAngle
        )

        xAxis.mLabelWidth = Math.round(labelRotatedSize.width)
        xAxis.mLabelHeight = Math.round(labelRotatedSize.height)

        FSize.recycleInstance(labelRotatedSize)
    }

    override fun renderAxisLabels(c: Canvas) {
        if (!xAxis.isEnabled || !xAxis.isDrawLabelsEnabled) return

        val xOffset = xAxis.xOffset

        paintAxisLabels!!.setTypeface(xAxis.typeface)
        paintAxisLabels!!.textSize = xAxis.textSize
        paintAxisLabels!!.color = xAxis.textColor

        val pointF = MPPointF.getInstance(0f, 0f)

        when (xAxis.position) {
            XAxisPosition.TOP -> {
                pointF.x = 0.0f
                pointF.y = 0.5f
                drawLabels(c, viewPortHandler.contentRight() + xOffset, pointF)
            }
            XAxisPosition.TOP_INSIDE -> {
                pointF.x = 1.0f
                pointF.y = 0.5f
                drawLabels(c, viewPortHandler.contentRight() - xOffset, pointF)
            }
            XAxisPosition.BOTTOM -> {
                pointF.x = 1.0f
                pointF.y = 0.5f
                drawLabels(c, viewPortHandler.contentLeft() - xOffset, pointF)
            }
            XAxisPosition.BOTTOM_INSIDE -> {
                pointF.x = 1.0f
                pointF.y = 0.5f
                drawLabels(c, viewPortHandler.contentLeft() + xOffset, pointF)
            }
            else -> { // BOTH SIDED
                pointF.x = 0.0f
                pointF.y = 0.5f
                drawLabels(c, viewPortHandler.contentRight() + xOffset, pointF)
                pointF.x = 1.0f
                pointF.y = 0.5f
                drawLabels(c, viewPortHandler.contentLeft() - xOffset, pointF)
            }
        }

        MPPointF.recycleInstance(pointF)
    }

    override fun drawLabels(c: Canvas?, pos: Float, anchor: MPPointF?) {
        val labelRotationAngleDegrees = xAxis.labelRotationAngle
        val centeringEnabled = xAxis.isCenterAxisLabelsEnabled

        val positions = FloatArray(xAxis.mEntryCount * 2)

        run {
            var i = 0
            while (i < positions.size) {
                // only fill x values
                if (centeringEnabled) {
                    positions[i + 1] = xAxis.mCenteredEntries[i / 2]
                } else {
                    positions[i + 1] = xAxis.mEntries[i / 2]
                }
                i += 2
            }
        }

        transformer!!.pointValuesToPixel(positions)

        var i = 0
        while (i < positions.size) {
            val y = positions[i + 1]

            if (viewPortHandler.isInBoundsY(y)) {
                val label = xAxis.valueFormatter.getFormattedValue(xAxis.mEntries[i / 2], xAxis)
                drawLabel(c, label, pos, y, anchor, labelRotationAngleDegrees)
            }
            i += 2
        }
    }

    override val gridClippingRect: RectF
        get() {
            mGridClippingRect.set(viewPortHandler.contentRect)
            mGridClippingRect.inset(0f, -axis.gridLineWidth)
            return mGridClippingRect
        }

    override fun drawGridLine(c: Canvas, x: Float, y: Float, gridLinePath: Path) {
        gridLinePath.moveTo(viewPortHandler.contentRight(), y)
        gridLinePath.lineTo(viewPortHandler.contentLeft(), y)

        // draw a path because lines don't support dashing on lower android versions
        c.drawPath(gridLinePath, paintGrid!!)

        gridLinePath.reset()
    }

    override fun renderAxisLine(c: Canvas) {
        if (!xAxis.isDrawAxisLineEnabled || !xAxis.isEnabled) return

        paintAxisLine!!.color = xAxis.axisLineColor
        paintAxisLine!!.strokeWidth = xAxis.axisLineWidth

        if (xAxis.position == XAxisPosition.TOP || xAxis.position == XAxisPosition.TOP_INSIDE || xAxis.position == XAxisPosition.BOTH_SIDED) {
            c.drawLine(
                viewPortHandler.contentRight(),
                viewPortHandler.contentTop(), viewPortHandler.contentRight(),
                viewPortHandler.contentBottom(), paintAxisLine!!
            )
        }

        if (xAxis.position == XAxisPosition.BOTTOM || xAxis.position == XAxisPosition.BOTTOM_INSIDE || xAxis.position == XAxisPosition.BOTH_SIDED) {
            c.drawLine(
                viewPortHandler.contentLeft(),
                viewPortHandler.contentTop(), viewPortHandler.contentLeft(),
                viewPortHandler.contentBottom(), paintAxisLine!!
            )
        }
    }

    /**
     * Draws the LimitLines associated with this axis to the screen.
     * This is the standard YAxis renderer using the XAxis limit lines.
     *
     * @param c
     */
    override fun renderLimitLines(c: Canvas) {
        val limitLines = xAxis.limitLines

        if (limitLines == null || limitLines.size <= 0) return

        val pts = mRenderLimitLinesBuffer
        pts[0] = 0f
        pts[1] = 0f

        val limitLinePath = renderLimitLinesPathBuffer
        limitLinePath.reset()

        for (i in limitLines.indices) {
            val l = limitLines[i]

            if (!l.isEnabled) continue

            c.withSave {
                mLimitLineClippingRect.set(viewPortHandler.contentRect)
                mLimitLineClippingRect.inset(0f, -l.lineWidth)
                c.clipRect(mLimitLineClippingRect)

                limitLinePaint!!.style = Paint.Style.STROKE
                limitLinePaint!!.color = l.lineColor
                limitLinePaint!!.strokeWidth = l.lineWidth
                limitLinePaint!!.setPathEffect(l.dashPathEffect)

                pts[1] = l.limit

                transformer!!.pointValuesToPixel(pts)

                limitLinePath.moveTo(viewPortHandler.contentLeft(), pts[1])
                limitLinePath.lineTo(viewPortHandler.contentRight(), pts[1])

                c.drawPath(limitLinePath, limitLinePaint!!)
                limitLinePath.reset()

                // c.drawLines(pts, mLimitLinePaint);
                val label = l.label

                // if drawing the limit-value label is enabled
                if (label != null && label != "") {
                    limitLinePaint!!.style = l.textStyle
                    limitLinePaint!!.setPathEffect(null)
                    limitLinePaint!!.color = l.textColor
                    limitLinePaint!!.strokeWidth = 0.5f
                    limitLinePaint!!.textSize = l.textSize

                    val labelLineHeight = Utils.calcTextHeight(limitLinePaint, label).toFloat()
                    val xOffset = Utils.convertDpToPixel(4f) + l.xOffset
                    val yOffset = l.lineWidth + labelLineHeight + l.yOffset

                    val position = l.labelPosition

                    when (position) {
                        LimitLabelPosition.RIGHT_TOP -> {
                            limitLinePaint!!.textAlign = Align.RIGHT
                            c.drawText(
                                label,
                                viewPortHandler.contentRight() - xOffset,
                                pts[1] - yOffset + labelLineHeight, limitLinePaint!!
                            )
                        }
                        LimitLabelPosition.RIGHT_BOTTOM -> {
                            limitLinePaint!!.textAlign = Align.RIGHT
                            c.drawText(
                                label,
                                viewPortHandler.contentRight() - xOffset,
                                pts[1] + yOffset, limitLinePaint!!
                            )
                        }
                        LimitLabelPosition.LEFT_TOP -> {
                            limitLinePaint!!.textAlign = Align.LEFT
                            c.drawText(
                                label,
                                viewPortHandler.contentLeft() + xOffset,
                                pts[1] - yOffset + labelLineHeight, limitLinePaint!!
                            )
                        }
                        else -> {
                            limitLinePaint!!.textAlign = Align.LEFT
                            c.drawText(
                                label,
                                viewPortHandler.offsetLeft() + xOffset,
                                pts[1] + yOffset, limitLinePaint!!
                            )
                        }
                    }
                }

            }
        }
    }
}
