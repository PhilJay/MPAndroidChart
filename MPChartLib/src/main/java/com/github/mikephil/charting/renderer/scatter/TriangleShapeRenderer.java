package com.github.mikephil.charting.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.github.mikephil.charting.buffer.ScatterBuffer;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by wajdic on 15/06/2016.
 * Created at Time 09:08
 */
public class TriangleShapeRenderer implements IShapeRenderer
{

    protected Path mTrianglePathBuffer = new Path();

    @Override
    public void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler, ScatterBuffer buffer, Paint
            renderPaint, final float shapeSize) {

        final float shapeHalf = shapeSize / 2f;
        final float shapeHoleSizeHalf = Utils.convertDpToPixel(dataSet.getScatterShapeHoleRadius());
        final float shapeHoleSize = shapeHoleSizeHalf * 2.f;
        final float shapeStrokeSize = (shapeSize - shapeHoleSize) / 2.f;

        final int shapeHoleColor = dataSet.getScatterShapeHoleColor();

        renderPaint.setStyle(Paint.Style.FILL);

        // create a triangle path
        Path tri = mTrianglePathBuffer;
        tri.reset();

        for (int i = 0; i < buffer.size(); i += 2) {

            if (!viewPortHandler.isInBoundsRight(buffer.buffer[i]))
                break;

            if (!viewPortHandler.isInBoundsLeft(buffer.buffer[i])
                    || !viewPortHandler.isInBoundsY(buffer.buffer[i + 1]))
                continue;

            renderPaint.setColor(dataSet.getColor(i / 2));

            tri.moveTo(buffer.buffer[i], buffer.buffer[i + 1] - shapeHalf);
            tri.lineTo(buffer.buffer[i] + shapeHalf, buffer.buffer[i + 1] + shapeHalf);
            tri.lineTo(buffer.buffer[i] - shapeHalf, buffer.buffer[i + 1] + shapeHalf);

            if (shapeSize > 0.0) {
                tri.lineTo(buffer.buffer[i], buffer.buffer[i + 1] - shapeHalf);

                tri.moveTo(buffer.buffer[i] - shapeHalf + shapeStrokeSize,
                        buffer.buffer[i + 1] + shapeHalf - shapeStrokeSize);
                tri.lineTo(buffer.buffer[i] + shapeHalf - shapeStrokeSize,
                        buffer.buffer[i + 1] + shapeHalf - shapeStrokeSize);
                tri.lineTo(buffer.buffer[i],
                        buffer.buffer[i + 1] - shapeHalf + shapeStrokeSize);
                tri.lineTo(buffer.buffer[i] - shapeHalf + shapeStrokeSize,
                        buffer.buffer[i + 1] + shapeHalf - shapeStrokeSize);
            }

            tri.close();

            c.drawPath(tri, renderPaint);
            tri.reset();

            if (shapeSize > 0.0 &&
                    shapeHoleColor != ColorTemplate.COLOR_NONE) {

                renderPaint.setColor(shapeHoleColor);

                tri.moveTo(buffer.buffer[i],
                        buffer.buffer[i + 1] - shapeHalf + shapeStrokeSize);
                tri.lineTo(buffer.buffer[i] + shapeHalf - shapeStrokeSize,
                        buffer.buffer[i + 1] + shapeHalf - shapeStrokeSize);
                tri.lineTo(buffer.buffer[i] - shapeHalf + shapeStrokeSize,
                        buffer.buffer[i + 1] + shapeHalf - shapeStrokeSize);
                tri.close();

                c.drawPath(tri, renderPaint);
                tri.reset();
            }
        }

    }

}