package com.xxmassdeveloper.mpchartexample.custom;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.renderer.scatter.IShapeRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Custom shape renderer that draws a single line.
 * Created by philipp on 26/06/16.
 */
public class CustomScatterShapeRenderer implements IShapeRenderer {

    @Override
    public void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler,
                            float posX, float posY, Paint renderPaint, Entry e) {

        final float shapeHalf = dataSet.getScatterShapeSize() / 2f;

        // example of using the entry to modify displayed data. Could be more pushed, for example we could draw the Y value directly on the chart, or draw a specific icon (like a weather icon)
        /*
        if (e.getY() < 50) {
            renderPaint.setColor(Color.BLUE);
        } else {
            renderPaint.setColor(Color.RED);
        }
        */

        c.drawLine(
                posX - shapeHalf,
                posY - shapeHalf,
                posX + shapeHalf,
                posY + shapeHalf,
                renderPaint);
    }
}
