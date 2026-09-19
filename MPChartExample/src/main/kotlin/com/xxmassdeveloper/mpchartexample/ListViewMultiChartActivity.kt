package com.xxmassdeveloper.mpchartexample

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.databinding.ActivityListviewChartBinding
import com.xxmassdeveloper.mpchartexample.listviewitems.BarChartItem
import com.xxmassdeveloper.mpchartexample.listviewitems.ChartItem
import com.xxmassdeveloper.mpchartexample.listviewitems.LineChartItem
import com.xxmassdeveloper.mpchartexample.listviewitems.PieChartItem
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class ListViewMultiChartActivity : DemoBase() {

    private lateinit var binding: ActivityListviewChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListviewChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "ListViewMultiChartActivity"

        val list = ArrayList<ChartItem>()
        for (i in 0 until 30) {
            when (i % 3) {
                0 -> list.add(LineChartItem(generateDataLine(i + 1), applicationContext))
                1 -> list.add(BarChartItem(generateDataBar(i + 1), applicationContext))
                2 -> list.add(PieChartItem(generateDataPie(), applicationContext))
            }
        }

        binding.listView1.adapter = ChartDataAdapter(applicationContext, list)
    }

    private inner class ChartDataAdapter(context: Context, objects: List<ChartItem>) : ArrayAdapter<ChartItem>(context, 0, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return getItem(position)!!.getView(position, convertView, context)
        }

        override fun getItemViewType(position: Int): Int {
            return getItem(position)?.itemType ?: 0
        }

        override fun getViewTypeCount(): Int = 3
    }

    private fun generateDataLine(cnt: Int): LineData {
        val values1 = ArrayList<Entry<Any?>>()
        for (i in 0 until 12) {
            values1.add(Entry(i.toFloat(), ((Math.random() * 65).toInt() + 40).toFloat()))
        }

        val d1 = LineDataSet(values1, "New DataSet $cnt, (1)").apply {
            lineWidth = 2.5f
            circleRadius = 4.5f
            highlightColor = Color.rgb(244, 117, 117)
            isDrawValuesEnabled = false
        }

        val values2 = ArrayList<Entry<Any?>>()
        for (i in 0 until 12) {
            values2.add(Entry(i.toFloat(), values1[i].y - 30))
        }

        val d2 = LineDataSet(values2, "New DataSet $cnt, (2)").apply {
            lineWidth = 2.5f
            circleRadius = 4.5f
            highlightColor = Color.rgb(244, 117, 117)
            color = ColorTemplate.VORDIPLOM_COLORS[0]
            circleColor = ColorTemplate.VORDIPLOM_COLORS[0]
            isDrawValuesEnabled = false
        }

        val sets = ArrayList<ILineDataSet<*>>()
        sets.add(d1)
        sets.add(d2)

        return LineData(sets)
    }

    private fun generateDataBar(cnt: Int): BarData {
        val entries = ArrayList<BarEntry<Any?>>()
        for (i in 0 until 12) {
            entries.add(BarEntry(i.toFloat(), ((Math.random() * 70).toInt() + 30).toFloat()))
        }

        val d = BarDataSet(entries, "New DataSet $cnt").apply {
            colors = ColorTemplate.VORDIPLOM_COLORS
            highlightAlpha = 255
        }

        return BarData(d).apply { barWidth = 0.9f }
    }

    private fun generateDataPie(): PieData {
        val entries = ArrayList<PieEntry<Any?>>()
        for (i in 0 until 4) {
            entries.add(PieEntry((Math.random() * 70 + 30).toFloat(), "Quarter " + (i + 1)))
        }

        val d = PieDataSet(entries, "").apply {
            sliceSpace = 2f
            colors = ColorTemplate.VORDIPLOM_COLORS
        }

        return PieData(d)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.only_github, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/ListViewMultiChartActivity.java")
                startActivity(i)
            }
        }
        return true
    }

    override fun saveToGallery() {}
}
