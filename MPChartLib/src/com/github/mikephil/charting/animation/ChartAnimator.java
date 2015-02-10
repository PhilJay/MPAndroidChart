package com.github.mikephil.charting.animation;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class ChartAnimator {
    
    /** object that is updated upon animation update */
    private AnimatorUpdateListener mListener;
    
    public ChartAnimator(AnimatorUpdateListener listener) {
        mListener = listener;
    }

    /**
     * ################ ################ ################ ################
     * Animation support below Honeycomb thanks to Jake Wharton's awesome
     * nineoldandroids library: https://github.com/JakeWharton/NineOldAndroids
     */
    /** CODE BELOW THIS RELATED TO ANIMATION */

    /** the phase that is animated and influences the drawn values on the y-axis */
    protected float mPhaseY = 1f;

    /** the phase that is animated and influences the drawn values on the x-axis */
    protected float mPhaseX = 1f;

    /** objectanimator used for animating values on y-axis */
    private ObjectAnimator mAnimatorY;

    /** objectanimator used for animating values on x-axis */
    private ObjectAnimator mAnimatorX;

    /**
     * Animates the drawing / rendering of the chart on both x- and y-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart.
     *
     * @param durationMillisX
     * @param durationMillisY
     */
    public void animateXY(int durationMillisX, int durationMillisY) {

        mAnimatorY = ObjectAnimator.ofFloat(this, "phaseY", 0f, 1f);
        mAnimatorY.setDuration(
                durationMillisY);
        mAnimatorX = ObjectAnimator.ofFloat(this, "phaseX", 0f, 1f);
        mAnimatorX.setDuration(
                durationMillisX);

        // make sure only one animator produces update-callbacks (which then
        // call invalidate())
        if (durationMillisX > durationMillisY) {
            mAnimatorX.addUpdateListener(mListener);
        } else {
            mAnimatorY.addUpdateListener(mListener);
        }

        mAnimatorX.start();
        mAnimatorY.start();
    }

    /**
     * Animates the rendering of the chart on the x-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     *
     * @param durationMillis
     */
    public void animateX(int durationMillis) {

        mAnimatorX = ObjectAnimator.ofFloat(this, "phaseX", 0f, 1f);
        mAnimatorX.setDuration(durationMillis);
        mAnimatorX.addUpdateListener(mListener);
        mAnimatorX.start();
    }

    /**
     * Animates the rendering of the chart on the y-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     *
     * @param durationMillis
     */
    public void animateY(int durationMillis) {

        mAnimatorY = ObjectAnimator.ofFloat(this, "phaseY", 0f, 1f);
        mAnimatorY.setDuration(durationMillis);
        mAnimatorY.addUpdateListener(mListener);
        mAnimatorY.start();
    }

    /**
     * This gets the y-phase that is used to animate the values.
     *
     * @return
     */
    public float getPhaseY() {
        return mPhaseY;
    }

    /**
     * This modifys the y-phase that is used to animate the values.
     *
     * @param phase
     */
    public void setPhaseY(float phase) {
        mPhaseY = phase;
    }

    /**
     * This gets the x-phase that is used to animate the values.
     *
     * @return
     */
    public float getPhaseX() {
        return mPhaseX;
    }

    /**
     * This modifys the x-phase that is used to animate the values.
     *
     * @param phase
     */
    public void setPhaseX(float phase) {
        mPhaseX = phase;
    }
}
