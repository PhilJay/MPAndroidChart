package com.github.mikephil.charting.jobs

import android.animation.ValueAnimator
import android.view.View
import com.github.mikephil.charting.utils.ObjectPool
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Animates the viewport from the value point ([xOrigin], [yOrigin]) to ([xValue], [yValue]). On every frame the
 * interpolated point is placed at the top left corner of the content rectangle.
 */
public class AnimatedMoveViewJob(
    viewPortHandler: ViewPortHandler?,
    xValue: Float,
    yValue: Float,
    transformer: Transformer?,
    view: View?,
    xOrigin: Float,
    yOrigin: Float,
    duration: Long
) : AnimatedViewPortJob(viewPortHandler, xValue, yValue, transformer, view, xOrigin, yOrigin, duration) {

    override fun onAnimationUpdate(animation: ValueAnimator) {
        val transformer = transformer ?: return
        val viewPortHandler = viewPortHandler ?: return
        val view = view ?: return

        pts[0] = xOrigin + (xValue - xOrigin) * phase
        pts[1] = yOrigin + (yValue - yOrigin) * phase

        transformer.pointValuesToPixel(pts)
        viewPortHandler.centerViewPort(pts, view)
    }

    override fun recycleSelf() {
        releaseChart()
        recycleInstance(this)
    }

    override fun instantiate(): ObjectPool.Poolable = AnimatedMoveViewJob(null, 0f, 0f, null, null, 0f, 0f, 0)

    public companion object {

        private val pool: ObjectPool<AnimatedMoveViewJob> = ObjectPool.create(4, AnimatedMoveViewJob(null, 0f, 0f, null, null, 0f, 0f, 0)).apply {
            replenishPercentage = 0.5f
        }

        /**
         * Returns a pooled job set up to animate the viewport of [view].
         *
         * @param xValue x value that lands at the left edge of the content rectangle when the animation ends
         * @param yValue y value that lands at the top edge of the content rectangle when the animation ends
         * @param transformer transformer of the axis that [yValue] belongs to
         * @param xOrigin x value at the left edge when the animation starts
         * @param yOrigin y value at the top edge when the animation starts
         * @param duration length of the animation in milliseconds
         */
        public fun getInstance(
            viewPortHandler: ViewPortHandler,
            xValue: Float,
            yValue: Float,
            transformer: Transformer,
            view: View,
            xOrigin: Float,
            yOrigin: Float,
            duration: Long
        ): AnimatedMoveViewJob {
            val result = pool.get()
            result.viewPortHandler = viewPortHandler
            result.xValue = xValue
            result.yValue = yValue
            result.transformer = transformer
            result.view = view
            result.xOrigin = xOrigin
            result.yOrigin = yOrigin
            result.animator.duration = duration
            return result
        }

        /**
         * Returns [instance] to the pool. Jobs recycle themselves when their animation ends, so this is only needed
         * for jobs that were never run.
         *
         * @throws IllegalArgumentException when [instance] is already in the pool
         */
        public fun recycleInstance(instance: AnimatedMoveViewJob) {
            pool.recycle(instance)
        }
    }
}
