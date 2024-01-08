package com.github.mikephil.charting.interfaces.datasets;

import android.graphics.DashPathEffect;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by Philipp Jahoda on 21/10/15.
 * Modified by Gurgen Khachatryan on 08/01/24.
 */
public interface ILineScatterCandleRadarDataSet<T extends Entry> extends IBarLineScatterCandleBubbleDataSet<T> {

    /**
     * Returns true if vertical highlight indicator lines are enabled (drawn)
     * @return
     */
    boolean isVerticalHighlightIndicatorEnabled();

    /**
     * Returns true if vertical highlight indicator lines are enabled (drawn)
     * @return
     */
    boolean isHorizontalHighlightIndicatorEnabled();

    /**
     * Returns true if circle highlight indicator is enabled (drawn)
     * @return
     */
    boolean isCircleHighlightEnabled();

    /**
     * Returns the line-width in which highlight lines are to be drawn.
     * @return
     */
    float getHighlightLineWidth();

    /**
     * Returns the radius of highlight circle.
     * @return
     */
    float getHighlightCircleRadius();

    /**
     * Returns the width of highlight circle border.
     * @return
     */
    float getHighlightCircleBorderWidth();

    /**
     * Returns the DashPathEffect that is used for highlighting.
     * @return
     */
    DashPathEffect getDashPathEffectHighlight();
}
