package com.xxmassdeveloper.mpchartexample

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.animation.Easing.EasingFunction
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.xxmassdeveloper.mpchartexample.databinding.ActivityAnimationPlaygroundBinding
import com.xxmassdeveloper.mpchartexample.design.Nightfall
import com.xxmassdeveloper.mpchartexample.design.nightfallBars
import com.xxmassdeveloper.mpchartexample.design.nightfallBase
import com.xxmassdeveloper.mpchartexample.design.nightfallDonut
import com.xxmassdeveloper.mpchartexample.design.nightfallFullWidthX
import com.xxmassdeveloper.mpchartexample.design.nightfallLine
import com.xxmassdeveloper.mpchartexample.design.nightfallPaddedX
import com.xxmassdeveloper.mpchartexample.design.nightfallSlices
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import kotlin.random.Random

/** Every animation the library offers, on a line, bar or donut chart, with any easing curve and duration. */
class AnimationPlaygroundActivity : DemoBase() {

    private lateinit var binding: ActivityAnimationPlaygroundBinding
    private val theme = Nightfall.dark

    private val easings = linkedMapOf(
        "Linear" to Easing.Linear,
        "EaseInQuad" to Easing.EaseInQuad, "EaseOutQuad" to Easing.EaseOutQuad, "EaseInOutQuad" to Easing.EaseInOutQuad,
        "EaseInCubic" to Easing.EaseInCubic, "EaseOutCubic" to Easing.EaseOutCubic, "EaseInOutCubic" to Easing.EaseInOutCubic,
        "EaseInQuart" to Easing.EaseInQuart, "EaseOutQuart" to Easing.EaseOutQuart, "EaseInOutQuart" to Easing.EaseInOutQuart,
        "EaseInSine" to Easing.EaseInSine, "EaseOutSine" to Easing.EaseOutSine, "EaseInOutSine" to Easing.EaseInOutSine,
        "EaseInExpo" to Easing.EaseInExpo, "EaseOutExpo" to Easing.EaseOutExpo, "EaseInOutExpo" to Easing.EaseInOutExpo,
        "EaseInCirc" to Easing.EaseInCirc, "EaseOutCirc" to Easing.EaseOutCirc, "EaseInOutCirc" to Easing.EaseInOutCirc,
        "EaseInElastic" to Easing.EaseInElastic, "EaseOutElastic" to Easing.EaseOutElastic, "EaseInOutElastic" to Easing.EaseInOutElastic,
        "EaseInBack" to Easing.EaseInBack, "EaseOutBack" to Easing.EaseOutBack, "EaseInOutBack" to Easing.EaseInOutBack,
        "EaseInBounce" to Easing.EaseInBounce, "EaseOutBounce" to Easing.EaseOutBounce, "EaseInOutBounce" to Easing.EaseInOutBounce,
    )

    private enum class Kind(val label: String, val subtitle: String) {
        LINE("Line", "animateX draws it, animateY grows it"),
        BAR("Bars", "tap New data to grow, shrink and move bars"),
        DONUT("Donut", "animateX sweeps the slices, Spin turns it"),
    }

    private var kind = Kind.LINE
    private var easingName = "EaseInOutCubic"
    private val easing: EasingFunction get() = easings.getValue(easingName)
    private var durationMillis = 1500
    private var ended = 0
    private var lastEnded = ""

    private val tabs = mutableMapOf<Kind, TextView>()
    private val easingChips = mutableMapOf<String, TextView>()
    private lateinit var spinButton: TextView

    private val chart: Chart<*>
        get() = when (kind) {
            Kind.LINE -> binding.lineChart
            Kind.BAR -> binding.barChart
            Kind.DONUT -> binding.pieChart
        }

    private var shownRunning: Boolean? = null

    private val showStatus = object : Runnable {
        override fun run() {
            val running = chart.isAnimating
            if (running != shownRunning) {
                shownRunning = running
                binding.status.text = if (running) "●  Running" else "●  Idle"
                val color = if (running) Nightfall.green else theme.muted
                binding.status.setTextColor(color)
                binding.status.background = rounded(Nightfall.withAlpha(color, 36), 12f)
            }
            binding.status.postDelayed(this, 80)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimationPlaygroundBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "AnimationPlaygroundActivity"

        styleSurfaces()
        setUpCharts()
        setUpTabs()
        setUpEasing()
        setUpDuration()
        setUpActions()
        select(Kind.LINE)
        showEnded()
    }

    override fun onStart() {
        super.onStart()
        binding.status.post(showStatus)
    }

    override fun onStop() {
        binding.status.removeCallbacks(showStatus)
        super.onStop()
    }

    private fun styleSurfaces() {
        binding.stage.setBackgroundColor(theme.stage)
        binding.chartCard.background = card()
        binding.controlsCard.background = card()
        binding.title.setTextColor(theme.text)
        binding.subtitle.setTextColor(theme.muted)
        binding.easingLabel.setTextColor(theme.muted)
        binding.durationLabel.setTextColor(theme.muted)
        binding.easingName.setTextColor(theme.text)
        binding.durationText.setTextColor(theme.text)
        binding.ended.setTextColor(theme.muted)
        binding.tabs.background = rounded(theme.track, 14f)
        binding.easingCurve.curveColor = Nightfall.accent
        binding.easingCurve.guideColor = theme.grid
    }

    private fun setUpCharts() {
        binding.lineChart.apply {
            nightfallBase(theme)
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 110f
            data = lineData(12)
        }
        binding.barChart.apply {
            nightfallBase(theme)
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 110f
            data = barData(8)
        }
        binding.pieChart.apply {
            nightfallDonut(theme)
            holeRadius = 62f
            data = pieData(5)
        }
    }

    private fun setUpTabs() {
        for (k in Kind.entries) {
            val tab = TextView(this).apply {
                text = k.label
                gravity = Gravity.CENTER
                textSize = 13f
                setPadding(0, dp(8), 0, dp(8))
                setOnClickListener { select(k) }
            }
            binding.tabs.addView(tab, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            tabs[k] = tab
        }
    }

    private fun select(next: Kind) {
        chart.stopAnimations()
        kind = next
        binding.lineChart.visibility = if (kind == Kind.LINE) View.VISIBLE else View.GONE
        binding.barChart.visibility = if (kind == Kind.BAR) View.VISIBLE else View.GONE
        binding.pieChart.visibility = if (kind == Kind.DONUT) View.VISIBLE else View.GONE
        binding.subtitle.text = kind.subtitle
        for ((k, tab) in tabs) {
            val selected = k == kind
            tab.setTextColor(if (selected) theme.stage else theme.muted)
            tab.paint.isFakeBoldText = selected
            tab.background = if (selected) rounded(Nightfall.accent, 11f) else null
        }
        spinButton.isEnabled = kind == Kind.DONUT
        spinButton.alpha = if (spinButton.isEnabled) 1f else 0.35f
    }

    private fun setUpEasing() {
        for (name in easings.keys) {
            val chip = TextView(this).apply {
                text = name.removePrefix("Ease")
                textSize = 12f
                setPadding(dp(12), dp(7), dp(12), dp(7))
                setOnClickListener { selectEasing(name) }
            }
            binding.easingChips.addView(chip, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                marginStart = dp(4)
                marginEnd = dp(4)
            })
            easingChips[name] = chip
        }
        selectEasing(easingName)
    }

    private fun selectEasing(name: String) {
        easingName = name
        binding.easingName.text = name
        binding.easingCurve.easing = easing
        for ((chipName, chip) in easingChips) {
            val selected = chipName == name
            chip.setTextColor(if (selected) Nightfall.accent else theme.muted)
            chip.background = rounded(if (selected) Nightfall.withAlpha(Nightfall.accent, 40) else theme.track, 14f)
        }
    }

    private fun setUpDuration() {
        binding.duration.progressTintList = ColorStateList.valueOf(Nightfall.accent)
        binding.duration.thumbTintList = ColorStateList.valueOf(Nightfall.accent)
        binding.duration.progressBackgroundTintList = ColorStateList.valueOf(theme.muted)
        binding.duration.progress = durationMillis
        binding.durationText.text = "$durationMillis ms"
        binding.duration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                durationMillis = progress.coerceAtLeast(100)
                binding.durationText.text = "$durationMillis ms"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun setUpActions() {
        action(binding.actionsTop, "Draw X", Nightfall.accent) { chart.animateX(durationMillis, easing) { ended("animateX") } }
        action(binding.actionsTop, "Grow Y", Nightfall.accent) { chart.animateY(durationMillis, easing) { ended("animateY") } }
        action(binding.actionsTop, "Both", Nightfall.accent) {
            chart.animateXY(durationMillis, durationMillis, easing) { ended("animateXY") }
        }
        spinButton = action(binding.actionsTop, "Spin", Nightfall.violet) {
            val pie = binding.pieChart
            pie.spin(durationMillis, pie.rotationAngle, pie.rotationAngle + 360f, easing) { ended("spin") }
        }
        action(binding.actionsBottom, "New data", Nightfall.amber, weight = 1.3f) { animateNewData() }
        action(binding.actionsBottom, "One value", Nightfall.green, weight = 1.3f) { animateOneValue() }
        action(binding.actionsBottom, "Stop", Nightfall.coral) { chart.stopAnimations() }
    }

    private fun action(row: LinearLayout, label: String, color: Int, weight: Float = 1f, onClick: () -> Unit): TextView {
        val button = TextView(this).apply {
            text = label
            gravity = Gravity.CENTER
            textSize = 13f
            paint.isFakeBoldText = true
            setTextColor(color)
            background = rounded(Nightfall.withAlpha(color, 38), 14f)
            setPadding(0, dp(12), 0, dp(12))
            setOnClickListener { onClick() }
        }
        row.addView(button, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight).apply {
            marginStart = dp(4)
            marginEnd = dp(4)
        })
        return button
    }

    /** New data with a different number of entries, so values move, new entries grow in and removed ones go. */
    private fun animateNewData() {
        when (kind) {
            Kind.LINE -> binding.lineChart.animateDataChange(lineData(Random.nextInt(6, 18)), durationMillis, easing) { ended("animateDataChange") }
            Kind.BAR -> binding.barChart.animateDataChange(barData(Random.nextInt(4, 12)), durationMillis, easing) { ended("animateDataChange") }
            Kind.DONUT -> binding.pieChart.animateDataChange(pieData(Random.nextInt(3, 6)), durationMillis, easing) { ended("animateDataChange") }
        }
    }

    private fun animateOneValue() {
        val set = chart.data?.getDataSetByIndex(0) ?: return
        if (set.entryCount == 0) return
        val entry = set.getEntryForIndex(Random.nextInt(set.entryCount))
        chart.animateValue(entry, randomValue(), durationMillis, easing) { ended("animateValue") }
    }

    private fun ended(name: String) {
        ended++
        lastEnded = name
        showEnded()
    }

    private fun showEnded() {
        binding.ended.text = when (ended) {
            0 -> "onEnd has not run yet"
            1 -> "onEnd ran once, for $lastEnded"
            else -> "onEnd ran $ended times, last for $lastEnded"
        }
    }

    private fun lineData(count: Int): LineData {
        val set = LineDataSet(List(count) { Entry(it.toFloat(), randomValue()) }, "Values").apply {
            nightfallLine(theme)
            isDrawCirclesEnabled = true
            circleRadius = 3.5f
            circleHoleRadius = 1.8f
            circleColor = Nightfall.accent
        }
        binding.lineChart.nightfallFullWidthX(count)
        return LineData(set)
    }

    private fun barData(count: Int): BarData {
        val set = BarDataSet(List(count) { BarEntry(it.toFloat(), randomValue()) }, "Values").apply {
            nightfallBars()
        }
        binding.barChart.nightfallPaddedX(count)
        return BarData(set).apply { barWidth = 0.6f }
    }

    private fun pieData(count: Int): PieData {
        val set = PieDataSet(List(count) { PieEntry(randomValue(), "Part ${it + 1}") }, "").apply {
            nightfallSlices()
        }
        return PieData(set)
    }

    private fun randomValue(): Float = Random.nextInt(15, 100).toFloat()

    private fun card() = GradientDrawable().apply {
        cornerRadius = dp(18).toFloat()
        setColor(theme.card)
        setStroke(1, theme.grid)
    }

    private fun rounded(color: Int, radiusDp: Float) = GradientDrawable().apply {
        cornerRadius = radiusDp * resources.displayMetrics.density
        setColor(color)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun saveToGallery() = saveToGallery(chart, "AnimationPlaygroundActivity")
}
