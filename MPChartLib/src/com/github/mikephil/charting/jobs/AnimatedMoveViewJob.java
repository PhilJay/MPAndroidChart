package com.github.mikephil.charting.jobs;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.View;

import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 19/02/16.
 */
public class AnimatedMoveViewJob extends AnimatedViewPortJob {

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
}
