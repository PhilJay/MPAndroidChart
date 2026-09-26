package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.highlight.HighlightLineSpan
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView
import com.xxmassdeveloper.mpchartexample.databinding.ActivityLinechartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class LineChartActivity1 : DemoBase(), SeekBar.OnSeekBarChangeListener {

    private lateinit var binding: ActivityLinechartBinding

    private var lineSet: LineDataSet<Any?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinechartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "LineChartActivity1"

        binding.seekBar1.setOnSeekBarChangeListener(this)
        binding.seekBar2.max = 180
        binding.seekBar2.setOnSeekBarChangeListener(this)

        binding.chart1.apply {
            setBackgroundColor(Color.WHITE)
            description.isEnabled = false
            isTouchEnabled = true
            onValueSelected(onNothingSelected = { Log.i("Nothing selected", "Nothing selected.") }) { entry, _ ->
                Log.i("Entry<*> selected", entry.toString())
                Log.i("LOW HIGH", "low: $lowestVisibleX, high: $highestVisibleX")
                Log.i("MIN MAX", "xMin: $xChartMin, xMax: $xChartMax, yMin: $yChartMin, yMax: $yChartMax")
            }
            isDrawGridBackgroundEnabled = false

            val mv = MyMarkerView(this@LineChartActivity1, R.layout.custom_marker_view)
            marker = mv

            isDragEnabled = true
            isScaleEnabled = true
            isPinchZoomEnabled = true
        }

        val xAxis = binding.chart1.xAxis
        xAxis.enableGridDashedLine(10f, 10f, 0f)

        val yAxis = binding.chart1.axisLeft
        binding.chart1.axisRight.isEnabled = false
        yAxis.enableGridDashedLine(10f, 10f, 0f)
        yAxis.axisMaximum = 200f
        yAxis.axisMinimum = -50f

        val ll1 = LimitLine(150f, "Upper Limit").apply {
            lineWidth = 4f
            enableDashedLine(10f, 10f, 0f)
            labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            textSize = 10f
            typeface = tfRegular
        }

        val ll2 = LimitLine(-30f, "Lower Limit").apply {
            lineWidth = 4f
            enableDashedLine(10f, 10f, 0f)
            labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
            textSize = 10f
            typeface = tfRegular
        }

        yAxis.isDrawLimitLinesBehindDataEnabled = true
        xAxis.isDrawLimitLinesBehindDataEnabled = true

        yAxis.addLimitLine(ll1)
        yAxis.addLimitLine(ll2)

        binding.seekBar1.progress = 45
        binding.seekBar2.progress = 180
        setData(45, 180f)

        binding.chart1.animateX(1500)

        binding.chart1.legend.form = Legend.LegendForm.LINE
    }

    private fun setData(count: Int, range: Float) {
        val values = ArrayList<Entry<Any?>>()

        for (i in 0 until count) {
            val value = (Math.random() * range).toFloat() - 30
            values.add(Entry(i.toFloat(), value, ContextCompat.getDrawable(this, R.drawable.star)))
        }

        val existing = lineSet
        if (existing != null) {
            existing.entries = values
            binding.chart1.notifyDataSetChanged()
        } else {
            val set1 = LineDataSet(values, "DataSet 1").apply {
                isDrawIconsEnabled = false
                enableDashedLine(10f, 5f, 0f)
                color = Color.BLACK
                circleColor = Color.BLACK
                lineWidth = 1f
                circleRadius = 3f
                isDrawCircleHoleEnabled = false
                formLineWidth = 1f
                formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                formSize = 15f
                valueTextSize = 9f
                enableDashedHighlightLine(10f, 5f, 0f)
                isDrawFilledEnabled = true
                fillFormatter = IFillFormatter { _, _ -> binding.chart1.axisLeft.axisMinimum }
                fillDrawable = ContextCompat.getDrawable(this@LineChartActivity1, R.drawable.fade_red)
            }

            val dataSets = ArrayList<ILineDataSet<*>>()
            dataSets.add(set1)

            lineSet = set1
            binding.chart1.data = LineData(dataSets)
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
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/LineChartActivity1.java")
                startActivity(i)
            }
            R.id.actionToggleValues -> {
                chart.data?.dataSets?.forEach { set ->
                    set.isDrawValuesEnabled = !set.isDrawValuesEnabled
                }
                chart.invalidate()
            }
            R.id.actionToggleIcons -> {
                chart.data?.dataSets?.forEach { set ->
                    set.isDrawIconsEnabled = !set.isDrawIconsEnabled
                }
                chart.invalidate()
            }
            R.id.actionToggleHighlightSpan -> {
                chart.data?.dataSets?.forEach {
                    val set = it as LineDataSet<*>
                    set.verticalHighlightIndicatorSpan = when (set.verticalHighlightIndicatorSpan) {
                        HighlightLineSpan.FULL -> HighlightLineSpan.TO_ENTRY
                        HighlightLineSpan.TO_ENTRY -> HighlightLineSpan.FROM_ENTRY
                        HighlightLineSpan.FROM_ENTRY -> HighlightLineSpan.FULL
                    }
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
            R.id.animateY -> chart.animateY(2000, Easing.EaseInCubic)
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

    override fun saveToGallery() = saveToGallery(binding.chart1, "LineChartActivity1")

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
