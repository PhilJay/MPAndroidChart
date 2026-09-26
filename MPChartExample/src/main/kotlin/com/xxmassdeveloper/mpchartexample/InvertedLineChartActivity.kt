package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.EntryXComparator
import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView
import com.xxmassdeveloper.mpchartexample.databinding.ActivityLinechartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class InvertedLineChartActivity : DemoBase(), SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener {

    private lateinit var binding: ActivityLinechartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinechartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "InvertedLineChartActivity"

        binding.seekBar2.setOnSeekBarChangeListener(this)
        binding.seekBar1.setOnSeekBarChangeListener(this)

        binding.chart1.apply {
            onChartValueSelectedListener = this@InvertedLineChartActivity
            isDrawGridBackgroundEnabled = false
            description.isEnabled = false
            isTouchEnabled = true
            isDragEnabled = true
            isScaleEnabled = true
            isPinchZoomEnabled = true

            val mv = MyMarkerView(this@InvertedLineChartActivity, R.layout.custom_marker_view)
            marker = mv

            xAxis.isAvoidFirstLastClippingEnabled = true
            xAxis.axisMinimum = 0f

            axisLeft.isInverted = true
            axisLeft.axisMinimum = 0f

            axisRight.isEnabled = false
        }

        binding.seekBar1.progress = 25
        binding.seekBar2.progress = 50

        binding.chart1.legend.form = Legend.LegendForm.LINE

        binding.chart1.invalidate()
    }

    private fun setData(count: Int, range: Float) {
        val entries = ArrayList<Entry<Any?>>()

        for (i in 0 until count) {
            val xValue = (Math.random() * range).toFloat()
            val yValue = (Math.random() * range).toFloat()
            entries.add(Entry(xValue, yValue))
        }

        entries.sortWith(EntryXComparator())

        val set1 = LineDataSet(entries, "DataSet 1").apply {
            lineWidth = 1.5f
            circleRadius = 4f
        }

        binding.chart1.data = LineData(set1)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.line, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val chart = binding.chart1
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/InvertedLineChartActivity.java")
                startActivity(i)
            }
            R.id.actionToggleValues -> {
                chart.data?.dataSets?.forEach { set ->
                    set.isDrawValuesEnabled = !set.isDrawValuesEnabled
                }
                chart.invalidate()
            }
            R.id.actionToggleHighlight -> {
                chart.data?.let { data ->
                    data.isHighlightEnabled = !data.isHighlightEnabled
                    chart.invalidate()
                }
            }
            R.id.actionToggleFilled -> {
                chart.data?.dataSets?.forEach { set ->
                    set.isDrawFilledEnabled = !set.isDrawFilledEnabled
                }
                chart.invalidate()
            }
            R.id.actionToggleCircles -> {
                chart.data?.dataSets?.filterIsInstance<LineDataSet<*>>()?.forEach { set ->
                    set.isDrawCirclesEnabled = !set.isDrawCirclesEnabled
                }
                chart.invalidate()
            }
            R.id.animateX -> chart.animateX(2000)
            R.id.animateY -> chart.animateY(2000)
            R.id.animateXY -> chart.animateXY(2000, 2000)
            R.id.actionTogglePinch -> {
                chart.isPinchZoomEnabled = !chart.isPinchZoomEnabled
                chart.invalidate()
            }
            R.id.actionToggleAutoScaleMinMax -> {
                chart.isAutoScaleMinMaxEnabled = !chart.isAutoScaleMinMaxEnabled
                chart.notifyDataSetChanged()
            }
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

    override fun saveToGallery() = saveToGallery(binding.chart1, "InvertedLineChartActivity")

    override fun onValueSelected(e: Entry<*>, h: Highlight) {
        Log.i("VAL SELECTED", "Value: ${e.y}, xIndex: ${e.x}, DataSet index: ${h.dataSetIndex}")
    }

    override fun onNothingSelected() {}

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
