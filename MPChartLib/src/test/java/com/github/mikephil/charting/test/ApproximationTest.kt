package com.github.mikephil.charting.test

import com.github.mikephil.charting.data.filter.Approximator
import org.junit.Assert
import org.junit.Test

class ApproximationTest {
    @Test
    fun testApproximation() {
        val points = floatArrayOf(10f, 20f, 20f, 30f, 25f, 25f, 30f, 28f, 31f, 31f, 33f, 33f, 40f, 40f, 44f, 40f, 48f, 23f, 50f, 20f, 55f, 20f, 60f, 25f)

        Assert.assertEquals(24, points.size)

        val a = Approximator()

        val reduced = a.reduceWithDouglasPeucker(points, 2f)

        Assert.assertEquals(18, reduced.size)
    }
}
