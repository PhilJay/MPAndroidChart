package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.buffer.BarBuffer
import com.github.mikephil.charting.buffer.HorizontalBarBuffer
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.Fill
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.max
import kotlin.math.min

/**
 * Draws the bars of a horizontal bar chart, where the x values run along the vertical axis and the bar values
 * along the horizontal axis. Uses a [HorizontalBarBuffer] per data set and places value labels to the right of
 * the bars (to the left for negative values).
 */
public open class HorizontalBarChartRenderer(chart: BarDataProvider, animator: ChartAnimator, viewPortHandler: ViewPortHandler) : BarChartRenderer(chart, animator, viewPortHandler) {

    init {
        valuePaint.textAlign = Paint.Align.LEFT
    }

    override val xAxisPixels: Float
        get() = viewPortHandler.contentHeight

    /**
     * Creates one [HorizontalBarBuffer] per data set.
     */
    override fun initBuffers() {
        val barData = chart.barData ?: return
        if (buffersFitData(barData)) return

        barBuffers = Array<BarBuffer>(barData.dataSetCount) { i ->
            val set = barData.dataSets[i]
            HorizontalBarBuffer(set.entryCount * 4 * (if (set.isStacked) set.stackSize else 1), barData.dataSetCount, set.isStacked)
        }
    }

    /**
     * Same as the base class, but skips bars by their vertical position and fills custom fills left to right.
     */
    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet<*>, index: Int) {
        if (dataSet.entryCount < 1) return

        val trans = chart.getTransformer(dataSet.axisDependency)

        barBorderPaint.color = dataSet.barBorderColor
        barBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)

        val drawBorder = dataSet.barBorderWidth > 0f

        val phaseX = animator.phaseX
        val phaseY = animator.phaseY

        xBounds.set(chart, dataSet)
        reduceVisible(chart, dataSet)

        val buffer = barBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.dataSetIndex = index
        buffer.isInverted = chart.isInverted(dataSet.axisDependency)
        buffer.barWidth = chart.barData?.barWidth ?: return
        buffer.columnWidth = reducedColumnWidth

        if (chart.isDrawBarShadowEnabled) {
            shadowPaint.color = dataSet.barShadowColor

            for (k in 0 until reducer.count) {
                val e = dataSet.getEntryForIndex(reducer.indices[k])

                val x = e.x
                val barWidthHalf = buffer.widthOfBar(reducer.columnCounts[k]) / 2f

                barShadowRectBuffer.top = x - barWidthHalf
                barShadowRectBuffer.bottom = x + barWidthHalf

                trans.rectValueToPixel(barShadowRectBuffer)

                if (!viewPortHandler.isInBoundsTop(barShadowRectBuffer.bottom)) continue

                if (!viewPortHandler.isInBoundsBottom(barShadowRectBuffer.top)) break

                barShadowRectBuffer.left = viewPortHandler.contentLeft
                barShadowRectBuffer.right = viewPortHandler.contentRight

                drawBarShape(c, dataSet, barShadowRectBuffer.left, barShadowRectBuffer.top, barShadowRectBuffer.right, barShadowRectBuffer.bottom, BarCorners.ALL) {
                    c.drawRect(barShadowRectBuffer, shadowPaint)
                }
            }
        }

        buffer.feed(dataSet, reducer.indices, reducer.columnCounts, reducer.count)

        trans.pointValuesToPixel(buffer.buffer, buffer.filledSize)

        val fills = dataSet.fills
        val isCustomFill = fills != null && fills.isNotEmpty()
        val isSingleColor = dataSet.colors.size == 1
        val isInverted = chart.isInverted(dataSet.axisDependency)

        if (isSingleColor) {
            renderPaint.color = dataSet.color
        }

        val barsPerEntry = buffer.barsPerEntry
        var sections = 1

        for (j in 0 until buffer.filledSize step 4) {
            val bar = j / 4
            val pos = buffer.positionOfBar(bar)

            if (dataSet.isStacked && bar % barsPerEntry == 0) {
                sections = dataSet.getEntryForIndex(buffer.entryOfBar(bar)).stackValues?.size ?: 1
            }

            if (!viewPortHandler.isInBoundsTop(buffer.buffer[j + 3])) break

            if (!viewPortHandler.isInBoundsBottom(buffer.buffer[j + 1])) continue

            if (!isSingleColor) {
                renderPaint.color = dataSet.getColor(pos)
            }

            val corners = barCorners(dataSet, pos, sections, BarCorners.RIGHT)
            drawBarShape(c, dataSet, buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3], corners) {
                if (isCustomFill) {
                    dataSet.getFill(pos).fillRect(
                        c, renderPaint,
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        buffer.buffer[j + 2],
                        buffer.buffer[j + 3],
                        if (isInverted) Fill.Direction.LEFT else Fill.Direction.RIGHT
                    )
                } else {
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3], renderPaint)
                }

                if (drawBorder) {
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3], barBorderPaint)
                }
            }
        }
    }

    override fun drawValues(c: Canvas) {
        val barData = chart.barData ?: return
        val dataSets = barData.dataSets

        val valueOffsetPlus = Utils.convertDpToPixel(5f)
        var posOffset: Float
        var negOffset: Float
        val drawValueAboveBar = chart.isDrawValueAboveBarEnabled

        for (i in 0 until barData.dataSetCount) {
            val dataSet = dataSets[i]

            if (!shouldDrawValues(chart, dataSet)) continue

            val isInverted = chart.isInverted(dataSet.axisDependency)

            applyValueTextStyle(dataSet)
            val halfTextHeight = Utils.calcTextHeight(valuePaint, "10") / 2f

            val formatter = dataSet.valueFormatter

            val buffer = barBuffers[i]

            val phaseY = animator.phaseY

            val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)

            if (!dataSet.isStacked) {
                var j = 0
                while (j < buffer.filledSize) {
                    val y = (buffer.buffer[j + 1] + buffer.buffer[j + 3]) / 2f

                    if (!viewPortHandler.isInBoundsTop(buffer.buffer[j + 1])) break

                    // The label sits at the tip of the bar, so the whole bar counts as visible, not only its base.
                    val barStart = min(buffer.buffer[j], buffer.buffer[j + 2])
                    val barEnd = max(buffer.buffer[j], buffer.buffer[j + 2])

                    if (!viewPortHandler.isInBoundsLeft(barEnd) ||
                        !viewPortHandler.isInBoundsRight(barStart) ||
                        !viewPortHandler.isInBoundsBottom(buffer.buffer[j + 1])
                    ) {
                        j += 4
                        continue
                    }

                    val entryIndex = buffer.entryOfBar(j / 4)
                    val entry = dataSet.getEntryForIndex(entryIndex)
                    val value = entry.y
                    val formattedValue = formatter.getFormattedValue(value, entry, i, viewPortHandler)

                    val valueTextWidth = Utils.calcTextWidth(valuePaint, formattedValue).toFloat()
                    posOffset = if (drawValueAboveBar) valueOffsetPlus else -(valueTextWidth + valueOffsetPlus)
                    negOffset = (if (drawValueAboveBar) -(valueTextWidth + valueOffsetPlus) else valueOffsetPlus) - (buffer.buffer[j + 2] - buffer.buffer[j])

                    if (isInverted) {
                        posOffset = -posOffset - valueTextWidth
                        negOffset = -negOffset - valueTextWidth
                    }

                    if (dataSet.isDrawValuesEnabled) {
                        drawValue(
                            c, formattedValue,
                            buffer.buffer[j + 2] + (if (value >= 0) posOffset else negOffset),
                            y + halfTextHeight,
                            dataSet.getValueTextColor(entryIndex)
                        )
                    }

                    val icon = entry.icon
                    if (icon != null && dataSet.isDrawIconsEnabled) {
                        var px = buffer.buffer[j + 2] + (if (value >= 0) posOffset else negOffset)
                        var py = y

                        px += iconsOffset.x
                        py += iconsOffset.y

                        Utils.drawImage(c, icon, px.toInt(), py.toInt(), icon.intrinsicWidth, icon.intrinsicHeight)
                    }

                    j += 4
                }
            } else {
                val trans = chart.getTransformer(dataSet.axisDependency)

                val entryStride = 4 * buffer.barsPerEntry

                var bufferIndex = 0

                while (bufferIndex < buffer.filledSize) {
                    val index = buffer.entryOfBar(bufferIndex / 4)
                    val entry = dataSet.getEntryForIndex(index)

                    val color = dataSet.getValueTextColor(index)
                    val vals = entry.stackValues

                    val entryStart = bufferIndex
                    bufferIndex += entryStride

                    if (vals == null) {
                        if (!viewPortHandler.isInBoundsTop(buffer.buffer[entryStart + 1])) break

                        val barStart = min(buffer.buffer[entryStart], buffer.buffer[entryStart + 2])
                        val barEnd = max(buffer.buffer[entryStart], buffer.buffer[entryStart + 2])

                        if (!viewPortHandler.isInBoundsLeft(barEnd) ||
                            !viewPortHandler.isInBoundsRight(barStart) ||
                            !viewPortHandler.isInBoundsBottom(buffer.buffer[entryStart + 1])
                        ) continue

                        val value = entry.y
                        val formattedValue = formatter.getFormattedValue(value, entry, i, viewPortHandler)

                        val valueTextWidth = Utils.calcTextWidth(valuePaint, formattedValue).toFloat()
                        posOffset = if (drawValueAboveBar) valueOffsetPlus else -(valueTextWidth + valueOffsetPlus)
                        negOffset = if (drawValueAboveBar) -(valueTextWidth + valueOffsetPlus) else valueOffsetPlus

                        if (isInverted) {
                            posOffset = -posOffset - valueTextWidth
                            negOffset = -negOffset - valueTextWidth
                        }

                        if (dataSet.isDrawValuesEnabled) {
                            drawValue(
                                c, formattedValue,
                                buffer.buffer[entryStart + 2] + (if (entry.y >= 0) posOffset else negOffset),
                                buffer.buffer[entryStart + 1] + halfTextHeight,
                                color
                            )
                        }

                        val icon = entry.icon
                        if (icon != null && dataSet.isDrawIconsEnabled) {
                            var px = buffer.buffer[entryStart + 2] + (if (entry.y >= 0) posOffset else negOffset)
                            var py = buffer.buffer[entryStart + 1]

                            px += iconsOffset.x
                            py += iconsOffset.y

                            Utils.drawImage(c, icon, px.toInt(), py.toInt(), icon.intrinsicWidth, icon.intrinsicHeight)
                        }
                    } else {
                        val transformed = FloatArray(vals.size * 2)

                        var posY = 0f
                        var negY = -entry.negativeSum

                        var k = 0
                        var idx = 0
                        while (k < transformed.size) {
                            val value = vals[idx]
                            val y: Float

                            if (value == 0.0f && (posY == 0.0f || negY == 0.0f)) {
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

                        trans.pointValuesToPixel(transformed)

                        for (k in transformed.indices step 2) {
                            val value = vals[k / 2]
                            val formattedValue = formatter.getStackedFormattedValue(value, k / 2, entry, i, viewPortHandler)

                            val valueTextWidth = Utils.calcTextWidth(valuePaint, formattedValue).toFloat()
                            posOffset = if (drawValueAboveBar) valueOffsetPlus else -(valueTextWidth + valueOffsetPlus)
                            negOffset = if (drawValueAboveBar) -(valueTextWidth + valueOffsetPlus) else valueOffsetPlus

                            if (isInverted) {
                                posOffset = -posOffset - valueTextWidth
                                negOffset = -negOffset - valueTextWidth
                            }

                            val drawBelow = (value == 0.0f && negY == 0.0f && posY > 0.0f) || value < 0.0f

                            val x = transformed[k] + (if (drawBelow) negOffset else posOffset)
                            val y = (buffer.buffer[entryStart + 1] + buffer.buffer[entryStart + 3]) / 2f

                            if (!viewPortHandler.isInBoundsTop(y)) break

                            if (!viewPortHandler.isInBoundsX(x) || !viewPortHandler.isInBoundsBottom(y)) continue

                            if (dataSet.isDrawValuesEnabled) {
                                drawValue(c, formattedValue, x, y + halfTextHeight, color)
                            }

                            val icon = entry.icon
                            if (icon != null && dataSet.isDrawIconsEnabled) {
                                Utils.drawImage(
                                    c, icon,
                                    (x + iconsOffset.x).toInt(),
                                    (y + iconsOffset.y).toInt(),
                                    icon.intrinsicWidth,
                                    icon.intrinsicHeight
                                )
                            }
                        }
                    }
                }
            }

            MPPointF.recycleInstance(iconsOffset)
        }
    }

    /**
     * Draws an already formatted value text with [valuePaint] in [color], left aligned at the pixel position
     * ([x], [y]), where [y] is the text baseline.
     */
    protected open fun drawValue(c: Canvas, valueText: String, x: Float, y: Float, color: Int) {
        valuePaint.color = color
        c.drawText(valueText, x, y, valuePaint)
    }

    /**
     * Swaps the axes: the bar is centered vertically on the x value [x] and spans horizontally from [y1] to [y2].
     */
    override fun prepareBarHighlight(x: Float, y1: Float, y2: Float, barWidthHalf: Float, trans: Transformer) {
        val top = x - barWidthHalf
        val bottom = x + barWidthHalf
        val left = y1
        val right = y2

        barRect.set(left, top, right, bottom)

        trans.rectToPixelPhaseHorizontal(barRect, animator.phaseY)
    }

}
