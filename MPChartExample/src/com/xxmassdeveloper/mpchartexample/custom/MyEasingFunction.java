
package com.xxmassdeveloper.mpchartexample.custom;

import com.github.mikephil.charting.animation.Easing;

/**
 * Example of a custom made animation EasingFunction.
 * 
 * @author Philipp Jahoda
 */
public class MyEasingFunction implements Easing.EasingFunction {

    @Override
    public float getInterpolation(float input) {
        // do awesome stuff here, this is just linear easing
        return input;
    }
}
