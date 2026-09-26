package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.xxmassdeveloper.mpchartexample.custom.RadarMarkerView
import com.xxmassdeveloper.mpchartexample.databinding.ActivityRadarchartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class RadarChartActivity : DemoBase() {

    private lateinit var binding: ActivityRadarchartBinding

    private val activities = arrayOf("Burger", "Steak", "Salad", "Pasta", "Pizza")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRadarchartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "RadarChartActivity"

        val chart = binding.chart1
        chart.setBackgroundColor(Color.rgb(60, 65, 82))

        chart.description.isEnabled = false

        chart.webLineWidth = 1f
        chart.webColor = Color.LTGRAY
        chart.webLineWidthInner = 1f
        chart.webColorInner = Color.LTGRAY
        chart.webAlpha = 100

        val mv = RadarMarkerView(this, R.layout.radar_markerview)
        chart.marker = mv

        setData()

        chart.animateXY(1400, 1400, Easing.EaseInOutQuad)

        chart.xAxis.apply {
            typeface = tfLight
            textSize = 9f
            yOffset = 0f
            xOffset = 0f
            valueFormatter = IAxisValueFormatter { value, _ -> activities[value.toInt() % activities.size] }
            textColor = Color.WHITE
        }

        chart.yAxis.apply {
            typeface = tfLight
            labelCount = 5
            textSize = 9f
            axisMinimum = 0f
            axisMaximum = 80f
            isDrawLabelsEnabled = false
        }

        chart.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL
            isDrawInsideEnabled = false
            typeface = tfLight
            xEntrySpace = 7f
            yEntrySpace = 5f
            textColor = Color.WHITE
        }
    }

    private fun setData() {
        val mul = 80f
        val min = 20f
        val cnt = 5

        val entries1 = ArrayList<RadarEntry<Any?>>()
        val entries2 = ArrayList<RadarEntry<Any?>>()

        for (i in 0 until cnt) {
            val val1 = (Math.random() * mul).toFloat() + min
            entries1.add(RadarEntry(val1))

            val val2 = (Math.random() * mul).toFloat() + min
            entries2.add(RadarEntry(val2))
        }

        val set1 = RadarDataSet(entries1, "Last Week")
        set1.color = Color.rgb(103, 110, 129)
        set1.fillColor = Color.rgb(103, 110, 129)
        set1.isDrawFilledEnabled = true
        set1.fillAlpha = 180
        set1.lineWidth = 2f
        set1.isDrawHighlightCircleEnabled = true
        set1.setDrawHighlightIndicators(false)

        val set2 = RadarDataSet(entries2, "This Week")
        set2.color = Color.rgb(121, 162, 175)
        set2.fillColor = Color.rgb(121, 162, 175)
        set2.isDrawFilledEnabled = true
        set2.fillAlpha = 180
        set2.lineWidth = 2f
        set2.isDrawHighlightCircleEnabled = true
        set2.setDrawHighlightIndicators(false)

        val sets = ArrayList<IRadarDataSet<*>>()
        sets.add(set1)
        sets.add(set2)

        val data = RadarData(sets)
        data.setValueTypeface(tfLight)
        data.setValueTextSize(8f)
        data.setDrawValues(false)
        data.setValueTextColor(Color.WHITE)

        binding.chart1.data = data
        binding.chart1.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.radar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val chart = binding.chart1
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/RadarChartActivity.java")
                startActivity(i)
            }
            R.id.actionToggleValues -> {
                chart.data?.dataSets?.forEach { it.isDrawValuesEnabled = !it.isDrawValuesEnabled }
                chart.invalidate()
            }
            R.id.actionToggleHighlight -> {
                chart.data?.let {
                    it.isHighlightEnabled = !it.isHighlightEnabled
                    chart.invalidate()
                }
            }
            R.id.actionToggleRotate -> {
                chart.isRotationEnabled = !chart.isRotationEnabled
                chart.invalidate()
            }
            R.id.actionToggleFilled -> {
                chart.data?.dataSets?.forEach { it.isDrawFilledEnabled = !it.isDrawFilledEnabled }
                chart.invalidate()
            }
            R.id.actionToggleHighlightCircle -> {
                chart.data?.dataSets?.forEach { it.isDrawHighlightCircleEnabled = !it.isDrawHighlightCircleEnabled }
                chart.invalidate()
            }
            R.id.actionToggleXLabels -> {
                chart.xAxis.isEnabled = !chart.xAxis.isEnabled
                chart.notifyDataSetChanged()
                chart.invalidate()
            }
            R.id.actionToggleYLabels -> {
                chart.yAxis.isEnabled = !chart.yAxis.isEnabled
                chart.invalidate()
            }
            R.id.animateX -> chart.animateX(1400)
            R.id.animateY -> chart.animateY(1400)
            R.id.animateXY -> chart.animateXY(1400, 1400)
            R.id.actionToggleSpin -> chart.spin(2000, chart.rotationAngle, chart.rotationAngle + 360, Easing.EaseInOutCubic)
            R.id.actionSave -> saveChartToGalleryWithPermission(chart)
        }
        return true
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "RadarChartActivity")
}
