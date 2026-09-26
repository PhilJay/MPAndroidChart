package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.databinding.ActivityScrollviewBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class ScrollViewActivity : DemoBase() {

    private lateinit var binding: ActivityScrollviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrollviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "ScrollViewActivity"

        binding.chart1.apply {
            description.isEnabled = false

            isPinchZoomEnabled = false

            isDrawBarShadowEnabled = false
            isDrawGridBackgroundEnabled = false

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.isDrawGridLinesEnabled = false

            axisLeft.isDrawGridLinesEnabled = false

            legend.isEnabled = false
        }

        setData(10)
        binding.chart1.isFitBarsEnabled = true
    }

    private fun setData(count: Int) {
        val values = ArrayList<BarEntry<Any?>>()

        for (i in 0 until count) {
            val value = (Math.random() * count).toFloat() + 15
            values.add(BarEntry(i.toFloat(), value.toInt().toFloat()))
        }

        val set = BarDataSet(values, "Data Set")
        set.colors = ColorTemplate.VORDIPLOM_COLORS
        set.isDrawValuesEnabled = false

        val data = BarData(set)

        binding.chart1.data = data
        binding.chart1.invalidate()
        binding.chart1.animateY(800)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.only_github, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/ScrollViewActivity.java")
                startActivity(i)
            }
        }
        return true
    }

    override fun saveToGallery() {}
}
