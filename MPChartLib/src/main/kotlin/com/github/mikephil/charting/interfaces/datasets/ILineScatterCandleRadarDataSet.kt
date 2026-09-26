package com.github.mikephil.charting.interfaces.datasets

import android.graphics.DashPathEffect
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.HighlightLineSpan

/**
 * Styling of the highlight indicator lines shared by line, scatter, candle and radar data sets.
 */
public interface ILineScatterCandleRadarDataSet<T : Entry<*>> : IBarLineScatterCandleBubbleDataSet<T> {

    /** True to draw a vertical line through a highlighted entry. Default true. */
    public val isVerticalHighlightIndicatorEnabled: Boolean

    /** True to draw a horizontal line through a highlighted entry. Default true. */
    public val isHorizontalHighlightIndicatorEnabled: Boolean

    /** How far the vertical highlight line reaches. Default [HighlightLineSpan.FULL]. */
    public val verticalHighlightIndicatorSpan: HighlightLineSpan

    /** How far the horizontal highlight line reaches. Default [HighlightLineSpan.FULL]. */
    public val horizontalHighlightIndicatorSpan: HighlightLineSpan

    /** Width of the highlight indicator lines in dp. Default 0.5 dp. */
    public val highlightLineWidth: Float

    /** Dash effect of the highlight indicator lines, null for solid lines. */
    public val dashPathEffectHighlight: DashPathEffect?
}
