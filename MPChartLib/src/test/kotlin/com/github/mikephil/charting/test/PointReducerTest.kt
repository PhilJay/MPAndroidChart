package com.github.mikephil.charting.test

import com.github.mikephil.charting.renderer.ColumnValues
import com.github.mikephil.charting.renderer.PointReducer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sin

class PointReducerTest {

    private class Points(private val xs: FloatArray, private val ys: FloatArray) : ColumnValues {
        override fun xAt(index: Int) = xs[index]
        override fun lowAt(index: Int) = ys[index]
        override fun highAt(index: Int) = ys[index]
    }

    private fun kept(reducer: PointReducer) = reducer.indices.copyOf(reducer.count).toList()

    @Test
    fun keepsTheHighestAndLowestOfEveryColumn() {
        val xs = FloatArray(40) { it.toFloat() }
        val ys = FloatArray(40) { 10f }
        ys[21] = -5f
        ys[22] = 99f

        val reducer = PointReducer()
        reducer.reduce(Points(xs, ys), from = 0, to = 39, firstX = 0f, valuesPerPixel = 4f, pixelWidth = 10f)

        assertTrue("the spike survives", kept(reducer).contains(22))
        assertTrue("the dip survives", kept(reducer).contains(21))
    }

    @Test
    fun keepsANonFiniteEntryAndTheRealExtremesOfItsColumn() {
        val xs = FloatArray(40) { it.toFloat() }
        val ys = FloatArray(40) { 10f }
        ys[20] = Float.NaN
        ys[21] = -5f
        ys[22] = 99f

        val reducer = PointReducer()
        reducer.reduce(Points(xs, ys), from = 0, to = 39, firstX = 0f, valuesPerPixel = 4f, pixelWidth = 10f)

        val indices = kept(reducer)
        assertTrue("the gap survives", indices.contains(20))
        assertTrue("the dip survives", indices.contains(21))
        assertTrue("the spike survives", indices.contains(22))
    }

    @Test
    fun keepsEveryEntryBelowOnePointPerPixel() {
        val xs = FloatArray(10) { it.toFloat() }
        val ys = FloatArray(10) { it.toFloat() }

        val reducer = PointReducer()
        reducer.reduce(Points(xs, ys), from = 0, to = 9, firstX = 0f, valuesPerPixel = 0.1f, pixelWidth = 100f)

        assertEquals((0..9).toList(), kept(reducer))
    }

    @Test
    fun returnsAscendingIndices() {
        val xs = FloatArray(1000) { it.toFloat() }
        val ys = FloatArray(1000) { sin(it * 0.1f) * 100f }

        val reducer = PointReducer()
        reducer.reduce(Points(xs, ys), from = 0, to = 999, firstX = 0f, valuesPerPixel = 10f, pixelWidth = 100f)

        val indices = kept(reducer)
        assertTrue("reduced below the input size", indices.size < 1000)
        for (i in 1 until indices.size) {
            assertTrue("index $i is after index ${i - 1}", indices[i] > indices[i - 1])
        }
    }
}
