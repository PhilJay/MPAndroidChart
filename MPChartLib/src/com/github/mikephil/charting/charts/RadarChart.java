
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.renderer.RadarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRendererRadarChart;
import com.github.mikephil.charting.renderer.YAxisRendererRadarChart;
import com.github.mikephil.charting.utils.Utils;

/**
 * Implementation of the RadarChart, a "spidernet"-like chart. It works best
 * when displaying 5-10 entries per DataSet.
 * 
 * @author Philipp Jahoda
 */
public class RadarChart extends PieRadarChartBase<RadarData> {

    /** width of the main web lines */
    private float mWebLineWidth = 2.5f;

    /** width of the inner web lines */
    private float mInnerWebLineWidth = 1.5f;

    /** color for the main web lines */
    private int mWebColor = Color.rgb(122, 122, 122);

    /** color for the inner web */
    private int mWebColorInner = Color.rgb(122, 122, 122);

    /** transparency the grid is drawn with (0-255) */
    private int mWebAlpha = 150;

    /** flag indicating if the x-labels should be drawn or not */
    private boolean mDrawXLabels = true;

    /** flag indicating if the web lines should be drawn or not */
    private boolean mDrawWeb = true;

    /** the object reprsenting the y-axis labels */
    private YAxis mYAxis = new YAxis(AxisDependency.LEFT);

    /** the object representing the x-axis labels */
    private XAxis mXAxis = new XAxis();

    protected YAxisRendererRadarChart mYAxisRenderer;
    protected XAxisRendererRadarChart mXAxisRenderer;

    public RadarChart(Context context) {
        super(context);
    }

    public RadarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mYAxis = new YAxis(AxisDependency.LEFT);
        mXAxis = new XAxis();
        mXAxis.setSpaceBetweenLabels(0);

        mWebLineWidth = Utils.convertDpToPixel(1.5f);
        mInnerWebLineWidth = Utils.convertDpToPixel(0.75f);

        mRenderer = new RadarChartRenderer(this, mAnimator, mViewPortHandler);
        mYAxisRenderer = new YAxisRendererRadarChart(mViewPortHandler, mYAxis, this);
        mXAxisRenderer = new XAxisRendererRadarChart(mViewPortHandler, mXAxis, this);
    }

    @Override
    protected void calcMinMax(boolean fixedValues) {
        super.calcMinMax(fixedValues);

        // additional handling for space (default 15% space)
        // float space = Math.abs(mDeltaY / 100f * 15f);

        if (mYAxis.mAxisMaximum <= 0)
            mYAxis.mAxisMaximum = 1f;

        mYAxis.mAxisMinimum = 0;

//        mDeltaY = Math.abs(mYChartMax - mYChartMin);
    }

    @Override
    protected float[] getMarkerPosition(Entry e, int dataSetIndex) {

        float angle = getSliceAngle() * e.getXIndex() + getRotationAngle();
        float val = e.getVal() * getFactor();
        PointF c = getCenterOffsets();

        PointF p = new PointF((float) (c.x + val * Math.cos(Math.toRadians(angle))),
                (float) (c.y + val * Math.sin(Math.toRadians(angle))));

        return new float[] {
                p.x, p.y
        };
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mYAxisRenderer.computeAxis(0f, 0f);
        prepareYLabels();
        mXAxisRenderer.computeAxis(mData.getXValAverageLength(), mData.getXVals());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDataNotSet)
            return;

        if (mDrawXLabels)
            mXAxisRenderer.renderAxis(mDrawCanvas);

        if (mDrawWeb)
            mRenderer.drawExtras(mDrawCanvas);

        mYAxisRenderer.renderLimitLines(mDrawCanvas, mValueFormatter);

        mRenderer.drawData(mDrawCanvas);

        if (mHighlightEnabled && valuesToHighlight())
            mRenderer.drawHighlighted(mDrawCanvas, mIndicesToHightlight);

        if (mYAxis.isEnabled())
            mYAxisRenderer.renderAxis(mDrawCanvas);

        mRenderer.drawValues(mDrawCanvas);

        drawLegend();

        drawDescription();

        drawMarkers();

        canvas.drawBitmap(mDrawBitmap, 0, 0, mDrawPaint);
    }

    /**
     * Calculates the required maximum y-value in order to be able to provide
     * the desired number of label entries and rounded label values.
     */
    private void prepareYLabels() {

        int labelCount = mYAxis.getLabelCount();

        double max = mData.getYMax() > 0 ? mData.getYMax() : 1.0;
        double range = max - mYAxis.mAxisMinimum;

        double rawInterval = range / labelCount;
        double interval = Utils.roundToNextSignificant(rawInterval);
        double intervalMagnitude = Math.pow(10, (int) Math.log10(interval));
        int intervalSigDigit = (int) (interval / intervalMagnitude);
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or
            // 90
            interval = Math.floor(10 * intervalMagnitude);
        }

        double first = Math.ceil(mYAxis.mAxisMinimum / interval) * interval;
        double last = Utils.nextUp(Math.floor(max / interval) * interval);

        double f;
        int n = 0;
        for (f = first; f <= last; f += interval) {
            ++n;
        }

        mYAxis.mEntryCount = n;

        mYAxis.mAxisMaximum = (float) interval * n;

        // calc delta
//        mDeltaY = Math.abs(mYChartMax - mYChartMin);
    }

    /**
     * Returns the factor that is needed to transform values into pixels.
     * 
     * @return
     */
    public float getFactor() {
        RectF content = mViewPortHandler.getContentRect();
        return (float) Math.min(content.width() / 2, content.height() / 2)
                / mYAxis.mAxisMaximum;
    }

    /**
     * Returns the angle that each slice in the radar chart occupies.
     * 
     * @return
     */
    public float getSliceAngle() {
        return 360f / (float) mData.getXValCount();
    }

    @Override
    public int getIndexForAngle(float angle) {

        // take the current angle of the chart into consideration
        float a = (angle - mRotationAngle + 360) % 360f;

        float sliceangle = getSliceAngle();

        for (int i = 0; i < mData.getXValCount(); i++) {
            if (sliceangle * (i + 1) - sliceangle / 2f > a)
                return i;
        }

        return 0;
    }

    /**
     * Returns the object that represents all y-labels of the RadarChart.
     * 
     * @return
     */
    public YAxis getYAxis() {
        return mYAxis;
    }

    /**
     * Returns the object that represents all x-labels of the RadarChart.
     * 
     * @return
     */
    public XAxis getXAxis() {
        return mXAxis;
    }

    /**
     * Sets the width of the web lines that come from the center.
     * 
     * @param width
     */
    public void setWebLineWidth(float width) {
        mWebLineWidth = Utils.convertDpToPixel(width);
    }

    public float getWebLineWidth() {
        return mWebLineWidth;
    }

    /**
     * Sets the width of the web lines that are in between the lines coming from
     * the center.
     * 
     * @param width
     */
    public void setWebLineWidthInner(float width) {
        mInnerWebLineWidth = Utils.convertDpToPixel(width);
    }

    public float getWebLineWidthInner() {
        return mInnerWebLineWidth;
    }

    /**
     * Sets the transparency (alpha) value for all web lines, default: 150, 255
     * = 100% opaque, 0 = 100% transparent
     * 
     * @param alpha
     */
    public void setWebAlpha(int alpha) {
        mWebAlpha = alpha;
    }

    /**
     * Returns the alpha value for all web lines.
     * 
     * @return
     */
    public int getWebAlpha() {
        return mWebAlpha;
    }

    /**
     * Sets the color for the web lines that come from the center. Don't forget
     * to use getResources().getColor(...) when loading a color from the
     * resources. Default: Color.rgb(122, 122, 122)
     * 
     * @param color
     */
    public void setWebColor(int color) {
        mWebColor = color;
    }

    public int getWebColor() {
        return mWebColor;
    }

    /**
     * Sets the color for the web lines in between the lines that come from the
     * center. Don't forget to use getResources().getColor(...) when loading a
     * color from the resources. Default: Color.rgb(122, 122, 122)
     * 
     * @param color
     */
    public void setWebColorInner(int color) {
        mWebColorInner = color;
    }

    public int getWebColorInner() {
        return mWebColorInner;
    }

    /**
     * If set to true, drawing the web is enabled, if set to false, drawing the
     * whole web is disabled. Default: true
     * 
     * @param enabled
     */
    public void setDrawWeb(boolean enabled) {
        mDrawWeb = enabled;
    }

    /**
     * set this to true to enable drawing the x-labels, false if not
     * 
     * @param enabled
     */
    public void setDrawXLabels(boolean enabled) {
        mDrawXLabels = enabled;
    }

    /**
     * Returns true if drawing x-labels is enabled, false if not.
     * 
     * @return
     */
    public boolean isDrawXLabelsEnabled() {
        return mDrawXLabels;
    }

    @Override
    protected float getRequiredBottomOffset() {
        return mLegendLabelPaint.getTextSize() * 6.5f;
    }

    @Override
    protected float getRequiredBaseOffset() {
        return mXAxis.mLabelWidth;
    }

    @Override
    public float getRadius() {
        RectF content = mViewPortHandler.getContentRect();
        return Math.min(content.width() / 2f, content.height() / 2f);
    }
    
    public float getYChartMax() {
        return mYAxis.mAxisMaximum;
    }
    
    public float getYChartMin() {
        return mYAxis.mAxisMinimum;
    }

    @Override
    public void setPaint(Paint p, int which) {
        super.setPaint(p, which);

        switch (which) {
            case PAINT_RADAR_WEB:
                // mWebPaint = p;
                break;
        }
    }

    @Override
    public Paint getPaint(int which) {
        Paint p = super.getPaint(which);
        if (p != null)
            return p;

        switch (which) {
            case PAINT_RADAR_WEB:
                // return mWebPaint;
        }

        return null;
    }
}
