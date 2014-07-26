
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.PieChartTouchListener;
import com.github.mikephil.charting.utils.Utils;

/**
 * View that represents a pie chart.
 * 
 * @author Philipp Jahoda
 */
public class PieChart extends Chart {

    /**
     * rect object that represents the bounds of the piechart, needed for
     * drawing the circle
     */
    private RectF mCircleBox;

    /** holds the current rotation angle of the chart */
    private float mChartAngle = 0f;

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
    private String mCenterText = null;

    /** indicates the selection distance of a pie slice */
    private float mShift = 20f;

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
     * paint for the hole in the center of the pie chart
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

        // piechart has no offsets
        mOffsetTop = 0;
        mOffsetBottom = 0;
        mOffsetLeft = 0;
        mOffsetRight = 0;

        mShift = Utils.convertDpToPixel(mShift);

        mHolePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHolePaint.setColor(Color.WHITE);

        mCenterTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterTextPaint.setColor(mColorDarkBlue);
        mCenterTextPaint.setTextSize(Utils.convertDpToPixel(12f));
        mCenterTextPaint.setTextAlign(Align.CENTER);

        mValuePaint.setTextSize(Utils.convertDpToPixel(13f));
        mValuePaint.setColor(Color.WHITE);
        mValuePaint.setTextAlign(Align.CENTER);

        mListener = new PieChartTouchListener(this);

        // for the piechart, drawing values is enabled
        mDrawYValues = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDataNotSet)
            return;

        long starttime = System.currentTimeMillis();

        drawHighlights();

        drawData();

        drawAdditional();

        drawValues();

        drawDescription();

        drawCenterText();

        canvas.drawBitmap(mDrawBitmap, 0, 0, mDrawPaint);

        Log.i(LOG_TAG, "DrawTime: " + (System.currentTimeMillis() - starttime) + " ms");
    }

    /**
     * does all necessary preparations, needed when data is changed or flags
     * that effect the data are changed
     */
    @Override
    public void prepare() {

        if (mDataNotSet)
            return;

        calcMinMax(false);

        if (mCenterText == null)
            mCenterText = "Total Value\n" + (int) getYValueSum();

        // calculate how many digits are needed
        calcFormats();

        // prepareMatrix();

        // Log.i(LOG_TAG, "xVals: " + mXVals.size() + ", yVals: " +
        // mYVals.size());
    }

    @Override
    public void notifyDataSetChanged() {
        // TODO
    }

    @Override
    public void calculateOffsets() {
        // TODO Auto-generated method stub

    }

    /** the decimalformat responsible for formatting the values in the chart */
    protected DecimalFormat mFormatValue = null;

    /**
     * calculates the required number of digits for the y-legend and for the
     * values that might be drawn in the chart (if enabled)
     */
    protected void calcFormats() {

        // -1 means calculate digits
        if (mValueDigitsToUse == -1)
            mValueFormatDigits = Utils.getPieFormatDigits(mDeltaY);
        else
            mValueFormatDigits = mValueDigitsToUse;

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < mValueFormatDigits; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        mFormatValue = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mListener.onTouch(this, event);
    }

    /** the angle where the dragging started */
    private float mStartAngle = 0f;

    /**
     * sets the starting angle of the rotation, this is only used by the touch
     * listener, x and y is the touch position
     * 
     * @param x
     * @param y
     */
    public void setStartAngle(float x, float y) {

        mStartAngle = getAngleForPoint(x, y);

        // take the current angle into consideration when starting a new drag
        mStartAngle -= mChartAngle;
    }

    /**
     * updates the view rotation depending on the given touch position, also
     * takes the starting angle into consideration
     * 
     * @param x
     * @param y
     */
    public void updateRotation(float x, float y) {

        mChartAngle = getAngleForPoint(x, y);

        // take the offset into consideration
        mChartAngle -= mStartAngle;

        // keep the angle >= 0 and <= 360
        mChartAngle = (mChartAngle + 360f) % 360f;
    }

    @Override
    protected void prepareContentRect() {
        super.prepareContentRect();

        int width = mContentRect.width() + mOffsetLeft + mOffsetRight;
        int height = mContentRect.height() + mOffsetTop + mOffsetBottom;

        float diameter = getDiameter();

        // create the circle box that will contain the pie-chart (the bounds of
        // the pie-chart)
        mCircleBox = new RectF(width / 2 - diameter / 2 + mShift, height / 2 - diameter / 2
                + mShift + mOffsetTop,
                width / 2 + diameter / 2 - mShift, height / 2 + diameter / 2 - mOffsetBottom
                        - mShift);
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

        mDrawAngles = new float[mCurrentData.getYValCount()];
        mAbsoluteAngles = new float[mCurrentData.getYValCount()];

        ArrayList<DataSet> dataSets = mCurrentData.getDataSets();

        int cnt = 0;

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            DataSet set = dataSets.get(i);
            ArrayList<Entry> entries = set.getYVals();

            for (int j = 0; j < entries.size(); j++) {

                mDrawAngles[cnt] = calcAngle(entries.get(j).getVal());

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
                if (xIndex >= mDrawAngles.length || xIndex > mDeltaX)
                    continue;

                if (xIndex == 0)
                    angle = mChartAngle;
                else
                    angle = mChartAngle + mAbsoluteAngles[xIndex - 1];

                float sliceDegrees = mDrawAngles[xIndex];

                float shiftangle = (float) Math.toRadians(angle + sliceDegrees / 2f);

                float xShift = mShift * (float) Math.cos(shiftangle);
                float yShift = mShift * (float) Math.sin(shiftangle);

                RectF highlighted = new RectF(mCircleBox.left + xShift, mCircleBox.top + yShift,
                        mCircleBox.right
                                + xShift, mCircleBox.bottom + yShift);

                DataSet set = mCurrentData.getDataSetByIndex(mIndicesToHightlight[i]
                        .getDataSetIndex());

                int color = mCt.getDataSetColor(mIndicesToHightlight[i].getDataSetIndex(),
                        set.getIndexInEntries(xIndex));

                mRenderPaint.setColor(color);

                // redefine the rect that contains the arc so that the
                // highlighted pie is not cut off
                mDrawCanvas.drawArc(highlighted, angle, sliceDegrees, true, mRenderPaint);
            }
        }
    }

    @Override
    protected void drawData() {

        float angle = mChartAngle;

        ArrayList<DataSet> dataSets = mCurrentData.getDataSets();

        int cnt = 0;

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            DataSet dataSet = dataSets.get(i);
            ArrayList<Entry> entries = dataSet.getYVals();

            // Get the colors for the DataSet at the current index. If the index
            // is out of bounds, reuse DataSet colors.
            ArrayList<Integer> colors = mCt.getDataSetColors(i % mCt.getColors().size());

            for (int j = 0; j < entries.size(); j++) {

                float newangle = mDrawAngles[cnt];

                if (!needsHighlight(entries.get(j).getXIndex(), i)) {

                    mRenderPaint.setColor(colors.get(j % colors.size()));
                    mDrawCanvas.drawArc(mCircleBox, angle, newangle, true, mRenderPaint);
                }

                angle += newangle;
                cnt++;
            }
        }
    }

    /**
     * draws the hole in the center of the chart
     */
    private void drawHole() {

        if (mDrawHole) {

            mDrawCanvas.drawCircle(mContentRect.width() / 2, mContentRect.height() / 2,
                    getDiameter() / 4, mHolePaint);
        }
    }

    /**
     * draws the description text in the center of the pie chart makes most
     * sense when center-hole is enabled
     */
    private void drawCenterText() {

        if (mDrawCenterText) {

            PointF c = getCenter();

            // get all lines from the text
            String[] lines = mCenterText.split("\n");
            
            // calculate the height for each line
            float lineHeight = Utils.calcTextHeight(mCenterTextPaint, lines[0]) * 1.2f;
            
            float totalheight = lineHeight * lines.length;

            int cnt = lines.length;
            for (int i = 0; i < lines.length; i++) {
                
                String line = lines[lines.length - i - 1];
                
                mDrawCanvas.drawText(line, c.x, c.y
                        + lineHeight * cnt - lineHeight / 2 - totalheight * 0.45f,
                        mCenterTextPaint);
                cnt--;
            }
        }
    }

    @Override
    protected void drawValues() {

        // if neither xvals nor yvals are drawn, return
        if (!mDrawXVals && !mDrawYValues)
            return;

        PointF center = getCenter();

        float off = mCircleBox.width() / 8;

        // increase offset if there is no hole
        if (!mDrawHole)
            off += off / 2;

        // get the radius
        float r = mCircleBox.width() / 2 - off; // offset to keep things inside
                                                // the chart

        ArrayList<DataSet> dataSets = mCurrentData.getDataSets();

        int cnt = 0;

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            DataSet dataSet = dataSets.get(i);
            ArrayList<Entry> entries = dataSet.getYVals();

            for (int j = 0; j < entries.size(); j++) {

                // offset needed to center the drawn text in the slice
                float offset = mDrawAngles[cnt] / 2;

                // calculate the text position
                float x = (float) (r
                        * Math.cos(Math.toRadians(mChartAngle + mAbsoluteAngles[cnt] - offset)) + center.x);
                float y = (float) (r
                        * Math.sin(Math.toRadians(mChartAngle + mAbsoluteAngles[cnt] - offset)) + center.y);

                // if (y > center.y) {
                // y += 10;
                // x += 3;
                // }

                String val = "";
                float value = entries.get(j).getVal();

                if (mUsePercentValues)
                    val = mFormatValue.format(getPercentOfTotal(value)) + " %";
                else
                    val = mFormatValue.format(value);

                // draw everything, depending on settings
                if (mDrawXVals && mDrawYValues) {

                    // use ascent and descent to calculate the new line
                    // position,
                    // 1.6f is the line spacing
                    float lineHeight = (mValuePaint.ascent() + mValuePaint.descent()) * 1.6f;
                    y -= lineHeight / 2;

                    mDrawCanvas.drawText(val, x, y, mValuePaint);
                    mDrawCanvas.drawText(mCurrentData.getXVals().get(j), x, y + lineHeight,
                            mValuePaint);

                } else if (mDrawXVals && !mDrawYValues) {
                    mDrawCanvas.drawText(mCurrentData.getXVals().get(j), x, y, mValuePaint);
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
        return value / mCurrentData.getYValueSum() * 360f;
    }

    /**
     * returns the pie index for the pie at the given angle
     * 
     * @param angle
     * @return
     */
    public int getIndexForAngle(float angle) {

        // take the current angle of the chart into consideration
        float a = (angle - mChartAngle + 360) % 360f;

        for (int i = 0; i < mAbsoluteAngles.length; i++) {
            if (mAbsoluteAngles[i] > a)
                return i;
        }

        return -1; // return -1 if no index found
    }

    /**
     * returns the index of the DataSet this x-index belongs to.
     * 
     * @param xIndex
     * @return
     */
    public int getDataSetIndexForIndex(int xIndex) {

        ArrayList<DataSet> sets = mCurrentData.getDataSets();

        for (int i = 0; i < sets.size(); i++) {
            if (sets.get(i).getEntryForXIndex(xIndex) != null)
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
     * set a new starting angle for the pie chart (0-360) default is 0° -->
     * right side (EAST)
     * 
     * @param angle
     */
    public void setStartAngle(float angle) {
        mChartAngle = angle;
    }

    /**
     * gets the current rotation angle of the pie chart
     * 
     * @return
     */
    public float getCurrentRotation() {
        return mChartAngle;
    }

    /**
     * sets the distance of the highlighted value to the piechart default 20f
     * 
     * @param shift
     */
    public void setShift(float shift) {
        mShift = Utils.convertDpToPixel(shift);
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

    /**
     * returns the radius of the pie-chart
     * 
     * @return
     */
    public float getRadius() {
        if (mCircleBox == null)
            return 0;
        else
            return mCircleBox.width() / 2f;
    }

    /**
     * returns the diameter of the pie-chart
     * 
     * @return
     */
    public float getDiameter() {
        if (mContentRect == null)
            return 0;
        else
            return Math.min(mContentRect.width(), mContentRect.height());
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
     * returns the angle relative to the chart center for the given point on the
     * chart in degrees. The angle is always between 0 and 360°, 0° is EAST
     * 
     * @param x
     * @param y
     * @return
     */
    public float getAngleForPoint(float x, float y) {

        PointF c = getCenter();

        double tx = x - c.x, ty = y - c.y;
        double length = Math.sqrt(tx * tx + ty * ty);
        double r = Math.acos(ty / length);

        float angle = (float) Math.toDegrees(r);

        if (x > getCenter().x)
            angle = 360f - angle;

        // add 90° because chart starts EAST
        angle = angle + 90f;

        // neutralize overflow
        if (angle > 360f)
            angle = angle - 360f;

        return angle;
    }

    /**
     * returns the distance of a certain point on the chart to the center of the
     * piechart
     * 
     * @param x
     * @param y
     * @return
     */
    public float distanceToCenter(float x, float y) {

        PointF c = getCenter();

        float dist = 0f;

        float xDist = 0f;
        float yDist = 0f;

        if (x > c.x) {
            xDist = x - c.x;
        } else {
            xDist = c.x - x;
        }

        if (y > c.y) {
            yDist = y - c.y;
        } else {
            yDist = c.y - y;
        }

        // pythagoras
        dist = (float) Math.sqrt(Math.pow(xDist, 2.0) + Math.pow(yDist, 2.0));

        return dist;
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
}
