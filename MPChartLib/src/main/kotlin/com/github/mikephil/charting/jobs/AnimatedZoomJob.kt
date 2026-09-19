package com.github.mikephil.charting.jobs

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Matrix
import android.view.View
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.utils.ObjectPool
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Animates the zoom of a chart from the absolute scale ([xOrigin], [yOrigin]) to ([xValue], [yValue]) while
 * moving the viewport so that the centered value point travels from ([zoomOriginX], [zoomOriginY]) to
 * ([zoomCenterX], [zoomCenterY]). Recalculates the chart offsets when the animation ends. Used instances are
 * not returned to the pool.
 *
 * @property yAxis The y axis whose range decides how many y values fit into the view at a given scale.
 * @property xAxisRange Full range of the x axis in value units.
 * @property zoomCenterX x value in the center of the content rectangle when the animation ends.
 * @property zoomCenterY y value in the center of the content rectangle when the animation ends.
 * @property zoomOriginX x value in the center of the content rectangle when the animation starts.
 * @property zoomOriginY y value in the center of the content rectangle when the animation starts.
 */
class AnimatedZoomJob(
    viewPortHandler: ViewPortHandler?,
    view: View?,
    transformer: Transformer?,
    protected var yAxis: YAxis?,
    protected var xAxisRange: Float,
    scaleX: Float,
    scaleY: Float,
    xOrigin: Float,
    yOrigin: Float,
    protected var zoomCenterX: Float,
    protected var zoomCenterY: Float,
    protected var zoomOriginX: Float,
    protected var zoomOriginY: Float,
    duration: Long
) : AnimatedViewPortJob(viewPortHandler, scaleX, scaleY, transformer, view, xOrigin, yOrigin, duration) {

    private val onAnimationUpdateMatrixBuffer = Matrix()

    override fun onAnimationUpdate(animation: ValueAnimator) {
        val viewPortHandler = viewPortHandler ?: return
        val transformer = transformer ?: return
        val view = view ?: return
        val yAxis = yAxis ?: return

        val scaleX = xOrigin + (xValue - xOrigin) * phase
        val scaleY = yOrigin + (yValue - yOrigin) * phase

        val save = onAnimationUpdateMatrixBuffer
        viewPortHandler.setZoom(scaleX, scaleY, save)
        viewPortHandler.refresh(save, view, false)

        val valsInView = yAxis.axisRange / viewPortHandler.scaleY
        val xsInView = xAxisRange / viewPortHandler.scaleX

        pts[0] = zoomOriginX + ((zoomCenterX - xsInView / 2f) - zoomOriginX) * phase
        pts[1] = zoomOriginY + ((zoomCenterY + valsInView / 2f) - zoomOriginY) * phase

        transformer.pointValuesToPixel(pts)

        viewPortHandler.translate(pts, save)
        viewPortHandler.refresh(save, view, true)
    }

    /**
     * Recalculates the offsets of the chart and redraws it. Unlike the base class, the job is not recycled.
     */
    override fun onAnimationEnd(animation: Animator) {
        (view as? BarLineChartBase<*>)?.calculateOffsets()
        view?.postInvalidate()
    }

    override fun onAnimationCancel(animation: Animator) {
    }

    override fun onAnimationRepeat(animation: Animator) {
    }

    override fun recycleSelf() {
    }

    override fun onAnimationStart(animation: Animator) {
    }

    override fun instantiate(): ObjectPool.Poolable = AnimatedZoomJob(null, null, null, null, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0)

    companion object {

        private val pool: ObjectPool<AnimatedZoomJob> = ObjectPool.create(8, AnimatedZoomJob(null, null, null, null, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0))

        /**
         * Returns a pooled job set up to animate the zoom of [view].
         *
         * @param axis the y axis the zoom and the y values refer to
         * @param xAxisRange full range of the x axis in value units
         * @param scaleX absolute x scale when the animation ends
         * @param scaleY absolute y scale when the animation ends
         * @param xOrigin absolute x scale when the animation starts
         * @param yOrigin absolute y scale when the animation starts
         * @param zoomCenterX x value centered when the animation ends
         * @param zoomCenterY y value centered when the animation ends
         * @param zoomOriginX x value centered when the animation starts
         * @param zoomOriginY y value centered when the animation starts
         * @param duration length of the animation in milliseconds
         */
        fun getInstance(
            viewPortHandler: ViewPortHandler,
            view: View,
            transformer: Transformer,
            axis: YAxis,
            xAxisRange: Float,
            scaleX: Float,
            scaleY: Float,
            xOrigin: Float,
            yOrigin: Float,
            zoomCenterX: Float,
            zoomCenterY: Float,
            zoomOriginX: Float,
            zoomOriginY: Float,
            duration: Long
        ): AnimatedZoomJob {
            val result = pool.get()
            result.viewPortHandler = viewPortHandler
            result.xValue = scaleX
            result.yValue = scaleY
            result.transformer = transformer
            result.view = view
            result.xOrigin = xOrigin
            result.yOrigin = yOrigin
            result.yAxis = axis
            result.xAxisRange = xAxisRange
            result.zoomCenterX = zoomCenterX
            result.zoomCenterY = zoomCenterY
            result.zoomOriginX = zoomOriginX
            result.zoomOriginY = zoomOriginY
            result.resetAnimator()
            result.animator.duration = duration
            return result
        }
    }
}
