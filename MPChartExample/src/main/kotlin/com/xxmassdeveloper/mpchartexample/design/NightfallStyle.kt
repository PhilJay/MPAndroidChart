package com.xxmassdeveloper.mpchartexample.design

import android.graphics.drawable.GradientDrawable
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.Fill

/** Applies the Nightfall look to any chart: no description, no legend, no axis lines, faint horizontal grid, muted labels. */
fun Chart<*>.nightfallBase(theme: Nightfall) {
    description.isEnabled = false
    legend.isEnabled = false
    noDataTextColor = theme.muted
    if (this is BarLineChartBase<*>) {
        isScaleEnabled = false
        isDragEnabled = false
        isDoubleTapToZoomEnabled = false
        setExtraOffsets(0f, 8f, 8f, 4f)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.nightfallAxis(theme)
        xAxis.isDrawGridLinesEnabled = false
        xAxis.yOffset = 8f
        axisLeft.nightfallAxis(theme)
        axisLeft.isDrawGridLinesEnabled = true
        axisLeft.gridColor = theme.grid
        axisLeft.gridLineWidth = 1f
        axisLeft.xOffset = 10f
        axisRight.isEnabled = false
    }
}

fun AxisBase.nightfallAxis(theme: Nightfall) {
    isDrawAxisLineEnabled = false
    textColor = theme.muted
    textSize = 10f
}

/**
 * Spreads the entries over the full plot width, so the line touches the left and right edge.
 * The first and last labels are pulled inside the chart so they are not cut off.
 */
fun BarLineChartBase<*>.nightfallFullWidthX(pointCount: Int) {
    xAxis.axisMinimum = 0f
    xAxis.axisMaximum = (pointCount - 1).toFloat()
    xAxis.granularity = 1f
    xAxis.labelCount = pointCount
    xAxis.isAvoidFirstLastClippingEnabled = true
    setExtraOffsets(6f, 8f, 6f, 4f)
}

/** Leaves half a slot of space before the first and after the last entry, the way bar charts sit. */
fun BarLineChartBase<*>.nightfallPaddedX(pointCount: Int) {
    xAxis.axisMinimum = -0.5f
    xAxis.axisMaximum = pointCount - 0.5f
    xAxis.granularity = 1f
    xAxis.labelCount = pointCount
}

/** Smooth line with a fading area fill, a dashed guide and a ring on the highlighted entry. */
fun LineDataSet<*>.nightfallLine(theme: Nightfall, lineColor: Int = Nightfall.accent) {
    mode = LineDataSet.Mode.CUBIC_BEZIER
    cubicIntensity = 0.18f
    color = lineColor
    lineWidth = 3f
    isDrawCirclesEnabled = false
    isDrawValuesEnabled = false
    isDrawFilledEnabled = true
    fillDrawable = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(Nightfall.withAlpha(lineColor, 115), Nightfall.withAlpha(lineColor, 0))
    )
    highlightColor = lineColor
    highlightLineWidth = 1f
    enableDashedHighlightLine(8f, 8f, 0f)
    isHorizontalHighlightIndicatorEnabled = false
    isDrawHighlightCircleEnabled = true
    circleHoleColor = theme.card
}

/** Rounded bars with a vertical gradient per bar; [selected] gets the secondary color. */
fun BarDataSet<*>.nightfallBars(barColor: Int = Nightfall.accent, selected: Int = -1, selectedColor: Int = Nightfall.violet) {
    barCornerRadius = 7f
    isDrawValuesEnabled = false
    highlightAlpha = 0
    fills = entries.indices.map { i ->
        val color = if (i == selected) selectedColor else barColor
        Fill(color, Nightfall.withAlpha(color, 90))
    }.toMutableList()
}

/** Donut with rounded slice ends, no labels on the slices and a legend on the right. */
fun PieChart.nightfallDonut(theme: Nightfall) {
    nightfallBase(theme)
    isDrawEntryLabelsEnabled = false
    holeRadius = 72f
    transparentCircleRadius = 0f
    holeColor = theme.card
    isDrawRoundedSlicesEnabled = true
    isRotationEnabled = false
    centerTextColor = theme.text
    centerTextSize = 11f
    legend.apply {
        isEnabled = true
        verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        orientation = Legend.LegendOrientation.VERTICAL
        form = Legend.LegendForm.SQUARE
        formSize = 10f
        textColor = theme.text
        textSize = 12f
        yEntrySpace = 8f
        xEntrySpace = 8f
    }
    setExtraOffsets(0f, 0f, 24f, 0f)
}

fun PieDataSet<*>.nightfallSlices() {
    sliceSpace = 3f
    colors = Nightfall.palette.take(entryCount)
    isDrawValuesEnabled = false
}
