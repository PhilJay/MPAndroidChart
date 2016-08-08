package com.xxmassdeveloper.mpchartexample.custom;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.buffer.ScatterBuffer;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.renderer.scatter.IShapeRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Custom shape renderer that draws a single line.
 * Created by philipp on 26/06/16.
 */
public class CustomScatterShapeRenderer implements IShapeRenderer
{

    @Override
    public void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler, ScatterBuffer buffer, Paint
            renderPaint, float shapeSize) {

        final float shapeHalf = shapeSize / 2f;

        for (int i = 0; i < buffer.size(); i += 2) {

            if (!viewPortHandler.isInBoundsRight(buffer.buffer[i]))
                break;

            if (!viewPortHandler.isInBoundsLeft(buffer.buffer[i])
                    || !viewPortHandler.isInBoundsY(buffer.buffer[i + 1]))
                continue;

            renderPaint.setColor(dataSet.getColor(i / 2));

            c.drawLine(
                    buffer.buffer[i] - shapeHalf,
                    buffer.buffer[i + 1] - shapeHalf,
                    buffer.buffer[i] + shapeHalf,
                    buffer.buffer[i + 1] + shapeHalf,
                    renderPaint);
        }
    }
}
