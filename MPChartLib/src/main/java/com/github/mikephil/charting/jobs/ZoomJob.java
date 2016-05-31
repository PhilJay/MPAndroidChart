
package com.github.mikephil.charting.jobs;

import android.graphics.Matrix;
import android.view.View;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 19/02/16.
 */
public class ZoomJob extends ViewPortJob {

    protected float scaleX;
    protected float scaleY;

    protected YAxis.AxisDependency axisDependency;

    public ZoomJob(ViewPortHandler viewPortHandler, float scaleX, float scaleY, float xValue, float yValue, Transformer trans, YAxis.AxisDependency axis, View v) {
        super(viewPortHandler, xValue, yValue, trans, v);

        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.axisDependency = axis;
    }

    @Override
    public void run() {

        Matrix save = mViewPortHandler.zoom(scaleX, scaleY);
        mViewPortHandler.refresh(save, view, false);

        float valsInView = ((BarLineChartBase) view).getDeltaY(axisDependency) / mViewPortHandler.getScaleY();
        float xsInView =  ((BarLineChartBase) view).getXAxis().mAxisRange / mViewPortHandler.getScaleX();

        pts[0] = xValue - xsInView / 2f;
        pts[1] = yValue + valsInView / 2f;

        mTrans.pointValuesToPixel(pts);

        save = mViewPortHandler.translate(pts);
        mViewPortHandler.refresh(save, view, false);

        ((BarLineChartBase) view).calculateOffsets();
        view.postInvalidate();
    }
}
