package com.github.mikephil.charting.interfaces.datasets

import android.graphics.DashPathEffect
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter

/**
 * What the line chart renderer reads from a data set: line mode, circles at the entries, dashing and fill.
 * Implemented by [LineDataSet].
 *
 * @param D the type of the payload attached to each entry.
 */
public interface ILineDataSet<D> : ILineRadarDataSet<Entry<D>> {

    /** How the entries are connected: straight, stepped, cubic or horizontal cubic. Default [LineDataSet.Mode.LINEAR]. */
    public val mode: LineDataSet.Mode

    /** Curvature of cubic lines from 0.05 (nearly straight) to 1 (very round). Default 0.2. */
    public val cubicIntensity: Float

    /** Radius of the circles drawn at the entries in dp. Default 4 dp. */
    public val circleRadius: Float

    /** Radius of the hole inside each circle in dp. Default 2 dp. */
    public val circleHoleRadius: Float

    /** Returns the circle color for entry [index], wrapping around the circle colors with modulo. */
    public fun getCircleColor(index: Int): Int

    /** Number of circle colors set. */
    public val circleColorCount: Int

    /** True to draw a circle at every entry. Default true. */
    public val isDrawCirclesEnabled: Boolean

    /** Color of the hole inside the circles. Default white; [com.github.mikephil.charting.utils.ColorTemplate.COLOR_NONE] leaves the hole transparent. */
    public val circleHoleColor: Int

    /** True to draw the hole inside the circles. Default true. */
    public val isDrawCircleHoleEnabled: Boolean

    /** Dash effect of the line, null for a solid line. */
    public val dashPathEffect: DashPathEffect?

    /** True if [dashPathEffect] is set, so the line is drawn dashed. */
    public val isDashedLineEnabled: Boolean

    /** True to draw a ring around the highlighted entry. Default false. */
    public val isDrawHighlightCircleEnabled: Boolean

    /** Radius of the highlight ring in dp. Default 5. The ring uses the line color, the halo behind it the highlight color. */
    public val highlightCircleRadius: Float

    /** Decides the y value to which the filled area extends. Default fills to 0 when the values cross zero, otherwise to the nearer axis end. */
    public val fillFormatter: IFillFormatter
}
