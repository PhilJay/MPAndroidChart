package com.github.mikephil.charting.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by wajdic on 15/06/2016.
 * Created at Time 09:08
 */
public class SquareShapeRenderer extends ShapeRenderer
{


    @Override
    protected void render(Canvas c, float shapeHoleSizeHalf, float shapeStrokeSizeHalf, Paint renderPaint, float x, float y) {
        drawRect(c, shapeHoleSizeHalf - shapeStrokeSizeHalf,
                shapeHoleSizeHalf + shapeStrokeSizeHalf,
                renderPaint, x, y);
    }

    @Override
    protected void renderHole(Canvas c, float radius, Paint renderPaint, float x, float y) {
        drawRect(c, radius, radius, renderPaint, x, y);
    }

    private void drawRect(Canvas c, float offsetTopLeft, float offsetBottomRight,
                          Paint renderPaint, float x, float y){
        c.drawRect(
                x-offsetTopLeft,
                y-offsetTopLeft,
                x+offsetBottomRight,
                y+offsetBottomRight,
                renderPaint
        );
    }
}
