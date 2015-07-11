package com.github.mikephil.charting.data;

import com.github.mikephil.charting.utils.Utils;

import java.util.List;

/**
 * Created by philipp on 11/07/15.
 */
public abstract class LineScatterCandleRadarDataSet<T extends Entry> extends BarLineScatterCandleDataSet<T> {

    protected boolean mDrawVerticalHighlightIndicator = true;
    protected boolean mDrawHorizontalHighlightIndicator = true;

    /** the width of the highlight indicator lines */
    protected float mHighlightLineWidth = 0.5f;

    public LineScatterCandleRadarDataSet(List<T> yVals, String label) {
        super(yVals, label);
        mHighlightLineWidth = Utils.convertDpToPixel(0.5f);
    }

    /**
     * Enables / disables the horizontal highlight-indicator. If disabled, the indicator is not drawn.
     * @param enabled
     */
    public void setDrawHorizontalHighlightIndicator(boolean enabled) {
        this.mDrawHorizontalHighlightIndicator = enabled;
    }

    /**
     * Enables / disables the vertical highlight-indicator. If disabled, the indicator is not drawn.
     * @param enabled
     */
    public void setDrawVerticalHighlightIndicator(boolean enabled) {
        this.mDrawVerticalHighlightIndicator = enabled;
    }

    /**
     * Enables / disables both vertical and horizontal highlight-indicators.
     * @param enabled
     */
    public void setDrawHighlightIndicators(boolean enabled) {
        setDrawVerticalHighlightIndicator(enabled);
        setDrawHorizontalHighlightIndicator(enabled);
    }

    public boolean isVerticalHighlightIndicatorEnabled() {
        return mDrawVerticalHighlightIndicator;
    }

    public boolean isHorizontalHighlightIndicatorEnabled() {
        return mDrawHorizontalHighlightIndicator;
    }

    /**
     * Sets the width of the highlight line in dp.
     * @param width
     */
    public void setHighlightLineWidth(float width) {
        mHighlightLineWidth = Utils.convertDpToPixel(width);
    }

    public float getHighlightLineWidth() {
        return mHighlightLineWidth;
    }
}
