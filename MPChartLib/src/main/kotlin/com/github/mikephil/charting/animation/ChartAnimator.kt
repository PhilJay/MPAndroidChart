package com.github.mikephil.charting.animation

import android.animation.ValueAnimator
import androidx.annotation.Keep
import com.github.mikephil.charting.animation.Easing.EasingFunction

/**
 * Runs the entry animations of a chart. It drives [phaseX] and [phaseY] from 0 to 1 with a [ValueAnimator]; the
 * renderers draw only the first [phaseX] share of the entries and scale their values by [phaseY]. Every chart owns
 * one and passes a listener that redraws the chart on each animation frame.
 * @param listener called on every animation frame; the chart passes one that invalidates itself.
 */
@Keep
open class ChartAnimator(private val listener: ValueAnimator.AnimatorUpdateListener? = null) {

    /** Progress of the y animation, 0..1; the renderers multiply the drawn values by it. Assigned values are clamped. Default 1. */
    var phaseY = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
        }

    /** Progress of the x animation, 0..1; the renderers draw only this share of the entries. Assigned values are clamped. Default 1. */
    var phaseX = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
        }

    private fun xAnimator(duration: Int, easing: EasingFunction): ValueAnimator {
        return ValueAnimator.ofFloat(0f, 1f).apply {
            interpolator = easing
            setDuration(duration.toLong())
            addUpdateListener { phaseX = it.animatedValue as Float }
        }
    }

    private fun yAnimator(duration: Int, easing: EasingFunction): ValueAnimator {
        return ValueAnimator.ofFloat(0f, 1f).apply {
            interpolator = easing
            setDuration(duration.toLong())
            addUpdateListener { phaseY = it.animatedValue as Float }
        }
    }

    /**
     * Animates [phaseX] from 0 to 1.
     * @param durationMillis duration in milliseconds
     * @param easing easing curve, default [Easing.Linear]
     */
    fun animateX(durationMillis: Int, easing: EasingFunction = Easing.Linear) {
        val animatorX = xAnimator(durationMillis, easing)
        listener?.let { animatorX.addUpdateListener(it) }
        animatorX.start()
    }

    /**
     * Animates [phaseX] and [phaseY] from 0 to 1 at the same time. The chart is redrawn from the longer of the two
     * animations.
     * @param durationMillisX duration of the x animation in milliseconds
     * @param durationMillisY duration of the y animation in milliseconds
     * @param easingX easing curve for x, default [Easing.Linear]
     * @param easingY easing curve for y, default the same as [easingX]
     */
    fun animateXY(
        durationMillisX: Int,
        durationMillisY: Int,
        easingX: EasingFunction = Easing.Linear,
        easingY: EasingFunction = easingX
    ) {
        val xAnimator = xAnimator(durationMillisX, easingX)
        val yAnimator = yAnimator(durationMillisY, easingY)
        listener?.let {
            if (durationMillisX > durationMillisY) xAnimator.addUpdateListener(it) else yAnimator.addUpdateListener(it)
        }
        xAnimator.start()
        yAnimator.start()
    }

    /**
     * Animates [phaseY] from 0 to 1.
     * @param durationMillis duration in milliseconds
     * @param easing easing curve, default [Easing.Linear]
     */
    fun animateY(durationMillis: Int, easing: EasingFunction = Easing.Linear) {
        val animatorY = yAnimator(durationMillis, easing)
        listener?.let { animatorY.addUpdateListener(it) }
        animatorY.start()
    }
}
