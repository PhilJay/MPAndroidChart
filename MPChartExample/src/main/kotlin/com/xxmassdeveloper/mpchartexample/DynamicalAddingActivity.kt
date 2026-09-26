package com.xxmassdeveloper.mpchartexample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.databinding.ActivityLinechartNoseekbarBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

class DynamicalAddingActivity : DemoBase(), OnChartValueSelectedListener {

    private lateinit var binding: ActivityLinechartNoseekbarBinding

    private val colors = ColorTemplate.VORDIPLOM_COLORS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinechartNoseekbarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "DynamicalAddingActivity"

        binding.chart1.apply {
            onChartValueSelectedListener = this@DynamicalAddingActivity
            isDrawGridBackgroundEnabled = false
            description.isEnabled = false
            noDataText = "Use the menu to add entries and data sets"
            invalidate()
        }
    }

    private fun addEntry() {
        val data = binding.chart1.data ?: LineData().also { binding.chart1.data = it }

        if (data.getDataSetByIndex(0) == null) {
            data.addDataSet(createSet())
        }

        val randomDataSetIndex = (Math.random() * data.dataSetCount).toInt()
        val randomSet = data.getDataSetByIndex(randomDataSetIndex) ?: return
        val value = (Math.random() * 50).toFloat() + 50f * (randomDataSetIndex + 1)

        data.addEntry(Entry(randomSet.entryCount.toFloat(), value), randomDataSetIndex)

        binding.chart1.notifyDataSetChanged()

        binding.chart1.setVisibleXRangeMaximum(6f)
        binding.chart1.moveViewTo((data.entryCount - 7).toFloat(), 50f, YAxis.AxisDependency.LEFT)
    }

    private fun removeLastEntry() {
        val data = binding.chart1.data ?: return
        val set = data.getDataSetByIndex(0) ?: return

        val e = set.getEntryForXValue((set.entryCount - 1).toFloat(), Float.NaN)

        data.removeEntry(e, 0)
        binding.chart1.notifyDataSetChanged()
        binding.chart1.invalidate()
    }

    private fun addDataSet() {
        val data = binding.chart1.data

        if (data == null) {
            binding.chart1.data = LineData()
            return
        }

        val count = data.dataSetCount + 1
        val amount = data.getDataSetByIndex(0)?.entryCount ?: return

        val values = ArrayList<Entry<Any?>>()
        for (i in 0 until amount) {
            values.add(Entry(i.toFloat(), (Math.random() * 50f).toFloat() + 50f * count))
        }

        val color = colors[count % colors.size]
        val set = LineDataSet(values, "DataSet $count").apply {
            lineWidth = 2.5f
            circleRadius = 4.5f
            this.color = color
            circleColor = color
            highlightColor = color
            valueTextSize = 10f
            valueTextColor = color
        }

        data.addDataSet(set)
        binding.chart1.notifyDataSetChanged()
        binding.chart1.invalidate()
    }

    private fun removeDataSet() {
        val data = binding.chart1.data ?: return

        data.removeDataSet(data.getDataSetByIndex(data.dataSetCount - 1))

        binding.chart1.notifyDataSetChanged()
        binding.chart1.invalidate()
    }

    private fun createSet(): LineDataSet<Any?> {
        return LineDataSet(ArrayList<Entry<Any?>>(), "DataSet 1").apply {
            lineWidth = 2.5f
            circleRadius = 4.5f
            color = Color.rgb(240, 99, 99)
            circleColor = Color.rgb(240, 99, 99)
            highlightColor = Color.rgb(190, 190, 190)
            axisDependency = YAxis.AxisDependency.LEFT
            valueTextSize = 10f
        }
    }

    override fun onValueSelected(e: Entry<*>, h: Highlight) {
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
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/DynamicalAddingActivity.java")
                startActivity(i)
            }
            R.id.actionAddEntry -> {
                addEntry()
                Toast.makeText(this, "Entry<*> added!", Toast.LENGTH_SHORT).show()
            }
            R.id.actionRemoveEntry -> {
                removeLastEntry()
                Toast.makeText(this, "Entry<*> removed!", Toast.LENGTH_SHORT).show()
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
                binding.chart1.clear()
                Toast.makeText(this, "Chart cleared!", Toast.LENGTH_SHORT).show()
            }
            R.id.actionSave -> saveChartToGalleryWithPermission(binding.chart1)
        }
        return true
    }

    override fun saveToGallery() = saveToGallery(binding.chart1, "DynamicalAddingActivity")
}
