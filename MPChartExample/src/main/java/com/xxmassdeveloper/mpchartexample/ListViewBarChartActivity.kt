package com.xxmassdeveloper.mpchartexample

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

/**
 * Demonstrates the use of charts inside a ListView. IMPORTANT: provide a
 * specific height attribute for the chart inside your ListView item
 *
 * @author Philipp Jahoda
 */
class ListViewBarChartActivity : DemoBase() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_listview_chart)
        title = "ListViewBarChartActivity"
        val lv = findViewById<ListView>(R.id.listView1)
        val list = ArrayList<BarData>()

        // 20 items
        for (i in 0..19) {
            list.add(generateData(i + 1))
        }
        val cda: ChartDataAdapter = ChartDataAdapter(
            applicationContext, list
        )
        lv.adapter = cda
    }

    private inner class ChartDataAdapter internal constructor(
        context: Context?,
        objects: List<BarData?>?
    ) : ArrayAdapter<BarData?>(
        context!!, 0, objects!!
    ) {
        @SuppressLint("InflateParams")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val data = getItem(position)
            val holder: ViewHolder
            if (convertView == null) {
                holder = ViewHolder()
                convertView = LayoutInflater.from(context).inflate(
                    R.layout.list_item_barchart, null
                )
                holder.chart = convertView.findViewById(R.id.chart)
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }

            // apply styling
            if (data != null) {
                data.setValueTypeface(tfLight)
                data.setValueTextColor(Color.BLACK)
            }
            holder.chart!!.description.isEnabled = false
            holder.chart!!.setDrawGridBackground(false)
            val xAxis = holder.chart!!.xAxis
            xAxis.position = XAxisPosition.BOTTOM
            xAxis.typeface = tfLight
            xAxis.setDrawGridLines(false)
            val leftAxis = holder.chart!!.axisLeft
            leftAxis.typeface = tfLight
            leftAxis.setLabelCount(5, false)
            leftAxis.spaceTop = 15f
            val rightAxis = holder.chart!!.axisRight
            rightAxis.typeface = tfLight
            rightAxis.setLabelCount(5, false)
            rightAxis.spaceTop = 15f

            // set data
            holder.chart!!.data = data
            holder.chart!!.setFitBars(true)

            // do not forget to refresh the chart
//            holder.chart.invalidate();
            holder.chart!!.animateY(700)
            return convertView!!
        }

        private inner class ViewHolder {
            var chart: BarChart? = null
        }
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Bar data
     */
    private fun generateData(cnt: Int): BarData {
        val entries = ArrayList<BarEntry>()
        for (i in 0..11) {
            entries.add(BarEntry(i.toFloat(), (Math.random() * 70).toFloat() + 30))
        }
        val d = BarDataSet(entries, "New DataSet $cnt")
        d.setColors(*ColorTemplate.VORDIPLOM_COLORS)
        d.barShadowColor = Color.rgb(203, 203, 203)
        val sets = ArrayList<IBarDataSet>()
        sets.add(d)
        val cd = BarData(sets)
        cd.barWidth = 0.9f
        return cd
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.only_github, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data =
                    Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/ListViewBarChartActivity.java")
                startActivity(i)
            }
        }
        return true
    }

    public override fun saveToGallery() { /* Intentionally left empty */
    }
}