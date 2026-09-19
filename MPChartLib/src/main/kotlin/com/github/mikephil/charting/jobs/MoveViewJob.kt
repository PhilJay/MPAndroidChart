package com.github.mikephil.charting.jobs

import android.view.View
import com.github.mikephil.charting.utils.ObjectPool
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Moves the viewport so that the value point ([xValue], [yValue]) lands at the top left corner of the content
 * rectangle, without animation. Runs once, redraws the chart and returns itself to the pool.
 */
class MoveViewJob(
    viewPortHandler: ViewPortHandler?,
    xValue: Float,
    yValue: Float,
    transformer: Transformer?,
    view: View?
) : ViewPortJob(viewPortHandler, xValue, yValue, transformer, view) {

    /**
     * Translates the viewport and refreshes the chart, then recycles this job. Does nothing when the pooled fields
     * have not been set.
     */
    override fun run() {
        val transformer = transformer ?: return
        val viewPortHandler = viewPortHandler ?: return
        val view = view ?: return

        pts[0] = xValue
        pts[1] = yValue

        transformer.pointValuesToPixel(pts)
        viewPortHandler.centerViewPort(pts, view)

        recycleInstance(this)
    }

    override fun instantiate(): ObjectPool.Poolable = MoveViewJob(viewPortHandler, xValue, yValue, transformer, view)

    companion object {

        private val pool: ObjectPool<MoveViewJob> = ObjectPool.create(2, MoveViewJob(null, 0f, 0f, null, null)).apply {
            replenishPercentage = 0.5f
        }

        /**
         * Returns a pooled job set up to move the viewport of [view].
         *
         * @param xValue x value that lands at the left edge of the content rectangle
         * @param yValue y value that lands at the top edge of the content rectangle
         * @param transformer transformer of the axis that [yValue] belongs to
         */
        fun getInstance(viewPortHandler: ViewPortHandler, xValue: Float, yValue: Float, transformer: Transformer, view: View): MoveViewJob {
            val result = pool.get()
            result.viewPortHandler = viewPortHandler
            result.xValue = xValue
            result.yValue = yValue
            result.transformer = transformer
            result.view = view
            return result
        }

        /**
         * Returns [instance] to the pool. Jobs recycle themselves after running, so this is only needed for jobs that
         * were never run.
         *
         * @throws IllegalArgumentException when [instance] is already in the pool
         */
        fun recycleInstance(instance: MoveViewJob) {
            pool.recycle(instance)
        }
    }
}
