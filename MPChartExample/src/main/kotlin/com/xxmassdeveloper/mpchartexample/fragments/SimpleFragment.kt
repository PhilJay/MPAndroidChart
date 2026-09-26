package com.xxmassdeveloper.mpchartexample.fragments

import android.graphics.Color
import android.graphics.Typeface
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.FileUtils

abstract class SimpleFragment : Fragment() {

    private val tf: Typeface by lazy { Typeface.createFromAsset(requireContext().assets, "OpenSans-Regular.ttf") }

    private val labels = arrayOf("Company A", "Company B", "Company C", "Company D", "Company E", "Company F")

    protected fun generateBarData(dataSets: Int, range: Float, count: Int): BarData {
        val sets = ArrayList<IBarDataSet<*>>()
        for (i in 0 until dataSets) {
            val entries = ArrayList<BarEntry<Any?>>()
            for (j in 0 until count) {
                entries.add(BarEntry(j.toFloat(), (Math.random() * range).toFloat() + range / 4))
            }
            val ds = BarDataSet(entries, labels[i])
            ds.colors = ColorTemplate.VORDIPLOM_COLORS
            sets.add(ds)
        }
        return BarData(sets).apply { setValueTypeface(tf) }
    }

    protected fun generateScatterData(dataSets: Int, range: Float, count: Int): ScatterData {
        val sets = ArrayList<IScatterDataSet<*>>()
        val shapes = ScatterChart.ScatterShape.entries
        for (i in 0 until dataSets) {
            val entries = ArrayList<Entry<Any?>>()
            for (j in 0 until count) {
                entries.add(Entry(j.toFloat(), (Math.random() * range).toFloat() + range / 4))
            }
            val ds = ScatterDataSet(entries, labels[i]).apply {
                scatterShapeSize = 6f
                setScatterShape(shapes[i % shapes.size])
                colors = ColorTemplate.COLORFUL_COLORS
                scatterShapeSize = 4.5f
            }
            sets.add(ds)
        }
        return ScatterData(sets).apply { setValueTypeface(tf) }
    }

    protected fun generatePieData(): PieData {
        val count = 4
        val entries1 = ArrayList<PieEntry<Any?>>()
        for (i in 0 until count) {
            entries1.add(PieEntry((Math.random() * 60 + 40).toFloat(), "Quarter " + (i + 1)))
        }
        val ds1 = PieDataSet(entries1, "Quarterly Revenues 2015").apply {
            colors = ColorTemplate.VORDIPLOM_COLORS
            sliceSpace = 2f
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }
        return PieData(ds1).apply { setValueTypeface(tf) }
    }

    protected fun generateLineData(): LineData {
        val assets = requireContext().assets
        val ds1 = LineDataSet(FileUtils.loadEntriesFromAssets(assets, "sine.txt"), "Sine function").apply {
            lineWidth = 2f
            isDrawCirclesEnabled = false
            color = ColorTemplate.VORDIPLOM_COLORS[0]
        }
        val ds2 = LineDataSet(FileUtils.loadEntriesFromAssets(assets, "cosine.txt"), "Cosine function").apply {
            lineWidth = 2f
            isDrawCirclesEnabled = false
            color = ColorTemplate.VORDIPLOM_COLORS[1]
        }
        val sets = ArrayList<ILineDataSet<*>>()
        sets.add(ds1)
        sets.add(ds2)
        return LineData(sets).apply { setValueTypeface(tf) }
    }

    protected fun getComplexity(): LineData {
        val assets = requireContext().assets
        val files = arrayOf("n.txt", "nlogn.txt", "square.txt", "three.txt")
        val names = arrayOf("O(n)", "O(nlogn)", "O(n²)", "O(n³)")
        val sets = ArrayList<ILineDataSet<*>>()
        for (i in files.indices) {
            sets.add(LineDataSet(FileUtils.loadEntriesFromAssets(assets, files[i]), names[i]).apply {
                color = ColorTemplate.VORDIPLOM_COLORS[i]
                circleColor = ColorTemplate.VORDIPLOM_COLORS[i]
                lineWidth = 2.5f
                circleRadius = 3f
            })
        }
        return LineData(sets).apply { setValueTypeface(tf) }
    }
}
