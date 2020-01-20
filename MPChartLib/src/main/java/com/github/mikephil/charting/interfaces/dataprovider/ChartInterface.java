package com.github.mikephil.charting.interfaces.dataprovider;

import android.graphics.RectF;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.MPPointF;

import androidx.annotation.Nullable;

/**
 * Interface that provides everything there is to know about the dimensions,
 * bounds, and range of the chart.
 *
 * @author Philipp Jahoda
 */
public interface ChartInterface {

    /**
     * Returns the minimum x value of the chart, regardless of zoom or translation.
     *
     * @return
     */
    float getXChartMin();

    /**
     * Returns the maximum x value of the chart, regardless of zoom or translation.
     *
     * @return
     */
    float getXChartMax();

    float getXRange();

    /**
     * Returns the minimum y value of the chart, regardless of zoom or translation.
     *
     * @return
     */
    float getYChartMin();

    /**
     * Returns the maximum y value of the chart, regardless of zoom or translation.
     *
     * @return
     */
    float getYChartMax();

    /**
     * Returns the maximum distance in scren dp a touch can be away from an entry to cause it to get highlighted.
     *
     * @return
     */
    float getMaxHighlightDistance();

    int getWidth();

    int getHeight();

    MPPointF getCenterOfView();

    MPPointF getCenterOffsets();

    RectF getContentRect();

    ValueFormatter getDefaultValueFormatter();

    ChartData getData();

    int getMaxVisibleCount();

    // ####### make it obvious what charts have which axes #######

    /**
     * Returns true if the chart has an x axis.
     *
     * @return has x axis
     */
    boolean hasXAxis();

    /**
     * Returns the x axis. Do not call until after checking hasXAxis().
     * @return x axis
     */
    @Nullable
    XAxis getXAxis();


    /**
     * Returns true if the chart has a left axis.
     *
     * @return has left axis
     */
    boolean hasLeftAxis();

    /**
     * Returns the left axis, or null if there is not one.
     * Do not call before checking hasLeftAxis, or you may get a run time exception.
     *
     * @return left axis or null
     */
    @Nullable
    YAxis getLeftAxis();

    /**
     * Returns true if the chart has a right axis.
     *
     * @return has right axis
     */
    boolean hasRightAxis();

    /**
     * Returns the right axis, or null if there is not one.
     * Do not call before checking hasRightAxis, or you may get a run time exception.
     *
     * @return right axis or null
     */
    @Nullable YAxis getRightAxis();

    /**
     * Clears highlights associated with the data values.
     */
    void clearValueHighlights();

    /**
     * Clears all highlights from data values and axes.
     */
    void clearAllHighlights();
}
