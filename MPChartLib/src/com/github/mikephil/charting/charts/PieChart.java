
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
import com.github.mikephil.charting.renderer.PieChartRenderer;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

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

    /** array that holds the width of each pie-slice in degrees */
    private float[] mDrawAngles;

    /** array that holds the absolute angle in degrees of each slice */
    private float[] mAbsoluteAngles;

    /** if true, the white hole inside the chart will be drawn */
    private boolean mDrawHole = true;

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
    private float mTransparentCircleRadiusPercent = 55f;

    /** if enabled, centertext is drawn */
    private boolean mDrawCenterText = true;

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

        if (mHighlightEnabled && valuesToHighlight())
            mRenderer.drawHighlighted(mDrawCanvas, mIndicesToHightlight);

        mRenderer.drawData(mDrawCanvas);

        mRenderer.drawExtras(mDrawCanvas);

        mRenderer.drawValues(mDrawCanvas);

        drawLegend();

        drawDescription();

        canvas.drawBitmap(mDrawBitmap, 0, 0, mDrawPaint);
    }

    @Override
    protected void calculateOffsets() {
        super.calculateOffsets();

        // prevent nullpointer when no data set
        if (mDataNotSet)
            return;

        float diameter = getDiameter();
        float boxSize = diameter / 2f;

        PointF c = getCenterOffsets();

        // create the circle box that will contain the pie-chart (the bounds of
        // the pie-chart)
        mCircleBox.set(c.x - boxSize, c.y - boxSize,
                c.x + boxSize, c.y + boxSize);
    }

    // @Override
    // protected void prepareContentRect() {
    // super.prepareContentRect();
    //
    // // prevent nullpointer when no data set
    // if (mDataNotSet)
    // return;
    //
    // float diameter = getDiameter();
    // float boxSize = diameter / 2f;
    //
    // PointF c = getCenterOffsets();
    //
    // // create the circle box that will contain the pie-chart (the bounds of
    // // the pie-chart)
    // mCircleBox.set(c.x - boxSize, c.y - boxSize,
    // c.x + boxSize, c.y + boxSize);
    // }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        calcAngles();
    }

    /** PieChart does not support MarkerView */
    @Override
    protected float[] getMarkerPosition(Entry e, int dataSetIndex) {
        return new float[0];
    }

    /**
     * calculates the needed angles for the chart slices
     */
    private void calcAngles() {

        mDrawAngles = new float[mData.getYValCount()];
        mAbsoluteAngles = new float[mData.getYValCount()];

        ArrayList<PieDataSet> dataSets = mData.getDataSets();

        int cnt = 0;

        for (int i = 0; i < mData.getDataSetCount(); i++) {

            PieDataSet set = dataSets.get(i);
            ArrayList<Entry> entries = set.getYVals();

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

        for (int i = 0; i < mIndicesToHightlight.length; i++)

            // check if the xvalue for the given dataset needs highlight
            if (mIndicesToHightlight[i].getXIndex() == xIndex
                    && mIndicesToHightlight[i].getDataSetIndex() == dataSetIndex)
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
        float a = (angle - mRotationAngle + 360) % 360f;

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

        ArrayList<? extends DataSet<? extends Entry>> dataSets = mData.getDataSets();

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
     * @link https://github.com/tbarthel-fr
     * @param enable
     */
    public void setHoleColorTransparent(boolean enable) {
        if (enable) {
            ((PieChartRenderer) mRenderer).getPaintHole().setXfermode(
                    new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
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
    protected float getRequiredBottomOffset() {
        return mLegendLabelPaint.getTextSize() * 4f;
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
     * Sets the size of the center text of the piechart.
     * 
     * @param size
     */
    public void setCenterTextSize(float size) {
        ((PieChartRenderer) mRenderer).getPaintCenterText().setTextSize(
                Utils.convertDpToPixel(size));
    }

    /**
     * sets the radius of the hole in the center of the piechart in percent of
     * the maximum radius (max = the radius of the whole chart), default 50%
     * 
     * @param size
     */
    public void setHoleRadius(final float percent) {
        mHoleRadiusPercent = percent;
    }

    public float getHoleRadius() {
        return mHoleRadiusPercent;
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

    @Override
    public void setPaint(Paint p, int which) {
        super.setPaint(p, which);

        switch (which) {
            case PAINT_HOLE:
                // mHolePaint = p;
                break;
            case PAINT_CENTER_TEXT:
                // mCenterTextPaint = p;
                break;
        }
    }

    @Override
    public Paint getPaint(int which) {
        Paint p = super.getPaint(which);
        if (p != null)
            return p;

        switch (which) {
            case PAINT_HOLE:
                // return mHolePaint;
            case PAINT_CENTER_TEXT:
                // return mCenterTextPaint;
        }

        return null;
    }
}
