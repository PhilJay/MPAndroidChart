package com.github.mikephil.charting.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by wajdic on 15/06/2016.
 * Created at Time 09:08
 */
public class CircleShapeRenderer extends ShapeRenderer
{

    @Override
    protected void render(Canvas c, float shapeHoleSizeHalf, float shapeStrokeSizeHalf,
                          Paint renderPaint, float x, float y) {
        renderHole(c, shapeHoleSizeHalf, renderPaint, x, y);
    }

    @Override
    protected void renderHole(Canvas c, float radius, Paint renderPaint, float x, float y) {
        c.drawCircle(x,  y, radius, renderPaint);
    }

}
