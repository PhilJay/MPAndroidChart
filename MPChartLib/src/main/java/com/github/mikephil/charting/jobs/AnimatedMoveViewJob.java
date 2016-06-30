package com.github.mikephil.charting.jobs;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.View;

import com.github.mikephil.charting.utils.ObjectPool;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 19/02/16.
 */
@SuppressLint("NewApi")
public class AnimatedMoveViewJob extends AnimatedViewPortJob {

    private static ObjectPool<AnimatedMoveViewJob> pool;

    static {
        pool = ObjectPool.create(4, new AnimatedMoveViewJob(null,0,0,null,null,0,0,0));
        pool.setReplenishPercentage(0.5f);
    }

    public static AnimatedMoveViewJob getInstance(ViewPortHandler viewPortHandler, float xValue, float yValue, Transformer trans, View v, float xOrigin, float yOrigin, long duration){
        AnimatedMoveViewJob result = pool.get();
        result.mViewPortHandler = viewPortHandler;
        result.xValue = xValue;
        result.yValue = yValue;
        result.mTrans = trans;
        result.view = v;
        result.xOrigin = xOrigin;
        result.yOrigin = yOrigin;
        result.resetAnimator();
        result.animator.setDuration(duration);
        return result;
    }

    public static void recycleInstance(AnimatedMoveViewJob instance){
        pool.recycle(instance);
    }


    public AnimatedMoveViewJob(ViewPortHandler viewPortHandler, float xValue, float yValue, Transformer trans, View v, float xOrigin, float yOrigin, long duration) {
        super(viewPortHandler, xValue, yValue, trans, v, xOrigin, yOrigin, duration);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

        pts[0] = xOrigin + (xValue - xOrigin) * phase;
        pts[1] = yOrigin + (yValue - yOrigin) * phase;

        mTrans.pointValuesToPixel(pts);
        mViewPortHandler.centerViewPort(pts, view);
    }

    public void recycleSelf(){
        recycleInstance(this);
    }

    @Override
    protected ObjectPool.Poolable instantiate() {
        return new AnimatedMoveViewJob(null,0,0,null,null,0,0,0);
    }
}
