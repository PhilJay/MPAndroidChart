package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.databinding.ActivityRealtimeLinechartBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class RealtimeLineChartActivity : DemoBase(), OnChartValueSelectedListener {

    private lateinit var binding: ActivityRealtimeLinechartBinding
    private var thread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRealtimeLinechartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "RealtimeLineChartActivity"

        binding.chart1.apply {
            onChartValueSelectedListener = this@RealtimeLineChartActivity
            description.isEnabled = true
            isTouchEnabled = true
            isDragEnabled = true
            isScaleEnabled = true
            isDrawGridBackgroundEnabled = false
            isPinchZoomEnabled = true
            setBackgroundColor(Color.LTGRAY)

            val lineData = LineData()
            lineData.setValueTextColor(Color.WHITE)
            data = lineData

            legend.apply {
                form = Legend.LegendForm.LINE
                typeface = tfLight
                textColor = Color.WHITE
            }

            xAxis.apply {
                typeface = tfLight
                textColor = Color.WHITE
                isDrawGridLinesEnabled = false
                isAvoidFirstLastClippingEnabled = true
                isEnabled = true
            }

            axisLeft.apply {
                typeface = tfLight
                textColor = Color.WHITE
                axisMaximum = 100f
                axisMinimum = 0f
                isDrawGridLinesEnabled = true
            }

            axisRight.isEnabled = false
        }
    }

    private fun addEntry() {
        val data = binding.chart1.data ?: return

        val set = data.getDataSetByIndex(0) ?: createSet().also { data.addDataSet(it) }

        data.addEntry(Entry(set.entryCount.toFloat(), (Math.random() * 40).toFloat() + 30f), 0)

        binding.chart1.notifyDataSetChanged()
        binding.chart1.setVisibleXRangeMaximum(120f)
        binding.chart1.moveViewToX(data.entryCount.toFloat())
    }

    private fun createSet(): LineDataSet<Any?> {
        return LineDataSet(ArrayList<Entry<Any?>>(), "Dynamic Data").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = ColorTemplate.holoBlue
            circleColor = Color.WHITE
            lineWidth = 2f
            circleRadius = 4f
            fillAlpha = 65
            fillColor = ColorTemplate.holoBlue
            highlightColor = Color.rgb(244, 117, 117)
            valueTextColor = Color.WHITE
            valueTextSize = 9f
            isDrawValuesEnabled = false
        }
    }

    private fun feedMultiple() {
        thread?.interrupt()

        val runnable = Runnable { addEntry() }

        thread = Thread {
            for (i in 0 until 1000) {
                runOnUiThread(runnable)

                try {
                    Thread.sleep(25)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.apply { start() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.realtime, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/RealtimeLineChartActivity.java")
                startActivity(i)
            }
            R.id.actionAdd -> addEntry()
            R.id.actionClear -> {
                binding.chart1.clearValues()
                Toast.makeText(this, "Chart cleared!", Toast.LENGTH_SHORT).show()
            }
            R.id.actionFeedMultiple -> feedMultiple()
            R.id.actionSave -> saveChartToGalleryWithPermission(binding.chart1)
        }
        return true
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "RealtimeLineChartActivity")

    override fun onValueSelected(e: Entry<*>, h: Highlight) {
        Log.i("Entry<*> selected", e.toString())
    }

    override fun onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.")
    }

    override fun onPause() {
        super.onPause()
        thread?.interrupt()
    }
}
