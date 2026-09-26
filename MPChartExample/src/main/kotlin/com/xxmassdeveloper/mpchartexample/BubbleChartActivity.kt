package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.xxmassdeveloper.mpchartexample.databinding.ActivityBubblechartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class BubbleChartActivity : DemoBase(), SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener {

    private lateinit var binding: ActivityBubblechartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBubblechartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "BubbleChartActivity"

        binding.seekBar1.setOnSeekBarChangeListener(this)
        binding.seekBar2.setOnSeekBarChangeListener(this)

        binding.chart1.apply {
            description.isEnabled = false

            onChartValueSelectedListener = this@BubbleChartActivity

            isDrawGridBackgroundEnabled = false

            isTouchEnabled = true

            isDragEnabled = true
            isScaleEnabled = true

            maxVisibleCount = 200
            isPinchZoomEnabled = true
        }

        binding.seekBar1.progress = 10
        binding.seekBar2.progress = 50

        binding.chart1.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.VERTICAL
            isDrawInsideEnabled = false
            typeface = tfLight
        }

        binding.chart1.axisLeft.apply {
            typeface = tfLight
            spaceTop = 30f
            spaceBottom = 30f
            isDrawZeroLineEnabled = false
        }

        binding.chart1.axisRight.isEnabled = false

        binding.chart1.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            typeface = tfLight
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val count = binding.seekBar1.progress
        val range = binding.seekBar2.progress

        binding.tvXMax.text = count.toString()
        binding.tvYMax.text = range.toString()

        val values1 = ArrayList<BubbleEntry<Any?>>()
        val values2 = ArrayList<BubbleEntry<Any?>>()
        val values3 = ArrayList<BubbleEntry<Any?>>()

        for (i in 0 until count) {
            values1.add(BubbleEntry(i.toFloat(), (Math.random() * range).toFloat(), (Math.random() * range).toFloat(), ContextCompat.getDrawable(this, R.drawable.star)))
            values2.add(BubbleEntry(i.toFloat(), (Math.random() * range).toFloat(), (Math.random() * range).toFloat(), ContextCompat.getDrawable(this, R.drawable.star)))
            values3.add(BubbleEntry(i.toFloat(), (Math.random() * range).toFloat(), (Math.random() * range).toFloat()))
        }

        val set1 = BubbleDataSet(values1, "DS 1")
        set1.isDrawIconsEnabled = false
        set1.setColor(ColorTemplate.COLORFUL_COLORS[0], 130)
        set1.isDrawValuesEnabled = true

        val set2 = BubbleDataSet(values2, "DS 2")
        set2.isDrawIconsEnabled = false
        set2.iconsOffset = MPPointF(0f, 15f)
        set2.setColor(ColorTemplate.COLORFUL_COLORS[1], 130)
        set2.isDrawValuesEnabled = true

        val set3 = BubbleDataSet(values3, "DS 3")
        set3.setColor(ColorTemplate.COLORFUL_COLORS[2], 130)
        set3.isDrawValuesEnabled = true

        val dataSets = ArrayList<IBubbleDataSet<*>>()
        dataSets.add(set1)
        dataSets.add(set2)
        dataSets.add(set3)

        val data = BubbleData(dataSets)
        data.setDrawValues(false)
        data.setValueTypeface(tfLight)
        data.setValueTextSize(8f)
        data.setValueTextColor(Color.WHITE)
        data.setHighlightCircleWidth(1.5f)

        binding.chart1.data = data
        binding.chart1.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bubble, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val chart = binding.chart1
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/BubbleChartActivity.java")
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
            R.id.actionSave -> saveChartToGalleryWithPermission(chart)
            R.id.animateX -> chart.animateX(2000)
            R.id.animateY -> chart.animateY(2000)
            R.id.animateXY -> chart.animateXY(2000, 2000)
        }
        return true
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "BubbleChartActivity")

    override fun onValueSelected(e: Entry<*>, h: Highlight) {
        Log.i("VAL SELECTED", "Value: ${e.y}, xIndex: ${e.x}, DataSet index: ${h.dataSetIndex}")
    }

    override fun onNothingSelected() {}

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
