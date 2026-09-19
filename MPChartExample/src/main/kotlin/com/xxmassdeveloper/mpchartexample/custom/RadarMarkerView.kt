package com.xxmassdeveloper.mpchartexample.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.xxmassdeveloper.mpchartexample.databinding.RadarMarkerviewBinding
import java.text.DecimalFormat

@SuppressLint("ViewConstructor")
class RadarMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val binding = RadarMarkerviewBinding.bind(this)

    private val format = DecimalFormat("##0")

    init {
        binding.tvContent.typeface = Typeface.createFromAsset(context.assets, "OpenSans-Light.ttf")
    }

    override fun refreshContent(e: Entry<*>, highlight: Highlight) {
        binding.tvContent.text = String.format("%s %%", format.format(e.y.toDouble()))
        super.refreshContent(e, highlight)
    }

    override var offset: MPPointF
        get() = MPPointF(-(width / 2f), -height.toFloat() - 10)
        set(value) {
            super.offset = value
        }
}
