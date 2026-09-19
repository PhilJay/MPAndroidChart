package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BubbleDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Draws the bubbles of a [BubbleDataProvider]. Each entry is a filled circle at its x and y value whose
 * diameter follows the entry size relative to the largest size of its data set. A highlight redraws the outline
 * of the bubble in a darker shade of its color.
 *
 * @property chart The chart that supplies the bubble data and the transformers.
 */
open class BubbleChartRenderer(
    protected val chart: BubbleDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarLineScatterCandleBubbleRenderer(animator, viewPortHandler) {

    private val sizeBuffer = FloatArray(4)
    private val pointBuffer = FloatArray(2)
    private val hsvBuffer = FloatArray(3)

    private var maxBubbleSize = 0f
    private var bubbleReference = 0f
    private var normalizeBubbleSize = true
    private var valuesPerPixelY = 0f

    init {
        renderPaint.style = Paint.Style.FILL

        highlightPaint.style = Paint.Style.STROKE
        highlightPaint.strokeWidth = Utils.convertDpToPixel(1.5f)
    }

    /**
     * Does nothing; bubbles are drawn without a buffer.
     */
    override fun initBuffers() {
    }

    override fun drawData(c: Canvas) {
        val bubbleData = chart.bubbleData ?: return

        for (set in bubbleData.dataSets) {
            if (set.isVisible) drawDataSet(c, set)
        }
    }

    /**
     * Returns the diameter in pixels of a bubble with the given size.
     *
     * @param entrySize size value of the entry
     * @param maxSize largest size value in the data set
     * @param reference pixel diameter of the largest bubble: the smaller of one x unit and the content height
     * @param normalizeSize true scales [reference] by the square root of `entrySize / maxSize`, false multiplies it
     * by [entrySize] directly
     */
    protected fun getShapeSize(entrySize: Float, maxSize: Float, reference: Float, normalizeSize: Boolean): Float {
        val factor = if (normalizeSize) {
            if (maxSize == 0f) 1f else sqrt(entrySize / maxSize)
        } else {
            entrySize
        }
        return reference * factor
    }

    /**
     * Half the height the bubble of [entry] paints, in y value units, as of the last [measureBubbles]. A bubble
     * stands for the whole disc it covers, so the reduction keeps the ones that reach furthest rather than the
     * ones whose center sits furthest out.
     */
    private fun halfHeightOf(entry: Entry<*>): Float {
        val size = (entry as? BubbleEntry<*>)?.size ?: return 0f
        return getShapeSize(size, maxBubbleSize, bubbleReference, normalizeBubbleSize) / 2f * valuesPerPixelY
    }

    override fun lowOf(entry: Entry<*>) = entry.y - halfHeightOf(entry)

    override fun highOf(entry: Entry<*>) = entry.y + halfHeightOf(entry)

    override val hasSizes: Boolean
        get() = true

    override fun sizeOf(entry: Entry<*>) = (entry as? BubbleEntry<*>)?.size ?: 0f

    /**
     * Remembers how [dataSet] scales its bubbles in the current viewport, so that [lowOf] and [highOf] can say how
     * much room one bubble takes. Needed before every reduction of a bubble data set.
     */
    private fun measureBubbles(dataSet: IBubbleDataSet<*>, trans: Transformer) {
        sizeBuffer[0] = 0f
        sizeBuffer[1] = 0f
        sizeBuffer[2] = 1f
        sizeBuffer[3] = 1f

        trans.pointValuesToPixel(sizeBuffer)

        val maxBubbleWidth = abs(sizeBuffer[2] - sizeBuffer[0])
        val maxBubbleHeight = abs(viewPortHandler.contentBottom - viewPortHandler.contentTop)
        val pixelsPerValueY = abs(sizeBuffer[3] - sizeBuffer[1])

        maxBubbleSize = dataSet.maxSize
        normalizeBubbleSize = dataSet.isNormalizeSizeEnabled
        bubbleReference = min(maxBubbleHeight, maxBubbleWidth)
        valuesPerPixelY = if (pixelsPerValueY > 0f) 1f / pixelsPerValueY else 0f
    }

    /**
     * Draws every bubble of [dataSet] inside the visible range that the reduction kept, with the color of its
     * entry.
     */
    protected fun drawDataSet(c: Canvas, dataSet: IBubbleDataSet<*>) {
        if (dataSet.entryCount < 1) return

        val trans = chart.getTransformer(dataSet.axisDependency)

        val phaseY = animator.phaseY

        xBounds.set(chart, dataSet)
        measureBubbles(dataSet, trans)
        reduceVisible(chart, dataSet)

        for (k in 0 until reducer.count) {
            val index = reducer.indices[k]
            val entry = dataSet.getEntryForIndex(index)

            pointBuffer[0] = entry.x
            pointBuffer[1] = entry.y * phaseY
            trans.pointValuesToPixel(pointBuffer)

            val shapeHalf = getShapeSize(entry.size, maxBubbleSize, bubbleReference, normalizeBubbleSize) / 2f

            if (!viewPortHandler.isInBoundsTop(pointBuffer[1] + shapeHalf)
                || !viewPortHandler.isInBoundsBottom(pointBuffer[1] - shapeHalf)
            ) continue

            if (!viewPortHandler.isInBoundsLeft(pointBuffer[0] + shapeHalf)) continue

            if (!viewPortHandler.isInBoundsRight(pointBuffer[0] - shapeHalf)) break

            renderPaint.color = dataSet.getColor(index)
            c.drawCircle(pointBuffer[0], pointBuffer[1], shapeHalf, renderPaint)
        }
    }

    /**
     * Draws the size value of each bubble that was drawn centered on it, fading the text in with the animation.
     */
    override fun drawValues(c: Canvas) {
        val bubbleData = chart.bubbleData ?: return

        val dataSets = bubbleData.dataSets

        val lineHeight = Utils.calcTextHeight(valuePaint, "1").toFloat()

        for (i in dataSets.indices) {
            val dataSet = dataSets[i]

            if (!shouldDrawValues(chart, dataSet) || dataSet.entryCount < 1) continue

            applyValueTextStyle(dataSet)

            val phaseX = max(0f, min(1f, animator.phaseX))
            val phaseY = animator.phaseY

            val trans = chart.getTransformer(dataSet.axisDependency)

            xBounds.set(chart, dataSet)
            measureBubbles(dataSet, trans)
            reduceVisible(chart, dataSet)

            val alpha = if (phaseX == 1f) phaseY else phaseX

            val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)

            for (k in 0 until reducer.count) {
                val index = reducer.indices[k]

                var valueTextColor = dataSet.getValueTextColor(index)
                valueTextColor = Color.argb(
                    (255f * alpha).roundToInt(), Color.red(valueTextColor),
                    Color.green(valueTextColor), Color.blue(valueTextColor)
                )

                val entry = dataSet.getEntryForIndex(index)

                pointBuffer[0] = entry.x
                pointBuffer[1] = entry.y * phaseY
                trans.pointValuesToPixel(pointBuffer)

                val x = pointBuffer[0]
                val y = pointBuffer[1]

                if (!viewPortHandler.isInBoundsRight(x)) break

                if (!viewPortHandler.isInBoundsLeft(x) || !viewPortHandler.isInBoundsY(y)) continue

                if (dataSet.isDrawValuesEnabled) {
                    drawValue(
                        c, dataSet.valueFormatter, entry.size, entry, i, x,
                        y + (0.5f * lineHeight), valueTextColor
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
     * Does nothing; bubble charts have no extras.
     */
    override fun drawExtras(c: Canvas) {
    }

    override fun drawHighlighted(c: Canvas, indices: List<Highlight>) {
        val bubbleData = chart.bubbleData ?: return

        val phaseY = animator.phaseY

        for (high in indices) {
            val set = bubbleData.getDataSetByIndex(high.dataSetIndex)

            if (set == null || !set.isHighlightEnabled) continue

            val entry = set.getEntryForXValue(high.x, high.y, DataSet.Rounding.CLOSEST) ?: continue

            if (entry.y != high.y) continue

            if (!isInBoundsX(entry, set)) continue

            val trans = chart.getTransformer(set.axisDependency)

            sizeBuffer[0] = 0f
            sizeBuffer[2] = 1f

            trans.pointValuesToPixel(sizeBuffer)

            val normalizeSize = set.isNormalizeSizeEnabled

            val maxBubbleWidth = abs(sizeBuffer[2] - sizeBuffer[0])
            val maxBubbleHeight = abs(viewPortHandler.contentBottom - viewPortHandler.contentTop)
            val referenceSize = min(maxBubbleHeight, maxBubbleWidth)

            pointBuffer[0] = entry.x
            pointBuffer[1] = entry.y * phaseY
            trans.pointValuesToPixel(pointBuffer)

            high.setDraw(pointBuffer[0], pointBuffer[1])

            val shapeHalf = getShapeSize(entry.size, set.maxSize, referenceSize, normalizeSize) / 2f

            if (!viewPortHandler.isInBoundsTop(pointBuffer[1] + shapeHalf)
                || !viewPortHandler.isInBoundsBottom(pointBuffer[1] - shapeHalf)
            ) continue

            if (!viewPortHandler.isInBoundsLeft(pointBuffer[0] + shapeHalf)) continue

            if (!viewPortHandler.isInBoundsRight(pointBuffer[0] - shapeHalf)) break

            val originalColor = set.getColor(entry.x.toInt())

            Color.RGBToHSV(
                Color.red(originalColor), Color.green(originalColor),
                Color.blue(originalColor), hsvBuffer
            )
            hsvBuffer[2] *= 0.5f
            val color = Color.HSVToColor(Color.alpha(originalColor), hsvBuffer)

            highlightPaint.color = color
            highlightPaint.strokeWidth = Utils.convertDpToPixel(set.highlightCircleWidth)
            c.drawCircle(pointBuffer[0], pointBuffer[1], shapeHalf, highlightPaint)
        }
    }
}
