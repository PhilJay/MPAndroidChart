package com.xxmassdeveloper.mpchartexample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mikephil.charting.compose.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.xxmassdeveloper.mpchartexample.design.Nightfall
import com.xxmassdeveloper.mpchartexample.design.nightfallBase
import com.xxmassdeveloper.mpchartexample.design.nightfallFullWidthX
import com.xxmassdeveloper.mpchartexample.design.nightfallLine
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import kotlin.math.roundToInt
import kotlin.random.Random

/** A chart per row in a LazyColumn, with the data built once and the gestures left to the list. */
class ComposeListActivity : DemoBase() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "ComposeListActivity"
        setContent {
            MaterialTheme(colorScheme = listColors) {
                RegionList()
            }
        }
    }

    override fun saveToGallery() {}
}

private val theme = Nightfall.light

private val listColors = lightColorScheme(
    primary = Color(Nightfall.accent),
    background = Color(theme.stage),
    surface = Color(theme.card),
    onSurface = Color(theme.text),
    onSurfaceVariant = Color(theme.muted),
)

private class Region(val name: String, val data: LineData, val change: Int)

@Composable
private fun RegionList() {
    // Built once and remembered, so scrolling never rebuilds a data set.
    val regions = remember { regions() }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(theme.stage)).safeDrawingPadding(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        items(regions, key = { it.name }) { region ->
            RegionCard(region)
        }
    }
}

@Composable
private fun RegionCard(region: Region) {
    Surface(color = Color(theme.card), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(region.name, color = Color(theme.text), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(
                if (region.change >= 0) "+${region.change}% this quarter" else "${region.change}% this quarter",
                color = Color(theme.muted),
                fontSize = 13.sp,
            )
            LineChart(
                data = region.data,
                // A fixed height keeps the row measurable while the list is scrolling.
                modifier = Modifier.fillMaxWidth().height(120.dp).padding(top = 10.dp),
                contentDescription = "${region.name}, ${region.change} percent this quarter",
                setup = {
                    nightfallBase(theme)
                    nightfallFullWidthX(region.data.entryCount)
                    axisLeft.isEnabled = false
                    axisRight.isEnabled = false
                    xAxis.isEnabled = false
                    legend.isEnabled = false
                    // Leave the vertical gesture to the list; a tap still selects a point.
                    isDragEnabled = false
                    isScaleEnabled = false
                    isHighlightPerDragEnabled = false
                },
            )
        }
    }
}

private fun regions(): List<Region> {
    val random = Random(7)
    val names = listOf(
        "Berlin", "Hamburg", "Munich", "Cologne", "Frankfurt", "Stuttgart", "Leipzig", "Dresden",
        "Hanover", "Nuremberg", "Bremen", "Essen", "Dortmund", "Bonn", "Mannheim", "Karlsruhe",
        "Wiesbaden", "Augsburg", "Kiel", "Freiburg",
    )
    return names.map { name ->
        var value = random.nextInt(40, 80).toFloat()
        val entries = (0 until 24).map { index ->
            value = (value + random.nextInt(-8, 11)).coerceIn(12f, 100f)
            Entry(index.toFloat(), value)
        }
        val color = Nightfall.palette[names.indexOf(name) % Nightfall.palette.size]
        val set = LineDataSet(entries, name).apply {
            nightfallLine(theme, color)
            isDrawCirclesEnabled = false
            isDrawValuesEnabled = false
        }
        val change = ((entries.last().y - entries.first().y) / entries.first().y * 100).roundToInt()
        Region(name, LineData(set), change)
    }
}
