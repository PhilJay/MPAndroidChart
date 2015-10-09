
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.renderer.PieChartRenderer;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;

/**
 * View that represents a pie chart. Draws cake like slices.
 *
 * @author Philipp Jahoda
 */
public class PieChart extends PieRadarChartBase<PieData> {

    /**
     * rect object that represents the bounds of the piechart, needed for
     * drawing the circle
     */
    private RectF mCircleBox = new RectF();

    /**
     * flag indicating if the x-labels should be drawn or not
     */
    private boolean mDrawXLabels = true;

    /**
     * array that holds the width of each pie-slice in degrees
     */
    private float[] mDrawAngles;

    /**
     * array that holds the absolute angle in degrees of each slice
     */
    private float[] mAbsoluteAngles;

    /**
     * if true, the white hole inside the chart will be drawn
     */
    private boolean mDrawHole = true;

    /**
     * if true, the values inside the piechart are drawn as percent values
     */
    private boolean mUsePercentValues = false;

    /**
     * if true, the slices of the piechart are rounded
     */
    private boolean mDrawRoundedSlices = false;

    /**
     * variable for the text that is drawn in the center of the pie-chart. If
     * this value is null, the default is "Total Value\n + getYValueSum()"
     */
    private String mCenterText = "";

    /**
     * indicates the size of the hole in the center of the piechart, default:
     * radius / 2
     */
    private float mHoleRadiusPercent = 50f;

    /**
     * the radius of the transparent circle next to the chart-hole in the center
     */
    protected float mTransparentCircleRadiusPercent = 55f;

    /**
     * if enabled, centertext is drawn
     */
    private boolean mDrawCenterText = true;

    private boolean mCenterTextWordWrapEnabled = false;

    private float mCenterTextRadiusPercent = 1.f;

    public PieChart(Context context) {
        super(context);
    }

    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new PieChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDataNotSet)
            return;

        mRenderer.drawData(canvas);

        if (valuesToHighlight())
            mRenderer.drawHighlighted(canvas, mIndicesToHighlight);

        mRenderer.drawExtras(canvas);

        mRenderer.drawValues(canvas);

        mLegendRenderer.renderLegend(canvas);

        drawDescription(canvas);

        drawMarkers(canvas);
    }

    @Override
    public void calculateOffsets() {
        super.calculateOffsets();

        // prevent nullpointer when no data set
        if (mDataNotSet)
            return;

        float diameter = getDiameter();
        float radius = diameter / 2f;

        PointF c = getCenter();

        float shift = mData.getDataSet().getSelectionShift();

        // create the circle box that will contain the pie-chart (the bounds of
        // the pie-chart)
        mCircleBox.set(c.x - radius + shift,
                c.y - radius + shift,
                c.x + radius - shift,
                c.y + radius - shift);
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        calcAngles();
    }

    @Override
    protected float[] getMarkerPosition(Entry e, Highlight highlight) {

        PointF center = getCenterCircleBox();
        float r = getRadius();

        float off = r / 10f * 3.6f;

        if (isDrawHoleEnabled()) {
            off = (r - (r / 100f * getHoleRadius())) / 2f;
        }

        r -= off; // offset to keep things inside the chart

        float rotationAngle = getRotationAngle();

        int i = e.getXIndex();

        // offset needed to center the drawn text in the slice
        float offset = mDrawAngles[i] / 2;

        // calculate the text position
        float x = (float) (r
                * Math.cos(Math.toRadians((rotationAngle + mAbsoluteAngles[i] - offset)
                * mAnimator.getPhaseY())) + center.x);
        float y = (float) (r
                * Math.sin(Math.toRadians((rotationAngle + mAbsoluteAngles[i] - offset)
                * mAnimator.getPhaseY())) + center.y);

        return new float[]{x, y};
    }

    /**
     * calculates the needed angles for the chart slices
     */
    private void calcAngles() {

        mDrawAngles = new float[mData.getYValCount()];
        mAbsoluteAngles = new float[mData.getYValCount()];

        List<PieDataSet> dataSets = mData.getDataSets();

        int cnt = 0;

        for (int i = 0; i < mData.getDataSetCount(); i++) {

            PieDataSet set = dataSets.get(i);
            List<Entry> entries = set.getYVals();

            for (int j = 0; j < entries.size(); j++) {

                mDrawAngles[cnt] = calcAngle(Math.abs(entries.get(j).getVal()));

                if (cnt == 0) {
                    mAbsoluteAngles[cnt] = mDrawAngles[cnt];
                } else {
                    mAbsoluteAngles[cnt] = mAbsoluteAngles[cnt - 1] + mDrawAngles[cnt];
                }

                cnt++;
            }
        }

    }

    /**
     * checks if the given index in the given DataSet is set for highlighting or
     * not
     *
     * @param xIndex
     * @param dataSetIndex
     * @return
     */
    public boolean needsHighlight(int xIndex, int dataSetIndex) {

        // no highlight
        if (!valuesToHighlight() || dataSetIndex < 0)
            return false;

        for (int i = 0; i < mIndicesToHighlight.length; i++)

            // check if the xvalue for the given dataset needs highlight
            if (mIndicesToHighlight[i].getXIndex() == xIndex
                    && mIndicesToHighlight[i].getDataSetIndex() == dataSetIndex)
                return true;

        return false;
    }

    /**
     * calculates the needed angle for a given value
     *
     * @param value
     * @return
     */
    private float calcAngle(float value) {
        return value / mData.getYValueSum() * 360f;
    }

    @Override
    public int getIndexForAngle(float angle) {

        // take the current angle of the chart into consideration
        float a = Utils.getNormalizedAngle(angle - getRotationAngle());

        for (int i = 0; i < mAbsoluteAngles.length; i++) {
            if (mAbsoluteAngles[i] > a)
                return i;
        }

        return -1; // return -1 if no index found
    }

    /**
     * Returns the index of the DataSet this x-index belongs to.
     *
     * @param xIndex
     * @return
     */
    public int getDataSetIndexForIndex(int xIndex) {

        List<? extends DataSet<? extends Entry>> dataSets = mData.getDataSets();

        for (int i = 0; i < dataSets.size(); i++) {
            if (dataSets.get(i).getEntryForXIndex(xIndex) != null)
                return i;
        }

        return -1;
    }

    /**
     * returns an integer array of all the different angles the chart slices
     * have the angles in the returned array determine how much space (of 360°)
     * each slice takes
     *
     * @return
     */
    public float[] getDrawAngles() {
        return mDrawAngles;
    }

    /**
     * returns the absolute angles of the different chart slices (where the
     * slices end)
     *
     * @return
     */
    public float[] getAbsoluteAngles() {
        return mAbsoluteAngles;
    }

    /**
     * Sets the color for the hole that is drawn in the center of the PieChart
     * (if enabled). NOTE: Use setHoleColorTransparent(boolean enabled) to make
     * the hole transparent.
     *
     * @param color
     */
    public void setHoleColor(int color) {
        ((PieChartRenderer) mRenderer).getPaintHole().setXfermode(null);
        ((PieChartRenderer) mRenderer).getPaintHole().setColor(color);
    }

    /**
     * Set the hole in the center of the PieChart transparent. Thank you, code
     * provided by:
     *
     * @param enable
     * @link https://github.com/tbarthel-fr
     */
    public void setHoleColorTransparent(boolean enable) {
        if (enable) {
            ((PieChartRenderer) mRenderer).getPaintHole().setColor(0xFFFFFFFF);
            ((PieChartRenderer) mRenderer).getPaintHole().setXfermode(
                    new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            ((PieChartRenderer) mRenderer).getPaintHole().setXfermode(null);
        }
    }

    /**
     * Returns true if the hole in the center of the PieChart is transparent,
     * false if not.
     *
     * @return true if hole is transparent.
     */
    public boolean isHoleTransparent() {
        return ((PieChartRenderer) mRenderer).getPaintHole().getXfermode() != null;
    }

    /**
     * set this to true to draw the pie center empty
     *
     * @param enabled
     */
    public void setDrawHoleEnabled(boolean enabled) {
        this.mDrawHole = enabled;
    }

    /**
     * returns true if the hole in the center of the pie-chart is set to be
     * visible, false if not
     *
     * @return
     */
    public boolean isDrawHoleEnabled() {
        return mDrawHole;
    }

    /**
     * sets the text that is displayed in the center of the pie-chart. By
     * default, the text is "Total Value + sumofallvalues"
     *
     * @param text
     */
    public void setCenterText(String text) {
        mCenterText = text;
    }

    /**
     * returns the text that is drawn in the center of the pie-chart
     *
     * @return
     */
    public String getCenterText() {
        return mCenterText;
    }

    /**
     * set this to true to draw the text that is displayed in the center of the
     * pie chart
     *
     * @param enabled
     */
    public void setDrawCenterText(boolean enabled) {
        this.mDrawCenterText = enabled;
    }

    /**
     * returns true if drawing the center text is enabled
     *
     * @return
     */
    public boolean isDrawCenterTextEnabled() {
        return mDrawCenterText;
    }

    @Override
    protected float getRequiredLegendOffset() {
        return mLegendRenderer.getLabelPaint().getTextSize() * 2.f;
    }

    @Override
    protected float getRequiredBaseOffset() {
        return 0;
    }

    @Override
    public float getRadius() {
        if (mCircleBox == null)
            return 0;
        else
            return Math.min(mCircleBox.width() / 2f, mCircleBox.height() / 2f);
    }

    /**
     * returns the circlebox, the boundingbox of the pie-chart slices
     *
     * @return
     */
    public RectF getCircleBox() {
        return mCircleBox;
    }

    /**
     * returns the center of the circlebox
     *
     * @return
     */
    public PointF getCenterCircleBox() {
        return new PointF(mCircleBox.centerX(), mCircleBox.centerY());
    }

    /**
     * sets the typeface for the center-text paint
     *
     * @param t
     */
    public void setCenterTextTypeface(Typeface t) {
        ((PieChartRenderer) mRenderer).getPaintCenterText().setTypeface(t);
    }

    /**
     * Sets the size of the center text of the PieChart in dp.
     *
     * @param sizeDp
     */
    public void setCenterTextSize(float sizeDp) {
        ((PieChartRenderer) mRenderer).getPaintCenterText().setTextSize(
                Utils.convertDpToPixel(sizeDp));
    }

    /**
     * Sets the size of the center text of the PieChart in pixels.
     *
     * @param sizePixels
     */
    public void setCenterTextSizePixels(float sizePixels) {
        ((PieChartRenderer) mRenderer).getPaintCenterText().setTextSize(sizePixels);
    }

    /**
     * Sets the color of the center text of the PieChart.
     *
     * @param color
     */
    public void setCenterTextColor(int color) {
        ((PieChartRenderer) mRenderer).getPaintCenterText().setColor(color);
    }

    /**
     * sets the radius of the hole in the center of the piechart in percent of
     * the maximum radius (max = the radius of the whole chart), default 50%
     *
     * @param percent
     */
    public void setHoleRadius(final float percent) {
        mHoleRadiusPercent = percent;
    }

    /**
     * Returns the size of the hole radius in percent of the total radius.
     *
     * @return
     */
    public float getHoleRadius() {
        return mHoleRadiusPercent;
    }

    /**
     * Sets the color the transparent-circle should have.
     *
     * @param color
     */
    public void setTransparentCircleColor(int color) {

        Paint p = ((PieChartRenderer) mRenderer).getPaintTransparentCircle();
        int alpha = p.getAlpha();
        p.setColor(color);
        p.setAlpha(alpha);
    }

    /**
     * sets the radius of the transparent circle that is drawn next to the hole
     * in the piechart in percent of the maximum radius (max = the radius of the
     * whole chart), default 55% -> means 5% larger than the center-hole by
     * default
     *
     * @param percent
     */
    public void setTransparentCircleRadius(final float percent) {
        mTransparentCircleRadiusPercent = percent;
    }

    public float getTransparentCircleRadius() {
        return mTransparentCircleRadiusPercent;
    }

    /**
     * Sets the amount of transparency the transparent circle should have 0 = fully transparent, 255 = fully opaque.
     * Default value is 100.
     *
     * @param alpha 0-255
     */
    public void setTransparentCircleAlpha(int alpha) {
        ((PieChartRenderer) mRenderer).getPaintTransparentCircle().setAlpha(alpha);
    }

    /**
     * set this to true to draw the x-value text into the pie slices
     *
     * @param enabled
     */
    public void setDrawSliceText(boolean enabled) {
        mDrawXLabels = enabled;
    }

    /**
     * returns true if drawing x-values is enabled, false if not
     *
     * @return
     */
    public boolean isDrawSliceTextEnabled() {
        return mDrawXLabels;
    }

    /**
     * Returns true if the chart is set to draw each end of a pie-slice
     * "rounded".
     *
     * @return
     */
    public boolean isDrawRoundedSlicesEnabled() {
        return mDrawRoundedSlices;
    }

    /**
     * If this is enabled, values inside the PieChart are drawn in percent and
     * not with their original value. Values provided for the ValueFormatter to
     * format are then provided in percent.
     *
     * @param enabled
     */
    public void setUsePercentValues(boolean enabled) {
        mUsePercentValues = enabled;
    }

    /**
     * Returns true if using percentage values is enabled for the chart.
     *
     * @return
     */
    public boolean isUsePercentValuesEnabled() {
        return mUsePercentValues;
    }

    /**
     * should the center text be word wrapped?
     * note that word wrapping takes a toll on performance
     * if word wrapping is disabled, newlines are still respected
     */
    public void setCenterTextWordWrapEnabled(boolean enabled) {
        mCenterTextWordWrapEnabled = enabled;
    }

    /**
     * should the center text be word wrapped?
     * note that word wrapping takes a toll on performance
     * if word wrapping is disabled, newlines are still respected
     */
    public boolean isCenterTextWordWrapEnabled() {
        return mCenterTextWordWrapEnabled;
    }

    /**
     * the rectangular radius of the bounding box for the center text, as a percentage of the pie hole
     * default 1.f (100%)
     */
    public void setCenterTextRadiusPercent(float percent) {
        mCenterTextRadiusPercent = percent;
    }

    /**
     * the rectangular radius of the bounding box for the center text, as a percentage of the pie hole
     * default 1.f (100%)
     */
    public float getCenterTextRadiusPercent() {
        return mCenterTextRadiusPercent;
    }


    @Override
    protected void onDetachedFromWindow() {
        // releases the bitmap in the renderer to avoid oom error
        if(mRenderer != null && mRenderer instanceof PieChartRenderer) {
            ((PieChartRenderer) mRenderer).releaseBitmap();
        }
        super.onDetachedFromWindow();
    }

}
