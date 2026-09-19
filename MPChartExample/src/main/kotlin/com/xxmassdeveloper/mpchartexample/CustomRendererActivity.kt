package com.xxmassdeveloper.mpchartexample

import android.graphics.Color
import android.os.Bundle
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.xxmassdeveloper.mpchartexample.custom.BadgeBarRenderer
import com.xxmassdeveloper.mpchartexample.databinding.ActivityCustomRendererBinding
import com.xxmassdeveloper.mpchartexample.design.Nightfall
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import kotlin.math.roundToInt

/**
 * Replaces the bar renderer with one that draws each value on a badge. Everything else about the chart is
 * ordinary.
 */
class CustomRendererActivity : DemoBase() {

    private lateinit var binding: ActivityCustomRendererBinding

    private val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomRendererBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "CustomRendererActivity"

        val values = listOf(42f, 55f, 48f, 71f, 66f, 34f, 29f)
        val entries = values.mapIndexed { index, value -> BarEntry(index.toFloat(), value) }

        val set = BarDataSet(entries, "Sessions").apply {
            color = Nightfall.accent
            barCornerRadius = 8f
            valueTextColor = Color.WHITE
            valueTextSize = 11f
            valueTypeface = tfRegular
            valueFormatter = IValueFormatter { value, _, _, _ -> "${value.roundToInt()}" }
        }

        binding.chart1.apply {
            description.isEnabled = false
            legend.isEnabled = false
            isDrawGridBackgroundEnabled = false
            setExtraOffsets(8f, 24f, 8f, 8f)

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.isDrawGridLinesEnabled = false
            xAxis.typeface = tfRegular
            xAxis.valueFormatter = IAxisValueFormatter { value, _ -> days[value.toInt() % days.size] }
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f

            // The renderer needs the chart, the animator and the viewport handler the chart already owns.
            renderer = BadgeBarRenderer(this, animator, viewPortHandler, Nightfall.dark.card)

            data = BarData(set).apply { barWidth = 0.55f }
            animateY(900)
        }
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "CustomRendererActivity")
}
