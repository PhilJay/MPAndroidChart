package com.xxmassdeveloper.mpchartexample

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter

/** Mirrors the README snippets so they stop compiling when the API changes. Never executed. */
@Suppress("unused")
private object ReadmeSamplesCompile {
    class Order

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
        BarEntry(0f, listOf(3f, 5f))

        chart.apply {
            description.isEnabled = false
            legend.form = Legend.LegendForm.LINE
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisLeft.addLimitLine(LimitLine(10f, "Target"))
            isDragEnabled = true
            isScaleEnabled = true
            isPinchZoomEnabled = true
        }

        chart.xAxis.valueFormatter = IAxisValueFormatter { value, _ -> months[value.toInt()] }
        set.valueFormatter = IValueFormatter { value, _, _, _ -> "%.1f".format(value) }
        chart.onValueSelected { entry, highlight -> showDetails(entry) }
        chart.notifyDataSetChanged()
        chart.saveToGallery("chart.png")

        val typed = LineDataSet(listOf(Entry(1f, 2f, data = Order())), "typed")
        val order: Order? = typed.getEntryForXValue(1f)?.data
        println(order)
    }
}
