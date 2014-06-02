
package com.github.mikephil.charting.charts;

import android.annotation.SuppressLint;
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

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PointD;
import com.github.mikephil.charting.utils.SelInfo;
import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Baseclass of all LineChart and BarChart.
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

    /** if true, the y scale is predefined */
    protected boolean mFixedYValues = false;

    /** if true, the y-legend will always start at zero */
    protected boolean mStartAtZero = true;

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
     * the object representing the y-legend, this object is prepared in the
     * prepareYLegend() method
     */
    protected YLegend mYLegend = new YLegend();

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
        drawValues();

        drawXLegend();

        drawYLegend();

        drawMarkerView();

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

        // Log.i(LOG_TAG, "xVals: " + mXVals.size() + ", yVals: " +
        // mYVals.size());
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
        // else if(mXVals.get(0).length() <= 5) length *= 1;
        // else if(mXVals.get(0).length() <= 7) length = (int) (length / 1.5f);
        // else if(mXVals.get(0).length() <= 9) length = (int) (length / 2.5f);
        // else if(mXVals.get(0).length() > 9) length = (int) (length / 4f);

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
    @SuppressLint("NewApi")
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

    private static class YLegend {
        float[] mEntries = new float[] {};
        int mEntryCount;
        int mDecimals;
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

                    // Rect rect = new Rect();
                    // mXLegendPaint.getTextBounds(mXVals.get(i), 0,
                    // mXVals.get(i).length(), rect);
                    //
                    // float toRight = rect.width() / 2;
                    //
                    // // make sure
                    // if(i == 0) toRight = rect.width();

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

            String text = Utils
                    .formatNumber(mYLegend.mEntries[i], mYLegend.mDecimals);

            if (!mDrawTopYLegendEntry && i >= mYLegend.mEntryCount - 1)
                return;

            if (mDrawUnitInLegend) {
                mDrawCanvas.drawText(text + mUnit, mOffsetLeft - 10,
                        positions[i * 2 + 1],
                        mYLegendPaint);
            } else {
                mDrawCanvas
                        .drawText(text, mOffsetLeft - 10,
                                positions[i * 2 + 1], mYLegendPaint);
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

    /** indicates if the top y-legend entry is drawn or not */
    private boolean mDrawTopYLegendEntry = true;

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
     * determines how much space (in percent of the total range) is left between
     * the loweset value of the chart and its bottom (bottomSpace) and the
     * highest value of the chart and its top (topSpace) recommended: 3-25 %
     * NOTE: the bottomSpace will only apply if "startAtZero" is set to false
     * 
     * @param bottomSpace
     * @param topSpace
     */
    // public void setSpacePercent(int bottomSpace, int topSpace) {
    // mYSpacePercentBottom = bottomSpace;
    // mYSpacePercentTop = topSpace;
    // }

    /**
     * sets the effective range of y-values the chart can display
     * 
     * @param minY
     * @param maxY
     */
    public void setYRange(float minY, float maxY) {
        mFixedYValues = true;

        mYChartMin = minY;
        mYChartMax = maxY;
        mDeltaY = mYChartMax - mYChartMin;

        calcFormats();
        prepareMatrix();
        invalidate();
    }

    /**
     * Resets the previously set y range
     */
    public void resetYRange() {
        mFixedYValues = false;
        calcMinMax(mFixedYValues);

        prepareMatrix();
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
     * returns the Highlight object (x index and DataSet index) of the selected
     * value at the given touch point
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
        if (this instanceof LineChart && (xTouchVal < 0 || xTouchVal > mDeltaX))
            return null;
        if (this instanceof BarChart && (xTouchVal < 0 || xTouchVal > mDeltaX + 1))
            return null;

        int xIndex = (int) base;
        int yIndex = 0; // index of the DataSet inside the ChartData object

        if (this instanceof LineChart) {

            // check if we are more than half of a x-value or not
            if (xTouchVal - base > 0.5) {
                xIndex = (int) base + 1;
            }
        }

        ArrayList<SelInfo> valsAtIndex = getYValsAtIndex(xIndex);

        yIndex = getClosestDataSetIndex(valsAtIndex, (float) yTouchVal);

        if (yIndex == -1)
            return null;

        return new Highlight(xIndex, yIndex);
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
     * Returns the x and y values at the given touch point (encapsulated in a
     * PointD).
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

    /**
     * Sets a filter on the whole ChartData. If the type is NONE, the filtering
     * is reset. Be aware that the original DataSets are not modified. Instead
     * there are modified copies of the data. All methods return the filtered
     * values if a filter is set. To receive the original values despite a set
     * filter, call getOriginalDataSets() of the class ChartData. The ChartData
     * can be received by calling getData().
     * 
     * @param type the filter type. NONE to reset filtering
     * @param tolerance the tolerance
     */
    public void setFilter(ApproximatorType type, double tolerance) {
        mData.setFilter(type, tolerance);
    }

    /**
     * returns true if a filter has been set, flase if not
     * 
     * @return
     */
    public boolean isFilterSet() {
        return mData.isApproximatedData();
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
