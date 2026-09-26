package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.xxmassdeveloper.mpchartexample.databinding.ActivityCandlechartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class CandleStickChartActivity : DemoBase(), SeekBar.OnSeekBarChangeListener {

    private lateinit var binding: ActivityCandlechartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCandlechartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "CandleStickChartActivity"

        binding.seekBar1.setOnSeekBarChangeListener(this)
        binding.seekBar2.setOnSeekBarChangeListener(this)

        binding.chart1.apply {
            setBackgroundColor(Color.WHITE)

            description.isEnabled = false

            maxVisibleCount = 60

            isPinchZoomEnabled = false

            isDrawGridBackgroundEnabled = false
        }

        binding.chart1.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            isDrawGridLinesEnabled = false
        }

        binding.chart1.axisLeft.apply {
            labelCount = 7
            isDrawGridLinesEnabled = false
            isDrawAxisLineEnabled = false
        }

        binding.chart1.axisRight.isEnabled = false

        binding.seekBar1.progress = 40
        binding.seekBar2.progress = 100

        binding.chart1.legend.isEnabled = false
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val count = binding.seekBar1.progress

        binding.tvXMax.text = count.toString()
        binding.tvYMax.text = binding.seekBar2.progress.toString()

        binding.chart1.resetTracking()

        val values = ArrayList<CandleEntry<Any?>>()

        for (i in 0 until count) {
            val multi = (binding.seekBar2.progress + 1).toFloat()
            val value = (Math.random() * 40).toFloat() + multi

            val high = (Math.random() * 9).toFloat() + 8f
            val low = (Math.random() * 9).toFloat() + 8f

            val open = (Math.random() * 6).toFloat() + 1f
            val close = (Math.random() * 6).toFloat() + 1f

            val even = i % 2 == 0

            values.add(
                CandleEntry(
                    i.toFloat(),
                    value + high,
                    value - low,
                    if (even) value + open else value - open,
                    if (even) value - close else value + close,
                    ContextCompat.getDrawable(this, R.drawable.star)
                )
            )
        }

        val set1 = CandleDataSet(values, "Data Set")

        set1.isDrawIconsEnabled = false
        set1.axisDependency = YAxis.AxisDependency.LEFT
        set1.shadowColor = Color.DKGRAY
        set1.shadowWidth = 0.7f
        set1.decreasingColor = Color.RED
        set1.decreasingPaintStyle = Paint.Style.FILL
        set1.increasingColor = Color.rgb(122, 242, 84)
        set1.increasingPaintStyle = Paint.Style.STROKE
        set1.neutralColor = Color.BLUE

        val data = CandleData(set1)

        binding.chart1.data = data
        binding.chart1.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.candle, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val chart = binding.chart1
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/CandleStickChartActivity.java")
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
            R.id.actionToggleMakeShadowSameColorAsCandle -> {
                chart.data?.dataSets?.forEach { (it as CandleDataSet<*>).shadowColorSameAsCandle = !it.shadowColorSameAsCandle }
                chart.invalidate()
            }
            R.id.animateX -> chart.animateX(2000)
            R.id.animateY -> chart.animateY(2000)
            R.id.animateXY -> chart.animateXY(2000, 2000)
            R.id.actionSave -> saveChartToGalleryWithPermission(chart)
        }
        return true
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "CandleStickChartActivity")

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
