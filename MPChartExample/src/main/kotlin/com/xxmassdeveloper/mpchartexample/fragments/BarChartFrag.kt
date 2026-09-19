package com.xxmassdeveloper.mpchartexample.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.xxmassdeveloper.mpchartexample.R
import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView
import com.xxmassdeveloper.mpchartexample.databinding.FragSimpleBarBinding

class BarChartFrag : SimpleFragment(), OnChartGestureListener {

    private var binding: FragSimpleBarBinding? = null
    private lateinit var chart: BarChart

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragSimpleBarBinding.inflate(inflater, container, false)
        this.binding = binding

        val tf = Typeface.createFromAsset(requireContext().assets, "OpenSans-Light.ttf")

        chart = BarChart(requireContext()).apply {
            description.isEnabled = false
            onChartGestureListener = this@BarChartFrag

            val mv = MyMarkerView(requireContext(), R.layout.custom_marker_view)
            marker = mv

            isDrawGridBackgroundEnabled = false
            isDrawBarShadowEnabled = false

            data = generateBarData(1, 20000f, 12)

            legend.typeface = tf

            axisLeft.typeface = tf
            axisLeft.axisMinimum = 0f

            axisRight.isEnabled = false
            xAxis.isEnabled = false
        }

        binding.parentLayout.addView(chart)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onChartGestureStart(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
        Log.i("Gesture", "START")
    }

    override fun onChartGestureEnd(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
        Log.i("Gesture", "END")
        chart.highlightValues(emptyList())
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

    companion object {
        fun newInstance(): Fragment = BarChartFrag()
    }
}
