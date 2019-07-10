package com.xxmassdeveloper.mpchartexample

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast

import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.xxmassdeveloper.mpchartexample.custom.RadarMarkerView
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import kotlinx.android.synthetic.main.activity_crecre_chart.*

import java.util.ArrayList

class CreCreChartActivity : FragmentActivity() {

    lateinit var total: TextView
    lateinit var review: TextView
    lateinit var mChart: RadarChart

    val testItem = arrayListOf<String>("진행능력", "소통력", "참신성", "편집력", "핫 지수")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crecre_chart)

        review = act_crecre_chart_tv_review
        total = act_crecre_chart_tv_total

        val fontReguler = total.typeface

        total.setTextColor(Color.rgb(51, 51, 51))

        mChart = act_crecre_chart_rc
        mChart.run {
            isRotationEnabled = false
            description.isEnabled = false
            webLineWidth = 1f
            webColor = Color.WHITE
            webLineWidthInner = 1f
            radarBackgroundColor = Color.rgb(240, 240, 240)
            webOuterColor = Color.rgb(170, 170, 170)
            webColorInner = Color.rgb(221, 221, 221)
            //webAlpha = 100
        }


        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        val mv = RadarMarkerView(this, R.layout.radar_markerview)
        mv.chartView = mChart // For bounds control
        mChart.marker = mv // Set the marker to the chart

        val cnt = 5

        val entries1 = ArrayList<RadarEntry>()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (i in 0 until cnt) {
            val val1 = (i + 1).toFloat()
            entries1.add(RadarEntry(val1))
        }

        val set1 = RadarDataSet(entries1, "크리에이터 스텟")
        set1.color = Color.rgb(28, 28, 28)
        set1.fillDrawable = getDrawable(R.drawable.radar_bg)
        set1.setDrawFilled(true)
        set1.lineWidth = 1f

        val sets = ArrayList<IRadarDataSet>()
        sets.add(set1)

        val data = RadarData(sets)
        data.run {
            setValueTypeface(fontReguler)
            setValueTextSize(8f)
            setDrawValues(false)
            setValueTextColor(Color.WHITE)
            isHighlightEnabled = false
        }

        mChart.data = data
        mChart.invalidate()

        total.text = "토탈${dataSetAvg(entries1)}점"
        review.text = "(192명 리뷰)"

        mChart.animateXY(1400, 1400, Easing.EaseInOutQuad)

        mChart.run {
            xAxis.run {
                typeface = fontReguler
                textSize = 14f
                yOffset = 0f
                xOffset = 0f
                setMultiLineLabel(true)
                valueFormatter = object : IAxisValueFormatter {

                    private val mActivities = arrayOf(
                            "${testItem[0]}\n${entries1[0].value}점",
                            "${testItem[1]}\n${entries1[1].value}점",
                            "${testItem[2]}\n${entries1[2].value}점",
                            "${testItem[3]}\n${entries1[3].value}점",
                            "${testItem[4]}\n${entries1[4].value}점")

                    override fun getFormattedValue(value: Float, axis: AxisBase): String {
                        return mActivities[value.toInt() % mActivities.size]
                    }
                }
                textColor = R.color.chartXAxisTextColor
            }

            yAxis.run {
                //typeface = mTfLight
                setLabelCount(6, true)
                axisMinimum = 0f
                axisMaximum = 5f
                setDrawLabels(false)
            }

//            legend.run {
//                verticalAlignment = Legend.LegendVerticalAlignment.TOP
//                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
//                orientation = Legend.LegendOrientation.HORIZONTAL
//                setDrawInside(false)
//                typeface = mTfLight
//                xEntrySpace = 7f
//                yEntrySpace = 5f
//                textColor = Color.WHITE
//            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.radar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.actionToggleValues -> {
                for (set in mChart.data.dataSets)
                    set.setDrawValues(!set.isDrawValuesEnabled)

                mChart.invalidate()
            }
            R.id.actionToggleHighlight -> {
                if (mChart.data != null) {
                    mChart.data.isHighlightEnabled = !mChart!!.data.isHighlightEnabled
                    mChart.invalidate()
                }
            }
            R.id.actionToggleRotate -> {
                if (mChart.isRotationEnabled)
                    mChart.isRotationEnabled = false
                else
                    mChart.isRotationEnabled = true
                mChart.invalidate()
            }
            R.id.actionToggleFilled -> {

                val sets = mChart.data
                        .dataSets as ArrayList<IRadarDataSet>

                for (set in sets) {
                    if (set.isDrawFilledEnabled)
                        set.setDrawFilled(false)
                    else
                        set.setDrawFilled(true)
                }
                mChart.invalidate()
            }
            R.id.actionToggleHighlightCircle -> {

                val sets = mChart.data
                        .dataSets as ArrayList<IRadarDataSet>

                for (set in sets) {
                    set.isDrawHighlightCircleEnabled = !set.isDrawHighlightCircleEnabled
                }
                mChart.invalidate()
            }
            R.id.actionSave -> {
                if (mChart.saveToPath("title" + System.currentTimeMillis(), "")) {
                    Toast.makeText(applicationContext, "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show()
                } else
                    Toast.makeText(applicationContext, "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show()
            }
            R.id.actionToggleXLabels -> {
                mChart.xAxis.isEnabled = !mChart!!.xAxis.isEnabled
                mChart.notifyDataSetChanged()
                mChart.invalidate()
            }
            R.id.actionToggleYLabels -> {

                mChart.yAxis.isEnabled = !mChart!!.yAxis.isEnabled
                mChart.invalidate()
            }
            R.id.animateX -> {
                mChart.animateX(1400)
            }
            R.id.animateY -> {
                mChart.animateY(1400)
            }
            R.id.animateXY -> {
                mChart.animateXY(1400, 1400)
            }
            R.id.actionToggleSpin -> {
                mChart.spin(2000, mChart!!.rotationAngle, mChart!!.rotationAngle + 360, Easing.EaseInCubic)
            }
        }
        return true
    }

    fun setData() {

    }

    fun dataSetAvg(dataSet: ArrayList<RadarEntry>): Float {
        var total = 0f
        for (i in dataSet.indices)
            total += dataSet[i].value
        return total / dataSet.size
    }
}
