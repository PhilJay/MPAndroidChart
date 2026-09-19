package com.github.mikephil.charting.data

import android.graphics.DashPathEffect
import com.github.mikephil.charting.highlight.HighlightLineSpan
import com.github.mikephil.charting.interfaces.datasets.ILineScatterCandleRadarDataSet

/**
 * Base for the data sets whose highlight is drawn as crosshair lines (line, scatter, candle and radar).
 * Holds the settings for those highlight lines.
 *
 * @param T the entry type this set holds.
 */
abstract class LineScatterCandleRadarDataSet<T : Entry<*>>(entries: List<T>, label: String) :
    BarLineScatterCandleBubbleDataSet<T>(entries, label), ILineScatterCandleRadarDataSet<T> {

    override var isVerticalHighlightIndicatorEnabled = true

    override var isHorizontalHighlightIndicatorEnabled = true

    override var verticalHighlightIndicatorSpan = HighlightLineSpan.FULL

    override var horizontalHighlightIndicatorSpan = HighlightLineSpan.FULL

    override var highlightLineWidth = 0.5f

    /** Dash pattern of the highlight lines, or null for solid lines. Set through [enableDashedHighlightLine]. */
    override var dashPathEffectHighlight: DashPathEffect? = null
        protected set

    /** Turns both the vertical and the horizontal highlight line on or off at once. */
    fun setDrawHighlightIndicators(enabled: Boolean) {
        isVerticalHighlightIndicatorEnabled = enabled
        isHorizontalHighlightIndicatorEnabled = enabled
    }

    /**
     * Draws the highlight lines dashed, like "- - - -".
     *
     * @param lineLength length of each dash in px.
     * @param spaceLength length of the gap between dashes in px.
     * @param phase offset into the pattern in px, normally 0.
     */
    fun enableDashedHighlightLine(lineLength: Float, spaceLength: Float, phase: Float) {
        dashPathEffectHighlight = DashPathEffect(floatArrayOf(lineLength, spaceLength), phase)
    }

    /** Draws the highlight lines solid again. */
    fun disableDashedHighlightLine() {
        dashPathEffectHighlight = null
    }

    /** True while a dash pattern is set for the highlight lines. Default false. */
    val isDashedHighlightLineEnabled: Boolean
        get() = dashPathEffectHighlight != null

    /** Copies the styling from this set into [dataSet]; entries are not copied here. */
    protected open fun copy(dataSet: LineScatterCandleRadarDataSet<*>) {
        super.copy(dataSet)
        dataSet.isHorizontalHighlightIndicatorEnabled = isHorizontalHighlightIndicatorEnabled
        dataSet.isVerticalHighlightIndicatorEnabled = isVerticalHighlightIndicatorEnabled
        dataSet.verticalHighlightIndicatorSpan = verticalHighlightIndicatorSpan
        dataSet.horizontalHighlightIndicatorSpan = horizontalHighlightIndicatorSpan
        dataSet.highlightLineWidth = highlightLineWidth
        dataSet.dashPathEffectHighlight = dashPathEffectHighlight
    }
}
