package com.xxmassdeveloper.mpchartexample

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.databinding.ActivityListviewChartBinding
import com.xxmassdeveloper.mpchartexample.databinding.ListItemBarchartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class ListViewBarChartActivity : DemoBase() {

    private lateinit var binding: ActivityListviewChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListviewChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "ListViewBarChartActivity"

        val list = ArrayList<BarData>()
        for (i in 0 until 20) {
            list.add(generateData(i + 1))
        }

        binding.listView1.adapter = ChartDataAdapter(applicationContext, list)
    }

    private inner class ChartDataAdapter(context: Context, objects: List<BarData>) : ArrayAdapter<BarData>(context, 0, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val data = getItem(position)

            val view: View
            val holder: ViewHolder
            if (convertView == null) {
                val itemBinding = ListItemBarchartBinding.inflate(LayoutInflater.from(context))
                view = itemBinding.root
                holder = ViewHolder(itemBinding.chart)
                view.tag = holder
            } else {
                view = convertView
                holder = convertView.tag as ViewHolder
            }

            data?.apply {
                setValueTypeface(tfLight)
                setValueTextColor(Color.BLACK)
            }

            holder.chart.apply {
                description.isEnabled = false
                isDrawGridBackgroundEnabled = false

                xAxis.apply {
                    this.position = XAxis.XAxisPosition.BOTTOM
                    typeface = tfLight
                    isDrawGridLinesEnabled = false
                }

                axisLeft.apply {
                    typeface = tfLight
                    labelCount = 5
                    spaceTop = 15f
                }

                axisRight.apply {
                    typeface = tfLight
                    labelCount = 5
                    spaceTop = 15f
                }

                this.data = data
                isFitBarsEnabled = true

                animateY(700)
            }

            return view
        }
    }

    private class ViewHolder(val chart: BarChart)

    private fun generateData(cnt: Int): BarData {
        val entries = ArrayList<BarEntry<Any?>>()
        for (i in 0 until 12) {
            entries.add(BarEntry(i.toFloat(), (Math.random() * 70).toFloat() + 30))
        }

        val d = BarDataSet(entries, "New DataSet $cnt").apply {
            colors = ColorTemplate.VORDIPLOM_COLORS
            barShadowColor = Color.rgb(203, 203, 203)
        }

        val sets = ArrayList<IBarDataSet<*>>()
        sets.add(d)

        return BarData(sets).apply { barWidth = 0.9f }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.only_github, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/ListViewBarChartActivity.java")
                startActivity(i)
            }
        }
        return true
    }

    override fun saveToGallery() {}
}
