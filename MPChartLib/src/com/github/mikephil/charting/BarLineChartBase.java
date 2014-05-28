
package com.github.mikephil.charting;

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
     * array(list) is drawn or not. If index % modulus == 0 DRAW, else dont
     * draw.
     */
    protected int mLegendGridModulus = 1;

    /**
     * indicates how many digits should be used for the y-legend, -1 means
     * automatically calculate
     */
    protected int mYLegendDigitsToUse = -1;

    /** the number of y-legend entries the chart has */
    protected int mYLegendCount = 9;

    /** the width of the grid lines */
    protected float mGridWidth = 1f;

    /** array that contains all values of the y-axis legend */
    protected Float[] mYLegend = new Float[mYLegendCount];

    /** if true, units are drawn next to the values in the chart */
    protected boolean mDrawUnitInChart = false;

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

    /**
     * if true, the y-legend values will be rounded - the legend entry count
     * will only be approximated
     */
    protected boolean mRoundedYLegend = false;

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
    protected void prepare() {

        if (mDataNotSet)
            return;

        calcMinMax();

        prepareXLegend();

        prepareYLegend();

        // calculate how many digits are needed
        calcFormats();

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

        mLegendGridModulus = (int) Math.ceil((mData.getXValCount() * mXLegendWidth)
                / (mContentRect.width() * values[Matrix.MSCALE_X]));
    }

    /** the decimalformat responsible for formatting the y-legend */
    protected DecimalFormat mFormatYLegend = null;

    /** the decimalformat responsible for formatting the values in the chart */
    protected DecimalFormat mFormatValue = null;

    /** the number of digits the y-legend is formatted with */
    protected int mYLegendFormatDigits = -1;

    /**
     * calculates the required number of digits for the y-legend and for the
     * values that might be drawn in the chart (if enabled)
     */
    protected void calcFormats() {

        int bonus = 0;
        if (!mRoundedYLegend)
            bonus++;

        // calcualte the step between the legend entries and get the needed
        // digits, -1 means calculate automatically, else the set value is used
        if (mYLegendDigitsToUse == -1)
            mYLegendFormatDigits = Utils.getLegendFormatDigits(mYLegend[2] - mYLegend[1], bonus);
        else
            mYLegendFormatDigits = mYLegendDigitsToUse;

        if (mValueDigitsToUse == -1)
            mValueFormatDigits = Utils.getFormatDigits(mDeltaY);
        else
            mValueFormatDigits = mValueDigitsToUse;

        StringBuffer a = new StringBuffer();
        for (int i = 0; i < mYLegendFormatDigits; i++) {
            if (i == 0)
                a.append(".");
            a.append("0");
        }

        mFormatYLegend = new DecimalFormat("###,###,###,##0" + a.toString());

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < mValueFormatDigits; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        mFormatValue = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax(); // calc min and max in the super class

        // additional handling for space (default 10% space)

        float spaceTop = (mData.getYMax() - mYChartMin) / 100f * 10f;

        if (mStartAtZero) {
            mYChartMin = 0;
        } else {
            float spaceBottom = (mData.getYMax() - mYChartMin) / 100f * 10f;
            mYChartMin = mYChartMin - spaceBottom;
        }

        // calc delta
        mDeltaY = (mData.getYMax() + spaceTop) - mYChartMin;
        mYChartMax = mYChartMin + mDeltaY;

        Log.i(LOG_TAG, "DeltaX: " + mDeltaX + ", DeltaY: " + mDeltaY);
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
     * Math.pow(...) is very expensive, so avoid calling it and create it
     * yourself
     */
    protected static final double POW_10[] = {
            0.00001, 0.0001, 0.001, 0.01, 0.1, 1, 10, 100, 1000, 10000, 100000,
            1000000, 10000000, 100000000, 1000000000
            // maximum = 1 billion
    };

    /**
     * setup the Y legend
     */
    protected void prepareYLegend() {

        ArrayList<Float> yLegend = new ArrayList<Float>();

        // if the legend is supposed to be rounded
        if (mRoundedYLegend) {

            // float interval = (mYMax+mYMin) / (Y_LEGEND_COUNT - 1);
            float interval = (mDeltaY) / (mYLegendCount - 1);
            double log10 = Math.log10(interval);
            int exp = (int) Math.floor(log10);
            double tenPowExp = POW_10[exp + 5]; // +5 because the POW array has
                                                // its "1" at index 5
            double multi = Math.round(interval / tenPowExp);

            if (multi >= 1) {
                if (multi > 2 && multi < 5) {
                    multi = 5;
                } else if (multi > 5 && multi < 10) {
                    multi = 10;
                }
            }

            float step = (float) (multi * tenPowExp);

            float val = 0;

            if (step >= 1f)
                val = (int) (mYChartMin / step) * step;
            else {
                val = mYChartMin;
            }

            // float val = mYMin % step * (int) (mYMin / step);

            // create the y-legend values in the calculated step size
            while (val <= mDeltaY + step + mYChartMin) {
                yLegend.add(val);
                val = val + step;
            }

            if (step >= 1f)
                mYChartMin = (int) (mYChartMin / step) * step;

            // set the new delta adequate to the last y-legend value
            mDeltaY = val - step - mYChartMin;
            mYChartMax = yLegend.get(yLegend.size() - 1);

        } else {

            float interval = (mDeltaY) / (mYLegendCount - 1);

            yLegend.add(mYChartMin);

            for (int i = 1; i < mYLegendCount - 1; i++) {
                yLegend.add(mYChartMin + ((float) i * interval));
            }

            yLegend.add(mDeltaY + mYChartMin);
        }
        // convert the list to an array
        mYLegend = yLegend.toArray(new Float[0]);
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

            if (i % mLegendGridModulus == 0) {

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

        float[] positions = new float[mYLegend.length * 2];

        for (int i = 0; i < positions.length; i += 2) {
            positions[i] = 0;
            positions[i + 1] = mYLegend[i / 2];
        }

        // y-axis cannot be scaled, therefore transform without the touch matrix
        transformPointArrayNoTouch(positions);

        for (int i = 0; i < positions.length; i += 2) {
            if (mYLegend[i / 2] != null) {

                float yPos = positions[i + 1] + 8;

                // lower the position of the top y-legend entry so that it does
                // not interfear with the x-legend
                if (i >= positions.length - 2)
                    yPos = positions[i + 1] + 14;

                if (!mDrawTopYLegendEntry && i >= positions.length - 2)
                    return;

                if (mDrawUnitInLegend) {
                    mDrawCanvas.drawText(mFormatYLegend.format(mYLegend[i / 2]) + mUnit,
                            positions[i] - 10, yPos,
                            mYLegendPaint);
                } else {
                    mDrawCanvas
                            .drawText(mFormatYLegend.format(mYLegend[i / 2]), positions[i] - 10,
                                    yPos, mYLegendPaint);
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
        for (int i = 0; i < mYLegend.length; i++) {

            p.reset();
            p.moveTo(0, mYLegend[i]);
            p.lineTo(mDeltaX, mYLegend[i]);

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

            if (i % mLegendGridModulus == 0) {

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

        mYChartMin = minY;
        mYChartMax = maxY;
        mDeltaY = mYChartMax - mYChartMin;

        prepareMatrix();
        prepareYLegend();
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

        mYLegend = new Float[mYLegendCount];
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
     * set to true to round all y-legend values if true, the y-legend values
     * will be rounded - the y-legend entry count will only be approximated this
     * way
     * 
     * @param enabled
     */
    public void setRoundedYLegend(boolean enabled) {
        this.mRoundedYLegend = enabled;
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
     * returns true if the y-legend is set to be rounded, false if not
     * 
     * @return
     */
    public boolean isYLegendRounded() {
        return mRoundedYLegend;
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
    public Highlight getIndexByTouchPoint(float x, float y) {

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
        if (this instanceof LineChart
                && (xTouchVal < 0 || xTouchVal > mDeltaX))
            return null;
        if (this instanceof BarChart && (xTouchVal < 0 || xTouchVal > mDeltaX+1))
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
        
        if(yIndex == -1) return null;

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
     * returns the value displayed at the touched position of the chart
     * 
     * @param x
     * @param y
     * @return
     */
    public Series getSeriesByTouchPoint(float x, float y) {
        Highlight h = getIndexByTouchPoint(x, y);
        return getSeries(h.getXIndex(), mData.getDataSetByIndex(h.getDataSetIndex()).getType());
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
     * sets the number of y-legend digits to use, if set to -1, digits will be
     * automatically calculated
     * 
     * @param digits
     */
    public void setLegendDigits(int digits) {
        mYLegendDigitsToUse = digits;
    }

    /**
     * returns the number of y-legend digits that is set to use, -1 means auto
     * calculate
     * 
     * @return
     */
    public int getLegendDigits() {
        return mYLegendDigitsToUse;
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
