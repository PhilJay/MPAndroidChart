package com.github.mikephil.charting.interfaces.dataprovider

import android.graphics.RectF
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.MPPointF

/**
 * What every chart exposes to its renderers, highlighters and markers: value ranges, layout in pixels
 * and the data. Implemented by [com.github.mikephil.charting.charts.Chart].
 */
interface ChartInterface {

    /** Smallest x value of the x axis, independent of zoom and drag. */
    val xChartMin: Float

    /** Largest x value of the x axis, independent of zoom and drag. */
    val xChartMax: Float

    /** Difference between [xChartMax] and [xChartMin]. */
    val xRange: Float

    /** Smallest y value across all y axes, independent of zoom and drag. */
    val yChartMin: Float

    /** Largest y value across all y axes, independent of zoom and drag. */
    val yChartMax: Float

    /** Maximum distance in dp between a touch and an entry for the entry to be highlighted. */
    val maxHighlightDistance: Float

    /** Center of the whole chart view in pixels, as a fresh [MPPointF]. */
    val centerOfView: MPPointF

    /** Center of the content rectangle in pixels, as a fresh [MPPointF]. */
    val centerOffsets: MPPointF

    /** Rectangle in pixels in which the data is drawn, the view minus axis, legend and extra offsets. */
    val contentRect: RectF

    /** Formatter the chart computed from its data, used by data sets that have no formatter of their own. */
    val defaultValueFormatter: IValueFormatter

    /** The data shown in the chart, null before data is set. */
    val data: ChartData<*>?

    /**
     * Number of entries one data set may have inside the visible x range for its values and icons to be drawn.
     * A set with more entries in view draws none, because the labels would sit too close together to read.
     */
    val maxVisibleCount: Int
}
