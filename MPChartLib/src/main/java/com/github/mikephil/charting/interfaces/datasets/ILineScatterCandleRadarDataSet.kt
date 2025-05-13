package com.github.mikephil.charting.interfaces.datasets

import android.graphics.DashPathEffect
import com.github.mikephil.charting.data.Entry

interface ILineScatterCandleRadarDataSet<T : Entry> : IBarLineScatterCandleBubbleDataSet<T> {
    /**
     * Returns true if vertical highlight indicator lines are enabled (drawn)
     * @return
     */
    val isVerticalHighlightIndicatorEnabled: Boolean

    /**
     * Returns true if vertical highlight indicator lines are enabled (drawn)
     * @return
     */
    val isHorizontalHighlightIndicatorEnabled: Boolean

    /**
     * Returns the line-width in which highlight lines are to be drawn.
     * @return
     */
    val highlightLineWidth: Float

    /**
     * Returns the DashPathEffect that is used for highlighting.
     * @return
     */
    val dashPathEffectHighlight: DashPathEffect?
}
