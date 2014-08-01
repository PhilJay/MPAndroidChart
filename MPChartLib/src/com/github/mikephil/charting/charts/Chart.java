
package com.github.mikephil.charting.charts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.MarkerView;
import com.github.mikephil.charting.utils.SelInfo;
import com.github.mikephil.charting.utils.Utils;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Baseclass of all Chart-Views.
 * 
 * @author Philipp Jahoda
 */
public abstract class Chart extends View {

    public static final String LOG_TAG = "MPChart";

    protected int mColorDarkBlue = Color.rgb(41, 128, 186);
    protected int mColorDarkRed = Color.rgb(232, 76, 59);

    /**
     * defines the number of digits to use for all printed values, -1 means
     * automatically determine
     */
    protected int mValueDigitsToUse = -1;

    /**
     * defines the number of digits all printed values
     */
    protected int mValueFormatDigits = -1;

    /** chart offset to the left */
    protected float mOffsetLeft = 12;

    /** chart toffset to the top */
    protected float mOffsetTop = 12;

    /** chart offset to the right */
    protected float mOffsetRight = 12;

    /** chart offset to the bottom */
    protected float mOffsetBottom = 12;

    /**
     * object that holds all data relevant for the chart (x-vals, y-vals, ...)
     * that are currently displayed
     */
    protected ChartData mCurrentData = null;

    /**
     * object that holds all data that was originally set for the chart, before
     * it was modified or any filtering algorithms had been applied
     */
    protected ChartData mOriginalData = null;

    /** final bitmap that contains all information and is drawn to the screen */
    protected Bitmap mDrawBitmap;

    /** the canvas that is used for drawing on the bitmap */
    protected Canvas mDrawCanvas;

    /** the lowest value the chart can display */
    protected float mYChartMin = 0.0f;

    /** the highest value the chart can display */
    protected float mYChartMax = 0.0f;

    /**
     * paint object used for darwing the bitmap to the screen
     */
    protected Paint mDrawPaint;

    /**
     * paint object used for drawing the description text in the bottom right
     * corner of the chart
     */
    protected Paint mDescPaint;

    /**
     * paint object for drawing the information text when there are no values in
     * the chart
     */
    protected Paint mInfoPaint;

    /**
     * paint object for drawing values (text representing values of chart
     * entries)
     */
    protected Paint mValuePaint;

    /** this is the paint object used for drawing the data onto the chart */
    protected Paint mRenderPaint;
    
    /** paint for the legend labels */
    protected Paint mLegendLabelPaint;

    /** paint used for the legend forms */
    protected Paint mLegendFormPaint;

    /** the colortemplate the chart uses */
    protected ColorTemplate mCt;

    /** description text that appears in the bottom right corner of the chart */
    protected String mDescription = "Description.";

    /** flag that indicates if the chart has been fed with data yet */
    protected boolean mDataNotSet = true;

    /** the range of y-values the chart displays */
    protected float mDeltaY = 1f;

    /** the number of x-values the chart displays */
    protected float mDeltaX = 1f;

    /** matrix to map the values to the screen pixels */
    protected Matrix mMatrixValueToPx = new Matrix();

    /** matrix for handling the different offsets of the chart */
    protected Matrix mMatrixOffset = new Matrix();

    /** matrix used for touch events */
    protected final Matrix mMatrixTouch = new Matrix();

    /** if true, touch gestures are enabled on the chart */
    protected boolean mTouchEnabled = true;

    /** if true, y-values are drawn on the chart */
    protected boolean mDrawYValues = true;

    /** if true, value highlightning is enabled */
    protected boolean mHighlightEnabled = true;

    /** if true, thousands values are separated by a dot */
    protected boolean mSeparateTousands = true;
    
    /** flag indicating if the legend is drawn of not */
    protected boolean mDrawLegend = true;

    /** this rectangle defines the area in which graph values can be drawn */
    protected Rect mContentRect = new Rect();

    /** the legend object containing all data associated with the legend */
    protected Legend mLegend;

    /** listener that is called when a value on the chart is selected */
    protected OnChartValueSelectedListener mSelectionListener;

    /** default constructor for initialization in code */
    public Chart(Context context) {
        super(context);
        init();
    }

    /** constructor for initialization in xml */
    public Chart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /** even more awesome constructor */
    public Chart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * initialize all paints and stuff
     */
    protected void init() {

        // initialize the utils
        Utils.init(getContext().getResources());

        // do screen density conversions
        mOffsetBottom = (int) Utils.convertDpToPixel(mOffsetBottom);
        mOffsetLeft = (int) Utils.convertDpToPixel(mOffsetLeft);
        mOffsetRight = (int) Utils.convertDpToPixel(mOffsetRight);
        mOffsetTop = (int) Utils.convertDpToPixel(mOffsetTop);

        mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRenderPaint.setStyle(Style.FILL);

        mDrawPaint = new Paint();

        mDescPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDescPaint.setColor(Color.BLACK);
        mDescPaint.setTextAlign(Align.RIGHT);
        mDescPaint.setTextSize(Utils.convertDpToPixel(9f));

        mInfoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInfoPaint.setColor(Color.rgb(247, 189, 51)); // orange
        mInfoPaint.setTextAlign(Align.CENTER);
        mInfoPaint.setTextSize(Utils.convertDpToPixel(12f));

        mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint.setColor(Color.rgb(63, 63, 63));
        mValuePaint.setTextAlign(Align.CENTER);
        mValuePaint.setTextSize(Utils.convertDpToPixel(9f));

        mLegendFormPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendFormPaint.setStyle(Paint.Style.FILL);
        mLegendFormPaint.setStrokeWidth(3f);

        mLegendLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendLabelPaint.setTextSize(Utils.convertDpToPixel(9f));

        mCt = new ColorTemplate();
        mCt.addDataSetColors(ColorTemplate.VORDIPLOM_COLORS, getContext());
    }

    public void initWithDummyData() {
        ColorTemplate template = new ColorTemplate();
        template.addColorsForDataSets(ColorTemplate.COLORFUL_COLORS, getContext());

        setColorTemplate(template);
        setDrawYValues(false);

        ArrayList<String> xVals = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 12; i++) {
            xVals.add(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
        }

        ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
        for (int i = 0; i < 3; i++) {

            ArrayList<Entry> yVals = new ArrayList<Entry>();

            for (int j = 0; j < 12; j++) {
                float val = (float) (Math.random() * 100);
                yVals.add(new Entry(val, j));
            }

            DataSet set = new DataSet(yVals, "DataSet " + i);
            dataSets.add(set); // add the datasets
        }
        // create a data object with the datasets
        ChartData data = new ChartData(xVals, dataSets);
        setData(data);
        invalidate();
    }

    protected boolean mOffsetsCalculated = false;

    /**
     * Sets a new ChartData object for the chart.
     * 
     * @param data
     */
    public void setData(ChartData data) {

        if (data == null || !data.isValid()) {
            Log.e(LOG_TAG,
                    "Cannot set data for chart. Provided chart values are null or contain less than 2 entries.");
            mDataNotSet = true;
            return;
        }

        // LET THE CHART KNOW THERE IS DATA
        mDataNotSet = false;
        mOffsetsCalculated = false;
        mCurrentData = data;
        mOriginalData = data;

        prepare();

        Log.i(LOG_TAG, "Data is set.");
    }

    /**
     * Sets primitive data for the chart. Internally, this is converted into a
     * ChartData object with one DataSet (type 0). If you have more specific
     * requirements for your data, use the setData(ChartData data) method and
     * create your own ChartData object with as many DataSets as you like.
     * 
     * @param xVals
     * @param yVals
     */
    public void setData(ArrayList<String> xVals, ArrayList<Float> yVals) {

        ArrayList<Entry> series = new ArrayList<Entry>();

        for (int i = 0; i < yVals.size(); i++) {
            series.add(new Entry(yVals.get(i), i));
        }

        DataSet set = new DataSet(series, "DataSet");
        ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.add(set);

        ChartData data = new ChartData(xVals, dataSets);

        setData(data);
    }

    /**
     * does needed preparations for drawing
     */
    public abstract void prepare();

    /** lets the chart know its unterlying data has changed */
    public abstract void notifyDataSetChanged();

    /**
     * calculates the offsets of the chart to the border depending on the
     * position of an eventual legend or depending on the length of the y-axis
     * labels
     */
    protected abstract void calculateOffsets();

    /**
     * calcualtes the y-min and y-max value and the y-delta and x-delta value
     */
    protected void calcMinMax(boolean fixedValues) {
        // only calculate values if not fixed values
        if (!fixedValues) {
            mYChartMin = mCurrentData.getYMin();
            mYChartMax = mCurrentData.getYMax();
        }

        // calc delta
        mDeltaY = Math.abs(mYChartMax - mYChartMin);
        mDeltaX = mCurrentData.getXVals().size() - 1;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (!mOffsetsCalculated) {

            calculateOffsets();
            mOffsetsCalculated = true;
        }

        if (mDataNotSet) { // check if there is data

            // if no data, inform the user
            canvas.drawText("No chart data available.", getWidth() / 2, getHeight() / 2, mInfoPaint);
            return;
        }

        if (mDrawBitmap == null || mDrawCanvas == null) {

            // use RGB_565 for best performance
            mDrawBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
            mDrawCanvas = new Canvas(mDrawBitmap);
        }

        mDrawCanvas.drawColor(Color.WHITE); // clear all
    }

    /**
     * setup all the matrices that will be used for scaling the coordinates to
     * the display
     */
    protected void prepareMatrix() {

        float scaleX = (float) ((getWidth() - mOffsetLeft - mOffsetRight) / mDeltaX);
        float scaleY = (float) ((getHeight() - mOffsetBottom - mOffsetTop) / mDeltaY);

        // setup all matrices
        mMatrixValueToPx.reset();
        mMatrixValueToPx.postTranslate(0, -mYChartMin);
        mMatrixValueToPx.postScale(scaleX, -scaleY);

        mMatrixOffset.reset();
        mMatrixOffset.postTranslate(mOffsetLeft, getHeight() - mOffsetBottom);

        Log.i(LOG_TAG, "Matrices prepared.");
    }

    /**
     * sets up the content rect that restricts the chart surface
     */
    protected void prepareContentRect() {

        mContentRect.set((int) mOffsetLeft, (int) mOffsetTop, getMeasuredWidth() - (int) mOffsetRight,
                getMeasuredHeight()
                        - (int) mOffsetBottom);

//        Log.i(LOG_TAG, "Contentrect prepared. Width: " + mContentRect.width() + ", height: "
//                + mContentRect.height());
    }
    

    /**
     * Generates an automatically prepared legend depending on the DataSets in
     * the chart and their colors.
     */
    public void prepareLegend() {

        ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int i = 0; i < mOriginalData.getDataSetCount(); i++) {

            ArrayList<Integer> clrs = mCt.getDataSetColors(i % mCt.getColors().size());
            int dataSetCount = mOriginalData.getDataSetByIndex(i).getEntryCount();
            
            for (int j = 0; j < clrs.size() && j < dataSetCount; j++) {

                // if multiple colors are set for a DataSet, group them
                if (j < clrs.size() - 1 && j < dataSetCount - 1) {

                    labels.add(null);
                } else { // add label to the last entry

                    String label = mOriginalData.getDataSetByIndex(i).getLabel();
                    labels.add(label);
                }

                colors.add(clrs.get(j));
            }
        }
        
//        Log.i(LOG_TAG, "Preparing legend, colors size: " + colors.size() + ", labels size: " + labels.size());

        Legend l = new Legend(colors, labels);

        if (mLegend != null) {
            // apply the old legend settings to a potential new legend
            l.apply(mLegend);
        }

        mLegend = l;
    }

    /**
     * transforms an arraylist of Entry into a float array containing the x and
     * y values transformed with all matrices
     * 
     * @param entries
     * @param xoffset offset the chart values should have on the x-axis (0.5f)
     *            to center for barchart
     * @return
     */
    protected float[] generateTransformedValues(ArrayList<Entry> entries, float xOffset) {

        float[] valuePoints = new float[entries.size() * 2];

        for (int j = 0; j < valuePoints.length; j += 2) {
            valuePoints[j] = entries.get(j / 2).getXIndex() + xOffset;
            valuePoints[j + 1] = entries.get(j / 2).getVal();
        }

        transformPointArray(valuePoints);

        return valuePoints;
    }

    /**
     * transform a path with all the given matrices VERY IMPORTANT: keep order
     * to value-touch-offset
     * 
     * @param path
     */
    protected void transformPath(Path path) {

        path.transform(mMatrixValueToPx);
        path.transform(mMatrixTouch);
        path.transform(mMatrixOffset);
    }

    /**
     * transforms multiple paths will all matrices
     * 
     * @param paths
     */
    protected void transformPaths(ArrayList<Path> paths) {

        for (int i = 0; i < paths.size(); i++) {
            transformPath(paths.get(i));
        }
    }

    /**
     * transform an array of points VERY IMPORTANT: keep order to
     * value-touch-offset
     * 
     * @param pts
     */
    protected void transformPointArray(float[] pts) {

        mMatrixValueToPx.mapPoints(pts);
        mMatrixTouch.mapPoints(pts);
        mMatrixOffset.mapPoints(pts);
    }

    /**
     * transform a rectangle with all matrices
     * 
     * @param r
     */
    protected void transformRect(RectF r) {

        mMatrixValueToPx.mapRect(r);
        mMatrixTouch.mapRect(r);
        mMatrixOffset.mapRect(r);
    }

    /**
     * transforms multiple rects with all matrices
     * 
     * @param rects
     */
    protected void transformRects(ArrayList<RectF> rects) {

        for (int i = 0; i < rects.size(); i++)
            transformRect(rects.get(i));
    }

    /**
     * transforms the given rect objects with the touch matrix only
     * 
     * @param paths
     */
    protected void transformRectsTouch(ArrayList<RectF> rects) {
        for (int i = 0; i < rects.size(); i++) {
            mMatrixTouch.mapRect(rects.get(i));
        }
    }

    /**
     * transforms the given path objects with the touch matrix only
     * 
     * @param paths
     */
    protected void transformPathsTouch(ArrayList<Path> paths) {
        for (int i = 0; i < paths.size(); i++) {
            paths.get(i).transform(mMatrixTouch);
        }
    }
    
    /**
     * draws the legend
     */
    protected void drawLegend() {

        if (!mDrawLegend || mLegend == null)
            return;

        String[] labels = mLegend.getLegendLabels();
        Typeface tf = mLegend.getTypeface();

        if (tf != null)
            mLegendLabelPaint.setTypeface(tf);

        float formSize = mLegend.getFormSize();

        // space between text and shape/form of entry
        float formToTextSpace = mLegend.getFormToTextSpace() + formSize;

        // space between the entries
        float entrySpace = mLegend.getEntrySpace() + formSize;

        float textSize = mLegendLabelPaint.getTextSize();

        // the amount of pixels the text needs to be set down to be on the same
        // height as the form
        float textDrop = (Utils.calcTextHeight(mLegendLabelPaint, "AQJ") + formSize) / 2f;
        
//        Log.i(LOG_TAG, "OffsetBottom: " + mLegend.getOffsetBottom() + ", Formsize: " + formSize + ", Textsize: " + textSize + ", TextDrop: " + textDrop);

        float posX, posY;

        switch (mLegend.getPosition()) {
            case BELOW_CHART_LEFT:

                posX = mLegend.getOffsetLeft();
                posY = getHeight() - mLegend.getOffsetBottom() / 2f - formSize / 2f;

                for (int i = 0; i < labels.length; i++) {

                    mLegend.drawForm(mDrawCanvas, posX, posY, mLegendFormPaint, i);

                    // grouped forms have null labels
                    if (labels[i] != null) {
                        
                        // make a step to the left
                        posX += formToTextSpace;

                        mLegend.drawLabel(mDrawCanvas, posX, posY + textDrop, mLegendLabelPaint, i);
                        posX += Utils.calcTextWidth(mLegendLabelPaint, labels[i]) + entrySpace;
                    } else {
                        posX += entrySpace;
                    }
                }

                break;
            case BELOW_CHART_RIGHT:

                posX = getWidth() - mLegend.getOffsetRight() - getOffsetRight();
                posY = getHeight() - mLegend.getOffsetBottom() / 2f - formSize / 2f;

                for (int i = labels.length - 1; i >= 0; i--) {

                    if (labels[i] != null) {

                        posX -= Utils.calcTextWidth(mLegendLabelPaint, labels[i]);
                        mLegend.drawLabel(mDrawCanvas, posX, posY + textDrop, mLegendLabelPaint, i);
                        posX -= formToTextSpace;
                    }

                    mLegend.drawForm(mDrawCanvas, posX, posY, mLegendFormPaint, i);

                    // make a step to the left
                    posX -= entrySpace;
                }

                break;
            case RIGHT_OF_CHART:
                
                if(this instanceof BarLineChartBase) {
                    posX = getWidth() - mLegend.getOffsetRight() + Utils.convertDpToPixel(10f); 
                    posY = mLegend.getOffsetTop();
                } else {
                    posX = getWidth() - mLegend.getMaximumEntryLength(mLegendLabelPaint);
                    posY = Utils.calcTextHeight(mLegendLabelPaint, "A") * 1.5f;
                }
                
                float stack = 0f;
                boolean wasStacked = false;

                for (int i = 0; i < labels.length; i++) {

                    mLegend.drawForm(mDrawCanvas, posX + stack, posY, mLegendFormPaint, i);

                    if (labels[i] != null) {

                        if (!wasStacked) {
                            mLegend.drawLabel(mDrawCanvas, posX + formToTextSpace, posY + textDrop,
                                    mLegendLabelPaint, i);
                        } else {

                            mLegend.drawLabel(mDrawCanvas, posX, posY + textSize + formSize + mLegend.getEntrySpace(),
                                    mLegendLabelPaint, i);
                            posY += entrySpace;
                        }

                        // make a step down
                        posY += entrySpace + textSize;
                        stack = 0f;
                    } else {
                        stack += formSize + 4f;
                        wasStacked = true;
                    }
                }

                break;
        }
    }

    /**
     * draws the description text in the bottom right corner of the chart
     */
    protected void drawDescription() {

        mDrawCanvas
                .drawText(mDescription, getWidth() - mOffsetRight - 10, getHeight() - mOffsetBottom
                        - 10, mDescPaint);
    }

    /**
     * draws all the text-values to the chart
     */
    protected abstract void drawValues();

    /**
     * draws the actual data
     */
    protected abstract void drawData();

    /**
     * draws additional stuff, whatever that might be
     */
    protected abstract void drawAdditional();

    /**
     * draws the values of the chart that need highlightning
     */
    protected abstract void drawHighlights();

    /** touchlistener that handles touches and gestures on the chart */
    protected OnTouchListener mListener;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mListener == null)
            return false;

        // check if touch gestures are enabled
        if (!mTouchEnabled)
            return false;
        else
            return mListener.onTouch(this, event);
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW THIS CODE FOR HIGHLIGHTING */

    /**
     * array of Highlight objects that reference the highlighted slices in the
     * chart
     */
    protected Highlight[] mIndicesToHightlight = new Highlight[0];

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
        if (!valuesToHighlight())
            return false;

        for (int i = 0; i < mIndicesToHightlight.length; i++)

            // check if the xvalue for the given dataset needs highlight
            if (mIndicesToHightlight[i].getXIndex() == xIndex
                    && mIndicesToHightlight[i].getDataSetIndex() == dataSetIndex
                    && xIndex <= mDeltaX)
                return true;

        return false;
    }

    /**
     * Returns true if there are values to highlight, false if there are no
     * values to highlight. Checks if the highlight array is null, has a length
     * of zero or if the first object is null.
     * 
     * @return
     */
    public boolean valuesToHighlight() {
        return mIndicesToHightlight == null || mIndicesToHightlight.length <= 0
                || mIndicesToHightlight[0] == null ? false
                : true;
    }

    /**
     * Highlights the values at the given indices in the given DataSets. Provide
     * null or an empty array to undo all highlighting.
     * 
     * @param highs
     */
    public void highlightValues(Highlight[] highs) {

        // set the indices to highlight
        mIndicesToHightlight = highs;

        // redraw the chart
        invalidate();

        if (mSelectionListener != null) {

            if (!valuesToHighlight())
                mSelectionListener.onNothingSelected();
            else {

                Entry[] values = new Entry[highs.length];

                for (int i = 0; i < values.length; i++)
                    values[i] = getEntryByDataSetIndex(highs[i].getXIndex(),
                            highs[i].getDataSetIndex());

                // notify the listener
                mSelectionListener.onValuesSelected(values, highs);
            }
        }
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW CODE IS FOR THE MARKER VIEW */

    /** if set to true, the marker view is drawn when a value is clicked */
    protected boolean mDrawMarkerViews = true;

    /** the view that represents the marker */
    protected MarkerView mMarkerView;

    /**
     * draws all MarkerViews on the highlighted positions
     */
    protected void drawMarkers() {

        // if there is no marker view or drawing marker is disabled
        if (mMarkerView == null || !mDrawMarkerViews || !valuesToHighlight())
            return;

        for (int i = 0; i < mIndicesToHightlight.length; i++) {

            int xIndex = mIndicesToHightlight[i].getXIndex();

            drawMarkerView(xIndex, mIndicesToHightlight[i].getDataSetIndex());
        }
    }

    /**
     * Draws the view that is displayed when a value is highlighted.
     * 
     * @param xIndex the selected x-index
     * @param dataSetIndex the index of the selected DataSet
     */
    private void drawMarkerView(int xIndex, int dataSetIndex) {

        float value = getYValueByDataSetIndex(xIndex, dataSetIndex);
        float xPos = (float) xIndex;

        // make sure the marker is in the center of the bars in BarChart
        if (this instanceof BarChart)
            xPos += 0.5f;

        // position of the marker depends on selected value index and value
        float[] pts = new float[] {
                xPos, value
        };
        transformPointArray(pts);

        float posX = pts[0];
        float posY = pts[1];

        // callbacks to update the content
        mMarkerView.refreshContent(xIndex, value, dataSetIndex);

        // call the draw method of the markerview that will translate to the
        // given position and draw the view
        mMarkerView.draw(mDrawCanvas, posX, posY);
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW THIS ONLY GETTERS AND SETTERS */

    /**
     * set a new (e.g. custom) charttouchlistener NOTE: make sure to
     * setTouchEnabled(true); if you need touch gestures on the chart
     * 
     * @param l
     */
    public void setOnTouchListener(OnTouchListener l) {
        this.mListener = l;
    }

    /**
     * set a selection listener for the chart
     * 
     * @param l
     */
    public void setOnChartValueSelectedListener(OnChartValueSelectedListener l) {
        this.mSelectionListener = l;
    }

    /**
     * if set to true, value highlightning is enabled
     * 
     * @param enabled
     */
    public void setHighlightEnabled(boolean enabled) {
        mHighlightEnabled = enabled;
    }

    /**
     * returns true if highlightning of values is enabled, false if not
     * 
     * @return
     */
    public boolean isHighlightEnabled() {
        return mHighlightEnabled;
    }

    /**
     * returns the total value (sum) of all y-values across all DataSets
     * 
     * @return
     */
    public float getYValueSum() {
        return mCurrentData.getYValueSum();
    }

    /**
     * returns the current y-max value across all DataSets
     * 
     * @return
     */
    public float getYMax() {
        return mCurrentData.getYMax();
    }

    /**
     * returns the lowest value the chart can display
     * 
     * @return
     */
    public float getYChartMin() {
        return mYChartMin;
    }

    /**
     * returns the highest value the chart can display
     * 
     * @return
     */
    public float getYChartMax() {
        return mYChartMax;
    }

    /**
     * returns the current y-min value across all DataSets
     * 
     * @return
     */
    public float getYMin() {
        return mCurrentData.getYMin();
    }

    /**
     * Get the total number of X-values.
     * 
     * @return
     */
    public float getDeltaX() {
        return mDeltaX;
    }

    /**
     * returns the average value of all values the chart holds
     * 
     * @return
     */
    public float getAverage() {
        return getYValueSum() / mCurrentData.getYValCount();
    }

    /**
     * returns the average value for a specific DataSet (with a specific label)
     * in the chart
     * 
     * @param dataSetLabel
     * @return
     */
    public float getAverage(String dataSetLabel) {

        DataSet ds = mCurrentData.getDataSetByLabel(dataSetLabel, true);

        return ds.getYValueSum()
                / ds.getEntryCount();
    }

    /**
     * returns the total number of values the chart holds (across all DataSets)
     * 
     * @return
     */
    public int getValueCount() {
        return mCurrentData.getYValCount();
    }

    /**
     * returns the center point of the chart in pixels
     * 
     * @return
     */
    public PointF getCenter() {
        return new PointF(getWidth() / 2, getHeight() / 2);
    }
    
    /**
     * returns the center of the chart taking offsets under consideration
     * @return
     */
    public PointF getCenterOffsets() {      
        return new PointF(mContentRect.left + mContentRect.width() / 2 , mContentRect.top + mContentRect.height() / 2);
    }

    /**
     * sets the size of the description text in pixels, min 7f, max 14f
     * 
     * @param size
     */
    public void setDescriptionTextSize(float size) {

        if (size > 14f)
            size = 14f;
        if (size < 7f)
            size = 7f;

        mInfoPaint.setTextSize(Utils.convertDpToPixel(size));
    }

    /**
     * set a description text that appears in the bottom right corner of the
     * chart, size = Y-legend text size
     * 
     * @param desc
     */
    public void setDescription(String desc) {
        this.mDescription = desc;
    }

    /**
     * Sets the offsets from the border of the view to the actual chart in every
     * direction manually. Provide density pixels -> they are then rendered to
     * pixels inside the chart
     * 
     * @param left
     * @param right
     * @param top
     * @param bottom
     */
    public void setOffsets(float left, float top, float right, float bottom) {

        mOffsetBottom = Utils.convertDpToPixel(bottom);
        mOffsetLeft = Utils.convertDpToPixel(left);
        mOffsetRight = Utils.convertDpToPixel(right);
        mOffsetTop = Utils.convertDpToPixel(top);
    }

    public float getOffsetLeft() {
        return mOffsetLeft;
    }

    public float getOffsetBottom() {
        return mOffsetBottom;
    }

    public float getOffsetRight() {
        return mOffsetRight;
    }

    public float getOffsetTop() {
        return mOffsetTop;
    }

    /**
     * set this to false to disable gestures on the chart, default: true
     * 
     * @param enabled
     */
    public void setTouchEnabled(boolean enabled) {
        this.mTouchEnabled = enabled;
    }

    /**
     * set this to true to draw y-values on the chart NOTE (for bar and
     * linechart): if "maxvisiblecount" is reached, no values will be drawn even
     * if this is enabled
     * 
     * @param enabled
     */
    public void setDrawYValues(boolean enabled) {
        this.mDrawYValues = enabled;
    }

    /**
     * Sets a colortemplate for the chart that defindes the colors used for
     * drawing. If more values need to be drawn than provided colors available
     * in the colortemplate, colors are repeated.
     * 
     * @param ct
     */
    public void setColorTemplate(ColorTemplate ct) {
        this.mCt = ct;
        
        Log.i(LOG_TAG, "ColorTemplate set.");
    }

    /**
     * returns the colortemplate used by the chart
     * 
     * @return
     */
    public ColorTemplate getColorTemplate() {
        return mCt;
    }

    /**
     * sets the view that is displayed when a value is clicked on the chart
     * 
     * @param v
     */
    public void setMarkerView(MarkerView v) {
        mMarkerView = v;
    }

    /**
     * returns the view that is set as a marker view for the chart
     * 
     * @return
     */
    public MarkerView getMarkerView() {
        return mMarkerView;
    }
    
    /**
     * set this to true to draw the legend, false if not
     * 
     * @param enabled
     */
    public void setDrawLegend(boolean enabled) {
        mDrawLegend = enabled;
    }

    /**
     * returns true if drawing the legend is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawLegendEnabled() {
        return mDrawLegend;
    }

    /**
     * Returns the legend object of the chart. This method can be used to
     * customize the automatically generated legend. IMPORTANT: this will return
     * null if no data has been set for the chart when calling this method
     * 
     * @return
     */
    public Legend getLegend() {
        return mLegend;
    }

    /** paint for the grid lines (only line and barchart) */
    public static final int PAINT_GRID = 3;

    /** paint for the grid background (only line and barchart) */
    public static final int PAINT_GRID_BACKGROUND = 4;

    /** paint for the y-legend values (only line and barchart) */
    public static final int PAINT_YLABEL = 5;

    /** paint for the x-legend values (only line and barchart) */
    public static final int PAINT_XLABEL = 6;

    /**
     * paint for the info text that is displayed when there are no values in the
     * chart
     */
    public static final int PAINT_INFO = 7;

    /** paint for the value text */
    public static final int PAINT_VALUES = 8;

    /** paint for the inner circle (linechart) */
    public static final int PAINT_CIRCLES_INNER = 10;

    /** paint for the description text in the bottom right corner */
    public static final int PAINT_DESCRIPTION = 11;

    /** paint for the line surrounding the chart (only line and barchart) */
    public static final int PAINT_BORDER = 12;

    /** paint for the hole in the middle of the pie chart */
    public static final int PAINT_HOLE = 13;

    /** paint for the text in the middle of the pie chart */
    public static final int PAINT_CENTER_TEXT = 14;

    /** paint for highlightning the values of a linechart */
    public static final int PAINT_HIGHLIGHT_LINE = 15;

    /** paint for highlightning the values of a linechart */
    public static final int PAINT_HIGHLIGHT_BAR = 16;

    /** paint used for all rendering processes */
    public static final int PAINT_RENDER = 17;

    /** paint used for the legend */
    public static final int PAINT_LEGEND_LABEL = 18;

    /**
     * set a new paint object for the specified parameter in the chart e.g.
     * Chart.PAINT_VALUES
     * 
     * @param p the new paint object
     * @param which Chart.PAINT_VALUES, Chart.PAINT_GRID, Chart.PAINT_VALUES,
     *            ...
     */
    public void setPaint(Paint p, int which) {

        switch (which) {
            case PAINT_INFO:
                mInfoPaint = p;
                break;
            case PAINT_DESCRIPTION:
                mDescPaint = p;
                break;
            case PAINT_VALUES:
                mValuePaint = p;
                break;
            case PAINT_RENDER:
                mRenderPaint = p;
                break;
            case PAINT_LEGEND_LABEL:
                mLegendLabelPaint = p;
                break;
        }
    }
    
    /**
     * Returns the paint object associated with the provided constant.
     * @param which e.g. Chart.PAINT_LEGEND_LABEL
     * @return
     */
    public Paint getPaint(int which) {
        switch (which) {
            case PAINT_INFO:
                return mInfoPaint;
            case PAINT_DESCRIPTION:
                return mDescPaint;
            case PAINT_VALUES:
                return mValuePaint;
            case PAINT_RENDER:
                return mRenderPaint;
            case PAINT_LEGEND_LABEL:
                return mLegendLabelPaint;
        }
        
        return null;
    }

    /**
     * returns true if drawing the marker-view is enabled when tapping on values
     * (use the setMarkerView(View v) method to specify a marker view)
     * 
     * @return
     */
    public boolean isDrawMarkerViewEnabled() {
        return mDrawMarkerViews;
    }

    /**
     * Set this to true to draw a user specified marker-view when tapping on
     * chart values (use the setMarkerView(MarkerView mv) method to specify a
     * marker view). Default: true
     * 
     * @param enabled
     */
    public void setDrawMarkerViews(boolean enabled) {
        mDrawMarkerViews = enabled;
    }

    /**
     * sets the draw color for the value paint object
     * 
     * @param color
     */
    public void setValuePaintColor(int color) {
        mValuePaint.setColor(color);
    }

    /**
     * set this to true to separate thousands values by a dot. Default: true
     * 
     * @param enabled
     */
    public void setSeparateThousands(boolean enabled) {
        mSeparateTousands = enabled;
    }

    /**
     * returns true if y-value drawing is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawYValuesEnabled() {
        return mDrawYValues;
    }

    /**
     * returns the x-value at the given index
     * 
     * @param index
     * @return
     */
    public String getXValue(int index) {
        if (mCurrentData == null || mCurrentData.getXValCount() <= index)
            return null;
        else
            return mCurrentData.getXVals().get(index);
    }

    /**
     * Returns the y-value for the given index from the first DataSet. If
     * multiple DataSets are used, please use getYValue(int index, int type);
     * 
     * @param index
     * @return
     */
    public float getYValue(int index) {
        return mCurrentData.getDataSetByIndex(0).getYVals().get(index).getVal();
    }

    /**
     * returns the y-value for the given index from the DataSet with the given
     * label
     * 
     * @param index
     * @param dataSetLabel
     * @return
     */
    public float getYValue(int index, String dataSetLabel) {
        DataSet set = mCurrentData.getDataSetByLabel(dataSetLabel, true);
        return set.getYVals().get(index).getVal();
    }

    /**
     * returns the y-value for the given x-index and DataSet index
     * 
     * @param index
     * @param dataSet
     * @return
     */
    public float getYValueByDataSetIndex(int xIndex, int dataSet) {
        DataSet set = mCurrentData.getDataSetByIndex(dataSet);
        return set.getYValForXIndex(xIndex);
    }

    /**
     * returns the DataSet with the given index in the DataSet array held by the
     * ChartData object.
     * 
     * @param index
     * @return
     */
    public DataSet getDataSetByIndex(int index) {
        return mCurrentData.getDataSetByIndex(index);
    }

    /**
     * returns the DataSet with the given label that is stored in the ChartData
     * object.
     * 
     * @param type
     * @return
     */
    public DataSet getDataSetByLabel(String dataSetLabel) {
        return mCurrentData.getDataSetByLabel(dataSetLabel, true);
    }

    /**
     * returns the Entry object from the first DataSet stored in the ChartData
     * object. If multiple DataSets are used, use getEntry(index, type) or
     * getEntryByDataSetIndex(xIndex, dataSetIndex);
     * 
     * @param index
     * @return
     */
    public Entry getEntry(int index) {
        return mCurrentData.getDataSetByIndex(0).getYVals().get(index);
    }

    /**
     * returns the Entry object at the given index from the DataSet with the
     * given label.
     * 
     * @param index
     * @param dataSetLabel
     * @return
     */
    public Entry getEntry(int index, String dataSetLabel) {
        return mCurrentData.getDataSetByLabel(dataSetLabel, true).getYVals().get(index);
    }

    /**
     * Returns the corresponding Entry object at the given xIndex from the given
     * DataSet. INFORMATION: This method does calculations at runtime. Do not
     * over-use in performance critical situations.
     * 
     * @param xIndex
     * @param dataSetIndex
     * @return
     */
    public Entry getEntryByDataSetIndex(int xIndex, int dataSetIndex) {
        return mCurrentData.getDataSetByIndex(dataSetIndex).getEntryForXIndex(xIndex);
    }

    /**
     * Returns an array of SelInfo objects for the given x-index. The SelInfo
     * objects give information about the value at the selected index and the
     * DataSet it belongs to. INFORMATION: This method does calculations at
     * runtime. Do not over-use in performance critical situations.
     * 
     * @param xIndex
     * @return
     */
    protected ArrayList<SelInfo> getYValsAtIndex(int xIndex) {

        ArrayList<SelInfo> vals = new ArrayList<SelInfo>();

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            // extract all y-values from all DataSets at the given x-index
            float yVal = mCurrentData.getDataSetByIndex(i).getYValForXIndex(xIndex);

            if (!Float.isNaN(yVal)) {
                vals.add(new SelInfo(yVal, i));
            }
        }

        return vals;
    }

    /**
     * Get all Entry objects at the given index across all DataSets.
     * INFORMATION: This method does calculations at runtime. Do not over-use in
     * performance critical situations.
     * 
     * @param xIndex
     * @return
     */
    public ArrayList<Entry> getEntriesAtIndex(int xIndex) {

        ArrayList<Entry> vals = new ArrayList<Entry>();

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            DataSet set = mCurrentData.getDataSetByIndex(i);

            Entry e = set.getEntryForXIndex(xIndex);

            if (e != null) {
                vals.add(e);
            }
        }

        return vals;
    }

    /**
     * Returns the ChartData object the chart CURRENTLY represents. It contains
     * all values and information the chart displays. If filtering algorithms
     * have been applied, this returns the filtered state of data.
     * 
     * @return
     */
    public ChartData getDataCurrent() {
        return mCurrentData;
    }

    /**
     * Returns the ChartData object that ORIGINALLY has been set for the chart.
     * It contains all data in an unaltered state, before any filtering
     * algorithms have been applied.
     * 
     * @return
     */
    public ChartData getDataOriginal() {
        return mOriginalData;
    }

    /**
     * returns the percentage the given value has of the total y-value sum
     * 
     * @param val
     * @return
     */
    public float getPercentOfTotal(float val) {
        return val / mCurrentData.getYValueSum() * 100f;
    }

    /**
     * sets a typeface for the value-paint
     * 
     * @param t
     */
    public void setValueTypeface(Typeface t) {
        mValuePaint.setTypeface(t);
    }

    /**
     * sets the typeface for the description paint
     * 
     * @param t
     */
    public void setDescriptionTypeface(Typeface t) {
        mDescPaint.setTypeface(t);
    }

    /**
     * sets the number of digits that should be used for all printed values (if
     * this is set to -1, digits will be calculated automatically), default -1
     * 
     * @param digits
     */
    public void setValueDigits(int digits) {
        mValueDigitsToUse = digits;
    }

    /**
     * returns the number of digits used to format the printed values of the
     * chart (-1 means digits are calculated automatically)
     * 
     * @return
     */
    public int getValueDigits() {
        return mValueDigitsToUse;
    }

    /**
     * saves the current chart state to a bitmap in the gallery NOTE: Needs
     * permission WRITE_EXTERNAL_STORAGE
     * 
     * @param title
     */
    public void saveToGallery(String title) {
        MediaStore.Images.Media.insertImage(getContext().getContentResolver(), mDrawBitmap, title,
                "");
    }

    /**
     * saves the chart with the given name to the given path on the sdcard
     * leaving the path empty "" will put the saved file directly on the SD card
     * chart is saved as .png example: saveToPath("myfilename",
     * "foldername1/foldername2");
     * 
     * @param title
     * @param pathOnSD e.g. "folder1/folder2/folder3"
     */
    public void saveToPath(String title, String pathOnSD) {

        OutputStream stream = null;
        try {
            stream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()
                    + pathOnSD + "/" + title
                    + ".png");

            /*
             * Write bitmap to file using JPEG or PNG and 40% quality hint for
             * JPEG.
             */
            mDrawBitmap.compress(CompressFormat.PNG, 40, stream);

            stream.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    private boolean mMatrixOnLayoutPrepared = false;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        prepareContentRect();
        Log.i(LOG_TAG, "onLayout(), width: " + mContentRect.width() + ", height: " + mContentRect.height());

        if (this instanceof BarLineChartBase) {

            BarLineChartBase b = (BarLineChartBase) this;
            
            // if y-values are not fixed
            if (!b.hasFixedYValues() && !mMatrixOnLayoutPrepared) {
                prepareMatrix();
                mMatrixOnLayoutPrepared = true;
            }
                
        } else {
            prepareMatrix();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isInEditMode()) {
            initWithDummyData();
        }
    }
}
