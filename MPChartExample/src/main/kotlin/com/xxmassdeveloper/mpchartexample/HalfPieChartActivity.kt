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
import android.view.Menu
import android.view.MenuItem
import android.widget.RelativeLayout
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.databinding.ActivityPiechartHalfBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class HalfPieChartActivity : DemoBase() {

    private lateinit var binding: ActivityPiechartHalfBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPiechartHalfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "HalfPieChartActivity"

        binding.chart1.setBackgroundColor(Color.WHITE)

        moveOffScreen()

        binding.chart1.apply {
            isUsePercentValuesEnabled = true
            description.isEnabled = false

            centerTextTypeface = tfLight
            centerText = generateCenterSpannableText()

            isDrawHoleEnabled = true
            holeColor = Color.WHITE

            transparentCircleColor = Color.WHITE
            transparentCircleAlpha = 110

            holeRadius = 58f
            transparentCircleRadius = 61f

            isDrawCenterTextEnabled = true

            isRotationEnabled = false
            isHighlightPerTapEnabled = true

            maxAngle = 180f
            rotationAngle = 180f
            setCenterTextOffset(0f, -20f)
        }

        setData(4, 100f)

        binding.chart1.animateY(1400, Easing.EaseInOutQuad)

        binding.chart1.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL
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
        val values = ArrayList<PieEntry<Any?>>()

        for (i in 0 until count) {
            values.add(PieEntry((Math.random() * range).toFloat() + range / 5, parties[i % parties.size]))
        }

        val dataSet = PieDataSet(values, "Election Results")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        dataSet.colors = ColorTemplate.MATERIAL_COLORS

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        data.setValueTypeface(tfLight)
        binding.chart1.data = data

        binding.chart1.invalidate()
    }

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

    private fun moveOffScreen() {
        val height = resources.displayMetrics.heightPixels
        val offset = (height * 0.65).toInt()

        val params = binding.chart1.layoutParams as RelativeLayout.LayoutParams
        params.setMargins(0, 0, 0, -offset)
        binding.chart1.layoutParams = params
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.only_github, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/HalfPieChartActivity.java")
                startActivity(i)
            }
        }
        return true
    }

    override fun saveToGallery() {}
}
