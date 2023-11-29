package com.xxmassdeveloper.mpchartexample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.xxmassdeveloper.mpchartexample.DataTools.Companion.setData
import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView
import com.xxmassdeveloper.mpchartexample.databinding.ActivityLinechartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

/**
 * Example of a heavily customized [LineChart] with limit lines, custom line shapes, etc.
 */
class LineChartActivity1 : DemoBase(), OnSeekBarChangeListener, OnChartValueSelectedListener {

    private lateinit var binding: ActivityLinechartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinechartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        title = "LineChartActivity1"
        binding.seekBarX.setOnSeekBarChangeListener(this)
        binding.seekBarY.max = 180
        binding.seekBarY.setOnSeekBarChangeListener(this)

        // background color
        binding.chart1.setBackgroundColor(Color.WHITE)

        // disable description text
        binding.chart1.description.isEnabled = false

        // enable touch gestures
        binding.chart1.setTouchEnabled(true)

        // set listeners
        binding.chart1.setOnChartValueSelectedListener(this)
        binding.chart1.setDrawGridBackground(false)

        // create marker to display box when values are selected
        val mv = MyMarkerView(this, R.layout.custom_marker_view)

        // Set the marker to the chart
        mv.chartView = binding.chart1
        binding.chart1.marker = mv

        // enable scaling and dragging
        binding.chart1.isDragEnabled = true
        binding.chart1.setScaleEnabled(true)

        // force pinch zoom along both axis
        binding.chart1.setPinchZoom(true)

        // vertical grid lines
        binding.chart1.xAxis.enableGridDashedLine(10f, 10f, 0f)

        // disable dual axis (only use LEFT axis)
        binding.chart1.axisRight.isEnabled = false

        // horizontal grid lines
        binding.chart1.axisLeft.enableGridDashedLine(10f, 10f, 0f)

        // axis range
        binding.chart1.axisLeft.axisMaximum = 200f
        binding.chart1.axisLeft.axisMinimum = -50f
        val llXAxis = LimitLine(9f, "Index 10")
        llXAxis.lineWidth = 4f
        llXAxis.enableDashedLine(10f, 10f, 0f)
        llXAxis.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        llXAxis.textSize = 10f
        llXAxis.typeface = tfRegular
        val limitLine1 = LimitLine(150f, "Upper Limit")
        limitLine1.lineWidth = 4f
        limitLine1.enableDashedLine(10f, 10f, 0f)
        limitLine1.labelPosition = LimitLabelPosition.RIGHT_TOP
        limitLine1.textSize = 10f
        limitLine1.typeface = tfRegular
        val limitLine2 = LimitLine(-30f, "Lower Limit")
        limitLine2.lineWidth = 4f
        limitLine2.enableDashedLine(10f, 10f, 0f)
        limitLine2.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        limitLine2.textSize = 10f
        limitLine2.typeface = tfRegular

        // draw limit lines behind data instead of on top
        binding.chart1.axisLeft.setDrawLimitLinesBehindData(true)
        binding.chart1.xAxis.setDrawLimitLinesBehindData(true)

        // add limit lines
        binding.chart1.axisLeft.addLimitLine(limitLine1)
        binding.chart1.axisLeft.addLimitLine(limitLine2)
        //xAxis.addLimitLine(llXAxis);

        // add data
        binding.seekBarX.progress = 45
        binding.seekBarY.progress = 180
        Log.d("setDataCreate", "\$count=45 range=180f")
        setData(this, binding.chart1, 45, 180f)

        // draw points over time
        binding.chart1.animateX(1500)

        // get the legend (only possible after setting data)
        val legend = binding.chart1.legend
        legend.form = LegendForm.LINE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.line, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data =
                    Uri.parse("https://github.com/AppDevNext/AndroidChart/blob/master/MPChartExample/src/main/java/com/xxmassdeveloper/mpchartexample/LineChartActivity1.java")
                startActivity(i)
            }

            R.id.actionToggleValues -> {
                binding.chart1.data?.dataSets?.forEach {
                    val set = it as LineDataSet
                    set.setDrawValues(!set.isDrawValuesEnabled)
                }
                binding.chart1.invalidate()
            }

            R.id.actionToggleIcons -> {
                binding.chart1.data?.dataSets?.forEach {
                    val set = it as LineDataSet
                    set.setDrawIcons(!set.isDrawIconsEnabled)
                    binding.chart1.invalidate()
                }
            }

            R.id.actionToggleHighlight -> {
                binding.chart1.data?.let {
                    it.isHighlightEnabled = !it.isHighlightEnabled
                    binding.chart1.invalidate()
                }
            }

            R.id.actionToggleFilled -> {
                binding.chart1.data?.dataSets?.let {
                    val set = it as LineDataSet
                    set.setDrawFilled(!set.isDrawFilledEnabled)
                    binding.chart1.invalidate()
                }
            }

            R.id.actionToggleCircles -> {
                binding.chart1.data?.dataSets?.forEach {
                    val set = it as LineDataSet
                    set.setDrawCircles(!set.isDrawCirclesEnabled)
                }
                binding.chart1.invalidate()
            }

            R.id.actionToggleCubic -> {
                binding.chart1.data?.dataSets?.forEach {
                    val set = it as LineDataSet
                    set.mode = if (set.mode == LineDataSet.Mode.CUBIC_BEZIER) LineDataSet.Mode.LINEAR else LineDataSet.Mode.CUBIC_BEZIER
                }
                binding.chart1.invalidate()
            }

            R.id.actionToggleStepped -> {
                binding.chart1.data?.dataSets?.forEach {
                    val set = it as LineDataSet
                    set.mode = if (set.mode == LineDataSet.Mode.STEPPED) LineDataSet.Mode.LINEAR else LineDataSet.Mode.STEPPED
                }
                binding.chart1.invalidate()
            }

            R.id.actionToggleHorizontalCubic -> {
                binding.chart1.data?.dataSets?.forEach {
                    val set = it as LineDataSet
                    set.mode = if (set.mode == LineDataSet.Mode.HORIZONTAL_BEZIER) LineDataSet.Mode.LINEAR else LineDataSet.Mode.HORIZONTAL_BEZIER
                }
                binding.chart1.invalidate()
            }

            R.id.actionTogglePinch -> {
                binding.chart1.setPinchZoom(!binding.chart1.isPinchZoomEnabled)
                binding.chart1.invalidate()
            }

            R.id.actionToggleAutoScaleMinMax -> {
                binding.chart1.isAutoScaleMinMaxEnabled = !binding.chart1.isAutoScaleMinMaxEnabled
                binding.chart1.notifyDataSetChanged()
            }

            R.id.animateX -> binding.chart1.animateX(2000)
            R.id.animateY -> binding.chart1.animateY(2000, Easing.EaseInCubic)
            R.id.animateXY -> binding.chart1.animateXY(2000, 2000)
            R.id.actionSave -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveToGallery()
                } else {
                    requestStoragePermission(binding.chart1)
                }
            }
        }
        return true
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        binding.tvXMax.text = binding.seekBarX.progress.toString()
        binding.tvYMax.text = binding.seekBarY.progress.toString()
        setData(this, binding.chart1, binding.seekBarX.progress, binding.seekBarY.progress.toFloat())

        // redraw
        binding.chart1.invalidate()
    }

    override fun saveToGallery() {
        saveToGallery(binding.chart1, "LineChartActivity1")
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
    override fun onValueSelected(e: Entry, h: Highlight) {
        Log.i("Entry selected", e.toString())
        Log.i("LOW HIGH", "low: " + binding.chart1.lowestVisibleX + ", high: " + binding.chart1.highestVisibleX)
        Log.i(
            "MIN MAX",
            "xMin: " + binding.chart1.xChartMin + ", xMax: " + binding.chart1.xChartMax + ", yMin: " + binding.chart1.yChartMin + ", yMax: " + binding.chart1.yChartMax
        )
    }

    override fun onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.")
    }
}
