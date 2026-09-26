package com.github.mikephil.charting.devicetest

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.ref.WeakReference

@RunWith(AndroidJUnit4::class)
class ChartLeakTest {

    @After
    fun tearDown() {
        HostActivity.content = null
    }

    @Test
    fun chartsOfAClosedListAreCollected() {
        val created = mutableListOf<WeakReference<Chart<*>>>()
        scrollThroughAndClose(created)
        assertTrue(created.size > 4)
        assertEquals("charts still reachable", 0, Ui.awaitCollected(created))
    }

    @Test
    fun chartRemovedWhileAnimatingIsCollected() {
        HostActivity.content = { FrameLayout(it) }
        ActivityScenario.launch(HostActivity::class.java).use { scenario ->
            val chart = addAnimatingChartThenRemoveIt(scenario)
            assertEquals("chart still reachable", 0, Ui.awaitCollected(listOf(chart)))
        }
    }

    @Test
    fun recreatedActivityShowsANewChartAndReleasesTheOldOne() {
        val created = mutableListOf<WeakReference<Chart<*>>>()
        HostActivity.content = { activity ->
            LineChart(activity).apply {
                data = LineData(Fixtures.lineSet(Fixtures.lineEntries(50)))
                highlightValue(10f, 0)
                animateY(10_000)
                created += WeakReference(this)
            }
        }

        ActivityScenario.launch(HostActivity::class.java).use { scenario ->
            Ui.idle()
            scenario.recreate()
            Ui.idle()

            val problems = mutableListOf<String>()
            scenario.onActivity { activity ->
                val chart = content(activity).getChildAt(0) as LineChart
                if (chart !== created.last().get()) problems += "the activity shows an old chart"
                if (!chart.isAttachedToWindow) problems += "the new chart is not attached"
                if (chart.highlighted.size != 1) problems += "the new chart has no highlight"
                Ui.drawOffscreen(chart)
            }
            assertEquals(emptyList<String>(), problems)
            assertEquals(2, created.size)
            assertEquals("the chart of the destroyed activity is still reachable", 0, Ui.awaitCollected(created.take(1)))
        }
    }

    private fun scrollThroughAndClose(created: MutableList<WeakReference<Chart<*>>>) {
        HostActivity.content = { chartList(it, ChartAdapter(created)) }
        ActivityScenario.launch(HostActivity::class.java).use { scenario ->
            lateinit var list: RecyclerView
            scenario.onActivity { list = listIn(it) }
            Ui.idle()
            var more = true
            while (more) {
                Ui.onMain {
                    list.scrollBy(0, list.height / 2)
                    more = list.canScrollVertically(1)
                }
                Ui.idle()
            }
            Ui.onMain { list.scrollToPosition(0) }
            Ui.idle()
            scenario.onActivity { content(it).removeAllViews() }
        }
        HostActivity.content = null
    }

    private fun addAnimatingChartThenRemoveIt(scenario: ActivityScenario<HostActivity>): WeakReference<Chart<*>> {
        lateinit var ref: WeakReference<Chart<*>>
        scenario.onActivity { activity ->
            val chart = LineChart(activity).apply { data = LineData(Fixtures.lineSet(Fixtures.lineEntries(50))) }
            (content(activity).getChildAt(0) as ViewGroup).addView(chart, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            chart.animateXY(60_000, 60_000)
            ref = WeakReference(chart)
        }
        Thread.sleep(200)
        Ui.idle()
        scenario.onActivity { (content(it).getChildAt(0) as ViewGroup).removeAllViews() }
        return ref
    }

    private fun content(activity: android.app.Activity): ViewGroup = activity.findViewById(android.R.id.content)
}
