package com.github.mikephil.charting.interfaces.dataprovider

import android.graphics.RectF
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.MPPointF

/**
 * What every chart exposes to its renderers, highlighters and markers: value ranges, layout in pixels
 * and the data. Implemented by [com.github.mikephil.charting.charts.Chart].
 */
public interface ChartInterface {

    /** Smallest x value of the x axis, independent of zoom and drag. */
    public val xChartMin: Float

    /** Largest x value of the x axis, independent of zoom and drag. */
    public val xChartMax: Float

    /** Difference between [xChartMax] and [xChartMin]. */
    public val xRange: Float

    /** Smallest y value across all y axes, independent of zoom and drag. */
    public val yChartMin: Float

    /** Largest y value across all y axes, independent of zoom and drag. */
    public val yChartMax: Float

    /** Maximum distance in dp between a touch and an entry for the entry to be highlighted. */
    public val maxHighlightDistance: Float

    /** Center of the whole chart view in pixels, as a fresh [MPPointF]. */
    public val centerOfView: MPPointF

    /** Center of the content rectangle in pixels, as a fresh [MPPointF]. */
    public val centerOffsets: MPPointF

    /** Rectangle in pixels in which the data is drawn, the view minus axis, legend and extra offsets. */
    public val contentRect: RectF

    /** Formatter the chart computed from its data, used by data sets that have no formatter of their own. */
    public val defaultValueFormatter: IValueFormatter

    /** The data shown in the chart, null before data is set. */
    public val data: ChartData<*>?

    /**
     * Number of entries one data set may have inside the visible x range for its values and icons to be drawn.
     * A set with more entries in view draws none, because the labels would sit too close together to read.
     */
    public val maxVisibleCount: Int
}
