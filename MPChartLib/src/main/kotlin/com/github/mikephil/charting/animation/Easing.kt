package com.github.mikephil.charting.animation

import android.animation.TimeInterpolator
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Easing curves for [ChartAnimator]. Each maps animation progress 0..1 to an eased value that is 0 at 0 and 1 at 1;
 * the elastic, back and bounce curves overshoot in between. EaseIn starts slow, EaseOut ends slow, EaseInOut does
 * both.
 */
public object Easing {

    /** An easing curve: a [TimeInterpolator] that maps progress 0..1 to a value that is 0 at 0 and 1 at 1. */
    public fun interface EasingFunction : TimeInterpolator

    private const val DOUBLE_PI = 2f * Math.PI.toFloat()

    /** Constant speed. */
    public val Linear: EasingFunction = EasingFunction { input -> input }

    /** Quadratic, starts slow. */
    public val EaseInQuad: EasingFunction = EasingFunction { input -> input * input }

    /** Quadratic, ends slow. */
    public val EaseOutQuad: EasingFunction = EasingFunction { input -> -input * (input - 2f) }

    /** Quadratic, starts and ends slow. */
    public val EaseInOutQuad: EasingFunction = EasingFunction { value ->
        var input = value * 2f
        if (input < 1f) {
            0.5f * input * input
        } else {
            input -= 1f
            -0.5f * (input * (input - 2f) - 1f)
        }
    }

    /** Cubic, starts slow. */
    public val EaseInCubic: EasingFunction = EasingFunction { input -> input.pow(3) }

    /** Cubic, ends slow. */
    public val EaseOutCubic: EasingFunction = EasingFunction { value ->
        val input = value - 1f
        input.pow(3) + 1f
    }

    /** Cubic, starts and ends slow. */
    public val EaseInOutCubic: EasingFunction = EasingFunction { value ->
        var input = value * 2f
        if (input < 1f) {
            0.5f * input.pow(3)
        } else {
            input -= 2f
            0.5f * (input.pow(3) + 2f)
        }
    }

    /** Quartic, starts slow. */
    public val EaseInQuart: EasingFunction = EasingFunction { input -> input.pow(4) }

    /** Quartic, ends slow. */
    public val EaseOutQuart: EasingFunction = EasingFunction { value ->
        val input = value - 1f
        -(input.pow(4) - 1f)
    }

    /** Quartic, starts and ends slow. */
    public val EaseInOutQuart: EasingFunction = EasingFunction { value ->
        var input = value * 2f
        if (input < 1f) {
            0.5f * input.pow(4)
        } else {
            input -= 2f
            -0.5f * (input.pow(4) - 2f)
        }
    }

    /** Sine wave, gently starts slow. */
    public val EaseInSine: EasingFunction = EasingFunction { input -> -cos(input * (Math.PI / 2f)).toFloat() + 1f }

    /** Sine wave, gently ends slow. */
    public val EaseOutSine: EasingFunction = EasingFunction { input -> sin(input * (Math.PI / 2f)).toFloat() }

    /** Sine wave, gently starts and ends slow. */
    public val EaseInOutSine: EasingFunction = EasingFunction { input -> -0.5f * (cos(Math.PI * input).toFloat() - 1f) }

    /** Exponential, starts very slow. */
    public val EaseInExpo: EasingFunction = EasingFunction { input -> if (input == 0f) 0f else 2f.pow(10f * (input - 1f)) }

    /** Exponential, ends very slow. */
    public val EaseOutExpo: EasingFunction = EasingFunction { input -> if (input == 1f) 1f else 1f - 2f.pow(-10f * input) }

    /** Exponential, starts and ends very slow. */
    public val EaseInOutExpo: EasingFunction = EasingFunction { value ->
        when (value) {
            0f -> 0f
            1f -> 1f
            else -> {
                var input = value * 2f
                if (input < 1f) {
                    0.5f * 2f.pow(10f * (input - 1f))
                } else {
                    input -= 1f
                    0.5f * (-(2f.pow(-10f * input)) + 2f)
                }
            }
        }
    }

    /** Circular, starts slow. */
    public val EaseInCirc: EasingFunction = EasingFunction { input -> -(sqrt(1f - input * input) - 1f) }

    /** Circular, ends slow. */
    public val EaseOutCirc: EasingFunction = EasingFunction { value ->
        val input = value - 1f
        sqrt(1f - input * input)
    }

    /** Circular, starts and ends slow. */
    public val EaseInOutCirc: EasingFunction = EasingFunction { value ->
        var input = value * 2f
        if (input < 1f) {
            -0.5f * (sqrt(1f - input * input) - 1f)
        } else {
            input -= 2f
            0.5f * (sqrt(1f - input * input) + 1f)
        }
    }

    /** Winds back below 0 like a spring before moving to 1. */
    public val EaseInElastic: EasingFunction = EasingFunction { value ->
        when (value) {
            0f -> 0f
            1f -> 1f
            else -> {
                val p = 0.3f
                val s = p / DOUBLE_PI * asin(1f)
                val input = value - 1f
                -(2f.pow(10f * input) * sin((input - s) * DOUBLE_PI / p))
            }
        }
    }

    /** Springs past 1 and settles back. */
    public val EaseOutElastic: EasingFunction = EasingFunction { input ->
        when (input) {
            0f -> 0f
            1f -> 1f
            else -> {
                val p = 0.3f
                val s = p / DOUBLE_PI * asin(1f)
                1f + 2f.pow(-10f * input) * sin((input - s) * DOUBLE_PI / p)
            }
        }
    }

    /** Winds back at the start and springs past 1 at the end. */
    public val EaseInOutElastic: EasingFunction = EasingFunction { value ->
        if (value == 0f) {
            0f
        } else {
            var input = value * 2f
            if (input == 2f) {
                1f
            } else {
                val p = 1f / 0.45f
                val s = 0.45f / DOUBLE_PI * asin(1f)
                if (input < 1f) {
                    input -= 1f
                    -0.5f * (2f.pow(10f * input) * sin((input * 1f - s) * DOUBLE_PI * p))
                } else {
                    input -= 1f
                    1f + 0.5f * (2f.pow(-10f * input) * sin((input * 1f - s) * DOUBLE_PI * p))
                }
            }
        }
    }

    /** Pulls back below 0 slightly before moving to 1. */
    public val EaseInBack: EasingFunction = EasingFunction { input ->
        val s = 1.70158f
        input * input * ((s + 1f) * input - s)
    }

    /** Overshoots 1 slightly and settles back. */
    public val EaseOutBack: EasingFunction = EasingFunction { value ->
        val s = 1.70158f
        val input = value - 1f
        input * input * ((s + 1f) * input + s) + 1f
    }

    /** Pulls back at the start and overshoots at the end. */
    public val EaseInOutBack: EasingFunction = EasingFunction { value ->
        var s = 1.70158f
        var input = value * 2f
        if (input < 1f) {
            s *= 1.525f
            0.5f * (input * input * ((s + 1f) * input - s))
        } else {
            input -= 2f
            s *= 1.525f
            0.5f * (input * input * ((s + 1f) * input + s) + 2f)
        }
    }

    /** Bounces to a stop at 1 like a dropped ball. */
    public val EaseOutBounce: EasingFunction = EasingFunction { value ->
        val s = 7.5625f
        var input = value
        when {
            input < 1f / 2.75f -> s * input * input
            input < 2f / 2.75f -> {
                input -= 1.5f / 2.75f
                s * input * input + 0.75f
            }
            input < 2.5f / 2.75f -> {
                input -= 2.25f / 2.75f
                s * input * input + 0.9375f
            }
            else -> {
                input -= 2.625f / 2.75f
                s * input * input + 0.984375f
            }
        }
    }

    /** Bounces at the start, the mirror of [EaseOutBounce]. */
    public val EaseInBounce: EasingFunction = EasingFunction { input -> 1f - EaseOutBounce.getInterpolation(1f - input) }

    /** Bounces at the start and at the end. */
    public val EaseInOutBounce: EasingFunction = EasingFunction { input ->
        if (input < 0.5f) {
            EaseInBounce.getInterpolation(input * 2f) * 0.5f
        } else {
            EaseOutBounce.getInterpolation(input * 2f - 1f) * 0.5f + 0.5f
        }
    }
}
