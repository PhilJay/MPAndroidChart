package com.xxmassdeveloper.mpchartexample

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.DataSet
import com.xxmassdeveloper.mpchartexample.databinding.ActivityBarPerformanceBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import java.util.Locale
import java.util.Random
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.sin

/**
 * Bar chart you can pan and zoom by hand while a readout shows what each draw of the chart costs.
 */
class BarPerformanceActivity : DemoBase() {

    private lateinit var binding: ActivityBarPerformanceBinding

    private val dataBuilder = Executors.newSingleThreadExecutor()
    private val onMainThread = Handler(Looper.getMainLooper())

    private var build = 0
    private var buildingCount: Int? = null
    private var barSet: BarDataSet<Any?>? = null

    private val readoutTick = object : Runnable {
        override fun run() {
            showReadout()
            onMainThread.postDelayed(this, READOUT_INTERVAL_MS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Bar chart performance"

        binding.chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            isDrawGridBackgroundEnabled = false
            isPinchZoomEnabled = true
            isHighlightPerTapEnabled = false
            isHighlightPerDragEnabled = false
            isDrawValueAboveBarEnabled = true
            maxVisibleCount = binding.labelLimit.progress
            axisRight.isEnabled = false
            axisLeft.typeface = tfLight
            axisLeft.axisMinimum = 0f
            xAxis.typeface = tfLight
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.isDrawGridLinesEnabled = false
        }

        binding.entryCount.setOnCheckedChangeListener { _, _ -> rebuild() }

        binding.stacked.setOnCheckedChangeListener { _, _ -> rebuild() }

        binding.decimation.setOnCheckedChangeListener { _, on ->
            binding.chart.isDecimationEnabled = on
            binding.chart.invalidate()
        }

        binding.valueLabels.setOnCheckedChangeListener { _, on ->
            barSet?.isDrawValuesEnabled = on
            binding.chart.invalidate()
        }

        binding.labelLimit.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.labelLimitValue.text = grouped(progress)
                binding.chart.maxVisibleCount = progress
                binding.chart.invalidate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        binding.labelLimitValue.text = grouped(binding.labelLimit.progress)

        binding.roundedCorners.setOnCheckedChangeListener { _, on ->
            barSet?.barCornerRadius = if (on) CORNER_RADIUS_DP else 0f
            binding.chart.invalidate()
        }

        binding.barShadows.setOnCheckedChangeListener { _, on ->
            binding.chart.isDrawBarShadowEnabled = on
            binding.chart.invalidate()
        }

        rebuild()
    }

    override fun onResume() {
        super.onResume()
        onMainThread.post(readoutTick)
    }

    override fun onPause() {
        super.onPause()
        onMainThread.removeCallbacks(readoutTick)
    }

    override fun onDestroy() {
        super.onDestroy()
        onMainThread.removeCallbacksAndMessages(null)
        dataBuilder.shutdownNow()
    }

    private fun rebuild() {
        val count = selectedCount()
        val stacked = binding.stacked.isChecked
        build++
        val generation = build

        buildingCount = count
        barSet = null
        binding.chart.noDataText = "Building ${grouped(count)} entries..."
        binding.chart.data = null
        binding.chart.frameTimes.clear()
        showReadout()

        dataBuilder.execute {
            val built = buildSet(count, stacked)
            onMainThread.post {
                if (generation != build || isFinishing || isDestroyed) return@post
                show(built, stacked)
            }
        }
    }

    private fun buildSet(count: Int, stacked: Boolean): BarDataSet<Any?> {
        val random = Random(count.toLong())
        val values = ArrayList<BarEntry<Any?>>(count)
        for (i in 0 until count) {
            val height = 30f + sin(i * 0.01f) * 20f + random.nextFloat() * 15f
            if (stacked) {
                values.add(BarEntry(i.toFloat(), listOf(height * 0.5f, height * 0.3f, height * 0.2f)))
            } else {
                values.add(BarEntry(i.toFloat(), height))
            }
        }
        return BarDataSet(values, "${grouped(count)} entries")
    }

    private fun show(built: BarDataSet<Any?>, stacked: Boolean) {
        built.apply {
            isDrawIconsEnabled = false
            valueTextSize = 8f
            barShadowColor = Color.rgb(226, 226, 226)
            barCornerRadius = if (binding.roundedCorners.isChecked) CORNER_RADIUS_DP else 0f
            isDrawValuesEnabled = binding.valueLabels.isChecked
            colors = if (stacked) STACK_COLORS else listOf(STACK_COLORS.first())
        }

        barSet = built
        buildingCount = null
        binding.chart.data = BarData(built).apply { barWidth = 0.9f }
        binding.chart.fitScreen()
        binding.chart.frameTimes.clear()
    }

    private fun showReadout() {
        val building = buildingCount
        if (building != null) {
            binding.readout.text = "Building ${grouped(building)} entries..."
            return
        }

        val frameTimes = binding.chart.frameTimes
        val recent = frameTimes.recentMillis
        val worst = frameTimes.worstMillisLastSecond
        val total = barSet?.entryCount ?: 0

        binding.readout.text = listOf(
            if (recent > 0f) String.format(Locale.US, "%.1f ms per frame, %.0f fps", recent, 1000f / recent) else getString(R.string.perfWaiting),
            if (worst > 0f) String.format(Locale.US, "worst frame in the last second %.1f ms", worst) else "no frame in the last second",
            "${grouped(visibleEntries())} of ${grouped(total)} entries on screen"
        ).joinToString("\n")
    }

    private fun visibleEntries(): Int {
        val set = barSet ?: return 0
        if (set.entryCount == 0) return 0

        val from = set.getEntryIndex(binding.chart.lowestVisibleX, Float.NaN, DataSet.Rounding.DOWN).coerceAtLeast(0)
        val to = set.getEntryIndex(binding.chart.highestVisibleX, Float.NaN, DataSet.Rounding.UP).coerceAtLeast(0)
        return max(0, to - from) + 1
    }

    private fun selectedCount(): Int = when (binding.entryCount.checkedRadioButtonId) {
        R.id.count1k -> 1_000
        R.id.count50k -> 50_000
        R.id.count100k -> 100_000
        R.id.count500k -> 500_000
        else -> 10_000
    }

    private fun grouped(value: Int): String = String.format(Locale.US, "%,d", value)

    override fun saveToGallery() = saveToGallery(binding.chart, "BarPerformanceActivity")

    private companion object {
        const val READOUT_INTERVAL_MS = 200L
        const val CORNER_RADIUS_DP = 4f
        val STACK_COLORS = listOf(
            Color.rgb(28, 88, 154),
            Color.rgb(106, 167, 79),
            Color.rgb(233, 168, 41)
        )
    }
}
