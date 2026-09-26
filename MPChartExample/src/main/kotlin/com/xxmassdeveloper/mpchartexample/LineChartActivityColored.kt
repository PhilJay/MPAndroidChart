package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.xxmassdeveloper.mpchartexample.databinding.ActivityColoredLinesBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class LineChartActivityColored : DemoBase() {

    private lateinit var binding: ActivityColoredLinesBinding

    private val colors = intArrayOf(
        Color.rgb(137, 230, 81),
        Color.rgb(240, 240, 30),
        Color.rgb(89, 199, 250),
        Color.rgb(250, 104, 104)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityColoredLinesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "LineChartActivityColored"

        val charts = arrayOf(binding.chart1, binding.chart2, binding.chart3, binding.chart4)

        val tf = Typeface.createFromAsset(assets, "OpenSans-Bold.ttf")

        for (i in charts.indices) {
            val data = getData(36, 100f)
            data.setValueTypeface(tf)

            setupChart(charts[i], data, colors[i % colors.size])
        }
    }

    private fun setupChart(chart: LineChart, data: LineData, color: Int) {
        (data.getDataSetByIndex(0) as LineDataSet<*>).circleHoleColor = color

        chart.apply {
            description.isEnabled = false
            isDrawGridBackgroundEnabled = false
            isTouchEnabled = true
            isDragEnabled = true
            isScaleEnabled = true
            isPinchZoomEnabled = false
            setBackgroundColor(color)
            setViewPortOffsets(10f, 0f, 10f, 0f)
            this.data = data
            legend.isEnabled = false
            axisLeft.isEnabled = false
            axisLeft.spaceTop = 40f
            axisLeft.spaceBottom = 40f
            axisRight.isEnabled = false
            xAxis.isEnabled = false
            animateX(2500)
        }
    }

    private fun getData(count: Int, range: Float): LineData {
        val values = ArrayList<Entry<Any?>>()

        for (i in 0 until count) {
            val value = (Math.random() * range).toFloat() + 3
            values.add(Entry(i.toFloat(), value))
        }

        val set1 = LineDataSet(values, "DataSet 1").apply {
            lineWidth = 1.75f
            circleRadius = 5f
            circleHoleRadius = 2.5f
            color = Color.WHITE
            circleColor = Color.WHITE
            highlightColor = Color.WHITE
            isDrawValuesEnabled = false
        }

        return LineData(set1)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.only_github, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/LineChartActivityColored.java")
                startActivity(i)
            }
        }
        return true
    }

    override fun saveToGallery() {}
}
