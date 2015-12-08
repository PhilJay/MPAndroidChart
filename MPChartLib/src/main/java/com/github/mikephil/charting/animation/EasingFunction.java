package com.github.mikephil.charting.animation;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;

/**
 * Interface for creating custom made easing functions. Uses the
 * TimeInterpolator interface provided by Android.
 */
@SuppressLint("NewApi")
public interface EasingFunction extends TimeInterpolator {

    @Override
    float getInterpolation(float input);
}
