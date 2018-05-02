package com.github.mikephil.charting.jobs;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.view.View;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.ObjectPool;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 19/02/16.
 */
@SuppressLint("NewApi")
public class AnimatedZoomJob extends AnimatedViewPortJob implements Animator.AnimatorListener {

    private static ObjectPool<AnimatedZoomJob> pool;

    static {
        pool = ObjectPool.create(8, new AnimatedZoomJob(null,null,null,null,0,0,0,0,0,0,0,0,0,0));
    }

    public static AnimatedZoomJob getInstance(ViewPortHandler viewPortHandler, View v, Transformer trans, YAxis axis, float xAxisRange, float scaleX, float scaleY, float xOrigin, float yOrigin, float zoomCenterX, float zoomCenterY, float zoomOriginX, float zoomOriginY, long duration) {
        AnimatedZoomJob result = pool.get();
        result.mViewPortHandler = viewPortHandler;
        result.xValue = scaleX;
        result.yValue = scaleY;
        result.mTrans = trans;
        result.view = v;
        result.xOrigin = xOrigin;
        result.yOrigin = yOrigin;
        result.yAxis = axis;
        result.xAxisRange = xAxisRange;
        result.resetAnimator();
        result.animator.setDuration(duration);
        return result;
    }

    protected float zoomOriginX;
    protected float zoomOriginY;

    protected float zoomCenterX;
    protected float zoomCenterY;

    protected YAxis yAxis;

    protected float xAxisRange;

    @SuppressLint("NewApi")
    public AnimatedZoomJob(ViewPortHandler viewPortHandler, View v, Transformer trans, YAxis axis, float xAxisRange, float scaleX, float scaleY, float xOrigin, float yOrigin, float zoomCenterX, float zoomCenterY, float zoomOriginX, float zoomOriginY, long duration) {
        super(viewPortHandler, scaleX, scaleY, trans, v, xOrigin, yOrigin, duration);

        this.zoomCenterX = zoomCenterX;
        this.zoomCenterY = zoomCenterY;
        this.zoomOriginX = zoomOriginX;
        this.zoomOriginY = zoomOriginY;
        this.animator.addListener(this);
        this.yAxis = axis;
        this.xAxisRange = xAxisRange;
    }

    protected Matrix mOnAnimationUpdateMatrixBuffer = new Matrix();
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

        float scaleX = xOrigin + (xValue - xOrigin) * phase;
        float scaleY = yOrigin + (yValue - yOrigin) * phase;

        Matrix save = mOnAnimationUpdateMatrixBuffer;
        mViewPortHandler.setZoom(scaleX, scaleY, save);
        mViewPortHandler.refresh(save, view, false);

        float valsInView = yAxis.mAxisRange / mViewPortHandler.getScaleY();
        float xsInView =  xAxisRange / mViewPortHandler.getScaleX();

        pts[0] = zoomOriginX + ((zoomCenterX - xsInView / 2f) - zoomOriginX) * phase;
        pts[1] = zoomOriginY + ((zoomCenterY + valsInView / 2f) - zoomOriginY) * phase;

        mTrans.pointValuesToPixel(pts);

        mViewPortHandler.translate(pts, save);
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
    public void recycleSelf() {

    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    protected ObjectPool.Poolable instantiate() {
        return new AnimatedZoomJob(null,null,null,null,0,0,0,0,0,0,0,0,0,0);
    }
}
