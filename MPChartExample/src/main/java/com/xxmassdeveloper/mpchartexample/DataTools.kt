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

        private val VAL_102 = arrayOf(
            0.31704906,
            0.11076527,
            0.67421365,
            0.30298668,
            0.37076905,
            0.21838248,
            0.7565924,
            0.8588904,
            0.33838162,
            0.63905376,
            0.052202724,
            0.3856282,
            0.73625267,
            0.007399183,
            0.5608851,
            0.8627598,
            0.331517,
            0.4500423,
            0.7748036,
            0.09964817,
            0.96290344,
            0.7727165,
            0.50386846,
            0.1740686,
            0.6228984,
            0.113764636,
            0.63975865,
            0.23871686,
            0.98804605,
            0.88095695,
            0.963798,
            0.20052797,
            0.9914091,
            0.047342073,
            0.7767942,
            0.92713374,
            0.21969187,
            0.17188361,
            0.08304611,
            0.31545073,
            0.8872535,
            0.8319247,
            0.29608163,
            0.7693036,
            0.8902154,
            0.5070619,
            0.95028555,
            0.376154,
            0.9760939,
            0.33779907,
            0.7004318,
            0.34741813,
            0.37786564,
            0.59739006,
            0.9702337,
            0.09252104,
            0.35609993,
            0.16156897,
            0.97137845,
            0.6337493,
            0.9687681,
            0.738406,
            0.456114,
            0.9371279,
            0.045567524,
            0.12931462,
            0.48930678,
            0.13396108,
            0.78207386,
            0.3322763,
            0.7299913,
            0.53834146,
            0.58876187,
            0.86963767,
            0.19778427,
            0.827845,
            0.83091146,
            0.3253732,
            0.26613656,
            0.7477944,
            0.8776427,
            0.7104973,
            0.6722489,
            0.2238866,
            0.441799,
            0.9403467,
            0.6625779,
            0.8236447,
            0.53712654,
            0.40374467,
            0.0032116973,
            0.09406879,
            0.5051711,
            0.33159724,
            0.47599393,
            0.42102405,
            0.47154334,
            0.43126026,
            0.65412974,
            0.7053179,
            0.77,
            0.69
        )

        private val VAL_FIX = arrayOf(
            94.84043, -19.610321, 34.980606, 137.20502, 3.3113098, 18.506958,
            72.16055, 36.291832, 135.97142, 122.0381, 85.873055, 68.582016, -13.099461, 85.85466,
            85.45009, 97.90119, 132.87332, 125.11917, 147.57323, -2.4140186, 28.75475, -17.208168,
            70.36055, 10.715485, 53.00132, 49.197624, 48.25173, 117.765854, 15.96653, 114.79433,
            127.782455, 14.049572, 44.2753, 106.75516, 108.96782, 75.33596, 128.22353, -12.423063,
            44.768654, -25.790316, 5.9754066, 99.64748, 141.99321, -17.990795, 38.272446
        )

        fun getValues(size: Int) = VAL_102.copyOf(size)

        fun getMuchValues(size: Int): Array<Double?> {
            var result = VAL_102.copyOf(VAL_102.size)
            while (result.size < size)
                result = result.plus(VAL_102.copyOf(VAL_102.size))
            return result
        }

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
                lineDataSet0.fillFormatter = object : IFillFormatter {
                    override fun getFillLinePosition(dataSet: ILineDataSet?, dataProvider: LineDataProvider?): Float {
                        return lineChart.axisLeft.axisMinimum
                    }
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
