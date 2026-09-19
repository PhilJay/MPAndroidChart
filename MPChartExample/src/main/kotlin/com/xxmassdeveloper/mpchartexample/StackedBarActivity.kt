package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.custom.MyAxisValueFormatter
import com.xxmassdeveloper.mpchartexample.custom.MyValueFormatter
import com.xxmassdeveloper.mpchartexample.databinding.ActivityBarchartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class StackedBarActivity : DemoBase(), OnSeekBarChangeListener, OnChartValueSelectedListener {

    private lateinit var binding: ActivityBarchartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarchartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "StackedBarActivity"

        binding.seekBar1.setOnSeekBarChangeListener(this)
        binding.seekBar2.setOnSeekBarChangeListener(this)

        binding.chart1.apply {
            onChartValueSelectedListener = this@StackedBarActivity
            description.isEnabled = false
            maxVisibleCount = 40
            isPinchZoomEnabled = false
            isDrawGridBackgroundEnabled = false
            isDrawBarShadowEnabled = false
            isDrawValueAboveBarEnabled = false
            isHighlightFullBarEnabled = false

            axisLeft.valueFormatter = MyAxisValueFormatter()
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false

            xAxis.position = XAxis.XAxisPosition.TOP
        }

        binding.seekBar1.progress = 12
        binding.seekBar2.progress = 100

        binding.chart1.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.HORIZONTAL
            isDrawInsideEnabled = false
            formSize = 8f
            formToTextSpace = 4f
            xEntrySpace = 6f
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        binding.tvXMax.text = binding.seekBar1.progress.toString()
        binding.tvYMax.text = binding.seekBar2.progress.toString()

        val values = ArrayList<BarEntry<Any?>>()
        for (i in 0 until binding.seekBar1.progress) {
            val mul = (binding.seekBar2.progress + 1).toFloat()
            val val1 = (Math.random() * mul).toFloat() + mul / 3
            val val2 = (Math.random() * mul).toFloat() + mul / 3
            val val3 = (Math.random() * mul).toFloat() + mul / 3

            values.add(BarEntry(i.toFloat(), listOf(val1, val2, val3), ContextCompat.getDrawable(this, R.drawable.star)))
        }

        val chartData = binding.chart1.data
        if (chartData != null && chartData.dataSetCount > 0) {
            val set1 = chartData.getDataSetByIndex(0) as BarDataSet<Any?>
            set1.entries = values
            chartData.notifyDataChanged()
            binding.chart1.notifyDataSetChanged()
        } else {
            val set1 = BarDataSet(values, "Statistics Vienna 2014")
            set1.isDrawIconsEnabled = false
            set1.colors = getColors()
            set1.stackLabels = listOf("Births", "Divorces", "Marriages")

            val dataSets = ArrayList<IBarDataSet<*>>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setValueFormatter(MyValueFormatter())
            data.setValueTextColor(Color.WHITE)

            binding.chart1.data = data
        }

        binding.chart1.isFitBarsEnabled = true
        binding.chart1.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val chart = binding.chart1
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/StackedBarActivity.java")
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
            R.id.actionToggleHighlight -> {
                chart.data?.let {
                    it.isHighlightEnabled = !it.isHighlightEnabled
                    chart.invalidate()
                }
            }
            R.id.actionTogglePinch -> {
                chart.isPinchZoomEnabled = !chart.isPinchZoomEnabled
                chart.invalidate()
            }
            R.id.actionToggleAutoScaleMinMax -> {
                chart.isAutoScaleMinMaxEnabled = !chart.isAutoScaleMinMaxEnabled
                chart.notifyDataSetChanged()
            }
            R.id.actionToggleRoundedSections -> {
                chart.data?.dataSets?.forEach {
                    val set = it as BarDataSet<Any?>
                    set.isStackSectionsRounded = !set.isStackSectionsRounded
                    set.barCornerRadius = if (set.isStackSectionsRounded) 6f else 0f
                }
                chart.invalidate()
            }
            R.id.actionToggleBarBorders -> {
                chart.data?.dataSets?.forEach { (it as BarDataSet<Any?>).barBorderWidth = if (it.barBorderWidth == 1f) 0f else 1f }
                chart.invalidate()
            }
            R.id.animateX -> chart.animateX(2000)
            R.id.animateY -> chart.animateY(2000)
            R.id.animateXY -> chart.animateXY(2000, 2000)
            R.id.actionSave -> saveChartToGalleryWithPermission(chart)
        }
        return true
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "StackedBarActivity")

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onValueSelected(e: Entry<*>, h: Highlight) {
        val entry = e as BarEntry<*>
        val stackValues = entry.stackValues
        if (stackValues != null) {
            Log.i("VAL SELECTED", "Value: ${stackValues[h.stackIndex]}")
        } else {
            Log.i("VAL SELECTED", "Value: ${entry.y}")
        }
    }

    override fun onNothingSelected() {}

    private fun getColors(): List<Int> = ColorTemplate.MATERIAL_COLORS.take(3)
}
