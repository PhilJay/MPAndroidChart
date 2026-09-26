package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.databinding.ActivityLinechartTimeBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class LineChartTime : DemoBase(), SeekBar.OnSeekBarChangeListener {

    private lateinit var binding: ActivityLinechartTimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinechartTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "LineChartTime"

        binding.seekBar1.setOnSeekBarChangeListener(this)

        binding.chart1.apply {
            description.isEnabled = false
            isTouchEnabled = true
            dragDecelerationFrictionCoef = 0.9f
            isDragEnabled = true
            isScaleEnabled = true
            isDrawGridBackgroundEnabled = false
            isHighlightPerDragEnabled = true
            setBackgroundColor(Color.WHITE)
            setViewPortOffsets(0f, 0f, 0f, 0f)
        }

        binding.seekBar1.progress = 100

        binding.chart1.legend.isEnabled = false

        binding.chart1.xAxis.apply {
            position = XAxis.XAxisPosition.TOP_INSIDE
            typeface = tfLight
            textSize = 10f
            textColor = Color.WHITE
            isDrawAxisLineEnabled = false
            isDrawGridLinesEnabled = true
            textColor = Color.rgb(255, 192, 56)
            isCenterAxisLabelsEnabled = true
            granularity = 1f
            valueFormatter = object : IAxisValueFormatter {
                private val format = SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH)

                override fun getFormattedValue(value: Float, axis: AxisBase): String {
                    val millis = TimeUnit.HOURS.toMillis(value.toLong())
                    return format.format(Date(millis))
                }
            }
        }

        binding.chart1.axisLeft.apply {
            labelPosition = YAxis.YAxisLabelPosition.INSIDE_CHART
            typeface = tfLight
            textColor = ColorTemplate.holoBlue
            isDrawGridLinesEnabled = true
            isGranularityEnabled = true
            axisMinimum = 0f
            axisMaximum = 170f
            yOffset = -9f
            textColor = Color.rgb(255, 192, 56)
        }

        binding.chart1.axisRight.isEnabled = false
    }

    private fun setData(count: Int, range: Float) {
        val now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis())

        val values = ArrayList<Entry<Any?>>()

        val to = (now + count).toFloat()

        var x = now.toFloat()
        while (x < to) {
            val y = getRandom(range, 50f)
            values.add(Entry(x, y))
            x++
        }

        val set1 = LineDataSet(values, "DataSet 1").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = ColorTemplate.holoBlue
            valueTextColor = ColorTemplate.holoBlue
            lineWidth = 1.5f
            isDrawCirclesEnabled = false
            isDrawValuesEnabled = false
            fillAlpha = 65
            fillColor = ColorTemplate.holoBlue
            highlightColor = Color.rgb(244, 117, 117)
            isDrawCircleHoleEnabled = false
        }

        val data = LineData(set1)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)

        binding.chart1.data = data
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
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/LineChartTime.java")
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

        setData(binding.seekBar1.progress, 50f)

        binding.chart1.invalidate()
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "LineChartTime")

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
