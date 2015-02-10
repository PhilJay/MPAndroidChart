
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.FillFormatter;

/**
 * Chart that draws lines, surfaces, circles, ...
 * 
 * @author Philipp Jahoda
 */
public class LineChart extends BarLineChartBase<LineData> {

    /** the width of the highlighning line */
    protected float mHighlightWidth = 3f;

    private FillFormatter mFillFormatter;

    public LineChart(Context context) {
        super(context);
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new LineChartRenderer(this, mAnimator, mViewPortHandler);
        
        mFillFormatter = new DefaultFillFormatter();
    }

    @Override
    protected void calcMinMax(boolean fixedValues) {
        super.calcMinMax(fixedValues);

        // // if there is only one value in the chart
        // if (mOriginalData.getYValCount() == 1
        // || mOriginalData.getYValCount() <= mOriginalData.getDataSetCount()) {
        // mDeltaX = 1;
        // }

        if (mDeltaX == 0 && mData.getYValCount() > 0)
            mDeltaX = 1;
    }

    @Override
    protected void drawDataSet(int index) {
        
    }

    @Override
    protected void drawValues() {

    }
   
    @Override
    protected void drawAdditional() {

    }
    
    /**
     * set the width of the highlightning lines, default 3f
     * 
     * @param width
     */
    public void setHighlightLineWidth(float width) {
        mHighlightWidth = width;
    }

    /**
     * returns the width of the highlightning line, default 3f
     * 
     * @return
     */
    public float getHighlightLineWidth() {
        return mHighlightWidth;
    }

    @Override
    public void setPaint(Paint p, int which) {
        super.setPaint(p, which);

        switch (which) {
            case PAINT_CIRCLES_INNER:
//                mCirclePaintInner = p;
                break;
        }
    }

    @Override
    public Paint getPaint(int which) {
        Paint p = super.getPaint(which);
        if (p != null)
            return p;

        switch (which) {
            case PAINT_CIRCLES_INNER:
                return null;
        }

        return null;
    }

    /**
     * Sets a custom FillFormatter to the chart that handles the position of the
     * filled-line for each DataSet. Set this to null to use the default logic.
     * 
     * @param formatter
     */
    public void setFillFormatter(FillFormatter formatter) {

        if (formatter == null)
            formatter = new DefaultFillFormatter();
        else
            mFillFormatter = formatter;
    }

    /**
     * Returns the FillFormatter that handles the position of the filled-line.
     * 
     * @return
     */
    public FillFormatter getFillFormatter() {
        return mFillFormatter;
    }

    /**
     * Default formatter that calculates the position of the filled line.
     * 
     * @author Philipp Jahoda
     */
    private class DefaultFillFormatter implements FillFormatter {

        @Override
        public float getFillLinePosition(LineDataSet dataSet, LineData data,
                float chartMaxY, float chartMinY) {

            float fillMin = 0f;

            if (dataSet.getYMax() > 0 && dataSet.getYMin() < 0) {
                fillMin = 0f;
            } else {

                if (!mStartAtZero) {

                    float max, min;

                    if (data.getYMax() > 0)
                        max = 0f;
                    else
                        max = chartMaxY;
                    if (data.getYMin() < 0)
                        min = 0f;
                    else
                        min = chartMinY;

                    fillMin = dataSet.getYMin() >= 0 ? min : max;
                } else {
                    fillMin = 0f;
                }

            }

            return fillMin;
        }
    }
}
