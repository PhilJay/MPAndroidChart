package com.xxmassdeveloper.mpchartexample

import android.os.Bundle
import android.widget.SeekBar
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.ColorBubbleEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnAxisSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.*


class AxisHighlightBubbleChartActivity : BubbleChartActivity(), OnAxisSelectedListener {

    val minAlpha = 0x10
    val maxAlpha = 0x80

    val maxSize = 25f
    val minSize = 10f

    var colors = ColorTemplate.MATERIAL_COLORS_16

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle("Touch X and Y Axes")

        with (chart) {
            setExtraBottomOffset(10f);
            getLegend().setEnabled(false);
            maxHighlightDistance = 30f
        }
        chart.axisSelectedListener = this

        with (chart.xAxis!!) {
            setTextSize(25f)
            setEnabled(true)
            setDrawLabels(true)
            isHighlightEnabled = true
            isDrawHighlightValue = true
        }
        with (chart.getRendererXAxis()) {
            setHighlightFillColor(colors[9])
            setHighlightTextColor(colors[1])
            setHighlightFillPadding(20f, 0f, 20f, 0f)
        }


        with (chart.leftAxis!!) {
            setTextSize(25f)
            isHighlightEnabled = true
            isDrawHighlightValue = true
        }
        with (chart.rendererLeftYAxis) {
            setHighlightFillColor(colors[10])
            setHighlightTextColor(colors[2])
            setHighlightFillPadding(10f, 10f, 10f, 10f)
        }
    }


    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val count = seekBarX.progress
        val range = seekBarY.progress

        tvX.text = count.toString()
        tvY.text = range.toString()

        val entries: MutableList<BubbleEntry> = mutableListOf()

        for (i in 0 until count) {
            val y = (Math.random() * range).toFloat()
            val size = maxSize
            val color = getBubbleColor(i)
            entries.add(ColorBubbleEntry(i.toFloat(), y, size, null, null, color))
        }

        val set = BubbleDataSet(entries, "")
        val data = BubbleData(set)
        data.setDrawValues(false)
        data.setHighlightCircleWidth(5f)

        chart.data = data
        chart.invalidate()
    }

    /**
     * Called when an axis has been selected outside the chart.
     *
     * @param h highlight object that contains information
     * about the highlighted position.
     */
    override fun onAxisSelected(h: Highlight) {

        when (h.type) {

            // increase transparency with distance from x touch point
            Highlight.Type.X_AXIS -> {
                val maxDistance = Math.max(h.x - chart.xChartMin, chart.xChartMax - h.x);
                val gradient = (maxAlpha - minAlpha) / maxDistance

                // there is only one set, but map for demo purposes
                chart.bubbleData.dataSets.map { set ->
                    for (i in 0 until set.entryCount) {
                        val entry = set.getEntryForIndex(i) as ColorBubbleEntry
                        val distance = Math.abs(entry.x - h.x)
                        val alpha = if (entry.containsX(h.x)) 0xff
                                        else maxAlpha - (distance * gradient).toInt()
                        entry.setAlpha(alpha)
                    }
                }

                // add a limit line
                with (chart.xAxis!!) {
                    if (!highlights.isMultipleHighlightsEnabled)
                        removeAllLimitLines()
                    val ll = LimitLine(h.x, String.format(Locale.getDefault(), "%.1f", h.x))
                    ll.textSize = 15f
                    addLimitLine(ll)
                }
            }

            // decrease size with distance from y touch point
            Highlight.Type.LEFT_AXIS -> {
                val maxDistance = Math.max(h.y - chart.yMin, chart.yMax - h.y);
                val gradient = (maxSize - minSize) / maxDistance

                chart.data.dataSets.map { set ->
                    for (i in 0 until set.entryCount) {
                        val e = set.getEntryForIndex(i)
                        val distance = Math.abs(e.y - h.y)
                        e.size = maxSize - distance * gradient
                    }
                }
                with (chart.leftAxis!!) {
                    removeAllLimitLines()
                    addLimitLine(LimitLine(h.y))
                }
            }
            else -> { // TBD
            }
        }
    }

    override fun onNothingSelected() {
        chart.xAxis!!.removeAllLimitLines()
        chart.leftAxis!!.removeAllLimitLines()
        chart.bubbleData.dataSets.map { set ->
            for (i in 0 until set.entryCount) {
                val entry = set.getEntryForIndex(i) as ColorBubbleEntry
                entry.alpha = 0xff
                entry.size = maxSize
            }
        }
        chart.invalidate()
    }

    private fun getBubbleColor(i: Int): Int {
        return colors[i % 16]
    }
}