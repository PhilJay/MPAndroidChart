package com.xxmassdeveloper.mpchartexample.custom;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.renderer.LegendRenderer.CustomLegendDrawable;

/**
 * Created by keaideluren on 2019/11/25 0025.
 * Email:513421345@qq.com
 */
public class MyCustomLegendDrawable extends CustomLegendDrawable {
    private int color;
    private Paint mPaint;
    private Paint outRingPaint;

    public MyCustomLegendDrawable() {
        this(Color.BLACK);
    }

    public MyCustomLegendDrawable(int color) {
        this.color = color;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        float halfWidth = getIntrinsicWidth() * 0.5F;
        float halfHeight = getIntrinsicWidth() * 0.5F;
        float minHalfSize = Math.min(halfWidth, halfHeight);
        canvas.translate(getBounds().left, getBounds().top - halfHeight);
        canvas.drawCircle(halfWidth, halfHeight, minHalfSize, outRingPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(halfWidth, halfHeight, minHalfSize * 0.6F, mPaint);
        mPaint.setColor(color);
        canvas.drawCircle(halfWidth, halfHeight, minHalfSize * 0.4F, mPaint);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
