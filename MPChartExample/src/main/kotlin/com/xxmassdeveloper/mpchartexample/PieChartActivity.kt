package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.xxmassdeveloper.mpchartexample.databinding.ActivityPiechartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class PieChartActivity : DemoBase(), SeekBar.OnSeekBarChangeListener {

    private lateinit var binding: ActivityPiechartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPiechartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "PieChartActivity"

        binding.seekBar1.setOnSeekBarChangeListener(this)
        binding.seekBar2.setOnSeekBarChangeListener(this)

        binding.chart1.apply {
            isUsePercentValuesEnabled = true
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)

            dragDecelerationFrictionCoef = 0.95f

            centerTextTypeface = tfLight
            centerText = generateCenterSpannableText()

            isDrawHoleEnabled = true
            holeColor = Color.WHITE

            transparentCircleColor = Color.WHITE
            transparentCircleAlpha = 110

            holeRadius = 58f
            transparentCircleRadius = 61f

            isDrawCenterTextEnabled = true

            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true

            onValueSelected(onNothingSelected = { Log.i("PieChart", "nothing selected") }) { entry, highlight ->
                Log.i("VAL SELECTED", "Value: ${entry.y}, index: ${highlight.x}, DataSet index: ${highlight.dataSetIndex}")
            }
        }

        binding.seekBar1.progress = 4
        binding.seekBar2.progress = 10

        binding.chart1.animateY(1400, Easing.EaseInOutQuad)

        binding.chart1.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.VERTICAL
            isDrawInsideEnabled = false
            xEntrySpace = 7f
            yEntrySpace = 0f
            yOffset = 0f
        }

        binding.chart1.apply {
            entryLabelColor = Color.WHITE
            entryLabelTypeface = tfRegular
            entryLabelTextSize = 12f
        }
    }

    private fun setData(count: Int, range: Float) {
        val entries = ArrayList<PieEntry<Any?>>()

        for (i in 0 until count) {
            entries.add(
                PieEntry(
                    (Math.random() * range).toFloat() + range / 5,
                    parties[i % parties.size],
                    ContextCompat.getDrawable(this, R.drawable.star)
                )
            )
        }

        val dataSet = PieDataSet(entries, "Election Results")

        dataSet.isDrawIconsEnabled = false

        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colors = ArrayList<Int>()

        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)

        colors.add(ColorTemplate.holoBlue)

        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        data.setValueTypeface(tfLight)
        binding.chart1.data = data

        binding.chart1.highlightValues(emptyList())

        binding.chart1.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.pie, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val chart = binding.chart1
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/PieChartActivity.java")
                startActivity(i)
            }
            R.id.actionToggleValues -> {
                chart.data?.dataSets?.forEach { it.isDrawValuesEnabled = !it.isDrawValuesEnabled }
                chart.invalidate()
            }
            R.id.actionToggleIcons -> {
                chart.data?.dataSets?.forEach { it.isDrawIconsEnabled = !it.isDrawIconsEnabled }
                chart.invalidate()
            }
            R.id.actionToggleHole -> {
                chart.isDrawHoleEnabled = !chart.isDrawHoleEnabled
                chart.invalidate()
            }
            R.id.actionToggleMinAngles -> {
                chart.minAngleForSlices = if (chart.minAngleForSlices == 0f) 36f else 0f
                chart.notifyDataSetChanged()
                chart.invalidate()
            }
            R.id.actionToggleCurvedSlices -> {
                val toSet = !chart.isDrawRoundedSlicesEnabled || !chart.isDrawHoleEnabled
                chart.isDrawRoundedSlicesEnabled = toSet
                if (toSet && !chart.isDrawHoleEnabled) {
                    chart.isDrawHoleEnabled = true
                }
                if (toSet && chart.isDrawSlicesUnderHoleEnabled) {
                    chart.isDrawSlicesUnderHoleEnabled = false
                }
                chart.invalidate()
            }
            R.id.actionDrawCenter -> {
                chart.isDrawCenterTextEnabled = !chart.isDrawCenterTextEnabled
                chart.invalidate()
            }
            R.id.actionToggleXValues -> {
                chart.isDrawEntryLabelsEnabled = !chart.isDrawEntryLabelsEnabled
                chart.invalidate()
            }
            R.id.actionTogglePercent -> {
                chart.isUsePercentValuesEnabled = !chart.isUsePercentValuesEnabled
                chart.invalidate()
            }
            R.id.animateX -> chart.animateX(1400)
            R.id.animateY -> chart.animateY(1400)
            R.id.animateXY -> chart.animateXY(1400, 1400)
            R.id.actionToggleSpin -> chart.spin(1000, chart.rotationAngle, chart.rotationAngle + 360, Easing.EaseInOutCubic)
            R.id.actionSave -> saveChartToGalleryWithPermission(chart)
        }
        return true
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        binding.tvXMax.text = binding.seekBar1.progress.toString()
        binding.tvYMax.text = binding.seekBar2.progress.toString()

        setData(binding.seekBar1.progress, binding.seekBar2.progress.toFloat())
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "PieChartActivity")

    private fun generateCenterSpannableText(): SpannableString {
        val s = SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda")
        s.setSpan(RelativeSizeSpan(1.7f), 0, 14, 0)
        s.setSpan(StyleSpan(Typeface.NORMAL), 14, s.length - 15, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 14, s.length - 15, 0)
        s.setSpan(RelativeSizeSpan(.8f), 14, s.length - 15, 0)
        s.setSpan(StyleSpan(Typeface.ITALIC), s.length - 14, s.length, 0)
        s.setSpan(ForegroundColorSpan(ColorTemplate.holoBlue), s.length - 14, s.length, 0)
        return s
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
