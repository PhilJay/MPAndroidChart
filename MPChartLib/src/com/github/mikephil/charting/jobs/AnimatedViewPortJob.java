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
@SuppressLint("NewApi")
public abstract class AnimatedViewPortJob extends ViewPortJob implements ValueAnimator.AnimatorUpdateListener {

    protected ObjectAnimator animator;

    protected float phase;

    protected float xOrigin;
    protected float yOrigin;

    public AnimatedViewPortJob(ViewPortHandler viewPortHandler, float xValue, float yValue, Transformer trans, View v, float xOrigin, float yOrigin, long duration) {
        super(viewPortHandler, xValue, yValue, trans, v);
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
        animator = ObjectAnimator.ofFloat(this, "phase", 0f, 1f);
        animator.setDuration(duration);
        animator.addUpdateListener(this);
    }

    @SuppressLint("NewApi")
    @Override
    public void run() {
        animator.start();
    }

    public float getPhase() {
        return phase;
    }

    public void setPhase(float phase) {
        this.phase = phase;
    }

    public float getXOrigin() {
        return xOrigin;
    }

    public float getYOrigin() {
        return yOrigin;
    }
}
