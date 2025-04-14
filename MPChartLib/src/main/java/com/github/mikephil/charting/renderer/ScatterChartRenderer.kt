package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.util.Log
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.ScatterDataProvider
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.ceil
import kotlin.math.min

open class ScatterChartRenderer(@JvmField var chart: ScatterDataProvider, animator: ChartAnimator?, viewPortHandler: ViewPortHandler?) :
    LineScatterCandleRadarRenderer(animator, viewPortHandler) {
    override fun initBuffers() {
    }

    override fun drawData(c: Canvas) {
        val scatterData = chart.scatterData

        for (set in scatterData.dataSets) {
            if (set.isVisible) drawDataSet(c, set)
        }
    }

    var pixelBuffer: FloatArray = FloatArray(2)

    protected fun drawDataSet(c: Canvas?, dataSet: IScatterDataSet) {
        if (dataSet.entryCount < 1) return

        val viewPortHandler = this.viewPortHandler

        val trans = chart.getTransformer(dataSet.axisDependency)

        val phaseY = animator.phaseY

        val renderer = dataSet.shapeRenderer
        if (renderer == null) {
            Log.i("MISSING", "There's no IShapeRenderer specified for ScatterDataSet")
            return
        }

        val max = (min(
            ceil((dataSet.entryCount.toFloat() * animator.phaseX).toDouble()),
            dataSet.entryCount.toFloat().toDouble()
        )).toInt()

        for (i in 0..<max) {
            val e = dataSet.getEntryForIndex(i)

            pixelBuffer[0] = e.x
            pixelBuffer[1] = e.y * phaseY

            trans!!.pointValuesToPixel(pixelBuffer)

            if (!viewPortHandler.isInBoundsRight(pixelBuffer[0])) break

            if (!viewPortHandler.isInBoundsLeft(pixelBuffer[0])
                || !viewPortHandler.isInBoundsY(pixelBuffer[1])
            ) continue

            renderPaint.color = dataSet.getColor(i / 2)
            renderer.renderShape(
                c, dataSet, this.viewPortHandler,
                pixelBuffer[0], pixelBuffer[1],
                renderPaint
            )
        }
    }

    override fun drawValues(c: Canvas) {
        // if values are drawn

        if (isDrawingValuesAllowed(chart)) {
            val dataSets = chart.scatterData.dataSets

            for (i in 0..<chart.scatterData.dataSetCount) {
                val dataSet = dataSets[i]

                if (dataSet.entryCount == 0) {
                    continue
                }
                if (!shouldDrawValues(dataSet) || dataSet.entryCount < 1) {
                    continue
                }

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet)

                xBounds[chart] = dataSet

                val positions = chart.getTransformer(dataSet.axisDependency)!!.generateTransformedValuesScatter(
                    dataSet,
                    animator.phaseX, animator.phaseY, xBounds.min, xBounds.max
                )

                val shapeSize = Utils.convertDpToPixel(dataSet.scatterShapeSize)

                val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
                iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
                iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)

                var j = 0
                while (j < positions.size) {
                    if (!viewPortHandler.isInBoundsRight(positions[j])) break

                    // make sure the lines don't do shitty things outside bounds
                    if ((!viewPortHandler.isInBoundsLeft(positions[j])
                                || !viewPortHandler.isInBoundsY(positions[j + 1]))
                    ) {
                        j += 2
                        continue
                    }

                    val entry = dataSet.getEntryForIndex(j / 2 + xBounds.min)

                    if (dataSet.isDrawValuesEnabled) {
                        drawValue(
                            c,
                            dataSet.valueFormatter,
                            entry.y,
                            entry,
                            i,
                            positions[j],
                            positions[j + 1] - shapeSize,
                            dataSet.getValueTextColor(j / 2 + xBounds.min)
                        )
                    }

                    if (entry.icon != null && dataSet.isDrawIconsEnabled) {
                        val icon = entry.icon

                        Utils.drawImage(
                            c,
                            icon,
                            (positions[j] + iconsOffset.x).toInt(),
                            (positions[j + 1] + iconsOffset.y).toInt(),
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

    override fun drawExtras(c: Canvas) {
    }

    override fun drawHighlighted(c: Canvas, indices: Array<Highlight>) {
        val scatterData = chart.scatterData

        for (high in indices) {
            val set = scatterData.getDataSetByIndex(high.dataSetIndex)

            if (set == null || !set.isHighlightEnabled) continue

            val e = set.getEntryForXValue(high.x, high.y)

            if (!isInBoundsX(e, set)) continue

            val pix = chart.getTransformer(set.axisDependency)!!.getPixelForValues(
                e.x, e.y * animator
                    .phaseY
            )

            high.setDraw(pix.x.toFloat(), pix.y.toFloat())

            // draw the lines
            drawHighlightLines(c, pix.x.toFloat(), pix.y.toFloat(), set)
        }
    }
}
