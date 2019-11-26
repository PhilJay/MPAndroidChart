package com.xxmassdeveloper.mpchartexample.custom;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Created by keaideluren on 2019/11/25 0025.
 * Email:513421345@qq.com
 */
public class MyCustomLegendDrawable extends Drawable {
    private int color;
    private Paint mPaint;
    private Paint whitePaint;
    private Paint outRingPaint;

    public MyCustomLegendDrawable() {
        this(Color.BLACK);
    }

    public MyCustomLegendDrawable(int color) {
        this.color = color;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setColor(Color.WHITE);
        outRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setColor(color);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        RadialGradient radialGradientShader = new RadialGradient(
                getIntrinsicWidth() * 0.5F, getIntrinsicHeight() * 0.5F,
                Math.min(getIntrinsicWidth(), getIntrinsicHeight()),
                new int[]{color, color & 0x00111111},
                new float[]{0f, 0.5F},
                Shader.TileMode.REPEAT
        );
        outRingPaint.setShader(radialGradientShader);
    }

    @Override
    public int getIntrinsicWidth() {
        return getBounds().width();
    }

    @Override
    public int getIntrinsicHeight() {
        return getBounds().height();
    }

    public void setColor(int color) {
        this.color = color;
        mPaint.setColor(color);
        outRingPaint.setColor(color);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        float halfWidth = getIntrinsicWidth() * 0.5F;
        float halfHeight = getIntrinsicWidth() * 0.5F;
        float minHalfSize = Math.min(halfWidth, halfHeight);
        canvas.translate(getBounds().left, getBounds().top);
        canvas.drawCircle(halfWidth, halfHeight, minHalfSize, outRingPaint);

        canvas.drawCircle(halfWidth, halfHeight, minHalfSize * 0.6F, whitePaint);

        canvas.drawCircle(halfWidth, halfHeight, minHalfSize * 0.4F, mPaint);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        outRingPaint.setAlpha(alpha);
        mPaint.setAlpha(alpha);
        whitePaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        outRingPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
