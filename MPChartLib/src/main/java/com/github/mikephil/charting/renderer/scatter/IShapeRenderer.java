package com.github.mikephil.charting.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.buffer.ScatterBuffer;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by wajdic on 15/06/2016.
 * Created at Time 09:07
 */
public interface IShapeRenderer
{

    /**
     * Renders the provided ScatterDataSet with a shape.
     *
     * @param c               Canvas object for drawing the shape
     * @param dataSet         the DataSet to be drawn
     * @param viewPortHandler contains information about the current state of the view
     * @param buffer          buffer containing the transformed values of all entries in the DataSet
     * @param renderPaint     Paint object used for styling and drawing
     * @param shapeSize
     */
    void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler, ScatterBuffer buffer, Paint
            renderPaint, final float shapeSize);
}
