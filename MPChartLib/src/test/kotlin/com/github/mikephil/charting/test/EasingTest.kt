package com.github.mikephil.charting.test

import com.github.mikephil.charting.animation.Easing
import org.junit.Assert.assertEquals
import org.junit.Test

class EasingTest {

    @Test
    fun everyEasingStartsAtZeroAndEndsAtOne() {
        val easings = mapOf(
            "Linear" to Easing.Linear,
            "EaseInQuad" to Easing.EaseInQuad, "EaseOutQuad" to Easing.EaseOutQuad, "EaseInOutQuad" to Easing.EaseInOutQuad,
            "EaseInCubic" to Easing.EaseInCubic, "EaseOutCubic" to Easing.EaseOutCubic, "EaseInOutCubic" to Easing.EaseInOutCubic,
            "EaseInQuart" to Easing.EaseInQuart, "EaseOutQuart" to Easing.EaseOutQuart, "EaseInOutQuart" to Easing.EaseInOutQuart,
            "EaseInSine" to Easing.EaseInSine, "EaseOutSine" to Easing.EaseOutSine, "EaseInOutSine" to Easing.EaseInOutSine,
            "EaseInExpo" to Easing.EaseInExpo, "EaseOutExpo" to Easing.EaseOutExpo, "EaseInOutExpo" to Easing.EaseInOutExpo,
            "EaseInCirc" to Easing.EaseInCirc, "EaseOutCirc" to Easing.EaseOutCirc, "EaseInOutCirc" to Easing.EaseInOutCirc,
            "EaseInElastic" to Easing.EaseInElastic, "EaseOutElastic" to Easing.EaseOutElastic, "EaseInOutElastic" to Easing.EaseInOutElastic,
            "EaseInBack" to Easing.EaseInBack, "EaseOutBack" to Easing.EaseOutBack, "EaseInOutBack" to Easing.EaseInOutBack,
            "EaseInBounce" to Easing.EaseInBounce, "EaseOutBounce" to Easing.EaseOutBounce, "EaseInOutBounce" to Easing.EaseInOutBounce,
        )
        for ((name, easing) in easings) {
            assertEquals(name, 0f, easing.getInterpolation(0f), 0.01f)
            assertEquals(name, 1f, easing.getInterpolation(1f), 0.01f)
        }
        assertEquals(0.25f, Easing.EaseInQuad.getInterpolation(0.5f), 0.001f)
    }
}
