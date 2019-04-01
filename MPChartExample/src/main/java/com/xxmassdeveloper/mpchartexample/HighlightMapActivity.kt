package com.xxmassdeveloper.mpchartexample

import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.github.mikephil.charting.charts.BubbleChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet
import com.github.mikephil.charting.listener.OnAxisSelectedListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.custom.CityMarkerView
import com.xxmassdeveloper.mpchartexample.custom.data.City
import com.xxmassdeveloper.mpchartexample.custom.formatter.LogLogAxisFormatter
import com.xxmassdeveloper.mpchartexample.custom.renderer.CityRenderer
import com.xxmassdeveloper.mpchartexample.custom.utils.ExampleFileUtils
import com.xxmassdeveloper.mpchartexample.custom.utils.Extensions.Companion.almostEquals
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import java.util.*

class HighlightMapActivity : DemoBase(), OnChartValueSelectedListener, OnAxisSelectedListener {
    var logEnabled = true

    private lateinit var chart: BubbleChart
    private val rightAxisFormatter = LogLogAxisFormatter()

    private lateinit var largestCity: City
    private lateinit var smallestCity: City

    private val maxRadiusPix = 40f
    private val minRadiusPix = 10f

    private lateinit var legendColors: IntArray
    private lateinit var legendLabels: Array<String>
    private val colors = ColorTemplate.MATERIAL_COLORS_16

    private val savedData = BubbleData()
    private val limitFormatter = LargeValueFormatter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_mapbubblechart)

        chart = findViewById(R.id.chart1)

        // create the raw data - sorted by x
        val bubbles = loadData()

        // create the chart data
        val set = BubbleDataSet(bubbles, "")
        set.colors = colors.toList()
        val data = BubbleData(set)
        with (data) {
            setDrawValues(false)
            setHighlightCircleWidth(5f)
        }

        title = "City Populations"

        chart.setOnChartValueSelectedListener(this)
        chart.axisSelectedListener = this
        with (chart) {
            description.isEnabled = false
            setDrawGridBackground(false)

            setExtraOffsets(0f, 0f, 10f, 10f)

            // enable scaling and dragging
            isDragEnabled = true
            setScaleEnabled(true)

            setMaxVisibleValueCount(200)
            setPinchZoom(true)
        }

        with (chart.axisRight) {
            isHighlightEnabled = true
            isMultipleHighlightsEnabled = true
            valueFormatter = rightAxisFormatter
            isEnabled = true
            textSize = 25f
            setDrawZeroLine(false)
            setDrawGridLines(false)
            mEntryCount = 4

            // set the right axis limits to slightly larger than population range
            axisMaximum = Math.ceil (rightAxisFormatter.encode(largestCity.population.toFloat()).toDouble() * 10f).toFloat() / 10f
            axisMinimum = Math.floor(rightAxisFormatter.encode(smallestCity.population.toFloat()).toDouble() * 10f).toFloat() / 10f
        }

        with (chart.rendererRightYAxis) {
            setHighlightFillPadding(0f, 0f, 0f, 0f)
        }

        // load the boundary
        val boundary = loadBoundary()

        (chart.renderer as CityRenderer).let {
            it.maxRadiusPix = maxRadiusPix
            it.minRadiusPix = minRadiusPix
            it.maxPopulation = largestCity.population
            it.minPopulation = smallestCity.population

            // draw the entire boundary, not just the cities
            val bounds = it.setBoundary(boundary)
            chart.axisLeft.axisMaximum = bounds.top
            chart.axisLeft.axisMinimum = bounds.bottom
            chart.xAxis!!.axisMinimum = bounds.left
            chart.xAxis!!.axisMaximum = bounds.right
        }

        with (chart.axisLeft) {
            isHighlightEnabled = true
            textSize = 15f
            // isEnabled = false
        }

        with (chart.xAxis!!) {
            position = XAxis.XAxisPosition.BOTTOM
            isHighlightEnabled = true
            textSize = 15f
            labelRotationAngle = -90f
        }

        chart.getRendererXAxis().setHighlightFillPadding(0f, 20f, 0f, 10f)

        with (CityMarkerView(applicationContext, R.layout.custom_marker_view_dark_text)) {
            minimumHeight = 30
            chartView = chart
            chart.marker = this
            chart.setDrawMarkers(true)
        }

        with (chart.legend) {
            isEnabled = true
            formSize = 10f // set the size of the legend forms/shapes
            form = Legend.LegendForm.CIRCLE // set what type of form/shape should be used
            setCustom(legendColors, legendLabels)
            textSize = 15f
        }

        chart.data = data
        chart.invalidate()

        // save the data for future reuse
        chart.bubbleData.dataSets.map {
            savedData.addDataSet(it)
        }
    }

    override fun onValueSelected(entry: Entry, highlight: Highlight) {
        Log.i("Activity", "onValueSelected: " + entry)
    }

    override fun onAxisSelected(highlight: Highlight) {
        Log.i("Activity", "onAxisSelected: " + highlight)
        val highPopulation: Float
        val lowPopulation: Float

        when (highlight.type) {
            Highlight.Type.RIGHT_AXIS -> {
                // determine the population that was clicked
                val population = rightAxisFormatter.decode(highlight.y)

                // count the highlights - including this one
                when (chart.axisRight.highlights.size()) {
                    2 -> {
                        chart.axisRight.removeAllLimitLines()
                        val highs = chart.axisRight.highlights
                        val itr = highs.iterator()
                        val firstPop  = rightAxisFormatter.decode((itr.next() as Highlight).y)
                        val secondPop = rightAxisFormatter.decode((itr.next() as Highlight).y)
                        highPopulation = Math.max(firstPop, secondPop)
                        lowPopulation  = Math.min(firstPop, secondPop)
                    }
                    1 -> {
                        // go 20% above and below
                        highPopulation = population * 1.2f
                        lowPopulation = population * 0.8f
                    }
                    else  // 3rd click - remove highlights
                    -> {
                        onNothingSelected()
                        return
                    }
                }

                // draw upper and lower limit lines
                val upperMsg = String.format(Locale.getDefault(), "population < %s",
                        limitFormatter.getFormattedValue(highPopulation))
                val lowerMsg = String.format(Locale.getDefault(), "population > %s",
                        limitFormatter.getFormattedValue(lowPopulation))
                val upper = LimitLine(rightAxisFormatter.encode(highPopulation), upperMsg)
                val lower = LimitLine(rightAxisFormatter.encode(lowPopulation), lowerMsg)
                upper.textSize = 15f
                upper.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
                lower.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
                lower.textSize = 15f
                with (chart.rightAxis!!) {
                    addLimitLine(upper)
                    addLimitLine(lower)
                }

                val max = highPopulation.toInt()
                val min = lowPopulation.toInt()

                // clear the data
                chart.clearValues()

                // make make new data set with just the included cities
                val newSets = ArrayList<IBubbleDataSet>()
                savedData.dataSets.map { set ->
                    val show: MutableList<ColorBubbleEntry> = mutableListOf()
                    for (i in 0 until set.entryCount) {
                        val entry = set.getEntryForIndex(i) as ColorBubbleEntry
                        val pop = (entry.data as City).population
                        if (pop in min..max) {
                            val copy = entry.copy()
                            copy.alpha = 0xff
                            show.add(copy)
                            if (logEnabled) Log.i("onAxisSelected", "showing " + (entry.getData() as City).name)
                        }
                    }
                    val newSet = ColorBubbleDataSet(show, set.label)
                    newSet.copyFieldsFrom(set as BubbleDataSet)
                    newSets.add(newSet)
                }
                val data = BubbleData(newSets)
                chart.data = data
                chart.invalidate()
            }
            Highlight.Type.X_AXIS -> {
                with (chart.xAxis!!) {
                    removeAllLimitLines()
                    addLimitLine(LimitLine(highlight.x))
                }
            }
            Highlight.Type.LEFT_AXIS -> {
                with(chart.leftAxis!!) {
                    removeAllLimitLines()
                    addLimitLine(LimitLine(highlight.y))
                }
            }
            else -> Log.i("Activity", "ignoring $highlight")
        }
    }

    override fun onNothingSelected() {
        Log.i("Activity", "onNothingSelected")
        // restore all data
        with (chart) {
            savedData.dataSets.map {
                data.dataSets.add((it as BubbleDataSet).copy() as IBubbleDataSet)
            }
            xAxis!!.removeAllLimitLines()
            leftAxis!!.removeAllLimitLines()
            rightAxis!!.removeAllLimitLines()
            clearAllHighlights()
            invalidate()
        }
    }

    private fun loadData(): List<BubbleEntry> {
        val cities = ExampleFileUtils.loadCaliforniaCitiesFromAssets(assets, "usa_cities.txt")

        Collections.sort(cities, City.BY_POPULATION)
        smallestCity = cities[0]
        largestCity = cities[cities.size - 1]


        Log.i("Activity", "largest= " + largestCity)
        Log.i("Activity", "smallest= " + smallestCity)

        Collections.sort(cities, City.BY_LATITUDE)
        Log.i("Activity", "lowest= " + cities[0])
        Log.i("Activity", "highest= " + cities[cities.size - 1])

        Collections.sort(cities, City.BY_LONGITUDE)
        Log.i("Activity", "western= " + cities[0])
        Log.i("Activity", "eastern= " + cities[cities.size - 1])

        // make 20 colors - kinda ROYGBIV, use every 4th
        val colors = ColorTemplate.DISTINCT_COLORS

        legendColors = intArrayOf(colors[15], colors[11], colors[7], colors[3], colors[0])
        legendLabels = arrayOf("< 100k", "< 250k", "< 500k", "< 1M", "> 1M")

        val entries: MutableList<BubbleEntry> = mutableListOf()
        for (city in cities) {
            val baseColor = when (city.population) {
                in 1 .. 100E3.toInt() -> colors[15]
                in 1 .. 250E3.toInt() -> colors[11]
                in 1 .. 500E3.toInt() -> colors[7]
                in 1 .. 1E6.toInt()   -> colors[3]
                else ->                  colors[0]
            }
            val color = ColorTemplate.colorWithAlpha(baseColor, 0x80)

            val entry = ColorBubbleEntry(city.longitide, city.latitude, city.population.toFloat(), null, city, color)
            entries.add(entry)
//            if (logEnabled)
//                Log.i("HighlightMapActivity", String.format("read: %s, %.1f, %.1f, %d", city.name, city.longitide, city.latitude, city.population))
        }
        return entries
    }




    override fun saveToGallery() {
        saveToGallery(chart, title.toString())
    }

    /**
     * Reads boundary from "california_boundary.txt".
     * The file includes California ans several islands.
     * Ignores the islands and returns only the biggest shape.
     */
    private fun loadBoundary(): List<PointF> {
        val points = ExampleFileUtils.loadBoundariesFromAssets(assets, "california_boundary.txt")

        // break into individual shapes
        val islands: MutableList<List<PointF>> = mutableListOf()
        val island: MutableList<PointF> = mutableListOf()
        var first = PointF()
        var count = 0
        points.map p@ { p ->
            island.add(p)
            if (count == 0) {
                first = p
                ++count
            } else if (first.almostEquals(p)) {
                // we have closed an island
                islands.add(island.toList())
                island.clear()
                count = 0
            } else {
                ++count
            }
        }
        val biggest = islands.maxBy { it.size } ?: emptyList()
        return biggest.toList()
    }
}


