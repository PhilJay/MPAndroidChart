package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Paint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Draws the candles of a [CandleDataProvider]. With `showCandleBar` an entry becomes a body from open to
 * close plus a shadow line from high to low; otherwise it is drawn as a high-low line with an open tick on the
 * left and a close tick on the right. Colors follow whether the entry increased, decreased or stayed equal.
 *
 * @property chart The chart that supplies the candle data and the transformers.
 */
open class CandleStickChartRenderer(
    protected val chart: CandleDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : LineScatterCandleRadarRenderer(animator, viewPortHandler) {

    private val shadowBuffers = FloatArray(8)
    private val bodyBuffers = FloatArray(4)
    private val rangeBuffers = FloatArray(4)
    private val openBuffers = FloatArray(4)
    private val closeBuffers = FloatArray(4)

    override fun lowOf(entry: Entry<*>) = (entry as CandleEntry<*>).low

    override fun highOf(entry: Entry<*>) = (entry as CandleEntry<*>).high

    /**
     * Does nothing; candles are drawn without a buffer.
     */
    override fun initBuffers() {
    }

    override fun drawData(c: Canvas) {
        val candleData = chart.candleData ?: return

        for (set in candleData.dataSets) {
            if (set.isVisible) drawDataSet(c, set)
        }
    }

    /**
     * Draws every candle of [dataSet] inside the visible range, using its shadow width (dp), bar space (x value
     * units), paint styles and colors.
     */
    protected open fun drawDataSet(c: Canvas, dataSet: ICandleDataSet<*>) {
        val trans = chart.getTransformer(dataSet.axisDependency)

        val phaseY = animator.phaseY
        val barSpace = dataSet.barSpace
        val showCandleBar = dataSet.showCandleBar

        xBounds.set(chart, dataSet)
        reduceVisible(chart, dataSet)

        renderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.shadowWidth)

        for (k in 0 until reducer.count) {
            val index = reducer.indices[k]
            val e = dataSet.getEntryForIndex(index)

            val xPos = e.x
            val open = e.open
            val close = e.close
            val high = e.high
            val low = e.low

            if (showCandleBar) {
                shadowBuffers[0] = xPos
                shadowBuffers[2] = xPos
                shadowBuffers[4] = xPos
                shadowBuffers[6] = xPos

                if (open > close) {
                    shadowBuffers[1] = high * phaseY
                    shadowBuffers[3] = open * phaseY
                    shadowBuffers[5] = low * phaseY
                    shadowBuffers[7] = close * phaseY
                } else if (open < close) {
                    shadowBuffers[1] = high * phaseY
                    shadowBuffers[3] = close * phaseY
                    shadowBuffers[5] = low * phaseY
                    shadowBuffers[7] = open * phaseY
                } else {
                    shadowBuffers[1] = high * phaseY
                    shadowBuffers[3] = open * phaseY
                    shadowBuffers[5] = low * phaseY
                    shadowBuffers[7] = shadowBuffers[3]
                }

                trans.pointValuesToPixel(shadowBuffers)

                if (dataSet.shadowColorSameAsCandle) {
                    if (open > close) {
                        renderPaint.color =
                            if (dataSet.decreasingColor == ColorTemplate.COLOR_NONE) dataSet.getColor(index)
                            else dataSet.decreasingColor
                    } else if (open < close) {
                        renderPaint.color =
                            if (dataSet.increasingColor == ColorTemplate.COLOR_NONE) dataSet.getColor(index)
                            else dataSet.increasingColor
                    } else {
                        renderPaint.color =
                            if (dataSet.neutralColor == ColorTemplate.COLOR_NONE) dataSet.getColor(index)
                            else dataSet.neutralColor
                    }
                } else {
                    renderPaint.color =
                        if (dataSet.shadowColor == ColorTemplate.COLOR_NONE) dataSet.getColor(index)
                        else dataSet.shadowColor
                }

                renderPaint.style = Paint.Style.STROKE

                c.drawLines(shadowBuffers, renderPaint)

                bodyBuffers[0] = xPos - 0.5f + barSpace
                bodyBuffers[1] = close * phaseY
                bodyBuffers[2] = xPos + 0.5f - barSpace
                bodyBuffers[3] = open * phaseY

                trans.pointValuesToPixel(bodyBuffers)

                if (open > close) {
                    renderPaint.color =
                        if (dataSet.decreasingColor == ColorTemplate.COLOR_NONE) dataSet.getColor(index)
                        else dataSet.decreasingColor

                    renderPaint.style = dataSet.decreasingPaintStyle

                    c.drawRect(
                        bodyBuffers[0], bodyBuffers[3],
                        bodyBuffers[2], bodyBuffers[1],
                        renderPaint
                    )
                } else if (open < close) {
                    renderPaint.color =
                        if (dataSet.increasingColor == ColorTemplate.COLOR_NONE) dataSet.getColor(index)
                        else dataSet.increasingColor

                    renderPaint.style = dataSet.increasingPaintStyle

                    c.drawRect(
                        bodyBuffers[0], bodyBuffers[1],
                        bodyBuffers[2], bodyBuffers[3],
                        renderPaint
                    )
                } else {
                    renderPaint.color =
                        if (dataSet.neutralColor == ColorTemplate.COLOR_NONE) dataSet.getColor(index)
                        else dataSet.neutralColor

                    c.drawLine(
                        bodyBuffers[0], bodyBuffers[1],
                        bodyBuffers[2], bodyBuffers[3],
                        renderPaint
                    )
                }
            } else {
                rangeBuffers[0] = xPos
                rangeBuffers[1] = high * phaseY
                rangeBuffers[2] = xPos
                rangeBuffers[3] = low * phaseY

                openBuffers[0] = xPos - 0.5f + barSpace
                openBuffers[1] = open * phaseY
                openBuffers[2] = xPos
                openBuffers[3] = open * phaseY

                closeBuffers[0] = xPos + 0.5f - barSpace
                closeBuffers[1] = close * phaseY
                closeBuffers[2] = xPos
                closeBuffers[3] = close * phaseY

                trans.pointValuesToPixel(rangeBuffers)
                trans.pointValuesToPixel(openBuffers)
                trans.pointValuesToPixel(closeBuffers)

                val barColor = if (open > close) {
                    if (dataSet.decreasingColor == ColorTemplate.COLOR_NONE) dataSet.getColor(index)
                    else dataSet.decreasingColor
                } else if (open < close) {
                    if (dataSet.increasingColor == ColorTemplate.COLOR_NONE) dataSet.getColor(index)
                    else dataSet.increasingColor
                } else {
                    if (dataSet.neutralColor == ColorTemplate.COLOR_NONE) dataSet.getColor(index)
                    else dataSet.neutralColor
                }

                renderPaint.color = barColor
                c.drawLine(
                    rangeBuffers[0], rangeBuffers[1],
                    rangeBuffers[2], rangeBuffers[3],
                    renderPaint
                )
                c.drawLine(
                    openBuffers[0], openBuffers[1],
                    openBuffers[2], openBuffers[3],
                    renderPaint
                )
                c.drawLine(
                    closeBuffers[0], closeBuffers[1],
                    closeBuffers[2], closeBuffers[3],
                    renderPaint
                )
            }
        }
    }

    /**
     * Draws the high value of each candle that was drawn above it.
     */
    override fun drawValues(c: Canvas) {
        val dataSets = chart.candleData?.dataSets ?: return

        for (i in dataSets.indices) {
            val dataSet = dataSets[i]

            if (!shouldDrawValues(chart, dataSet) || dataSet.entryCount < 1) continue

            applyValueTextStyle(dataSet)

            val trans = chart.getTransformer(dataSet.axisDependency)

            xBounds.set(chart, dataSet)
            reduceVisible(chart, dataSet)

            val positions = reducedPositions(dataSet, trans, animator.phaseY)

            val yOffset = Utils.convertDpToPixel(5f)

            val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)

            for (k in 0 until reducer.count) {
                val x = positions[2 * k]
                val y = positions[2 * k + 1]

                if (!viewPortHandler.isInBoundsRight(x)) break

                if (!viewPortHandler.isInBoundsLeft(x) || !viewPortHandler.isInBoundsY(y)) continue

                val index = reducer.indices[k]
                val entry = dataSet.getEntryForIndex(index)

                if (dataSet.isDrawValuesEnabled) {
                    drawValue(
                        c,
                        dataSet.valueFormatter,
                        entry.high,
                        entry,
                        i,
                        x,
                        y - yOffset,
                        dataSet.getValueTextColor(index)
                    )
                }

                val icon = entry.icon
                if (icon != null && dataSet.isDrawIconsEnabled) {
                    Utils.drawImage(
                        c,
                        icon,
                        (x + iconsOffset.x).toInt(),
                        (y + iconsOffset.y).toInt(),
                        icon.intrinsicWidth,
                        icon.intrinsicHeight
                    )
                }
            }

            MPPointF.recycleInstance(iconsOffset)
        }
    }

    /**
     * Does nothing; candle charts have no extras.
     */
    override fun drawExtras(c: Canvas) {
    }

    /**
     * Draws the highlight lines through the vertical middle of each highlighted candle.
     */
    override fun drawHighlighted(c: Canvas, indices: List<Highlight>) {
        val candleData = chart.candleData ?: return

        for (high in indices) {
            val set = candleData.getDataSetByIndex(high.dataSetIndex)

            if (set == null || !set.isHighlightEnabled) continue

            val e = set.getEntryForXValue(high.x, high.y, DataSet.Rounding.CLOSEST)

            if (e == null || !isInBoundsX(e, set)) continue

            val lowValue = e.low * animator.phaseY
            val highValue = e.high * animator.phaseY
            val y = (lowValue + highValue) / 2f

            val pix = chart.getTransformer(set.axisDependency).getPixelForValues(e.x, y)

            high.setDraw(pix.x.toFloat(), pix.y.toFloat())

            drawHighlightLines(c, pix.x.toFloat(), pix.y.toFloat(), set)
        }
    }
}
