package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.utils.FSize
import com.github.mikephil.charting.utils.MPPointD
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import androidx.core.graphics.withClip
import androidx.core.graphics.withSave

open class XAxisRenderer(
    viewPortHandler: ViewPortHandler,
    @JvmField protected var xAxis: XAxis,
    trans: Transformer?
) :
    AxisRenderer(viewPortHandler, trans, xAxis) {
    protected fun setupGridPaint() {
        paintGrid!!.color = xAxis.gridColor
        paintGrid!!.strokeWidth = xAxis.gridLineWidth
        paintGrid!!.setPathEffect(xAxis.gridDashPathEffect)
    }

    override fun computeAxis(min: Float, max: Float, inverted: Boolean) {
        // calculate the starting and entry point of the y-labels (depending on
        // zoom / contentrect bounds)

        var minLocal = min
        var maxLocal = max
        if (viewPortHandler.contentWidth() > 10 && !viewPortHandler.isFullyZoomedOutX) {
            val p1 = transformer!!.getValuesByTouchPoint(viewPortHandler.contentLeft(), viewPortHandler.contentTop())
            val p2 = transformer!!.getValuesByTouchPoint(viewPortHandler.contentRight(), viewPortHandler.contentTop())

            if (inverted) {
                minLocal = p2.x.toFloat()
                maxLocal = p1.x.toFloat()
            } else {
                minLocal = p1.x.toFloat()
                maxLocal = p2.x.toFloat()
            }

            MPPointD.recycleInstance(p1)
            MPPointD.recycleInstance(p2)
        }

        computeAxisValues(minLocal, maxLocal)
    }

    override fun computeAxisValues(min: Float, max: Float) {
        super.computeAxisValues(min, max)

        computeSize()
    }

    protected open fun computeSize() {
        val longest = xAxis.longestLabel

        paintAxisLabels!!.setTypeface(xAxis.typeface)
        paintAxisLabels!!.textSize = xAxis.textSize

        val labelSize = Utils.calcTextSize(paintAxisLabels, longest)

        val labelWidth = labelSize.width
        val labelHeight = Utils.calcTextHeight(paintAxisLabels, "Q").toFloat()

        val labelRotatedSize = Utils.getSizeOfRotatedRectangleByDegrees(
            labelWidth,
            labelHeight,
            xAxis.labelRotationAngle
        )


        xAxis.mLabelWidth = Math.round(labelRotatedSize.width)
        xAxis.mLabelHeight = Math.round(labelRotatedSize.height)

        FSize.recycleInstance(labelRotatedSize)
        FSize.recycleInstance(labelSize)
    }

    override fun renderAxisLabels(c: Canvas) {
        if (!xAxis.isEnabled || !xAxis.isDrawLabelsEnabled) return

        val yoffset = xAxis.yOffset

        paintAxisLabels!!.color = xAxis.textColor

        val pointF = MPPointF.getInstance(0f, 0f)
        if (xAxis.position == XAxisPosition.TOP) {
            pointF.x = 0.5f
            pointF.y = 1.0f
            drawLabels(c, viewPortHandler.contentTop() - yoffset, pointF)
        } else if (xAxis.position == XAxisPosition.TOP_INSIDE) {
            pointF.x = 0.5f
            pointF.y = 1.0f
            drawLabels(c, viewPortHandler.contentTop() + yoffset + xAxis.mLabelHeight, pointF)
        } else if (xAxis.position == XAxisPosition.BOTTOM) {
            pointF.x = 0.5f
            pointF.y = 0.0f
            drawLabels(c, viewPortHandler.contentBottom() + yoffset, pointF)
        } else if (xAxis.position == XAxisPosition.BOTTOM_INSIDE) {
            pointF.x = 0.5f
            pointF.y = 0.0f
            drawLabels(c, viewPortHandler.contentBottom() - yoffset - xAxis.mLabelHeight, pointF)
        } else { // BOTH SIDED
            pointF.x = 0.5f
            pointF.y = 1.0f
            drawLabels(c, viewPortHandler.contentTop() - yoffset, pointF)
            pointF.x = 0.5f
            pointF.y = 0.0f
            drawLabels(c, viewPortHandler.contentBottom() + yoffset, pointF)
        }
        MPPointF.recycleInstance(pointF)
    }

    override fun renderAxisLine(c: Canvas) {
        if (!xAxis.isDrawAxisLineEnabled || !xAxis.isEnabled) return

        paintAxisLine!!.color = xAxis.axisLineColor
        paintAxisLine!!.strokeWidth = xAxis.axisLineWidth
        paintAxisLine!!.setPathEffect(xAxis.axisLineDashPathEffect)

        if (xAxis.position == XAxisPosition.TOP || xAxis.position == XAxisPosition.TOP_INSIDE || xAxis.position == XAxisPosition.BOTH_SIDED) {
            c.drawLine(
                viewPortHandler.contentLeft(),
                viewPortHandler.contentTop(), viewPortHandler.contentRight(),
                viewPortHandler.contentTop(), paintAxisLine!!
            )
        }

        if (xAxis.position == XAxisPosition.BOTTOM || xAxis.position == XAxisPosition.BOTTOM_INSIDE || xAxis.position == XAxisPosition.BOTH_SIDED) {
            c.drawLine(
                viewPortHandler.contentLeft(),
                viewPortHandler.contentBottom(), viewPortHandler.contentRight(),
                viewPortHandler.contentBottom(), paintAxisLine!!
            )
        }
    }

    /**
     * draws the x-labels on the specified y-position
     *
     * @param pos
     */
    protected open fun drawLabels(c: Canvas?, pos: Float, anchor: MPPointF?) {
        val labelRotationAngleDegrees = xAxis.labelRotationAngle
        val centeringEnabled = xAxis.isCenterAxisLabelsEnabled

        val positions: FloatArray

        if (xAxis.isShowSpecificPositions) {
            positions = FloatArray(xAxis.specificPositions.size * 2)
            var i = 0
            while (i < positions.size) {
                positions[i] = xAxis.specificPositions[i / 2]
                i += 2
            }
        } else {
            positions = FloatArray(xAxis.mEntryCount * 2)
            var i = 0
            while (i < positions.size) {
                // only fill x values
                if (centeringEnabled) {
                    positions[i] = xAxis.mCenteredEntries[i / 2]
                } else {
                    positions[i] = xAxis.mEntries[i / 2]
                }
                i += 2
            }
        }

        transformer!!.pointValuesToPixel(positions)

        var i = 0
        while (i < positions.size) {
            var x = positions[i]

            if (viewPortHandler.isInBoundsX(x)) {
                val label = if (xAxis.isShowSpecificPositions)
                    xAxis.valueFormatter.getFormattedValue(xAxis.specificPositions[i / 2], xAxis)
                else
                    xAxis.valueFormatter.getFormattedValue(xAxis.mEntries[i / 2], xAxis)

                if (xAxis.isAvoidFirstLastClippingEnabled) {
                    // avoid clipping of the last

                    if (i / 2 == xAxis.mEntryCount - 1 && xAxis.mEntryCount > 1) {
                        val width = Utils.calcTextWidth(paintAxisLabels, label).toFloat()

                        if (width > viewPortHandler.offsetRight() * 2
                            && x + width > viewPortHandler.chartWidth
                        ) x -= width / 2

                        // avoid clipping of the first
                    } else if (i == 0) {
                        val width = Utils.calcTextWidth(paintAxisLabels, label).toFloat()
                        x += width / 2
                    }
                }

                drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees)
            }
            i += 2
        }
    }

    protected fun drawLabel(c: Canvas?, formattedLabel: String?, x: Float, y: Float, anchor: MPPointF?, angleDegrees: Float) {
        Utils.drawXAxisValue(c, formattedLabel, x, y, paintAxisLabels, anchor, angleDegrees)
    }

    protected var mRenderGridLinesPath: Path = Path()
    protected var mRenderGridLinesBuffer: FloatArray = FloatArray(2)
    override fun renderGridLines(c: Canvas) {
        if (!xAxis.isDrawGridLinesEnabled || !xAxis.isEnabled) return

        c.withClip(gridClippingRect!!) {
            if (axis.isShowSpecificPositions) {
                if (mRenderGridLinesBuffer.size != axis.specificPositions.size * 2) {
                    mRenderGridLinesBuffer = FloatArray(xAxis.specificPositions.size * 2)
                }
            } else {
                if (mRenderGridLinesBuffer.size != axis.mEntryCount * 2) {
                    mRenderGridLinesBuffer = FloatArray(xAxis.mEntryCount * 2)
                }
            }
            val positions = mRenderGridLinesBuffer

            run {
                var i = 0
                while (i < positions.size) {
                    if (axis.isShowSpecificPositions) {
                        positions[i] = xAxis.specificPositions[i / 2]
                        positions[i + 1] = xAxis.specificPositions[i / 2]
                    } else {
                        positions[i] = xAxis.mEntries[i / 2]
                        positions[i + 1] = xAxis.mEntries[i / 2]
                    }
                    i += 2
                }
            }

            transformer!!.pointValuesToPixel(positions)

            setupGridPaint()

            val gridLinePath = mRenderGridLinesPath
            gridLinePath.reset()

            var i = 0
            while (i < positions.size) {
                drawGridLine(c, positions[i], positions[i + 1], gridLinePath)
                i += 2
            }

        }
    }

    @JvmField
    protected var mGridClippingRect: RectF = RectF()

    open val gridClippingRect: RectF?
        get() {
            mGridClippingRect.set(viewPortHandler.contentRect)
            mGridClippingRect.inset(-axis.gridLineWidth, 0f)
            return mGridClippingRect
        }

    /**
     * Draws the grid line at the specified position using the provided path.
     *
     * @param c
     * @param x
     * @param y
     * @param gridLinePath
     */
    protected open fun drawGridLine(c: Canvas, x: Float, y: Float, gridLinePath: Path) {
        gridLinePath.moveTo(x, viewPortHandler.contentBottom())
        gridLinePath.lineTo(x, viewPortHandler.contentTop())

        // draw a path because lines don't support dashing on lower android versions
        c.drawPath(gridLinePath, paintGrid!!)

        gridLinePath.reset()
    }

    @JvmField
    protected var mRenderLimitLinesBuffer: FloatArray = FloatArray(2)

    @JvmField
    protected var mLimitLineClippingRect: RectF = RectF()

    /**
     * Draws the LimitLines associated with this axis to the screen.
     *
     * @param c
     */
    override fun renderLimitLines(c: Canvas) {
        val limitLines = xAxis.limitLines

        if (limitLines == null || limitLines.size <= 0) return

        val position = mRenderLimitLinesBuffer
        position[0] = 0f
        position[1] = 0f

        for (i in limitLines.indices) {
            val l = limitLines[i]

            if (!l.isEnabled) continue

            c.withSave {
                mLimitLineClippingRect.set(viewPortHandler.contentRect)
                mLimitLineClippingRect.inset(-l.lineWidth, 0f)
                c.clipRect(mLimitLineClippingRect)

                position[0] = l.limit
                position[1] = 0f

                transformer!!.pointValuesToPixel(position)

                renderLimitLineLine(c, l, position)
                renderLimitLineLabel(c, l, position, 2f + l.yOffset)

            }
        }
    }

    var mLimitLineSegmentsBuffer: FloatArray = FloatArray(4)
    private val mLimitLinePath = Path()

    init {
        paintAxisLabels!!.color = Color.BLACK
        paintAxisLabels!!.textAlign = Align.CENTER
        paintAxisLabels!!.textSize = Utils.convertDpToPixel(10f)
    }

    fun renderLimitLineLine(c: Canvas, limitLine: LimitLine, position: FloatArray) {
        mLimitLineSegmentsBuffer[0] = position[0]
        mLimitLineSegmentsBuffer[1] = viewPortHandler.contentTop()
        mLimitLineSegmentsBuffer[2] = position[0]
        mLimitLineSegmentsBuffer[3] = viewPortHandler.contentBottom()

        mLimitLinePath.reset()
        mLimitLinePath.moveTo(mLimitLineSegmentsBuffer[0], mLimitLineSegmentsBuffer[1])
        mLimitLinePath.lineTo(mLimitLineSegmentsBuffer[2], mLimitLineSegmentsBuffer[3])

        limitLinePaint.style = Paint.Style.STROKE
        limitLinePaint.color = limitLine.lineColor
        limitLinePaint.strokeWidth = limitLine.lineWidth
        limitLinePaint.setPathEffect(limitLine.dashPathEffect)

        c.drawPath(mLimitLinePath, limitLinePaint)
    }

    fun renderLimitLineLabel(c: Canvas, limitLine: LimitLine, position: FloatArray, yOffset: Float) {
        val label = limitLine.label

        // if drawing the limit-value label is enabled
        if (label != null && label != "") {
            limitLinePaint.style = limitLine.textStyle
            limitLinePaint.setPathEffect(null)
            limitLinePaint.color = limitLine.textColor
            limitLinePaint.strokeWidth = 0.5f
            limitLinePaint.textSize = limitLine.textSize


            val xOffset = limitLine.lineWidth + limitLine.xOffset

            val labelPosition = limitLine.labelPosition

            if (labelPosition == LimitLabelPosition.RIGHT_TOP) {
                val labelLineHeight = Utils.calcTextHeight(limitLinePaint, label).toFloat()
                limitLinePaint.textAlign = Align.LEFT
                c.drawText(
                    label, position[0] + xOffset, viewPortHandler.contentTop() + yOffset + labelLineHeight,
                    limitLinePaint
                )
            } else if (labelPosition == LimitLabelPosition.RIGHT_BOTTOM) {
                limitLinePaint.textAlign = Align.LEFT
                c.drawText(label, position[0] + xOffset, viewPortHandler.contentBottom() - yOffset, limitLinePaint)
            } else if (labelPosition == LimitLabelPosition.LEFT_TOP) {
                limitLinePaint.textAlign = Align.RIGHT
                val labelLineHeight = Utils.calcTextHeight(limitLinePaint, label).toFloat()
                c.drawText(
                    label, position[0] - xOffset, viewPortHandler.contentTop() + yOffset + labelLineHeight,
                    limitLinePaint
                )
            } else {
                limitLinePaint.textAlign = Align.RIGHT
                c.drawText(label, position[0] - xOffset, viewPortHandler.contentBottom() - yOffset, limitLinePaint)
            }
        }
    }
}
