package com.xxmassdeveloper.mpchartexample.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.xxmassdeveloper.mpchartexample.R
import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView

class ScatterChartFrag : SimpleFragment() {
    private var chart: ScatterChart? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.frag_simple_scatter, container, false)
        chart = v.findViewById(R.id.scatterChart1)
        chart?.description?.isEnabled = false
        val tf = Typeface.createFromAsset(requireContext().assets, "OpenSans-Light.ttf")
        val mv = MyMarkerView(activity, R.layout.custom_marker_view)
        mv.chartView = chart // For bounds control
        chart?.marker = mv
        chart?.setDrawGridBackground(false)
        chart?.data = generateScatterData(6, 10000f, 200)
        val xAxis = chart?.xAxis
        xAxis?.isEnabled = true
        xAxis?.position = XAxisPosition.BOTTOM
        val leftAxis = chart?.axisLeft
        leftAxis?.typeface = tf
        val rightAxis = chart?.axisRight
        rightAxis?.typeface = tf
        rightAxis?.setDrawGridLines(false)
        val l = chart?.legend
        l?.isWordWrapEnabled = true
        l?.typeface = tf
        l?.formSize = 14f
        l?.textSize = 9f

        // increase the space between legend & bottom and legend & content
        l?.yOffset = 13f
        chart?.extraBottomOffset = 16f
        return v
    }

    companion object {
        fun newInstance(): Fragment {
            return ScatterChartFrag()
        }
    }
}