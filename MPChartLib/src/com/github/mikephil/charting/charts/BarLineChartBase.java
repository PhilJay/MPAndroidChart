
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewParent;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.interfaces.OnDrawListener;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Legend.LegendPosition;
import com.github.mikephil.charting.utils.PointD;
import com.github.mikephil.charting.utils.SelInfo;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.YLabels;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Baseclass of LineChart and BarChart.
 * 
 * @author Philipp Jahoda
 */
public abstract class BarLineChartBase extends Chart {

    /**
     * string that is drawn next to the values in the chart, indicating their
     * unit
     */
    protected String mUnit = "";

    /** the maximum number of entried to which values will be drawn */
    protected int mMaxVisibleCount = 100;

    /** minimum scale value on the y-axis */
    private float mMinScaleY = 1f;

    /** minimum scale value on the x-axis */
    private float mMinScaleX = 1f;

    /** contains the current scale factor of the x-axis */
    protected float mScaleX = 1f;

    /** contains the current scale factor of the y-axis */
    protected float mScaleY = 1f;

    /** holds the maximum scale factor of the y-axis, default 7f */
    protected float mMaxScaleY = 7f;

    /**
     * width of the x-axis labels in pixels - this is calculated by the
     * calcTextWidth() method
     */
    protected int mXLabelWidth = 1;

    /**
     * the modulus that indicates if a value at a specified index in an
     * array(list) for the x-axis-labels is drawn or not. If index % modulus ==
     * 0 DRAW, else dont draw.
     */
    protected int mXAxisLabelModulus = 1;

    /** the number of y-label entries the chart has */
    protected int mYLabelCount = 9;

    /** the width of the grid lines */
    protected float mGridWidth = 1f;

    /** if true, units are drawn next to the values in the chart */
    protected boolean mDrawUnitInChart = false;

    /** indicates if the top y-label entry is drawn or not */
    private boolean mDrawTopYLabelEntry = true;

    /**
     * flag that indicates if pinch-zoom is enabled. if true, both x and y axis
     * can be scaled with 2 fingers, if false, x and y axis can be scaled
     * separately
     */
    protected boolean mPinchZoomEnabled = false;

    /**
     * if true, units are drawn next to the values of the axis labels
     */
    protected boolean mDrawUnitsInLabels = true;

    /** if true, x-axis label text is centered */
    protected boolean mCenterXAxisLabels = false;

    /**
     * if set to true, the x-axis label entries will adjust themselves when
     * scaling the graph
     */
    protected boolean mAdjustXAxisLabels = true;

    /** if true, dragging / scaling is enabled for the chart */
    protected boolean mDragEnabled = true;

    /** if true, the y range is predefined */
    protected boolean mFixedYValues = false;

    /** if true, the y-label entries will always start at zero */
    protected boolean mStartAtZero = true;

    /** if true, data filtering is enabled */
    protected boolean mFilterData = false;

    /** paint object for the grid lines */
    protected Paint mGridPaint;

    /** paint object for the (by default) lightgrey background of the grid */
    protected Paint mGridBackgroundPaint;

    /** paint for the line surrounding the chart */
    protected Paint mBorderPaint;

    /** paint for the x-label values */
    protected Paint mXLabelPaint;

    /** paint for the y-label values */
    protected Paint mYLabelPaint;

    /** paint used for highlighting values */
    protected Paint mHighlightPaint;

    /**
     * if set to true, the highlight indicator (lines for linechart, dark bar
     * for barchart) will be drawn upon selecting values.
     */
    protected boolean mHighLightIndicatorEnabled = true;

    /**
     * boolean to indicate if user drawing on chart should automatically be
     * finished
     */
    protected boolean mAutoFinishDrawing;

    /** flag indicating if the vertical grid should be drawn or not */
    protected boolean mDrawVerticalGrid = true;

    /** flag indicating if the horizontal grid should be drawn or not */
    protected boolean mDrawHorizontalGrid = true;

    /** flag indicating if the y-labels should be drawn or not */
    protected boolean mDrawYLabels = true;

    /** flag indicating if the x-labels should be drawn or not */
    protected boolean mDrawXLabels = true;

    /** flag indicating if the chart border rectangle should be drawn or not */
    protected boolean mDrawBorder = true;

    /** flag indicating if the grid background should be drawn or not */
    protected boolean mDrawGridBackground = true;
    
    /** the listener for user drawing on the chart */
    protected OnDrawListener mDrawListener;

    /**
     * the object representing the labels on the y-axis, this object is prepared
     * in the pepareYLabels() method
     */
    protected YLabels mYLabels = new YLabels();

    /** the approximator object used for data filtering */
    private Approximator mApproximator;

    public BarLineChartBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BarLineChartBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BarLineChartBase(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();

        mListener = new BarLineChartTouchListener(this, mMatrixTouch);

        mXLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mXLabelPaint.setColor(Color.BLACK);
        mXLabelPaint.setTextAlign(Align.CENTER);
        mXLabelPaint.setTextSize(Utils.convertDpToPixel(10f));

        mYLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mYLabelPaint.setColor(Color.BLACK);
        mYLabelPaint.setTextAlign(Align.RIGHT);
        mYLabelPaint.setTextSize(Utils.convertDpToPixel(10f));

        mGridPaint = new Paint();
        mGridPaint.setColor(Color.GRAY);
        mGridPaint.setStrokeWidth(mGridWidth);
        mGridPaint.setStyle(Style.STROKE);
        mGridPaint.setAlpha(90);

        mBorderPaint = new Paint();
        mBorderPaint.setColor(Color.BLACK);
        mBorderPaint.setStrokeWidth(mGridWidth * 2f);
        mBorderPaint.setStyle(Style.STROKE);

        mGridBackgroundPaint = new Paint();
        mGridBackgroundPaint.setStyle(Style.FILL);
        // mGridBackgroundPaint.setColor(Color.WHITE);
        mGridBackgroundPaint.setColor(Color.rgb(240, 240, 240)); // light
        // grey

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDataNotSet)
            return;

        long starttime = System.currentTimeMillis();

        // if data filtering is enabled
        if (mFilterData) {
            mCurrentData = getFilteredData();

            Log.i(LOG_TAG, "FilterTime: " + (System.currentTimeMillis() -
                    starttime) + " ms");
            starttime = System.currentTimeMillis();
        } else {
            mCurrentData = getDataOriginal();
            // Log.i(LOG_TAG, "Filtering disabled.");
        }

        if (mAdjustXAxisLabels)
            calcModulus();

        // execute all drawing commands
        drawGridBackground();
        drawBorder();

        prepareYLabels();

        // make sure the graph values and grid cannot be drawn outside the
        // content-rect
        int clipRestoreCount = mDrawCanvas.save();
        mDrawCanvas.clipRect(mContentRect);

        drawHorizontalGrid();
        drawVerticalGrid();

        drawData();
        drawHighlights();

        // Removes clipping rectangle
        mDrawCanvas.restoreToCount(clipRestoreCount);

        drawAdditional();

        drawXLabels();

        drawYLabels();

        drawValues();

        drawLegend();

        drawMarkers();

        drawDescription();

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

        calcMinMax(mFixedYValues);

        prepareXLabels();

        // calculate how many digits are needed
        calcFormats();

        prepareLegend();
    }

    @Override
    public void notifyDataSetChanged() {
        if (!mFixedYValues) {
            prepare();
        } else {
            calcMinMax(mFixedYValues);
        }
    }

    @Override
    public void calculateOffsets() {

        if (mDrawLegend) {
            if (mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART) {

                mOffsetRight = mLegend.getMaximumEntryLength(mLegendLabelPaint);
                mLegendLabelPaint.setTextAlign(Align.LEFT);

            } else if (mLegend.getPosition() == LegendPosition.BELOW_CHART_LEFT
                    || mLegend.getPosition() == LegendPosition.BELOW_CHART_RIGHT) {
                mOffsetBottom = (int) (mLegendLabelPaint.getTextSize() * 3.5f);
            }
        }

        mOffsetLeft = Utils.calcTextWidth(mYLabelPaint, (int) mDeltaY + ".0000" + mUnit);

        prepareContentRect();

        float scaleX = (float) ((getWidth() - mOffsetLeft - mOffsetRight) / mDeltaX);
        float scaleY = (float) ((getHeight() - mOffsetBottom - mOffsetTop) / mDeltaY);

        Matrix val = new Matrix();
        val.postTranslate(0, -mYChartMin);
        val.postScale(scaleX, -scaleY);

        mMatrixValueToPx.set(val);

        Matrix offset = new Matrix();
        offset.postTranslate(mOffsetLeft, getHeight() - mOffsetBottom);

        mMatrixOffset.set(offset);
    }

    /**
     * calculates the modulus for x-labels and grid
     */
    protected void calcModulus() {

        float[] values = new float[9];
        mMatrixTouch.getValues(values);

        mXAxisLabelModulus = (int) Math.ceil((mCurrentData.getXValCount() * mXLabelWidth)
                / (mContentRect.width() * values[Matrix.MSCALE_X]));
    }

    /** the decimalformat responsible for formatting the values in the chart */
    protected DecimalFormat mFormatValue = null;

    /** the number of digits the y-labels are formatted with */
    protected int mYLabelFormatDigits = -1;

    /**
     * calculates the required number of digits for the y-labels and for the
     * values that might be drawn in the chart (if enabled)
     */
    protected void calcFormats() {

        if (mValueDigitsToUse == -1)
            mValueFormatDigits = Utils.getFormatDigits(mDeltaY);
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
    protected void calcMinMax(boolean fixedValues) {
        super.calcMinMax(fixedValues); // calc min and max in the super class

        // additional handling for space (default 10% space), spacing only
        // applies with non-rounded y-label
        float space = mDeltaY / 100f * 10f;

        if (mStartAtZero) {
            mYChartMin = 0;
        } else {
            mYChartMin = mYChartMin - space;
        }

        // calc delta
        mYChartMax = mYChartMax + space;
        mDeltaY = Math.abs(mYChartMax - mYChartMin);
    }

    /**
     * setup the x-axis labels
     */
    protected void prepareXLabels() {

        StringBuffer a = new StringBuffer();

        int length = (int) (((float) (mCurrentData.getXVals().get(0).length() + mCurrentData
                .getXVals()
                .get(mCurrentData.getXValCount() - 1)
                .length())));

        if (mCurrentData.getXVals().get(0).length() <= 3)
            length *= 2;

        for (int i = 0; i < length; i++) {
            a.append("h");
        }

        mXLabelWidth = Utils.calcTextWidth(mXLabelPaint, a.toString());
    }

    /**
     * Sets up the y-axis labels. Computes the desired number of labels between
     * the two given extremes. Unlike the papareXLabels() method, this method
     * needs to be called upon every refresh of the view.
     * 
     * @return
     */
    private void prepareYLabels() {

        // calculate the currently visible extremes
        PointD p1 = getValuesByTouchPoint(mContentRect.left, mContentRect.top);
        PointD p2 = getValuesByTouchPoint(mContentRect.left, mContentRect.bottom);

        // update the current chart dimensions on the y-axis
        mYChartMin = (float) p2.y;
        mYChartMax = (float) p1.y;

        float yMin = mYChartMin;
        float yMax = mYChartMax;

        double range = yMax - yMin;
        if (mYLabelCount == 0 || range <= 0) {
            mYLabels.mEntries = new float[] {};
            mYLabels.mEntryCount = 0;
            return;
        }

        double rawInterval = range / mYLabelCount;
        double interval = Utils.roundToNextSignificant(rawInterval);
        double intervalMagnitude = Math.pow(10, (int) Math.log10(interval));
        int intervalSigDigit = (int) (interval / intervalMagnitude);
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or
            // 90
            interval = Math.floor(10 * intervalMagnitude);
        }

        double first = Math.ceil(yMin / interval) * interval;
        double last = Math.nextUp(Math.floor(yMax / interval) * interval);

        double f;
        int i;
        int n = 0;
        for (f = first; f <= last; f += interval) {
            ++n;
        }

        mYLabels.mEntryCount = n;

        if (mYLabels.mEntries.length < n) {
            // Ensure stops contains at least numStops elements.
            mYLabels.mEntries = new float[n];
        }

        for (f = first, i = 0; i < n; f += interval, ++i) {
            mYLabels.mEntries[i] = (float) f;
        }

        if (interval < 1) {
            mYLabels.mDecimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            mYLabels.mDecimals = 0;
        }
    }

    /**
     * draws the x-axis labels to the screen
     */
    protected void drawXLabels() {

        if (!mDrawXLabels)
            return;

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[] {
                0f, 0f
        };

        for (int i = 0; i < mCurrentData.getXValCount(); i++) {

            if (i % mXAxisLabelModulus == 0) {

                position[0] = i;

                // center the text
                if (mCenterXAxisLabels)
                    position[0] += 0.5f;

                transformPointArray(position);

                if (position[0] >= mOffsetLeft && position[0] <= getWidth() - mOffsetRight + 10) {

                    mDrawCanvas.drawText(mCurrentData.getXVals().get(i), position[0],
                            mOffsetTop - 5,
                            mXLabelPaint);
                }
            }
        }
    }

    /**
     * draws the y-axis labels to the screen
     */
    protected void drawYLabels() {

        if (!mDrawYLabels)
            return;

        float[] positions = new float[mYLabels.mEntryCount * 2];

        for (int i = 0; i < positions.length; i += 2) {
            // only fill y values, x values are not needed since the y-labels
            // are
            // static on the x-axis
            positions[i + 1] = mYLabels.mEntries[i / 2];
        }

        transformPointArray(positions);

        float xPos = mOffsetLeft - 10;

        for (int i = 0; i < mYLabels.mEntryCount; i++) {

            String text = Utils.formatNumber(mYLabels.mEntries[i], mYLabels.mDecimals,
                    mSeparateTousands);

            if (!mDrawTopYLabelEntry && i >= mYLabels.mEntryCount - 1)
                return;

            if (mDrawUnitsInLabels) {
                mDrawCanvas.drawText(text + mUnit, xPos, positions[i * 2 + 1],
                        mYLabelPaint);
            } else {
                mDrawCanvas.drawText(text, xPos, positions[i * 2 + 1], mYLabelPaint);
            }
        }
    }

    /** enums for all different border styles */
    public enum BorderStyle {
        LEFT, RIGHT, TOP, BOTTOM
    }

    /**
     * array that holds positions where to draw the chart border lines
     */
    private BorderStyle[] mBorderStyles = new BorderStyle[] {
            BorderStyle.BOTTOM
    };

    /**
     * draws a line that surrounds the chart
     */
    protected void drawBorder() {

        if (!mDrawBorder || mBorderStyles == null)
            return;

        for (int i = 0; i < mBorderStyles.length; i++) {

            switch (mBorderStyles[i]) {
                case LEFT:
                    mDrawCanvas.drawLine(mOffsetLeft, mOffsetTop, mOffsetLeft, getHeight()
                            - mOffsetBottom, mBorderPaint);
                    break;
                case RIGHT:
                    mDrawCanvas.drawLine(getWidth() - mOffsetRight, mOffsetTop, getWidth()
                            - mOffsetRight, getHeight()
                            - mOffsetBottom, mBorderPaint);
                    break;
                case TOP:
                    mDrawCanvas.drawLine(mOffsetLeft, mOffsetTop, getWidth() - mOffsetRight,
                            mOffsetTop, mBorderPaint);
                    break;
                case BOTTOM:
                    mDrawCanvas.drawLine(mOffsetLeft, getHeight()
                            - mOffsetBottom, getWidth() - mOffsetRight, getHeight()
                            - mOffsetBottom, mBorderPaint);
                    break;
            }
        }
    }

    /**
     * draws the grid background
     */
    protected void drawGridBackground() {

        if (!mDrawGridBackground)
            return;

        Rect gridBackground = new Rect(mOffsetLeft + 1, mOffsetTop + 1, getWidth() - mOffsetRight,
                getHeight() - mOffsetBottom);

        // draw the grid background
        mDrawCanvas.drawRect(gridBackground, mGridBackgroundPaint);
    }

    /**
     * draws the horizontal grid
     */
    protected void drawHorizontalGrid() {

        if (!mDrawHorizontalGrid)
            return;

        // create a new path object only once and use reset() instead of
        // unnecessary allocations
        Path p = new Path();

        // draw the horizontal grid
        for (int i = 0; i < mYLabels.mEntryCount; i++) {

            p.reset();
            p.moveTo(0, mYLabels.mEntries[i]);
            p.lineTo(mDeltaX, mYLabels.mEntries[i]);

            transformPath(p);

            mDrawCanvas.drawPath(p, mGridPaint);
        }
    }

    /**
     * draws the vertical grid
     */
    protected void drawVerticalGrid() {

        if (!mDrawVerticalGrid)
            return;

        float[] position = new float[] {
                0f, 0f
        };

        for (int i = 0; i < mCurrentData.getXValCount(); i++) {

            if (i % mXAxisLabelModulus == 0) {

                position[0] = i;

                transformPointArray(position);

                if (position[0] >= mOffsetLeft && position[0] <= getWidth()) {

                    mDrawCanvas.drawLine(position[0], mOffsetTop, position[0], getHeight()
                            - mOffsetBottom, mGridPaint);
                }
            }
        }
    }

    /**
     * returns true if the specified point (x-axis) exceeds the limits of what
     * is visible to the right side
     * 
     * @param v
     * @return
     */
    protected boolean isOffContentRight(float p) {
        if (p > mContentRect.right)
            return true;
        else
            return false;
    }

    /**
     * returns true if the specified point (x-axis) exceeds the limits of what
     * is visible to the left side
     * 
     * @param v
     * @return
     */
    protected boolean isOffContentLeft(float p) {
        if (p < mContentRect.left)
            return true;
        else
            return false;
    }

    /**
     * returns true if the specified point (y-axis) exceeds the limits of what
     * is visible on the top
     * 
     * @param v
     * @return
     */
    protected boolean isOffContentTop(float p) {
        if (p < mContentRect.top)
            return true;
        else
            return false;
    }

    /**
     * returns true if the specified point (y-axis) exceeds the limits of what
     * is visible on the bottom
     * 
     * @param v
     * @return
     */
    protected boolean isOffContentBottom(float p) {
        if (p > mContentRect.bottom)
            return true;
        else
            return false;
    }

    /**
     * ################ ################ ################ ################
     */
    /** CODE BELOW THIS RELATED TO SCALING AND GESTURES */

    /**
     * disables intercept touchevents
     */
    public void disableScroll() {
        ViewParent parent = getParent();
        parent.requestDisallowInterceptTouchEvent(true);
    }

    /**
     * enables intercept touchevents
     */
    public void enableScroll() {
        ViewParent parent = getParent();
        parent.requestDisallowInterceptTouchEvent(false);
    }

    /**
     * Zooms in by 1.4f, x and y are the coordinates (in pixels) of the zoom
     * center.
     * 
     * @param x
     * @param y
     */
    public void zoomIn(float x, float y) {

        Matrix save = new Matrix();
        save.set(mMatrixTouch);

        save.postScale(1.4f, 1.4f, x, y);

        refreshTouch(save);
    }

    /**
     * Zooms out by 0.7f, x and y are the coordinates (in pixels) of the zoom
     * center.
     */
    public void zoomOut(float x, float y) {

        Matrix save = new Matrix();
        save.set(mMatrixTouch);

        save.postScale(0.7f, 0.7f, x, y);

        refreshTouch(save);
    }

    /**
     * Zooms in or out by the given scale factor. x and y are the coordinates
     * (in pixels) of the zoom center.
     * 
     * @param scaleX if < 1f --> zoom out, if > 1f --> zoom in
     * @param scaleY if < 1f --> zoom out, if > 1f --> zoom in
     * @param x
     * @param y
     */
    public void zoom(float scaleX, float scaleY, float x, float y) {

        Matrix save = new Matrix();
        save.set(mMatrixTouch);

        // Log.i(LOG_TAG, "Zooming, x: " + x + ", y: " + y);

        save.postScale(scaleX, scaleY, x, -y);

        refreshTouch(save);
    }

    /**
     * Centers the viewport around the specified x-index and the specified
     * y-value in the chart. Centering the viewport outside the bounds of the
     * chart is not possible. Makes most sense in combination with the
     * setScaleMinima(...) method. SHOULD BE CALLED AFTER setting data for the
     * chart.
     * 
     * @param xIndex the index on the x-axis to center to
     * @param yVal the value ont he y-axis to center to
     */
    public synchronized void centerViewPort(final int xIndex, final float yVal) {

        // the post makes it possible that this call waits until the view has
        // finisted setting up
        post(new Runnable() {

            @Override
            public void run() {

                float indicesInView = mDeltaX / mScaleX;
                float valsInView = mDeltaY / mScaleY;

                float[] pts = new float[] {
                        xIndex - indicesInView / 2f, yVal + valsInView / 2f
                };

                Matrix save = new Matrix();
                save.set(mMatrixTouch);

                transformPointArray(pts);

                final float x = -pts[0] + getOffsetLeft();
                final float y = -pts[1] + getOffsetBottom();

                save.postTranslate(x, y);

                refreshTouchNoInvalidate(save);

                // Log.i(LOG_TAG, "ViewPort centered, xIndex: " + xIndex +
                // ", yVal: " + yVal
                // + ", transX: " + x + ", transY: " + y);
            }
        });
    }

    /**
     * call this method to refresh the graph with a given touch matrix
     * 
     * @param newTouchMatrix
     * @return
     */
    public Matrix refreshTouch(Matrix newTouchMatrix) {
        mMatrixTouch.set(newTouchMatrix);

        // make sure scale and translation are within their bounds
        limitTransAndScale(mMatrixTouch);

        // redraw
        invalidate();

        newTouchMatrix.set(mMatrixTouch);
        return newTouchMatrix;
    }

    /**
     * call this method to refresh the graph with a given touch matrix without
     * calling invalidate()
     * 
     * @param newTouchMatrix
     * @return
     */
    public Matrix refreshTouchNoInvalidate(Matrix newTouchMatrix) {
        mMatrixTouch.set(newTouchMatrix);

        // make sure scale and translation are within their bounds
        limitTransAndScale(mMatrixTouch);

        newTouchMatrix.set(mMatrixTouch);
        return newTouchMatrix;
    }

    /**
     * limits the maximum scale and X translation of the given matrix
     * 
     * @param matrix
     */
    protected void limitTransAndScale(Matrix matrix) {

        float[] vals = new float[9];
        matrix.getValues(vals);

        float curTransX = vals[Matrix.MTRANS_X];
        float curScaleX = vals[Matrix.MSCALE_X];

        float curTransY = vals[Matrix.MTRANS_Y];
        float curScaleY = vals[Matrix.MSCALE_Y];

        // Log.i(LOG_TAG, "curTransX: " + curTransX + ", curScaleX: " +
        // curScaleX);
        // Log.i(LOG_TAG, "curTransY: " + curTransY + ", curScaleY: " +
        // curScaleY);

        // min scale-x is 1f
        mScaleX = Math.max(mMinScaleX, Math.min(getMaxScaleX(), curScaleX));

        // min scale-y is 1f
        mScaleY = Math.max(mMinScaleY, Math.min(getMaxScaleY(), curScaleY));

        if (mContentRect == null)
            return;

        float maxTransX = -(float) mContentRect.width() * (mScaleX - 1f);
        float newTransX = Math.min(Math.max(curTransX, maxTransX), 0f);

        float maxTransY = (float) mContentRect.height() * (mScaleY - 1f);
        float newTransY = Math.max(Math.min(curTransY, maxTransY), 0f);

        // Log.i(LOG_TAG, "scale-X: " + mScaleX + ", maxTransX: " + maxTransX +
        // ", newTransX: "
        // + newTransX);
        // Log.i(LOG_TAG, "scale-Y: " + mScaleY + ", maxTransY: " + maxTransY +
        // ", newTransY: "
        // + newTransY);

        vals[Matrix.MTRANS_X] = newTransX;
        vals[Matrix.MSCALE_X] = mScaleX;

        vals[Matrix.MTRANS_Y] = newTransY;
        vals[Matrix.MSCALE_Y] = mScaleY;

        matrix.setValues(vals);
    }

    /**
     * ################ ################ ################ ################
     */
    /** CODE BELOW IS GETTERS AND SETTERS */

    /**
     * Sets the OnDrawListener
     * 
     * @param drawListener
     */
    public void setOnDrawListener(OnDrawListener drawListener) {
        this.mDrawListener = drawListener;
    }

    /**
     * set if the user should be allowed to draw onto the chart
     * 
     * @param drawingEnabled
     */
    public void setDrawingEnabled(boolean drawingEnabled) {
        if (mListener instanceof BarLineChartTouchListener) {
            ((BarLineChartTouchListener) mListener).setDrawingEnabled(drawingEnabled);
        }
    }

    /**
     * Set to true to auto finish user drawing
     * 
     * @param enabled
     */
    public void setAutoFinish(boolean enabled) {
        this.mAutoFinishDrawing = enabled;
    }

    /**
     * True if auto finish user drawing is enabled
     * 
     * @return
     */
    public boolean isAutoFinishEnabled() {
        return mAutoFinishDrawing;
    }

    /**
     * Gets the OnDrawListener. May be null.
     * 
     * @return
     */
    public OnDrawListener getDrawListener() {
        return mDrawListener;
    }

    /**
     * Sets the minimum scale values for both axes. This limits the extent to
     * which the user can zoom-out. Scale 0.5f means 0.5x zoom (zoomed out by
     * factor 2), scale 0.1f means maximum zoomed out by factor 10, scale 2f
     * means the user cannot zoom out further than 2x zoom, ...
     * 
     * @param scaleXmin
     * @param scaleYmin
     */
    public void setScaleMinima(float scaleXmin, float scaleYmin) {

        mMinScaleX = scaleXmin;
        mMinScaleY = scaleYmin;

        zoom(mMinScaleX, mMinScaleY, 0f, 0f);
    }

    /**
     * set this to true to enable drawing the top y-label entry. Disabling this
     * can be helpful when the top y-label and left x-label interfere with each
     * other. default: true
     * 
     * @param enabled
     */
    public void setDrawTopYLabelEntry(boolean enabled) {
        mDrawTopYLabelEntry = enabled;
    }

    /**
     * Sets the effective range of y-values the chart can display. If this is
     * set, the y-range is fixed and cannot be changed. This means, no
     * recalculation of the bounds of the chart concerning the y-axis will be
     * done when adding new data. To disable this, provide Float.NaN as a
     * parameter or call resetYRange();
     * 
     * @param minY
     * @param maxY
     * @param invalidate if set to true, the chart will redraw itself after
     *            calling this method
     */
    public void setYRange(float minY, float maxY, boolean invalidate) {

        if (Float.isNaN(minY) || Float.isNaN(maxY)) {
            resetYRange(invalidate);
            return;
        }

        mFixedYValues = true;

        mYChartMin = minY;
        mYChartMax = maxY;
        if (minY < 0) {
            mStartAtZero = false;
        }
        mDeltaY = mYChartMax - mYChartMin;

        calcFormats();
        prepareMatrix();
        if (invalidate)
            invalidate();
    }

    /**
     * Resets the previously set y range. If new data is added, the y-range will
     * be recalculated.
     * 
     * @param invalidate if set to true, the chart will redraw itself after
     *            calling this method
     */
    public void resetYRange(boolean invalidate) {
        mFixedYValues = false;
        calcMinMax(mFixedYValues);

        prepareMatrix();
        if (invalidate)
            invalidate();
    }

    /**
     * sets the number of label entries for the y-axis max = 15, min = 3
     * 
     * @param yCount
     */
    public void setYLabelCount(int yCount) {

        if (yCount > 15)
            yCount = 15;
        if (yCount < 3)
            yCount = 3;

        mYLabelCount = yCount;
    }

    /**
     * if set to true, the x-label entries will adjust themselves when scaling
     * the graph default: true
     * 
     * @param enabled
     */
    public void setAdjustXLabels(boolean enabled) {
        mAdjustXAxisLabels = enabled;
    }

    /**
     * returns true if the x-labels adjust themselves when scaling the graph,
     * false if not
     * 
     * @return
     */
    public boolean isAdjustXLabelsEnabled() {
        return mAdjustXAxisLabels;
    }

    /**
     * if this returns true, the chart has a fixed range on the y-axis that is
     * not dependant on the actual data in the chart
     * 
     * @return
     */
    public boolean hasFixedYValues() {
        return mFixedYValues;
    }

    /**
     * sets the color for the grid lines
     * 
     * @param color
     */
    public void setGridColor(int color) {
        mGridPaint.setColor(color);
    }

    /**
     * sets the number of maximum visible drawn values on the chart only active
     * when setDrawValues() is enabled
     * 
     * @param count
     */
    public void setMaxVisibleValueCount(int count) {
        this.mMaxVisibleCount = count;
    }

    /**
     * sets the size of the y-label text in pixels min = 7f, max = 14f
     * 
     * @param size
     */
    public void setYLabelTextSize(float size) {

        if (size > 14f)
            size = 14f;
        if (size < 7f)
            size = 7f;
        mYLabelPaint.setTextSize(Utils.convertDpToPixel(size));
    }

    /**
     * sets the size of the x-label text in pixels min = 7f, max = 14f
     * 
     * @param size
     */
    public void setXLabelTextSize(float size) {

        if (size > 14f)
            size = 14f;
        if (size < 7f)
            size = 7f;

        mXLabelPaint.setTextSize(Utils.convertDpToPixel(size));
    }

    /**
     * If set to true, the highlight indicators (cross of two lines for
     * LineChart and ScatterChart, dark bar overlay for BarChart) that give
     * visual indication that an Entry has been selected will be drawn upon
     * selecting values. This does not depend on the MarkerView. Default: true
     * 
     * @param enabled
     */
    public void setHighlightIndicatorEnabled(boolean enabled) {
        mHighLightIndicatorEnabled = enabled;
    }

    /**
     * enable this to force the y-axis labels to always start at zero
     * 
     * @param enabled
     */
    public void setStartAtZero(boolean enabled) {
        this.mStartAtZero = enabled;
        prepare();
        prepareMatrix();
    }

    /**
     * sets the unit that is drawn next to the values in the chart, e.g. %
     * 
     * @param unit
     */
    public void setUnit(String unit) {
        mUnit = unit;
    }

    /**
     * returns true if the chart is set to start at zero, false otherwise
     * 
     * @return
     */
    public boolean isStartAtZeroEnabled() {
        return mStartAtZero;
    }

    /**
     * if set to true, units are drawn next to values in the chart, default:
     * false
     * 
     * @param enabled
     */
    public void setDrawUnitsInChart(boolean enabled) {
        mDrawUnitInChart = enabled;
    }

    /**
     * if set to true, units are drawn next to y-label values, default: true
     * 
     * @param enabled
     */
    public void setDrawUnitsInYLabel(boolean enabled) {
        mDrawUnitsInLabels = enabled;
    }

    /**
     * set this to true to center the x-label text, default: false
     * 
     * @param enabled
     */
    public void setCenterXLabelText(boolean enabled) {
        mCenterXAxisLabels = enabled;
    }

    /**
     * returns true if the x-label text is centered
     * 
     * @return
     */
    public boolean isXLabelTextCentered() {
        return mCenterXAxisLabels;
    }

    /**
     * sets the width of the grid lines (min 0.1f, max = 3f)
     * 
     * @param width
     */
    public void setGridWidth(float width) {

        if (width < 0.1f)
            width = 0.1f;
        if (width > 3.0f)
            width = 3.0f;
        mGridWidth = width;
    }

    /**
     * set this to true to enable dragging / scaling for the chart
     * 
     * @param enabled
     */
    public void setDragEnabled(boolean enabled) {
        this.mDragEnabled = enabled;
    }

    /**
     * returns true if dragging / scaling is enabled for the chart, false if not
     * 
     * @return
     */
    public boolean isDragEnabled() {
        return mDragEnabled;
    }

    /**
     * if set to true, the vertical grid will be drawn, default: true
     * 
     * @param enabled
     */
    public void setDrawVerticalGrid(boolean enabled) {
        mDrawVerticalGrid = enabled;
    }

    /**
     * if set to true, the horizontal grid will be drawn, default: true
     * 
     * @param enabled
     */
    public void setDrawHorizontalGrid(boolean enabled) {
        mDrawHorizontalGrid = enabled;
    }

    /**
     * returns true if drawing the vertical grid is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawVerticalGridEnabled() {
        return mDrawVerticalGrid;
    }

    /**
     * returns true if drawing the horizontal grid is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawHorizontalGridEnabled() {
        return mDrawHorizontalGrid;
    }

    /**
     * set this to true to draw the border surrounding the chart, default: true
     * 
     * @param enabled
     */
    public void setDrawBorder(boolean enabled) {
        mDrawBorder = enabled;
    }

    /**
     * set this to true to draw the grid background, false if not
     * 
     * @param enabled
     */
    public void setDrawGridBackground(boolean enabled) {
        mDrawGridBackground = enabled;
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
     * set this to true to enable drawing the y-labels, false if not
     * 
     * @param enabled
     */
    public void setDrawYLabels(boolean enabled) {
        mDrawYLabels = enabled;
    }

    /**
     * Sets an array of positions where to draw the chart border lines (e.g. new
     * BorderStyle[] { BorderStyle.BOTTOM })
     * 
     * @param styles
     */
    public void setBorderStyles(BorderStyle[] styles) {
        mBorderStyles = styles;
    }

    /**
     * returns the Highlight object (contains x-index and DataSet index) of the
     * selected value at the given touch point.
     * 
     * @param x
     * @param y
     * @return
     */
    public Highlight getHighlightByTouchPoint(float x, float y) {

        // create an array of the touch-point
        float[] pts = new float[2];
        pts[0] = x;
        pts[1] = y;

        Matrix tmp = new Matrix();

        // invert all matrixes to convert back to the original value
        mMatrixOffset.invert(tmp);
        tmp.mapPoints(pts);

        mMatrixTouch.invert(tmp);
        tmp.mapPoints(pts);

        mMatrixValueToPx.invert(tmp);
        tmp.mapPoints(pts);

        double xTouchVal = pts[0];
        double yTouchVal = pts[1];
        double base = Math.floor(xTouchVal);

        Log.i(LOG_TAG, "touchindex x: " + xTouchVal + ", touchindex y: " + yTouchVal);

        // touch out of chart
        if ((this instanceof LineChart || this instanceof ScatterChart)
                && (xTouchVal < 0 || xTouchVal > mDeltaX))
            return null;
        if (this instanceof BarChart && (xTouchVal < 0 || xTouchVal > mDeltaX + 1))
            return null;

        int xIndex = (int) base;
        int dataSetIndex = 0; // index of the DataSet inside the ChartData
                              // object

        if (this instanceof LineChart || this instanceof ScatterChart) {

            // check if we are more than half of a x-value or not
            if (xTouchVal - base > 0.5) {
                xIndex = (int) base + 1;
            }
        }

        ArrayList<SelInfo> valsAtIndex = getYValsAtIndex(xIndex);

        dataSetIndex = getClosestDataSetIndex(valsAtIndex, (float) yTouchVal);

        if (dataSetIndex == -1)
            return null;

        return new Highlight(xIndex, dataSetIndex);
    }

    /**
     * returns the index of the DataSet that contains the closest value
     * 
     * @param valsAtIndex all the values at a specific index
     * @return
     */
    private int getClosestDataSetIndex(ArrayList<SelInfo> valsAtIndex, float val) {

        int index = -1;
        float distance = Float.MAX_VALUE;

        for (int i = 0; i < valsAtIndex.size(); i++) {

            float cdistance = Math.abs((float) valsAtIndex.get(i).val - val);
            if (cdistance < distance) {
                index = valsAtIndex.get(i).dataSetIndex;
                distance = cdistance;
            }
        }

        Log.i(LOG_TAG, "Closest DataSet index: " + index);

        return index;
    }

    /**
     * Returns the x and y values in the chart at the given touch point
     * (encapsulated in a PointD). This method transforms pixel coordinates to
     * coordinates / values in the chart. This is the opposite method to
     * getPixelsForValues(...).
     * 
     * @param x
     * @param y
     * @return
     */
    public PointD getValuesByTouchPoint(float x, float y) {

        // create an array of the touch-point
        float[] pts = new float[2];
        pts[0] = x;
        pts[1] = y;

        Matrix tmp = new Matrix();

        // invert all matrixes to convert back to the original value
        mMatrixOffset.invert(tmp);
        tmp.mapPoints(pts);

        mMatrixTouch.invert(tmp);
        tmp.mapPoints(pts);

        mMatrixValueToPx.invert(tmp);
        tmp.mapPoints(pts);

        double xTouchVal = pts[0];
        double yTouchVal = pts[1];

        return new PointD(xTouchVal, yTouchVal);
    }

    /**
     * Transforms the given chart values into pixels. This is the opposite
     * method to getValuesByTouchPoint(...).
     * 
     * @param x
     * @param y
     * @return
     */
    public PointD getPixelsForValues(float x, float y) {

        float[] pts = new float[] {
                x, y
        };

        transformPointArray(pts);

        return new PointD(pts[0], pts[1]);
    }

    /**
     * returns the y-value at the given touch position (must not necessarily be
     * a value contained in one of the datasets)
     * 
     * @param x
     * @param y
     * @return
     */
    public float getYValueByTouchPoint(float x, float y) {
        return (float) getValuesByTouchPoint(x, y).y;
    }

    /**
     * returns the Entry object displayed at the touched position of the chart
     * 
     * @param x
     * @param y
     * @return
     */
    public Entry getEntryByTouchPoint(float x, float y) {
        Highlight h = getHighlightByTouchPoint(x, y);
        if (h != null) {
            return mCurrentData.getEntryForHighlight(h);
        }
        return null;
    }

    /**
     * returns the current x-scale factor
     */
    public float getScaleX() {
        return mScaleX;
    }

    /**
     * returns the current y-scale factor
     */
    public float getScaleY() {
        return mScaleY;
    }

    /**
     * calcualtes the maximum x-scale value depending on the number of x-values,
     * maximum scale is numberOfXvals / 2
     * 
     * @return
     */
    public float getMaxScaleX() {
        return mDeltaX / 2f;
    }

    /**
     * Returns the maximum y-scale factor. Default 7f
     * 
     * @return
     */
    public float getMaxScaleY() {
        return mMaxScaleY;
    }

    /**
     * sets the maximum scale factor for the y-axis. Default 7f, min 1f, max 20f
     * 
     * @param factor
     */
    public void setMaxScaleY(float factor) {

        if (factor < 1f)
            factor = 1f;
        if (factor > 20f)
            factor = 20f;

        mMaxScaleY = factor;
    }

    /**
     * sets a typeface for the paint object of the x-labels
     * 
     * @param t
     */
    public void setXLabelTypeface(Typeface t) {
        mXLabelPaint.setTypeface(t);
    }

    /**
     * sets a typeface for the paint object of the y-labels
     * 
     * @param t
     */
    public void setYLabelTypeface(Typeface t) {
        mYLabelPaint.setTypeface(t);
    }

    /**
     * sets a typeface for both x and y-label paints
     * 
     * @param t
     */
    public void setLabelTypeface(Typeface t) {
        setXLabelTypeface(t);
        setYLabelTypeface(t);
    }

    /**
     * Enables data filtering for the chart data, filtering will use the user
     * customized Approximator handed over to this method.
     * 
     * @param a
     */
    public void enableFiltering(Approximator a) {
        mFilterData = true;
        mApproximator = a;
    }

    /**
     * Disables data filtering for the chart.
     */
    public void disableFiltering() {
        mFilterData = false;
    }

    /**
     * returns true if data filtering is enabled, false if not
     * 
     * @return
     */
    public boolean isFilteringEnabled() {
        return mFilterData;
    }

    /**
     * if set to true, both x and y axis can be scaled with 2 fingers, if false,
     * x and y axis can be scaled separately. default: false
     * 
     * @param enabled
     */
    public void setPinchZoom(boolean enabled) {
        mPinchZoomEnabled = enabled;
    }

    /**
     * returns true if pinch-zoom is enabled, false if not
     * 
     * @return
     */
    public boolean isPinchZoomEnabled() {
        return mPinchZoomEnabled;
    }

    /**
     * returns the filtered ChartData object depending on approximator settings,
     * current scale level and x- and y-axis ratio
     * 
     * @return
     */
    private ChartData getFilteredData() {

        float deltaRatio = mDeltaY / mDeltaX;
        float scaleRatio = mScaleY / mScaleX;

        // set the determined ratios
        mApproximator.setRatios(deltaRatio, scaleRatio);

        // Log.i("Approximator", "DeltaRatio: " + deltaRatio + ", ScaleRatio: "
        // + scaleRatio);

        ArrayList<DataSet> dataSets = new ArrayList<DataSet>();

        for (int j = 0; j < mOriginalData.getDataSetCount(); j++) {

            DataSet old = mOriginalData.getDataSetByIndex(j);

            // do the filtering
            ArrayList<Entry> approximated = mApproximator.filter(old.getYVals());

            DataSet set = new DataSet(approximated, old.getLabel());
            dataSets.add(set);
        }

        ChartData d = new ChartData(mOriginalData.getXVals(), dataSets);
        return d;
    }

    @Override
    public void setPaint(Paint p, int which) {
        super.setPaint(p, which);

        switch (which) {
            case PAINT_GRID:
                mGridPaint = p;
                break;
            case PAINT_GRID_BACKGROUND:
                mGridBackgroundPaint = p;
                break;
            case PAINT_BORDER:
                mBorderPaint = p;
                break;
            case PAINT_XLABEL:
                mXLabelPaint = p;
                break;
            case PAINT_YLABEL:
                mYLabelPaint = p;
                break;
        }
    }
}
