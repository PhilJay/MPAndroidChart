package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.FileUtils
import com.xxmassdeveloper.mpchartexample.databinding.ActivityBarchartSinusBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class BarChartActivitySinus : DemoBase(), OnSeekBarChangeListener {

    private lateinit var binding: ActivityBarchartSinusBinding

    private var barSet: BarDataSet<Any?>? = null

    private lateinit var data: List<BarEntry<*>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarchartSinusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "BarChartActivitySinus"

        data = FileUtils.loadBarEntriesFromAssets(assets, "othersine.txt")

        binding.chart1.apply {
            isDrawBarShadowEnabled = false
            isDrawValueAboveBarEnabled = true
            description.isEnabled = false
            maxVisibleCount = 60
            isPinchZoomEnabled = false
            isDrawGridBackgroundEnabled = false

            xAxis.isEnabled = false

            axisLeft.apply {
                typeface = tfLight
                labelCount = 6
                axisMinimum = -2.5f
                axisMaximum = 2.5f
                isGranularityEnabled = true
                granularity = 0.1f
            }

            axisRight.apply {
                isDrawGridLinesEnabled = false
                typeface = tfLight
                labelCount = 6
                axisMinimum = -2.5f
                axisMaximum = 2.5f
                granularity = 0.1f
            }
        }

        binding.seekbarValues.setOnSeekBarChangeListener(this)
        binding.seekbarValues.progress = 150

        binding.chart1.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.HORIZONTAL
            isDrawInsideEnabled = false
            form = Legend.LegendForm.SQUARE
            formSize = 9f
            textSize = 11f
            xEntrySpace = 4f
        }

        binding.chart1.animateXY(1500, 1500)
    }

    private fun setData(count: Int) {
        val entries = ArrayList<BarEntry<Any?>>()
        for (i in 0 until count) {
            entries.add(data[i])
        }

        val set = barSet?.also { it.entries = entries } ?: BarDataSet(entries, "Sinus Function").also {
            it.color = Color.rgb(240, 120, 124)
            barSet = it
        }

        val barData = BarData(set)
        barData.setValueTextSize(10f)
        barData.setValueTypeface(tfLight)
        barData.setDrawValues(false)
        barData.barWidth = 0.8f

        binding.chart1.data = barData
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
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/BarChartActivitySinus.java")
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
            R.id.animateX -> chart.animateX(2000)
            R.id.animateY -> chart.animateY(2000)
            R.id.animateXY -> chart.animateXY(2000, 2000)
            R.id.actionSave -> saveChartToGalleryWithPermission(chart)
        }
        return true
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        binding.tvValueCount.text = binding.seekbarValues.progress.toString()

        setData(binding.seekbarValues.progress)
        binding.chart1.invalidate()
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "BarChartActivitySinus")

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
