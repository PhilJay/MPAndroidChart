package com.github.mikephil.charting.devicetest

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.ref.WeakReference
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class AnimationTest {

    private fun onMain(block: () -> Unit) = InstrumentationRegistry.getInstrumentation().runOnMainSync(block)

    private fun CountDownLatch.awaitEnds() = assertTrue("animations did not end", await(10, TimeUnit.SECONDS))

    private fun lineData(vararg ys: Float) = LineData(LineDataSet(ys.mapIndexed { i, y -> Entry(i.toFloat(), y) }, "line"))

    private fun ys(data: LineData, set: Int = 0) =
        List(data.dataSets[set].entryCount) { data.dataSets[set].getEntryForIndex(it).y }

    private fun lineChart(data: LineData) = Fixtures.lay { LineChart(Fixtures.context()).apply { this.data = data } }

    @Test
    fun animationsReachTheirFinalStateAndEndOnce() {
        val data = lineData(10f, 20f, 30f)
        val line = lineChart(data)
        val pie = Fixtures.lay { PieChart(Fixtures.context()).apply { this.data = PieData(PieDataSet(listOf(PieEntry(1f), PieEntry(2f)), "")) } }
        val entry = data.dataSets[0].getEntryForIndex(1)
        val ends = List(3) { AtomicInteger() }
        val latch = CountDownLatch(3)

        onMain {
            line.animateXY(200, 300) { ends[0].incrementAndGet(); latch.countDown() }
            line.animateValue(entry, 50f, 200) { ends[1].incrementAndGet(); latch.countDown() }
            pie.spin(200, 0f, 90f) { ends[2].incrementAndGet(); latch.countDown() }
            assertTrue(line.isAnimating)
        }
        latch.awaitEnds()
        Thread.sleep(300)

        onMain {
            assertEquals(listOf(1, 1, 1), ends.map { it.get() })
            assertEquals(1f, line.animator.phaseX)
            assertEquals(1f, line.animator.phaseY)
            assertEquals(50f, entry.y)
            assertEquals(50f, line.yMax)
            assertEquals(90f, pie.rotationAngle)
            assertFalse(line.isAnimating)
            assertFalse(pie.isAnimating)
        }
    }

    @Test
    fun aNewAnimationCancelsTheRunningOne() {
        val data = lineData(10f, 20f, 30f)
        val chart = lineChart(data)
        val entry = data.dataSets[0].getEntryForIndex(0)
        val firstEnds = AtomicInteger()
        val latch = CountDownLatch(2)

        onMain {
            chart.animateX(60_000) { firstEnds.incrementAndGet() }
            chart.animateValue(entry, 100f, 60_000) { firstEnds.incrementAndGet() }
            chart.animateX(100) { latch.countDown() }
            chart.animateValue(entry, 20f, 100) { latch.countDown() }
            assertEquals(2, firstEnds.get())
        }
        latch.awaitEnds()

        onMain {
            assertEquals(2, firstEnds.get())
            assertEquals(1f, chart.animator.phaseX)
            assertEquals(20f, entry.y)
        }
    }

    @Test
    fun leavingTheWindowEndsAnimationsAndReleasesTheChart() {
        val entry = Entry(1f, 20f)
        val ends = AtomicInteger()
        var chartRef: WeakReference<LineChart>? = null

        ActivityScenario.launch(HostActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val chart = LineChart(activity)
                chart.data = LineData(LineDataSet(listOf(Entry(0f, 10f), entry), "line"))
                activity.root.addView(chart)
                chart.animateXY(60_000, 60_000) { ends.incrementAndGet() }
                chart.animateValue(entry, 70f, 60_000) { ends.incrementAndGet() }

                activity.root.removeView(chart)

                assertEquals(2, ends.get())
                assertEquals(1f, chart.animator.phaseX)
                assertEquals(70f, entry.y)
                assertFalse(chart.isAnimating)
                chartRef = WeakReference(chart)
            }

            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            repeat(50) {
                if (chartRef!!.get() == null) return@repeat
                Runtime.getRuntime().gc()
                System.runFinalization()
                Thread.sleep(100)
            }
            assertNull("the detached chart is still reachable", chartRef!!.get())
        }
    }

    @Test
    fun dataChangesEndWithExactTargetValues() {
        val chart = lineChart(lineData(10f, 20f, 30f))
        val grown = lineData(1f, 2f, 3f, 4f, 5f)
        val shrunk = lineData(7f, 8f)
        val otherShape = LineData(LineDataSet(listOf(Entry(0f, 3f)), "a"), LineDataSet(listOf(Entry(0f, 4f)), "b"))
        val latch = CountDownLatch(1)

        onMain {
            chart.highlightValue(Highlight(1f, 20f, 0))
            chart.animateDataChange(grown, 60_000)
            chart.animateDataChange(shrunk, 100) { latch.countDown() }
            assertEquals(listOf(1f, 2f, 3f, 4f, 5f), ys(grown))
        }
        latch.awaitEnds()

        onMain {
            assertSame(shrunk, chart.data)
            assertEquals(listOf(7f, 8f), ys(shrunk))
            assertEquals(8f, chart.highlighted.single().y)
            var ended = false
            chart.animateDataChange(otherShape, 60_000) { ended = true }
            assertTrue(ended)
            assertSame(otherShape, chart.data)
            assertFalse(chart.isAnimating)
        }

        val stacks = listOf(listOf(1f, 2f), listOf(3f, 4f, 5f))
        val bars = BarData(BarDataSet(stacks.mapIndexed { i, stack -> BarEntry(i.toFloat(), stack) }, "bars"))
        val barChart = Fixtures.lay {
            BarChart(Fixtures.context()).apply { data = BarData(BarDataSet(listOf(BarEntry(0f, 9f)), "bars")) }
        }
        val barLatch = CountDownLatch(1)
        onMain { barChart.animateDataChange(bars, 100) { barLatch.countDown() } }
        barLatch.awaitEnds()
        onMain {
            val set = bars.dataSets[0]
            assertEquals(stacks, List(set.entryCount) { set.getEntryForIndex(it).stackValues })
            assertEquals(12f, set.getEntryForIndex(1).y)
        }
    }

    @Test
    fun stopAnimationsEndsEverythingAtTheFinalState() {
        val chart = lineChart(lineData(10f, 20f, 30f))
        val target = lineData(40f, 50f)
        val ends = AtomicInteger()

        onMain {
            chart.animateY(60_000) { ends.incrementAndGet() }
            chart.animateDataChange(target, 60_000) { ends.incrementAndGet() }
            assertTrue(chart.isAnimating)

            chart.stopAnimations()

            assertEquals(2, ends.get())
            assertFalse(chart.isAnimating)
            assertEquals(1f, chart.animator.phaseY)
            assertEquals(listOf(40f, 50f), ys(target))
            assertEquals(50f, chart.yMax)
        }
    }
}
