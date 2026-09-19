package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView
import com.xxmassdeveloper.mpchartexample.databinding.ActivityBarchartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import java.util.Locale

class BarChartActivityMultiDataset : DemoBase(), OnSeekBarChangeListener, OnChartValueSelectedListener {

    private lateinit var binding: ActivityBarchartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarchartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "BarChartActivityMultiDataset"

        binding.tvXMax.textSize = 10f

        binding.seekBar1.max = 50
        binding.seekBar1.setOnSeekBarChangeListener(this)
        binding.seekBar2.setOnSeekBarChangeListener(this)

        binding.chart1.apply {
            onChartValueSelectedListener = this@BarChartActivityMultiDataset
            description.isEnabled = false
            isPinchZoomEnabled = false
            isDrawBarShadowEnabled = false
            isDrawGridBackgroundEnabled = false

            val mv = MyMarkerView(this@BarChartActivityMultiDataset, R.layout.custom_marker_view)
            marker = mv
        }

        binding.seekBar1.progress = 10
        binding.seekBar2.progress = 100

        binding.chart1.apply {
            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
                isDrawInsideEnabled = true
                typeface = tfLight
                yOffset = 0f
                xOffset = 10f
                yEntrySpace = 0f
                textSize = 8f
            }

            xAxis.apply {
                typeface = tfLight
                granularity = 1f
                isCenterAxisLabelsEnabled = true
                valueFormatter = IAxisValueFormatter { value, _ -> value.toInt().toString() }
            }

            axisLeft.apply {
                typeface = tfLight
                valueFormatter = LargeValueFormatter()
                isDrawGridLinesEnabled = false
                spaceTop = 35f
                axisMinimum = 0f
            }

            axisRight.isEnabled = false
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val groupSpace = 0.08f
        val barSpace = 0.03f
        val barWidth = 0.2f

        val groupCount = binding.seekBar1.progress + 1
        val startYear = 1980
        val endYear = startYear + groupCount

        binding.tvXMax.text = String.format(Locale.ENGLISH, "%d-%d", startYear, endYear)
        binding.tvYMax.text = binding.seekBar2.progress.toString()

        val values1 = ArrayList<BarEntry<Any?>>()
        val values2 = ArrayList<BarEntry<Any?>>()
        val values3 = ArrayList<BarEntry<Any?>>()
        val values4 = ArrayList<BarEntry<Any?>>()

        val randomMultiplier = binding.seekBar2.progress * 100000f

        for (i in startYear until endYear) {
            values1.add(BarEntry(i.toFloat(), (Math.random() * randomMultiplier).toFloat()))
            values2.add(BarEntry(i.toFloat(), (Math.random() * randomMultiplier).toFloat()))
            values3.add(BarEntry(i.toFloat(), (Math.random() * randomMultiplier).toFloat()))
            values4.add(BarEntry(i.toFloat(), (Math.random() * randomMultiplier).toFloat()))
        }

        val chart = binding.chart1
        val chartData = chart.data
        if (chartData != null && chartData.dataSetCount > 0) {
            (chartData.getDataSetByIndex(0) as BarDataSet<Any?>).entries = values1
            (chartData.getDataSetByIndex(1) as BarDataSet<Any?>).entries = values2
            (chartData.getDataSetByIndex(2) as BarDataSet<Any?>).entries = values3
            (chartData.getDataSetByIndex(3) as BarDataSet<Any?>).entries = values4
            chartData.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            val set1 = BarDataSet(values1, "Company A")
            set1.color = Color.rgb(104, 241, 175)
            val set2 = BarDataSet(values2, "Company B")
            set2.color = Color.rgb(164, 228, 251)
            val set3 = BarDataSet(values3, "Company C")
            set3.color = Color.rgb(242, 247, 158)
            val set4 = BarDataSet(values4, "Company D")
            set4.color = Color.rgb(255, 102, 0)

            val data = BarData(set1, set2, set3, set4)
            data.setValueFormatter(LargeValueFormatter())
            data.setValueTypeface(tfLight)

            chart.data = data
        }

        val barData = chart.barData ?: return
        barData.barWidth = barWidth

        chart.xAxis.axisMinimum = startYear.toFloat()
        chart.xAxis.axisMaximum = startYear + barData.getGroupWidth(groupSpace, barSpace) * groupCount
        chart.groupBars(startYear.toFloat(), groupSpace, barSpace)
        chart.invalidate()
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
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/BarChartActivityMultiDataset.java")
                startActivity(i)
            }
            R.id.actionToggleValues -> {
                chart.data?.dataSets?.forEach { it.isDrawValuesEnabled = !it.isDrawValuesEnabled }
                chart.invalidate()
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
                chart.data?.dataSets?.forEach { (it as BarDataSet<Any?>).barBorderWidth = if (it.barBorderWidth == 1f) 0f else 1f }
                chart.invalidate()
            }
            R.id.actionToggleHighlight -> {
                chart.data?.let {
                    it.isHighlightEnabled = !it.isHighlightEnabled
                    chart.invalidate()
                }
            }
            R.id.actionSave -> saveChartToGalleryWithPermission(chart)
            R.id.animateX -> chart.animateX(2000)
            R.id.animateY -> chart.animateY(2000)
            R.id.animateXY -> chart.animateXY(2000, 2000)
        }
        return true
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "BarChartActivityMultiDataset")

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onValueSelected(e: Entry<*>, h: Highlight) {
        Log.i("Activity", "Selected: $e, dataSet: ${h.dataSetIndex}")
    }

    override fun onNothingSelected() {
        Log.i("Activity", "Nothing selected.")
    }
}
