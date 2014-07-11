
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

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.ZoomHandler;
import com.github.mikephil.charting.interfaces.OnDrawListener;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PointD;
import com.github.mikephil.charting.utils.SelInfo;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.YLegend;

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
     * width of the x-legend in pixels - this is calculated by the
     * calcTextWidth() method
     */
    protected int mXLegendWidth = 1;

    /**
     * the modulus that indicates if a value at a specified index in an
     * array(list) for the x-legend is drawn or not. If index % modulus == 0
     * DRAW, else dont draw.
     */
    protected int mXLegendGridModulus = 1;

    /** the number of y-legend entries the chart has */
    protected int mYLegendCount = 9;

    /** the width of the grid lines */
    protected float mGridWidth = 1f;

    /** if true, units are drawn next to the values in the chart */
    protected boolean mDrawUnitInChart = false;

    /** indicates if the top y-legend entry is drawn or not */
    private boolean mDrawTopYLegendEntry = true;

    /**
     * flag that indicates if pinch-zoom is enabled. if true, both x and y axis
     * can be scaled with 2 fingers, if false, x and y axis can be scaled
     * separately
     */
    protected boolean mPinchZoomEnabled = false;

    /**
     * if true, units are drawn next to the values in the legend
     */
    protected boolean mDrawUnitInLegend = true;

    /** if true, x-legend text is centered */
    protected boolean mCenterXLegendText = false;

    /**
     * if set to true, the x-legend entries will adjust themselves when scaling
     * the graph
     */
    protected boolean mAdjustXLegend = true;

    /** if true, the grid will be drawn, otherwise not, default true */
    protected boolean mDrawGrid = true;

    /** if true, dragging / scaling is enabled for the chart */
    protected boolean mDragEnabled = true;

    /** if true, the y range is predefined */
    protected boolean mFixedYValues = false;

    /** if true, the y-legend will always start at zero */
    protected boolean mStartAtZero = true;

    /** if true, data filterin is enabled */
    protected boolean mFilterData = true;

    /** paint object for the grid lines */
    protected Paint mGridPaint;

    /** paint object for the (by default) lightgrey background of the grid */
    protected Paint mGridBackgroundPaint;

    /** paint for the line surrounding the chart */
    protected Paint mOutLinePaint;

    /** paint for the x-legend values */
    protected Paint mXLegendPaint;

    /** paint for the y-legend values */
    protected Paint mYLegendPaint;

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

    /** the listener for user drawing on the chart */
    protected OnDrawListener mDrawListener;

    /**
     * the object representing the y-legend, this object is prepared in the
     * prepareYLegend() method
     */
    protected YLegend mYLegend = new YLegend();

    protected ZoomHandler mZoomHandler = new ZoomHandler();

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

        mXLegendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mXLegendPaint.setColor(Color.BLACK);
        mXLegendPaint.setTextAlign(Align.CENTER);
        mXLegendPaint.setTextSize(Utils.convertDpToPixel(10f));

        mYLegendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mYLegendPaint.setColor(Color.BLACK);
        mYLegendPaint.setTextAlign(Align.RIGHT);
        mYLegendPaint.setTextSize(Utils.convertDpToPixel(10f));

        mGridPaint = new Paint();
        mGridPaint.setColor(Color.GRAY);
        mGridPaint.setStrokeWidth(mGridWidth);
        mGridPaint.setStyle(Style.STROKE);
        mGridPaint.setAlpha(90);

        mOutLinePaint = new Paint();
        mOutLinePaint.setColor(Color.BLACK);
        mOutLinePaint.setStrokeWidth(mGridWidth * 2f);
        mOutLinePaint.setStyle(Style.STROKE);

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
        
//        if(mFilterData) mData = mZoomHandler.getFiltered(mOriginalData, mScaleX, mScaleY);
//        else mData = mOriginalData;
//        
//        Log.i(LOG_TAG, "FilterTime: " + (System.currentTimeMillis() - starttime) + " ms");
//        starttime = System.currentTimeMillis();

        if (mAdjustXLegend)
            calcModulus();

        // execute all drawing commands
        drawOutline();
        drawGridBackground();

        prepareYLegend();

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

        drawMarkers();

        drawValues();

        drawXLegend();

        drawYLegend();

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

        prepareXLegend();

        // calculate how many digits are needed
        calcFormats();

        if (!mFixedYValues)
            prepareMatrix();
    }

    @Override
    public void notifyDataSetChanged() {
        if (!mFixedYValues) {
            prepare();
        } else {
            calcMinMax(mFixedYValues);
        }
    }

    /**
     * calculates the modulus for legend and grid
     */
    protected void calcModulus() {

        float[] values = new float[9];
        mMatrixTouch.getValues(values);

        mXLegendGridModulus = (int) Math.ceil((mData.getXValCount() * mXLegendWidth)
                / (mContentRect.width() * values[Matrix.MSCALE_X]));
    }

    /** the decimalformat responsible for formatting the values in the chart */
    protected DecimalFormat mFormatValue = null;

    /** the number of digits the y-legend is formatted with */
    protected int mYLegendFormatDigits = -1;

    /**
     * calculates the required number of digits for the y-legend and for the
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
        // applies with non-rounded y-legend
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
     * setup the X legend
     */
    protected void prepareXLegend() {

        StringBuffer a = new StringBuffer();

        int length = (int) (((float) (mData.getXVals().get(0).length() + mData.getXVals()
                .get(mData.getXValCount() - 1)
                .length())));

        if (mData.getXVals().get(0).length() <= 3)
            length *= 2;

        for (int i = 0; i < length; i++) {
            a.append("h");
        }

        mXLegendWidth = calcTextWidth(mXLegendPaint, a.toString());
    }

    /**
     * Sets up the y-legend. Computes the desired number of labels between the
     * two given extremes. Unlike the prepareXLegend() method, this method needs
     * to be called upon every refresh of the view.
     * 
     * @return
     */
    private void prepareYLegend() {

        // calculate the currently visible extremes
        PointD p1 = getValuesByTouchPoint(mContentRect.left, mContentRect.top);
        PointD p2 = getValuesByTouchPoint(mContentRect.left, mContentRect.bottom);

        // update the current chart dimensions on the y-axis
        mYChartMin = (float) p2.y;
        mYChartMax = (float) p1.y;

        float yMin = mYChartMin;
        float yMax = mYChartMax;

        double range = yMax - yMin;
        if (mYLegendCount == 0 || range <= 0) {
            mYLegend.mEntries = new float[] {};
            mYLegend.mEntryCount = 0;
            return;
        }

        double rawInterval = range / mYLegendCount;
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

        mYLegend.mEntryCount = n;

        if (mYLegend.mEntries.length < n) {
            // Ensure stops contains at least numStops elements.
            mYLegend.mEntries = new float[n];
        }

        for (f = first, i = 0; i < n; f += interval, ++i) {
            mYLegend.mEntries[i] = (float) f;
        }

        if (interval < 1) {
            mYLegend.mDecimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            mYLegend.mDecimals = 0;
        }
    }

    /**
     * draws the x legend to the screen
     */
    protected void drawXLegend() {

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[] {
                0f, 0f
        };

        for (int i = 0; i < mData.getXValCount(); i++) {

            if (i % mXLegendGridModulus == 0) {

                position[0] = i;

                // center the text
                if (mCenterXLegendText)
                    position[0] += 0.5f;

                transformPointArray(position);

                if (position[0] >= mOffsetLeft && position[0] <= getWidth() - mOffsetRight + 10) {

                    mDrawCanvas.drawText(mData.getXVals().get(i), position[0], mOffsetTop - 5,
                            mXLegendPaint);
                }
            }
        }
    }

    /**
     * draws the y legend to the screen
     */
    protected void drawYLegend() {

        float[] positions = new float[mYLegend.mEntryCount * 2];

        for (int i = 0; i < positions.length; i += 2) {
            // only fill y values, x values are not needed since the y-legend is
            // static on the x-axis
            positions[i + 1] = mYLegend.mEntries[i / 2];
        }

        transformPointArray(positions);

        for (int i = 0; i < mYLegend.mEntryCount; i++) {

            String text = Utils.formatNumber(mYLegend.mEntries[i], mYLegend.mDecimals,
                    mSeparateTousands);

            if (!mDrawTopYLegendEntry && i >= mYLegend.mEntryCount - 1)
                return;

            if (mDrawUnitInLegend) {
                mDrawCanvas.drawText(text + mUnit, mOffsetLeft - 10, positions[i * 2 + 1],
                        mYLegendPaint);
            } else {
                mDrawCanvas.drawText(text, mOffsetLeft - 10, positions[i * 2 + 1], mYLegendPaint);
            }
        }
    }

    /**
     * draws a line that surrounds the chart
     */
    protected void drawOutline() {

        mDrawCanvas.drawRect(mOffsetLeft, mOffsetTop, getWidth() - mOffsetRight, getHeight()
                - mOffsetBottom,
                mOutLinePaint);
    }

    /**
     * draws the grid background
     */
    protected void drawGridBackground() {
        Rect gridBackground = new Rect(mOffsetLeft, mOffsetTop, getWidth() - mOffsetRight,
                getHeight() - mOffsetBottom);

        // draw the grid background
        mDrawCanvas.drawRect(gridBackground, mGridBackgroundPaint);
    }

    /**
     * draws the horizontal grid
     */
    protected void drawHorizontalGrid() {

        if (!mDrawGrid)
            return;

        // create a new path object only once and use reset() instead of
        // unnecessary allocations
        Path p = new Path();

        // draw the horizontal grid
        for (int i = 0; i < mYLegend.mEntryCount; i++) {

            p.reset();
            p.moveTo(0, mYLegend.mEntries[i]);
            p.lineTo(mDeltaX, mYLegend.mEntries[i]);

            transformPath(p);

            mDrawCanvas.drawPath(p, mGridPaint);
        }
    }

    /**
     * draws the vertical grid
     */
    protected void drawVerticalGrid() {

        if (!mDrawGrid)
            return;

        float[] position = new float[] {
                0f, 0f
        };

        for (int i = 0; i < mData.getXValCount(); i++) {

            if (i % mXLegendGridModulus == 0) {

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

        save.postScale(scaleX, scaleY, x, y);

        refreshTouch(save);
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
     * Sets the minimum scale values for both axes. Scale 0.5f means 0.5x zoom
     * (zoomed out by factor 2), scale 0.1f means maximum zoomed out by factor
     * 10, scale 2f means the user cannot zoom out further than 2x zoom, ...
     * 
     * @param scaleXmin
     * @param scaleYmin
     */
    public void setScaleMinima(float scaleXmin, float scaleYmin) {

        mMinScaleX = scaleXmin;
        mMinScaleY = scaleYmin;
    }

    /**
     * set this to true to enable drawing the top y-legend entry. Disabling this
     * can be helpful when the y-legend and x-legend interfear with each other.
     * default: true
     * 
     * @param enabled
     */
    public void setDrawTopYLegendEntry(boolean enabled) {
        mDrawTopYLegendEntry = enabled;
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
     * sets the number of legend entries for the y-legend max = 15, min = 3
     * 
     * @param yCount
     */
    public void setYLegendCount(int yCount) {

        if (yCount > 15)
            yCount = 15;
        if (yCount < 3)
            yCount = 3;

        mYLegendCount = yCount;
    }

    /**
     * if set to true, the x-legend entries will adjust themselves when scaling
     * the graph default: true
     * 
     * @param enabled
     */
    public void setAdjustXLegend(boolean enabled) {
        mAdjustXLegend = enabled;
    }

    /**
     * returns true if the x-legend adjusts itself when scaling the graph, false
     * if not
     * 
     * @return
     */
    public boolean isAdjustXLegendEnabled() {
        return mAdjustXLegend;
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
     * sets the size of the y-legend text in pixels min = 7f, max = 14f
     * 
     * @param size
     */
    public void setYLegendTextSize(float size) {

        if (size > 14f)
            size = 14f;
        if (size < 7f)
            size = 7f;
        mYLegendPaint.setTextSize(Utils.convertDpToPixel(size));
    }

    /**
     * sets the size of the x-legend text in pixels min = 7f, max = 14f
     * 
     * @param size
     */
    public void setXLegendTextSize(float size) {

        if (size > 14f)
            size = 14f;
        if (size < 7f)
            size = 7f;

        mXLegendPaint.setTextSize(Utils.convertDpToPixel(size));
    }

    /**
     * returns true if drawing the grid is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawGridEnabled() {
        return mDrawGrid;
    }

    /**
     * set to true if the grid should be drawn, false if not
     * 
     * @param enabled
     */
    public void setDrawGrid(boolean enabled) {
        this.mDrawGrid = enabled;
    }

    /**
     * If set to true, the highlight indicators (two lines for linechart, dark
     * bar for barchart) will be drawn upon selecting values. Default: true
     * 
     * @param enabled
     */
    public void setHighlightIndicatorEnabled(boolean enabled) {
        mHighLightIndicatorEnabled = enabled;
    }

    /**
     * enable this to force the y-legend to always start at zero
     * 
     * @param enabled
     */
    public void setStartAtZero(boolean enabled) {
        this.mStartAtZero = enabled;
        prepare();
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
     * if set to true, units are drawn next to y-legend values, default: true
     * 
     * @param enabled
     */
    public void setDrawUnitsInLegend(boolean enabled) {
        mDrawUnitInLegend = enabled;
    }

    /**
     * set this to true to center the x-legend text, default: false
     * 
     * @param enabled
     */
    public void setCenterXLegend(boolean enabled) {
        mCenterXLegendText = enabled;
    }

    /**
     * returns true if the x-legend text is centered
     * 
     * @return
     */
    public boolean isXLegendCentered() {
        return mCenterXLegendText;
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

//        Log.i(LOG_TAG, "touchindex x: " + xTouchVal + ", touchindex y: " + yTouchVal);

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

//        Log.i(LOG_TAG, "Closest DataSet index: " + index);

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
        return getEntry(h.getXIndex(), mData.getDataSetByIndex(h.getDataSetIndex()).getType());
    }

    /**
     * returns the zoomhandler of the chart
     * 
     * @return
     */
    public ZoomHandler getZoomHandler() {
        return mZoomHandler;
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
     * sets a typeface for the paint object of the x-legend
     * 
     * @param t
     */
    public void setXLegendTypeface(Typeface t) {
        mXLegendPaint.setTypeface(t);
    }

    /**
     * sets a typeface for the paint object of the y-legend
     * 
     * @param t
     */
    public void setYLegendTypeface(Typeface t) {
        mYLegendPaint.setTypeface(t);
    }

    /**
     * sets a typeface for both x and y-legend paints
     * 
     * @param t
     */
    public void setLegendTypeface(Typeface t) {
        setXLegendTypeface(t);
        setYLegendTypeface(t);
    }

    // /**
    // * Sets a filter on the whole ChartData. If the type is NONE, the
    // filtering
    // * is reset. What the filter does is remove certain values which's
    // distance
    // * to the next value in the chart is below the tolerance. The space left
    // in
    // * between is then approximated. This will increase performance whith
    // large
    // * amounts of data.
    // *
    // * @param type the filter type. NONE to reset filtering
    // * @param tolerance the tolerance
    // */
    // public void setFilter(ApproximatorType type, double tolerance) {
    // mData.setFilter(type, tolerance);
    // }

    /**
     * Enables data filtering for the chart data, filtering will use the default
     * Approximator. What filtering does is reduce the data displayed in the
     * chart with a certain tolerance. This can especially be important
     * concerning performance when displaying large datasets.
     */
    public void enableFiltering() {
        mFilterData = true;
        mZoomHandler.setCustomApproximator(null);
    }

    /**
     * Enables data filtering for the chart data, filtering will use the user
     * customized Approximator handed over to this method.
     * 
     * @param a
     */
    public void enableFiltering(Approximator a) {
        mFilterData = true;
        mZoomHandler.setCustomApproximator(a);
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

    // /**
    // * returns true if a filter has been set, flase if not
    // *
    // * @return
    // */
    // public boolean isFilterSet() {
    // return mData.isApproximatedData();
    // }

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
            case PAINT_OUTLINE:
                mOutLinePaint = p;
                break;
            case PAINT_XLEGEND:
                mXLegendPaint = p;
                break;
            case PAINT_YLEGEND:
                mYLegendPaint = p;
                break;
        }
    }
}
