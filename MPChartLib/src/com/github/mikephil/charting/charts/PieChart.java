
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
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
    private float mTransparentCircleRadius = 55f;

    /** if enabled, centertext is drawn */
    private boolean mDrawCenterText = true;

    /**
     * set this to true to draw the x-values next to the values in the pie
     * slices
     */
    private boolean mDrawXVals = true;

    /**
     * if set to true, all values show up in percent instead of their real value
     */
    private boolean mUsePercentValues = false;

    /**
     * paint for the hole in the center of the pie chart and the transparent
     * circle
     */
    private Paint mHolePaint;

    /**
     * paint object for the text that can be displayed in the center of the
     * chart
     */
    private Paint mCenterTextPaint;

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

        // // piechart has no offsets
        // mOffsetTop = 0;
        // mOffsetBottom = 0;
        // mOffsetLeft = 0;
        // mOffsetRight = 0;

        mHolePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHolePaint.setColor(Color.WHITE);

        mCenterTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterTextPaint.setColor(Color.BLACK);
        mCenterTextPaint.setTextSize(Utils.convertDpToPixel(12f));
        mCenterTextPaint.setTextAlign(Align.CENTER);

        mValuePaint.setTextSize(Utils.convertDpToPixel(13f));
        mValuePaint.setColor(Color.WHITE);
        mValuePaint.setTextAlign(Align.CENTER);

        // for the piechart, drawing values is enabled
        mDrawYValues = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDataNotSet)
            return;

        drawHighlights();

        drawData();

        drawAdditional();

        drawValues();

        drawLegend();

        drawDescription();

        drawCenterText();

        canvas.drawBitmap(mDrawBitmap, 0, 0, mDrawPaint);
    }

    @Override
    protected void prepareContentRect() {
        super.prepareContentRect();

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

    @Override
    protected void calcMinMax(boolean fixedValues) {
        super.calcMinMax(fixedValues);

        calcAngles();
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

    @Override
    protected void drawHighlights() {

        // if there are values to highlight and highlighnting is enabled, do it
        if (mHighlightEnabled && valuesToHighlight()) {

            float angle = 0f;

            for (int i = 0; i < mIndicesToHightlight.length; i++) {

                // get the index to highlight
                int xIndex = mIndicesToHightlight[i].getXIndex();
                if (xIndex >= mDrawAngles.length)
                    continue;

                if (xIndex == 0)
                    angle = mRotationAngle;
                else
                    angle = mRotationAngle + mAbsoluteAngles[xIndex - 1];

                angle *= mPhaseY;

                float sliceDegrees = mDrawAngles[xIndex];

                float shiftangle = (float) Math.toRadians(angle + sliceDegrees / 2f);

                PieDataSet set = mData
                        .getDataSetByIndex(mIndicesToHightlight[i]
                                .getDataSetIndex());

                if (set == null)
                    continue;

                float shift = set.getSelectionShift();
                float xShift = shift * (float) Math.cos(shiftangle);
                float yShift = shift * (float) Math.sin(shiftangle);

                RectF highlighted = new RectF(mCircleBox.left + xShift, mCircleBox.top + yShift,
                        mCircleBox.right
                                + xShift, mCircleBox.bottom + yShift);

                mRenderPaint.setColor(set.getColor(xIndex));

                // redefine the rect that contains the arc so that the
                // highlighted pie is not cut off
                mDrawCanvas.drawArc(highlighted, angle + set.getSliceSpace() / 2f, sliceDegrees
                        - set.getSliceSpace() / 2f, true, mRenderPaint);
            }
        }
    }

    @Override
    protected void drawData() {

        float angle = mRotationAngle;

        ArrayList<PieDataSet> dataSets = mData.getDataSets();

        int cnt = 0;

        for (int i = 0; i < mData.getDataSetCount(); i++) {

            PieDataSet dataSet = dataSets.get(i);
            ArrayList<Entry> entries = dataSet.getYVals();

            for (int j = 0; j < entries.size(); j++) {

                float newangle = mDrawAngles[cnt];
                float sliceSpace = dataSet.getSliceSpace();

                Entry e = entries.get(j);

                // draw only if the value is greater than zero
                if ((Math.abs(e.getVal()) > 0.000001)) {

                    if (!needsHighlight(e.getXIndex(), i)) {

                        mRenderPaint.setColor(dataSet.getColor(j));
                        mDrawCanvas.drawArc(mCircleBox, angle + sliceSpace / 2f, newangle * mPhaseY
                                - sliceSpace / 2f, true, mRenderPaint);
                    }

                    // if(sliceSpace > 0f) {
                    //
                    // PointF outer = getPosition(c, radius, angle);
                    // PointF inner = getPosition(c, radius * mHoleRadiusPercent
                    // / 100f, angle);
                    // }
                }

                angle += newangle * mPhaseX;
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
    private boolean needsHighlight(int xIndex, int dataSetIndex) {

        // no highlight
        if (!valuesToHighlight())
            return false;

        for (int i = 0; i < mIndicesToHightlight.length; i++)

            // check if the xvalue for the given dataset needs highlight
            if (mIndicesToHightlight[i].getXIndex() == xIndex
                    && mIndicesToHightlight[i].getDataSetIndex() == dataSetIndex)
                return true;

        return false;
    }

    /**
     * draws the hole in the center of the chart and the transparent circle /
     * hole
     */
    private void drawHole() {

        if (mDrawHole) {

            float radius = getRadius();

            PointF c = getCenterCircleBox();

            int color = mHolePaint.getColor();

            // draw the hole-circle
            mDrawCanvas.drawCircle(c.x, c.y,
                    radius / 100 * mHoleRadiusPercent, mHolePaint);

            if (mTransparentCircleRadius > mHoleRadiusPercent) {

                // make transparent
                mHolePaint.setColor(color & 0x60FFFFFF);

                // draw the transparent-circle
                mDrawCanvas.drawCircle(c.x, c.y,
                        radius / 100 * mTransparentCircleRadius, mHolePaint);

                mHolePaint.setColor(color);
            }
        }
    }

    /**
     * draws the description text in the center of the pie chart makes most
     * sense when center-hole is enabled
     */
    private void drawCenterText() {

        if (mDrawCenterText && mCenterText != null) {

            PointF c = getCenterCircleBox();

            // get all lines from the text
            String[] lines = mCenterText.split("\n");

            // calculate the height for each line
            float lineHeight = Utils.calcTextHeight(mCenterTextPaint, lines[0]);
            float linespacing = lineHeight * 0.2f;

            float totalheight = lineHeight * lines.length - linespacing * (lines.length - 1);

            int cnt = lines.length;

            float y = c.y;

            for (int i = 0; i < lines.length; i++) {

                String line = lines[lines.length - i - 1];

                mDrawCanvas.drawText(line, c.x, y
                        + lineHeight * cnt - totalheight / 2f,
                        mCenterTextPaint);
                cnt--;
                y -= linespacing;
            }
        }
    }

    @Override
    protected void drawValues() {

        // if neither xvals nor yvals are drawn, return
        if (!mDrawXVals && !mDrawYValues)
            return;

        PointF center = getCenterCircleBox();

        // get whole the radius
        float r = getRadius();

        float off = r / 2f;

        if (mDrawHole) {
            off = (r - (r / 100f * mHoleRadiusPercent)) / 2f;
        }

        r -= off; // offset to keep things inside the chart

        ArrayList<PieDataSet> dataSets = mData.getDataSets();

        int cnt = 0;

        for (int i = 0; i < mData.getDataSetCount(); i++) {

            PieDataSet dataSet = dataSets.get(i);
            ArrayList<Entry> entries = dataSet.getYVals();

            for (int j = 0; j < entries.size() * mPhaseX; j++) {

                // offset needed to center the drawn text in the slice
                float offset = mDrawAngles[cnt] / 2;

                // calculate the text position
                float x = (float) (r
                        * Math.cos(Math.toRadians((mRotationAngle + mAbsoluteAngles[cnt] - offset)
                                * mPhaseY)) + center.x);
                float y = (float) (r
                        * Math.sin(Math.toRadians((mRotationAngle + mAbsoluteAngles[cnt] - offset)
                                * mPhaseY)) + center.y);

                String val = "";
                float value = entries.get(j).getVal();

                if (mUsePercentValues)
                    val = mValueFormatter.getFormattedValue(Math.abs(getPercentOfTotal(value)))
                            + " %";
                else
                    val = mValueFormatter.getFormattedValue(value);

                if (mDrawUnitInChart)
                    val = val + mUnit;

                // draw everything, depending on settings
                if (mDrawXVals && mDrawYValues) {

                    // use ascent and descent to calculate the new line
                    // position,
                    // 1.6f is the line spacing
                    float lineHeight = (mValuePaint.ascent() + mValuePaint.descent()) * 1.6f;
                    y -= lineHeight / 2;

                    mDrawCanvas.drawText(val, x, y, mValuePaint);
                    if (j < mData.getXValCount())
                        mDrawCanvas.drawText(mData.getXVals().get(j), x, y + lineHeight,
                                mValuePaint);

                } else if (mDrawXVals && !mDrawYValues) {
                    if (j < mData.getXValCount())
                        mDrawCanvas.drawText(mData.getXVals().get(j), x, y, mValuePaint);
                } else if (!mDrawXVals && mDrawYValues) {

                    mDrawCanvas.drawText(val, x, y, mValuePaint);
                }

                cnt++;
            }
        }
    }

    @Override
    protected void drawAdditional() {
        drawHole();
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
        mHolePaint.setXfermode(null);
        mHolePaint.setColor(color);
    }

    /**
     * Set the hole in the center of the PieChart transparent.
     *
     * @param enable
     */
    public void setHoleColorTransparent(boolean enable) {
        if (enable) {
            mHolePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        } else {
            mHolePaint.setXfermode(null);
        }
    }

    /**
     * Returns true if the hole in the center of the PieChart is transparent,
     * false if not.
     *
     * @return true if hole is transparent.
     */
    public boolean isHoleTransparent() {
        return mHolePaint.getXfermode() != null;
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

    /**
     * set this to true to draw percent values instead of the actual values
     * 
     * @param enabled
     */
    public void setUsePercentValues(boolean enabled) {
        mUsePercentValues = enabled;
    }

    /**
     * returns true if drawing percent values is enabled
     * 
     * @return
     */
    public boolean isUsePercentValuesEnabled() {
        return mUsePercentValues;
    }

    /**
     * set this to true to draw the x-value text into the pie slices
     * 
     * @param enabled
     */
    public void setDrawXValues(boolean enabled) {
        mDrawXVals = enabled;
    }

    /**
     * returns true if drawing x-values is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawXValuesEnabled() {
        return mDrawXVals;
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
        mCenterTextPaint.setTypeface(t);
    }

    /**
     * Sets the size of the center text of the piechart.
     * 
     * @param size
     */
    public void setCenterTextSize(float size) {
        mCenterTextPaint.setTextSize(Utils.convertDpToPixel(size));
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

    /**
     * sets the radius of the transparent circle that is drawn next to the hole
     * in the piechart in percent of the maximum radius (max = the radius of the
     * whole chart), default 55% -> means 5% larger than the center-hole by
     * default
     * 
     * @param percent
     */
    public void setTransparentCircleRadius(final float percent) {
        mTransparentCircleRadius = percent;
    }

    @Override
    public void setPaint(Paint p, int which) {
        super.setPaint(p, which);

        switch (which) {
            case PAINT_HOLE:
                mHolePaint = p;
                break;
            case PAINT_CENTER_TEXT:
                mCenterTextPaint = p;
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
                return mHolePaint;
            case PAINT_CENTER_TEXT:
                return mCenterTextPaint;
        }

        return null;
    }
}
