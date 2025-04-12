package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.min

/** @noinspection unused
 */
class RoundedBarChartRenderer(chart: BarDataProvider, animator: ChartAnimator?, viewPortHandler: ViewPortHandler?) :
    BarChartRenderer(chart, animator, viewPortHandler) {
    private val mBarShadowRectBuffer = RectF()
    private val mRadius = 20f
    private var roundedShadowRadius = 0f
    private var roundedPositiveDataSetRadius = 0f
    private var roundedNegativeDataSetRadius = 0f

    fun setRoundedNegativeDataSetRadius(roundedNegativeDataSet: Float) {
        roundedNegativeDataSetRadius = roundedNegativeDataSet
    }

    fun setRoundedShadowRadius(roundedShadow: Float) {
        roundedShadowRadius = roundedShadow
    }

    fun setRoundedPositiveDataSetRadius(roundedPositiveDataSet: Float) {
        roundedPositiveDataSetRadius = roundedPositiveDataSet
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        initBuffers()
        val trans = chart.getTransformer(dataSet.axisDependency)
        barBorderPaint.color = dataSet.barBorderColor
        barBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)
        shadowPaint.color = dataSet.barShadowColor
        val drawBorder = dataSet.barBorderWidth > 0f
        val phaseX = animator.phaseX
        val phaseY = animator.phaseY

        if (chart.isDrawBarShadowEnabled) {
            shadowPaint.color = dataSet.barShadowColor
            val barData = chart.barData
            val barWidth = barData.barWidth
            val barWidthHalf = barWidth / 2.0f
            var x: Float
            var i = 0
            val count = min((dataSet.entryCount.toFloat() * phaseX).toDouble().toInt().toDouble(), dataSet.entryCount.toDouble())
            while (i < count) {
                val e = dataSet.getEntryForIndex(i)
                x = e.x
                mBarShadowRectBuffer.left = x - barWidthHalf
                mBarShadowRectBuffer.right = x + barWidthHalf
                trans!!.rectValueToPixel(mBarShadowRectBuffer)
                if (!viewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                    i++
                    continue
                }
                if (!viewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left)) {
                    break
                }
                mBarShadowRectBuffer.top = viewPortHandler.contentTop()
                mBarShadowRectBuffer.bottom = viewPortHandler.contentBottom()


                if (roundedShadowRadius > 0) {
                    c.drawRoundRect(barRect, roundedShadowRadius, roundedShadowRadius, shadowPaint)
                } else {
                    c.drawRect(mBarShadowRectBuffer, shadowPaint)
                }
                i++
            }
        }

        val buffer = barBuffers!![index]!!
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(chart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(chart.barData.barWidth)
        buffer.feed(dataSet)
        trans!!.pointValuesToPixel(buffer.buffer)

        // if multiple colors has been assigned to Bar Chart
        if (dataSet.colors.size > 1) {
            var j = 0
            while (j < buffer.size()) {
                if (!viewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                    j += 4
                    continue
                }

                if (!viewPortHandler.isInBoundsRight(buffer.buffer[j])) {
                    break
                }

                if (chart.isDrawBarShadowEnabled) {
                    if (roundedShadowRadius > 0) {
                        c.drawRoundRect(
                            RectF(
                                buffer.buffer[j], viewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                viewPortHandler.contentBottom()
                            ), roundedShadowRadius, roundedShadowRadius, shadowPaint
                        )
                    } else {
                        c.drawRect(
                            buffer.buffer[j], viewPortHandler.contentTop(),
                            buffer.buffer[j + 2],
                            viewPortHandler.contentBottom(), shadowPaint
                        )
                    }
                }

                // Set the color for the currently drawn value. If the index
                renderPaint.color = dataSet.getColor(j / 4)

                if (roundedPositiveDataSetRadius > 0) {
                    c.drawRoundRect(
                        RectF(
                            buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3]
                        ), roundedPositiveDataSetRadius, roundedPositiveDataSetRadius, renderPaint
                    )
                } else {
                    c.drawRect(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], renderPaint
                    )
                }
                j += 4
            }
        } else {
            renderPaint.color = dataSet.color

            var j = 0
            while (j < buffer.size()) {
                if (!viewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                    j += 4
                    continue
                }

                if (!viewPortHandler.isInBoundsRight(buffer.buffer[j])) {
                    break
                }

                if (chart.isDrawBarShadowEnabled) {
                    if (roundedShadowRadius > 0) {
                        c.drawRoundRect(
                            RectF(
                                buffer.buffer[j], viewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                viewPortHandler.contentBottom()
                            ), roundedShadowRadius, roundedShadowRadius, shadowPaint
                        )
                    } else {
                        c.drawRect(
                            buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], renderPaint
                        )
                    }
                }

                if (roundedPositiveDataSetRadius > 0) {
                    c.drawRoundRect(
                        RectF(
                            buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3]
                        ), roundedPositiveDataSetRadius, roundedPositiveDataSetRadius, renderPaint
                    )
                } else {
                    c.drawRect(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], renderPaint
                    )
                }
                j += 4
            }
        }


        val isSingleColor = dataSet.colors.size == 1
        if (isSingleColor) {
            renderPaint.color = dataSet.getColor(index)
        }

        var j = 0
        while (j < buffer.size()) {
            if (!viewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4
                continue
            }

            if (!viewPortHandler.isInBoundsRight(buffer.buffer[j])) {
                break
            }

            if (!isSingleColor) {
                renderPaint.color = dataSet.getColor(j / 4)
            }

            renderPaint.setShader(
                LinearGradient(
                    buffer.buffer[j],
                    buffer.buffer[j + 3],
                    buffer.buffer[j],
                    buffer.buffer[j + 1],
                    dataSet.getColor(j / 4),
                    dataSet.getColor(j / 4),
                    Shader.TileMode.MIRROR
                )
            )

            renderPaint.setShader(
                LinearGradient(
                    buffer.buffer[j],
                    buffer.buffer[j + 3],
                    buffer.buffer[j],
                    buffer.buffer[j + 1],
                    dataSet.getColor(j / 4),
                    dataSet.getColor(j / 4),
                    Shader.TileMode.MIRROR
                )
            )


            if ((dataSet.getEntryForIndex(j / 4).y < 0 && roundedNegativeDataSetRadius > 0)) {
                val path2 = roundRect(
                    RectF(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3]
                    ), roundedNegativeDataSetRadius, roundedNegativeDataSetRadius, true, true, true, true
                )
                c.drawPath(path2, renderPaint)
            } else if ((dataSet.getEntryForIndex(j / 4).y > 0 && roundedPositiveDataSetRadius > 0)) {
                val path2 = roundRect(
                    RectF(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3]
                    ), roundedPositiveDataSetRadius, roundedPositiveDataSetRadius, true, true, true, true
                )
                c.drawPath(path2, renderPaint)
            } else {
                c.drawRect(
                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], renderPaint
                )
            }

            j += 4
        }
    }

    override fun drawHighlighted(c: Canvas, indices: Array<Highlight>) {
        val barData = chart.barData

        for (high in indices) {
            val set = barData.getDataSetByIndex(high.dataSetIndex)

            if (set == null || !set.isHighlightEnabled) {
                continue
            }

            val e = set.getEntryForXValue(high.x, high.y)

            if (!isInBoundsX(e, set)) {
                continue
            }

            val trans = chart.getTransformer(set.axisDependency)

            highlightPaint.color = set.highLightColor
            highlightPaint.alpha = set.highLightAlpha

            val isStack = high.stackIndex >= 0 && e.isStacked

            val y1: Float
            val y2: Float

            if (isStack) {
                if (chart.isHighlightFullBarEnabled) {
                    y1 = e.positiveSum
                    y2 = -e.negativeSum
                } else {
                    val range = e.ranges[high.stackIndex]

                    y1 = range.from
                    y2 = range.to
                }
            } else {
                y1 = e.y
                y2 = 0f
            }

            prepareBarHighlight(e.x, y1, y2, barData.barWidth / 2f, trans!!)

            setHighlightDrawPos(high, barRect)

            val path2 = roundRect(
                RectF(
                    barRect.left, barRect.top, barRect.right,
                    barRect.bottom
                ), mRadius, mRadius, true, true, true, true
            )

            c.drawPath(path2, highlightPaint)
        }
    }

    private fun roundRect(rect: RectF, rx: Float, ry: Float, tl: Boolean, tr: Boolean, br: Boolean, bl: Boolean): Path {
        var rx = rx
        var ry = ry
        val top = rect.top
        val left = rect.left
        val right = rect.right
        val bottom = rect.bottom
        val path = Path()
        if (rx < 0) {
            rx = 0f
        }
        if (ry < 0) {
            ry = 0f
        }
        val width = right - left
        val height = bottom - top
        if (rx > width / 2) {
            rx = width / 2
        }
        if (ry > height / 2) {
            ry = height / 2
        }
        val widthMinusCorners = (width - (2 * rx))
        val heightMinusCorners = (height - (2 * ry))

        path.moveTo(right, top + ry)
        if (tr) {
            path.rQuadTo(0f, -ry, -rx, -ry) //top-right corner
        } else {
            path.rLineTo(0f, -ry)
            path.rLineTo(-rx, 0f)
        }
        path.rLineTo(-widthMinusCorners, 0f)
        if (tl) {
            path.rQuadTo(-rx, 0f, -rx, ry) //top-left corner
        } else {
            path.rLineTo(-rx, 0f)
            path.rLineTo(0f, ry)
        }
        path.rLineTo(0f, heightMinusCorners)

        if (bl) {
            path.rQuadTo(0f, ry, rx, ry) //bottom-left corner
        } else {
            path.rLineTo(0f, ry)
            path.rLineTo(rx, 0f)
        }

        path.rLineTo(widthMinusCorners, 0f)
        if (br) path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner
        else {
            path.rLineTo(rx, 0f)
            path.rLineTo(0f, -ry)
        }

        path.rLineTo(0f, -heightMinusCorners)
        path.close()
        return path
    }
}
