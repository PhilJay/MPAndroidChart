package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.min

/**
 * @noinspection unused
 */
class RoundedHorizontalBarChartRenderer(chart: BarDataProvider, animator: ChartAnimator?, viewPortHandler: ViewPortHandler?) :
    HorizontalBarChartRenderer(chart, animator, viewPortHandler) {
    private val mBarShadowRectBuffer = RectF()
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

    init {
        paintValues.textAlign = Paint.Align.LEFT
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

                if (roundedShadowRadius > 0) {
                    c.drawRoundRect(barRect, roundedShadowRadius, roundedShadowRadius, shadowPaint)
                } else {
                    c.drawRect(mBarShadowRectBuffer, shadowPaint)
                }
                i++
            }
        }

        val buffer = barBuffers[index]!!
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
                if (!viewPortHandler.isInBoundsTop(buffer.buffer[j + 3])) {
                    j += 4
                    continue
                }

                if (!viewPortHandler.isInBoundsBottom(buffer.buffer[j + 1])) {
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
                paintRender.color = dataSet.getColor(j / 4)

                if (roundedPositiveDataSetRadius > 0) {
                    c.drawRoundRect(
                        RectF(
                            buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3]
                        ), roundedPositiveDataSetRadius, roundedPositiveDataSetRadius, paintRender
                    )
                } else {
                    c.drawRect(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], paintRender
                    )
                }
                j += 4
            }
        } else {
            paintRender.color = dataSet.color

            var j = 0
            while (j < buffer.size()) {
                if (!viewPortHandler.isInBoundsTop(buffer.buffer[j + 3])) {
                    j += 4
                    continue
                }

                if (!viewPortHandler.isInBoundsBottom(buffer.buffer[j + 1])) {
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
                            buffer.buffer[j + 3], paintRender
                        )
                    }
                }

                if (roundedPositiveDataSetRadius > 0) {
                    c.drawRoundRect(
                        RectF(
                            buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3]
                        ), roundedPositiveDataSetRadius, roundedPositiveDataSetRadius, paintRender
                    )
                } else {
                    c.drawRect(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], paintRender
                    )
                }
                j += 4
            }
        }

        val isSingleColor = dataSet.colors.size == 1
        if (isSingleColor) {
            paintRender.color = dataSet.getColor(index)
        }

        var j = 0
        while (j < buffer.size()) {
            if (!viewPortHandler.isInBoundsTop(buffer.buffer[j + 3])) {
                j += 4
                continue
            }

            if (!viewPortHandler.isInBoundsBottom(buffer.buffer[j + 1])) {
                break
            }

            if (!isSingleColor) {
                paintRender.color = dataSet.getColor(j / 4)
            }

            if ((dataSet.getEntryForIndex(j / 4).y < 0 && roundedNegativeDataSetRadius > 0)) {
                val path2 = roundRect(
                    RectF(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3]
                    ), roundedNegativeDataSetRadius, roundedNegativeDataSetRadius, true, true, true, true
                )
                c.drawPath(path2, paintRender)
            } else if ((dataSet.getEntryForIndex(j / 4).y > 0 && roundedPositiveDataSetRadius > 0)) {
                val path2 = roundRect(
                    RectF(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3]
                    ), roundedPositiveDataSetRadius, roundedPositiveDataSetRadius, true, true, true, true
                )
                c.drawPath(path2, paintRender)
            } else {
                c.drawRect(
                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], paintRender
                )
            }
            j += 4
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
        if (br) {
            path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner
        } else {
            path.rLineTo(rx, 0f)
            path.rLineTo(0f, -ry)
        }

        path.rLineTo(0f, -heightMinusCorners)
        path.close()
        return path
    }
}
