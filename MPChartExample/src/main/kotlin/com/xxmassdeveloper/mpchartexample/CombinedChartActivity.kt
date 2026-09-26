package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.databinding.ActivityCombinedBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class CombinedChartActivity : DemoBase() {

    private lateinit var binding: ActivityCombinedBinding
    private val count = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCombinedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "CombinedChartActivity"

        val chart = binding.chart1
        chart.description.isEnabled = false
        chart.setBackgroundColor(Color.WHITE)
        chart.isDrawGridBackgroundEnabled = false
        chart.isDrawBarShadowEnabled = false
        chart.isHighlightFullBarEnabled = false

        chart.drawOrder = listOf(
            CombinedChart.DrawOrder.BAR,
            CombinedChart.DrawOrder.BUBBLE,
            CombinedChart.DrawOrder.CANDLE,
            CombinedChart.DrawOrder.LINE,
            CombinedChart.DrawOrder.SCATTER
        )

        chart.legend.apply {
            isWordWrapEnabled = true
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL
            isDrawInsideEnabled = false
        }

        chart.axisRight.apply {
            isDrawGridLinesEnabled = false
            axisMinimum = 0f
        }

        chart.axisLeft.apply {
            isDrawGridLinesEnabled = false
            axisMinimum = 0f
        }

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
        xAxis.axisMinimum = 0f
        xAxis.granularity = 1f
        xAxis.valueFormatter = IAxisValueFormatter { value, _ -> months[value.toInt() % months.size] }

        val data = CombinedData()

        data.lineData = generateLineData()
        data.barData = generateBarData()
        data.bubbleData = generateBubbleData()
        data.scatterData = generateScatterData()
        data.candleData = generateCandleData()
        data.setValueTypeface(tfLight)

        xAxis.axisMaximum = data.xMax + 0.25f

        chart.data = data
        chart.invalidate()
    }

    private fun generateLineData(): LineData {
        val d = LineData()

        val entries = ArrayList<Entry<Any?>>()

        for (index in 0 until count) {
            entries.add(Entry(index + 0.5f, getRandom(15f, 5f)))
        }

        val set = LineDataSet(entries, "Line DataSet")
        set.color = Color.rgb(240, 238, 70)
        set.lineWidth = 2.5f
        set.circleColor = Color.rgb(240, 238, 70)
        set.circleRadius = 5f
        set.fillColor = Color.rgb(240, 238, 70)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.isDrawValuesEnabled = true
        set.valueTextSize = 10f
        set.valueTextColor = Color.rgb(240, 238, 70)

        set.axisDependency = YAxis.AxisDependency.LEFT
        d.addDataSet(set)

        return d
    }

    private fun generateBarData(): BarData {
        val entries1 = ArrayList<BarEntry<Any?>>()
        val entries2 = ArrayList<BarEntry<Any?>>()

        for (index in 0 until count) {
            entries1.add(BarEntry(0f, getRandom(25f, 25f)))

            entries2.add(BarEntry(0f, listOf(getRandom(13f, 12f), getRandom(13f, 12f))))
        }

        val set1 = BarDataSet(entries1, "Bar 1")
        set1.color = Color.rgb(60, 220, 78)
        set1.valueTextColor = Color.rgb(60, 220, 78)
        set1.valueTextSize = 10f
        set1.axisDependency = YAxis.AxisDependency.LEFT

        val set2 = BarDataSet(entries2, "")
        set2.stackLabels = listOf("Stack 1", "Stack 2")
        set2.setColors(Color.rgb(61, 165, 255), Color.rgb(23, 197, 255))
        set2.valueTextColor = Color.rgb(61, 165, 255)
        set2.valueTextSize = 10f
        set2.axisDependency = YAxis.AxisDependency.LEFT

        val groupSpace = 0.06f
        val barSpace = 0.02f
        val barWidth = 0.45f

        val d = BarData(set1, set2)
        d.barWidth = barWidth

        d.groupBars(0f, groupSpace, barSpace)

        return d
    }

    private fun generateScatterData(): ScatterData {
        val d = ScatterData()

        val entries = ArrayList<Entry<Any?>>()

        var index = 0f
        while (index < count) {
            entries.add(Entry(index + 0.25f, getRandom(10f, 55f)))
            index += 0.5f
        }

        val set = ScatterDataSet(entries, "Scatter DataSet")
        set.colors = ColorTemplate.MATERIAL_COLORS
        set.scatterShapeSize = 3.75f
        set.isDrawValuesEnabled = false
        set.valueTextSize = 10f
        d.addDataSet(set)

        return d
    }

    private fun generateCandleData(): CandleData {
        val d = CandleData()

        val entries = ArrayList<CandleEntry<Any?>>()

        for (index in 0 until count step 2) {
            entries.add(CandleEntry(index + 1f, 90f, 70f, 85f, 75f))
        }

        val set = CandleDataSet(entries, "Candle DataSet")
        set.decreasingColor = Color.rgb(142, 150, 175)
        set.shadowColor = Color.DKGRAY
        set.barSpace = 0.3f
        set.valueTextSize = 10f
        set.isDrawValuesEnabled = false
        d.addDataSet(set)

        return d
    }

    private fun generateBubbleData(): BubbleData {
        val bd = BubbleData()

        val entries = ArrayList<BubbleEntry<Any?>>()

        for (index in 0 until count) {
            val y = getRandom(10f, 105f)
            val size = getRandom(100f, 105f)
            entries.add(BubbleEntry(index + 0.5f, y, size))
        }

        val set = BubbleDataSet(entries, "Bubble DataSet")
        set.colors = ColorTemplate.VORDIPLOM_COLORS
        set.valueTextSize = 10f
        set.valueTextColor = Color.WHITE
        set.highlightCircleWidth = 1.5f
        set.isDrawValuesEnabled = true
        bd.addDataSet(set)

        return bd
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.combined, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val chart = binding.chart1
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/CombinedChartActivity.java")
                startActivity(i)
            }
            R.id.actionToggleLineValues -> {
                chart.data?.dataSets?.forEach {
                    if (it is LineDataSet<*>) it.isDrawValuesEnabled = !it.isDrawValuesEnabled
                }
                chart.invalidate()
            }
            R.id.actionToggleBarValues -> {
                chart.data?.dataSets?.forEach {
                    if (it is BarDataSet<*>) it.isDrawValuesEnabled = !it.isDrawValuesEnabled
                }
                chart.invalidate()
            }
            R.id.actionRemoveDataSet -> {
                chart.data?.let { data ->
                    val rnd = getRandom(data.dataSetCount.toFloat(), 0f).toInt()
                    data.removeDataSet(data.getDataSetByIndex(rnd))
                }
                chart.notifyDataSetChanged()
                chart.invalidate()
            }
        }
        return true
    }

    override fun saveToGallery() {}
}
