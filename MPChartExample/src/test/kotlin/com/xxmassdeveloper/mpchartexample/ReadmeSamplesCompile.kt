package com.xxmassdeveloper.mpchartexample

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.compose.rememberChartState
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.Fill
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import com.github.mikephil.charting.compose.LineChart as LineChartComposable

/** Mirrors the README and MIGRATION snippets so they stop compiling when the API changes. Never executed. */
@Suppress("unused", "UNUSED_VARIABLE", "UNUSED_ANONYMOUS_PARAMETER")
private object ReadmeSamplesCompile {
    class Order

    @Composable
    fun compose(lineData: LineData) {
        val state = rememberChartState()
        LaunchedEffect(lineData) { state.animateX(500) }

        LineChartComposable(
            data = lineData,
            modifier = Modifier.fillMaxWidth().height(300.dp),
            state = state,
            marker = { entry, _ -> Text("${entry.y}") },
            setup = {
                description.isEnabled = false
                axisRight.isEnabled = false
            },
        )
        val selected: Entry<*>? = state.selectedEntry
        val range = state.lowestVisibleX..state.highestVisibleX
        val zoom = state.zoomX
        state.highlight(1f)
        state.zoomIn()
        state.fitScreen()
        state.moveViewToX(0f)
        state.animateY(500)
    }

    fun views(chart: LineChart, months: List<String>, showDetails: (Entry<*>) -> Unit) {
        val entries = listOf(Entry(0f, 4f), Entry(1f, 8f), Entry(2f, 6f))
        val set = LineDataSet(entries, "Sales").apply {
            color = Color.BLUE
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            isDrawValuesEnabled = false
        }
        chart.data = LineData(set)
        chart.animateX(500)

        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisRight.isEnabled = false
        chart.xAxis.valueFormatter = IAxisValueFormatter { value, _ -> months[value.toInt()] }
        chart.onValueSelected { entry, _ -> showDetails(entry) }

        chart.apply {
            description.isEnabled = false
            legend.form = Legend.LegendForm.LINE
            xAxis.granularity = 1f
            axisLeft.axisMinimum = 0f
            axisLeft.addLimitLine(LimitLine(10f, "Target"))
            isScaleEnabled = true
            isPinchZoomEnabled = true
        }
        set.valueFormatter = IValueFormatter { value, _, _, _ -> "%.1f".format(value) }
        chart.notifyDataSetChanged()

        val order = Order()
        val typedEntry: Entry<Order> = Entry(1f, 2f, data = order)
        val typed = LineDataSet(listOf(typedEntry), "typed")
        val payload: Order? = typed.getEntryForXValue(1f)?.data
    }

    fun migration(
        chart: LineChart,
        barChart: BarChart,
        pieChart: PieChart,
        set: LineDataSet<*>,
        barSet: BarDataSet<*>,
        pieSet: PieDataSet<*>,
        order: Order,
    ) {
        chart.isDragEnabled = true
        val max: Float = chart.axisLeft.axisMaximum
        set.isDrawValuesEnabled = false
        set.isVerticalHighlightIndicatorEnabled = false

        val setEntries = set.entries
        set.highlightColor = Color.RED
        barSet.highlightAlpha = 100
        val bitmap = chart.toBitmap()
        val touchListener = chart.chartTouchListener
        pieSet.isUseValueColorForLineEnabled = true

        chart.xAxis.valueFormatter = IAxisValueFormatter { value, axis -> "$value" }
        set.valueFormatter = IValueFormatter { value, entry, dataSetIndex, viewPortHandler -> "$value" }

        barSet.fills = listOf(Fill(Color.RED, Color.BLUE))
        barSet.setGradientColor(Color.RED, Color.BLUE)
        chart.highlightValue(1f, 0, callListener = false)

        val plain: Entry<Nothing> = Entry(1f, 2f)
        val typed: Entry<Order> = Entry(1f, 2f, data = order)
        val stacked = BarEntry(1f, listOf(1f, 2f))
        val stackValues: List<Float>? = stacked.stackValues

        set.colors = ColorTemplate.MATERIAL_COLORS

        chart.xAxis.labelCount = 5
        chart.xAxis.isForceLabelsEnabled = true
        chart.legend.entries = listOf(LegendEntry("Sales", formColor = Color.BLUE))

        chart.notifyDataSetChanged()
        val zoom: Float = chart.zoomX

        chart.infoPaint.color = Color.GRAY
        chart.descriptionPaint.color = Color.GRAY
        chart.gridBackgroundPaint.color = Color.WHITE
        chart.legendLabelPaint.color = Color.BLACK
        pieChart.holePaint.color = Color.WHITE
        pieChart.centerTextPaint.color = Color.BLACK

        chart.onValueSelected { entry, highlight -> }
        barChart.highlightValue(1f, 0, stackIndex = 1)

        chart.saveToGallery("chart")
        chart.maxVisibleCount = 50
        chart.xAxis.labelRotationAngle = -45f
    }

    class DenseXAxisRenderer(viewPortHandler: ViewPortHandler, xAxis: XAxis, transformer: Transformer?) :
        XAxisRenderer(viewPortHandler, xAxis, transformer) {
        override fun minimumInterval(range: Double): Double = 0.0
    }

    val stackedFormatter = object : IValueFormatter {
        override fun getFormattedValue(value: Float, entry: Entry<*>, dataSetIndex: Int, viewPortHandler: ViewPortHandler) = "$value"

        override fun getStackedFormattedValue(value: Float, stackIndex: Int, entry: Entry<*>, dataSetIndex: Int, viewPortHandler: ViewPortHandler) =
            if (stackIndex == 0) "$value" else ""
    }
}
