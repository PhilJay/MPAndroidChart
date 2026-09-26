package com.github.mikephil.charting.test

import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.animation.ValueTransition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class ValueTransitionTest {

    private fun line(vararg ys: Float) = LineData(LineDataSet(ys.mapIndexed { i, y -> Entry(i.toFloat(), y) }, "line"))

    private fun ys(data: LineData) = List(data.dataSets[0].entryCount) { data.dataSets[0].getEntryForIndex(it).y }

    @Test
    fun linePairsByIndexAndGrowsNewEntriesFromZero() {
        val old = line(10f, 20f, 30f)
        val new = line(20f, 40f, 60f, 80f)
        val transition = ValueTransition.between(old, new)!!

        transition.apply(0f)
        assertEquals(listOf(10f, 20f, 30f, 0f), ys(new))
        transition.apply(0.5f)
        assertEquals(listOf(15f, 30f, 45f, 40f), ys(new))
        transition.finish()
        assertEquals(listOf(20f, 40f, 60f, 80f), ys(new))
        assertEquals(listOf(10f, 20f, 30f), ys(old))

        val shrunk = line(1f)
        ValueTransition.between(new, shrunk)!!.apply(0f)
        assertEquals(listOf(20f), ys(shrunk))
    }

    @Test
    fun stacksCandlesAndBubblesMoveEveryValue() {
        val targetStack = listOf(4f, 6f, 10f)
        val bar = BarEntry(0f, targetStack)
        val bars = BarData(BarDataSet(listOf(bar), "bars"))
        ValueTransition.between(BarData(BarDataSet(listOf(BarEntry(0f, 10f)), "bars")), bars)!!.apply {
            apply(0f)
            assertEquals(listOf(10f, 0f, 0f), bar.stackValues)
            apply(0.5f)
            assertEquals(listOf(7f, 3f, 5f), bar.stackValues)
            assertEquals(15f, bar.y)
            finish()
        }
        assertSame(targetStack, bar.stackValues)
        assertEquals(20f, bar.y)

        val candle = CandleEntry(0f, 20f, 10f, 12f, 18f)
        val candles = CandleData(CandleDataSet(listOf(candle), "candles"))
        ValueTransition.between(CandleData(CandleDataSet(listOf(CandleEntry(0f, 10f, 0f, 2f, 8f)), "candles")), candles)!!.apply(0.5f)
        assertEquals(listOf(15f, 5f, 7f, 13f, 10f), listOf(candle.high, candle.low, candle.open, candle.close, candle.y))

        val bubble = BubbleEntry(0f, 10f, 4f)
        val bubbles = BubbleData(BubbleDataSet(listOf(bubble), "bubbles"))
        ValueTransition.between(BubbleData(BubbleDataSet(emptyList<BubbleEntry<Nothing>>(), "bubbles")), bubbles)!!.apply(0.5f)
        assertEquals(listOf(5f, 2f), listOf(bubble.y, bubble.size))
    }

    @Test
    fun differentShapesAreNotPaired() {
        val old = line(1f, 2f)
        assertNull(ValueTransition.between(old, ScatterData(ScatterDataSet(listOf(Entry(0f, 1f)), "scatter"))))
        assertNull(ValueTransition.between(old, LineData(old.dataSets[0], LineDataSet(listOf(Entry(0f, 1f)), "second"))))
        assertNull(ValueTransition.between(old, LineData()))
    }

    @Test
    fun afterCallsRunsTheBlockOnTheLastCallOnly() {
        var runs = 0
        val call = ChartAnimator.afterCalls(2) { runs++ }
        call()
        assertEquals(0, runs)
        call()
        call()
        assertEquals(1, runs)
    }
}
