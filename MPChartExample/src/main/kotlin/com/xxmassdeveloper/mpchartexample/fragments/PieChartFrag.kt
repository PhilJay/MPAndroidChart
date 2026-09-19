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
import com.github.mikephil.charting.components.Legend
import com.xxmassdeveloper.mpchartexample.databinding.FragSimplePieBinding

class PieChartFrag : SimpleFragment() {

    private var binding: FragSimplePieBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragSimplePieBinding.inflate(inflater, container, false)
        this.binding = binding

        val tf = Typeface.createFromAsset(requireContext().assets, "OpenSans-Light.ttf")

        binding.pieChart1.apply {
            description.isEnabled = false

            centerTextTypeface = tf
            centerText = generateCenterText()
            centerTextSize = 10f

            holeRadius = 45f
            transparentCircleRadius = 50f

            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
                isDrawInsideEnabled = false
            }

            data = generatePieData()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun generateCenterText(): SpannableString {
        val s = SpannableString("Revenues\nQuarters 2015")
        s.setSpan(RelativeSizeSpan(2f), 0, 8, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 8, s.length, 0)
        return s
    }

    companion object {
        fun newInstance(): Fragment = PieChartFrag()
    }
}
