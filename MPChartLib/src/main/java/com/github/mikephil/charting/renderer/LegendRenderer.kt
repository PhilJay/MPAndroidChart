package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Path
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendDirection
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment
import com.github.mikephil.charting.components.Legend.LegendOrientation
import com.github.mikephil.charting.components.Legend.LegendVerticalAlignment
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import java.util.Collections
import kotlin.math.min

@Suppress("MemberVisibilityCanBePrivate")
open class LegendRenderer(
    viewPortHandler: ViewPortHandler,
    protected var legend: Legend
) : Renderer(viewPortHandler) {
    /**
     * paint for the legend labels
     */
    var labelPaint: Paint
        protected set

    var formPaint: Paint
        protected set

    protected var computedEntries: MutableList<LegendEntry> = ArrayList(16)

    /**
     * Prepares the legend and calculates all needed forms, labels and colors.
     */
    fun computeLegend(data: ChartData<*>) {
        if (!legend.isLegendCustom) {
            computedEntries.clear()

            // loop for building up the colors and labels used in the legend
            for (i in 0..<data.dataSetCount) {
                val dataSet = data.getDataSetByIndex(i) ?: continue

                val clrs = dataSet.colors
                val entryCount = dataSet.entryCount

                // if we have a barchart with stacked bars
                if (dataSet is IBarDataSet && dataSet.isStacked) {
                    val bds = dataSet
                    val sLabels = bds.stackLabels

                    val minEntries = min(clrs.size.toDouble(), bds.stackSize.toDouble()).toInt()

                    for (j in 0..<minEntries) {
                        val label: String?
                        if (sLabels.isNotEmpty()) {
                            val labelIndex = j % minEntries
                            label = if (labelIndex < sLabels.size) sLabels[labelIndex] else null
                        } else {
                            label = null
                        }

                        computedEntries.add(
                            LegendEntry(
                                label,
                                dataSet.getForm(),
                                dataSet.getFormSize(),
                                dataSet.getFormLineWidth(),
                                dataSet.getFormLineDashEffect(),
                                clrs[j]
                            )
                        )
                    }

                    if (bds.label != null) {
                        // add the legend description label
                        computedEntries.add(
                            LegendEntry(
                                dataSet.getLabel(),
                                LegendForm.NONE,
                                Float.NaN,
                                Float.NaN,
                                null,
                                ColorTemplate.COLOR_NONE
                            )
                        )
                    }
                } else if (dataSet is IPieDataSet) {
                    val pds = dataSet

                    var j = 0
                    while (j < clrs.size && j < entryCount) {
                        computedEntries.add(
                            LegendEntry(
                                pds.getEntryForIndex(j).label,
                                dataSet.getForm(),
                                dataSet.getFormSize(),
                                dataSet.getFormLineWidth(),
                                dataSet.getFormLineDashEffect(),
                                clrs[j]
                            )
                        )
                        j++
                    }

                    if (pds.label != null) {
                        // add the legend description label
                        computedEntries.add(
                            LegendEntry(
                                dataSet.getLabel(),
                                LegendForm.NONE,
                                Float.NaN,
                                Float.NaN,
                                null,
                                ColorTemplate.COLOR_NONE
                            )
                        )
                    }
                } else if (dataSet is ICandleDataSet && dataSet.decreasingColor !=
                    ColorTemplate.COLOR_NONE
                ) {
                    val decreasingColor = dataSet.decreasingColor
                    val increasingColor = dataSet.increasingColor

                    computedEntries.add(
                        LegendEntry(
                            null,
                            dataSet.getForm(),
                            dataSet.getFormSize(),
                            dataSet.getFormLineWidth(),
                            dataSet.getFormLineDashEffect(),
                            decreasingColor
                        )
                    )

                    computedEntries.add(
                        LegendEntry(
                            dataSet.getLabel(),
                            dataSet.getForm(),
                            dataSet.getFormSize(),
                            dataSet.getFormLineWidth(),
                            dataSet.getFormLineDashEffect(),
                            increasingColor
                        )
                    )
                } else { // all others

                    var j = 0
                    while (j < clrs.size && j < entryCount) {
                        // if multiple colors are set for a DataSet, group them
                        val label = if (j < clrs.size - 1 && j < entryCount - 1) {
                            null
                        } else { // add label to the last entry
                            data.getDataSetByIndex(i).label
                        }

                        computedEntries.add(
                            LegendEntry(
                                label,
                                dataSet.form,
                                dataSet.formSize,
                                dataSet.formLineWidth,
                                dataSet.formLineDashEffect,
                                clrs[j]
                            )
                        )
                        j++
                    }
                }
            }

            if (legend.extraEntries != null) {
                Collections.addAll(computedEntries, *legend.extraEntries)
            }

            legend.setEntries(computedEntries)
        }

        val tf = legend.typeface

        if (tf != null) labelPaint.setTypeface(tf)

        labelPaint.textSize = legend.textSize
        labelPaint.color = legend.textColor

        // calculate all dimensions of the mLegend
        legend.calculateDimensions(labelPaint, viewPortHandler)
    }

    protected var legendFontMetrics: Paint.FontMetrics = Paint.FontMetrics()

    fun renderLegend(c: Canvas) {
        if (!legend.isEnabled) return

        val tf = legend.typeface

        if (tf != null) labelPaint.setTypeface(tf)

        labelPaint.textSize = legend.textSize
        labelPaint.color = legend.textColor

        val labelLineHeight = Utils.getLineHeight(labelPaint, legendFontMetrics)
        val labelLineSpacing = (Utils.getLineSpacing(labelPaint, legendFontMetrics)
                + Utils.convertDpToPixel(legend.yEntrySpace))
        val formYOffset = labelLineHeight - Utils.calcTextHeight(labelPaint, "ABC") / 2f

        val entries = legend.entries

        val formToTextSpace = Utils.convertDpToPixel(legend.formToTextSpace)
        val xEntrySpace = Utils.convertDpToPixel(legend.xEntrySpace)
        val orientation = legend.orientation
        val horizontalAlignment = legend.horizontalAlignment
        val verticalAlignment = legend.verticalAlignment
        val direction = legend.direction
        val defaultFormSize = Utils.convertDpToPixel(legend.formSize)

        // space between the entries
        val stackSpace = Utils.convertDpToPixel(legend.stackSpace)

        val yOffset = legend.yOffset
        val xOffset = legend.xOffset
        var originPosX = 0f

        when (horizontalAlignment) {
            LegendHorizontalAlignment.LEFT -> {
                originPosX = if (orientation == LegendOrientation.VERTICAL) xOffset
                else viewPortHandler.contentLeft() + xOffset

                if (direction == LegendDirection.RIGHT_TO_LEFT) originPosX += legend.mNeededWidth
            }

            LegendHorizontalAlignment.RIGHT -> {
                originPosX = if (orientation == LegendOrientation.VERTICAL) viewPortHandler.chartWidth - xOffset
                else viewPortHandler.contentRight() - xOffset

                if (direction == LegendDirection.LEFT_TO_RIGHT)
                    originPosX -= legend.mNeededWidth
            }

            LegendHorizontalAlignment.CENTER -> {
                originPosX = if (orientation == LegendOrientation.VERTICAL)
                    viewPortHandler.chartWidth / 2f
                else
                    (viewPortHandler.contentLeft() + viewPortHandler.contentWidth() / 2f)

                originPosX += (if (direction == LegendDirection.LEFT_TO_RIGHT)
                    +xOffset
                else
                    -xOffset)

                // Horizontally layed out legends do the center offset on a line basis,
                // So here we offset the vertical ones only.
                if (orientation == LegendOrientation.VERTICAL) {
                    originPosX += (if (direction == LegendDirection.LEFT_TO_RIGHT)
                        -legend.mNeededWidth / 2.0 + xOffset
                    else
                        legend.mNeededWidth / 2.0 - xOffset).toFloat()
                }
            }
        }

        when (orientation) {
            LegendOrientation.HORIZONTAL -> {
                val calculatedLineSizes = legend.calculatedLineSizes
                val calculatedLabelSizes = legend.calculatedLabelSizes
                val calculatedLabelBreakPoints = legend.calculatedLabelBreakPoints

                var posX = originPosX

                var posY: Float = when (verticalAlignment) {
                    LegendVerticalAlignment.TOP -> yOffset
                    LegendVerticalAlignment.BOTTOM -> viewPortHandler.chartHeight - yOffset - legend.mNeededHeight
                    LegendVerticalAlignment.CENTER -> (viewPortHandler.chartHeight - legend.mNeededHeight) / 2f + yOffset
                }

                var lineIndex = 0

                var i = 0
                val count = entries.size
                while (i < count) {
                    val e = entries[i]
                    val drawingForm = e.form != LegendForm.NONE
                    val formSize = if (java.lang.Float.isNaN(e.formSize)) defaultFormSize else Utils.convertDpToPixel(e.formSize)

                    if (i < calculatedLabelBreakPoints.size && calculatedLabelBreakPoints[i]) {
                        posX = originPosX
                        posY += labelLineHeight + labelLineSpacing
                    }

                    if (posX == originPosX && horizontalAlignment == LegendHorizontalAlignment.CENTER && lineIndex < calculatedLineSizes.size) {
                        posX += (if (direction == LegendDirection.RIGHT_TO_LEFT)
                            calculatedLineSizes[lineIndex].width
                        else
                            -calculatedLineSizes[lineIndex].width) / 2f
                        lineIndex++
                    }

                    val isStacked = e.label == null // grouped forms have null labels

                    if (drawingForm) {
                        if (direction == LegendDirection.RIGHT_TO_LEFT) posX -= formSize

                        drawForm(c, posX, posY + formYOffset, e, legend)

                        if (direction == LegendDirection.LEFT_TO_RIGHT) posX += formSize
                    }

                    if (!isStacked) {
                        if (drawingForm) posX += if (direction == LegendDirection.RIGHT_TO_LEFT) -formToTextSpace else formToTextSpace

                        if (direction == LegendDirection.RIGHT_TO_LEFT) posX -= calculatedLabelSizes[i].width

                        drawLabel(c, posX, posY + labelLineHeight, e.label)

                        if (direction == LegendDirection.LEFT_TO_RIGHT) posX += calculatedLabelSizes[i].width

                        posX += if (direction == LegendDirection.RIGHT_TO_LEFT) -xEntrySpace else xEntrySpace
                    } else posX += if (direction == LegendDirection.RIGHT_TO_LEFT) -stackSpace else stackSpace
                    i++
                }
            }

            LegendOrientation.VERTICAL -> {
                // contains the stacked legend size in pixels
                var stack = 0f
                var wasStacked = false
                var posY = 0f

                when (verticalAlignment) {
                    LegendVerticalAlignment.TOP -> {
                        posY = (if (horizontalAlignment == LegendHorizontalAlignment.CENTER)
                            0f
                        else
                            viewPortHandler.contentTop())
                        posY += yOffset
                    }

                    LegendVerticalAlignment.BOTTOM -> {
                        posY = (if (horizontalAlignment == LegendHorizontalAlignment.CENTER)
                            viewPortHandler.chartHeight
                        else
                            viewPortHandler.contentBottom())
                        posY -= legend.mNeededHeight + yOffset
                    }

                    LegendVerticalAlignment.CENTER -> posY = (viewPortHandler.chartHeight / 2f
                            - legend.mNeededHeight / 2f
                            + legend.yOffset)
                }

                var i = 0
                while (i < entries.size) {
                    val e = entries[i]
                    val drawingForm = e.form != LegendForm.NONE
                    val formSize = if (java.lang.Float.isNaN(e.formSize)) defaultFormSize else Utils.convertDpToPixel(e.formSize)

                    var posX = originPosX

                    if (drawingForm) {
                        if (direction == LegendDirection.LEFT_TO_RIGHT) posX += stack
                        else posX -= formSize - stack

                        drawForm(c, posX, posY + formYOffset, e, legend)

                        if (direction == LegendDirection.LEFT_TO_RIGHT) posX += formSize
                    }

                    if (e.label != null) {
                        if (drawingForm && !wasStacked) posX += if (direction == LegendDirection.LEFT_TO_RIGHT)
                            formToTextSpace
                        else
                            -formToTextSpace
                        else if (wasStacked) posX = originPosX

                        if (direction == LegendDirection.RIGHT_TO_LEFT) posX -= Utils.calcTextWidth(labelPaint, e.label).toFloat()

                        if (!wasStacked) {
                            drawLabel(c, posX, posY + labelLineHeight, e.label)
                        } else {
                            posY += labelLineHeight + labelLineSpacing
                            drawLabel(c, posX, posY + labelLineHeight, e.label)
                        }

                        // make a step down
                        posY += labelLineHeight + labelLineSpacing
                        stack = 0f
                    } else {
                        stack += formSize + stackSpace
                        wasStacked = true
                    }
                    i++
                }
            }
        }
    }

    private val mLineFormPath = Path()

    init {
        labelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        labelPaint.textSize = Utils.convertDpToPixel(9f)
        labelPaint.textAlign = Align.LEFT

        formPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        formPaint.style = Paint.Style.FILL
    }

    /**
     * Draws the Legend-form at the given position with the color at the given
     * index.
     *
     * @param c      canvas to draw with
     * @param x      position
     * @param y      position
     * @param entry  the entry to render
     * @param legend the legend context
     */
    protected fun drawForm(
        c: Canvas,
        x: Float, y: Float,
        entry: LegendEntry,
        legend: Legend
    ) {
        if (entry.formColor == ColorTemplate.COLOR_SKIP || entry.formColor == ColorTemplate.COLOR_NONE || entry.formColor == 0) return

        val restoreCount = c.save()

        var form = entry.form
        if (form == LegendForm.DEFAULT) form = legend.form

        formPaint.color = entry.formColor

        val formSize = Utils.convertDpToPixel(
            if (java.lang.Float.isNaN(entry.formSize))
                legend.formSize
            else
                entry.formSize
        )
        val half = formSize / 2f

        when (form) {
            LegendForm.NONE -> {}
            LegendForm.EMPTY -> {}
            LegendForm.DEFAULT, LegendForm.CIRCLE -> {
                formPaint.style = Paint.Style.FILL
                c.drawCircle(x + half, y, half, formPaint)
            }

            LegendForm.SQUARE -> {
                formPaint.style = Paint.Style.FILL
                c.drawRect(x, y - half, x + formSize, y + half, formPaint)
            }

            LegendForm.LINE -> {
                val formLineWidth = Utils.convertDpToPixel(
                    if (java.lang.Float.isNaN(entry.formLineWidth))
                        legend.formLineWidth
                    else
                        entry.formLineWidth
                )
                val formLineDashEffect = if (entry.formLineDashEffect == null)
                    legend.formLineDashEffect
                else
                    entry.formLineDashEffect
                formPaint.style = Paint.Style.STROKE
                formPaint.strokeWidth = formLineWidth
                formPaint.setPathEffect(formLineDashEffect)

                mLineFormPath.reset()
                mLineFormPath.moveTo(x, y)
                mLineFormPath.lineTo(x + formSize, y)
                c.drawPath(mLineFormPath, formPaint)
            }
        }

        c.restoreToCount(restoreCount)
    }

    /**
     * Draws the provided label at the given position.
     *
     * @param c     canvas to draw with
     * @param x
     * @param y
     * @param label the label to draw
     */
    protected fun drawLabel(c: Canvas, x: Float, y: Float, label: String) {
        c.drawText(label, x, y, labelPaint)
    }
}
