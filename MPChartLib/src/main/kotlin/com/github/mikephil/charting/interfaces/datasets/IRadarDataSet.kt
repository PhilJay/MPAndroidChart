package com.github.mikephil.charting.interfaces.datasets

import com.github.mikephil.charting.data.RadarEntry

/**
 * What the radar chart renderer reads from a data set: the corner rounding of the polygon and the ring
 * drawn around a highlighted entry.
 * Implemented by [com.github.mikephil.charting.data.RadarDataSet].
 *
 * @param D the type of the payload attached to each entry.
 */
public interface IRadarDataSet<D> : ILineRadarDataSet<RadarEntry<D>> {

    /** Radius in dp the corners of the polygon are rounded with. 0, the default, draws sharp corners. */
    public val cornerRadius: Float

    /** True to draw a ring around a highlighted entry. Default false. */
    public var isDrawHighlightCircleEnabled: Boolean

    /** Fill color of the ring. Default white; [com.github.mikephil.charting.utils.ColorTemplate.COLOR_NONE] draws no fill. */
    public val highlightCircleFillColor: Int

    /** Stroke color of the ring. Default [com.github.mikephil.charting.utils.ColorTemplate.COLOR_NONE], which uses the data set color. */
    public val highlightCircleStrokeColor: Int

    /** Opacity of the ring stroke from 0 to 255. Default 76. */
    public val highlightCircleStrokeAlpha: Int

    /** Inner radius of the ring in dp. Default 3 dp. */
    public val highlightCircleInnerRadius: Float

    /** Outer radius of the ring in dp. Default 4 dp. */
    public val highlightCircleOuterRadius: Float

    /** Stroke width of the ring in dp. Default 2 dp. */
    public val highlightCircleStrokeWidth: Float
}
