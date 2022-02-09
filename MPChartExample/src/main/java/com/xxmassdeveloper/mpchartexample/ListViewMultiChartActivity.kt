package com.xxmassdeveloper.mpchartexample

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.listviewitems.BarChartItem
import com.xxmassdeveloper.mpchartexample.listviewitems.ChartItem
import com.xxmassdeveloper.mpchartexample.listviewitems.LineChartItem
import com.xxmassdeveloper.mpchartexample.listviewitems.PieChartItem
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

/**
 * Demonstrates the use of charts inside a ListView. IMPORTANT: provide a
 * specific height attribute for the chart inside your ListView item
 *
 * @author Philipp Jahoda
 */
class ListViewMultiChartActivity : DemoBase() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_listview_chart)
        title = "ListViewMultiChartActivity"
        val lv = findViewById<ListView>(R.id.listView1)
        val list = ArrayList<ChartItem>()

        // 30 items
        for (i in 0..29) {
            if (i % 3 == 0) {
                list.add(LineChartItem(generateDataLine(i + 1), applicationContext))
            } else if (i % 3 == 1) {
                list.add(BarChartItem(generateDataBar(i + 1), applicationContext))
            } else if (i % 3 == 2) {
                list.add(PieChartItem(generateDataPie(), applicationContext))
            }
        }
        val cda: ChartDataAdapter = ChartDataAdapter(
            applicationContext, list
        )
        lv.adapter = cda
    }

    /** adapter that supports 3 different item types  */
    private inner class ChartDataAdapter internal constructor(
        context: Context?,
        objects: List<ChartItem?>?
    ) : ArrayAdapter<ChartItem?>(
        context!!, 0, objects!!
    ) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return getItem(position)!!.getView(position, convertView, context)!!
        }

        override fun getItemViewType(position: Int): Int {
            // return the views type
            val ci = getItem(position)
            return ci?.itemType ?: 0
        }

        override fun getViewTypeCount(): Int {
            return 3 // we have 3 different item-types
        }
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Line data
     */
    private fun generateDataLine(cnt: Int): LineData {
        val values1 = ArrayList<Entry>()
        for (i in 0..11) {
            values1.add(Entry(i.toFloat(), ((Math.random() * 65).toInt() + 40).toFloat()))
        }
        val d1 = LineDataSet(values1, "New DataSet $cnt, (1)")
        d1.lineWidth = 2.5f
        d1.circleRadius = 4.5f
        d1.highLightColor = Color.rgb(244, 117, 117)
        d1.setDrawValues(false)
        val values2 = ArrayList<Entry>()
        for (i in 0..11) {
            values2.add(Entry(i.toFloat(), values1[i].y - 30))
        }
        val d2 = LineDataSet(values2, "New DataSet $cnt, (2)")
        d2.lineWidth = 2.5f
        d2.circleRadius = 4.5f
        d2.highLightColor = Color.rgb(244, 117, 117)
        d2.color = ColorTemplate.VORDIPLOM_COLORS[0]
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0])
        d2.setDrawValues(false)
        val sets = ArrayList<ILineDataSet>()
        sets.add(d1)
        sets.add(d2)
        return LineData(sets)
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Bar data
     */
    private fun generateDataBar(cnt: Int): BarData {
        val entries = ArrayList<BarEntry>()
        for (i in 0..11) {
            entries.add(BarEntry(i.toFloat(), (Math.random() * 70).toInt() + 30f))
        }
        val d = BarDataSet(entries, "New DataSet $cnt")
        d.setColors(*ColorTemplate.VORDIPLOM_COLORS)
        d.highLightAlpha = 255
        val cd = BarData(d)
        cd.barWidth = 0.9f
        return cd
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Pie data
     */
    private fun generateDataPie(): PieData {
        val entries = ArrayList<PieEntry>()
        for (i in 0..3) {
            entries.add(PieEntry((Math.random() * 70 + 30).toFloat(), "Quarter " + (i + 1)))
        }
        val d = PieDataSet(entries, "")

        // space between slices
        d.sliceSpace = 2f
        d.setColors(*ColorTemplate.VORDIPLOM_COLORS)
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
                i.data =
                    Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/ListViewMultiChartActivity.java")
                startActivity(i)
            }
        }
        return true
    }

    public override fun saveToGallery() { /* Intentionally left empty */
    }
}