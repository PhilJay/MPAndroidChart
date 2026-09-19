package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.SeekBar
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.databinding.ActivityLinechartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class MultiLineChartActivity : DemoBase(), SeekBar.OnSeekBarChangeListener, OnChartGestureListener, OnChartValueSelectedListener {

    private lateinit var binding: ActivityLinechartBinding

    private val colors = intArrayOf(
        ColorTemplate.VORDIPLOM_COLORS[0],
        ColorTemplate.VORDIPLOM_COLORS[1],
        ColorTemplate.VORDIPLOM_COLORS[2]
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinechartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "MultiLineChartActivity"

        binding.seekBar1.setOnSeekBarChangeListener(this)
        binding.seekBar2.setOnSeekBarChangeListener(this)

        binding.chart1.apply {
            onChartValueSelectedListener = this@MultiLineChartActivity
            isDrawGridBackgroundEnabled = false
            description.isEnabled = false
            isDrawBordersEnabled = false

            axisLeft.isEnabled = false
            axisRight.isDrawAxisLineEnabled = false
            axisRight.isDrawGridLinesEnabled = false
            xAxis.isDrawAxisLineEnabled = false
            xAxis.isDrawGridLinesEnabled = false

            isTouchEnabled = true
            isDragEnabled = true
            isScaleEnabled = true
            isPinchZoomEnabled = false
        }

        binding.seekBar1.progress = 20
        binding.seekBar2.progress = 100

        binding.chart1.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.VERTICAL
            isDrawInsideEnabled = false
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        binding.chart1.resetTracking()

        val count = binding.seekBar1.progress

        binding.tvXMax.text = binding.seekBar1.progress.toString()
        binding.tvYMax.text = binding.seekBar2.progress.toString()

        val dataSets = ArrayList<ILineDataSet<*>>()

        for (z in 0 until 3) {
            val values = ArrayList<Entry<Any?>>()

            for (i in 0 until count) {
                val value = (Math.random() * binding.seekBar2.progress) + 3
                values.add(Entry(i.toFloat(), value.toFloat()))
            }

            val color = colors[z % colors.size]
            val d = LineDataSet(values, "DataSet ${z + 1}").apply {
                lineWidth = 2.5f
                circleRadius = 4f
                this.color = color
                circleColor = color
            }
            dataSets.add(d)
        }

        (dataSets[0] as LineDataSet<Any?>).apply {
            enableDashedLine(10f, 10f, 0f)
            colors = ColorTemplate.VORDIPLOM_COLORS
            circleColors = ColorTemplate.VORDIPLOM_COLORS
        }

        binding.chart1.data = LineData(dataSets)
        binding.chart1.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.line, menu)
        menu.removeItem(R.id.actionToggleIcons)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val chart = binding.chart1
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/MultiLineChartActivity.java")
                startActivity(i)
            }
            R.id.actionToggleValues -> {
                chart.data?.dataSets?.forEach { set ->
                    set.isDrawValuesEnabled = !set.isDrawValuesEnabled
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
            R.id.actionSave -> saveChartToGalleryWithPermission(chart)
            R.id.animateX -> chart.animateX(2000)
            R.id.animateY -> chart.animateY(2000)
            R.id.animateXY -> chart.animateXY(2000, 2000)
        }
        return true
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "MultiLineChartActivity")

    override fun onChartGestureStart(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
        Log.i("Gesture", "START, x: ${me.x}, y: ${me.y}")
    }

    override fun onChartGestureEnd(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
        Log.i("Gesture", "END, lastGesture: $lastPerformedGesture")

        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP) {
            binding.chart1.highlightValues(emptyList())
        }
    }

    override fun onChartLongPressed(me: MotionEvent) {
        Log.i("LongPress", "Chart long pressed.")
    }

    override fun onChartDoubleTapped(me: MotionEvent) {
        Log.i("DoubleTap", "Chart double-tapped.")
    }

    override fun onChartSingleTapped(me: MotionEvent) {
        Log.i("SingleTap", "Chart single-tapped.")
    }

    override fun onChartFling(me1: MotionEvent?, me2: MotionEvent, velocityX: Float, velocityY: Float) {
        Log.i("Fling", "Chart fling. VelocityX: $velocityX, VelocityY: $velocityY")
    }

    override fun onChartScale(me: MotionEvent, scaleX: Float, scaleY: Float) {
        Log.i("Scale / Zoom", "ScaleX: $scaleX, ScaleY: $scaleY")
    }

    override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {
        Log.i("Translate / Move", "dX: $dX, dY: $dY")
    }

    override fun onValueSelected(e: Entry<*>, h: Highlight) {
        Log.i("VAL SELECTED", "Value: ${e.y}, xIndex: ${e.x}, DataSet index: ${h.dataSetIndex}")
    }

    override fun onNothingSelected() {}

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
