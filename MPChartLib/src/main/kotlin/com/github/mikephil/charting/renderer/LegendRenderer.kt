package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.min

/**
 * Draws the [Legend] of a chart. [computeLegend] builds the legend entries from the data sets and measures the
 * legend; [renderLegend] then draws the forms and labels at the position given by the legend's alignment,
 * orientation and direction. Assign a subclass to `chart.legendRenderer` to change the drawing.
 *
 * @property legend The legend this renderer draws.
 */
open class LegendRenderer(viewPortHandler: ViewPortHandler, protected val legend: Legend) : Renderer(viewPortHandler) {

    /**
     * Paint for the legend labels; typeface, text size (dp) and color are copied from the legend before drawing.
     * Left aligned, 9 dp by default.
     */
    var labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = Utils.convertDpToPixel(9f)
        textAlign = Paint.Align.LEFT
    }

    /**
     * Paint for the legend forms (circle, square or line); restyled per entry while drawing.
     */
    val formPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    /**
     * The entries built from the data by [computeLegend], followed by the extra entries of the legend.
     */
    protected val computedEntries: MutableList<LegendEntry> = ArrayList(16)

    /**
     * Builds the legend entries from the data sets of [data], unless the legend is custom: one entry per color of a
     * data set, with stack labels for stacked bar sets, entry labels for pie sets and the decreasing and increasing
     * colors for candle sets, plus the extra entries of the legend. Then measures the legend with [labelPaint] so
     * the chart can reserve space for it.
     */
    fun computeLegend(data: ChartData<*>) {
        if (!legend.isLegendCustom) {
            computedEntries.clear()

            for (i in 0 until data.dataSetCount) {
                val dataSet = data.getDataSetByIndex(i) ?: continue

                val clrs = dataSet.colors
                val entryCount = dataSet.entryCount

                if (dataSet is IBarDataSet<*> && dataSet.isStacked) {
                    val sLabels = dataSet.stackLabels
                    val minEntries = min(clrs.size, dataSet.stackSize)

                    for (j in 0 until minEntries) {
                        val label = if (sLabels.isNotEmpty()) {
                            val labelIndex = j % minEntries
                            if (labelIndex < sLabels.size) sLabels[labelIndex] else null
                        } else {
                            null
                        }

                        computedEntries.add(LegendEntry(label, dataSet.form, dataSet.formSize, dataSet.formLineWidth, dataSet.formLineDashEffect, clrs[j]))
                    }

                    computedEntries.add(LegendEntry(dataSet.label, Legend.LegendForm.NONE, Float.NaN, Float.NaN, null, ColorTemplate.COLOR_NONE))
                } else if (dataSet is IPieDataSet<*>) {
                    var j = 0
                    while (j < clrs.size && j < entryCount) {
                        computedEntries.add(LegendEntry(dataSet.getEntryForIndex(j).label, dataSet.form, dataSet.formSize, dataSet.formLineWidth, dataSet.formLineDashEffect, clrs[j]))
                        j++
                    }

                    if (dataSet.label.isNotBlank()) {
                        computedEntries.add(LegendEntry(dataSet.label, Legend.LegendForm.NONE, Float.NaN, Float.NaN, null, ColorTemplate.COLOR_NONE))
                    }
                } else if (dataSet is ICandleDataSet<*> && dataSet.decreasingColor != ColorTemplate.COLOR_NONE) {
                    val decreasingColor = dataSet.decreasingColor
                    val increasingColor = dataSet.increasingColor

                    computedEntries.add(LegendEntry(null, dataSet.form, dataSet.formSize, dataSet.formLineWidth, dataSet.formLineDashEffect, decreasingColor))
                    computedEntries.add(LegendEntry(dataSet.label, dataSet.form, dataSet.formSize, dataSet.formLineWidth, dataSet.formLineDashEffect, increasingColor))
                } else {
                    var j = 0
                    while (j < clrs.size && j < entryCount) {
                        val label = if (j < clrs.size - 1 && j < entryCount - 1) null else dataSet.label
                        computedEntries.add(LegendEntry(label, dataSet.form, dataSet.formSize, dataSet.formLineWidth, dataSet.formLineDashEffect, clrs[j]))
                        j++
                    }
                }
            }

            computedEntries.addAll(legend.extraEntries)

            legend.setComputedEntries(computedEntries)
        }

        val tf = legend.typeface
        if (tf != null) labelPaint.typeface = tf

        labelPaint.textSize = Utils.convertDpToPixel(legend.textSize)
        labelPaint.color = legend.textColor

        legend.calculateDimensions(labelPaint, viewPortHandler)
    }

    private val legendFontMetrics = Paint.FontMetrics()

    /**
     * Draws the legend onto [c]. Does nothing when the legend is disabled.
     */
    fun renderLegend(c: Canvas) {
        if (!legend.isEnabled) return

        val tf = legend.typeface
        if (tf != null) labelPaint.typeface = tf

        labelPaint.textSize = Utils.convertDpToPixel(legend.textSize)
        labelPaint.color = legend.textColor

        val labelLineHeight = Utils.getLineHeight(labelPaint, legendFontMetrics)
        val labelLineSpacing = Utils.getLineSpacing(labelPaint, legendFontMetrics) + Utils.convertDpToPixel(legend.yEntrySpace)
        val formYOffset = labelLineHeight - Utils.calcTextHeight(labelPaint, "ABC") / 2f

        val entries = legend.entries

        val formToTextSpace = Utils.convertDpToPixel(legend.formToTextSpace)
        val xEntrySpace = Utils.convertDpToPixel(legend.xEntrySpace)
        val orientation = legend.orientation
        val horizontalAlignment = legend.horizontalAlignment
        val verticalAlignment = legend.verticalAlignment
        val direction = legend.direction
        val defaultFormSize = Utils.convertDpToPixel(legend.formSize)
        val stackSpace = Utils.convertDpToPixel(legend.stackSpace)

        val yoffset = Utils.convertDpToPixel(legend.yOffset)
        val xoffset = Utils.convertDpToPixel(legend.xOffset)
        var originPosX = 0f

        when (horizontalAlignment) {
            Legend.LegendHorizontalAlignment.LEFT -> {
                originPosX = if (orientation == Legend.LegendOrientation.VERTICAL) xoffset else viewPortHandler.contentLeft + xoffset
                if (direction == Legend.LegendDirection.RIGHT_TO_LEFT) originPosX += legend.neededWidth
            }
            Legend.LegendHorizontalAlignment.RIGHT -> {
                originPosX = if (orientation == Legend.LegendOrientation.VERTICAL) viewPortHandler.chartWidth - xoffset else viewPortHandler.contentRight - xoffset
                if (direction == Legend.LegendDirection.LEFT_TO_RIGHT) originPosX -= legend.neededWidth
            }
            Legend.LegendHorizontalAlignment.CENTER -> {
                originPosX = if (orientation == Legend.LegendOrientation.VERTICAL) viewPortHandler.chartWidth / 2f else viewPortHandler.contentLeft + viewPortHandler.contentWidth / 2f

                originPosX += if (direction == Legend.LegendDirection.LEFT_TO_RIGHT) xoffset else -xoffset

                if (orientation == Legend.LegendOrientation.VERTICAL) {
                    originPosX += (if (direction == Legend.LegendDirection.LEFT_TO_RIGHT) -legend.neededWidth / 2.0 + xoffset else legend.neededWidth / 2.0 - xoffset).toFloat()
                }
            }
        }

        when (orientation) {
            Legend.LegendOrientation.HORIZONTAL -> {
                val calculatedLineSizes = legend.calculatedLineSizes
                val calculatedLabelSizes = legend.calculatedLabelSizes
                val calculatedLabelBreakPoints = legend.calculatedLabelBreakPoints

                var posX = originPosX
                var posY = 0f

                when (verticalAlignment) {
                    Legend.LegendVerticalAlignment.TOP -> posY = yoffset
                    Legend.LegendVerticalAlignment.BOTTOM -> posY = viewPortHandler.chartHeight - yoffset - legend.neededHeight
                    Legend.LegendVerticalAlignment.CENTER -> posY = (viewPortHandler.chartHeight - legend.neededHeight) / 2f + yoffset
                }

                var lineIndex = 0

                for (i in entries.indices) {
                    val e = entries[i]
                    val drawingForm = e.form != Legend.LegendForm.NONE
                    val formSize = if (e.formSize.isNaN()) defaultFormSize else Utils.convertDpToPixel(e.formSize)

                    if (i < calculatedLabelBreakPoints.size && calculatedLabelBreakPoints[i]) {
                        posX = originPosX
                        posY += labelLineHeight + labelLineSpacing
                    }

                    if (posX == originPosX && horizontalAlignment == Legend.LegendHorizontalAlignment.CENTER && lineIndex < calculatedLineSizes.size) {
                        posX += (if (direction == Legend.LegendDirection.RIGHT_TO_LEFT) calculatedLineSizes[lineIndex].width else -calculatedLineSizes[lineIndex].width) / 2f
                        lineIndex++
                    }

                    val isStacked = e.label == null

                    if (drawingForm) {
                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT) posX -= formSize

                        drawForm(c, posX, posY + formYOffset, e, legend)

                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT) posX += formSize
                    }

                    if (!isStacked) {
                        if (drawingForm) posX += if (direction == Legend.LegendDirection.RIGHT_TO_LEFT) -formToTextSpace else formToTextSpace

                        val label = e.label ?: ""
                        // The sizes are only measured for a horizontal legend, so a legend that was switched to
                        // horizontal, or whose entries were replaced, after the last pass has none for this entry.
                        val labelWidth = if (i < calculatedLabelSizes.size) {
                            calculatedLabelSizes[i].width
                        } else {
                            Utils.calcTextWidth(labelPaint, label).toFloat()
                        }

                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT) posX -= labelWidth

                        drawLabel(c, posX, posY + labelLineHeight, label)

                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT) posX += labelWidth

                        posX += if (direction == Legend.LegendDirection.RIGHT_TO_LEFT) -xEntrySpace else xEntrySpace
                    } else {
                        posX += if (direction == Legend.LegendDirection.RIGHT_TO_LEFT) -stackSpace else stackSpace
                    }
                }
            }
            Legend.LegendOrientation.VERTICAL -> {
                var stack = 0f
                var wasStacked = false
                var posY = 0f

                when (verticalAlignment) {
                    Legend.LegendVerticalAlignment.TOP -> {
                        posY = if (horizontalAlignment == Legend.LegendHorizontalAlignment.CENTER) 0f else viewPortHandler.contentTop
                        posY += yoffset
                    }
                    Legend.LegendVerticalAlignment.BOTTOM -> {
                        posY = if (horizontalAlignment == Legend.LegendHorizontalAlignment.CENTER) viewPortHandler.chartHeight else viewPortHandler.contentBottom
                        posY -= legend.neededHeight + yoffset
                    }
                    Legend.LegendVerticalAlignment.CENTER -> {
                        posY = viewPortHandler.chartHeight / 2f - legend.neededHeight / 2f + Utils.convertDpToPixel(legend.yOffset)
                    }
                }

                for (e in entries) {
                    val drawingForm = e.form != Legend.LegendForm.NONE
                    val formSize = if (e.formSize.isNaN()) defaultFormSize else Utils.convertDpToPixel(e.formSize)

                    var posX = originPosX

                    if (drawingForm) {
                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT) posX += stack else posX -= formSize - stack

                        drawForm(c, posX, posY + formYOffset, e, legend)

                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT) posX += formSize
                    }

                    val label = e.label
                    if (label != null) {
                        if (drawingForm && !wasStacked) {
                            posX += if (direction == Legend.LegendDirection.LEFT_TO_RIGHT) formToTextSpace else -formToTextSpace
                        } else if (wasStacked) {
                            posX = originPosX
                        }

                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT) posX -= Utils.calcTextWidth(labelPaint, label)

                        if (!wasStacked) {
                            drawLabel(c, posX, posY + labelLineHeight, label)
                        } else {
                            posY += labelLineHeight + labelLineSpacing
                            drawLabel(c, posX, posY + labelLineHeight, label)
                        }

                        posY += labelLineHeight + labelLineSpacing
                        stack = 0f
                    } else {
                        stack += formSize + stackSpace
                        wasStacked = true
                    }
                }
            }
        }
    }

    private val lineFormPath = Path()

    /**
     * Draws the form of [entry] with its left edge at the pixel position [x] and its vertical center at [y]. Form,
     * size (dp), line width (dp) and dash effect fall back to those of [legend] where the entry leaves them unset.
     * Does nothing for entries without a color.
     */
    protected open fun drawForm(c: Canvas, x: Float, y: Float, entry: LegendEntry, legend: Legend) {
        if (entry.formColor == ColorTemplate.COLOR_SKIP || entry.formColor == ColorTemplate.COLOR_NONE || entry.formColor == 0) return

        val restoreCount = c.save()

        var form = entry.form
        if (form == Legend.LegendForm.DEFAULT) form = legend.form

        formPaint.color = entry.formColor

        val formSize = Utils.convertDpToPixel(if (entry.formSize.isNaN()) legend.formSize else entry.formSize)
        val half = formSize / 2f

        when (form) {
            Legend.LegendForm.NONE, Legend.LegendForm.EMPTY -> {}
            Legend.LegendForm.DEFAULT, Legend.LegendForm.CIRCLE -> {
                formPaint.style = Paint.Style.FILL
                c.drawCircle(x + half, y, half, formPaint)
            }
            Legend.LegendForm.SQUARE -> {
                formPaint.style = Paint.Style.FILL
                c.drawRect(x, y - half, x + formSize, y + half, formPaint)
            }
            Legend.LegendForm.LINE -> {
                val formLineWidth = Utils.convertDpToPixel(if (entry.formLineWidth.isNaN()) legend.formLineWidth else entry.formLineWidth)
                val formLineDashEffect = entry.formLineDashEffect ?: legend.formLineDashEffect
                formPaint.style = Paint.Style.STROKE
                formPaint.strokeWidth = formLineWidth
                formPaint.pathEffect = formLineDashEffect

                lineFormPath.reset()
                lineFormPath.moveTo(x, y)
                lineFormPath.lineTo(x + formSize, y)
                c.drawPath(lineFormPath, formPaint)
            }
        }

        c.restoreToCount(restoreCount)
    }

    /**
     * Draws [label] with [labelPaint] at the pixel position ([x], [y]), where [y] is the text baseline.
     */
    protected open fun drawLabel(c: Canvas, x: Float, y: Float, label: String) {
        c.drawText(label, x, y, labelPaint)
    }
}
