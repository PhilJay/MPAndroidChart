package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.xxmassdeveloper.mpchartexample.databinding.ActivityBarchartNoseekbarBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import java.text.DecimalFormat

class BarChartPositiveNegative : DemoBase() {

    private lateinit var binding: ActivityBarchartNoseekbarBinding

    private var barSet: BarDataSet<Any?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarchartNoseekbarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "BarChartPositiveNegative"

        val data = listOf(
            Data(0f, -224.1f, "12-29"),
            Data(1f, 238.5f, "12-30"),
            Data(2f, 1280.1f, "12-31"),
            Data(3f, -442.3f, "01-01"),
            Data(4f, -2280.1f, "01-02")
        )

        binding.chart1.apply {
            setBackgroundColor(Color.WHITE)
            extraTopOffset = -30f
            extraBottomOffset = 10f
            extraLeftOffset = 70f
            extraRightOffset = 70f

            isDrawBarShadowEnabled = false
            isDrawValueAboveBarEnabled = true
            description.isEnabled = false
            isPinchZoomEnabled = false
            isDrawGridBackgroundEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                typeface = tfRegular
                isDrawGridLinesEnabled = false
                isDrawAxisLineEnabled = false
                textColor = Color.LTGRAY
                textSize = 13f
                labelCount = 5
                isCenterAxisLabelsEnabled = true
                granularity = 1f
                valueFormatter = IAxisValueFormatter { value, _ ->
                    data[value.toInt().coerceIn(0, data.size - 1)].xAxisValue
                }
            }

            axisLeft.apply {
                isDrawLabelsEnabled = false
                spaceTop = 25f
                spaceBottom = 25f
                isDrawAxisLineEnabled = false
                isDrawGridLinesEnabled = false
                isDrawZeroLineEnabled = true
                zeroLineColor = Color.GRAY
                zeroLineWidth = 0.7f
            }
            axisRight.isEnabled = false
            legend.isEnabled = false
        }

        setData(data)
    }

    private fun setData(dataList: List<Data>) {
        val values = ArrayList<BarEntry<Any?>>()
        val colors = ArrayList<Int>()

        val green = Color.rgb(110, 190, 102)
        val red = Color.rgb(211, 74, 88)

        for (d in dataList) {
            values.add(BarEntry(d.xValue, d.yValue))
            colors.add(if (d.yValue >= 0) red else green)
        }

        val existing = barSet
        if (existing != null) {
            existing.entries = values
            binding.chart1.notifyDataSetChanged()
        } else {
            val set = BarDataSet(values, "Values")
            barSet = set
            set.colors = colors
            set.valueTextColors = colors

            val data = BarData(set)
            data.setValueTextSize(13f)
            data.setValueTypeface(tfRegular)
            data.setValueFormatter(ValueFormatter())
            data.barWidth = 0.8f

            binding.chart1.data = data
            binding.chart1.invalidate()
        }
    }

    private class Data(val xValue: Float, val yValue: Float, val xAxisValue: String)

    private class ValueFormatter : IValueFormatter {

        private val format = DecimalFormat("######.0")

        override fun getFormattedValue(value: Float, entry: Entry<*>, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {
            return format.format(value.toDouble())
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
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/BarChartPositiveNegative.java")
                startActivity(i)
            }
        }
        return true
    }

    override fun saveToGallery() {}
}
