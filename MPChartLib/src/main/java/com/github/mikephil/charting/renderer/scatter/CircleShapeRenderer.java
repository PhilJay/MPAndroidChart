package com.github.mikephil.charting.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by wajdic on 15/06/2016.
 * Created at Time 09:08
 */
public class CircleShapeRenderer implements IShapeRenderer
{

    @Override
    public void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler,
                            float posX, float posY, Paint renderPaint) {

        final Shape shape = new Shape(dataSet);
        /**
         final float shapeSize = dataSet.getScatterShapeSize();
         final float shapeHalf = shapeSize / 2f;
         final float shapeHoleSizeHalf = Utils.convertDpToPixel(dataSet.getScatterShapeHoleRadius());
         final float shapeHoleSize = shapeHoleSizeHalf * 2.f;
         final float shapeStrokeSize = (shapeSize - shapeHoleSize) / 2.f;
         final float shapeStrokeSizeHalf = shapeStrokeSize / 2.f;

         final int shapeHoleColor = dataSet.getScatterShapeHoleColor();
         **/
        if (shape.getShapeSize() > 0.0) {
            renderPaint.setStyle(Paint.Style.STROKE);
            renderPaint.setStrokeWidth(shape.getShapeStrokeSize());

            c.drawCircle(
                    posX,
                    posY,
                    shape.getShapeHoleSizeHalf() + shape.getShapeStrokeSizeHalf(),
                    renderPaint);

            if (shape.getShapeHoleColor() != ColorTemplate.COLOR_NONE) {
                renderPaint.setStyle(Paint.Style.FILL);

                renderPaint.setColor(shape.getShapeHoleColor());
                c.drawCircle(
                        posX,
                        posY,
                        shape.getShapeHoleSizeHalf(),
                        renderPaint);
            }
        } else {
            renderPaint.setStyle(Paint.Style.FILL);

            c.drawCircle(
                    posX,
                    posY,
                    shape.getShapeHalf(),
                    renderPaint);
        }

    }

}
