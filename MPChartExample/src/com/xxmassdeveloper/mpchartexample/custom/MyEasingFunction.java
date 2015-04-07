
package com.xxmassdeveloper.mpchartexample.custom;

import com.github.mikephil.charting.animation.AnimationEasing.EasingFunction;

/**
 * Example of a custom made animation EasingFunction.
 * 
 * @author Philipp Jahoda
 */
public class MyEasingFunction implements EasingFunction {

    @Override
    public float ease(long elapsed, long duration) {

        // do awesome stuff here, this is just linear easing
        return elapsed / (float) duration;
    }

}
