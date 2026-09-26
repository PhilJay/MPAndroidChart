package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
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
import com.xxmassdeveloper.mpchartexample.databinding.ActivityAgeDistributionBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import java.text.DecimalFormat
import kotlin.math.abs

class StackedBarActivityNegative : DemoBase(), OnChartValueSelectedListener {

    private lateinit var binding: ActivityAgeDistributionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgeDistributionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "StackedBarActivityNegative"

        binding.chart1.apply {
            onChartValueSelectedListener = this@StackedBarActivityNegative
            isDrawGridBackgroundEnabled = false
            description.isEnabled = false
            isPinchZoomEnabled = false
            isDrawBarShadowEnabled = false
            isDrawValueAboveBarEnabled = true
            isHighlightFullBarEnabled = false

            axisLeft.isEnabled = false
            axisRight.apply {
                axisMaximum = 25f
                axisMinimum = -25f
                isDrawGridLinesEnabled = false
                isDrawZeroLineEnabled = true
                labelCount = 7
                valueFormatter = CustomFormatter()
                textSize = 9f
            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTH_SIDED
                isDrawGridLinesEnabled = false
                isDrawAxisLineEnabled = false
                textSize = 9f
                axisMinimum = 0f
                axisMaximum = 110f
                isCenterAxisLabelsEnabled = true
                labelCount = 12
                granularity = 10f
                valueFormatter = object : IAxisValueFormatter {
                    private val format = DecimalFormat("###")

                    override fun getFormattedValue(value: Float, axis: AxisBase): String {
                        return format.format(value.toDouble()) + "-" + format.format((value + 10).toDouble())
                    }
                }
            }

            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.HORIZONTAL
                isDrawInsideEnabled = false
                formSize = 8f
                formToTextSpace = 4f
                xEntrySpace = 6f
            }
        }

        // Negative values must come first in each stack
        val values = ArrayList<BarEntry<Any?>>()
        values.add(BarEntry(5f, listOf(-10f, 10f)))
        values.add(BarEntry(15f, listOf(-12f, 13f)))
        values.add(BarEntry(25f, listOf(-15f, 15f)))
        values.add(BarEntry(35f, listOf(-17f, 17f)))
        values.add(BarEntry(45f, listOf(-19f, 20f)))
        values.add(BarEntry(45f, listOf(-19f, 20f), ContextCompat.getDrawable(this, R.drawable.star)))
        values.add(BarEntry(55f, listOf(-19f, 19f)))
        values.add(BarEntry(65f, listOf(-16f, 16f)))
        values.add(BarEntry(75f, listOf(-13f, 14f)))
        values.add(BarEntry(85f, listOf(-10f, 11f)))
        values.add(BarEntry(95f, listOf(-5f, 6f)))
        values.add(BarEntry(105f, listOf(-1f, 2f)))

        val set = BarDataSet(values, "Age Distribution")
        set.isDrawIconsEnabled = false
        set.valueFormatter = CustomFormatter()
        set.valueTextSize = 7f
        set.axisDependency = YAxis.AxisDependency.RIGHT
        set.setColors(Color.rgb(67, 67, 72), Color.rgb(124, 181, 236))
        set.stackLabels = listOf("Men", "Women")

        val data = BarData(set)
        data.barWidth = 8.5f
        binding.chart1.data = data
        binding.chart1.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val chart = binding.chart1
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/StackedBarActivityNegative.java")
                startActivity(i)
            }
            R.id.actionToggleValues -> {
                chart.data?.dataSets?.forEach { it.isDrawValuesEnabled = !it.isDrawValuesEnabled }
                chart.invalidate()
            }
            R.id.actionToggleIcons -> {
                chart.data?.dataSets?.forEach { it.isDrawIconsEnabled = !it.isDrawIconsEnabled }
                chart.invalidate()
            }
            R.id.actionToggleHighlight -> {
                chart.data?.let {
                    it.isHighlightEnabled = !it.isHighlightEnabled
                    chart.invalidate()
                }
            }
            R.id.actionTogglePinch -> {
                chart.isPinchZoomEnabled = !chart.isPinchZoomEnabled
                chart.invalidate()
            }
            R.id.actionToggleAutoScaleMinMax -> {
                chart.isAutoScaleMinMaxEnabled = !chart.isAutoScaleMinMaxEnabled
                chart.notifyDataSetChanged()
            }
            R.id.actionToggleBarBorders -> {
                chart.data?.dataSets?.forEach { (it as BarDataSet<*>).barBorderWidth = if (it.barBorderWidth == 1f) 0f else 1f }
                chart.invalidate()
            }
            R.id.animateX -> chart.animateX(3000)
            R.id.animateY -> chart.animateY(3000)
            R.id.animateXY -> chart.animateXY(3000, 3000)
            R.id.actionSave -> saveChartToGalleryWithPermission(chart)
        }
        return true
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "StackedBarActivityNegative")

    override fun onValueSelected(e: Entry<*>, h: Highlight) {
        val entry = e as BarEntry<*>
        val stackValues = entry.stackValues ?: return
        Log.i("VAL SELECTED", "Value: ${abs(stackValues[h.stackIndex])}")
    }

    override fun onNothingSelected() {
        Log.i("NOTING SELECTED", "")
    }

    private class CustomFormatter : IValueFormatter, IAxisValueFormatter {

        private val format = DecimalFormat("###")

        override fun getFormattedValue(value: Float, entry: Entry<*>, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {
            return format.format(abs(value).toDouble()) + "m"
        }

        override fun getFormattedValue(value: Float, axis: AxisBase): String {
            return format.format(abs(value).toDouble()) + "m"
        }
    }
}
