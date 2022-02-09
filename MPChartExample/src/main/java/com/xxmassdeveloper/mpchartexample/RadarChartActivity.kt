package com.xxmassdeveloper.mpchartexample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.xxmassdeveloper.mpchartexample.custom.RadarMarkerView
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class RadarChartActivity : DemoBase() {
    private var chart: RadarChart? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_radarchart)
        title = "RadarChartActivity"
        chart = findViewById(R.id.chart1)
        chart?.setBackgroundColor(Color.rgb(60, 65, 82))
        chart?.description?.isEnabled = false
        chart?.webLineWidth = 1f
        chart?.webColor = Color.LTGRAY
        chart?.webLineWidthInner = 1f
        chart?.webColorInner = Color.LTGRAY
        chart?.webAlpha = 100

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        val mv: MarkerView = RadarMarkerView(this, R.layout.radar_markerview)
        mv.chartView = chart // For bounds control
        chart?.marker = mv // Set the marker to the chart
        setData()
        chart?.animateXY(1400, 1400, Easing.EaseInOutQuad)
        val xAxis = chart?.xAxis
        xAxis?.typeface = tfLight
        xAxis?.textSize = 9f
        xAxis?.yOffset = 0f
        xAxis?.xOffset = 0f
        xAxis?.valueFormatter = object : IAxisValueFormatter {
            private val mActivities = arrayOf("Burger", "Steak", "Salad", "Pasta", "Pizza")
            override fun getFormattedValue(value: Float, axis: AxisBase): String {
                return mActivities[value.toInt() % mActivities.size]
            }
        }
        xAxis?.textColor = Color.WHITE
        val yAxis = chart?.yAxis
        yAxis?.typeface = tfLight
        yAxis?.setLabelCount(5, false)
        yAxis?.textSize = 9f
        yAxis?.axisMinimum = 0f
        yAxis?.axisMaximum = 80f
        yAxis?.setDrawLabels(false)
        val l = chart?.legend
        l?.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l?.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l?.orientation = Legend.LegendOrientation.HORIZONTAL
        l?.setDrawInside(false)
        l?.typeface = tfLight
        l?.xEntrySpace = 7f
        l?.yEntrySpace = 5f
        l?.textColor = Color.WHITE
    }

    private fun setData() {
        val mul = 80f
        val min = 20f
        val cnt = 5
        val entries1 = ArrayList<RadarEntry>()
        val entries2 = ArrayList<RadarEntry>()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (i in 0 until cnt) {
            val val1 = (Math.random() * mul).toFloat() + min
            entries1.add(RadarEntry(val1))
            val val2 = (Math.random() * mul).toFloat() + min
            entries2.add(RadarEntry(val2))
        }
        val set1 = RadarDataSet(entries1, "Last Week")
        set1.color = Color.rgb(103, 110, 129)
        set1.fillColor = Color.rgb(103, 110, 129)
        set1.setDrawFilled(true)
        set1.fillAlpha = 180
        set1.lineWidth = 2f
        set1.isDrawHighlightCircleEnabled = true
        set1.setDrawHighlightIndicators(false)
        val set2 = RadarDataSet(entries2, "This Week")
        set2.color = Color.rgb(121, 162, 175)
        set2.fillColor = Color.rgb(121, 162, 175)
        set2.setDrawFilled(true)
        set2.fillAlpha = 180
        set2.lineWidth = 2f
        set2.isDrawHighlightCircleEnabled = true
        set2.setDrawHighlightIndicators(false)
        val sets = ArrayList<IRadarDataSet>()
        sets.add(set1)
        sets.add(set2)
        val data = RadarData(sets)
        data.setValueTypeface(tfLight)
        data.setValueTextSize(8f)
        data.setDrawValues(false)
        data.setValueTextColor(Color.WHITE)
        chart!!.data = data
        chart!!.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.radar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data =
                    Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/RadarChartActivity.java")
                startActivity(i)
            }
            R.id.actionToggleValues -> {
                for (set in chart!!.data.dataSets) set.setDrawValues(!set.isDrawValuesEnabled)
                chart!!.invalidate()
            }
            R.id.actionToggleHighlight -> {
                if (chart!!.data != null) {
                    chart!!.data.isHighlightEnabled = !chart!!.data.isHighlightEnabled
                    chart!!.invalidate()
                }
            }
            R.id.actionToggleRotate -> {
                if (chart!!.isRotationEnabled) chart!!.isRotationEnabled =
                    false else chart!!.isRotationEnabled = true
                chart!!.invalidate()
            }
            R.id.actionToggleFilled -> {
                val sets = chart!!.data
                    .dataSets as ArrayList<IRadarDataSet>
                for (set in sets) {
                    if (set.isDrawFilledEnabled) set.setDrawFilled(false) else set.setDrawFilled(
                        true
                    )
                }
                chart!!.invalidate()
            }
            R.id.actionToggleHighlightCircle -> {
                val sets = chart!!.data
                    .dataSets as ArrayList<IRadarDataSet>
                for (set in sets) {
                    set.isDrawHighlightCircleEnabled = !set.isDrawHighlightCircleEnabled
                }
                chart!!.invalidate()
            }
            R.id.actionToggleXLabels -> {
                chart!!.xAxis.isEnabled = !chart!!.xAxis.isEnabled
                chart!!.notifyDataSetChanged()
                chart!!.invalidate()
            }
            R.id.actionToggleYLabels -> {
                chart!!.yAxis.isEnabled = !chart!!.yAxis.isEnabled
                chart!!.invalidate()
            }
            R.id.animateX -> {
                chart!!.animateX(1400)
            }
            R.id.animateY -> {
                chart!!.animateY(1400)
            }
            R.id.animateXY -> {
                chart!!.animateXY(1400, 1400)
            }
            R.id.actionToggleSpin -> {
                chart!!.spin(
                    2000,
                    chart!!.rotationAngle,
                    chart!!.rotationAngle + 360,
                    Easing.EaseInOutCubic
                )
            }
            R.id.actionSave -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    saveToGallery()
                } else {
                    requestStoragePermission(chart)
                }
            }
        }
        return true
    }

    override fun saveToGallery() {
        saveToGallery(chart!!, "RadarChartActivity")
    }
}