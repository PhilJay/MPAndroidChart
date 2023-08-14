package com.xxmassdeveloper.mpchartexample

import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.util.Log
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils

class DataTools {
    companion object {
        private const val VAL_COUNT = 45
        private const val VAL_RANGE = 180f

        private val VAL_FIX = arrayOf(
            94.84043, -19.610321, 34.980606, 137.20502, 3.3113098, 18.506958,
            72.16055, 36.291832, 135.97142, 122.0381, 85.873055, 68.582016, -13.099461, 85.85466,
            85.45009, 97.90119, 132.87332, 125.11917, 147.57323, -2.4140186, 28.75475, -17.208168,
            70.36055, 10.715485, 53.00132, 49.197624, 48.25173, 117.765854, 15.96653, 114.79433,
            127.782455, 14.049572, 44.2753, 106.75516, 108.96782, 75.33596, 128.22353, -12.423063,
            44.768654, -25.790316, 5.9754066, 99.64748, 141.99321, -17.990795, 38.272446
        )

        fun setData(context: Context, lineChart: LineChart, count: Int = VAL_COUNT, range: Float = VAL_RANGE) {
            Log.d("setData", "$count= range=$range")
            val values = ArrayList<Entry>()
            if (count == VAL_COUNT) {
                VAL_FIX.forEachIndexed { index, d ->
                    values.add(Entry(index.toFloat(), d.toFloat(), ContextCompat.getDrawable(context, R.drawable.star)))
                }
            } else {
                for (i in 0 until count) {
                    val value = (Math.random() * range).toFloat() - 30
                    values.add(Entry(i.toFloat(), value, ContextCompat.getDrawable(context, R.drawable.star)))
                }
            }
            val lineDataSet0: LineDataSet
            if (lineChart.data != null && lineChart.data.dataSetCount > 0) {
                lineDataSet0 = lineChart.data.getDataSetByIndex(0) as LineDataSet
                lineDataSet0.entries = values
                lineDataSet0.notifyDataSetChanged()
                lineChart.data.notifyDataChanged()
                lineChart.notifyDataSetChanged()
            } else {
                // create a dataset and give it a type
                lineDataSet0 = LineDataSet(values, "DataSet 1")
                lineDataSet0.setDrawIcons(false)

                // draw dashed line
                lineDataSet0.enableDashedLine(10f, 5f, 0f)

                // black lines and points
                lineDataSet0.color = Color.BLACK
                lineDataSet0.setCircleColor(Color.BLACK)

                // line thickness and point size
                lineDataSet0.lineWidth = 1f
                lineDataSet0.circleRadius = 3f

                // draw points as solid circles
                lineDataSet0.setDrawCircleHole(false)

                // customize legend entry
                lineDataSet0.formLineWidth = 1f
                lineDataSet0.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                lineDataSet0.formSize = 15f

                // text size of values
                lineDataSet0.valueTextSize = 9f

                // draw selection line as dashed
                lineDataSet0.enableDashedHighlightLine(10f, 5f, 0f)

                // set the filled area
                lineDataSet0.setDrawFilled(true)
                lineDataSet0.fillFormatter = IFillFormatter { _: ILineDataSet?, _: LineDataProvider? ->
                    lineChart.axisLeft.axisMinimum
                }

                // set color of filled area
                if (Utils.getSDKInt() >= 18) {
                    // drawables only supported on api level 18 and above
                    val drawable = ContextCompat.getDrawable(context, R.drawable.fade_red)
                    lineDataSet0.fillDrawable = drawable
                } else {
                    lineDataSet0.fillColor = Color.BLACK
                }
                val dataSets = ArrayList<ILineDataSet>()
                dataSets.add(lineDataSet0) // add the data sets

                // create a data object with the data sets
                val data = LineData(dataSets)

                // set data
                lineChart.data = data
            }
        }
    }
}
