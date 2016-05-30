package com.github.mikephil.charting.interfaces.dataprovider;

import android.graphics.PointF;
import android.graphics.RectF;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.formatter.ValueFormatter;

/**
 * Interface that provides everything there is to know about the dimensions,
 * bounds, and range of the chart.
 *
 * @author Philipp Jahoda
 */
public interface ChartInterface {

    /**
     * Returns the minimum x-value of the chart, regardless of zoom or translation.
     *
     * @return
     */
    float getXChartMin();

    /**
     * Returns the maximum x-value of the chart, regardless of zoom or translation.
     *
     * @return
     */
    float getXChartMax();

    float getXRange();

    /**
     * Returns the minimum y-value of the chart, regardless of zoom or translation.
     *
     * @return
     */
    float getYChartMin();

    /**
     * Returns the maximum y-value of the chart, regardless of zoom or translation.
     *
     * @return
     */
    float getYChartMax();

    int getXValCount();

    int getWidth();

    int getHeight();

    PointF getCenterOfView();

    PointF getCenterOffsets();

    RectF getContentRect();

    ValueFormatter getDefaultValueFormatter();

    ChartData getData();
}
