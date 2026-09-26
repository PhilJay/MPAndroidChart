package com.github.mikephil.charting.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.ref.WeakReference
import kotlin.random.Random
import com.github.mikephil.charting.charts.CombinedChart as CombinedChartView

@RunWith(AndroidJUnit4::class)
class ChartLazyListTest {

    @get:Rule
    val rule = createComposeRule()

    private class Item(val index: Int, val data: ChartData<*>, val highlightX: Float?, val marker: Boolean)

    private class Shown(val chart: WeakReference<Chart<*>>, val state: WeakReference<ChartState>)

    private val items = List(COUNT) { item(it) }
    private val created = mutableListOf<WeakReference<Chart<*>>>()
    private val states = mutableListOf<WeakReference<ChartState>>()
    private val shown = HashMap<Int, Shown>()
    private var listState: LazyListState? = LazyListState()
    private var show by mutableStateOf(true)

    @Test
    fun chartsInALazyColumnShowTheirItemAndAreReleasedAfterwards() {
        rule.setContent {
            if (show) {
                LazyColumn(Modifier.fillMaxSize().testTag("list"), state = listState!!) {
                    items(COUNT) { index -> ChartItem(items[index]) }
                }
            } else {
                // Compose keeps removed nodes in a parent's child cache until that parent lays out again.
                BasicText("empty")
            }
        }

        val problems = scrollThroughEverything()
        assertEquals(emptyList<String>(), problems.distinct())
        assertTrue("only ${created.size} charts were created", created.size >= COUNT)

        rule.runOnIdle { show = false }
        rule.waitForIdle()
        shown.clear()
        listState = null
        assertEquals("charts still reachable", 0, awaitCollected(created))
        assertEquals("chart states still reachable", 0, awaitCollected(states))
    }

    private fun scrollThroughEverything(): List<String> {
        val problems = mutableListOf<String>()
        val list = rule.onNodeWithTag("list")
        for (index in 0 until COUNT step 2) {
            list.performScrollToIndex(index)
            check(problems)
        }
        for (index in COUNT - 1 downTo 0 step 3) {
            list.performScrollToIndex(index)
            check(problems)
        }
        repeat(2) {
            list.performScrollToIndex(COUNT - 1)
            check(problems)
            repeat(4) { list.performTouchInput { swipeDown() } }
            check(problems)
            list.performScrollToIndex(0)
            check(problems)
            repeat(4) { list.performTouchInput { swipeUp() } }
            check(problems)
        }
        rule.onNodeWithContentDescription("chart ${listState!!.firstVisibleItemIndex}").assertExists()
        return problems
    }

    private fun check(problems: MutableList<String>) = rule.runOnIdle {
        for (visible in listState!!.layoutInfo.visibleItemsInfo) {
            val index = visible.index
            val item = items[index]
            val entry = shown[index]
            val chart = entry?.chart?.get()
            val state = entry?.state?.get()
            if (chart == null || state == null) {
                problems += "$index has no chart"
                continue
            }
            if (chart.data !== item.data) problems += "$index shows data of another item"
            if (!chart.isAttachedToWindow) problems += "$index shows a detached chart"
            if (!state.isAttached) problems += "$index has a detached state"
            val selectedX = state.selectedHighlight?.x
            if (selectedX != item.highlightX) problems += "$index selected $selectedX instead of ${item.highlightX}"
        }
    }

    @Composable
    private fun ChartItem(item: Item) {
        val state = rememberChartState()
        val modifier = Modifier.fillMaxWidth().height(160.dp)
        val description = "chart ${item.index}"
        val marker: (@Composable (Entry<*>, Highlight) -> Unit)? = if (item.marker) { entry, _ -> BasicText("y ${entry.y}") } else null
        val setup: Chart<*>.() -> Unit = {
            created += WeakReference(this)
            states += WeakReference(state)
        }
        val update: Chart<*>.() -> Unit = {
            shown[item.index] = Shown(WeakReference(this), WeakReference(state))
            val x = item.highlightX
            if (x != null && highlighted.isEmpty()) highlightValue(x, 0, dataIndex = if (this is CombinedChartView) 0 else -1)
        }
        when (val data = item.data) {
            is CombinedData -> CombinedChart(data, modifier, state, description, marker, setup = setup, update = update)
            is LineData -> LineChart(data, modifier, state, description, marker, setup = setup, update = update)
            is BarData -> BarChart(data, modifier, state, description, marker, setup = setup, update = update)
            is PieData -> PieChart(data, modifier, state, description, marker, setup = setup, update = update)
        }
    }

    private companion object {
        const val COUNT = 200

        fun item(index: Int): Item {
            val random = Random(index)
            val size = 5 + random.nextInt(30)
            fun entries() = List(size) { Entry(it.toFloat(), random.nextFloat() * 100f) }
            val data: ChartData<*> = when (index % 4) {
                0 -> LineData(LineDataSet(entries(), "line $index"))
                1 -> BarData(BarDataSet(entries().map { BarEntry(it.x, it.y) }, "bar $index"))
                2 -> PieData(PieDataSet(List(3 + size % 5) { PieEntry(1f + random.nextFloat() * 10f) }, "pie $index"))
                else -> CombinedData().apply {
                    lineData = LineData(LineDataSet(entries(), "line $index"))
                    barData = BarData(BarDataSet(entries().map { BarEntry(it.x, it.y) }, "bar $index"))
                }
            }
            val highlightX = if (index % 5 == 0) (if (data is PieData) 1f else 2f) else null
            return Item(index, data, highlightX, marker = index % 3 == 0)
        }
    }
}
