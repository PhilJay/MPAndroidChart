package com.xxmassdeveloper.mpchartexample

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import com.xxmassdeveloper.mpchartexample.DataTools.Companion.getValues
import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class SpecificPositionsLineChartActivity : DemoBase(), OnSeekBarChangeListener, OnChartGestureListener, OnChartValueSelectedListener {

    private var mChart: LineChart? = null
    private var mSeekBarX: SeekBar? = null
    private var mSeekBarY: SeekBar? = null
    private var tvX: TextView? = null
    private var tvY: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_linechart)
        tvX = findViewById<View>(R.id.tvXMax) as TextView
        tvY = findViewById<View>(R.id.tvYMax) as TextView
        mSeekBarX = findViewById<View>(R.id.seekBarX) as SeekBar
        mSeekBarY = findViewById<View>(R.id.seekBarY) as SeekBar
        mSeekBarX!!.progress = 45
        mSeekBarY!!.progress = 100
        mSeekBarY!!.setOnSeekBarChangeListener(this)
        mSeekBarX!!.setOnSeekBarChangeListener(this)
        mChart = findViewById<View>(R.id.chart1) as LineChart
        mChart!!.onChartGestureListener = this
        mChart!!.setOnChartValueSelectedListener(this)
        mChart!!.setDrawGridBackground(false)

        // no description text
        mChart!!.description.isEnabled = false

        // enable touch gestures
        mChart!!.setTouchEnabled(true)

        // enable scaling and dragging
        mChart!!.isDragEnabled = true
        mChart!!.setScaleEnabled(true)
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart!!.setPinchZoom(true)

        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        val mv = MyMarkerView(this, R.layout.custom_marker_view)
        mv.chartView = mChart // For bounds control
        mChart!!.marker = mv // Set the marker to the chart

        // x-axis limit line
        val llXAxis = LimitLine(10f, "Index 10")
        llXAxis.lineWidth = 4f
        llXAxis.enableDashedLine(10f, 10f, 0f)
        llXAxis.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        llXAxis.textSize = 10f
        val xAxis = mChart!!.xAxis
        xAxis.enableGridDashedLine(10f, 10f, 0f)
        xAxis.isShowSpecificPositions = true
        xAxis.specificPositions = floatArrayOf(20f, 30f, 60f)
        val tf = Typeface.createFromAsset(assets, "OpenSans-Regular.ttf")
        val ll1 = LimitLine(150f, "Upper Limit")
        ll1.lineWidth = 4f
        ll1.enableDashedLine(10f, 10f, 0f)
        ll1.labelPosition = LimitLabelPosition.RIGHT_TOP
        ll1.textSize = 10f
        ll1.typeface = tf
        val ll2 = LimitLine(-30f, "Lower Limit")
        ll2.lineWidth = 4f
        ll2.enableDashedLine(10f, 10f, 0f)
        ll2.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        ll2.textSize = 10f
        ll2.typeface = tf
        val leftAxis = mChart!!.axisLeft
        leftAxis.removeAllLimitLines() // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1)
        leftAxis.addLimitLine(ll2)
        leftAxis.axisMaximum = 200f
        leftAxis.axisMinimum = -50f
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.setDrawZeroLine(false)
        leftAxis.isShowSpecificPositions = true
        leftAxis.specificPositions = floatArrayOf(0f, 10f, 20f, 50f, 100f, 300f)

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true)
        mChart!!.axisRight.isEnabled = false
        setData(45, 100f)
        mChart!!.animateX(2500)
        //mChart.invalidate();

        // get the legend (only possible after setting data)
        val l = mChart!!.legend

        // modify the legend ...
        l.form = LegendForm.LINE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.line, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionToggleValues -> {
                val sets = mChart!!.data.dataSets
                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.setDrawValues(!set.isDrawValuesEnabled)
                }
                mChart!!.invalidate()
            }

            R.id.actionToggleHighlight -> {
                if (mChart!!.data != null) {
                    mChart!!.data.isHighlightEnabled = !mChart!!.data.isHighlightEnabled
                    mChart!!.invalidate()
                }
            }

            R.id.actionToggleFilled -> {
                val sets = mChart!!.data.dataSets
                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.setDrawFilled(!set.isDrawFilledEnabled)
                }
                mChart!!.invalidate()
            }

            R.id.actionToggleCircles -> {
                val sets = mChart!!.data.dataSets
                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.setDrawCircles(!set.isDrawCirclesEnabled)
                }
                mChart!!.invalidate()
            }

            R.id.actionToggleCubic -> {
                val sets = mChart!!.data.dataSets
                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.mode = if (set.mode == LineDataSet.Mode.CUBIC_BEZIER) LineDataSet.Mode.LINEAR else LineDataSet.Mode.CUBIC_BEZIER
                }
                mChart!!.invalidate()
            }

            R.id.actionToggleStepped -> {
                val sets = mChart!!.data.dataSets
                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.mode = if (set.mode == LineDataSet.Mode.STEPPED) LineDataSet.Mode.LINEAR else LineDataSet.Mode.STEPPED
                }
                mChart!!.invalidate()
            }

            R.id.actionToggleHorizontalCubic -> {
                val sets = mChart!!.data.dataSets
                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.mode = if (set.mode == LineDataSet.Mode.HORIZONTAL_BEZIER) LineDataSet.Mode.LINEAR else LineDataSet.Mode.HORIZONTAL_BEZIER
                }
                mChart!!.invalidate()
            }

            R.id.actionTogglePinch -> {
                mChart!!.setPinchZoom(!mChart!!.isPinchZoomEnabled)
                mChart!!.invalidate()
            }

            R.id.actionToggleAutoScaleMinMax -> {
                mChart!!.isAutoScaleMinMaxEnabled = !mChart!!.isAutoScaleMinMaxEnabled
                mChart!!.notifyDataSetChanged()
            }

            R.id.animateX -> mChart!!.animateX(3000)
            R.id.animateY -> mChart!!.animateY(3000, Easing.EaseInCubic)
            R.id.animateXY -> mChart!!.animateXY(3000, 3000)
            R.id.actionSave -> {
                if (mChart!!.saveToPath("title" + System.currentTimeMillis(), "")) {
                    Toast.makeText(applicationContext, "Saving SUCCESSFUL!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Saving FAILED!", Toast.LENGTH_SHORT).show()
                }

                // mChart.saveToGallery("title"+System.currentTimeMillis())
            }
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        tvX!!.text = "" + (mSeekBarX!!.progress + 1)
        tvY!!.text = "" + mSeekBarY!!.progress
        setData(mSeekBarX!!.progress + 1, mSeekBarY!!.progress.toFloat())

        // redraw
        mChart!!.invalidate()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        // TODO Auto-generated method stub
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        // TODO Auto-generated method stub
    }

    private fun setData(count: Int, range: Float) {
        val values = ArrayList<Entry>()
        val sampleValues = getValues(100)
        for (i in 0 until count) {
            val `val` = (sampleValues[i]!!.toFloat() * range) + 3
            values.add(Entry(i.toFloat(), `val`))
        }
        val set1: LineDataSet
        if (mChart!!.data != null && mChart!!.data.dataSetCount > 0) {
            set1 = mChart!!.data.getDataSetByIndex(0) as LineDataSet
            set1.entries = values
            mChart!!.data.notifyDataChanged()
            mChart!!.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "DataSet 1")

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f)
            set1.enableDashedHighlightLine(10f, 5f, 0f)
            set1.color = Color.BLACK
            set1.setCircleColor(Color.BLACK)
            set1.lineWidth = 1f
            set1.circleRadius = 3f
            set1.setDrawCircleHole(false)
            set1.valueTextSize = 9f
            set1.setDrawFilled(true)
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f
            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(this, R.drawable.fade_red)
                set1.fillDrawable = drawable
            } else {
                set1.fillColor = Color.BLACK
            }
            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the datasets

            // create a data object with the datasets
            val data = LineData(dataSets)

            // set data
            mChart!!.data = data
        }
    }

    override fun onChartGestureStart(me: MotionEvent, lastPerformedGesture: ChartGesture) {
        Log.i("Gesture", "START, x: " + me.x + ", y: " + me.y)
    }

    override fun onChartGestureEnd(me: MotionEvent, lastPerformedGesture: ChartGesture) {
        Log.i("Gesture", "END, lastGesture: $lastPerformedGesture")

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartGesture.SINGLE_TAP) {
            mChart!!.highlightValues(null) // or highlightTouch(null) for callback to onNothingSelected(...)
        }
    }

    override fun onChartLongPressed(me: MotionEvent) {
        Log.i("LongPress", "Chart longpressed.")
    }

    override fun onChartDoubleTapped(me: MotionEvent) {
        Log.i("DoubleTap", "Chart double-tapped.")
    }

    override fun onChartSingleTapped(me: MotionEvent) {
        Log.i("SingleTap", "Chart single-tapped.")
    }

    override fun onChartFling(me1: MotionEvent, me2: MotionEvent, velocityX: Float, velocityY: Float) {
        Log.i("Fling", "Chart flinged. VeloX: $velocityX, VeloY: $velocityY")
    }

    override fun onChartScale(me: MotionEvent, scaleX: Float, scaleY: Float) {
        Log.i("Scale / Zoom", "ScaleX: $scaleX, ScaleY: $scaleY")
    }

    override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {
        Log.i("Translate / Move", "dX: $dX, dY: $dY")
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        Log.i("Entry selected", e.toString())
        Log.i("LOWHIGH", "low: " + mChart!!.lowestVisibleX + ", high: " + mChart!!.highestVisibleX)
        Log.i("MIN MAX", "xmin: " + mChart!!.xChartMin + ", xmax: " + mChart!!.xChartMax + ", ymin: " + mChart!!.yChartMin + ", ymax: " + mChart!!.yChartMax)
    }

    override fun onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.")
    }

    override fun saveToGallery() {
        saveToGallery(mChart, "SpecificPositionsLineChartActivity")
    }
}
