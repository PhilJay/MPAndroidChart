
package com.github.mikephil.charting.interfaces;

import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

/**
 * Interface that provides everything there is to know about the dimensions,
 * bounds, and range of the chart.
 * 
 * @author Philipp Jahoda
 */
public interface ChartInterface {

//    public float getOffsetBottom();
//
//    public float getOffsetTop();
//
//    public float getOffsetLeft();
//
//    public float getOffsetRight();

    public float getDeltaX();

//    public float getDeltaY();

//    public float getYChartMin();

//    public float getYChartMax();

    public int getWidth();

    public int getHeight();
    
//    public boolean isStartAtZeroEnabled();
    
    public PointF getCenterOfView();
    
    public PointF getCenterOffsets();
    
    public RectF getContentRect();
    
    public View getChartView();
}
