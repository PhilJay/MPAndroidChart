package com.xxmassdeveloper.mpchartexample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ViewPortHandler
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import java.text.DecimalFormat

class StackedBarActivityNegative : DemoBase(), OnChartValueSelectedListener {
    private var chart: HorizontalBarChart? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_age_distribution)
        title = "StackedBarActivityNegative"
        chart = findViewById(R.id.chart1)
        chart?.setOnChartValueSelectedListener(this)
        chart?.setDrawGridBackground(false)
        chart?.description?.isEnabled = false

        // scaling can now only be done on x- and y-axis separately
        chart?.setPinchZoom(false)
        chart?.setDrawBarShadow(false)
        chart?.setDrawValueAboveBar(true)
        chart?.isHighlightFullBarEnabled = false
        chart?.axisLeft!!.isEnabled = false
        chart?.axisRight!!.axisMaximum = 25f
        chart?.axisRight!!.axisMinimum = -25f
        chart?.axisRight!!.setDrawGridLines(false)
        chart?.axisRight!!.setDrawZeroLine(true)
        chart?.axisRight!!.setLabelCount(7, false)
        chart?.axisRight!!.valueFormatter = CustomFormatter()
        chart?.axisRight!!.textSize = 9f
        val xAxis = chart?.xAxis
        xAxis?.position = XAxisPosition.BOTH_SIDED
        xAxis?.setDrawGridLines(false)
        xAxis?.setDrawAxisLine(false)
        xAxis?.textSize = 9f
        xAxis?.axisMinimum = 0f
        xAxis?.axisMaximum = 110f
        xAxis?.setCenterAxisLabels(true)
        xAxis?.labelCount = 12
        xAxis?.granularity = 10f
        xAxis?.valueFormatter = object : IAxisValueFormatter {
            private val format = DecimalFormat("###")
            override fun getFormattedValue(value: Float, axis: AxisBase): String {
                return format.format(value.toDouble()) + "-" + format.format((value + 10).toDouble())
            }
        }
        val l = chart!!.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.formSize = 8f
        l.formToTextSpace = 4f
        l.xEntrySpace = 6f

        // IMPORTANT: When using negative values in stacked bars, always make sure the negative values are in the array first
        val values = ArrayList<BarEntry>()
        values.add(BarEntry(5f, floatArrayOf(-10f, 10f)))
        values.add(BarEntry(15f, floatArrayOf(-12f, 13f)))
        values.add(BarEntry(25f, floatArrayOf(-15f, 15f)))
        values.add(BarEntry(35f, floatArrayOf(-17f, 17f)))
        values.add(BarEntry(45f, floatArrayOf(-19f, 20f)))
        values.add(BarEntry(45f, floatArrayOf(-19f, 20f), resources.getDrawable(R.drawable.star)))
        values.add(BarEntry(55f, floatArrayOf(-19f, 19f)))
        values.add(BarEntry(65f, floatArrayOf(-16f, 16f)))
        values.add(BarEntry(75f, floatArrayOf(-13f, 14f)))
        values.add(BarEntry(85f, floatArrayOf(-10f, 11f)))
        values.add(BarEntry(95f, floatArrayOf(-5f, 6f)))
        values.add(BarEntry(105f, floatArrayOf(-1f, 2f)))
        val set = BarDataSet(values, "Age Distribution")
        set.setDrawIcons(false)
        set.valueFormatter = CustomFormatter()
        set.valueTextSize = 7f
        set.axisDependency = YAxis.AxisDependency.RIGHT
        set.setColors(Color.rgb(67, 67, 72), Color.rgb(124, 181, 236))
        set.stackLabels = arrayOf(
            "Men", "Women"
        )
        val data = BarData(set)
        data?.barWidth = 8.5f
        chart?.data = data
        chart?.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data =
                    Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/StackedBarActivityNegative.java")
                startActivity(i)
            }
            R.id.actionToggleValues -> {
                val sets = chart!!.data
                    .dataSets
                for (iSet in sets) {
                    val set = iSet as BarDataSet
                    set.setDrawValues(!set.isDrawValuesEnabled)
                }
                chart!!.invalidate()
            }
            R.id.actionToggleIcons -> {
                val sets = chart!!.data
                    .dataSets
                for (iSet in sets) {
                    val set = iSet as BarDataSet
                    set.setDrawIcons(!set.isDrawIconsEnabled)
                }
                chart!!.invalidate()
            }
            R.id.actionToggleHighlight -> {
                if (chart!!.data != null) {
                    chart!!.data.isHighlightEnabled = !chart!!.data.isHighlightEnabled
                    chart!!.invalidate()
                }
            }
            R.id.actionTogglePinch -> {
                if (chart!!.isPinchZoomEnabled) chart!!.setPinchZoom(false) else chart!!.setPinchZoom(
                    true
                )
                chart!!.invalidate()
            }
            R.id.actionToggleAutoScaleMinMax -> {
                chart!!.isAutoScaleMinMaxEnabled = !chart!!.isAutoScaleMinMaxEnabled
                chart!!.notifyDataSetChanged()
            }
            R.id.actionToggleBarBorders -> {
                for (set in chart!!.data.dataSets) (set as BarDataSet).barBorderWidth =
                    if (set.getBarBorderWidth() == 1f) 0f else 1f
                chart!!.invalidate()
            }
            R.id.animateX -> {
                chart!!.animateX(3000)
            }
            R.id.animateY -> {
                chart!!.animateY(3000)
            }
            R.id.animateXY -> {
                chart!!.animateXY(3000, 3000)
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
        chart?.let { saveToGallery(it, "StackedBarActivityNegative") }
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        val entry = e as BarEntry
        Log.i(
            "VAL SELECTED",
            "Value: " + Math.abs(entry.yVals[h.stackIndex])
        )
    }

    override fun onNothingSelected() {
        Log.i("NOTING SELECTED", "")
    }

    private inner class CustomFormatter internal constructor() : IValueFormatter,
        IAxisValueFormatter {
        private val mFormat: DecimalFormat

        // data
        override fun getFormattedValue(
            value: Float,
            entry: Entry,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler
        ): String {
            return mFormat.format(Math.abs(value).toDouble()) + "m"
        }

        // YAxis
        override fun getFormattedValue(value: Float, axis: AxisBase): String {
            return mFormat.format(Math.abs(value).toDouble()) + "m"
        }

        init {
            mFormat = DecimalFormat("###")
        }
    }
}