package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.custom.CustomScatterShapeRenderer
import com.xxmassdeveloper.mpchartexample.databinding.ActivityScatterchartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class ScatterChartActivity : DemoBase(), SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener {

    private lateinit var binding: ActivityScatterchartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScatterchartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "ScatterChartActivity"

        binding.seekBar1.setOnSeekBarChangeListener(this)
        binding.seekBar2.setOnSeekBarChangeListener(this)

        binding.chart1.apply {
            description.isEnabled = false
            onChartValueSelectedListener = this@ScatterChartActivity

            isDrawGridBackgroundEnabled = false
            isTouchEnabled = true
            maxHighlightDistance = 50f

            isDragEnabled = true
            isScaleEnabled = true

            maxVisibleCount = 200
            isPinchZoomEnabled = true
        }

        binding.seekBar1.progress = 45
        binding.seekBar2.progress = 100

        binding.chart1.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.VERTICAL
            isDrawInsideEnabled = false
            typeface = tfLight
            xOffset = 5f
        }

        binding.chart1.axisLeft.apply {
            typeface = tfLight
            axisMinimum = 0f
        }

        binding.chart1.axisRight.isEnabled = false

        binding.chart1.xAxis.apply {
            typeface = tfLight
            isDrawGridLinesEnabled = false
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val count = binding.seekBar1.progress
        val range = binding.seekBar2.progress

        binding.tvXMax.text = count.toString()
        binding.tvYMax.text = range.toString()

        val values1 = ArrayList<Entry<Any?>>()
        val values2 = ArrayList<Entry<Any?>>()
        val values3 = ArrayList<Entry<Any?>>()

        for (i in 0 until count) {
            val value = (Math.random() * range).toFloat() + 3
            values1.add(Entry(i.toFloat(), value))
        }

        for (i in 0 until count) {
            val value = (Math.random() * range).toFloat() + 3
            values2.add(Entry(i + 0.33f, value))
        }

        for (i in 0 until count) {
            val value = (Math.random() * range).toFloat() + 3
            values3.add(Entry(i + 0.66f, value))
        }

        val set1 = ScatterDataSet(values1, "DS 1")
        set1.setScatterShape(ScatterChart.ScatterShape.SQUARE)
        set1.color = ColorTemplate.COLORFUL_COLORS[0]
        val set2 = ScatterDataSet(values2, "DS 2")
        set2.setScatterShape(ScatterChart.ScatterShape.CIRCLE)
        set2.scatterShapeHoleColor = ColorTemplate.COLORFUL_COLORS[3]
        set2.scatterShapeHoleRadius = 1.5f
        set2.color = ColorTemplate.COLORFUL_COLORS[1]
        val set3 = ScatterDataSet(values3, "DS 3")
        set3.shapeRenderer = CustomScatterShapeRenderer()
        set3.color = ColorTemplate.COLORFUL_COLORS[2]

        set1.scatterShapeSize = 4f
        set2.scatterShapeSize = 4f
        set3.scatterShapeSize = 4f

        val dataSets = ArrayList<IScatterDataSet<*>>()
        dataSets.add(set1)
        dataSets.add(set2)
        dataSets.add(set3)

        val data = ScatterData(dataSets)
        data.setValueTypeface(tfLight)

        binding.chart1.data = data
        binding.chart1.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.scatter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val chart = binding.chart1
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/ScatterChartActivity.java")
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
            R.id.animateX -> chart.animateX(3000)
            R.id.animateY -> chart.animateY(3000)
            R.id.animateXY -> chart.animateXY(3000, 3000)
            R.id.actionSave -> saveChartToGalleryWithPermission(chart)
        }
        return true
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "ScatterChartActivity")

    override fun onValueSelected(e: Entry<*>, h: Highlight) {
        Log.i("VAL SELECTED", "Value: ${e.y}, xIndex: ${e.x}, DataSet index: ${h.dataSetIndex}")
    }

    override fun onNothingSelected() {}

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
