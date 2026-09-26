package com.github.mikephil.charting.compose

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.ref.WeakReference
import com.github.mikephil.charting.charts.LineChart as LineChartView

@RunWith(AndroidJUnit4::class)
class ChartRecreateTest {

    @get:Rule
    val rule = createEmptyComposeRule()

    @After
    fun tearDown() {
        ChartHostActivity.content = {}
    }

    @Test
    fun zoomAndSelectionSurviveRecreationAndTheOldChartIsReleased() {
        val data = LineData(LineDataSet(List(20) { Entry(it.toFloat(), (it % 7).toFloat()) }, "line"))
        val charts = mutableListOf<WeakReference<LineChartView>>()
        var state: ChartState? = null
        ChartHostActivity.content = {
            val remembered = rememberChartState()
            state = remembered
            LineChart(data, Modifier.size(300.dp), remembered, setup = { charts += WeakReference(this) })
        }

        ActivityScenario.launch(ChartHostActivity::class.java).use { scenario ->
            rule.runOnIdle {
                state!!.zoomIn()
                state!!.highlight(3f)
            }
            rule.runOnIdle { assertTrue(state!!.zoomX > 1f) }

            scenario.recreate()

            rule.runOnIdle {
                assertEquals(2, charts.size)
                assertTrue(charts[1].get()!!.isAttachedToWindow)
                assertEquals(1.4f, state!!.zoomX, 0.01f)
                assertEquals(3f, state!!.selectedEntry!!.x)
            }
            assertEquals("the chart of the destroyed activity is still reachable", 0, awaitCollected(charts.take(1)))
        }
    }
}
