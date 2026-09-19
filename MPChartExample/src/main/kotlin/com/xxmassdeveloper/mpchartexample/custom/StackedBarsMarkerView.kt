package com.xxmassdeveloper.mpchartexample.custom

import android.annotation.SuppressLint
import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.xxmassdeveloper.mpchartexample.databinding.CustomMarkerViewBinding

@SuppressLint("ViewConstructor")
class StackedBarsMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val binding = CustomMarkerViewBinding.bind(this)

    override fun refreshContent(e: Entry<*>, highlight: Highlight) {
        val stackValues = (e as? BarEntry<*>)?.stackValues
        binding.tvContent.text = if (stackValues != null) {
            Utils.formatNumber(stackValues[highlight.stackIndex], 0, true)
        } else {
            Utils.formatNumber(e.y, 0, true)
        }
        super.refreshContent(e, highlight)
    }

    override var offset: MPPointF
        get() = MPPointF(-(width / 2f), -height.toFloat())
        set(value) {
            super.offset = value
        }
}
