package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.xxmassdeveloper.mpchartexample.databinding.ActivityLinechartNoseekbarBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class FilledLineActivity : DemoBase() {

    private lateinit var binding: ActivityLinechartNoseekbarBinding
    private val fillColor = Color.argb(150, 51, 181, 229)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinechartNoseekbarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "FilledLineActivity"

        binding.chart1.apply {
            setBackgroundColor(Color.WHITE)
            gridBackgroundColor = fillColor
            isDrawGridBackgroundEnabled = true
            isDrawBordersEnabled = true
            description.isEnabled = false
            isPinchZoomEnabled = false

            legend.isEnabled = false

            xAxis.isEnabled = false

            axisLeft.apply {
                axisMaximum = 900f
                axisMinimum = -250f
                isDrawAxisLineEnabled = false
                isDrawZeroLineEnabled = false
                isDrawGridLinesEnabled = false
            }

            axisRight.isEnabled = false
        }

        setData(100, 60f)

        binding.chart1.invalidate()
    }

    private fun setData(count: Int, range: Float) {
        val values1 = ArrayList<Entry<Any?>>()
        for (i in 0 until count) {
            val value = (Math.random() * range).toFloat() + 50
            values1.add(Entry(i.toFloat(), value))
        }

        val values2 = ArrayList<Entry<Any?>>()
        for (i in 0 until count) {
            val value = (Math.random() * range).toFloat() + 450
            values2.add(Entry(i.toFloat(), value))
        }

        val data = binding.chart1.data

        if (data != null && data.dataSetCount > 0) {
            val set1 = data.getDataSetByIndex(0) as LineDataSet<Any?>
            val set2 = data.getDataSetByIndex(1) as LineDataSet<Any?>
            set1.entries = values1
            set2.entries = values2
            data.notifyDataChanged()
            binding.chart1.notifyDataSetChanged()
        } else {
            val set1 = LineDataSet(values1, "DataSet 1").apply {
                axisDependency = YAxis.AxisDependency.LEFT
                color = Color.rgb(255, 241, 46)
                isDrawCirclesEnabled = false
                lineWidth = 2f
                circleRadius = 3f
                fillAlpha = 255
                isDrawFilledEnabled = true
                fillColor = Color.WHITE
                highlightColor = Color.rgb(244, 117, 117)
                isDrawCircleHoleEnabled = false
                fillFormatter = IFillFormatter { _, _ -> binding.chart1.axisLeft.axisMinimum }
            }

            val set2 = LineDataSet(values2, "DataSet 2").apply {
                axisDependency = YAxis.AxisDependency.LEFT
                color = Color.rgb(255, 241, 46)
                isDrawCirclesEnabled = false
                lineWidth = 2f
                circleRadius = 3f
                fillAlpha = 255
                isDrawFilledEnabled = true
                fillColor = Color.WHITE
                isDrawCircleHoleEnabled = false
                highlightColor = Color.rgb(244, 117, 117)
                fillFormatter = IFillFormatter { _, _ -> binding.chart1.axisLeft.axisMaximum }
            }

            val dataSets = ArrayList<ILineDataSet<*>>()
            dataSets.add(set1)
            dataSets.add(set2)

            val lineData = LineData(dataSets)
            lineData.setDrawValues(false)

            binding.chart1.data = lineData
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.only_github, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/FilledLineActivity.java")
                startActivity(i)
            }
        }
        return true
    }

    override fun saveToGallery() {}
}
