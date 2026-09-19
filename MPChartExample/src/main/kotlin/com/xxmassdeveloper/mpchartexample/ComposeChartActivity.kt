package com.xxmassdeveloper.mpchartexample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mikephil.charting.compose.BarChart
import com.github.mikephil.charting.compose.LineChart
import com.github.mikephil.charting.compose.PieChart
import com.github.mikephil.charting.compose.rememberChartState
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.xxmassdeveloper.mpchartexample.design.Nightfall
import com.xxmassdeveloper.mpchartexample.design.nightfallBars
import com.xxmassdeveloper.mpchartexample.design.nightfallBase
import com.xxmassdeveloper.mpchartexample.design.nightfallDonut
import com.xxmassdeveloper.mpchartexample.design.nightfallLine
import com.xxmassdeveloper.mpchartexample.design.nightfallSlices
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import kotlin.math.roundToInt
import kotlin.random.Random

class ComposeChartActivity : DemoBase() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "ComposeChartActivity"
        setContent {
            MaterialTheme(colorScheme = nightfallColors) {
                ComposeChartScreen()
            }
        }
    }

    override fun saveToGallery() {}
}

private val theme = Nightfall.light

private val nightfallColors = lightColorScheme(
    primary = Color(Nightfall.accent),
    onPrimary = Color(0xFF06181A),
    background = Color(theme.stage),
    surface = Color(theme.card),
    onSurface = Color(theme.text),
    onSurfaceVariant = Color(theme.muted),
)

private val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

@Composable
private fun ComposeChartScreen() {
    var seed by remember { mutableStateOf(0) }
    val lineSet = remember(seed) { revenueSet(seed) }
    val lineData = remember(lineSet) { LineData(lineSet) }
    var pointCount by remember(lineSet) { mutableIntStateOf(lineSet.entryCount) }
    val pieData = remember(seed) { platformShare(seed) }
    val barData = remember(seed) { ordersPerQuarter(seed) }
    val lineState = rememberChartState()
    LaunchedEffect(lineData) { lineState.animateX(600) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(theme.stage)).safeDrawingPadding().verticalScroll(rememberScrollState()).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ChartCard("Revenue", "Driven by Compose state") {
            LineChart(
                data = lineData,
                modifier = Modifier.fillMaxWidth().height(220.dp),
                state = lineState,
                contentDescription = "Revenue line chart",
                marker = { entry, _ -> Pill("${months[entry.x.toInt() % 12]} · ${entry.y.roundToInt()}") },
                setup = {
                    nightfallBase(theme)
                    xAxis.valueFormatter = IAxisValueFormatter { value, _ -> months[value.toInt() % 12] }
                    xAxis.axisMinimum = -0.5f
                    xAxis.granularity = 1f
                    axisLeft.axisMinimum = 0f
                    axisLeft.axisMaximum = 100f
                    axisLeft.labelCount = 5
                    axisLeft.isForceLabelsEnabled = true
                },
                update = {
                    // The data changed in place, so the chart has to recompute its axes.
                    xAxis.axisMaximum = pointCount - 0.5f
                    xAxis.labelCount = pointCount.coerceAtMost(12)
                    notifyDataSetChanged()
                },
            )
            val selected = lineState.selectedEntry
            Text(
                if (selected == null) "Tap a point to select it" else "Selected ${months[selected.x.toInt() % 12]}: ${selected.y.roundToInt()}",
                color = Color(theme.muted), fontSize = 13.sp,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        lineSet.addEntry(Entry(pointCount.toFloat(), nextRevenue(lineSet.entries.last().y)))
                        pointCount++
                        lineState.highlight(pointCount - 1f)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(Nightfall.accent)),
                ) { Text("Add point") }
                Button(
                    onClick = { seed++ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(Nightfall.violet)),
                ) { Text("New data") }
            }
        }
        ChartCard("Sessions by platform", "Donut with a legend") {
            PieChart(
                data = pieData,
                modifier = Modifier.fillMaxWidth().height(220.dp),
                contentDescription = "Sessions pie chart",
                setup = { nightfallDonut(theme) },
            )
        }
        ChartCard("Orders per quarter", "Rounded bars with a gradient") {
            BarChart(
                data = barData,
                modifier = Modifier.fillMaxWidth().height(220.dp),
                contentDescription = "Orders bar chart",
                marker = { entry, _ -> Pill(entry.y.roundToInt().toString()) },
                setup = {
                    nightfallBase(theme)
                    xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Q1", "Q2", "Q3", "Q4", "Q1", "Q2", "Q3", "Q4"))
                    axisLeft.axisMinimum = 0f
                    axisLeft.axisMaximum = 100f
                    axisLeft.labelCount = 5
                    axisLeft.isForceLabelsEnabled = true
                },
                update = { highlightValue(5f, 0) },
            )
        }
    }
}

@Composable
private fun ChartCard(title: String, subtitle: String, content: @Composable () -> Unit) {
    Surface(color = Color(theme.card), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = Color(theme.text), fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(subtitle, color = Color(theme.muted), fontSize = 12.sp)
            content()
        }
    }
}

@Composable
private fun Pill(text: String) {
    Surface(color = Color(theme.pillBackground), shape = RoundedCornerShape(8.dp)) {
        Text(text, color = Color(theme.pillText), fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun ComposeChartScreenPreview() {
    MaterialTheme(colorScheme = nightfallColors) {
        ComposeChartScreen()
    }
}

private fun nextRevenue(previous: Float): Float = (previous + Random.nextInt(-9, 14)).coerceIn(18f, 92f)

private fun revenueSet(seed: Int): LineDataSet<Nothing> {
    val random = Random(seed)
    var value = 28f
    val entries = List(12) { i ->
        value = (value + random.nextInt(-5, 15)).coerceIn(18f, 92f)
        Entry(i.toFloat(), value)
    }
    return LineDataSet(entries, "Revenue").apply { nightfallLine(theme) }
}

private fun ordersPerQuarter(seed: Int): BarData {
    val random = Random(seed)
    val entries = List(8) { BarEntry(it.toFloat(), random.nextInt(30, 90).toFloat()) }
    val set = BarDataSet(entries, "Orders").apply { nightfallBars(selected = 5) }
    return BarData(set).apply { barWidth = 0.55f }
}

private fun platformShare(seed: Int): PieData {
    val random = Random(seed)
    val entries = listOf("Mobile", "Web", "Desktop", "Other").map { PieEntry(random.nextInt(10, 50).toFloat(), it) }
    val set = PieDataSet(entries, "").apply { nightfallSlices() }
    return PieData(set)
}
