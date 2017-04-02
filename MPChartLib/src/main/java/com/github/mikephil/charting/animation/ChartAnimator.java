
package com.github.mikephil.charting.animation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;

/**
 * Object responsible for all animations in the Chart. ANIMATIONS ONLY WORK FOR
 * API LEVEL 11 (Android 3.0.x) AND HIGHER.
 * 
 * @author Philipp Jahoda
 */
public class ChartAnimator {

    /** object that is updated upon animation update */
    private AnimatorUpdateListener mListener;

    public ChartAnimator() {

    }

    public ChartAnimator(AnimatorUpdateListener listener) {
        mListener = listener;
    }

    /**
     * ################ ################ ################ ################
     */
    /** CODE BELOW THIS RELATED TO ANIMATION */

    /** the phase that is animated and influences the drawn values on the y-axis */
    protected float mPhaseY = 1f;

    /** the phase that is animated and influences the drawn values on the x-axis */
    protected float mPhaseX = 1f;

    /** the object animator which animate the values on the y-axis */
    protected ObjectAnimator animatorY;

    /** the object animator which animate the values on the x-axis */
    protected ObjectAnimator animatorX;

    /**
     * ################ ################ ################ ################
     */
    /** METHODS FOR CUSTOM EASING */

    /**
     * Animates the drawing / rendering of the chart on both x- and y-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart.
     * If an animation is already running the chart will be redraw
     *
     * @param durationMillisX
     * @param durationMillisY
     * @param easingX
     * @param easingY
     */
    public void animateXY(int durationMillisX, int durationMillisY, EasingFunction easingX,
            EasingFunction easingY) {

        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        cancelAnimationXY();

        animatorY = ObjectAnimator.ofFloat(this, "phaseY", 0f, 1f);
        animatorY.setInterpolator(easingY);
        animatorY.setDuration(
                durationMillisY);
        animatorX = ObjectAnimator.ofFloat(this, "phaseX", 0f, 1f);
        animatorX.setInterpolator(easingX);
        animatorX.setDuration(
                durationMillisX);

        // make sure only one animator produces update-callbacks (which then
        // call invalidate())
        if (durationMillisX > durationMillisY) {
            animatorX.addUpdateListener(mListener);
        } else {
            animatorY.addUpdateListener(mListener);
        }

        animatorX.start();
        animatorY.start();
    }

    /**
     * Animates the rendering of the chart on the x-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     * If an animation is already running the chart will be redraw
     *
     * @param durationMillis
     * @param easing
     */
    public void animateX(int durationMillis, EasingFunction easing) {

        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        cancelAnimationX();

        animatorX = ObjectAnimator.ofFloat(this, "phaseX", 0f, 1f);
        animatorX.setInterpolator(easing);
        animatorX.setDuration(durationMillis);
        animatorX.addUpdateListener(mListener);
        animatorX.start();
    }

    /**
     * Animates the rendering of the chart on the y-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     * If an animation is already running the chart will be redraw
     *
     * @param durationMillis
     * @param easing
     */
    public void animateY(int durationMillis, EasingFunction easing) {

        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        cancelAnimationY();

        animatorY = ObjectAnimator.ofFloat(this, "phaseY", 0f, 1f);
        animatorY.setInterpolator(easing);
        animatorY.setDuration(durationMillis);
        animatorY.addUpdateListener(mListener);
        animatorY.start();
    }

    /**
     * ################ ################ ################ ################
     */
    /** METHODS FOR PREDEFINED EASING */

    /**
     * Animates the drawing / rendering of the chart on both x- and y-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart.
     * If an animation is already running the chart will be redraw
     *
     * @param durationMillisX
     * @param durationMillisY
     * @param easingX
     * @param easingY
     */
    public void animateXY(int durationMillisX, int durationMillisY, Easing.EasingOption easingX,
            Easing.EasingOption easingY) {

        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        cancelAnimationXY();

        animatorY = ObjectAnimator.ofFloat(this, "phaseY", 0f, 1f);
        animatorY.setInterpolator(Easing.getEasingFunctionFromOption(easingY));
        animatorY.setDuration(
                durationMillisY);
        animatorX = ObjectAnimator.ofFloat(this, "phaseX", 0f, 1f);
        animatorX.setInterpolator(Easing.getEasingFunctionFromOption(easingX));
        animatorX.setDuration(
                durationMillisX);

        // make sure only one animator produces update-callbacks (which then
        // call invalidate())
        if (durationMillisX > durationMillisY) {
            animatorX.addUpdateListener(mListener);
        } else {
            animatorY.addUpdateListener(mListener);
        }

        animatorX.start();
        animatorY.start();
    }

    /**
     * Animates the rendering of the chart on the x-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     * If an animation is already running the chart will be redraw
     *
     * @param durationMillis
     * @param easing
     */
    public void animateX(int durationMillis, Easing.EasingOption easing) {

        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        cancelAnimationX();

        animatorX = ObjectAnimator.ofFloat(this, "phaseX", 0f, 1f);
        animatorX.setInterpolator(Easing.getEasingFunctionFromOption(easing));
        animatorX.setDuration(durationMillis);
        animatorX.addUpdateListener(mListener);
        animatorX.start();
    }

    /**
     * Animates the rendering of the chart on the y-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     * If an animation is already running the chart will be redraw
     *
     * @param durationMillis
     * @param easing
     */
    public void animateY(int durationMillis, Easing.EasingOption easing) {

        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        cancelAnimationY();

        animatorY = ObjectAnimator.ofFloat(this, "phaseY", 0f, 1f);
        animatorY.setInterpolator(Easing.getEasingFunctionFromOption(easing));
        animatorY.setDuration(durationMillis);
        animatorY.addUpdateListener(mListener);
        animatorY.start();
    }

    /**
     * ################ ################ ################ ################
     */
    /** METHODS FOR ANIMATION WITHOUT EASING */

    /**
     * Animates the drawing / rendering of the chart on both x- and y-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart.
     * If an animation is already running the chart will be redraw
     *
     * @param durationMillisX
     * @param durationMillisY
     */
    public void animateXY(int durationMillisX, int durationMillisY) {

        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        cancelAnimationXY();

        animatorY = ObjectAnimator.ofFloat(this, "phaseY", 0f, 1f);
        animatorY.setDuration(
                durationMillisY);
        animatorX = ObjectAnimator.ofFloat(this, "phaseX", 0f, 1f);
        animatorX.setDuration(
                durationMillisX);

        // make sure only one animator produces update-callbacks (which then
        // call invalidate())
        if (durationMillisX > durationMillisY) {
            animatorX.addUpdateListener(mListener);
        } else {
            animatorY.addUpdateListener(mListener);
        }

        animatorX.start();
        animatorY.start();
    }

    /**
     * Animates the rendering of the chart on the x-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     * If an animation is already running the chart will be redraw
     *
     * @param durationMillis
     */
    public void animateX(int durationMillis) {

        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        cancelAnimationX();

        animatorX = ObjectAnimator.ofFloat(this, "phaseX", 0f, 1f);
        animatorX.setDuration(durationMillis);
        animatorX.addUpdateListener(mListener);
        animatorX.start();
    }

    /**
     * Animates the rendering of the chart on the y-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     * If an animation is already running the chart will be redraw
     *
     * @param durationMillis
     */
    public void animateY(int durationMillis) {

        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        cancelAnimationY();

        animatorY = ObjectAnimator.ofFloat(this, "phaseY", 0f, 1f);
        animatorY.setDuration(durationMillis);
        animatorY.addUpdateListener(mListener);
        animatorY.start();
    }

    /**
     * Check if a animation on the x-axis is currently running
     *
     */
    public boolean isAnimationXRunning() {
        if (android.os.Build.VERSION.SDK_INT < 11)
            return false;
        return animatorX != null && animatorX.isStarted() && animatorX.isRunning();
    }

    /**
     * Check if a animation on the Y-axis is currently running
     *
     */
    public boolean isAnimationYRunning() {
        if (android.os.Build.VERSION.SDK_INT < 11)
            return false;
        return animatorY != null && animatorY.isStarted() && animatorY.isRunning();
    }

    /**
     *  Cancel the animation on the x-axis and on the y-axis.
     *  No need to check if the animation is still running
     *
     */
    public void cancelAnimationXY() {
        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        cancelAnimationX();
        cancelAnimationY();
    }

    /**
     *  Cancel the animation on the x-axis.
     *  No need to check if the animation is still running
     *
     */
    public void cancelAnimationX() {
        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        if (isAnimationXRunning()) {
            animatorX.cancel();
            animatorX = null;
            mPhaseX = 1f;
        }
    }

    /**
     *  Cancel the animation on the y-axis.
     *  No need to check if the animation is still running
     *
     */
    public void cancelAnimationY() {
        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        if (isAnimationYRunning()) {
            animatorY.cancel();
            animatorY = null;
            mPhaseY = 1f;
        }
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
