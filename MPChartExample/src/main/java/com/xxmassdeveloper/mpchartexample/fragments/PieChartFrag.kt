package com.xxmassdeveloper.mpchartexample.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.xxmassdeveloper.mpchartexample.R

class PieChartFrag : SimpleFragment() {
    private var chart: PieChart? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.frag_simple_pie, container, false)
        chart = v.findViewById(R.id.pieChart1)
        chart?.description?.isEnabled = false
        val tf = Typeface.createFromAsset(requireContext().assets, "OpenSans-Light.ttf")
        chart?.setCenterTextTypeface(tf)
        chart?.centerText = generateCenterText()
        chart?.setCenterTextSize(10f)
        chart?.setCenterTextTypeface(tf)

        // radius of the center hole in percent of maximum radius
        chart?.holeRadius = 45f
        chart?.transparentCircleRadius = 50f
        val l = chart?.legend
        l?.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l?.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l?.orientation = Legend.LegendOrientation.VERTICAL
        l?.setDrawInside(false)
        chart?.data = generatePieData()
        return v
    }

    private fun generateCenterText(): SpannableString {
        val s = SpannableString("Revenues\nQuarters 2015")
        s.setSpan(RelativeSizeSpan(2f), 0, 8, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 8, s.length, 0)
        return s
    }

    companion object {
        fun newInstance(): Fragment {
            return PieChartFrag()
        }
    }
}