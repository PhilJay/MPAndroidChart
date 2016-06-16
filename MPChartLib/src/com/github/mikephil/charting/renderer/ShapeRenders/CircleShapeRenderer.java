package com.github.mikephil.charting.renderer.ShapeRenders;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.buffer.ScatterBuffer;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by wajdic on 15/06/2016.
 * Created at Time 09:08
 */
public class CircleShapeRenderer implements ShapeRenderer {


    @Override
    public void renderShape(
            Canvas c, IScatterDataSet dataSet,
            ViewPortHandler mViewPortHandler, ScatterBuffer buffer, Paint mRenderPaint, final int shapeHoleColor, final float shapeSize) {

        final float shapeHalf = shapeSize / 2f;
        final float shapeHoleSizeHalf = Utils.convertDpToPixel(dataSet.getScatterShapeHoleRadius());
        final float shapeHoleSize = shapeHoleSizeHalf * 2.f;
        final float shapeStrokeSize = (shapeSize - shapeHoleSize) / 2.f;
        final float shapeStrokeSizeHalf = shapeStrokeSize / 2.f;


        for (int i = 0; i < buffer.size(); i += 2) {

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[i]))
                break;

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[i])
                    || !mViewPortHandler.isInBoundsY(buffer.buffer[i + 1]))
                continue;

            mRenderPaint.setColor(dataSet.getColor(i / 2));

            if (shapeSize > 0.0) {
                mRenderPaint.setStyle(Paint.Style.STROKE);
                mRenderPaint.setStrokeWidth(shapeStrokeSize);

                c.drawCircle(
                        buffer.buffer[i],
                        buffer.buffer[i + 1],
                        shapeHoleSizeHalf + shapeStrokeSizeHalf,
                        mRenderPaint);

                if (shapeHoleColor != ColorTemplate.COLOR_NONE) {
                    mRenderPaint.setStyle(Paint.Style.FILL);

                    mRenderPaint.setColor(shapeHoleColor);
                    c.drawCircle(
                            buffer.buffer[i],
                            buffer.buffer[i + 1],
                            shapeHoleSizeHalf,
                            mRenderPaint);
                }
            } else {
                mRenderPaint.setStyle(Paint.Style.FILL);

                c.drawCircle(
                        buffer.buffer[i],
                        buffer.buffer[i + 1],
                        shapeHalf,
                        mRenderPaint);
            }
        }

    }

}
