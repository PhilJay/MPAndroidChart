
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.highlight.Highlight;
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

    /**
     * width of the main web lines
     */
    private float mWebLineWidth = 2.5f;

    /**
     * width of the inner web lines
     */
    private float mInnerWebLineWidth = 1.5f;

    /**
     * color for the main web lines
     */
    private int mWebColor = Color.rgb(122, 122, 122);

    /**
     * color for the inner web
     */
    private int mWebColorInner = Color.rgb(122, 122, 122);

    /**
     * transparency the grid is drawn with (0-255)
     */
    private int mWebAlpha = 150;

    /**
     * flag indicating if the web lines should be drawn or not
     */
    private boolean mDrawWeb = true;

    /**
     * modulus that determines how many labels and web-lines are skipped before the next is drawn
     */
    private int mSkipWebLineCount = 0;

    /**
     * the object reprsenting the y-axis labels
     */
    private YAxis mYAxis;

    /**
     * the object representing the x-axis labels
     */
    private XAxis mXAxis;

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
    protected void calcMinMax() {
        super.calcMinMax();

        float minLeft = mData.getYMin(AxisDependency.LEFT);
        float maxLeft = mData.getYMax(AxisDependency.LEFT);

        mXChartMax = mData.getXVals().size() - 1;
        mDeltaX = Math.abs(mXChartMax - mXChartMin);

        float leftRange = Math.abs(maxLeft - (mYAxis.isStartAtZeroEnabled() ? 0 : minLeft));

        float topSpaceLeft = leftRange / 100f * mYAxis.getSpaceTop();
        float bottomSpaceLeft = leftRange / 100f * mYAxis.getSpaceBottom();

        mXChartMax = mData.getXVals().size() - 1;
        mDeltaX = Math.abs(mXChartMax - mXChartMin);

        if (mYAxis.isStartAtZeroEnabled()) {
            if (minLeft < 0.f && maxLeft < 0.f) {
                // If the values are all negative, let's stay in the negative zone
                mYAxis.mAxisMinimum = Math.min(0.f, !Float.isNaN(mYAxis.getAxisMinValue()) ? mYAxis.getAxisMinValue() : (minLeft - bottomSpaceLeft));
                mYAxis.mAxisMaximum = 0.f;
            } else if (minLeft >= 0.0) {
                // We have positive values only, stay in the positive zone
                mYAxis.mAxisMinimum = 0.f;
                mYAxis.mAxisMaximum = Math.max(0.f, !Float.isNaN(mYAxis.getAxisMaxValue()) ? mYAxis.getAxisMaxValue() : (maxLeft + topSpaceLeft));
            } else {
                // Stick the minimum to 0.0 or less, and maximum to 0.0 or more (startAtZero for negative/positive at the same time)
                mYAxis.mAxisMinimum = Math.min(0.f, !Float.isNaN(mYAxis.getAxisMinValue()) ? mYAxis.getAxisMinValue() : (minLeft - bottomSpaceLeft));
                mYAxis.mAxisMaximum = Math.max(0.f, !Float.isNaN(mYAxis.getAxisMaxValue()) ? mYAxis.getAxisMaxValue() : (maxLeft + topSpaceLeft));
            }
        } else {
            // Use the values as they are
            mYAxis.mAxisMinimum = !Float.isNaN(mYAxis.getAxisMinValue()) ? mYAxis.getAxisMinValue() : (minLeft - bottomSpaceLeft);
            mYAxis.mAxisMaximum = !Float.isNaN(mYAxis.getAxisMaxValue()) ? mYAxis.getAxisMaxValue() : (maxLeft + topSpaceLeft);
        }

        mYAxis.mAxisRange = Math.abs(mYAxis.mAxisMaximum - mYAxis.mAxisMinimum);
    }

    @Override
    protected float[] getMarkerPosition(Entry e, Highlight highlight) {

        float angle = getSliceAngle() * e.getXIndex() + getRotationAngle();
        float val = e.getVal() * getFactor();
        PointF c = getCenterOffsets();

        PointF p = new PointF((float) (c.x + val * Math.cos(Math.toRadians(angle))),
                (float) (c.y + val * Math.sin(Math.toRadians(angle))));

        return new float[]{
                p.x, p.y
        };
    }

    @Override
    public void notifyDataSetChanged() {
        if (mData == null)
            return;

        calcMinMax();

//        if (mYAxis.needsDefaultFormatter()) {
//            mYAxis.setValueFormatter(mDefaultFormatter);
//        }

        mYAxisRenderer.computeAxis(mYAxis.mAxisMinimum, mYAxis.mAxisMaximum);
        mXAxisRenderer.computeAxis(mData.getXValMaximumLength(), mData.getXVals());

        if (mLegend != null && !mLegend.isLegendCustom())
            mLegendRenderer.computeLegend(mData);

        calculateOffsets();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mData == null)
            return;

        mXAxisRenderer.renderAxisLabels(canvas);

        if (mDrawWeb)
            mRenderer.drawExtras(canvas);

        mYAxisRenderer.renderLimitLines(canvas);

        mRenderer.drawData(canvas);

        if (valuesToHighlight())
            mRenderer.drawHighlighted(canvas, mIndicesToHighlight);

        mYAxisRenderer.renderAxisLabels(canvas);

        mRenderer.drawValues(canvas);

        mLegendRenderer.renderLegend(canvas);

        drawDescription(canvas);

        drawMarkers(canvas);
    }

    /**
     * Returns the factor that is needed to transform values into pixels.
     *
     * @return
     */
    public float getFactor() {
        RectF content = mViewPortHandler.getContentRect();
        return (float) Math.min(content.width() / 2f, content.height() / 2f)
                / mYAxis.mAxisRange;
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
        float a = Utils.getNormalizedAngle(angle - getRotationAngle());

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
     * Returns the object that represents all x-labels that are placed around
     * the RadarChart.
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
     * Sets the number of web-lines that should be skipped on chart web before the
     * next one is drawn. This targets the lines that come from the center of the RadarChart.
     *
     * @param count if count = 1 -> 1 line is skipped in between
     */
    public void setSkipWebLineCount(int count) {

        mSkipWebLineCount = Math.max(0, count);
    }

    /**
     * Returns the modulus that is used for skipping web-lines.
     *
     * @return
     */
    public int getSkipWebLineCount() {
        return mSkipWebLineCount;
    }

    @Override
    protected float getRequiredLegendOffset() {
        return mLegendRenderer.getLabelPaint().getTextSize() * 4.f;
    }

    @Override
    protected float getRequiredBaseOffset() {
        return mXAxis.isEnabled() && mXAxis.isDrawLabelsEnabled() ?
                mXAxis.mLabelRotatedWidth :
                Utils.convertDpToPixel(10f);
    }

    @Override
    public float getRadius() {
        RectF content = mViewPortHandler.getContentRect();
        return Math.min(content.width() / 2f, content.height() / 2f);
    }

    /**
     * Returns the maximum value this chart can display on it's y-axis.
     */
    public float getYChartMax() {
        return mYAxis.mAxisMaximum;
    }

    /**
     * Returns the minimum value this chart can display on it's y-axis.
     */
    public float getYChartMin() {
        return mYAxis.mAxisMinimum;
    }

    /**
     * Returns the range of y-values this chart can display.
     *
     * @return
     */
    public float getYRange() {
        return mYAxis.mAxisRange;
    }
}
