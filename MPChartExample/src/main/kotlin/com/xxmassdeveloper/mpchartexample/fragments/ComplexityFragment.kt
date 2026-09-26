package com.xxmassdeveloper.mpchartexample.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xxmassdeveloper.mpchartexample.databinding.FragSimpleLineBinding

class ComplexityFragment : SimpleFragment() {

    private var binding: FragSimpleLineBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragSimpleLineBinding.inflate(inflater, container, false)
        this.binding = binding

        val tf = Typeface.createFromAsset(requireContext().assets, "OpenSans-Light.ttf")

        binding.lineChart1.apply {
            description.isEnabled = false
            isDrawGridBackgroundEnabled = false

            data = getComplexity()
            animateX(3000)

            legend.typeface = tf
            axisLeft.typeface = tf
            axisRight.isEnabled = false
            xAxis.isEnabled = false
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        fun newInstance(): Fragment = ComplexityFragment()
    }
}
