package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class PerformanceLineChart : DemoBase(), OnSeekBarChangeListener {
    private var chart: LineChart? = null
    private var seekBarValues: SeekBar? = null
    private var tvCount: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_performance_linechart)
        title = "PerformanceLineChart"
        tvCount = findViewById(R.id.tvValueCount)
        seekBarValues = findViewById(R.id.seekbarValues)
        seekBarValues?.setOnSeekBarChangeListener(this)
        chart = findViewById(R.id.chart1)
        chart?.setDrawGridBackground(false)

        // no description text
        chart?.description?.isEnabled = false

        // enable touch gestures
        chart?.setTouchEnabled(true)

        // enable scaling and dragging
        chart?.isDragEnabled = true
        chart?.setScaleEnabled(true)

        // if disabled, scaling can be done on x- and y-axis separately
        chart?.setPinchZoom(false)
        chart?.axisLeft?.setDrawGridLines(false)
        chart?.axisRight?.isEnabled = false
        chart?.xAxis?.setDrawGridLines(true)
        chart?.xAxis?.setDrawAxisLine(false)
        seekBarValues?.progress = 9000

        // don't forget to refresh the drawing
        chart?.invalidate()
    }

    private fun setData(count: Int, range: Float) {
        val values = ArrayList<Entry>()
        for (i in 0 until count) {
            val `val` = (Math.random() * (range + 1)).toFloat() + 3
            values.add(Entry(i * 0.001f, `val`))
        }

        // create a dataset and give it a type
        val set1 = LineDataSet(values, "DataSet 1")
        set1.color = Color.BLACK
        set1.lineWidth = 0.5f
        set1.setDrawValues(false)
        set1.setDrawCircles(false)
        set1.mode = LineDataSet.Mode.LINEAR
        set1.setDrawFilled(false)

        // create a data object with the data sets
        val data = LineData(set1)

        // set data
        chart!!.data = data

        // get the legend (only possible after setting data)
        val l = chart!!.legend
        l.isEnabled = false
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
                    Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/PerformanceLineChart.java")
                startActivity(i)
            }
        }
        return true
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val count = seekBarValues!!.progress + 1000
        tvCount!!.text = count.toString()
        chart!!.resetTracking()
        setData(count, 500f)

        // redraw
        chart!!.invalidate()
    }

    public override fun saveToGallery() { /* Intentionally left empty */
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}