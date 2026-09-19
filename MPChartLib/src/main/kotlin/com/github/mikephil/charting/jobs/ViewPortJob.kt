package com.github.mikephil.charting.jobs

import android.view.View
import com.github.mikephil.charting.utils.ObjectPool
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Base class of the runnables that move or zoom the viewport of a chart to a position given in value space.
 *
 * Jobs are pooled: obtain one with the `getInstance` of a subclass instead of constructing it. Pass it to
 * `chart.addViewportJob`, which runs it right away when the chart has been laid out and otherwise after the
 * first layout, so it can be created before the chart has a size.
 *
 * @property viewPortHandler Viewport of the chart to change.
 * @property transformer Converts [xValue] and [yValue] to pixels.
 * @property view The chart view to refresh.
 */
abstract class ViewPortJob(
    protected var viewPortHandler: ViewPortHandler?,
    xValue: Float,
    yValue: Float,
    protected var transformer: Transformer?,
    protected var view: View?
) : ObjectPool.Poolable(), Runnable {

    /**
     * Buffer for the target point, filled in value space and converted to pixels in place.
     */
    protected val pts = FloatArray(2)

    /**
     * Target x value of the job.
     */
    var xValue = xValue
        protected set

    /**
     * Target y value of the job.
     */
    var yValue = yValue
        protected set
}
