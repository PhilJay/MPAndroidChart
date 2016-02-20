package com.github.mikephil.charting.jobs;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.View;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 19/02/16.
 */
@SuppressLint("NewApi")
public class AnimatedZoomJob extends AnimatedJob implements Animator.AnimatorListener {

    protected float zoomOriginX;
    protected float zoomOriginY;

    protected float zoomCenterX;
    protected float zoomCenterY;

    @SuppressLint("NewApi")
    public AnimatedZoomJob(ViewPortHandler viewPortHandler, View v, Transformer trans, float scaleX, float scaleY, float xOrigin, float yOrigin, float zoomCenterX, float zoomCenterY, float zoomOriginX, float zoomOriginY, long duration) {
        super(viewPortHandler, scaleX, scaleY, trans, v, xOrigin, yOrigin, duration);

        this.zoomCenterX = zoomCenterX;
        this.zoomCenterY = zoomCenterY;
        this.zoomOriginX = zoomOriginX;
        this.zoomOriginY = zoomOriginY;
        this.animator.addListener(this);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

        float scaleX = xOrigin + (xValue - xOrigin) * phase;
        float scaleY = yOrigin + (yValue - yOrigin) * phase;

        pts[0] = zoomOriginX + (zoomCenterX - zoomOriginX) * phase;
        pts[1] = zoomOriginY + (zoomCenterY - zoomOriginY) * phase;

        mTrans.pointValuesToPixel(pts);

        //Matrix save = mViewPortHandler.zoomAndCenter(xValue * phase, yValue * phase, zoomCenterX * phase, zoomCenterY * phase);

       mViewPortHandler.zoomAndCenter(scaleX, scaleY, pts, view);//zoomOriginX + (zoomCenterX - zoomOriginX) * phase, zoomOriginY + (zoomCenterY - zoomOriginY) * phase);
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
