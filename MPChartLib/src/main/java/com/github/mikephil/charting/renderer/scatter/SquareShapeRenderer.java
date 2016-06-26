package com.github.mikephil.charting.renderer.scatter;

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
public class SquareShapeRenderer implements ShapeRenderer {


    @Override
    public void renderShape(
            Canvas c, IScatterDataSet dataSet,
            ViewPortHandler mViewPortHandler, ScatterBuffer buffer, Paint mRenderPaint, final float shapeSize) {

        final float shapeHalf = shapeSize / 2f;
        final float shapeHoleSizeHalf = Utils.convertDpToPixel(dataSet.getScatterShapeHoleRadius());
        final float shapeHoleSize = shapeHoleSizeHalf * 2.f;
        final float shapeStrokeSize = (shapeSize - shapeHoleSize) / 2.f;
        final float shapeStrokeSizeHalf = shapeStrokeSize / 2.f;

        final int shapeHoleColor = dataSet.getScatterShapeHoleColor();

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

                c.drawRect(buffer.buffer[i] - shapeHoleSizeHalf - shapeStrokeSizeHalf,
                        buffer.buffer[i + 1] - shapeHoleSizeHalf - shapeStrokeSizeHalf,
                        buffer.buffer[i] + shapeHoleSizeHalf + shapeStrokeSizeHalf,
                        buffer.buffer[i + 1] + shapeHoleSizeHalf + shapeStrokeSizeHalf,
                        mRenderPaint);

                if (shapeHoleColor != ColorTemplate.COLOR_NONE) {
                    mRenderPaint.setStyle(Paint.Style.FILL);

                    mRenderPaint.setColor(shapeHoleColor);
                    c.drawRect(buffer.buffer[i] - shapeHoleSizeHalf,
                            buffer.buffer[i + 1] - shapeHoleSizeHalf,
                            buffer.buffer[i] + shapeHoleSizeHalf,
                            buffer.buffer[i + 1] + shapeHoleSizeHalf,
                            mRenderPaint);
                }

            } else {
                mRenderPaint.setStyle(Paint.Style.FILL);

                c.drawRect(buffer.buffer[i] - shapeHalf,
                        buffer.buffer[i + 1] - shapeHalf,
                        buffer.buffer[i] + shapeHalf,
                        buffer.buffer[i + 1] + shapeHalf,
                        mRenderPaint);
            }
        }
    }
}
