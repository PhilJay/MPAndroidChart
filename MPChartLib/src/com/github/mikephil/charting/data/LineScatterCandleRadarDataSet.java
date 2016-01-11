package com.github.mikephil.charting.data;

import android.graphics.DashPathEffect;

import com.github.mikephil.charting.interfaces.datasets.ILineScatterCandleRadarDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;

/**
 * Created by Philipp Jahoda on 11/07/15.
 */
public abstract class LineScatterCandleRadarDataSet<T extends Entry> extends BarLineScatterCandleBubbleDataSet<T> implements ILineScatterCandleRadarDataSet<T> {

    protected boolean mDrawVerticalHighlightIndicator = true;
    protected boolean mDrawHorizontalHighlightIndicator = true;

    /** the width of the highlight indicator lines */
    protected float mHighlightLineWidth = 0.5f;

    /** the path effect for dashed highlight-lines */
    protected DashPathEffect mHighlightDashPathEffect = null;


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

    @Override
    public boolean isVerticalHighlightIndicatorEnabled() {
        return mDrawVerticalHighlightIndicator;
    }

    @Override
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

    @Override
    public float getHighlightLineWidth() {
        return mHighlightLineWidth;
    }

    /**
     * Enables the highlight-line to be drawn in dashed mode, e.g. like this "- - - - - -"
     *
     * @param lineLength the length of the line pieces
     * @param spaceLength the length of space inbetween the line-pieces
     * @param phase offset, in degrees (normally, use 0)
     */
    public void enableDashedHighlightLine(float lineLength, float spaceLength, float phase) {
        mHighlightDashPathEffect = new DashPathEffect(new float[] {
                lineLength, spaceLength
        }, phase);
    }

    /**
     * Disables the highlight-line to be drawn in dashed mode.
     */
    public void disableDashedHighlightLine() {
        mHighlightDashPathEffect = null;
    }

    /**
     * Returns true if the dashed-line effect is enabled for highlight lines, false if not.
     * Default: disabled
     *
     * @return
     */
    public boolean isDashedHighlightLineEnabled() {
        return mHighlightDashPathEffect == null ? false : true;
    }

    @Override
    public DashPathEffect getDashPathEffectHighlight() {
        return mHighlightDashPathEffect;
    }
}
