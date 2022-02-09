package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.min

class BarChartPositiveNegative : DemoBase() {
    private var chart: BarChart? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_barchart_noseekbar)
        title = "BarChartPositiveNegative"
        chart = findViewById(R.id.chart1)
        chart?.setBackgroundColor(Color.WHITE)
        chart?.extraTopOffset = -30f
        chart?.extraBottomOffset = 10f
        chart?.extraLeftOffset = 70f
        chart?.extraRightOffset = 70f
        chart?.setDrawBarShadow(false)
        chart?.setDrawValueAboveBar(true)
        chart?.description?.isEnabled = false

        // scaling can now only be done on x- and y-axis separately
        chart?.setPinchZoom(false)
        chart?.setDrawGridBackground(false)
        val xAxis = chart?.xAxis
        xAxis?.position = XAxisPosition.BOTTOM
        xAxis?.typeface = tfRegular
        xAxis?.setDrawGridLines(false)
        xAxis?.setDrawAxisLine(false)
        xAxis?.textColor = Color.LTGRAY
        xAxis?.textSize = 13f
        xAxis?.labelCount = 5
        xAxis?.setCenterAxisLabels(true)
        xAxis?.granularity = 1f
        val left = chart?.axisLeft
        left?.setDrawLabels(false)
        left?.spaceTop = 25f
        left?.spaceBottom = 25f
        left?.setDrawAxisLine(false)
        left?.setDrawGridLines(false)
        left?.setDrawZeroLine(true) // draw a zero line
        left?.zeroLineColor = Color.GRAY
        left?.zeroLineWidth = 0.7f
        chart?.axisRight?.isEnabled = false
        chart?.legend?.isEnabled = false

        // THIS IS THE ORIGINAL DATA YOU WANT TO PLOT
        val data: MutableList<Data> = ArrayList()
        data.add(Data(0f, -224.1f, "12-29"))
        data.add(Data(1f, 238.5f, "12-30"))
        data.add(Data(2f, 1280.1f, "12-31"))
        data.add(Data(3f, -442.3f, "01-01"))
        data.add(Data(4f, -2280.1f, "01-02"))
        xAxis?.valueFormatter = IAxisValueFormatter { value, axis ->
            data[min(
                max(value.toInt(), 0),
                data.size - 1
            )].xAxisValue
        }
        setData(data)
    }

    private fun setData(dataList: List<Data>) {
        val values = ArrayList<BarEntry>()
        val colors: MutableList<Int> = ArrayList()
        val green = Color.rgb(110, 190, 102)
        val red = Color.rgb(211, 74, 88)
        for (i in dataList.indices) {
            val d = dataList[i]
            val entry = BarEntry(d.xValue, d.yValue)
            values.add(entry)

            // specific colors
            if (d.yValue >= 0) colors.add(red) else colors.add(green)
        }
        val set: BarDataSet
        if (chart!!.data != null &&
            chart!!.data.dataSetCount > 0
        ) {
            set = chart!!.data.getDataSetByIndex(0) as BarDataSet
            set.values = values
            chart!!.data.notifyDataChanged()
            chart!!.notifyDataSetChanged()
        } else {
            set = BarDataSet(values, "Values")
            set.colors = colors
            set.setValueTextColors(colors)
            val data = BarData(set)
            data.setValueTextSize(13f)
            data.setValueTypeface(tfRegular)
            data.setValueFormatter(ValueFormatter())
            data.barWidth = 0.8f
            chart!!.data = data
            chart!!.invalidate()
        }
    }

    /**
     * Demo class representing data.
     */
    private inner class Data internal constructor(
        val xValue: Float,
        val yValue: Float,
        val xAxisValue: String
    )

    private inner class ValueFormatter internal constructor() : IValueFormatter {
        private val mFormat: DecimalFormat
        override fun getFormattedValue(
            value: Float,
            entry: Entry,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler
        ): String {
            return mFormat.format(value.toDouble())
        }

        init {
            mFormat = DecimalFormat("######.0")
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
                i.data =
                    Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/BarChartPositiveNegative.java")
                startActivity(i)
            }
        }
        return true
    }

    public override fun saveToGallery() { /* Intentionally left empty */
    }
}