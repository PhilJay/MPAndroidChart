package com.github.mikephil.charting.jobs;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.view.View;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 19/02/16.
 */
@SuppressLint("NewApi")
public class AnimatedZoomJob extends AnimatedViewPortJob implements Animator.AnimatorListener {

    protected float zoomOriginX;
    protected float zoomOriginY;

    protected float zoomCenterX;
    protected float zoomCenterY;

    protected YAxis yAxis;

    protected float xValCount;

    @SuppressLint("NewApi")
    public AnimatedZoomJob(ViewPortHandler viewPortHandler, View v, Transformer trans, YAxis axis, float xValCount, float scaleX, float scaleY, float xOrigin, float yOrigin, float zoomCenterX, float zoomCenterY, float zoomOriginX, float zoomOriginY, long duration) {
        super(viewPortHandler, scaleX, scaleY, trans, v, xOrigin, yOrigin, duration);

        this.zoomCenterX = zoomCenterX;
        this.zoomCenterY = zoomCenterY;
        this.zoomOriginX = zoomOriginX;
        this.zoomOriginY = zoomOriginY;
        this.animator.addListener(this);
        this.yAxis = axis;
        this.xValCount = xValCount;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

        float scaleX = xOrigin + (xValue - xOrigin) * phase;
        float scaleY = yOrigin + (yValue - yOrigin) * phase;

        Matrix save = mViewPortHandler.setZoom(scaleX, scaleY);
        mViewPortHandler.refresh(save, view, false);

        float valsInView = yAxis.mAxisRange / mViewPortHandler.getScaleY();
        float xsInView =  xValCount / mViewPortHandler.getScaleX();

        pts[0] = zoomOriginX + ((zoomCenterX - xsInView / 2f) - zoomOriginX) * phase;
        pts[1] = zoomOriginY + ((zoomCenterY + valsInView / 2f) - zoomOriginY) * phase;

        mTrans.pointValuesToPixel(pts);

        save = mViewPortHandler.translate(pts);
        mViewPortHandler.refresh(save, view, true);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        ((BarLineChartBase) view).calculateOffsets();
        view.postInvalidate();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationStart(Animator animation) {

    }
}
