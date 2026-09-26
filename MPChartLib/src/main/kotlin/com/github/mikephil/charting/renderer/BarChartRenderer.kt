package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.buffer.BarBuffer
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.Fill
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.max

/**
 * Draws the bars of a [BarDataProvider] (a bar chart, or a combined chart with bar data). The bars of each data
 * set are collected in a [BarBuffer] in value space, converted to pixels in one step and drawn as rectangles
 * or custom fills. A highlight is drawn as a translucent rectangle over the selected bar or stack value.
 *
 * @property chart The chart that supplies the bar data, the transformers and the bar settings.
 */
public open class BarChartRenderer(protected val chart: BarDataProvider, animator: ChartAnimator, viewPortHandler: ViewPortHandler) : BarLineScatterCandleBubbleRenderer(animator, viewPortHandler) {

    /**
     * Pixel rectangle of the bar being highlighted, filled by [prepareBarHighlight].
     */
    protected val barRect: RectF = RectF()

    /** Clip path for rounded bars, reused across draws. */
    protected val barShapePath: Path = Path()

    /** Corner radii for [barShapePath], two values per corner starting top left. */
    protected val barShapeRadii: FloatArray = FloatArray(8)

    /** Which corners of a bar get rounded. */
    protected enum class BarCorners { ALL, TOP, BOTTOM, LEFT, RIGHT, NONE }

    /**
     * Runs [draw] clipped to the rounded shape of a bar when [dataSet] has a [IBarDataSet.barCornerRadius],
     * otherwise runs it directly. Coordinates are pixels.
     */
    protected inline fun drawBarShape(c: Canvas, dataSet: IBarDataSet<*>, left: Float, top: Float, right: Float, bottom: Float, corners: BarCorners, draw: () -> Unit) {
        val radius = Utils.convertDpToPixel(dataSet.barCornerRadius)
        if (radius <= 0f || corners == BarCorners.NONE) {
            draw()
            return
        }
        val r = minOf(radius, kotlin.math.abs(right - left) / 2f, kotlin.math.abs(bottom - top) / 2f)
        val radii = barShapeRadii
        java.util.Arrays.fill(radii, 0f)
        if (corners == BarCorners.ALL || corners == BarCorners.TOP || corners == BarCorners.LEFT) { radii[0] = r; radii[1] = r }
        if (corners == BarCorners.ALL || corners == BarCorners.TOP || corners == BarCorners.RIGHT) { radii[2] = r; radii[3] = r }
        if (corners == BarCorners.ALL || corners == BarCorners.BOTTOM || corners == BarCorners.RIGHT) { radii[4] = r; radii[5] = r }
        if (corners == BarCorners.ALL || corners == BarCorners.BOTTOM || corners == BarCorners.LEFT) { radii[6] = r; radii[7] = r }
        barShapePath.rewind()
        barShapePath.addRoundRect(minOf(left, right), minOf(top, bottom), maxOf(left, right), maxOf(top, bottom), radii, Path.Direction.CW)
        val save = c.save()
        c.clipPath(barShapePath)
        draw()
        c.restoreToCount(save)
    }

    /**
     * Corners to round for the bar rect at [pos] of a data set: all for plain bars and, in a stack, all of every
     * section when the set rounds its sections, otherwise only the end of the outermost one.
     *
     * @param sections how many values the entry this rect belongs to carries, which is at most the stack size
     *   of the data set; an entry that carries fewer leaves the rest of its room in the buffer empty.
     * @param outermost corners of the section farthest from zero, which is the top in a vertical bar chart
     */
    protected fun barCorners(dataSet: IBarDataSet<*>, pos: Int, sections: Int, outermost: BarCorners = BarCorners.TOP): BarCorners {
        if (!dataSet.isStacked) return BarCorners.ALL
        if (dataSet.isStackSectionsRounded) return BarCorners.ALL
        return if (pos % dataSet.stackSize == sections - 1) outermost else BarCorners.NONE
    }

    /**
     * One buffer per data set holding its bar rectangles, created by [initBuffers]. Empty until the chart has data.
     */
    protected var barBuffers: Array<BarBuffer> = emptyArray()

    /**
     * Paint for the bar shadow drawn behind each bar over the full content height when the chart enables it; its
     * color is the bar shadow color of the data set.
     */
    protected val shadowPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    /**
     * Stroke paint for the bar borders; color and width (dp) come from the data set.
     */
    protected val barBorderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    /**
     * Rectangle reused while drawing bar shadows.
     */
    protected val barShadowRectBuffer: RectF = RectF()

    init {
        highlightPaint.style = Paint.Style.FILL
        highlightPaint.color = Color.rgb(0, 0, 0)
        highlightPaint.alpha = 120
    }

    /**
     * Creates one [BarBuffer] per data set, sized for four floats per bar (times the stack size for stacked sets).
     * Does nothing without bar data and nothing while the buffers still fit, so a chart that keeps appending
     * entries does not throw its buffers away on every call. An override is expected to keep that.
     */
    override fun initBuffers() {
        val barData = chart.barData ?: return
        if (buffersFitData(barData)) return

        barBuffers = Array(barData.dataSetCount) { i ->
            val set = barData.dataSets[i]
            BarBuffer(set.entryCount * 4 * (if (set.isStacked) set.stackSize else 1), barData.dataSetCount, set.isStacked)
        }
    }

    /**
     * True while [barBuffers] still holds one buffer for every data set of [barData] that has both its size and
     * its stacking. The stacking is part of the question because two data sets of different shape can need the
     * same number of floats, and a buffer built for plain bars would drop the sections of a stacked one.
     */
    protected fun buffersFitData(barData: BarData): Boolean {
        if (barBuffers.size != barData.dataSetCount) return false

        for (i in 0 until barData.dataSetCount) {
            val set = barData.dataSets[i]
            if (barBuffers[i].containsStacks != set.isStacked) return false
            if (barBuffers[i].size != set.entryCount * 4 * (if (set.isStacked) set.stackSize else 1)) return false
        }

        return true
    }

    override fun drawData(c: Canvas) {
        val barData = chart.barData ?: return

        // The data can change without the chart being told, and a renderer can be installed after the data.
        initBuffers()

        for (i in 0 until barData.dataSetCount) {
            val set = barData.getDataSetByIndex(i) ?: continue

            if (set.isVisible) {
                drawDataSet(c, set, i)
            }
        }
    }

    /**
     * Draws the bars of [dataSet], the data set at [index] in the bar data, that lie inside the visible x range:
     * first their shadows when the chart enables them, then each bar with its color or custom fill, then its
     * border when the border width is above 0.
     *
     * The visible range rounds out to one entry past each edge, which is enough room for a bar whose center sits
     * off screen while part of it still shows. A bar wider than about two x values would reach further than that
     * and lose its edge.
     */
    protected open fun drawDataSet(c: Canvas, dataSet: IBarDataSet<*>, index: Int) {
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

                barShadowRectBuffer.left = x - barWidthHalf
                barShadowRectBuffer.right = x + barWidthHalf

                trans.rectValueToPixel(barShadowRectBuffer)

                if (!viewPortHandler.isInBoundsLeft(barShadowRectBuffer.right)) continue

                if (!viewPortHandler.isInBoundsRight(barShadowRectBuffer.left)) break

                barShadowRectBuffer.top = viewPortHandler.contentTop
                barShadowRectBuffer.bottom = viewPortHandler.contentBottom

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

            if (!viewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) continue

            if (!viewPortHandler.isInBoundsRight(buffer.buffer[j])) break

            if (!isSingleColor) {
                renderPaint.color = dataSet.getColor(pos)
            }

            drawBarShape(c, dataSet, buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3], barCorners(dataSet, pos, sections)) {
                if (isCustomFill) {
                    dataSet.getFill(pos).fillRect(
                        c, renderPaint,
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        buffer.buffer[j + 2],
                        buffer.buffer[j + 3],
                        if (isInverted) Fill.Direction.DOWN else Fill.Direction.UP
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

    /**
     * Fills [barRect] with the pixel rectangle of the bar centered at the x value [x], spanning from the value [y1]
     * to [y2], using [trans] and the y animation phase.
     *
     * @param barWidthHalf half the bar width in x value units
     */
    protected open fun prepareBarHighlight(x: Float, y1: Float, y2: Float, barWidthHalf: Float, trans: Transformer) {
        val left = x - barWidthHalf
        val right = x + barWidthHalf
        val top = y1
        val bottom = y2

        barRect.set(left, top, right, bottom)

        trans.rectToPixelPhase(barRect, animator.phaseY)
    }

    /**
     * Draws the value label and icon of each visible bar above it (or below it for negative values, and the other
     * way round when values are drawn inside the bars). Stacked bars get one label per stack value.
     */
    override fun drawValues(c: Canvas) {
        val barData = chart.barData ?: return
        val dataSets = barData.dataSets

        val valueOffsetPlus = Utils.convertDpToPixel(4.5f)
        var posOffset: Float
        var negOffset: Float
        val drawValueAboveBar = chart.isDrawValueAboveBarEnabled

        for (i in 0 until barData.dataSetCount) {
            val dataSet = dataSets[i]

            if (!shouldDrawValues(chart, dataSet)) continue

            applyValueTextStyle(dataSet)

            val isInverted = chart.isInverted(dataSet.axisDependency)

            val valueTextHeight = Utils.calcTextHeight(valuePaint, "8").toFloat()
            posOffset = if (drawValueAboveBar) -valueOffsetPlus else valueTextHeight + valueOffsetPlus
            negOffset = if (drawValueAboveBar) valueTextHeight + valueOffsetPlus else -valueOffsetPlus

            if (isInverted) {
                posOffset = -posOffset - valueTextHeight
                negOffset = -negOffset - valueTextHeight
            }

            val buffer = barBuffers[i]

            val phaseY = animator.phaseY

            val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)

            if (!dataSet.isStacked) {
                var j = 0
                while (j < buffer.filledSize) {
                    val x = (buffer.buffer[j] + buffer.buffer[j + 2]) / 2f

                    if (!viewPortHandler.isInBoundsRight(x)) break

                    if (!viewPortHandler.isInBoundsY(buffer.buffer[j + 1]) || !viewPortHandler.isInBoundsLeft(x)) {
                        j += 4
                        continue
                    }

                    val entryIndex = buffer.entryOfBar(j / 4)
                    val entry = dataSet.getEntryForIndex(entryIndex)
                    val value = entry.y

                    if (dataSet.isDrawValuesEnabled) {
                        drawValue(
                            c, dataSet.valueFormatter, value, entry, i, x,
                            if (value >= 0) buffer.buffer[j + 1] + posOffset else buffer.buffer[j + 3] + negOffset,
                            dataSet.getValueTextColor(entryIndex)
                        )
                    }

                    val icon = entry.icon
                    if (icon != null && dataSet.isDrawIconsEnabled) {
                        var px = x
                        var py = if (value >= 0) buffer.buffer[j + 1] + posOffset else buffer.buffer[j + 3] + negOffset

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

                    val vals = entry.stackValues
                    val x = (buffer.buffer[bufferIndex] + buffer.buffer[bufferIndex + 2]) / 2f

                    val color = dataSet.getValueTextColor(index)

                    val entryStart = bufferIndex
                    bufferIndex += entryStride

                    if (vals == null) {
                        if (!viewPortHandler.isInBoundsRight(x)) break

                        if (!viewPortHandler.isInBoundsY(buffer.buffer[entryStart + 1]) || !viewPortHandler.isInBoundsLeft(x)) continue

                        if (dataSet.isDrawValuesEnabled) {
                            drawValue(
                                c, dataSet.valueFormatter, entry.y, entry, i, x,
                                buffer.buffer[entryStart + 1] + (if (entry.y >= 0) posOffset else negOffset),
                                color
                            )
                        }

                        val icon = entry.icon
                        if (icon != null && dataSet.isDrawIconsEnabled) {
                            var px = x
                            var py = buffer.buffer[entryStart + 1] + (if (entry.y >= 0) posOffset else negOffset)

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

                            transformed[k + 1] = y * phaseY
                            k += 2
                            idx++
                        }

                        trans.pointValuesToPixel(transformed)

                        for (k in transformed.indices step 2) {
                            val value = vals[k / 2]
                            val drawBelow = (value == 0.0f && negY == 0.0f && posY > 0.0f) || value < 0.0f
                            val y = transformed[k + 1] + (if (drawBelow) negOffset else posOffset)

                            if (!viewPortHandler.isInBoundsRight(x)) break

                            if (!viewPortHandler.isInBoundsY(y) || !viewPortHandler.isInBoundsLeft(x)) continue

                            if (dataSet.isDrawValuesEnabled) {
                                drawValue(c, dataSet.valueFormatter, vals[k / 2], k / 2, entry, i, x, y, color)
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
     * Draws a rectangle in the highlight color and alpha of the data set over each highlighted bar. For a stacked
     * entry only the selected stack value is covered, unless the chart highlights full bars.
     */
    override fun drawHighlighted(c: Canvas, indices: List<Highlight>) {
        val barData = chart.barData ?: return

        for (high in indices) {
            val set = barData.getDataSetByIndex(high.dataSetIndex)

            if (set == null || !set.isHighlightEnabled) continue

            val e = set.getEntryForXValue(high.x, high.y, DataSet.Rounding.CLOSEST)

            if (e == null || !isInBoundsX(e, set)) continue

            val trans = chart.getTransformer(set.axisDependency)

            highlightPaint.color = set.highlightColor
            highlightPaint.alpha = set.highlightAlpha

            val isStack = high.stackIndex >= 0 && e.isStacked

            val y1: Float
            val y2: Float

            if (isStack) {
                if (chart.isHighlightFullBarEnabled) {
                    y1 = e.positiveSum
                    y2 = -e.negativeSum
                } else {
                    val range = e.ranges?.getOrNull(high.stackIndex) ?: continue

                    y1 = range.from
                    y2 = range.to
                }
            } else {
                y1 = e.y
                y2 = 0f
            }

            prepareBarHighlight(e.x, y1, y2, highlightedBarWidth(barData, high.dataSetIndex) / 2f, trans)

            setHighlightDrawPos(high, barRect)

            val corners = if (!isStack || chart.isHighlightFullBarEnabled || set.isStackSectionsRounded) BarCorners.ALL else BarCorners.NONE
            drawBarShape(c, set, barRect.left, barRect.top, barRect.right, barRect.bottom, corners) {
                c.drawRect(barRect, highlightPaint)
            }
        }
    }

    /**
     * Width in x value units of the bar the highlight has to cover for the data set at [index], which is the
     * pixel column that set was drawn with while its bars stood for a whole column each. Falls back to the bar
     * width of [barData] for a set that has no buffer yet or was skipped as invisible and whose buffer still
     * holds the column of an older draw.
     */
    private fun highlightedBarWidth(barData: BarData, index: Int): Float {
        val set = barData.getDataSetByIndex(index)
        if (set == null || !set.isVisible || index !in barBuffers.indices) return barData.barWidth

        return max(barData.barWidth, barBuffers[index].columnWidth)
    }

    /**
     * Stores on [high] the pixel position where a marker for the highlighted [bar] is drawn: its top center.
     */
    protected open fun setHighlightDrawPos(high: Highlight, bar: RectF) {
        high.setDraw(bar.centerX(), bar.top)
    }

    /**
     * Does nothing; bar charts have no extras.
     */
    override fun drawExtras(c: Canvas) {
    }
}
