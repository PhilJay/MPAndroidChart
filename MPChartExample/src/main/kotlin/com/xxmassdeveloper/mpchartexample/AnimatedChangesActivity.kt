package com.xxmassdeveloper.mpchartexample

import android.os.Bundle
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.databinding.ActivityAnimatedChangesBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import kotlin.random.Random

/** Animates new data in with animateDataChange and a single tapped bar with animateValue. */
class AnimatedChangesActivity : DemoBase() {

    private lateinit var binding: ActivityAnimatedChangesBinding

    private val values = MutableList(8) { randomValue() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimatedChangesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "AnimatedChangesActivity"

        binding.chart1.apply {
            description.isEnabled = false
            legend.isEnabled = false
            isDrawGridBackgroundEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.isDrawGridLinesEnabled = false
            xAxis.granularity = 1f
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            data = barData()
            onValueSelected { entry, _ ->
                val index = entry.x.toInt()
                values[index] = randomValue()
                animateValue(entry, values[index], 600, Easing.EaseInOutCubic)
            }
        }

        binding.randomize.setOnClickListener {
            for (i in values.indices) values[i] = randomValue()
            animate()
        }
        binding.add.setOnClickListener {
            values += randomValue()
            animate()
        }
        binding.remove.setOnClickListener {
            if (values.size > 1) values.removeAt(values.lastIndex)
            animate()
        }
    }

    private fun animate() {
        binding.chart1.animateDataChange(barData(), 600, Easing.EaseInOutCubic)
    }

    private fun barData(): BarData {
        val set = BarDataSet(values.mapIndexed { i, value -> BarEntry(i.toFloat(), value) }, "Values").apply {
            colors = ColorTemplate.MATERIAL_COLORS
            valueTextSize = 10f
        }
        return BarData(set).apply { barWidth = 0.7f }
    }

    private fun randomValue(): Float = Random.nextInt(10, 100).toFloat()

    override fun saveToGallery() = saveToGallery(binding.chart1, "AnimatedChangesActivity")
}
