package com.xxmassdeveloper.mpchartexample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class DynamicalAddingActivity : DemoBase(), OnChartValueSelectedListener {
    private var chart: LineChart? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_linechart_noseekbar)
        title = "DynamicalAddingActivity"
        chart = findViewById(R.id.chart1)
        chart?.setOnChartValueSelectedListener(this)
        chart?.setDrawGridBackground(false)
        chart?.description?.isEnabled = false
        chart?.setNoDataText("No chart data available. Use the menu to add entries and data sets!")

//        chart.getXAxis().setDrawLabels(false);
//        chart.getXAxis().setDrawGridLines(false);
        chart?.invalidate()
    }

    private val colors = ColorTemplate.VORDIPLOM_COLORS
    private fun addEntry() {
        var data = chart!!.data
        if (data == null) {
            data = LineData()
            chart!!.data = data
        }
        var set = data.getDataSetByIndex(0)
        // set.addEntry(...); // can be called as well
        if (set == null) {
            set = createSet()
            data.addDataSet(set)
        }

        // choose a random dataSet
        val randomDataSetIndex = (Math.random() * data.dataSetCount).toInt()
        val randomSet = data.getDataSetByIndex(randomDataSetIndex)
        val value = (Math.random() * 50).toFloat() + 50f * (randomDataSetIndex + 1)
        data.addEntry(Entry(randomSet.entryCount.toFloat(), value), randomDataSetIndex)
        data.notifyDataChanged()

        // let the chart know it's data has changed
        chart!!.notifyDataSetChanged()
        chart!!.setVisibleXRangeMaximum(6f)
        //chart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
//
//            // this automatically refreshes the chart (calls invalidate())
        chart!!.moveViewTo((data.entryCount - 7).toFloat(), 50f, AxisDependency.LEFT)
    }

    private fun removeLastEntry() {
        val data = chart!!.data
        if (data != null) {
            val set = data.getDataSetByIndex(0)
            if (set != null) {
                val e = set.getEntryForXValue((set.entryCount - 1).toFloat(), Float.NaN)
                data.removeEntry(e, 0)
                // or remove by index
                // mData.removeEntryByXValue(xIndex, dataSetIndex);
                data.notifyDataChanged()
                chart!!.notifyDataSetChanged()
                chart!!.invalidate()
            }
        }
    }

    private fun addDataSet() {
        val data = chart!!.data
        if (data == null) {
            chart!!.data = LineData()
        } else {
            val count = data.dataSetCount + 1
            val amount = data.getDataSetByIndex(0).entryCount
            val values = ArrayList<Entry>()
            for (i in 0 until amount) {
                values.add(Entry(i.toFloat(), (Math.random() * 50f).toFloat() + 50f * count))
            }
            val set = LineDataSet(values, "DataSet $count")
            set.lineWidth = 2.5f
            set.circleRadius = 4.5f
            val color = colors[count % colors.size]
            set.color = color
            set.setCircleColor(color)
            set.highLightColor = color
            set.valueTextSize = 10f
            set.valueTextColor = color
            data.addDataSet(set)
            data.notifyDataChanged()
            chart!!.notifyDataSetChanged()
            chart!!.invalidate()
        }
    }

    private fun removeDataSet() {
        val data = chart!!.data
        if (data != null) {
            data.removeDataSet(data.getDataSetByIndex(data.dataSetCount - 1))
            chart!!.notifyDataSetChanged()
            chart!!.invalidate()
        }
    }

    private fun createSet(): LineDataSet {
        val set = LineDataSet(null, "DataSet 1")
        set.lineWidth = 2.5f
        set.circleRadius = 4.5f
        set.color = Color.rgb(240, 99, 99)
        set.setCircleColor(Color.rgb(240, 99, 99))
        set.highLightColor = Color.rgb(190, 190, 190)
        set.axisDependency = AxisDependency.LEFT
        set.valueTextSize = 10f
        return set
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected() {}
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dynamical, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data =
                    Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/DynamicalAddingActivity.java")
                startActivity(i)
            }
            R.id.actionAddEntry -> {
                addEntry()
                Toast.makeText(this, "Entry added!", Toast.LENGTH_SHORT).show()
            }
            R.id.actionRemoveEntry -> {
                removeLastEntry()
                Toast.makeText(this, "Entry removed!", Toast.LENGTH_SHORT).show()
            }
            R.id.actionAddDataSet -> {
                addDataSet()
                Toast.makeText(this, "DataSet added!", Toast.LENGTH_SHORT).show()
            }
            R.id.actionRemoveDataSet -> {
                removeDataSet()
                Toast.makeText(this, "DataSet removed!", Toast.LENGTH_SHORT).show()
            }
            R.id.actionClear -> {
                chart!!.clear()
                Toast.makeText(this, "Chart cleared!", Toast.LENGTH_SHORT).show()
            }
            R.id.actionSave -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    saveToGallery()
                } else {
                    requestStoragePermission(chart)
                }
            }
        }
        return true
    }

    override fun saveToGallery() {
        saveToGallery(chart!!, "DynamicalAddingActivity")
    }
}