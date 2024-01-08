
package com.github.mikephil.charting.data;

import android.graphics.Color;

import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;

import java.util.List;

/**
 * Baseclass of all DataSets for Bar-, Line-, Scatter- and CandleStickChart.
 *
 * @author Philipp Jahoda
 * Modified by Gurgen Khachatryan on 08/01/24.
 */
public abstract class BarLineScatterCandleBubbleDataSet<T extends Entry>
        extends DataSet<T>
        implements IBarLineScatterCandleBubbleDataSet<T> {

    /**
     * default highlight color
     */
    protected int mHighLightColor = Color.rgb(255, 187, 115);
    protected int mHighLightCircleColor = Color.rgb(0, 0, 0);
    protected int mHighLightCircleBorderColor = Color.rgb(0, 0, 0);

    public BarLineScatterCandleBubbleDataSet(List<T> yVals, String label) {
        super(yVals, label);
    }

    /**
     * Sets the color that is used for drawing the highlight indicators. Dont
     * forget to resolve the color using getResources().getColor(...) or
     * Color.rgb(...).
     *
     * @param color
     */
    public void setHighLightColor(int color) {
        mHighLightColor = color;
    }

    /**
     * Sets the color that is used for drawing the circle highlight indicators. Dont
     * forget to resolve the color using getResources().getColor(...) or
     * Color.rgb(...).
     *
     * @param color
     */
    public void setHighLightCircleColor(int color) {
        mHighLightCircleColor = color;
    }

    /**
     * Sets the color that is used for drawing the circle highlight indicator border. Dont
     * forget to resolve the color using getResources().getColor(...) or
     * Color.rgb(...).
     *
     * @param color
     */
    public void setHighLightCircleBorderColor(int color) {
        mHighLightCircleBorderColor = color;
    }

    @Override
    public int getHighLightColor() {
        return mHighLightColor;
    }

    @Override
    public int getHighLightCircleColor() {
        return mHighLightCircleColor;
    }

    @Override
    public int getHighLightCircleBorderColor() {
        return mHighLightCircleBorderColor;
    }

    protected void copy(BarLineScatterCandleBubbleDataSet barLineScatterCandleBubbleDataSet) {
        super.copy(barLineScatterCandleBubbleDataSet);
        barLineScatterCandleBubbleDataSet.mHighLightColor = mHighLightColor;
        barLineScatterCandleBubbleDataSet.mHighLightCircleColor = mHighLightCircleColor;
        barLineScatterCandleBubbleDataSet.mHighLightCircleBorderColor = mHighLightCircleBorderColor;
    }
}
