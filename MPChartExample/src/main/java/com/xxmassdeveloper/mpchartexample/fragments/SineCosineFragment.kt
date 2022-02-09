package com.xxmassdeveloper.mpchartexample.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.xxmassdeveloper.mpchartexample.R

class SineCosineFragment : SimpleFragment() {
    private var chart: LineChart? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.frag_simple_line, container, false)
        chart = v.findViewById(R.id.lineChart1)
        chart?.description?.isEnabled = false
        chart?.setDrawGridBackground(false)
        chart?.data = generateLineData()
        chart?.animateX(3000)
        val tf = Typeface.createFromAsset(requireContext().assets, "OpenSans-Light.ttf")
        val l = chart?.legend
        l?.typeface = tf
        val leftAxis = chart?.axisLeft
        leftAxis?.typeface = tf
        leftAxis?.axisMaximum = 1.2f
        leftAxis?.axisMinimum = -1.2f
        chart?.axisRight?.isEnabled = false
        val xAxis = chart?.xAxis
        xAxis?.isEnabled = false
        return v
    }

    companion object {
        fun newInstance(): Fragment {
            return SineCosineFragment()
        }
    }
}