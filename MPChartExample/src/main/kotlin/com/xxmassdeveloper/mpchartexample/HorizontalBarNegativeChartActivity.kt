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
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import com.xxmassdeveloper.mpchartexample.databinding.ActivityHorizontalbarchartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class HorizontalBarNegativeChartActivity : DemoBase(), OnSeekBarChangeListener, OnChartValueSelectedListener {

    private lateinit var binding: ActivityHorizontalbarchartBinding

    private var barSet: BarDataSet<Any?>? = null

    private val onValueSelectedRectF = RectF()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHorizontalbarchartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "HorizontalBarChartActivity"

        binding.seekBar2.setOnSeekBarChangeListener(this)
        binding.seekBar1.setOnSeekBarChangeListener(this)

        binding.chart1.apply {
            onChartValueSelectedListener = this@HorizontalBarNegativeChartActivity
            isDrawBarShadowEnabled = false
            isDrawValueAboveBarEnabled = true
            description.isEnabled = false
            maxVisibleCount = 60
            isPinchZoomEnabled = false
            isDrawGridBackgroundEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                typeface = tfLight
                isDrawAxisLineEnabled = true
                isDrawGridLinesEnabled = false
                granularity = 10f
            }

            axisLeft.apply {
                typeface = tfLight
                isDrawAxisLineEnabled = true
                isDrawGridLinesEnabled = true
            }

            axisRight.apply {
                typeface = tfLight
                isDrawAxisLineEnabled = true
                isDrawGridLinesEnabled = false
            }

            isFitBarsEnabled = true
            animateY(2500)
        }

        binding.seekBar2.progress = 50
        binding.seekBar1.progress = 12

        binding.chart1.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.HORIZONTAL
            isDrawInsideEnabled = false
            formSize = 8f
            xEntrySpace = 4f
        }
    }

    private fun setData(count: Int, range: Float) {
        val barWidth = 9f
        val spaceForBar = 10f
        val values = ArrayList<BarEntry<Any?>>()

        for (i in 0 until count) {
            val value = (Math.random() * range - range / 2).toFloat()
            values.add(BarEntry(i * spaceForBar, value, ContextCompat.getDrawable(this, R.drawable.star)))
        }

        val existing = barSet
        if (existing != null) {
            existing.entries = values
            binding.chart1.notifyDataSetChanged()
        } else {
            val set1 = BarDataSet(values, "DataSet 1")
            set1.isDrawIconsEnabled = false

            val dataSets = ArrayList<IBarDataSet<*>>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.setValueTypeface(tfLight)
            data.barWidth = barWidth
            barSet = set1
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
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/HorizontalBarChartActivity.java")
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
        binding.chart1.isFitBarsEnabled = true
        binding.chart1.invalidate()
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "HorizontalBarChartActivity")

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onValueSelected(e: Entry<*>, h: Highlight) {
        val chart = binding.chart1
        val bounds = onValueSelectedRectF
        chart.getBarBounds(e as BarEntry<*>, bounds)

        val axis = chart.data?.getDataSetByIndex(h.dataSetIndex)?.axisDependency ?: return
        val position = chart.getPosition(e, axis)

        Log.i("bounds", bounds.toString())
        Log.i("position", position.toString())

        position?.let { MPPointF.recycleInstance(it) }
    }

    override fun onNothingSelected() {}
}
