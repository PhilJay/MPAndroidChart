package com.github.mikephil.charting.interfaces.datasets;

import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.model.GradientColor;

import java.util.List;

/**
 * Created by Philipp Jahoda on 03/11/15.
 */
public interface IRadarDataSet extends ILineRadarDataSet<RadarEntry> {

    /// flag indicating whether highlight circle should be drawn or not
    boolean isDrawHighlightCircleEnabled();

    /// Sets whether highlight circle should be drawn or not
    void setDrawHighlightCircleEnabled(boolean enabled);

    int getHighlightCircleFillColor();

    /// The stroke color for highlight circle.
    /// If Utils.COLOR_NONE, the color of the dataset is taken.
    int getHighlightCircleStrokeColor();

    int getHighlightCircleStrokeAlpha();

    float getHighlightCircleInnerRadius();

    float getHighlightCircleOuterRadius();

    float getHighlightCircleStrokeWidth();

    /**
     * Returns the Gradient color model
     *
     * @return
     *
     * @deprecated use {@link #getGradientColor(int)} or {@link #getGradientColors()} instead
     */
    GradientColor getGradientColor();

    /**
     * Returns the Gradient colors
     *
     * @return
     */
    List<GradientColor> getGradientColors();

    /**
     * Returns the Gradient colors
     *
     * @param index
     * @return
     */
    GradientColor getGradientColor(int index);

    /**
     * Check if we should use gradient colors for drawing this data set
     *
     * Normally it is a good idea to have this check before invoking
     * {@link #getGradientColor()}, {@link #getGradientColor(int)} or
     * {@link #getGradientColors()}
     *
     * @return {@code true} iff we have gradient colors available for this
     *         data set, {@code false} otherwise
     */
    boolean isGradientEnabled();
}
