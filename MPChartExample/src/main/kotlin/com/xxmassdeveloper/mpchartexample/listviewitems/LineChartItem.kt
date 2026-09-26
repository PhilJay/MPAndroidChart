package com.xxmassdeveloper.mpchartexample.listviewitems

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.LineData
import com.xxmassdeveloper.mpchartexample.databinding.ListItemLinechartBinding

class LineChartItem(cd: ChartData<*>, context: Context) : ChartItem(cd) {

    private val tf: Typeface = Typeface.createFromAsset(context.assets, "OpenSans-Regular.ttf")

    override val itemType: Int = TYPE_LINECHART

    override fun getView(position: Int, convertView: View?, context: Context): View {
        val view: View
        val holder: ViewHolder
        if (convertView == null) {
            val binding = ListItemLinechartBinding.inflate(LayoutInflater.from(context))
            view = binding.root
            holder = ViewHolder(binding.chart)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        holder.chart.apply {
            description.isEnabled = false
            isDrawGridBackgroundEnabled = false

            xAxis.apply {
                this.position = XAxis.XAxisPosition.BOTTOM
                typeface = tf
                isDrawGridLinesEnabled = false
                isDrawAxisLineEnabled = true
            }

            axisLeft.apply {
                typeface = tf
                labelCount = 5
                axisMinimum = 0f
            }

            axisRight.apply {
                typeface = tf
                labelCount = 5
                isDrawGridLinesEnabled = false
                axisMinimum = 0f
            }

            data = chartData as LineData

            animateX(750)
        }

        return view
    }

    private class ViewHolder(val chart: LineChart)
}
