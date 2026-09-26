package com.github.mikephil.charting.compose

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicInteger
import com.github.mikephil.charting.charts.LineChart as LineChartView

@RunWith(AndroidJUnit4::class)
class ChartComposableTest {

    @get:Rule
    val rule = createComposeRule()

    private val size = Modifier.size(200.dp)

    private fun lineData(vararg values: Float) =
        LineData(LineDataSet(values.mapIndexed { index, y -> Entry(index.toFloat(), y) }, "line"))

    @Test
    fun stateDoesNothingBeforeTheChartExistsAndAfterItLeaves() {
        val state = ChartState()
        val data = lineData(1f, 2f, 3f)
        var show by mutableStateOf(false)
        var chart: LineChartView? = null

        state.highlight(1f)
        state.zoomIn()
        state.animateX(100)
        state.notifyDataChanged()
        assertFalse(state.isAttached)

        rule.setContent {
            if (show) LineChart(data, size, state = state, setup = { chart = this })
        }
        rule.runOnIdle { show = true }
        rule.runOnIdle {
            assertTrue(state.isAttached)
            state.highlight(1f)
            assertEquals(2f, state.selectedEntry!!.y)
        }

        rule.runOnIdle { show = false }
        rule.runOnIdle {
            assertFalse(state.isAttached)
            assertTrue(chart!!.valueSelectedListeners.isEmpty())
            assertTrue(chart!!.gestureListeners.isEmpty())
            state.highlight(2f)
            state.fitScreen()
            state.clearHighlight()
        }
    }

    @Test
    fun newDataIsAppliedAndTheSelectionFollowsIt() {
        val state = ChartState()
        val first = lineData(1f, 2f, 3f)
        val second = lineData(5f, 6f, 7f)
        var data by mutableStateOf(first)
        var chart: LineChartView? = null

        rule.setContent { LineChart(data, size, state = state, setup = { chart = this }) }
        rule.runOnIdle { state.highlight(2f) }
        rule.runOnIdle { data = second }
        rule.runOnIdle {
            assertSame(second, chart!!.data)
            assertSame(second.getDataSetByIndex(0)!!.getEntryForIndex(2), state.selectedEntry)
        }
    }

    @Test
    fun changedDataAnimatesToItsOwnValues() {
        val state = ChartState()
        val second = lineData(5f, 6f, 7f, 8f)
        val third = lineData(1f)
        var data by mutableStateOf(lineData(1f, 2f, 3f))
        var chart: LineChartView? = null
        val ends = AtomicInteger()

        rule.setContent { LineChart(data, size, animateChanges = true, state = state, setup = { chart = this }) }
        rule.runOnIdle { data = second }
        rule.waitUntil(5_000) { !isAnimating(chart!!) }
        rule.runOnIdle {
            assertSame(second, chart!!.data)
            assertEquals(listOf(5f, 6f, 7f, 8f), List(4) { second.getDataSetByIndex(0)!!.getEntryForIndex(it).y })
            state.animateDataChange(third, 100) { ends.incrementAndGet() }
        }
        rule.waitUntil(5_000) { ends.get() == 1 }
        rule.runOnIdle {
            assertSame(third, chart!!.data)
            assertEquals(1f, third.getDataSetByIndex(0)!!.getEntryForIndex(0).y)
        }
    }

    private fun isAnimating(chart: LineChartView): Boolean {
        var animating = true
        rule.runOnUiThread { animating = chart.isAnimating }
        return animating
    }

    @Test
    fun aChartWithoutDataShowsTheLoadingStateFromUpdate() {
        var loading by mutableStateOf(true)
        var chart: LineChartView? = null
        rule.setContent {
            LineChart(data = null, modifier = size, setup = { chart = this }, update = { isLoading = loading })
        }
        rule.runOnIdle {
            assertTrue(chart!!.isLoading)
            assertTrue(chart!!.isEmpty)
        }
        rule.runOnIdle { loading = false }
        rule.runOnIdle { assertFalse(chart!!.isLoading) }
    }

    @Test
    fun stateMovesToTheNextChart() {
        val state = ChartState()
        val lineData = lineData(1f, 2f, 3f)
        val barData = BarData(BarDataSet(listOf(BarEntry(0f, 4f), BarEntry(1f, 5f)), "bar"))
        var showBar by mutableStateOf(false)

        rule.setContent {
            if (showBar) BarChart(barData, size, state = state) else LineChart(lineData, size, state = state)
        }
        rule.runOnIdle { showBar = true }
        rule.runOnIdle {
            assertTrue(state.isAttached)
            state.highlight(1f)
            assertTrue(state.selectedEntry is BarEntry)
        }
    }

    @Test
    fun zoomAndSelectionSurviveStateRestoration() {
        val tester = StateRestorationTester(rule)
        val data = lineData(1f, 2f, 3f, 4f, 5f)
        var state: ChartState? = null

        tester.setContent {
            val remembered = rememberChartState()
            state = remembered
            LineChart(data, size, state = remembered)
        }
        rule.runOnIdle {
            state!!.zoomIn()
            state!!.highlight(3f)
        }
        rule.runOnIdle { assertTrue(state!!.zoomX > 1f) }

        tester.emulateSavedInstanceStateRestore()

        rule.runOnIdle {
            assertEquals(1.4f, state!!.zoomX, 0.01f)
            assertEquals(3f, state!!.selectedEntry!!.x)
        }
    }

    @Test
    fun yScrollSurvivesStateRestoration() {
        val tester = StateRestorationTester(rule)
        val data = lineData(1f, 2f, 3f, 4f, 5f)
        var chart: LineChartView? = null

        tester.setContent {
            LineChart(data, size, state = rememberChartState(), setup = { chart = this })
        }
        rule.runOnIdle {
            chart!!.zoom(2f, 2f, 0f, 0f)
            chart!!.moveViewTo(1f, 4f, AxisDependency.LEFT)
        }
        var transY = 0f
        rule.runOnIdle {
            transY = chart!!.viewPortHandler.transY
            assertTrue(transY != 0f)
        }

        tester.emulateSavedInstanceStateRestore()

        rule.runOnIdle { assertEquals(transY, chart!!.viewPortHandler.transY, 1f) }
    }

    @Test
    fun highlightSelectsInsideCombinedData() {
        val state = ChartState()
        val barData = BarData(BarDataSet(listOf(BarEntry(0f, 4f), BarEntry(1f, 5f)), "bar"))
        val line = lineData(1f, 2f)
        val data = CombinedData().apply {
            lineData = line
            this.barData = barData
        }

        rule.setContent { CombinedChart(data, size, state = state) }
        rule.runOnIdle {
            state.highlight(1f, dataSetIndex = 0, dataIndex = data.getDataIndex(barData))
            assertEquals(5f, state.selectedEntry!!.y)
            assertTrue(state.selectedEntry is BarEntry)
        }
    }

    @Test
    fun markerShowsTheLatestContent() {
        val state = ChartState()
        val data = lineData(1f, 2f, 3f)
        var label by mutableStateOf("first")

        rule.setContent {
            val text = label
            LineChart(data, size, state = state, marker = { _, _ -> BasicText(text) })
        }
        rule.runOnIdle { state.highlight(1f) }
        rule.onNodeWithText("first").assertExists()

        rule.runOnIdle { label = "second" }
        rule.onNodeWithText("second").assertExists()
    }
}
