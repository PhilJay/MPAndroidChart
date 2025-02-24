// TODO: Finish and add to main activity list
package info.appdev.chartexample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.listener.OnDrawListener
import info.appdev.chartexample.databinding.ActivityDrawChartBinding
import info.appdev.chartexample.notimportant.DemoBase

/**
 * This Activity demonstrates drawing into the Chart with the finger. Both line,
 * bar and scatter charts can be used for drawing.
 *
 * @author Philipp Jahoda
 */
class DrawChartActivity : DemoBase(), OnChartValueSelectedListener, OnDrawListener {
    private lateinit var binding: ActivityDrawChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityDrawChartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        title = "DrawChartActivity"

        // listener for selecting and drawing
        binding.chart1.setOnChartValueSelectedListener(this)
        binding.chart1.setOnDrawListener(this)

        // if disabled, drawn data sets with the finger will not be automatically
        // finished
        // chart.setAutoFinish(true);
        binding.chart1.setDrawGridBackground(false)

        // add dummy-data to the chart
        initWithDummyData()

        val xl = binding.chart1.getXAxis()
        xl.typeface = tfRegular
        xl.setAvoidFirstLastClipping(true)

        val yl = binding.chart1.getAxisLeft()
        yl.typeface = tfRegular

        binding.chart1.getLegend().isEnabled = false

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

        binding.chart1.data = data
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.draw, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionToggleValues -> {
                val sets = binding.chart1.data!!.getDataSets()

                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.setDrawValues(!set.isDrawValuesEnabled)
                }

                binding.chart1.invalidate()
            }

            R.id.actionToggleHighlight -> {
                if (binding.chart1.data != null) {
                    binding.chart1.data!!.isHighlightEnabled = !binding.chart1.data!!.isHighlightEnabled
                    binding.chart1.invalidate()
                }
            }

            R.id.actionTogglePinch -> {
                if (binding.chart1.isPinchZoomEnabled) binding.chart1.setPinchZoom(false)
                else binding.chart1.setPinchZoom(true)

                binding.chart1.invalidate()
            }

            R.id.actionToggleAutoScaleMinMax -> {
                binding.chart1.isAutoScaleMinMaxEnabled = !binding.chart1.isAutoScaleMinMaxEnabled
                binding.chart1.notifyDataSetChanged()
            }

            R.id.actionSave -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveToGallery()
                } else {
                    requestStoragePermission(binding.chart1)
                }
            }
        }
        return true
    }

    override fun saveToGallery() {
        saveToGallery(binding.chart1, "DrawChartActivity")
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        Log.i("VAL SELECTED", ("Value: " + e.y + ", xIndex: " + e.x + ", DataSet index: " + h.dataSetIndex))
    }

    override fun onNothingSelected() = Unit

    /** callback for each new entry drawn with the finger  */
    override fun onEntryAdded(entry: Entry) {
        Log.i(Chart.LOG_TAG, entry.toString())
    }

    /** callback when a DataSet has been drawn (when lifting the finger)  */
    override fun onDrawFinished(dataSet: DataSet<*>) {
        Log.i(Chart.LOG_TAG, "DataSet drawn. " + dataSet.toSimpleString())

        // prepare the legend again
        binding.chart1.legendRenderer.computeLegend(binding.chart1.data)
    }

    override fun onEntryMoved(entry: Entry) {
        Log.i(Chart.LOG_TAG, "Point moved $entry")
    }
}
