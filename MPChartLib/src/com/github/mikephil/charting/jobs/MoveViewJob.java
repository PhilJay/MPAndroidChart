
package com.github.mikephil.charting.jobs;

import android.view.View;

import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 19/02/16.
 */
public class MoveViewJob extends Job {

    public MoveViewJob(ViewPortHandler viewPortHandler, float xIndex, float yValue, Transformer trans, View v) {
        super(viewPortHandler, xIndex, yValue, trans, v);
    }

    @Override
    public void run() {

        pts[0] = xValue;
        pts[1] = yValue;

        mTrans.pointValuesToPixel(pts);
        mViewPortHandler.centerViewPort(pts, view);
    }
}
