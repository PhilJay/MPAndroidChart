package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.xxmassdeveloper.mpchartexample.databinding.ActivityPerformanceLinechartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class PerformanceLineChart : DemoBase(), SeekBar.OnSeekBarChangeListener {

    private lateinit var binding: ActivityPerformanceLinechartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerformanceLinechartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "PerformanceLineChart"

        binding.seekbarValues.setOnSeekBarChangeListener(this)

        binding.chart1.apply {
            isDrawGridBackgroundEnabled = false
            description.isEnabled = false
            isTouchEnabled = true
            isDragEnabled = true
            isScaleEnabled = true
            isPinchZoomEnabled = false

            axisLeft.isDrawGridLinesEnabled = false
            axisRight.isEnabled = false
            xAxis.isDrawGridLinesEnabled = true
            xAxis.isDrawAxisLineEnabled = false
        }

        binding.seekbarValues.progress = 9000

        binding.chart1.invalidate()
    }

    private fun setData(count: Int, range: Float) {
        val values = ArrayList<Entry<Any?>>()

        for (i in 0 until count) {
            val value = (Math.random() * (range + 1)).toFloat() + 3
            values.add(Entry(i * 0.001f, value))
        }

        val set1 = LineDataSet(values, "DataSet 1").apply {
            color = Color.BLACK
            lineWidth = 0.5f
            isDrawValuesEnabled = false
            isDrawCirclesEnabled = false
            mode = LineDataSet.Mode.LINEAR
            isDrawFilledEnabled = false
        }

        binding.chart1.data = LineData(set1)

        binding.chart1.legend.isEnabled = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.only_github, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/PerformanceLineChart.java")
                startActivity(i)
            }
        }
        return true
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val count = binding.seekbarValues.progress + 1000
        binding.tvValueCount.text = count.toString()

        binding.chart1.resetTracking()

        setData(count, 500f)

        binding.chart1.invalidate()
    }

    override fun saveToGallery() {}

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
