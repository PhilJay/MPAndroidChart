package com.github.mikephil.charting.jobs

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import androidx.annotation.Keep
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Base class of the viewport jobs that animate from an origin to a target over a duration. A [ValueAnimator]
 * drives [phase] from 0 to 1 and calls [onAnimationUpdate] on every frame, where subclasses apply the
 * interpolated position. The job recycles itself when the animation ends or is cancelled.
 *
 * @param duration length of the animation in milliseconds
 */
@Keep
abstract class AnimatedViewPortJob(
    viewPortHandler: ViewPortHandler?,
    xValue: Float,
    yValue: Float,
    transformer: Transformer?,
    view: View?,
    xOrigin: Float,
    yOrigin: Float,
    duration: Long
) : ViewPortJob(viewPortHandler, xValue, yValue, transformer, view), ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

    /**
     * Animator running from 0 to 1 over the duration in milliseconds; started by [run].
     */
    private val phaseUpdater = ValueAnimator.AnimatorUpdateListener { phase = it.animatedValue as Float }

    protected val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        setDuration(duration)
        addUpdateListener(phaseUpdater)
        addUpdateListener(this@AnimatedViewPortJob)
        addListener(this@AnimatedViewPortJob)
    }

    /**
     * Progress of the animation, 0 at the origin and 1 at the target.
     */
    var phase = 0f

    /**
     * x value the animation starts from.
     */
    var xOrigin = xOrigin
        protected set

    /**
     * y value the animation starts from.
     */
    var yOrigin = yOrigin
        protected set

    /**
     * Starts the animation.
     */
    override fun run() {
        animator.start()
    }

    /**
     * Returns this job to its pool. Called when the animation ends or is cancelled.
     */
    abstract fun recycleSelf()

    /**
     * Removes all listeners of the animator, reverses it and registers this job again as its only listener, to
     * prepare a pooled instance for reuse.
     */
    protected fun resetAnimator() {
        animator.removeAllListeners()
        animator.removeAllUpdateListeners()
        animator.reverse()
        animator.addUpdateListener(phaseUpdater)
        animator.addUpdateListener(this)
        animator.addListener(this)
    }

    override fun onAnimationStart(animation: Animator) {
    }

    override fun onAnimationEnd(animation: Animator) {
        try {
            recycleSelf()
        } catch (e: IllegalArgumentException) {
        }
    }

    override fun onAnimationCancel(animation: Animator) {
        try {
            recycleSelf()
        } catch (e: IllegalArgumentException) {
        }
    }

    override fun onAnimationRepeat(animation: Animator) {
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
    }
}
