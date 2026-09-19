package com.xxmassdeveloper.mpchartexample.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.XAxis
import com.xxmassdeveloper.mpchartexample.R
import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView
import com.xxmassdeveloper.mpchartexample.databinding.FragSimpleScatterBinding

class ScatterChartFrag : SimpleFragment() {

    private var binding: FragSimpleScatterBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragSimpleScatterBinding.inflate(inflater, container, false)
        this.binding = binding

        val tf = Typeface.createFromAsset(requireContext().assets, "OpenSans-Light.ttf")

        binding.scatterChart1.apply {
            description.isEnabled = false

            val mv = MyMarkerView(requireContext(), R.layout.custom_marker_view)
            marker = mv

            isDrawGridBackgroundEnabled = false
            data = generateScatterData(6, 10000f, 200)

            xAxis.isEnabled = true
            xAxis.position = XAxis.XAxisPosition.BOTTOM

            axisLeft.typeface = tf

            axisRight.typeface = tf
            axisRight.isDrawGridLinesEnabled = false

            legend.apply {
                isWordWrapEnabled = true
                typeface = tf
                formSize = 14f
                textSize = 9f
                yOffset = 13f
            }
            extraBottomOffset = 16f
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        fun newInstance(): Fragment = ScatterChartFrag()
    }
}
