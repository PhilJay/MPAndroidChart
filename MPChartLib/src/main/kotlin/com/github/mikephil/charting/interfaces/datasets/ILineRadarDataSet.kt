package com.github.mikephil.charting.interfaces.datasets

import android.graphics.drawable.Drawable
import com.github.mikephil.charting.data.Entry

/**
 * Styling of the line and the filled area below it, shared by line and radar data sets.
 */
public interface ILineRadarDataSet<T : Entry<*>> : ILineScatterCandleRadarDataSet<T> {

    /** Color of the filled area, used when [fillDrawable] is null. */
    public val fillColor: Int

    /** Drawable painted into the filled area instead of [fillColor], null to use the color. */
    public val fillDrawable: Drawable?

    /** Opacity of the filled area from 0 to 255. Default 85. */
    public val fillAlpha: Int

    /** Stroke width of the line in dp. Default 1 dp. */
    public val lineWidth: Float

    /**
     * True to fill the area between the line and the axis. Default false. Filling clips the canvas to a path
     * and costs more than drawing the line alone.
     */
    public var isDrawFilledEnabled: Boolean
}
