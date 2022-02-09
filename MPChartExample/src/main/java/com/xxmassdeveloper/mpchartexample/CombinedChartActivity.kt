package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class CombinedChartActivity : DemoBase() {
    private var chart: CombinedChart? = null
    private val count = 12
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_combined)
        title = "CombinedChartActivity"
        chart = findViewById(R.id.chart1)
        chart?.description?.isEnabled = false
        chart?.setBackgroundColor(Color.WHITE)
        chart?.setDrawGridBackground(false)
        chart?.setDrawBarShadow(false)
        chart?.isHighlightFullBarEnabled = false

        // draw bars behind lines
        chart?.drawOrder = arrayOf(
            DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.CANDLE, DrawOrder.LINE, DrawOrder.SCATTER
        )
        val l = chart?.legend
        l?.isWordWrapEnabled = true
        l?.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l?.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l?.orientation = Legend.LegendOrientation.HORIZONTAL
        l?.setDrawInside(false)
        val rightAxis = chart?.axisRight
        rightAxis?.setDrawGridLines(false)
        rightAxis?.axisMinimum = 0f // this replaces setStartAtZero(true)
        val leftAxis = chart?.axisLeft
        leftAxis?.setDrawGridLines(false)
        leftAxis?.axisMinimum = 0f // this replaces setStartAtZero(true)
        val xAxis = chart?.xAxis
        xAxis?.position = XAxisPosition.BOTH_SIDED
        xAxis?.axisMinimum = 0f
        xAxis?.granularity = 1f
        xAxis?.valueFormatter =
            IAxisValueFormatter { value, axis -> months[value.toInt() % months.size] }
        val data = CombinedData()
        data.setData(generateLineData())
        data.setData(generateBarData())
        data.setData(generateBubbleData())
        data.setData(generateScatterData())
        data.setData(generateCandleData())
        data.setValueTypeface(tfLight)
        xAxis?.axisMaximum = data.xMax + 0.25f
        chart?.data = data
        chart?.invalidate()
    }

    private fun generateLineData(): LineData {
        val d = LineData()
        val entries = ArrayList<Entry>()
        for (index in 0 until count) entries.add(Entry(index + 0.5f, getRandom(15f, 5f)))
        val set = LineDataSet(entries, "Line DataSet")
        set.color = Color.rgb(240, 238, 70)
        set.lineWidth = 2.5f
        set.setCircleColor(Color.rgb(240, 238, 70))
        set.circleRadius = 5f
        set.fillColor = Color.rgb(240, 238, 70)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.setDrawValues(true)
        set.valueTextSize = 10f
        set.valueTextColor = Color.rgb(240, 238, 70)
        set.axisDependency = AxisDependency.LEFT
        d.addDataSet(set)
        return d
    }

    private fun generateBarData(): BarData {
        val entries1 = ArrayList<BarEntry>()
        val entries2 = ArrayList<BarEntry>()
        for (index in 0 until count) {
            entries1.add(BarEntry(0f, getRandom(25f, 25f)))

            // stacked
            entries2.add(BarEntry(0f, floatArrayOf(getRandom(13f, 12f), getRandom(13f, 12f))))
        }
        val set1 = BarDataSet(entries1, "Bar 1")
        set1.color = Color.rgb(60, 220, 78)
        set1.valueTextColor = Color.rgb(60, 220, 78)
        set1.valueTextSize = 10f
        set1.axisDependency = AxisDependency.LEFT
        val set2 = BarDataSet(entries2, "")
        set2.stackLabels = arrayOf("Stack 1", "Stack 2")
        set2.setColors(Color.rgb(61, 165, 255), Color.rgb(23, 197, 255))
        set2.valueTextColor = Color.rgb(61, 165, 255)
        set2.valueTextSize = 10f
        set2.axisDependency = AxisDependency.LEFT
        val groupSpace = 0.06f
        val barSpace = 0.02f // x2 dataset
        val barWidth = 0.45f // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"
        val d = BarData(set1, set2)
        d.barWidth = barWidth

        // make this BarData object grouped
        d.groupBars(0f, groupSpace, barSpace) // start at x = 0
        return d
    }

    private fun generateScatterData(): ScatterData {
        val d = ScatterData()
        val entries = ArrayList<Entry>()
        var index = 0f
        while (index < count) {
            entries.add(Entry(index + 0.25f, getRandom(10f, 55f)))
            index += 0.5f
        }
        val set = ScatterDataSet(entries, "Scatter DataSet")
        set.setColors(*ColorTemplate.MATERIAL_COLORS)
        set.scatterShapeSize = 7.5f
        set.setDrawValues(false)
        set.valueTextSize = 10f
        d.addDataSet(set)
        return d
    }

    private fun generateCandleData(): CandleData {
        val d = CandleData()
        val entries = ArrayList<CandleEntry>()
        var index = 0
        while (index < count) {
            entries.add(CandleEntry(index + 1f, 90f, 70f, 85f, 75f))
            index += 2
        }
        val set = CandleDataSet(entries, "Candle DataSet")
        set.decreasingColor = Color.rgb(142, 150, 175)
        set.shadowColor = Color.DKGRAY
        set.barSpace = 0.3f
        set.valueTextSize = 10f
        set.setDrawValues(false)
        d.addDataSet(set)
        return d
    }

    private fun generateBubbleData(): BubbleData {
        val bd = BubbleData()
        val entries = ArrayList<BubbleEntry>()
        for (index in 0 until count) {
            val y = getRandom(10f, 105f)
            val size = getRandom(100f, 105f)
            entries.add(BubbleEntry(index + 0.5f, y, size))
        }
        val set = BubbleDataSet(entries, "Bubble DataSet")
        set.setColors(*ColorTemplate.VORDIPLOM_COLORS)
        set.valueTextSize = 10f
        set.valueTextColor = Color.WHITE
        set.highlightCircleWidth = 1.5f
        set.setDrawValues(true)
        bd.addDataSet(set)
        return bd
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.combined, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data =
                    Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/CombinedChartActivity.java")
                startActivity(i)
            }
            R.id.actionToggleLineValues -> {
                for (set in chart!!.data.dataSets) {
                    (set as? LineDataSet)?.setDrawValues(!set.isDrawValuesEnabled())
                }
                chart!!.invalidate()
            }
            R.id.actionToggleBarValues -> {
                for (set in chart!!.data.dataSets) {
                    (set as? BarDataSet)?.setDrawValues(!set.isDrawValuesEnabled())
                }
                chart!!.invalidate()
            }
            R.id.actionRemoveDataSet -> {
                val rnd = getRandom(chart!!.data.dataSetCount.toFloat(), 0f).toInt()
                chart!!.data.removeDataSet(chart!!.data.getDataSetByIndex(rnd))
                chart!!.data.notifyDataChanged()
                chart!!.notifyDataSetChanged()
                chart!!.invalidate()
            }
        }
        return true
    }

    public override fun saveToGallery() { /* Intentionally left empty */
    }
}