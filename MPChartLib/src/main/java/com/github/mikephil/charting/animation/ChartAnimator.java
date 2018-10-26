
package com.github.mikephil.charting.animation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.animation.Easing.EasingFunction;

/**
 * Object responsible for all animations in the Chart. Animations require API level 11.
 *
 * @author Philipp Jahoda
 * @author Mick Ashton
 */
public class ChartAnimator {

    /** object that is updated upon animation update */
    private AnimatorUpdateListener mListener;

    /** The phase of drawn values on the y-axis. 0 - 1 */
    @SuppressWarnings("WeakerAccess")
    protected float mPhaseY = 1f;

    /** The phase of drawn values on the x-axis. 0 - 1 */
    @SuppressWarnings("WeakerAccess")
    protected float mPhaseX = 1f;

    public ChartAnimator() { }

    @RequiresApi(11)
    public ChartAnimator(AnimatorUpdateListener listener) {
        mListener = listener;
    }

    @RequiresApi(11)
    private ObjectAnimator xAnimator(int duration, EasingFunction easing) {

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "phaseX", 0f, 1f);
        animatorX.setInterpolator(easing);
        animatorX.setDuration(duration);

        return animatorX;
    }

    @RequiresApi(11)
    private ObjectAnimator yAnimator(int duration, EasingFunction easing) {

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "phaseY", 0f, 1f);
        animatorY.setInterpolator(easing);
        animatorY.setDuration(duration);

        return animatorY;
    }

    /**
     * Animates values along the X axis, in a linear fashion.
     *
     * @param durationMillis animation duration
     */
    @RequiresApi(11)
    public void animateX(int durationMillis) {
        animateX(durationMillis, Easing.Linear);
    }

    /**
     * Animates values along the X axis.
     *
     * @param durationMillis animation duration
     * @param easing EasingFunction
     */
    @RequiresApi(11)
    public void animateX(int durationMillis, EasingFunction easing) {

        ObjectAnimator animatorX = xAnimator(durationMillis, easing);
        animatorX.addUpdateListener(mListener);
        animatorX.start();
    }

    /**
     * Animates values along both the X and Y axes, in a linear fashion.
     *
     * @param durationMillisX animation duration along the X axis
     * @param durationMillisY animation duration along the Y axis
     */
    @RequiresApi(11)
    public void animateXY(int durationMillisX, int durationMillisY) {
        animateXY(durationMillisX, durationMillisY, Easing.Linear, Easing.Linear);
    }

    /**
     * Animates values along both the X and Y axes.
     *
     * @param durationMillisX animation duration along the X axis
     * @param durationMillisY animation duration along the Y axis
     * @param easing EasingFunction for both axes
     */
    @RequiresApi(11)
    public void animateXY(int durationMillisX, int durationMillisY, EasingFunction easing) {

        ObjectAnimator xAnimator = xAnimator(durationMillisX, easing);
        ObjectAnimator yAnimator = yAnimator(durationMillisY, easing);

        if (durationMillisX > durationMillisY) {
            xAnimator.addUpdateListener(mListener);
        } else {
            yAnimator.addUpdateListener(mListener);
        }

        xAnimator.start();
        yAnimator.start();
    }

    /**
     * Animates values along both the X and Y axes.
     *
     * @param durationMillisX animation duration along the X axis
     * @param durationMillisY animation duration along the Y axis
     * @param easingX EasingFunction for the X axis
     * @param easingY EasingFunction for the Y axis
     */
    @RequiresApi(11)
    public void animateXY(int durationMillisX, int durationMillisY, EasingFunction easingX,
                          EasingFunction easingY) {

        ObjectAnimator xAnimator = xAnimator(durationMillisX, easingX);
        ObjectAnimator yAnimator = yAnimator(durationMillisY, easingY);

        if (durationMillisX > durationMillisY) {
            xAnimator.addUpdateListener(mListener);
        } else {
            yAnimator.addUpdateListener(mListener);
        }

        xAnimator.start();
        yAnimator.start();
    }

    /**
     * Animates values along the Y axis, in a linear fashion.
     *
     * @param durationMillis animation duration
     */
    @RequiresApi(11)
    public void animateY(int durationMillis) {
        animateY(durationMillis, Easing.Linear);
    }

    /**
     * Animates values along the Y axis.
     *
     * @param durationMillis animation duration
     * @param easing EasingFunction
     */
    @RequiresApi(11)
    public void animateY(int durationMillis, EasingFunction easing) {

        ObjectAnimator animatorY = yAnimator(durationMillis, easing);
        animatorY.addUpdateListener(mListener);
        animatorY.start();
    }

    /**
     * Animates the drawing / rendering of the chart on both x- and y-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart.
     *
     * @param durationMillisX animation duration along the X axis
     * @param durationMillisY animation duration along the Y axis
     * @param easingX EasingFunction for the X axis
     * @param easingY EasingFunction for the Y axis
     *
     * @deprecated Use {@link #animateXY(int, int, EasingFunction, EasingFunction)}
     * @see #animateXY(int, int, EasingFunction, EasingFunction)
     */
    @SuppressWarnings("deprecation")
    @Deprecated
    public void animateXY(int durationMillisX, int durationMillisY, Easing.EasingOption easingX,
            Easing.EasingOption easingY) {

        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "phaseY", 0f, 1f);
        animatorY.setInterpolator(Easing.getEasingFunctionFromOption(easingY));
        animatorY.setDuration(
                durationMillisY);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "phaseX", 0f, 1f);
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
     *
     * @param durationMillis animation duration
     * @param easing EasingFunction
     *
     * @deprecated Use {@link #animateX(int, EasingFunction)}
     * @see #animateX(int, EasingFunction)
     */
    @SuppressWarnings("deprecation")
    @Deprecated
    public void animateX(int durationMillis, Easing.EasingOption easing) {

        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "phaseX", 0f, 1f);
        animatorX.setInterpolator(Easing.getEasingFunctionFromOption(easing));
        animatorX.setDuration(durationMillis);
        animatorX.addUpdateListener(mListener);
        animatorX.start();
    }

    /**
     * Animates the rendering of the chart on the y-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     *
     * @param durationMillis animation duration
     * @param easing EasingFunction
     *
     * @deprecated Use {@link #animateY(int, EasingFunction)}
     * @see #animateY(int, EasingFunction)
     */
    @SuppressWarnings("deprecation")
    @Deprecated
    public void animateY(int durationMillis, Easing.EasingOption easing) {

        if (android.os.Build.VERSION.SDK_INT < 11)
            return;

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "phaseY", 0f, 1f);
        animatorY.setInterpolator(Easing.getEasingFunctionFromOption(easing));
        animatorY.setDuration(durationMillis);
        animatorY.addUpdateListener(mListener);
        animatorY.start();
    }

    /**
     * Gets the Y axis phase of the animation.
     *
     * @return float value of {@link #mPhaseY}
     */
    public float getPhaseY() {
        return mPhaseY;
    }

    /**
     * Sets the Y axis phase of the animation.
     *
     * @param phase float value between 0 - 1
     */
    public void setPhaseY(float phase) {
        if (phase > 1f) {
            phase = 1f;
        } else if (phase < 0f) {
            phase = 0f;
        }
        mPhaseY = phase;
    }

    /**
     * Gets the X axis phase of the animation.
     *
     * @return float value of {@link #mPhaseX}
     */
    public float getPhaseX() {
        return mPhaseX;
    }

    /**
     * Sets the X axis phase of the animation.
     *
     * @param phase float value between 0 - 1
     */
    public void setPhaseX(float phase) {
        if (phase > 1f) {
            phase = 1f;
        } else if (phase < 0f) {
            phase = 0f;
        }
        mPhaseX = phase;
    }
}
