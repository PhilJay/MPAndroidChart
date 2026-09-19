package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.databinding.ActivityLinechartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class LineChartActivity2 : DemoBase(), SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener {

    private lateinit var binding: ActivityLinechartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinechartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "LineChartActivity2"

        binding.seekBar1.setOnSeekBarChangeListener(this)
        binding.seekBar2.setOnSeekBarChangeListener(this)

        binding.chart1.apply {
            onChartValueSelectedListener = this@LineChartActivity2
            description.isEnabled = false
            isTouchEnabled = true
            dragDecelerationFrictionCoef = 0.9f
            isDragEnabled = true
            isScaleEnabled = true
            isDrawGridBackgroundEnabled = false
            isHighlightPerDragEnabled = true
            isPinchZoomEnabled = true
            setBackgroundColor(Color.LTGRAY)
        }

        binding.seekBar1.progress = 20
        binding.seekBar2.progress = 30

        binding.chart1.animateX(1500)

        binding.chart1.legend.apply {
            form = Legend.LegendForm.LINE
            typeface = tfLight
            textSize = 11f
            textColor = Color.WHITE
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.HORIZONTAL
            isDrawInsideEnabled = false
        }

        binding.chart1.xAxis.apply {
            typeface = tfLight
            textSize = 11f
            textColor = Color.WHITE
            isDrawGridLinesEnabled = false
            isDrawAxisLineEnabled = false
        }

        binding.chart1.axisLeft.apply {
            typeface = tfLight
            textColor = ColorTemplate.holoBlue
            axisMaximum = 200f
            axisMinimum = 0f
            isDrawGridLinesEnabled = true
            isGranularityEnabled = true
        }

        binding.chart1.axisRight.apply {
            typeface = tfLight
            textColor = Color.RED
            axisMaximum = 900f
            axisMinimum = -200f
            isDrawGridLinesEnabled = false
            isDrawZeroLineEnabled = false
            isGranularityEnabled = false
        }
    }

    private fun setData(count: Int, range: Float) {
        val values1 = ArrayList<Entry<Any?>>()
        for (i in 0 until count) {
            val value = (Math.random() * (range / 2f)).toFloat() + 50
            values1.add(Entry(i.toFloat(), value))
        }

        val values2 = ArrayList<Entry<Any?>>()
        for (i in 0 until count) {
            val value = (Math.random() * range).toFloat() + 450
            values2.add(Entry(i.toFloat(), value))
        }

        val values3 = ArrayList<Entry<Any?>>()
        for (i in 0 until count) {
            val value = (Math.random() * range).toFloat() + 500
            values3.add(Entry(i.toFloat(), value))
        }

        val data = binding.chart1.data

        if (data != null && data.dataSetCount > 0) {
            val set1 = data.getDataSetByIndex(0) as LineDataSet<Any?>
            val set2 = data.getDataSetByIndex(1) as LineDataSet<Any?>
            val set3 = data.getDataSetByIndex(2) as LineDataSet<Any?>
            set1.entries = values1
            set2.entries = values2
            set3.entries = values3
            data.notifyDataChanged()
            binding.chart1.notifyDataSetChanged()
        } else {
            val set1 = LineDataSet(values1, "DataSet 1").apply {
                axisDependency = YAxis.AxisDependency.LEFT
                color = ColorTemplate.holoBlue
                circleColor = Color.WHITE
                lineWidth = 2f
                circleRadius = 3f
                fillAlpha = 65
                fillColor = ColorTemplate.holoBlue
                highlightColor = Color.rgb(244, 117, 117)
                isDrawCircleHoleEnabled = false
            }

            val set2 = LineDataSet(values2, "DataSet 2").apply {
                axisDependency = YAxis.AxisDependency.RIGHT
                color = Color.RED
                circleColor = Color.WHITE
                lineWidth = 2f
                circleRadius = 3f
                fillAlpha = 65
                fillColor = Color.RED
                isDrawCircleHoleEnabled = false
                highlightColor = Color.rgb(244, 117, 117)
            }

            val set3 = LineDataSet(values3, "DataSet 3").apply {
                axisDependency = YAxis.AxisDependency.RIGHT
                color = Color.YELLOW
                circleColor = Color.WHITE
                lineWidth = 2f
                circleRadius = 3f
                fillAlpha = 65
                fillColor = ColorTemplate.colorWithAlpha(Color.YELLOW, 200)
                isDrawCircleHoleEnabled = false
                highlightColor = Color.rgb(244, 117, 117)
            }

            val lineData = LineData(set1, set2, set3)
            lineData.setValueTextColor(Color.WHITE)
            lineData.setValueTextSize(9f)

            binding.chart1.data = lineData
        }
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
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/LineChartActivity2.java")
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
            R.id.actionToggleCubic -> {
                chart.data?.dataSets?.filterIsInstance<LineDataSet<*>>()?.forEach { set ->
                    set.mode = if (set.mode == LineDataSet.Mode.CUBIC_BEZIER) LineDataSet.Mode.LINEAR else LineDataSet.Mode.CUBIC_BEZIER
                }
                chart.invalidate()
            }
            R.id.actionToggleStepped -> {
                chart.data?.dataSets?.filterIsInstance<LineDataSet<*>>()?.forEach { set ->
                    set.mode = if (set.mode == LineDataSet.Mode.STEPPED) LineDataSet.Mode.LINEAR else LineDataSet.Mode.STEPPED
                }
                chart.invalidate()
            }
            R.id.actionToggleHorizontalCubic -> {
                chart.data?.dataSets?.filterIsInstance<LineDataSet<*>>()?.forEach { set ->
                    set.mode = if (set.mode == LineDataSet.Mode.HORIZONTAL_BEZIER) LineDataSet.Mode.LINEAR else LineDataSet.Mode.HORIZONTAL_BEZIER
                }
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

    override fun saveToGallery() = saveToGallery(binding.chart1, "LineChartActivity2")

    override fun onValueSelected(e: Entry<*>, h: Highlight) {
        Log.i("Entry<*> selected", e.toString())

        val axis = binding.chart1.data?.getDataSetByIndex(h.dataSetIndex)?.axisDependency ?: return
        binding.chart1.centerViewToAnimated(e.x, e.y, axis, 500)
    }

    override fun onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.")
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
