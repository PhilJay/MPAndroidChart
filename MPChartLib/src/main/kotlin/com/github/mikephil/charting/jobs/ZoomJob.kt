package com.github.mikephil.charting.jobs

import android.graphics.Matrix
import android.view.View
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.utils.ObjectPool
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Zooms the viewport by the factors [scaleX] and [scaleY] relative to the current zoom and then moves it so
 * that the value point ([xValue], [yValue]) sits in the center of the content rectangle, without animation.
 * Runs once, recalculates the chart offsets, redraws and returns itself to the pool.
 *
 * @property scaleX Zoom factor along x, relative to the current zoom.
 * @property scaleY Zoom factor along y, relative to the current zoom.
 * @property axisDependency The y axis whose range decides how many y values fit into the view.
 */
class ZoomJob(
    viewPortHandler: ViewPortHandler?,
    protected var scaleX: Float,
    protected var scaleY: Float,
    xValue: Float,
    yValue: Float,
    transformer: Transformer?,
    protected var axisDependency: YAxis.AxisDependency?,
    view: View?
) : ViewPortJob(viewPortHandler, xValue, yValue, transformer, view) {

    private val runMatrixBuffer = Matrix()

    /**
     * Applies the zoom and translation and refreshes the chart, then recycles this job. Does nothing unless the
     * view is a [BarLineChartBase] and all pooled fields are set.
     */
    override fun run() {
        val viewPortHandler = viewPortHandler ?: return
        val transformer = transformer ?: return
        val view = view as? BarLineChartBase<*> ?: return
        val axisDependency = axisDependency ?: return

        val save = runMatrixBuffer
        viewPortHandler.zoom(scaleX, scaleY, save)
        viewPortHandler.refresh(save, view, false)

        val stackValuesInView = view.getAxis(axisDependency).axisRange / viewPortHandler.scaleY
        val xValsInView = view.xAxis.axisRange / viewPortHandler.scaleX

        pts[0] = xValue - xValsInView / 2f
        pts[1] = yValue + stackValuesInView / 2f

        transformer.pointValuesToPixel(pts)

        viewPortHandler.translate(pts, save)
        viewPortHandler.refresh(save, view, false)

        view.calculateOffsets()
        view.postInvalidate()

        recycleInstance(this)
    }

    override fun instantiate(): ObjectPool.Poolable = ZoomJob(null, 0f, 0f, 0f, 0f, null, null, null)

    companion object {

        private val pool: ObjectPool<ZoomJob> = ObjectPool.create(1, ZoomJob(null, 0f, 0f, 0f, 0f, null, null, null)).apply {
            replenishPercentage = 0.5f
        }

        /**
         * Returns a pooled job set up to zoom the viewport of [view].
         *
         * @param xValue x value that ends up in the center of the content rectangle
         * @param yValue y value that ends up in the center of the content rectangle
         * @param transformer transformer of [axis]
         * @param axis the y axis that [yValue] belongs to
         */
        fun getInstance(
            viewPortHandler: ViewPortHandler,
            scaleX: Float,
            scaleY: Float,
            xValue: Float,
            yValue: Float,
            transformer: Transformer,
            axis: YAxis.AxisDependency,
            view: View
        ): ZoomJob {
            val result = pool.get()
            result.xValue = xValue
            result.yValue = yValue
            result.scaleX = scaleX
            result.scaleY = scaleY
            result.viewPortHandler = viewPortHandler
            result.transformer = transformer
            result.axisDependency = axis
            result.view = view
            return result
        }

        /**
         * Returns [instance] to the pool. Jobs recycle themselves after running, so this is only needed for jobs that
         * were never run.
         *
         * @throws IllegalArgumentException when [instance] is already in the pool
         */
        fun recycleInstance(instance: ZoomJob) {
            pool.recycle(instance)
        }
    }
}
