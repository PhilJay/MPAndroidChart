// TODO: Finish and add to main activity list
package com.xxmassdeveloper.mpchartexample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.listener.OnDrawListener
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

/**
 * This Activity demonstrates drawing into the Chart with the finger. Both line,
 * bar and scatter charts can be used for drawing.
 *
 * @author Philipp Jahoda
 */
class DrawChartActivity : DemoBase(), OnChartValueSelectedListener, OnDrawListener {
    private var chart: LineChart? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_draw_chart)
        title = "DrawChartActivity"
        chart = findViewById(R.id.chart1)

        // listener for selecting and drawing
        chart?.setOnChartValueSelectedListener(this)
        chart?.setOnDrawListener(this)

        // if disabled, drawn data sets with the finger will not be automatically
        // finished
        // chart.setAutoFinish(true);
        chart?.setDrawGridBackground(false)

        // add dummy-data to the chart
        initWithDummyData()
        val xl = chart?.xAxis
        xl?.typeface = tfRegular
        xl?.setAvoidFirstLastClipping(true)
        val yl = chart?.axisLeft
        yl?.typeface = tfRegular
        chart?.legend?.isEnabled = false

        // chart.setYRange(-40f, 40f, true);
        // call this to reset the changed y-range
        // chart.resetYRange(true);
    }

    private fun initWithDummyData() {
        val values = ArrayList<Entry>()

        // create a dataset and give it a type (0)
        val set1 = LineDataSet(values, "DataSet")
        set1.lineWidth = 3f
        set1.circleRadius = 5f

        // create a data object with the data sets
        val data = LineData(set1)
        chart!!.data = data
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.draw, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionToggleValues -> {
                val sets = chart!!.data
                    .dataSets
                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.setDrawValues(!set.isDrawValuesEnabled)
                }
                chart!!.invalidate()
            }
            R.id.actionToggleHighlight -> {
                if (chart!!.data != null) {
                    chart!!.data.isHighlightEnabled = !chart!!.data.isHighlightEnabled
                    chart!!.invalidate()
                }
            }
            R.id.actionTogglePinch -> {
                if (chart!!.isPinchZoomEnabled) chart!!.setPinchZoom(false) else chart!!.setPinchZoom(
                    true
                )
                chart!!.invalidate()
            }
            R.id.actionToggleAutoScaleMinMax -> {
                chart!!.isAutoScaleMinMaxEnabled = !chart!!.isAutoScaleMinMaxEnabled
                chart!!.notifyDataSetChanged()
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
        saveToGallery(chart!!, "DrawChartActivity")
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        Log.i(
            "VAL SELECTED",
            "Value: " + e.y + ", xIndex: " + e.x
                    + ", DataSet index: " + h.dataSetIndex
        )
    }

    override fun onNothingSelected() {}

    /** callback for each new entry drawn with the finger  */
    override fun onEntryAdded(entry: Entry) {
        Log.i(Chart.LOG_TAG, entry.toString())
    }

    /** callback when a DataSet has been drawn (when lifting the finger)  */
    override fun onDrawFinished(dataSet: DataSet<*>) {
        Log.i(Chart.LOG_TAG, "DataSet drawn. " + dataSet.toSimpleString())

        // prepare the legend again
        chart!!.legendRenderer.computeLegend(chart!!.data)
    }

    override fun onEntryMoved(entry: Entry) {
        Log.i(Chart.LOG_TAG, "Point moved $entry")
    }
}