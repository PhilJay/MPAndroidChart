package com.xxmassdeveloper.mpchartexample

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.xxmassdeveloper.mpchartexample.databinding.ActivityLinePerformanceBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import java.util.Locale
import java.util.Random
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.sin

/**
 * Line chart you can pan and zoom by hand while a readout shows what each draw of the chart costs.
 */
class LinePerformanceActivity : DemoBase() {

    private lateinit var binding: ActivityLinePerformanceBinding

    private val dataBuilder = Executors.newSingleThreadExecutor()
    private val onMainThread = Handler(Looper.getMainLooper())

    private var build = 0
    private var buildingCount: Int? = null
    private var lineSet: LineDataSet<Any?>? = null

    private val readoutTick = object : Runnable {
        override fun run() {
            showReadout()
            onMainThread.postDelayed(this, READOUT_INTERVAL_MS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinePerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Line chart performance"

        binding.chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            isDrawGridBackgroundEnabled = false
            isPinchZoomEnabled = true
            isHighlightPerTapEnabled = false
            isHighlightPerDragEnabled = false
            maxVisibleCount = binding.labelLimit.progress
            axisRight.isEnabled = false
            axisLeft.typeface = tfLight
            xAxis.typeface = tfLight
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.isDrawGridLinesEnabled = false
        }

        binding.entryCount.setOnCheckedChangeListener { _, _ -> rebuild() }

        binding.decimation.setOnCheckedChangeListener { _, on ->
            binding.chart.isDecimationEnabled = on
            binding.chart.invalidate()
        }

        binding.valueLabels.setOnCheckedChangeListener { _, on ->
            lineSet?.isDrawValuesEnabled = on
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

        binding.lineShape.setOnCheckedChangeListener { _, _ ->
            lineSet?.mode = selectedMode()
            binding.chart.invalidate()
        }

        binding.circles.setOnCheckedChangeListener { _, on ->
            lineSet?.isDrawCirclesEnabled = on
            binding.chart.invalidate()
        }

        binding.fill.setOnCheckedChangeListener { _, on ->
            lineSet?.isDrawFilledEnabled = on
            binding.chart.invalidate()
        }

        binding.multiColor.setOnCheckedChangeListener { _, on ->
            lineSet?.colors = if (on) SEGMENT_COLORS else listOf(SINGLE_COLOR)
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
        build++
        val generation = build

        buildingCount = count
        lineSet = null
        binding.chart.noDataText = "Building ${grouped(count)} entries..."
        binding.chart.data = null
        binding.chart.frameTimes.clear()
        showReadout()

        dataBuilder.execute {
            val built = buildSet(count)
            onMainThread.post {
                if (generation != build || isFinishing || isDestroyed) return@post
                show(built)
            }
        }
    }

    private fun buildSet(count: Int): LineDataSet<Any?> {
        val random = Random(count.toLong())
        val values = ArrayList<Entry<Any?>>(count)
        for (i in 0 until count) {
            values.add(Entry(i.toFloat(), sin(i * 0.002f) * 40f + random.nextFloat() * 20f))
        }
        return LineDataSet(values, "${grouped(count)} entries")
    }

    private fun show(built: LineDataSet<Any?>) {
        built.apply {
            lineWidth = 1f
            circleRadius = 2f
            isDrawCircleHoleEnabled = false
            circleColors = listOf(SINGLE_COLOR)
            fillColor = SINGLE_COLOR
            valueTextSize = 8f
            mode = selectedMode()
            isDrawCirclesEnabled = binding.circles.isChecked
            isDrawValuesEnabled = binding.valueLabels.isChecked
            isDrawFilledEnabled = binding.fill.isChecked
            colors = if (binding.multiColor.isChecked) SEGMENT_COLORS else listOf(SINGLE_COLOR)
        }

        lineSet = built
        buildingCount = null
        binding.chart.data = LineData(built)
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
        val total = lineSet?.entryCount ?: 0

        binding.readout.text = listOf(
            if (recent > 0f) String.format(Locale.US, "%.1f ms per frame, %.0f fps", recent, 1000f / recent) else getString(R.string.perfWaiting),
            if (worst > 0f) String.format(Locale.US, "worst frame in the last second %.1f ms", worst) else "no frame in the last second",
            "${grouped(visibleEntries())} of ${grouped(total)} entries on screen"
        ).joinToString("\n")
    }

    private fun visibleEntries(): Int {
        val set = lineSet ?: return 0
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

    private fun selectedMode(): LineDataSet.Mode = when (binding.lineShape.checkedRadioButtonId) {
        R.id.shapeStepped -> LineDataSet.Mode.STEPPED
        R.id.shapeCubic -> LineDataSet.Mode.CUBIC_BEZIER
        else -> LineDataSet.Mode.LINEAR
    }

    private fun grouped(value: Int): String = String.format(Locale.US, "%,d", value)

    override fun saveToGallery() = saveToGallery(binding.chart, "LinePerformanceActivity")

    private companion object {
        const val READOUT_INTERVAL_MS = 200L
        val SINGLE_COLOR = Color.rgb(28, 88, 154)
        val SEGMENT_COLORS = listOf(
            Color.rgb(28, 88, 154),
            Color.rgb(217, 80, 48),
            Color.rgb(106, 167, 79),
            Color.rgb(233, 168, 41),
            Color.rgb(123, 82, 171)
        )
    }
}
