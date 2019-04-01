package com.xxmassdeveloper.mpchartexample.custom.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.ColorBubbleEntry
import com.github.mikephil.charting.interfaces.dataprovider.BubbleDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet
import com.github.mikephil.charting.renderer.BubbleChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import com.xxmassdeveloper.mpchartexample.custom.data.City
import com.xxmassdeveloper.mpchartexample.custom.utils.Extensions.Companion.almostEquals

class CityRenderer(chart: BubbleDataProvider, animator: ChartAnimator,
                   viewPortHandler: ViewPortHandler) : BubbleChartRenderer(chart, animator, viewPortHandler) {
    var logEnabled = true

    var maxRadiusPix = 30f
    var minRadiusPix = 10f

    var maxPopulation = 0
    var minPopulation = 0

    var boundary: MutableList<PointF> = mutableListOf()
        private set

    // bounds - left, top, right, bottom
    var bounds = RectF()
        private set

    /**
     * Sets the boundary of the region to draw. Sets mBounds.
     * @param boundary list of PointF's
     */
    fun setBoundary(boundary: List<PointF>) : RectF {
        resetBounds()
        boundary.map { p ->
            if (p.x < bounds.left) bounds.left = p.x
            if (p.x > bounds.right) bounds.right = p.x
            if (p.y > bounds.top) bounds.top = p.y
            if (p.y < bounds.bottom) bounds.bottom = p.y
            this.boundary.add(p)
        }
        return bounds
    }

    override fun drawData(c: Canvas) {

        val bubbleData = mChart.bubbleData

        for (set in bubbleData.dataSets) {

            if (set.isVisible)
                drawDataSet(c, set)
        }
    }

    override fun drawDataSet(c: Canvas, dataSet: IBubbleDataSet) {

        if (dataSet.entryCount < 1)
            return

        val pointBuffer = FloatArray(2)

        val trans = mChart.getTransformer(dataSet.axisDependency)

        val phaseY = mAnimator.phaseY

        mXBounds.set(mChart, dataSet)

        val scale = (maxRadiusPix - minRadiusPix) / (Math.sqrt(maxPopulation.toDouble()) - Math.sqrt(minPopulation.toDouble())).toFloat()

        for (j in mXBounds.min..mXBounds.range + mXBounds.min) {

            val entry = dataSet.getEntryForIndex(j)

            pointBuffer[0] = entry.x
            pointBuffer[1] = entry.y * phaseY
            trans.pointValuesToPixel(pointBuffer)

            // draw log population scaled to min/max range
            val population = entry.size
            val radius = Math.sqrt(population.toDouble()).toFloat() * scale + minRadiusPix

            if (!mViewPortHandler.isInBoundsTop(pointBuffer[1] + radius) || !mViewPortHandler.isInBoundsBottom(pointBuffer[1] - radius))
                continue

            if (!mViewPortHandler.isInBoundsLeft(pointBuffer[0] + radius))
                continue

            if (!mViewPortHandler.isInBoundsRight(pointBuffer[0] - radius))
                break

            val color: Int
            if (entry is ColorBubbleEntry)
                color = entry.color
            else
                color = dataSet.getColor(entry.x.toInt())

            mRenderPaint.color = color
            if (logEnabled) {
                val city = entry.data as City
                Log.i("CityRenderer",
                        String.format("%s size= %d -> %.1f, radius = %.1f",
                                city.name, city.population, entry.size, radius))
            }
            Log.i("drawCity", String.format("%.1f, %.1f -> %.1f, %.1f", entry.x, entry.y, pointBuffer[0], pointBuffer[1]))
            c.drawCircle(pointBuffer[0], pointBuffer[1], radius, mRenderPaint)

            // translate the radius to data values and set the bubble radius fields
            pointBuffer[0] += radius  // right edge
            pointBuffer[1] += radius  // top edge
            trans.pointValuesToPixel(pointBuffer)
            val xRadius = pointBuffer[0].toFloat() - entry.x
            val yRadius = entry.y - pointBuffer[1].toFloat()
            entry.setDrawnRadii(xRadius, yRadius)
        }
    }

    /**
     * Draws the boundary of the map, previously set in mBoundary as a list of PointF in pixels
     * @param c canvas
     */
    override fun drawExtras(c: Canvas) {
        super.drawExtras(c)
        val pointBuffer = FloatArray(4)
        val trans = mChart.getTransformer(YAxis.AxisDependency.LEFT)
        val phaseY = mAnimator.phaseY
        mRenderPaint.color = Color.BLACK

        var first = boundary[0]
        var i = 0
        while (i < boundary.size - 1) {
            val from = boundary[i]
            val to = boundary[i + 1]

            // end a shape when we get back to the starting point
            if (first.almostEquals(from) && i > 0) {  // closed a shape - skip to the next shape
                first = to
                ++i
                ++i
                continue
            }
            pointBuffer[0] = from.x
            pointBuffer[1] = from.y * phaseY
            pointBuffer[2] = to.x
            pointBuffer[3] = to.y * phaseY
            trans.pointValuesToPixel(pointBuffer)
            c.drawLine(pointBuffer[0], pointBuffer[1], pointBuffer[2], pointBuffer[3], mRenderPaint)
            ++i
        }
    }

    private fun distance(a: PointF, b: PointF): Float {
        return Math.sqrt(((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y)).toDouble()).toFloat()
    }

    private fun resetBounds() {
        bounds.left = Float.POSITIVE_INFINITY
        bounds.top = Float.NEGATIVE_INFINITY
        bounds.right = Float.NEGATIVE_INFINITY
        bounds.bottom = Float.POSITIVE_INFINITY
    }
}
