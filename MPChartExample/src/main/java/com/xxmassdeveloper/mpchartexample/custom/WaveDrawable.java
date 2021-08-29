package com.xxmassdeveloper.mpchartexample.custom;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by keaideluren on 2019/11/26 0026.
 * Email:513421345@qq.com
 * wave drawable
 */
public class WaveDrawable extends Drawable {
    private Paint mWavePaint = new Paint();
    private Path mWavePath = new Path();

    public WaveDrawable() {
        mWavePaint.setAntiAlias(true);
        mWavePaint.setStyle(Paint.Style.STROKE);
        mWavePaint.setStrokeWidth(2);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int width = getIntrinsicWidth();
        int height = getIntrinsicHeight();
        int stepWidth = width / 8;
        canvas.translate(getBounds().left, getBounds().top + height / 2);

        mWavePath.moveTo(0, 0);
        mWavePath.quadTo(stepWidth, -height / 4, stepWidth * 2, 0);
        mWavePath.quadTo(stepWidth * 3, height / 4, stepWidth * 4, 0);
        mWavePath.quadTo(stepWidth * 5, -height / 4, stepWidth * 6, 0);
        mWavePath.quadTo(stepWidth * 7, height / 4, stepWidth * 8, 0);

        canvas.drawPath(mWavePath, mWavePaint);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        mWavePaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mWavePaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public int getIntrinsicWidth() {
        return getBounds().width();
    }

    @Override
    public int getIntrinsicHeight() {
        return getBounds().height();
    }
}
