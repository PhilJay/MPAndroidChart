
package com.github.mikephil.charting.animation;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.SystemClock;

/**
 * Object responsible for all animations in the Chart.
 * 
 * @author Philipp Jahoda
 */
@SuppressLint("NewApi")
public class ChartAnimator {

    /** object that is updated upon animation update */
    private UpdateListener mListener;

    public ChartAnimator() {
    }

    public ChartAnimator(UpdateListener listener) {
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

    private long mStartTime;

    /** objectanimator used for animating values on x-axis */
    private FrameHandler mHandler;
    private final Object mLock = new Object();
    private long mXDuration = 0;
    private long mYDuration = 0;
    private long mEndTimeX = 0;
    private long mEndTimeY = 0;
    private long mEndTime = 0;
    private boolean mEnabledX = false;
    private boolean mEnabledY = false;
    private AnimationEasing.EasingFunction mEasing;

    private static final long FRAME_DELAY = 15;

    protected void startAnimationLoop() {
        synchronized (mLock) {
            if (mHandler != null) {
                mHandler.removeMessages(0);
                mHandler = null;
            }

            mHandler = new FrameHandler();
            mHandler.queueNowFrame();
        }
    }

    public void stop() {
        mEnabledX = false;
        mEnabledY = false;

        synchronized (mLock) {
            if (mHandler != null) {
                mHandler.removeMessages(0);
                mHandler = null;
            }
        }
    }

    /**
     * Animates the drawing / rendering of the chart on both x- and y-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart.
     *
     * @param durationMillisX
     * @param durationMillisY
     * @param easing an easing function to be used on the animation phase
     */
    public void animateXY(int durationMillisX, int durationMillisY,
            final AnimationEasing.EasingFunction easing) {

        stop();

        mStartTime = SystemClock.uptimeMillis();
        mXDuration = durationMillisX;
        mYDuration = durationMillisY;
        mEndTimeX = mStartTime + durationMillisX;
        mEndTimeY = mStartTime + durationMillisY;
        mEndTime = mEndTimeX > mEndTimeY ? mEndTimeX : mEndTimeY;
        mEnabledX = durationMillisX > 0;
        mEnabledY = durationMillisY > 0;

        mEasing = easing;

        if (mEnabledX || mEnabledY)
            startAnimationLoop();
    }

    /**
     * Animates the drawing / rendering of the chart on both x- and y-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart.
     *
     * @param durationMillisX
     * @param durationMillisY
     * @param easing an easing function option to be used on the animation phase
     */
    public void animateXY(int durationMillisX, int durationMillisY,
            AnimationEasing.EasingOption easing) {
        animateXY(durationMillisX, durationMillisY,
                AnimationEasing.getEasingFunctionFromOption(easing));
    }

    /**
     * Animates the drawing / rendering of the chart on both x- and y-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart.
     *
     * @param durationMillisX
     * @param durationMillisY
     */
    public void animateXY(int durationMillisX, int durationMillisY) {
        animateXY(durationMillisX, durationMillisY, AnimationEasing.EasingOption.EaseInOutSine);
    }

    /**
     * Animates the rendering of the chart on the y-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     *
     * @param durationMillis
     * @param easing an easing function to be used on the animation phase
     */
    public void animateY(int durationMillis, final AnimationEasing.EasingFunction easing) {
        animateXY(0, durationMillis, easing);
    }

    /**
     * Animates the rendering of the chart on the y-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     *
     * @param durationMillis
     * @param easing an easing function option to be used on the animation phase
     */
    public void animateY(int durationMillis, AnimationEasing.EasingOption easing) {
        animateXY(0, durationMillis, AnimationEasing.getEasingFunctionFromOption(easing));
    }

    /**
     * Animates the rendering of the chart on the y-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     *
     * @param durationMillis
     */
    public void animateY(int durationMillis) {
        animateXY(0, durationMillis, AnimationEasing.EasingOption.EaseInOutSine);
    }

    /**
     * Animates the rendering of the chart on the x-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     *
     * @param durationMillis
     * @param easing an easing function to be used on the animation phase
     */
    public void animateX(int durationMillis, final AnimationEasing.EasingFunction easing) {
        animateXY(durationMillis, 0, easing);
    }

    /**
     * Animates the rendering of the chart on the x-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     *
     * @param durationMillis
     * @param easing an easing function option to be used on the animation phase
     */
    public void animateX(int durationMillis, AnimationEasing.EasingOption easing) {
        animateXY(durationMillis, 0, AnimationEasing.getEasingFunctionFromOption(easing));
    }

    /**
     * Animates the rendering of the chart on the x-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     *
     * @param durationMillis
     */
    public void animateX(int durationMillis) {
        animateXY(durationMillis, 0, AnimationEasing.EasingOption.EaseInOutSine);
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

    /**
     * Listener for callbacks on animation update.
     * 
     * @author Philipp Jahoda
     */
    public interface UpdateListener {
        public void onAnimationUpdate();
    }

    @SuppressLint("HandlerLeak")
    public class FrameHandler extends Handler {

        public void queueNextFrame() {
            postAtTime(frameHandler, SystemClock.uptimeMillis() + FRAME_DELAY);
        }

        public void queueNowFrame() {
            post(frameHandler);
        }

        private Runnable frameHandler = new Runnable()
        {
            @Override
            public void run()
            {

                synchronized (mLock) {

                    long currentTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentTime - mStartTime;

                    if (mEnabledX) {
                        long duration = mXDuration;
                        long elapsed = elapsedTime;

                        if (elapsed > duration)
                            elapsed = duration;

                        if (mEasing != null)
                            mPhaseX = mEasing.ease(elapsed, duration);
                        else
                            mPhaseX = elapsed / (float) duration;
                    }

                    if (mEnabledY) {
                        long duration = mYDuration;
                        long elapsed = elapsedTime;

                        if (elapsed > duration)
                            elapsed = duration;

                        if (mEasing != null)
                            mPhaseY = mEasing.ease(elapsed, duration);
                        else
                            mPhaseY = elapsed / (float) duration;
                    }

                    if (currentTime >= mEndTime)
                        stop();

                    if (mEnabledX || mEnabledY) {
                        queueNextFrame();
                    }

                    if (mListener != null)
                        mListener.onAnimationUpdate();
                }

            }
        };
    }
}
