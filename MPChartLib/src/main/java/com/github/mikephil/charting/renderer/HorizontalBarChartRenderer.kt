package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Paint.Align
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.buffer.HorizontalBarBuffer
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.Fill
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.ceil
import kotlin.math.min

/**
 * Renderer for the HorizontalBarChart.
 *
 * @author Philipp Jahoda
 */
open class HorizontalBarChartRenderer(
    chart: BarDataProvider, animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : BarChartRenderer(chart, animator, viewPortHandler) {
    override fun initBuffers() {
        val barData = chart.barData
        barBuffers = arrayOfNulls<HorizontalBarBuffer>(barData.dataSetCount).toMutableList()

        for (i in barBuffers.indices) {
            val set = barData.getDataSetByIndex(i)
            barBuffers[i] = HorizontalBarBuffer(
                set.entryCount * 4 * (if (set.isStacked) set.stackSize else 1),
                barData.dataSetCount, set.isStacked
            )
        }
    }

    private val mBarShadowRectBuffer = RectF()

    init {
        mValuePaint.textAlign = Align.LEFT
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = chart.getTransformer(dataSet.axisDependency)

        barBorderPaint.color = dataSet.barBorderColor
        barBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)

        val drawBorder = dataSet.barBorderWidth > 0f

        val phaseX = animator.phaseX
        val phaseY = animator.phaseY

        // draw the bar shadow before the values
        if (chart.isDrawBarShadowEnabled) {
            shadowPaint.color = dataSet.barShadowColor

            val barData = chart.barData

            val barWidth = barData.barWidth
            val barWidthHalf = barWidth / 2.0f
            var x: Float

            var i = 0
            val count = min((ceil(((dataSet.entryCount).toFloat() * phaseX).toDouble())).toInt().toDouble(), dataSet.entryCount.toDouble()).toInt()
            while (i < count
            ) {
                val e = dataSet.getEntryForIndex(i)

                x = e.x

                mBarShadowRectBuffer.top = x - barWidthHalf
                mBarShadowRectBuffer.bottom = x + barWidthHalf

                trans!!.rectValueToPixel(mBarShadowRectBuffer)

                if (!viewPortHandler.isInBoundsTop(mBarShadowRectBuffer.bottom)) {
                    i++
                    continue
                }

                if (!viewPortHandler.isInBoundsBottom(mBarShadowRectBuffer.top)) {
                    break
                }

                mBarShadowRectBuffer.left = viewPortHandler.contentLeft()
                mBarShadowRectBuffer.right = viewPortHandler.contentRight()

                c.drawRect(mBarShadowRectBuffer, shadowPaint)
                i++
            }
        }

        // initialize the buffer
        val buffer = barBuffers[index]!!
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(chart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(chart.barData.barWidth)

        buffer.feed(dataSet)

        trans!!.pointValuesToPixel(buffer.buffer)

        val isCustomFill = dataSet.fills != null && dataSet.fills.isNotEmpty()
        val isSingleColor = dataSet.colors.size == 1
        val isInverted = chart.isInverted(dataSet.axisDependency)

        if (isSingleColor) {
            renderPaint.color = dataSet.color
        }

        var j = 0
        var pos = 0
        while (j < buffer.size()) {
            if (!viewPortHandler.isInBoundsTop(buffer.buffer[j + 3])) {
                break
            }

            if (!viewPortHandler.isInBoundsBottom(buffer.buffer[j + 1])) {
                j += 4
                pos++
                continue
            }

            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                renderPaint.color = dataSet.getColor(j / 4)
            }

            if (isCustomFill) {
                dataSet.getFill(pos)
                    .fillRect(
                        c, renderPaint,
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        buffer.buffer[j + 2],
                        buffer.buffer[j + 3],
                        if (isInverted) Fill.Direction.LEFT else Fill.Direction.RIGHT,
                        0f
                    )
            } else {
                c.drawRect(
                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], renderPaint
                )
            }

            if (drawBorder) {
                c.drawRect(
                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], barBorderPaint
                )
            }
            j += 4
            pos++
        }
    }

    override fun drawValues(c: Canvas) {
        // if values are drawn
        if (isDrawingValuesAllowed(chart)) {
            val dataSets = chart.barData.dataSets

            val valueOffsetPlus = Utils.convertDpToPixel(5f)
            var posOffset: Float
            var negOffset: Float
            val drawValueAboveBar = chart.isDrawValueAboveBarEnabled

            for (i in 0..<chart.barData.dataSetCount) {
                val dataSet = dataSets[i]
                if (dataSet.entryCount == 0) {
                    continue
                }
                if (!shouldDrawValues(dataSet)) {
                    continue
                }

                val isInverted = chart.isInverted(dataSet.axisDependency)

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet)
                val halfTextHeight = Utils.calcTextHeight(mValuePaint, "10") / 2f

                val formatter = dataSet.valueFormatter

                // get the buffer
                val buffer = barBuffers[i]!!

                val phaseY = animator.phaseY

                val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
                iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
                iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)

                // if only single values are drawn (sum)
                if (!dataSet.isStacked) {
                    var j = 0
                    while (j < buffer.buffer.size * animator.phaseX) {
                        val y = (buffer.buffer[j + 1] + buffer.buffer[j + 3]) / 2f

                        if (!viewPortHandler.isInBoundsTop(buffer.buffer[j + 1])) {
                            break
                        }

                        if (!viewPortHandler.isInBoundsX(buffer.buffer[j])) {
                            j += 4
                            continue
                        }

                        if (!viewPortHandler.isInBoundsBottom(buffer.buffer[j + 1])) {
                            j += 4
                            continue
                        }

                        val entry = dataSet.getEntryForIndex(j / 4)
                        val `val` = entry.y
                        val formattedValue = formatter.getFormattedValue(`val`, entry, i, viewPortHandler)

                        // calculate the correct offset depending on the draw position of the value
                        val valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue).toFloat()
                        posOffset = (if (drawValueAboveBar) valueOffsetPlus else -(valueTextWidth + valueOffsetPlus))
                        negOffset = ((if (drawValueAboveBar) -(valueTextWidth + valueOffsetPlus) else valueOffsetPlus)
                                - (buffer.buffer[j + 2] - buffer.buffer[j]))

                        if (isInverted) {
                            posOffset = -posOffset - valueTextWidth
                            negOffset = -negOffset - valueTextWidth
                        }

                        if (dataSet.isDrawValuesEnabled) {
                            drawValue(
                                c,
                                formattedValue!!,
                                buffer.buffer[j + 2] + (if (`val` >= 0) posOffset else negOffset),
                                y + halfTextHeight,
                                dataSet.getValueTextColor(j / 2)
                            )
                        }

                        if (entry.icon != null && dataSet.isDrawIconsEnabled) {
                            val icon = entry.icon

                            var px = buffer.buffer[j + 2] + (if (`val` >= 0) posOffset else negOffset)
                            var py = y

                            px += iconsOffset.x
                            py += iconsOffset.y

                            Utils.drawImage(
                                c,
                                icon,
                                px.toInt(),
                                py.toInt(),
                                icon!!.intrinsicWidth,
                                icon.intrinsicHeight
                            )
                        }
                        j += 4
                    }

                    // if each value of a potential stack should be drawn
                } else {
                    val trans = chart.getTransformer(dataSet.axisDependency)

                    var bufferIndex = 0
                    var index = 0

                    while (index < dataSet.entryCount * animator.phaseX) {
                        val entry = dataSet.getEntryForIndex(index)

                        val color = dataSet.getValueTextColor(index)
                        val vals = entry.yVals

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {
                            if (!viewPortHandler.isInBoundsTop(buffer.buffer[bufferIndex + 1])) {
                                break
                            }

                            if (!viewPortHandler.isInBoundsX(buffer.buffer[bufferIndex])) {
                                continue
                            }

                            if (!viewPortHandler.isInBoundsBottom(buffer.buffer[bufferIndex + 1])) {
                                continue
                            }

                            val `val` = entry.y
                            val formattedValue = formatter.getFormattedValue(
                                `val`,
                                entry, i, viewPortHandler
                            )

                            // calculate the correct offset depending on the draw position of the value
                            val valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue).toFloat()
                            posOffset = (if (drawValueAboveBar) valueOffsetPlus else -(valueTextWidth + valueOffsetPlus))
                            negOffset = (if (drawValueAboveBar) -(valueTextWidth + valueOffsetPlus) else valueOffsetPlus)

                            if (isInverted) {
                                posOffset = -posOffset - valueTextWidth
                                negOffset = -negOffset - valueTextWidth
                            }

                            if (dataSet.isDrawValuesEnabled) {
                                drawValue(
                                    c, formattedValue!!,
                                    buffer.buffer[bufferIndex + 2]
                                            + (if (entry.y >= 0) posOffset else negOffset),
                                    buffer.buffer[bufferIndex + 1] + halfTextHeight, color
                                )
                            }

                            if (entry.icon != null && dataSet.isDrawIconsEnabled) {
                                val icon = entry.icon

                                var px = (buffer.buffer[bufferIndex + 2]
                                        + (if (entry.y >= 0) posOffset else negOffset))
                                var py = buffer.buffer[bufferIndex + 1]

                                px += iconsOffset.x
                                py += iconsOffset.y

                                Utils.drawImage(
                                    c,
                                    icon,
                                    px.toInt(),
                                    py.toInt(),
                                    icon!!.intrinsicWidth,
                                    icon.intrinsicHeight
                                )
                            }
                        } else {
                            val transformed = FloatArray(vals.size * 2)

                            var posY = 0f
                            var negY = -entry.negativeSum

                            run {
                                var k = 0
                                var idx = 0
                                while (k < transformed.size) {
                                    val value = vals[idx]
                                    val y: Float

                                    if (value == 0.0f && (posY == 0.0f || negY == 0.0f)) {
                                        // Take care of the situation of a 0.0 value, which overlaps a non-zero bar
                                        y = value
                                    } else if (value >= 0.0f) {
                                        posY += value
                                        y = posY
                                    } else {
                                        y = negY
                                        negY -= value
                                    }

                                    transformed[k] = y * phaseY
                                    k += 2
                                    idx++
                                }
                            }

                            trans!!.pointValuesToPixel(transformed)

                            var k = 0
                            while (k < transformed.size) {
                                val `val` = vals[k / 2]
                                val formattedValue = formatter.getFormattedValue(
                                    `val`,
                                    entry, i, viewPortHandler
                                )

                                // calculate the correct offset depending on the draw position of the value
                                val valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue).toFloat()
                                posOffset = (if (drawValueAboveBar) valueOffsetPlus else -(valueTextWidth + valueOffsetPlus))
                                negOffset = (if (drawValueAboveBar) -(valueTextWidth + valueOffsetPlus) else valueOffsetPlus)

                                if (isInverted) {
                                    posOffset = -posOffset - valueTextWidth
                                    negOffset = -negOffset - valueTextWidth
                                }

                                val drawBelow =
                                    (`val` == 0.0f && negY == 0.0f && posY > 0.0f) ||
                                            `val` < 0.0f

                                val x = (transformed[k]
                                        + (if (drawBelow) negOffset else posOffset))
                                val y = (buffer.buffer[bufferIndex + 1] + buffer.buffer[bufferIndex + 3]) / 2f

                                if (!viewPortHandler.isInBoundsTop(y)) {
                                    break
                                }

                                if (!viewPortHandler.isInBoundsX(x)) {
                                    k += 2
                                    continue
                                }

                                if (!viewPortHandler.isInBoundsBottom(y)) {
                                    k += 2
                                    continue
                                }

                                if (dataSet.isDrawValuesEnabled) {
                                    drawValue(c, formattedValue!!, x, y + halfTextHeight, color)
                                }

                                if (entry.icon != null && dataSet.isDrawIconsEnabled) {
                                    val icon = entry.icon

                                    Utils.drawImage(
                                        c,
                                        icon,
                                        (x + iconsOffset.x).toInt(),
                                        (y + iconsOffset.y).toInt(),
                                        icon!!.intrinsicWidth,
                                        icon.intrinsicHeight
                                    )
                                }
                                k += 2
                            }
                        }

                        bufferIndex = if (vals == null) bufferIndex + 4 else bufferIndex + 4 * vals.size
                        index++
                    }
                }

                MPPointF.recycleInstance(iconsOffset)
            }
        }
    }

    protected fun drawValue(c: Canvas, valueText: String, x: Float, y: Float, color: Int) {
        mValuePaint.color = color
        c.drawText(valueText, x, y, mValuePaint)
    }

    override fun prepareBarHighlight(x: Float, y1: Float, y2: Float, barWidthHalf: Float, trans: Transformer) {
        val top = x - barWidthHalf
        val bottom = x + barWidthHalf
        val left = y1
        val right = y2

        barRect[left, top, right] = bottom

        trans.rectToPixelPhaseHorizontal(barRect, animator.phaseY)
    }

    override fun setHighlightDrawPos(high: Highlight, bar: RectF) {
        high.setDraw(bar.centerY(), bar.right)
    }

    override fun isDrawingValuesAllowed(chart: ChartInterface): Boolean {
        return (chart.data!!.entryCount < chart.maxVisibleCount
                * viewPortHandler.scaleY)
    }
}
