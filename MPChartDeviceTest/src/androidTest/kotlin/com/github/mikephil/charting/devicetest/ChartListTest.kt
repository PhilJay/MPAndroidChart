package com.github.mikephil.charting.devicetest

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.Chart
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.ref.WeakReference

/**
 * Charts inside a RecyclerView. The library keeps a highlight when new data is set, so an adapter clears or
 * sets it on every bind; a highlight left over from the previous item must draw without crashing.
 */
@RunWith(AndroidJUnit4::class)
class ChartListTest {

    @After
    fun tearDown() {
        HostActivity.content = null
    }

    @Test
    fun recycledChartsShowTheItemTheyWereLastBoundTo() {
        val created = mutableListOf<WeakReference<Chart<*>>>()
        val adapter = ChartAdapter(created)
        HostActivity.content = { chartList(it, adapter) }

        ActivityScenario.launch(HostActivity::class.java).use { scenario ->
            lateinit var list: RecyclerView
            scenario.onActivity { list = listIn(it) }
            Ui.idle()
            val problems = mutableListOf<String>()

            repeat(3) {
                var more = true
                while (more) {
                    Ui.onMain {
                        list.scrollBy(0, list.height / 2)
                        more = list.canScrollVertically(1)
                    }
                    Ui.idle()
                    check(list, adapter, problems)
                }

                Ui.onMain { list.scrollToPosition(0) }
                Ui.idle()
                check(list, adapter, problems)

                Ui.onMain { list.smoothScrollToPosition(adapter.itemCount - 1) }
                Ui.awaitScrollIdle(list)
                check(list, adapter, problems)

                var atTop = false
                var flings = 0
                while (!atTop && flings++ < 30) {
                    Ui.onMain { list.fling(0, -list.maxFlingVelocity) }
                    Ui.awaitScrollIdle(list)
                    check(list, adapter, problems)
                    Ui.onMain { atTop = !list.canScrollVertically(-1) }
                }
                assertTrue("flinging never reached the top", atTop)
            }

            assertEquals(emptyList<String>(), problems.distinct())
            assertTrue("charts were not reused, ${created.size} created", created.size < 60)
            assertTrue("only ${adapter.binds} binds", adapter.binds > 3 * adapter.itemCount)
        }
    }

    @Test
    fun draggingAZoomedChartKeepsTheListStillUntilTheFingerLifts() {
        val adapter = ChartAdapter(mutableListOf(), count = 40, animate = false)
        HostActivity.content = { chartList(it, adapter) }

        ActivityScenario.launch(HostActivity::class.java).use { scenario ->
            lateinit var list: RecyclerView
            scenario.onActivity { list = listIn(it) }
            Ui.idle()

            val plain = visibleBarLineChart(list) { true }
            val start = offset(list)
            swipeUp(plain, list.height / 3f)
            Ui.awaitScrollIdle(list)
            assertTrue("a vertical swipe over a chart that is not zoomed scrolls the list", offset(list) > start)

            val zoomed = visibleBarLineChart(list) { true }
            Ui.onMain { zoomed.zoom(4f, 1f, 0f, 0f) }
            Ui.idle()
            val listBefore = offset(list)
            val lowestBefore = lowestVisibleX(zoomed)
            val (x, y) = center(zoomed)
            val downTime = Ui.down(x, y)
            Ui.move(downTime, x, y, -zoomed.width / 4f, 0f)
            Ui.move(downTime, x - zoomed.width / 4f, y, 0f, -zoomed.height / 2f)
            Ui.up(downTime, x - zoomed.width / 4f, y - zoomed.height / 2f)
            Ui.awaitScrollIdle(list)
            assertEquals("the list stays put while a zoomed chart pans", listBefore, offset(list))
            assertTrue("the zoomed chart panned", lowestVisibleX(zoomed) > lowestBefore)

            var other = findBarLineChart(list) { it !== zoomed }
            while (other == null) {
                Ui.onMain { list.scrollBy(0, list.height / 6) }
                Ui.idle()
                other = findBarLineChart(list) { it !== zoomed }
            }
            val afterDrag = offset(list)
            swipeUp(other, list.height / 3f)
            Ui.awaitScrollIdle(list)
            assertTrue("the list scrolls again once the finger lifted", offset(list) > afterDrag)
        }
    }

    private fun check(list: RecyclerView, adapter: ChartAdapter, problems: MutableList<String>) = Ui.onMain {
        for (i in 0 until list.childCount) {
            val holder = list.getChildViewHolder(list.getChildAt(i)) as ChartHolder
            val position = holder.bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) continue
            val chart = holder.chart
            val data = chart.data
            if (data !== adapter.bound[position]) problems += "$position shows data of another bind"
            val label = data?.getDataSetByIndex(0)?.label
            if (label != ChartAdapter.label(position)) problems += "$position shows '$label'"
            when (ChartAdapter.expectation(position)) {
                ChartAdapter.Expect.Highlighted -> {
                    val highlight = chart.highlighted.singleOrNull()
                    if (highlight == null || highlight.x != ChartAdapter.lastX(chart) || data?.getEntryForHighlight(highlight) == null) {
                        problems += "$position lost its highlight: ${chart.highlighted}"
                    }
                }
                ChartAdapter.Expect.Cleared -> if (chart.highlighted.isNotEmpty()) problems += "$position kept an old highlight"
                ChartAdapter.Expect.Untouched -> {}
            }
            Ui.drawOffscreen(chart)
        }
    }

    private fun visibleBarLineChart(list: RecyclerView, accept: (View) -> Boolean): BarLineChartBase<*> =
        findBarLineChart(list, accept) ?: throw AssertionError("no fully visible line or bar chart")

    private fun findBarLineChart(list: RecyclerView, accept: (View) -> Boolean): BarLineChartBase<*>? {
        var found: BarLineChartBase<*>? = null
        Ui.onMain {
            for (i in 0 until list.childCount) {
                val child = list.getChildAt(i)
                if (child is BarLineChartBase<*> && child.top >= 0 && child.bottom <= list.height && accept(child)) {
                    found = child
                    break
                }
            }
        }
        return found
    }

    private fun swipeUp(view: View, distance: Float) {
        val (x, y) = center(view)
        val downTime = Ui.down(x, y)
        Ui.move(downTime, x, y, 0f, -distance)
        Ui.up(downTime, x, y - distance)
    }

    private fun center(view: View): Pair<Float, Float> {
        val location = IntArray(2)
        Ui.onMain { view.getLocationOnScreen(location) }
        return (location[0] + view.width / 2f) to (location[1] + view.height / 2f)
    }

    private fun offset(list: RecyclerView): Int {
        var offset = 0
        Ui.onMain { offset = list.computeVerticalScrollOffset() }
        return offset
    }

    private fun lowestVisibleX(chart: BarLineChartBase<*>): Float {
        var x = 0f
        Ui.onMain { x = chart.lowestVisibleX }
        return x
    }
}
