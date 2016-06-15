package com.github.mikephil.charting.renderer.ShapeRenders;

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
public class TriangleShapeRenderer implements ShapeRenderer {


    @Override
    public void renderShape(
            Canvas c, IScatterDataSet dataSet,
            ViewPortHandler mViewPortHandler, ScatterBuffer buffer, Paint mRenderPaint, final int shapeHoleColor, final float shapeSize) {


        final float shapeHalf = shapeSize / 2f;
        final float shapeHoleSizeHalf = Utils.convertDpToPixel(dataSet.getScatterShapeHoleRadius());
        final float shapeHoleSize = shapeHoleSizeHalf * 2.f;
        final float shapeStrokeSize = (shapeSize - shapeHoleSize) / 2.f;


        mRenderPaint.setStyle(Paint.Style.FILL);

        // create a triangle path
        Path tri = new Path();

        for (int i = 0; i < buffer.size(); i += 2) {

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[i]))
                break;

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[i])
                    || !mViewPortHandler.isInBoundsY(buffer.buffer[i + 1]))
                continue;

            mRenderPaint.setColor(dataSet.getColor(i / 2));

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

            c.drawPath(tri, mRenderPaint);
            tri.reset();

            if (shapeSize > 0.0 &&
                    shapeHoleColor != ColorTemplate.COLOR_NONE) {

                mRenderPaint.setColor(shapeHoleColor);

                tri.moveTo(buffer.buffer[i],
                        buffer.buffer[i + 1] - shapeHalf + shapeStrokeSize);
                tri.lineTo(buffer.buffer[i] + shapeHalf - shapeStrokeSize,
                        buffer.buffer[i + 1] + shapeHalf - shapeStrokeSize);
                tri.lineTo(buffer.buffer[i] - shapeHalf + shapeStrokeSize,
                        buffer.buffer[i + 1] + shapeHalf - shapeStrokeSize);
                tri.close();

                c.drawPath(tri, mRenderPaint);
                tri.reset();
            }
        }

    }

}