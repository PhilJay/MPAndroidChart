package com.github.mikephil.charting.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import com.github.mikephil.charting.animation.Easing.EasingFunction
import java.util.IdentityHashMap

/**
 * Runs the animations of a chart. It drives [phaseX] and [phaseY] from 0 to 1 with a [ValueAnimator]; the
 * renderers draw only the first [phaseX] share of the entries and scale their values by [phaseY]. It also runs the
 * chart's spin and value animations. Every chart owns one and passes a listener that redraws the chart on each
 * animation frame.
 *
 * Each animation has a slot: one for [phaseX], one for [phaseY], one for the spin of a pie or radar chart, one per
 * animated entry and one for a data change. Starting an animation cancels the one running in the same slot, which
 * keeps its current value; the cancelled animation still counts as ended and calls its `onEnd`. All members are
 * main-thread only.
 *
 * @param listener called on every animation frame; the chart passes one that invalidates itself.
 */
public open class ChartAnimator(private val listener: ValueAnimator.AnimatorUpdateListener? = null) {

    /** Progress of the y animation, 0..1; the renderers multiply the drawn values by it. Assigned values are clamped. Default 1. */
    public var phaseY: Float = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
        }

    /** Progress of the x animation, 0..1; the renderers draw only this share of the entries. Assigned values are clamped. Default 1. */
    public var phaseX: Float = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
        }

    private val running = IdentityHashMap<Any, ValueAnimator>()

    /** True while any animation runs: a phase animation, a spin or a value animation. */
    public val isAnimating: Boolean
        get() = running.isNotEmpty()

    /**
     * Animates [phaseX] from 0 to 1. A running x animation is cancelled first.
     * @param durationMillis duration in milliseconds
     * @param easing easing curve, default [Easing.Linear]
     * @param onEnd called once when the animation ends, whether it finished, was stopped or was cancelled by a new x
     * animation.
     */
    @JvmOverloads
    public fun animateX(durationMillis: Int, easing: EasingFunction = Easing.Linear, onEnd: () -> Unit = {}) {
        animatePhase(PHASE_X, durationMillis, easing, onEnd) { phaseX = it }
    }

    /**
     * Animates [phaseX] and [phaseY] from 0 to 1 at the same time. Running x and y animations are cancelled first.
     * @param durationMillisX duration of the x animation in milliseconds
     * @param durationMillisY duration of the y animation in milliseconds
     * @param easingX easing curve for x, default [Easing.Linear]
     * @param easingY easing curve for y, default the same as [easingX]
     * @param onEnd called once after both animations have ended.
     */
    @JvmOverloads
    public fun animateXY(
        durationMillisX: Int,
        durationMillisY: Int,
        easingX: EasingFunction = Easing.Linear,
        easingY: EasingFunction = easingX,
        onEnd: () -> Unit = {}
    ) {
        val bothEnded = afterCalls(2, onEnd)
        animatePhase(PHASE_X, durationMillisX, easingX, bothEnded) { phaseX = it }
        animatePhase(PHASE_Y, durationMillisY, easingY, bothEnded) { phaseY = it }
    }

    /**
     * Animates [phaseY] from 0 to 1. A running y animation is cancelled first.
     * @param durationMillis duration in milliseconds
     * @param easing easing curve, default [Easing.Linear]
     * @param onEnd called once when the animation ends, whether it finished, was stopped or was cancelled by a new y
     * animation.
     */
    @JvmOverloads
    public fun animateY(durationMillis: Int, easing: EasingFunction = Easing.Linear, onEnd: () -> Unit = {}) {
        animatePhase(PHASE_Y, durationMillis, easing, onEnd) { phaseY = it }
    }

    /**
     * Ends every running animation at once at its final state: the phases jump to 1, a spin to its end angle and
     * animated values to their targets. Each `onEnd` runs.
     */
    public fun stop() {
        for (animator in running.values.toList()) animator.end()
    }

    /** Ends the value animations, those started with [start] under an entry or [DATA_CHANGE], at their final state. */
    internal fun endValueAnimations() {
        for ((key, animator) in running.entries.toList()) {
            if (key !== PHASE_X && key !== PHASE_Y && key !== SPIN) animator.end()
        }
    }

    private fun animatePhase(key: Any, durationMillis: Int, easing: EasingFunction, onEnd: () -> Unit, setPhase: (Float) -> Unit) {
        start(key, 0f, 1f, durationMillis, easing, onUpdate = setPhase, onEnd = { onEnd() })
    }

    /**
     * Animates a value from [from] to [to] in the slot [key], cancelling the animation running there first.
     * @param onUpdate called on every frame with the current value, before the redraw listener.
     * @param onEnd called once when the animation ends; the argument is true when a new animation in the same slot
     * cancelled it, false when it finished or was ended by [stop].
     */
    internal fun start(
        key: Any,
        from: Float,
        to: Float,
        durationMillis: Int,
        easing: EasingFunction,
        onUpdate: (Float) -> Unit,
        onEnd: (cancelled: Boolean) -> Unit
    ) {
        running.remove(key)?.cancel()
        val animator = ValueAnimator.ofFloat(from, to)
        animator.interpolator = easing
        animator.duration = durationMillis.coerceAtLeast(0).toLong()
        animator.addUpdateListener {
            onUpdate(it.animatedValue as Float)
            listener?.onAnimationUpdate(it)
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            private var cancelled = false

            override fun onAnimationCancel(animation: Animator) {
                cancelled = true
            }

            override fun onAnimationEnd(animation: Animator) {
                animation.removeAllListeners()
                (animation as ValueAnimator).removeAllUpdateListeners()
                if (running[key] === animation) running.remove(key)
                onEnd(cancelled)
            }
        })
        running[key] = animator
        animator.start()
    }

    internal companion object {
        private val PHASE_X = Any()
        private val PHASE_Y = Any()

        /** Slot of the spin of a pie or radar chart. */
        val SPIN = Any()

        /** Slot of an animated data change. */
        val DATA_CHANGE = Any()

        /** Returns a function that calls [block] on its [count]th call and does nothing on the others. */
        fun afterCalls(count: Int, block: () -> Unit): () -> Unit {
            var left = count
            return {
                left--
                if (left == 0) block()
            }
        }
    }
}
