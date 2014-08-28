
package com.github.mikephil.charting.charts;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.MarkerView;
import com.github.mikephil.charting.utils.SelInfo;
import com.github.mikephil.charting.utils.Utils;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Baseclass of all Chart-Views.
 * 
 * @author Philipp Jahoda
 */
public abstract class Chart extends View implements AnimatorUpdateListener {

    public static final String LOG_TAG = "MPChart";

    protected int mColorDarkBlue = Color.rgb(41, 128, 186);
    protected int mColorDarkRed = Color.rgb(232, 76, 59);

    /**
     * string that is drawn next to the values in the chart, indicating their
     * unit
     */
    protected String mUnit = "";

    /**
     * flag that holds the background color of the view and the color the canvas
     * is cleared with
     */
    private int mBackgroundColor = Color.WHITE;

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

    /** description text that appears in the bottom right corner of the chart */
    protected String mDescription = "Description.";

    /** flag that indicates if the chart has been fed with data yet */
    protected boolean mDataNotSet = true;

    /** if true, units are drawn next to the values in the chart */
    protected boolean mDrawUnitInChart = false;

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

    /** text that is displayed when the chart is empty */
    private String mNoDataText = "No chart data available.";

    /**
     * text that is displayed when the chart is empty that describes why the
     * chart is empty
     */
    private String mNoDataTextDescription;

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
    }

    // public void initWithDummyData() {
    // ColorTemplate template = new ColorTemplate();
    // template.addColorsForDataSets(ColorTemplate.COLORFUL_COLORS,
    // getContext());
    //
    // setColorTemplate(template);
    // setDrawYValues(false);
    //
    // ArrayList<String> xVals = new ArrayList<String>();
    // Calendar calendar = Calendar.getInstance();
    // for (int i = 0; i < 12; i++) {
    // xVals.add(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
    // Locale.getDefault()));
    // }
    //
    // ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
    // for (int i = 0; i < 3; i++) {
    //
    // ArrayList<Entry> yVals = new ArrayList<Entry>();
    //
    // for (int j = 0; j < 12; j++) {
    // float val = (float) (Math.random() * 100);
    // yVals.add(new Entry(val, j));
    // }
    //
    // DataSet set = new DataSet(yVals, "DataSet " + i);
    // dataSets.add(set); // add the datasets
    // }
    // // create a data object with the datasets
    // ChartData data = new ChartData(xVals, dataSets);
    // setData(data);
    // invalidate();
    // }

    protected boolean mOffsetsCalculated = false;

    /**
     * Sets a new ChartData object for the chart.
     * 
     * @param data
     */
    protected void setData(ChartData data) {

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

    // /**
    // * Sets primitive data for the chart. Internally, this is converted into a
    // * ChartData object with one DataSet (type 0). If you have more specific
    // * requirements for your data, use the setData(ChartData data) method and
    // * create your own ChartData object with as many DataSets as you like.
    // *
    // * @param xVals
    // * @param yVals
    // */
    // public void setData(ArrayList<String> xVals, ArrayList<Float> yVals) {
    //
    // ArrayList<Entry> series = new ArrayList<Entry>();
    //
    // for (int i = 0; i < yVals.size(); i++) {
    // series.add(new Entry(yVals.get(i), i));
    // }
    //
    // DataSet set = new DataSet(series, "DataSet");
    // ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
    // dataSets.add(set);
    //
    // ChartData data = new ChartData(xVals, dataSets);
    //
    // setData(data);
    // }

    // /**
    // * Sets primitive data for the chart. Internally, this is converted into a
    // * ChartData object with one DataSet (type 0). If you have more specific
    // * requirements for your data, use the setData(ChartData data) method and
    // * create your own ChartData object with as many DataSets as you like.
    // *
    // * @param xVals
    // * @param yVals
    // */
    // public void setData(ArrayList<String> xVals, ArrayList<Float> yVals) {
    //
    // ArrayList<Entry> series = new ArrayList<Entry>();
    //
    // for (int i = 0; i < yVals.size(); i++) {
    // series.add(new Entry(yVals.get(i), i));
    // }
    //
    // DataSet set = new DataSet(series, "DataSet");
    // ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
    // dataSets.add(set);
    //
    // ChartData data = new ChartData(xVals, dataSets);
    //
    // setData(data);
    // }

    /**
     * does needed preparations for drawing
     */
    public abstract void prepare();

    /** lets the chart know its unterlying data has changed */
    public abstract void notifyDataSetChanged();

    /**
     * calculates the offsets of the chart to the border depending on the
     * position of an eventual legend or depending on the length of the y-axis
     * and x-axis labels and their position
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mOffsetsCalculated) {

            calculateOffsets();
            mOffsetsCalculated = true;
        }

        if (mDataNotSet) { // check if there is data

            // if no data, inform the user
            canvas.drawText(mNoDataText, getWidth() / 2, getHeight() / 2, mInfoPaint);

            if (!TextUtils.isEmpty(mNoDataTextDescription)) {
                float textOffset = -mInfoPaint.ascent() + mInfoPaint.descent();
                canvas.drawText(mNoDataTextDescription, getWidth() / 2, (getHeight() / 2)
                        + textOffset, mInfoPaint);
            }
            return;
        }

        if (mDrawBitmap == null || mDrawCanvas == null) {

            // use RGB_565 for best performance
            mDrawBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
            mDrawCanvas = new Canvas(mDrawBitmap);
        }

        mDrawCanvas.drawColor(mBackgroundColor); // clear all
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

        // mMatrixOffset.setTranslate(mOffsetLeft, 0);
        // mMatrixOffset.postScale(1.0f, -1.0f);

        Log.i(LOG_TAG, "Matrices prepared.");
    }

    /**
     * sets up the content rect that restricts the chart surface
     */
    protected void prepareContentRect() {

        mContentRect.set((int) mOffsetLeft, (int) mOffsetTop, getMeasuredWidth()
                - (int) mOffsetRight,
                getMeasuredHeight()
                        - (int) mOffsetBottom + 1);

        // Log.i(LOG_TAG, "Contentrect prepared. Width: " + mContentRect.width()
        // + ", height: "
        // + mContentRect.height());
    }

    /**
     * Generates an automatically prepared legend depending on the DataSets in
     * the chart and their colors.
     */
    public void prepareLegend() {

        ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Integer> colors = new ArrayList<Integer>();

        // loop for building up the colors and labels used in the legend
        for (int i = 0; i < mOriginalData.getDataSetCount(); i++) {

            DataSet dataSet = mOriginalData.getDataSetByIndex(i);

            ArrayList<Integer> clrs = dataSet.getColors();
            int entryCount = dataSet.getEntryCount();

            // if we have a barchart with stacked bars
            if (dataSet instanceof BarDataSet && ((BarDataSet) dataSet).getStackSize() > 1) {

                BarDataSet bds = (BarDataSet) dataSet;
                String[] sLabels = bds.getStackLabels();

                for (int j = 0; j < clrs.size() && j < entryCount && j < bds.getStackSize(); j++) {

                    labels.add(sLabels[j % sLabels.length]);
                    colors.add(clrs.get(j));
                }

                // add the legend description label
                colors.add(-1);
                labels.add(bds.getLabel());

            } else if (dataSet instanceof PieDataSet) {

                ArrayList<String> xVals = mOriginalData.getXVals();
                PieDataSet pds = (PieDataSet) dataSet;

                for (int j = 0; j < clrs.size() && j < entryCount && j < xVals.size(); j++) {

                    labels.add(xVals.get(j));
                    colors.add(clrs.get(j));
                }

                // add the legend description label
                colors.add(-1);
                labels.add(pds.getLabel());

            } else { // all others

                for (int j = 0; j < clrs.size() && j < entryCount; j++) {

                    // if multiple colors are set for a DataSet, group them
                    if (j < clrs.size() - 1 && j < entryCount - 1) {

                        labels.add(null);
                    } else { // add label to the last entry

                        String label = mOriginalData.getDataSetByIndex(i).getLabel();
                        labels.add(label);
                    }

                    colors.add(clrs.get(j));
                }
            }
        }

        Legend l = new Legend(colors, labels);

        if (mLegend != null) {
            // apply the old legend settings to a potential new legend
            l.apply(mLegend);
        }

        mLegend = l;
    }

    /**
     * Transforms an arraylist of Entry into a float array containing the x and
     * y values transformed with all matrices for the LINECHART or SCATTERCHART.
     * 
     * @param entries
     * @return
     */
    protected float[] generateTransformedValuesLineScatter(ArrayList<? extends Entry> entries) {

        float[] valuePoints = new float[entries.size() * 2];

        for (int j = 0; j < valuePoints.length; j += 2) {
            valuePoints[j] = entries.get(j / 2).getXIndex();
            valuePoints[j + 1] = entries.get(j / 2).getVal() * mPhaseY;
        }

        transformPointArray(valuePoints);

        return valuePoints;
    }

    /**
     * Transforms an arraylist of Entry into a float array containing the x and
     * y values transformed with all matrices for the BARCHART.
     * 
     * @param entries
     * @param dataSet the dataset index
     * @return
     */
    protected float[] generateTransformedValuesBarChart(ArrayList<? extends Entry> entries,
            int dataSet) {

        float[] valuePoints = new float[entries.size() * 2];

        int setCount = mOriginalData.getDataSetCount();

        for (int j = 0; j < valuePoints.length; j += 2) {

            Entry e = entries.get(j / 2);

            // calculate the x-position, depending on datasetcount
            float x = e.getXIndex() + (j / 2 * (setCount - 1)) + dataSet + 0.5f;
            float y = e.getVal();

            valuePoints[j] = x;
            valuePoints[j + 1] = y * mPhaseY;
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
     * Transforms multiple paths will all matrices.
     * 
     * @param paths
     */
    protected void transformPaths(ArrayList<Path> paths) {

        for (int i = 0; i < paths.size(); i++) {
            transformPath(paths.get(i));
        }
    }

    /**
     * Transform an array of points with all matrices. VERY IMPORTANT: Keep
     * matrix order "value-touch-offset" when transforming.
     * 
     * @param pts
     */
    protected void transformPointArray(float[] pts) {

        mMatrixValueToPx.mapPoints(pts);
        mMatrixTouch.mapPoints(pts);
        mMatrixOffset.mapPoints(pts);
    }

    /**
     * Transform a rectangle with all matrices.
     * 
     * @param r
     */
    protected void transformRect(RectF r) {

        mMatrixValueToPx.mapRect(r);
        mMatrixTouch.mapRect(r);
        mMatrixOffset.mapRect(r);
    }

    /**
     * Transform a rectangle with all matrices with potential animation phases.
     * 
     * @param r
     */
    protected void transformRectWithPhase(RectF r) {

        // multiply the height of the rect with the phase
        if (r.top > 0)
            r.top *= mPhaseY;
        else
            r.bottom *= mPhaseY;

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

        mLegendLabelPaint.setTextSize(mLegend.getTextSize());

        float formSize = mLegend.getFormSize();

        // space between text and shape/form of entry
        float formTextSpaceAndForm = mLegend.getFormToTextSpace() + formSize;

        // space between the entries
        float stackSpace = mLegend.getStackSpace();

        float textSize = mLegend.getTextSize();

        // the amount of pixels the text needs to be set down to be on the same
        // height as the form
        float textDrop = (Utils.calcTextHeight(mLegendLabelPaint, "AQJ") + formSize) / 2f;

        // Log.i(LOG_TAG, "OffsetBottom: " + mLegend.getOffsetBottom() +
        // ", Formsize: " + formSize + ", Textsize: " + textSize +
        // ", TextDrop: " + textDrop);

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
                        if (mLegend.getColors()[i] != -1)
                            posX += formTextSpaceAndForm;

                        mLegend.drawLabel(mDrawCanvas, posX, posY + textDrop, mLegendLabelPaint, i);
                        posX += Utils.calcTextWidth(mLegendLabelPaint, labels[i])
                                + mLegend.getXEntrySpace();
                    } else {
                        posX += formSize + stackSpace;
                    }
                }

                break;
            case BELOW_CHART_RIGHT:

                posX = getWidth() - getOffsetRight();
                posY = getHeight() - mLegend.getOffsetBottom() / 2f - formSize / 2f;

                for (int i = labels.length - 1; i >= 0; i--) {

                    if (labels[i] != null) {

                        posX -= Utils.calcTextWidth(mLegendLabelPaint, labels[i])
                                + mLegend.getXEntrySpace();
                        mLegend.drawLabel(mDrawCanvas, posX, posY + textDrop, mLegendLabelPaint, i);
                        if (mLegend.getColors()[i] != -1)
                            posX -= formTextSpaceAndForm;
                    } else {
                        posX -= stackSpace + formSize;
                    }

                    mLegend.drawForm(mDrawCanvas, posX, posY, mLegendFormPaint, i);
                }

                break;
            case RIGHT_OF_CHART:

                posX = getWidth() - mLegend.getMaximumEntryLength(mLegendLabelPaint)
                        - formTextSpaceAndForm;
                posY = mLegend.getOffsetTop();

                float stack = 0f;
                boolean wasStacked = false;

                for (int i = 0; i < labels.length; i++) {

                    mLegend.drawForm(mDrawCanvas, posX + stack, posY, mLegendFormPaint, i);

                    if (labels[i] != null) {

                        if (!wasStacked) {

                            float x = posX;

                            if (mLegend.getColors()[i] != -1)
                                x += formTextSpaceAndForm;

                            posY += textDrop;

                            mLegend.drawLabel(mDrawCanvas, x, posY,
                                    mLegendLabelPaint, i);
                        } else {

                            posY += textSize * 1.2f + formSize;

                            mLegend.drawLabel(mDrawCanvas, posX, posY,
                                    mLegendLabelPaint, i);

                        }

                        // make a step down
                        posY += mLegend.getYEntrySpace();
                        stack = 0f;
                    } else {
                        stack += formSize + stackSpace;
                        wasStacked = true;
                    }
                }
                break;
            case BELOW_CHART_CENTER:

                float fullSize = mLegend.getFullWidth(mLegendLabelPaint);

                posX = getWidth() / 2f - fullSize / 2f;
                posY = getHeight() - mLegend.getOffsetBottom() / 2f - formSize / 2f;

                for (int i = 0; i < labels.length; i++) {

                    mLegend.drawForm(mDrawCanvas, posX, posY, mLegendFormPaint, i);

                    // grouped forms have null labels
                    if (labels[i] != null) {

                        // make a step to the left
                        if (mLegend.getColors()[i] != -1)
                            posX += formTextSpaceAndForm;

                        mLegend.drawLabel(mDrawCanvas, posX, posY + textDrop, mLegendLabelPaint, i);
                        posX += Utils.calcTextWidth(mLegendLabelPaint, labels[i])
                                + mLegend.getXEntrySpace();
                    } else {
                        posX += formSize + stackSpace;
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
     * null or an empty array to undo all highlighting. This should be used to
     * programmatically highlight values. This DOES NOT generate a callback to
     * the OnChartValueSelectedListener.
     * 
     * @param highs
     */
    public void highlightValues(Highlight[] highs) {

        // set the indices to highlight
        mIndicesToHightlight = highs;

        // redraw the chart
        invalidate();
    }

    /**
     * Highlights the value selected by touch gesture. Unlike
     * highlightValues(...), this generates a callback to the
     * OnChartValueSelectedListener.
     * 
     * @param highs
     */
    public void highlightTouch(Highlight high) {

        if (high == null)
            mIndicesToHightlight = null;
        else {

            // set the indices to highlight
            mIndicesToHightlight = new Highlight[] {
                    high
            };
        }

        // redraw the chart
        invalidate();

        if (mSelectionListener != null) {

            if (!valuesToHighlight())
                mSelectionListener.onNothingSelected();
            else {

                Entry e = getEntryByDataSetIndex(high.getXIndex(),
                        high.getDataSetIndex());

                // notify the listener
                mSelectionListener.onValueSelected(e, high.getDataSetIndex());
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

            if (xIndex <= mDeltaX && xIndex <= mDeltaX * mPhaseX)
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

        Entry e = getEntryByDataSetIndex(xIndex, dataSetIndex);

        // make sure the entry is not null
        if (e == null) {
            return;
        }

        float xPos = (float) xIndex;

        // make sure the marker is in the center of the bars in BarChart and
        // CandleStickChart
        if (this instanceof BarChart || this instanceof CandleStickChart)
            xPos += 0.5f;

        // position of the marker depends on selected value index and value
        float[] pts = new float[] {
                xPos, e.getVal() * mPhaseY
        };
        transformPointArray(pts);

        float posX = pts[0];
        float posY = pts[1];

        // callbacks to update the content
        mMarkerView.refreshContent(e, dataSetIndex);

        // call the draw method of the markerview that will translate to the
        // given position and draw the view
        mMarkerView.draw(mDrawCanvas, posX, posY);
    }

    /**
     * ################ ################ ################ ################
     * Animation support below Honeycomb thanks to Jake Wharton's awesome
     * nineoldandroids library: https://github.com/JakeWharton/NineOldAndroids
     */
    /** CODE BELOW THIS RELATED TO ANIMATION */

    /** the phase that is animated and influences the drawn values on the y-axis */
    protected float mPhaseY = 1f;

    /** the phase that is animated and influences the drawn values on the x-axis */
    protected float mPhaseX = 1f;

    /** objectanimator used for animating values on y-axis */
    private ObjectAnimator mAnimatorY;

    /** objectanimator used for animating values on x-axis */
    private ObjectAnimator mAnimatorX;

    /**
     * Animates the drawing / rendering of the chart on both x- and y-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart.
     * 
     * @param durationMillisX
     * @param durationMillisY
     */
    public void animateXY(int durationMillisX, int durationMillisY) {

        mAnimatorY = ObjectAnimator.ofFloat(this, "phaseY", 0f, 1f);
        mAnimatorY.setDuration(
                durationMillisY);
        mAnimatorX = ObjectAnimator.ofFloat(this, "phaseX", 0f, 1f);
        mAnimatorX.setDuration(
                durationMillisX);

        // make sure only one animator produces update-callbacks (which then
        // call invalidate())
        if (durationMillisX > durationMillisY) {
            mAnimatorX.addUpdateListener(this);
        } else {
            mAnimatorY.addUpdateListener(this);
        }

        mAnimatorX.start();
        mAnimatorY.start();
    }

    /**
     * Animates the rendering of the chart on the x-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     * 
     * @param durationMillis
     */
    public void animateX(int durationMillis) {

        mAnimatorX = ObjectAnimator.ofFloat(this, "phaseX", 0f, 1f);
        mAnimatorX.setDuration(durationMillis);
        mAnimatorX.addUpdateListener(this);
        mAnimatorX.start();
    }

    /**
     * Animates the rendering of the chart on the y-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     * 
     * @param durationMillis
     */
    public void animateY(int durationMillis) {

        mAnimatorY = ObjectAnimator.ofFloat(this, "phaseY", 0f, 1f);
        mAnimatorY.setDuration(durationMillis);
        mAnimatorY.addUpdateListener(this);
        mAnimatorY.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator va) {

        // redraw everything after animation value change
        invalidate();

        // Log.i(LOG_TAG, "UPDATING, x: " + mPhaseX + ", y: " + mPhaseY);
    }

    /**
     * This gets the y-phase that is used to animate the values.
     * 
     * @return
     */
    public float getPhaseY() {
        return mPhaseY;
    }

    /**
     * This modifys the y-phase that is used to animate the values.
     * 
     * @param phase
     */
    public void setPhaseY(float phase) {
        mPhaseY = phase;
    }

    /**
     * This gets the x-phase that is used to animate the values.
     * 
     * @return
     */
    public float getPhaseX() {
        return mPhaseX;
    }

    /**
     * This modifys the x-phase that is used to animate the values.
     * 
     * @param phase
     */
    public void setPhaseX(float phase) {
        mPhaseX = phase;
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW THIS FOR DYNAMICALLY ADDING ENTRIES AND DATASETS */

    // public void addEntry(Entry e, int dataSetIndex) {
    // mOriginalData.getDataSetByIndex(dataSetIndex).addEntry(e);
    //
    // prepare();
    // calcMinMax(false);
    // prepareMatrix();
    // calculateOffsets();
    // }
    //
    // public void addEntry(Entry e, String label) {
    // mOriginalData.getDataSetByLabel(label, false).addEntry(e);
    //
    // prepare();
    // calcMinMax(false);
    // prepareMatrix();
    // calculateOffsets();
    // }

    public void addDataSet(DataSet d) {
        mOriginalData.addDataSet(d);

        prepare();
        calcMinMax(false);
        prepareMatrix();
        calculateOffsets();
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW THIS ONLY GETTERS AND SETTERS */

    /**
     * set a selection listener for the chart
     * 
     * @param l
     */
    public void setOnChartValueSelectedListener(OnChartValueSelectedListener l) {
        this.mSelectionListener = l;
    }

    /**
     * If set to true, value highlighting is enabled which means that values can
     * be highlighted programmatically or by touch gesture.
     * 
     * @param enabled
     */
    public void setHighlightEnabled(boolean enabled) {
        mHighlightEnabled = enabled;
    }

    /**
     * returns true if highlighting of values is enabled, false if not
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
     * 
     * @return
     */
    public PointF getCenterOffsets() {
        return new PointF(mContentRect.left + mContentRect.width() / 2, mContentRect.top
                + mContentRect.height() / 2);
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
     * Sets the text that informs the user that there is no data available with
     * which to draw the chart.
     * 
     * @param text
     */
    public void setNoDataText(String text) {
        mNoDataText = text;
    }

    /**
     * Sets descriptive text to explain to the user why there is no chart
     * available Defaults to empty if not set
     * 
     * @param text
     */
    public void setNoDataTextDescription(String text) {
        mNoDataTextDescription = text;
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
     * Set this to false to disable all gestures and touches on the chart,
     * default: true
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
     * if set to true, units are drawn next to values in the chart, default:
     * false
     * 
     * @param enabled
     */
    public void setDrawUnitsInChart(boolean enabled) {
        mDrawUnitInChart = enabled;
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
     * Returns the unit that is used for the values in the chart
     * 
     * @return
     */
    public String getUnit() {
        return mUnit;
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

    /** paint object used for the limit lines */
    public static final int PAINT_LIMIT_LINE = 19;

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
     * 
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
    public void setValueTextColor(int color) {
        mValuePaint.setColor(color);
    }

    /**
     * Sets the font size of the values that are drawn inside the chart.
     * 
     * @param size
     */
    public void setValueTextSize(float size) {
        mValuePaint.setTextSize(Utils.convertDpToPixel(size));
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
     * Returns the ChartData object the chart CURRENTLY represents (not
     * dependant on zoom level). It contains all values and information the
     * chart displays. If filtering algorithms have been applied, this returns
     * the filtered state of data.
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
     * sets the background color for the chart --> this also sets the color the
     * canvas is cleared with
     */
    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);

        mBackgroundColor = color;
    }

    /**
     * Saves the chart with the given name to the given path on the sdcard
     * leaving the path empty "" will put the saved file directly on the SD card
     * chart is saved as a PNG image, example: saveToPath("myfilename",
     * "foldername1/foldername2");
     * 
     * @param title
     * @param pathOnSD e.g. "folder1/folder2/folder3"
     * @return returns true on success, false on error
     */
    public boolean saveToPath(String title, String pathOnSD) {

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
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Saves the current state of the chart to the gallery as a JPEG image. The
     * filename and compression can be set. 0 == maximum compression, 100 = low
     * compression (high quality). NOTE: Needs permission WRITE_EXTERNAL_STORAGE
     * 
     * @param fileName e.g. "my_image"
     * @param quality e.g. 50, min = 0, max = 100
     * @return returns true if saving was successfull, false if not
     */
    public boolean saveToGallery(String fileName, int quality) {

        // restrain quality
        if (quality < 0 || quality > 100)
            quality = 50;

        long currentTime = System.currentTimeMillis();

        File extBaseDir = Environment.getExternalStorageDirectory();
        File file = new File(extBaseDir.getAbsolutePath() + "/DCIM");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return false;
            }
        }

        String filePath = file.getAbsolutePath() + "/" + fileName;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);

            mDrawBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out); // control
            // the jpeg
            // quality

            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        long size = new File(filePath).length();

        ContentValues values = new ContentValues(8);

        values.put(Images.Media.TITLE, fileName);
        values.put(Images.Media.DISPLAY_NAME, fileName);
        values.put(Images.Media.DATE_ADDED, currentTime);
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put(Images.Media.DESCRIPTION, "MPAndroidChart-Library Save");
        values.put(Images.Media.ORIENTATION, 0);
        values.put(Images.Media.DATA, filePath);
        values.put(Images.Media.SIZE, size);

        return getContext().getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values) == null
                ? false : true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /** flag indicating if the matrix has alerady been prepared */
    private boolean mMatrixOnLayoutPrepared = false;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        prepareContentRect();
        Log.i(LOG_TAG,
                "onLayout(), width: " + mContentRect.width() + ", height: " + mContentRect.height());

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

    // @Override
    // protected void onAttachedToWindow() {
    // super.onAttachedToWindow();
    // if (isInEditMode()) {
    // initWithDummyData();
    // }
    // }
}
