package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BubbleDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

@Suppress("MemberVisibilityCanBePrivate")
open class BubbleChartRenderer(
    @JvmField
    var chart: BubbleDataProvider, animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : BarLineScatterCandleBubbleRenderer(animator, viewPortHandler) {
    override fun initBuffers() {
    }

    override fun drawData(c: Canvas) {
        val bubbleData = chart.bubbleData

        for (set in bubbleData.dataSets) {
            if (set.isVisible) drawDataSet(c, set)
        }
    }

    private val sizeBuffer = FloatArray(4)
    private val pointBuffer = FloatArray(2)

    protected fun getShapeSize(entrySize: Float, maxSize: Float, reference: Float, normalizeSize: Boolean): Float {
        val factor = if (normalizeSize) (if (maxSize == 0f) 1f else sqrt((entrySize / maxSize).toDouble()).toFloat()) else entrySize
        val shapeSize = reference * factor
        return shapeSize
    }

    protected fun drawDataSet(c: Canvas, dataSet: IBubbleDataSet) {
        if (dataSet.entryCount < 1) return

        val trans = chart.getTransformer(dataSet.axisDependency)

        val phaseY = animator.phaseY

        xBounds[chart] = dataSet

        sizeBuffer[0] = 0f
        sizeBuffer[2] = 1f

        trans!!.pointValuesToPixel(sizeBuffer)

        val normalizeSize = dataSet.isNormalizeSizeEnabled

        // calculate the full width of 1 step on the x-axis
        val maxBubbleWidth = abs((sizeBuffer[2] - sizeBuffer[0]).toDouble()).toFloat()
        val maxBubbleHeight = abs((viewPortHandler.contentBottom() - viewPortHandler.contentTop()).toDouble()).toFloat()
        val referenceSize = min(maxBubbleHeight.toDouble(), maxBubbleWidth.toDouble()).toFloat()

        for (j in xBounds.min..xBounds.range + xBounds.min) {
            val entry = dataSet.getEntryForIndex(j)

            pointBuffer[0] = entry.x
            pointBuffer[1] = (entry.y) * phaseY
            trans.pointValuesToPixel(pointBuffer)

            val shapeHalf = getShapeSize(entry.size, dataSet.maxSize, referenceSize, normalizeSize) / 2f

            if (!viewPortHandler.isInBoundsTop(pointBuffer[1] + shapeHalf)
                || !viewPortHandler.isInBoundsBottom(pointBuffer[1] - shapeHalf)
            ) continue

            if (!viewPortHandler.isInBoundsLeft(pointBuffer[0] + shapeHalf)) continue

            if (!viewPortHandler.isInBoundsRight(pointBuffer[0] - shapeHalf)) break

            val color = dataSet.getColor(j)

            renderPaint.color = color
            c.drawCircle(pointBuffer[0], pointBuffer[1], shapeHalf, renderPaint)
        }
    }

    override fun drawValues(c: Canvas) {
        val bubbleData = chart.bubbleData ?: return

        // if values are drawn
        if (isDrawingValuesAllowed(chart)) {
            val dataSets = bubbleData.dataSets

            val lineHeight = Utils.calcTextHeight(mValuePaint, "1").toFloat()

            for (i in dataSets.indices) {
                val dataSet = dataSets[i]
                if (dataSet.entryCount == 0) {
                    continue
                }
                if (!shouldDrawValues(dataSet) || dataSet.entryCount < 1) {
                    continue
                }

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet)

                val phaseX = max(0.0, min(1.0, animator.phaseX.toDouble())).toFloat()
                val phaseY = animator.phaseY

                xBounds[chart] = dataSet

                chart.getTransformer(dataSet.axisDependency)?.let { transformer ->
                    val positions = transformer.generateTransformedValuesBubble(dataSet, phaseY, xBounds.min, xBounds.max)

                    val alpha = if (phaseX == 1f)
                        phaseY
                    else
                        phaseX

                    val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
                    iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
                    iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)

                    var j = 0
                    while (j < positions.size) {
                        var valueTextColor = dataSet.getValueTextColor(j / 2 + xBounds.min)
                        valueTextColor = Color.argb(
                            Math.round(255f * alpha), Color.red(valueTextColor),
                            Color.green(valueTextColor), Color.blue(valueTextColor)
                        )

                        val x = positions[j]
                        val y = positions[j + 1]

                        if (!viewPortHandler.isInBoundsRight(x)) break

                        if ((!viewPortHandler.isInBoundsLeft(x) || !viewPortHandler.isInBoundsY(y))) {
                            j += 2
                            continue
                        }

                        val entry = dataSet.getEntryForIndex(j / 2 + xBounds.min)

                        if (dataSet.isDrawValuesEnabled) {
                            drawValue(
                                c, dataSet.valueFormatter, entry.size, entry, i, x,
                                y + (0.5f * lineHeight), valueTextColor
                            )
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
                        j += 2
                    }

                    MPPointF.recycleInstance(iconsOffset)
                }
            }
        }
    }

    override fun drawExtras(c: Canvas) {
    }

    private val _hsvBuffer = FloatArray(3)

    init {
        renderPaint.style = Paint.Style.FILL

        highlightPaint.style = Paint.Style.STROKE
        highlightPaint.strokeWidth = Utils.convertDpToPixel(1.5f)
    }

    override fun drawHighlighted(c: Canvas, indices: Array<Highlight>) {
        val bubbleData = chart.bubbleData

        val phaseY = animator.phaseY

        for (high in indices) {
            val set = bubbleData.getDataSetByIndex(high.dataSetIndex)

            if (set == null || !set.isHighlightEnabled) continue

            val entry = set.getEntryForXValue(high.x, high.y)

            if (entry.y != high.y) continue

            if (!isInBoundsX(entry, set)) continue

            val trans = chart.getTransformer(set.axisDependency)

            sizeBuffer[0] = 0f
            sizeBuffer[2] = 1f

            trans!!.pointValuesToPixel(sizeBuffer)

            val normalizeSize = set.isNormalizeSizeEnabled

            // calculate the full width of 1 step on the x-axis
            val maxBubbleWidth = abs((sizeBuffer[2] - sizeBuffer[0]).toDouble()).toFloat()
            val maxBubbleHeight = abs(
                (viewPortHandler.contentBottom() - viewPortHandler.contentTop()).toDouble()
            ).toFloat()
            val referenceSize = min(maxBubbleHeight.toDouble(), maxBubbleWidth.toDouble()).toFloat()

            pointBuffer[0] = entry.x
            pointBuffer[1] = (entry.y) * phaseY
            trans.pointValuesToPixel(pointBuffer)

            high.setDraw(pointBuffer[0], pointBuffer[1])

            val shapeHalf = getShapeSize(
                entry.size,
                set.maxSize,
                referenceSize,
                normalizeSize
            ) / 2f

            if (!viewPortHandler.isInBoundsTop(pointBuffer[1] + shapeHalf)
                || !viewPortHandler.isInBoundsBottom(pointBuffer[1] - shapeHalf)
            ) continue

            if (!viewPortHandler.isInBoundsLeft(pointBuffer[0] + shapeHalf)) continue

            if (!viewPortHandler.isInBoundsRight(pointBuffer[0] - shapeHalf)) break

            val originalColor = set.getColor(entry.x.toInt())

            Color.RGBToHSV(
                Color.red(originalColor), Color.green(originalColor),
                Color.blue(originalColor), _hsvBuffer
            )
            _hsvBuffer[2] *= 0.5f
            val color = Color.HSVToColor(Color.alpha(originalColor), _hsvBuffer)

            highlightPaint.color = color
            highlightPaint.strokeWidth = set.highlightCircleWidth
            c.drawCircle(pointBuffer[0], pointBuffer[1], shapeHalf, highlightPaint)
        }
    }
}
