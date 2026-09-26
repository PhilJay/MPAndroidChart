package com.xxmassdeveloper.mpchartexample.custom

import android.annotation.SuppressLint
import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.charts.BarLineChartBase
import com.xxmassdeveloper.mpchartexample.R
import com.xxmassdeveloper.mpchartexample.databinding.CustomMarkerViewBinding
import java.text.DecimalFormat

@SuppressLint("ViewConstructor")
class XYMarkerView(context: Context, private val xAxisValueFormatter: IAxisValueFormatter) : MarkerView(context, R.layout.custom_marker_view) {

    private val binding = CustomMarkerViewBinding.bind(this)

    private val format = DecimalFormat("###.0")

    override fun refreshContent(e: Entry<*>, highlight: Highlight) {
        binding.tvContent.text = String.format("x: %s, y: %s", xAxisValueFormatter.getFormattedValue(e.x, (chartView as BarLineChartBase<*>).xAxis), format.format(e.y.toDouble()))
        super.refreshContent(e, highlight)
    }

    override var offset: MPPointF
        get() = MPPointF(-(width / 2f), -height.toFloat())
        set(value) {
            super.offset = value
        }
}
