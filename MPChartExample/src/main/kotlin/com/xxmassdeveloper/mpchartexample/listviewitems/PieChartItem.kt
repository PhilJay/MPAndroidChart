package com.xxmassdeveloper.mpchartexample.listviewitems

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.xxmassdeveloper.mpchartexample.databinding.ListItemPiechartBinding

class PieChartItem(cd: ChartData<*>, context: Context) : ChartItem(cd) {

    private val tf: Typeface = Typeface.createFromAsset(context.assets, "OpenSans-Regular.ttf")
    private val centerText: SpannableString = generateCenterText()

    override val itemType: Int = TYPE_PIECHART

    override fun getView(position: Int, convertView: View?, context: Context): View {
        val view: View
        val holder: ViewHolder
        if (convertView == null) {
            val binding = ListItemPiechartBinding.inflate(LayoutInflater.from(context))
            view = binding.root
            holder = ViewHolder(binding.chart)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        holder.chart.apply {
            description.isEnabled = false
            holeRadius = 52f
            transparentCircleRadius = 57f
            centerText = this@PieChartItem.centerText
            centerTextTypeface = tf
            centerTextSize = 9f
            isUsePercentValuesEnabled = true
            setExtraOffsets(5f, 10f, 50f, 10f)

            chartData.setValueFormatter(PercentFormatter())
            chartData.setValueTypeface(tf)
            chartData.setValueTextSize(11f)
            chartData.setValueTextColor(Color.WHITE)
            data = chartData as PieData

            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
                isDrawInsideEnabled = false
                yEntrySpace = 0f
                yOffset = 0f
            }

            animateY(900)
        }

        return view
    }

    private fun generateCenterText(): SpannableString {
        val s = SpannableString("MPAndroidChart\ncreated by\nPhilipp Jahoda")
        s.setSpan(RelativeSizeSpan(1.6f), 0, 14, 0)
        s.setSpan(ForegroundColorSpan(ColorTemplate.VORDIPLOM_COLORS[0]), 0, 14, 0)
        s.setSpan(RelativeSizeSpan(.9f), 14, 25, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 14, 25, 0)
        s.setSpan(RelativeSizeSpan(1.4f), 25, s.length, 0)
        s.setSpan(ForegroundColorSpan(ColorTemplate.holoBlue), 25, s.length, 0)
        return s
    }

    private class ViewHolder(val chart: PieChart)
}
