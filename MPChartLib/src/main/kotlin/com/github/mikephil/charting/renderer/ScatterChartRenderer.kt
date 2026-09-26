package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.ScatterDataProvider
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Draws the points of a [ScatterDataProvider]. Each entry is drawn at its pixel position by the shape renderer
 * of its data set; a highlight is shown with the standard highlight lines.
 *
 * @property chart The chart that supplies the scatter data and the transformers.
 */
public open class ScatterChartRenderer(protected val chart: ScatterDataProvider, animator: ChartAnimator, viewPortHandler: ViewPortHandler) : LineScatterCandleRadarRenderer(animator, viewPortHandler) {

    private val pixelBuffer = FloatArray(2)

    /**
     * Does nothing; scatter shapes are drawn without a buffer.
     */
    override fun initBuffers() {
    }

    override fun drawData(c: Canvas) {
        val scatterData = chart.scatterData ?: return

        for (set in scatterData.dataSets) {
            if (set.isVisible) drawDataSet(c, set)
        }
    }

    /**
     * Draws every entry of [dataSet] that is visible in the content rectangle with the data set's shape renderer,
     * stopping at the entry count revealed by the x animation phase.
     */
    protected open fun drawDataSet(c: Canvas, dataSet: IScatterDataSet<*>) {
        if (dataSet.entryCount < 1) return

        val trans = chart.getTransformer(dataSet.axisDependency)

        xBounds.set(chart, dataSet)
        reduceVisible(chart, dataSet)

        val phaseY = animator.phaseY

        val renderer = dataSet.shapeRenderer

        for (k in 0 until reducer.count) {
            val index = reducer.indices[k]
            val e = dataSet.getEntryForIndex(index)

            pixelBuffer[0] = e.x
            pixelBuffer[1] = e.y * phaseY

            trans.pointValuesToPixel(pixelBuffer)

            if (!viewPortHandler.isInBoundsRight(pixelBuffer[0])) break

            if (!viewPortHandler.isInBoundsLeft(pixelBuffer[0]) || !viewPortHandler.isInBoundsY(pixelBuffer[1])) continue

            renderPaint.color = dataSet.getColor(index)
            renderer.renderShape(c, dataSet, viewPortHandler, pixelBuffer[0], pixelBuffer[1], renderPaint)
        }
    }

    /**
     * Draws the y value of each entry that was drawn one shape size above it.
     */
    override fun drawValues(c: Canvas) {
        val scatterData = chart.scatterData ?: return
        val dataSets = scatterData.dataSets

        for (i in 0 until scatterData.dataSetCount) {
            val dataSet = dataSets[i]

            if (!shouldDrawValues(chart, dataSet) || dataSet.entryCount < 1) continue

            applyValueTextStyle(dataSet)

            xBounds.set(chart, dataSet)
            reduceVisible(chart, dataSet)

            val positions = reducedPositions(dataSet, chart.getTransformer(dataSet.axisDependency), animator.phaseY)

            val shapeSize = Utils.convertDpToPixel(dataSet.scatterShapeSize)

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
                    drawValue(c, dataSet.valueFormatter, entry.y, entry, i, x, y - shapeSize, dataSet.getValueTextColor(index))
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
     * Does nothing; scatter charts have no extras.
     */
    override fun drawExtras(c: Canvas) {
    }

    override fun drawHighlighted(c: Canvas, indices: List<Highlight>) {
        val scatterData = chart.scatterData ?: return

        for (high in indices) {
            val set = scatterData.getDataSetByIndex(high.dataSetIndex)

            if (set == null || !set.isHighlightEnabled) continue

            val e = set.getEntryForXValue(high.x, high.y, DataSet.Rounding.CLOSEST) ?: continue

            if (!isInBoundsX(e, set)) continue

            val pix = chart.getTransformer(set.axisDependency).getPixelForValues(e.x, e.y * animator.phaseY)

            high.setDraw(pix.x.toFloat(), pix.y.toFloat())

            drawHighlightLines(c, pix.x.toFloat(), pix.y.toFloat(), set)
        }
    }
}
