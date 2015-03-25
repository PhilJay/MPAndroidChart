
package com.github.mikephil.charting.interfaces;

import android.graphics.PointF;
import android.graphics.RectF;

import com.github.mikephil.charting.utils.ValueFormatter;

/**
 * Interface that provides everything there is to know about the dimensions,
 * bounds, and range of the chart.
 * 
 * @author Philipp Jahoda
 */
public interface ChartInterface {

    public float getXChartMin();

    public float getXChartMax();

    public float getYChartMin();

    public float getYChartMax();

    public int getWidth();

    public int getHeight();

    public PointF getCenterOfView();

    public PointF getCenterOffsets();

    public RectF getContentRect();
    
    public ValueFormatter getDefaultValueFormatter();
}
