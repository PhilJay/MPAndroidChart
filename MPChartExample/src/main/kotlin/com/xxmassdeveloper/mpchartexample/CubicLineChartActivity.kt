package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.xxmassdeveloper.mpchartexample.databinding.ActivityLinechartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class CubicLineChartActivity : DemoBase(), SeekBar.OnSeekBarChangeListener {

    private lateinit var binding: ActivityLinechartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinechartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "CubicLineChartActivity"

        binding.chart1.apply {
            setViewPortOffsets(0f, 0f, 0f, 0f)
            setBackgroundColor(Color.rgb(104, 241, 175))
            description.isEnabled = false
            isTouchEnabled = true
            isDragEnabled = true
            isScaleEnabled = true
            isPinchZoomEnabled = false
            isDrawGridBackgroundEnabled = false
            maxHighlightDistance = 300f

            xAxis.isEnabled = false

            axisLeft.apply {
                typeface = tfLight
                labelCount = 6
                textColor = Color.WHITE
                labelPosition = YAxis.YAxisLabelPosition.INSIDE_CHART
                isDrawGridLinesEnabled = false
                axisLineColor = Color.WHITE
            }

            axisRight.isEnabled = false
        }

        binding.seekBar2.setOnSeekBarChangeListener(this)
        binding.seekBar1.setOnSeekBarChangeListener(this)

        binding.seekBar1.max = 700

        binding.seekBar1.progress = 45
        binding.seekBar2.progress = 100

        binding.chart1.legend.isEnabled = false

        binding.chart1.animateXY(2000, 2000)

        binding.chart1.invalidate()
    }

    private fun setData(count: Int, range: Float) {
        val values = ArrayList<Entry<Any?>>()

        for (i in 0 until count) {
            val value = (Math.random() * (range + 1)).toFloat() + 20
            values.add(Entry(i.toFloat(), value))
        }

        val data = binding.chart1.data

        if (data != null && data.dataSetCount > 0) {
            val set1 = data.getDataSetByIndex(0) as LineDataSet<Any?>
            set1.entries = values
            data.notifyDataChanged()
            binding.chart1.notifyDataSetChanged()
        } else {
            val set1 = LineDataSet(values, "DataSet 1").apply {
                mode = LineDataSet.Mode.CUBIC_BEZIER
                cubicIntensity = 0.2f
                isDrawFilledEnabled = true
                isDrawCirclesEnabled = false
                lineWidth = 1.8f
                circleRadius = 4f
                circleColor = Color.WHITE
                highlightColor = Color.rgb(244, 117, 117)
                color = Color.WHITE
                fillColor = Color.WHITE
                fillAlpha = 100
                isHorizontalHighlightIndicatorEnabled = false
                fillFormatter = IFillFormatter { _, _ -> binding.chart1.axisLeft.axisMinimum }
            }

            val lineData = LineData(set1)
            lineData.setValueTypeface(tfLight)
            lineData.setValueTextSize(9f)
            lineData.setDrawValues(false)

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
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/CubicLineChartActivity.java")
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

    override fun saveToGallery() = saveToGallery(binding.chart1, "CubicLineChartActivity")

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
