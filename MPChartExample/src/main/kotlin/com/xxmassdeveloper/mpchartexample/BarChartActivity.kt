package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Fill
import com.github.mikephil.charting.utils.MPPointF
import com.xxmassdeveloper.mpchartexample.custom.DayAxisValueFormatter
import com.xxmassdeveloper.mpchartexample.custom.MyAxisValueFormatter
import com.xxmassdeveloper.mpchartexample.custom.XYMarkerView
import com.xxmassdeveloper.mpchartexample.databinding.ActivityBarchartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class BarChartActivity : DemoBase(), OnSeekBarChangeListener, OnChartValueSelectedListener {

    private lateinit var binding: ActivityBarchartBinding

    private val onValueSelectedRectF = RectF()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarchartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "BarChartActivity"

        binding.seekBar2.setOnSeekBarChangeListener(this)
        binding.seekBar1.setOnSeekBarChangeListener(this)

        val xAxisFormatter = DayAxisValueFormatter(binding.chart1)
        val custom = MyAxisValueFormatter()

        binding.chart1.apply {
            onChartValueSelectedListener = this@BarChartActivity
            isDrawBarShadowEnabled = false
            isDrawValueAboveBarEnabled = true
            description.isEnabled = false
            maxVisibleCount = 60
            isPinchZoomEnabled = false
            isDrawGridBackgroundEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                typeface = tfLight
                isDrawGridLinesEnabled = false
                granularity = 1f
                labelCount = 7
                valueFormatter = xAxisFormatter
            }

            axisLeft.apply {
                typeface = tfLight
                labelCount = 8
                valueFormatter = custom
                labelPosition = YAxis.YAxisLabelPosition.OUTSIDE_CHART
                spaceTop = 15f
                axisMinimum = 0f
            }

            axisRight.apply {
                isDrawGridLinesEnabled = false
                typeface = tfLight
                labelCount = 8
                valueFormatter = custom
                spaceTop = 15f
                axisMinimum = 0f
            }

            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                isDrawInsideEnabled = false
                form = Legend.LegendForm.SQUARE
                formSize = 9f
                textSize = 11f
                xEntrySpace = 4f
            }

            val mv = XYMarkerView(this@BarChartActivity, xAxisFormatter)
            marker = mv
        }

        binding.seekBar2.progress = 50
        binding.seekBar1.progress = 12
    }

    private fun setData(count: Int, range: Float) {
        val start = 1f
        val values = ArrayList<BarEntry<Any?>>()

        for (i in start.toInt() until (start + count).toInt()) {
            val value = (Math.random() * (range + 1)).toFloat()
            if (Math.random() * 100 < 25) {
                values.add(BarEntry(i.toFloat(), value, ContextCompat.getDrawable(this, R.drawable.star)))
            } else {
                values.add(BarEntry(i.toFloat(), value))
            }
        }

        val chartData = binding.chart1.data
        if (chartData != null && chartData.dataSetCount > 0) {
            val set1 = chartData.getDataSetByIndex(0) as BarDataSet<Any?>
            set1.entries = values
            chartData.notifyDataChanged()
            binding.chart1.notifyDataSetChanged()
        } else {
            val set1 = BarDataSet(values, "The year 2017")
            set1.isDrawIconsEnabled = false

            val startColor1 = ContextCompat.getColor(this, android.R.color.holo_orange_light)
            val startColor2 = ContextCompat.getColor(this, android.R.color.holo_blue_light)
            val startColor3 = ContextCompat.getColor(this, android.R.color.holo_orange_light)
            val startColor4 = ContextCompat.getColor(this, android.R.color.holo_green_light)
            val startColor5 = ContextCompat.getColor(this, android.R.color.holo_red_light)
            val endColor1 = ContextCompat.getColor(this, android.R.color.holo_blue_dark)
            val endColor2 = ContextCompat.getColor(this, android.R.color.holo_purple)
            val endColor3 = ContextCompat.getColor(this, android.R.color.holo_green_dark)
            val endColor4 = ContextCompat.getColor(this, android.R.color.holo_red_dark)
            val endColor5 = ContextCompat.getColor(this, android.R.color.holo_orange_dark)

            val gradientFills = ArrayList<Fill>()
            gradientFills.add(Fill(startColor1, endColor1))
            gradientFills.add(Fill(startColor2, endColor2))
            gradientFills.add(Fill(startColor3, endColor3))
            gradientFills.add(Fill(startColor4, endColor4))
            gradientFills.add(Fill(startColor5, endColor5))

            set1.fills = gradientFills

            val dataSets = ArrayList<IBarDataSet<*>>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.setValueTypeface(tfLight)
            data.barWidth = 0.9f

            binding.chart1.data = data
        }
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
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/BarChartActivity.java")
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
                chart.data?.dataSets?.forEach { (it as BarDataSet<Any?>).barBorderWidth = if (it.barBorderWidth == 1f) 0f else 1f }
                chart.invalidate()
            }
            R.id.animateX -> chart.animateX(2000)
            R.id.animateY -> chart.animateY(2000)
            R.id.animateXY -> chart.animateXY(2000, 2000)
            R.id.actionSave -> saveChartToGalleryWithPermission(chart)
        }
        return true
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        binding.tvXMax.text = binding.seekBar1.progress.toString()
        binding.tvYMax.text = binding.seekBar2.progress.toString()

        setData(binding.seekBar1.progress, binding.seekBar2.progress.toFloat())
        binding.chart1.invalidate()
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "BarChartActivity")

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onValueSelected(e: Entry<*>, h: Highlight) {
        val bounds = onValueSelectedRectF
        binding.chart1.getBarBounds(e as BarEntry<*>, bounds)
        val position = binding.chart1.getPosition(e, YAxis.AxisDependency.LEFT)

        Log.i("bounds", bounds.toString())
        Log.i("position", position.toString())
        Log.i("x-index", "low: ${binding.chart1.lowestVisibleX}, high: ${binding.chart1.highestVisibleX}")

        position?.let { MPPointF.recycleInstance(it) }
    }

    override fun onNothingSelected() {}
}
