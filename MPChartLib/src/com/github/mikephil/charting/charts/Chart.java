
package com.github.mikephil.charting.charts;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.ChartInterface;
import com.github.mikephil.charting.interfaces.OnChartGestureListener;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.renderer.Transformer;
import com.github.mikephil.charting.renderer.ViewPortHandler;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.Legend.LegendPosition;
import com.github.mikephil.charting.utils.MarkerView;
import com.github.mikephil.charting.utils.SelInfo;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Baseclass of all Chart-Views.
 *
 * @author Philipp Jahoda
 */
public abstract class Chart<T extends ChartData<? extends DataSet<? extends Entry>>> extends
        ViewGroup
        implements AnimatorUpdateListener, ChartInterface {

    public static final String LOG_TAG = "MPChart";

    /** flag that indicates if logging is enabled or not */
    protected boolean mLogEnabled = false;

    /**
     * string that is drawn next to the values in the chart, indicating their
     * unit
     */
    protected String mUnit = "";

    /** custom formatter that is used instead of the auto-formatter if set */
    protected ValueFormatter mValueFormatter = null;

    /**
     * flag that indicates if the default formatter should be used or if a
     * custom one is set
     */
    private boolean mUseDefaultFormatter = true;

    /**
     * object that holds all data that was originally set for the chart, before
     * it was modified or any filtering algorithms had been applied
     */
    protected T mData = null;

    /** the canvas that is used for drawing on the bitmap */
    protected Canvas mDrawCanvas;

    /** the lowest value the chart can display */
    protected float mYChartMin = 0.0f;

    /** the highest value the chart can display */
    protected float mYChartMax = 0.0f;

    /** paint for the x-label values */
    protected Paint mXLabelPaint;

    /** paint for the y-label values */
    protected Paint mYLabelPaint;

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
    protected String mDescription = "Description";

    /** flag that indicates if the chart has been fed with data yet */
    protected boolean mDataNotSet = true;

    /** if true, units are drawn next to the values in the chart */
    protected boolean mDrawUnitInChart = false;

    /** the range of y-values the chart displays */
    protected float mDeltaY = 1f;

    /** the number of x-values the chart displays */
    protected float mDeltaX = 1f;

    /** if true, touch gestures are enabled on the chart */
    protected boolean mTouchEnabled = true;

    /** if true, value highlightning is enabled */
    protected boolean mHighlightEnabled = true;

    /** flag indicating if the legend is drawn of not */
    protected boolean mDrawLegend = true;

    /** the legend object containing all data associated with the legend */
    protected Legend mLegend;

    /** listener that is called when a value on the chart is selected */
    protected OnChartValueSelectedListener mSelectionListener;

    /** text that is displayed when the chart is empty */
    private String mNoDataText = "No chart data available.";

    /**
     * Gesture listener for custom callbacks when making gestures on the chart.
     */
    private OnChartGestureListener mGestureListener;

    /**
     * text that is displayed when the chart is empty that describes why the
     * chart is empty
     */
    private String mNoDataTextDescription;

    protected DataRenderer mRenderer;

    protected ViewPortHandler mViewPortHandler;

    protected ChartAnimator mAnimator;

    protected float yyy = 1f;
    protected float xxx = 1f;

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

        Log.i("", "Chart.init()");

        setWillNotDraw(false);
        // setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mAnimator = new ChartAnimator(this);

        // initialize the utils
        Utils.init(getContext().getResources());

        mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRenderPaint.setStyle(Style.FILL);

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

        mXLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mXLabelPaint.setColor(Color.BLACK);
        mXLabelPaint.setTextAlign(Align.CENTER);
        mXLabelPaint.setTextSize(Utils.convertDpToPixel(10f));

        mYLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mYLabelPaint.setColor(Color.BLACK);
        mYLabelPaint.setTextSize(Utils.convertDpToPixel(10f));

        mDrawPaint = new Paint(Paint.DITHER_FLAG);
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

    /**
     * Sets a new data object for the chart. The data object contains all values
     * and information needed for displaying.
     *
     * @param data
     */
    public void setData(T data) {

        // if (data == null || !data.isValid()) {
        // Log.e(LOG_TAG,
        // "Cannot set data for chart. Provided chart values are null or contain less than 1 entry.");
        // mDataNotSet = true;
        // return;
        // }

        if (data == null) {
            Log.e(LOG_TAG,
                    "Cannot set data for chart. Provided data object is null.");
            return;
        }

        // Log.i(LOG_TAG, "xvalcount: " + data.getXValCount());
        // Log.i(LOG_TAG, "entrycount: " + data.getYValCount());

        // LET THE CHART KNOW THERE IS DATA
        mDataNotSet = false;
        mOffsetsCalculated = false;
        mData = data;
        mData = data;

        prepare();

        // calculate how many digits are needed
        calcFormats();

        Log.i(LOG_TAG, "Data is set.");
    }

    /**
     * Clears the chart from all data and refreshes it (by calling
     * invalidate()).
     */
    public void clear() {
        mData = null;
        mData = null;
        mDataNotSet = true;
        invalidate();
    }

    /**
     * Returns true if the chart is empty (meaning it's data object is either
     * null or contains no entries).
     *
     * @return
     */
    public boolean isEmpty() {

        if (mData == null)
            return true;
        else {

            if (mData.getYValCount() <= 0)
                return true;
            else
                return false;
        }
    }

    /**
     * does needed preparations for drawing
     */
    public abstract void prepare();

    /**
     * Lets the chart know its underlying data has changed and performs all
     * necessary recalculations.
     */
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
            mYChartMin = mData.getYMin();
            mYChartMax = mData.getYMax();
        }

        // calc delta
        mDeltaY = Math.abs(mYChartMax - mYChartMin);
        mDeltaX = mData.getXVals().size() - 1;
    }

    /**
     * calculates the required number of digits for the values that might be
     * drawn in the chart (if enabled)
     */
    protected void calcFormats() {

        // check if a custom formatter is set or not
        if (mUseDefaultFormatter) {

            float reference = 0f;

            if (mData == null || mData.getXValCount() < 2) {

                reference = Math.max(Math.abs(mYChartMin), Math.abs(mYChartMax));
            } else {
                reference = mDeltaY;
            }

            int digits = Utils.getDecimals(reference);

            StringBuffer b = new StringBuffer();
            for (int i = 0; i < digits; i++) {
                if (i == 0)
                    b.append(".");
                b.append("0");
            }

            DecimalFormat formatter = new DecimalFormat("###,###,###,##0" + b.toString());
            mValueFormatter = new DefaultValueFormatter(formatter);
        }
    }

    /** flag that indicates if offsets calculation has already been done or not */
    private boolean mOffsetsCalculated = false;

    /**
     * Bitmap object used for drawing. This is necessary because hardware
     * acceleration uses OpenGL which only allows a specific texture size to be
     * drawn on the canvas directly.
     **/
    protected Bitmap mDrawBitmap;

    /** paint object used for drawing the bitmap */
    protected Paint mDrawPaint;

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);

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

        if (!mOffsetsCalculated) {

            calculateOffsets();
            mOffsetsCalculated = true;
        }

        if (mDrawBitmap == null || mDrawCanvas == null) {

            mDrawBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_4444);
            mDrawCanvas = new Canvas(mDrawBitmap);
        }

        // clear everything
        mDrawBitmap.eraseColor(Color.TRANSPARENT);

        // mDrawCanvas.drawColor(Color.WHITE);
        // canvas.drawColor(Color.TRANSPARENT,
        // android.graphics.PorterDuff.Mode.XOR); // clear all
    }

    // /**
    // * sets up the content rect that restricts the chart surface
    // */
    // protected void prepareContentRect(float offL, float offR, float offT,
    // float offB) {
    // mViewPortHandler.restrainViewPort(offL, offR, offT, offB);
    // // mContentRect.set(mOffsetLeft,
    // // mOffsetTop,
    // // getWidth() - mOffsetRight,
    // // getHeight() - mOffsetBottom);
    // }

    /**
     * Generates an automatically prepared legend depending on the DataSets in
     * the chart and their colors.
     */
    public void prepareLegend() {

        ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Integer> colors = new ArrayList<Integer>();

        // loop for building up the colors and labels used in the legend
        for (int i = 0; i < mData.getDataSetCount(); i++) {

            DataSet<? extends Entry> dataSet = mData.getDataSetByIndex(i);

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
                colors.add(-2);
                labels.add(bds.getLabel());

            } else if (dataSet instanceof PieDataSet) {

                ArrayList<String> xVals = mData.getXVals();
                PieDataSet pds = (PieDataSet) dataSet;

                for (int j = 0; j < clrs.size() && j < entryCount && j < xVals.size(); j++) {

                    labels.add(xVals.get(j));
                    colors.add(clrs.get(j));
                }

                // add the legend description label
                colors.add(-2);
                labels.add(pds.getLabel());

            } else { // all others

                for (int j = 0; j < clrs.size() && j < entryCount; j++) {

                    // if multiple colors are set for a DataSet, group them
                    if (j < clrs.size() - 1 && j < entryCount - 1) {

                        labels.add(null);
                    } else { // add label to the last entry

                        String label = mData.getDataSetByIndex(i).getLabel();
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
     * draws the legend
     */
    protected void drawLegend() {

        if (!mDrawLegend || mLegend == null || mLegend.getPosition() == LegendPosition.NONE)
            return;

        String[] labels = mLegend.getLegendLabels();
        Typeface tf = mLegend.getTypeface();

        if (tf != null)
            mLegendLabelPaint.setTypeface(tf);

        mLegendLabelPaint.setTextSize(mLegend.getTextSize());
        mLegendLabelPaint.setColor(mLegend.getTextColor());

        float formSize = mLegend.getFormSize();

        // space between text and shape/form of entry
        float formTextSpaceAndForm = mLegend.getFormToTextSpace() + formSize;

        // space between the entries
        float stackSpace = mLegend.getStackSpace();

        float textSize = mLegend.getTextSize();

        // the amount of pixels the text needs to be set down to be on the same
        // height as the form
        float textDrop = (Utils.calcTextHeight(mLegendLabelPaint, "AQJ") + formSize) / 2f;

        float posX, posY;

        // contains the stacked legend size in pixels
        float stack = 0f;

        boolean wasStacked = false;

        switch (mLegend.getPosition()) {
            case BELOW_CHART_LEFT:

                posX = mLegend.getOffsetLeft();
                posY = getHeight() - mLegend.getOffsetBottom() / 2f - formSize / 2f;

                for (int i = 0; i < labels.length; i++) {

                    mLegend.drawForm(mDrawCanvas, posX, posY, mLegendFormPaint, i);

                    // grouped forms have null labels
                    if (labels[i] != null) {

                        // make a step to the left
                        if (mLegend.getColors()[i] != -2)
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

                posX = mViewPortHandler.contentRight();
                posY = getHeight() - mLegend.getOffsetBottom() / 2f - formSize / 2f;

                for (int i = labels.length - 1; i >= 0; i--) {

                    if (labels[i] != null) {

                        posX -= Utils.calcTextWidth(mLegendLabelPaint, labels[i])
                                + mLegend.getXEntrySpace();
                        mLegend.drawLabel(mDrawCanvas, posX, posY + textDrop, mLegendLabelPaint, i);
                        if (mLegend.getColors()[i] != -2)
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

                for (int i = 0; i < labels.length; i++) {

                    mLegend.drawForm(mDrawCanvas, posX + stack, posY, mLegendFormPaint, i);

                    if (labels[i] != null) {

                        if (!wasStacked) {

                            float x = posX;

                            if (mLegend.getColors()[i] != -2)
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
            case RIGHT_OF_CHART_CENTER:
                posX = getWidth() - mLegend.getMaximumEntryLength(mLegendLabelPaint)
                        - formTextSpaceAndForm;
                posY = getHeight() / 2f - mLegend.getFullHeight(mLegendLabelPaint) / 2f;

                for (int i = 0; i < labels.length; i++) {

                    mLegend.drawForm(mDrawCanvas, posX + stack, posY, mLegendFormPaint, i);

                    if (labels[i] != null) {

                        if (!wasStacked) {

                            float x = posX;

                            if (mLegend.getColors()[i] != -2)
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
                        if (mLegend.getColors()[i] != -2)
                            posX += formTextSpaceAndForm;

                        mLegend.drawLabel(mDrawCanvas, posX, posY + textDrop, mLegendLabelPaint, i);
                        posX += Utils.calcTextWidth(mLegendLabelPaint, labels[i])
                                + mLegend.getXEntrySpace();
                    } else {
                        posX += formSize + stackSpace;
                    }
                }

                // Log.i(LOG_TAG, "content bottom: " + mContentRect.bottom +
                // ", height: "
                // + getHeight() + ", posY: " + posY + ", formSize: " +
                // formSize);

                break;
            case PIECHART_CENTER:

                posX = getWidth()
                        / 2f
                        - (mLegend.getMaximumEntryLength(mLegendLabelPaint) + mLegend
                                .getXEntrySpace())
                        / 2f;
                posY = getHeight() / 2f - mLegend.getFullHeight(mLegendLabelPaint) / 2f;

                for (int i = 0; i < labels.length; i++) {

                    mLegend.drawForm(mDrawCanvas, posX + stack, posY, mLegendFormPaint, i);

                    if (labels[i] != null) {

                        if (!wasStacked) {

                            float x = posX;

                            if (mLegend.getColors()[i] != -2)
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
            case RIGHT_OF_CHART_INSIDE:

                posX = getWidth() - mLegend.getMaximumEntryLength(mLegendLabelPaint)
                        - formTextSpaceAndForm;
                posY = mLegend.getOffsetTop();

                for (int i = 0; i < labels.length; i++) {

                    mLegend.drawForm(mDrawCanvas, posX + stack, posY, mLegendFormPaint, i);

                    if (labels[i] != null) {

                        if (!wasStacked) {

                            float x = posX;

                            if (mLegend.getColors()[i] != -2)
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
            case NONE:
                break;
        }
    }

    /**
     * draws the description text in the bottom right corner of the chart
     */
    protected void drawDescription() {

        mDrawCanvas
                .drawText(mDescription, getWidth() - mViewPortHandler.offsetRight() - 10,
                        getHeight() - mViewPortHandler.offsetBottom()
                                - 10, mDescPaint);
    }

    /**
     * draws all the text-values to the chart
     */
    protected abstract void drawValues();

    /**
     * Draws the DataSet at the given index.
     * 
     * @param index
     */
    protected abstract void drawDataSet(int index);

    /**
     * draws additional stuff, whatever that might be
     */
    protected abstract void drawAdditional();
    
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
     * Highlights the value at the given x-index in the given DataSet. Provide
     * -1 as the x-index to undo all highlighting.
     *
     * @param xIndex
     * @param dataSetIndex
     */
    public void highlightValue(int xIndex, int dataSetIndex) {

        if (xIndex < 0 || dataSetIndex < 0 || xIndex >= mData.getXValCount()
                || dataSetIndex >= mData.getDataSetCount()) {

            highlightValues(null);
        } else {
            highlightValues(new Highlight[] {
                    new Highlight(xIndex, dataSetIndex)
            });
        }
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
            int dataSetIndex = mIndicesToHightlight[i].getDataSetIndex();

            if (xIndex <= mDeltaX && xIndex <= mDeltaX * mAnimator.getPhaseX()) {

                Entry e = getEntryByDataSetIndex(xIndex, dataSetIndex);

                // make sure entry not null
                if (e == null)
                    continue;

                float[] pos = getMarkerPosition(e, dataSetIndex);

                // check bounds
                if (!mViewPortHandler.isInBounds(pos[0], pos[1]))
                    continue;

                // callbacks to update the content
                mMarkerView.refreshContent(e, dataSetIndex);

                // mMarkerView.measure(MeasureSpec.makeMeasureSpec(0,
                // MeasureSpec.UNSPECIFIED),
                // MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                // mMarkerView.layout(0, 0, mMarkerView.getMeasuredWidth(),
                // mMarkerView.getMeasuredHeight());
                // mMarkerView.draw(mDrawCanvas, pos[0], pos[1]);

                mMarkerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                mMarkerView.layout(0, 0, mMarkerView.getMeasuredWidth(),
                        mMarkerView.getMeasuredHeight());

                if (pos[1] - mMarkerView.getHeight() <= 0) {
                    float y = mMarkerView.getHeight() - pos[1];
                    mMarkerView.draw(mDrawCanvas, pos[0], pos[1] + y);
                } else {
                    mMarkerView.draw(mDrawCanvas, pos[0], pos[1]);
                }
            }
        }
    }

    /**
     * Returns the actual position in pixels of the MarkerView for the given
     * Entry in the given DataSet.
     *
     * @param xIndex
     * @param dataSetIndex
     * @return
     */
    private float[] getMarkerPosition(Entry e, int dataSetIndex) {

        float xPos = e.getXIndex();

        // make sure the marker is in the center of the bars in BarChart and
        // CandleStickChart
        if (this instanceof CandleStickChart)
            xPos += 0.5f;

        else if (this instanceof BarChart) {

            BarData bd = (BarData) mData;
            float space = bd.getGroupSpace();
            float j = mData.getDataSetByIndex(dataSetIndex)
                    .getEntryPosition(e);

            float x = (j * (mData.getDataSetCount() - 1)) + dataSetIndex + space * j + space
                    / 2f + 0.5f;

            xPos += x;
        } else if (this instanceof RadarChart) {

            RadarChart rc = (RadarChart) this;
            float angle = rc.getSliceAngle() * e.getXIndex() + rc.getRotationAngle();
            float val = e.getVal() * rc.getFactor();
            PointF c = getCenterOffsets();

            PointF p = new PointF((float) (c.x + val * Math.cos(Math.toRadians(angle))),
                    (float) (c.y + val * Math.sin(Math.toRadians(angle))));

            return new float[] {
                    p.x, p.y
            };
        }

        // position of the marker depends on selected value index and value
        float[] pts = new float[] {
                xPos, e.getVal() * mAnimator.getPhaseY()
        };

        mTrans.pointValuesToPixel(pts);

        return pts;
    }

    /**
     * ################ ################ ################ ################
     * Animation support below Honeycomb thanks to Jake Wharton's awesome
     * nineoldandroids library: https://github.com/JakeWharton/NineOldAndroids
     */
    /** CODE BELOW THIS RELATED TO ANIMATION */

    /**
     * Returns the animator responsible for animating chart values.
     * 
     * @return
     */
    public ChartAnimator getAnimator() {
        return mAnimator;
    }

    /**
     * Animates the drawing / rendering of the chart on both x- and y-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart.
     *
     * @param durationMillisX
     * @param durationMillisY
     */
    public void animateXY(int durationMillisX, int durationMillisY) {
        mAnimator.animateXY(durationMillisX, durationMillisY);
    }

    /**
     * Animates the rendering of the chart on the x-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     *
     * @param durationMillis
     */
    public void animateX(int durationMillis) {
        mAnimator.animateX(durationMillis);
    }

    /**
     * Animates the rendering of the chart on the y-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart.
     *
     * @param durationMillis
     */
    public void animateY(int durationMillis) {
        mAnimator.animateY(durationMillis);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator va) {

        // redraw everything after animation value change
        // invalidate();

        ViewCompat.postInvalidateOnAnimation(this);

        // Log.i(LOG_TAG, "UPDATING, x: " + mPhaseX + ", y: " + mPhaseY);
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
    //
    // public void addDataSet(DataSet d) {
    // mOriginalData.addDataSet(d);
    //
    // prepare();
    // calcMinMax(false);
    // prepareMatrix();
    // calculateOffsets();
    // }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW THIS ONLY GETTERS AND SETTERS */

    /**
     * Returns the canvas object the chart uses for drawing.
     *
     * @return
     */
    public Canvas getCanvas() {
        return mDrawCanvas;
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
     * Sets a gesture-listener for the chart for custom callbacks when executing
     * gestures on the chart surface.
     *
     * @param l
     */
    public void setOnChartGestureListener(OnChartGestureListener l) {
        this.mGestureListener = l;
    }

    /**
     * Returns the custom gesture listener.
     *
     * @return
     */
    public OnChartGestureListener getOnChartGestureListener() {
        return mGestureListener;
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
        return mData.getYValueSum();
    }

    /**
     * returns the current y-max value across all DataSets
     *
     * @return
     */
    public float getYMax() {
        return mData.getYMax();
    }

    /**
     * returns the lowest value the chart can display
     *
     * @return
     */
    @Override
    public float getYChartMin() {
        return mYChartMin;
    }

    /**
     * returns the highest value the chart can display
     *
     * @return
     */
    @Override
    public float getYChartMax() {
        return mYChartMax;
    }

    /**
     * returns the current y-min value across all DataSets
     *
     * @return
     */
    public float getYMin() {
        return mData.getYMin();
    }

    /**
     * Get the total number of X-values.
     *
     * @return
     */
    @Override
    public float getDeltaX() {
        return mDeltaX;
    }

    /**
     * Returns the total range of values (on y-axis) the chart displays.
     *
     * @return
     */
    @Override
    public float getDeltaY() {
        return mDeltaY;
    }

    /**
     * returns the average value of all values the chart holds
     *
     * @return
     */
    public float getAverage() {
        return getYValueSum() / mData.getYValCount();
    }

    /**
     * returns the average value for a specific DataSet (with a specific label)
     * in the chart
     *
     * @param dataSetLabel
     * @return
     */
    public float getAverage(String dataSetLabel) {

        DataSet<? extends Entry> ds = mData.getDataSetByLabel(dataSetLabel, true);

        return ds.getYValueSum()
                / ds.getEntryCount();
    }

    /**
     * returns the total number of values the chart holds (across all DataSets)
     *
     * @return
     */
    public int getValueCount() {
        return mData.getYValCount();
    }

    /**
     * Returns the center point of the chart (the whole View) in pixels.
     *
     * @return
     */
    public PointF getCenter() {
        return new PointF(getWidth() / 2f, getHeight() / 2f);
    }

    /**
     * Returns the center of the chart taking offsets under consideration.
     * (returns the center of the content rectangle)
     *
     * @return
     */
    @Override
    public PointF getCenterOffsets() {
        return mViewPortHandler.getContentCenter();
    }

    /**
     * sets the size of the description text in pixels, min 6f, max 16f
     *
     * @param size
     */
    public void setDescriptionTextSize(float size) {

        if (size > 16f)
            size = 16f;
        if (size < 6f)
            size = 6f;

        mDescPaint.setTextSize(Utils.convertDpToPixel(size));
    }

    /**
     * Set this to true to enable logcat outputs for the chart. Default:
     * disabled
     *
     * @param enabled
     */
    public void setLogEnabled(boolean enabled) {
        mLogEnabled = enabled;
    }

    /**
     * set a description text that appears in the bottom right corner of the
     * chart, size = Y-legend text size
     *
     * @param desc
     */
    public void setDescription(String desc) {
        if (desc == null)
            desc = "";
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

    // /**
    // * Sets the offsets from the border of the view to the actual chart in
    // every
    // * direction manually. This method needs to be recalled everytime a new
    // data
    // * object is set for the chart. Provide density pixels -> they are then
    // * rendered to pixels inside the chart.
    // *
    // * @param left
    // * @param right
    // * @param top
    // * @param bottom
    // */
    // public void setOffsets(float left, float top, float right, float bottom)
    // {
    //
    // mOffsetBottom = Utils.convertDpToPixel(bottom);
    // mOffsetLeft = Utils.convertDpToPixel(left);
    // mOffsetRight = Utils.convertDpToPixel(right);
    // mOffsetTop = Utils.convertDpToPixel(top);
    //
    // mTrans.prepareMatrixValuePx(this);
    // mTrans.prepareMatrixOffset(this);
    // prepareContentRect();
    // }

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

    /**
     * Returns the rectangle that defines the borders of the chart-value surface
     * (into which the actual values are drawn).
     *
     * @return
     */
    @Override
    public RectF getContentRect() {
        return mViewPortHandler.getContentRect();
    }

    /**
     * disables intercept touchevents
     */
    public void disableScroll() {
        ViewParent parent = getParent();
        if (parent != null)
            parent.requestDisallowInterceptTouchEvent(true);
    }

    /**
     * enables intercept touchevents
     */
    public void enableScroll() {
        ViewParent parent = getParent();
        if (parent != null)
            parent.requestDisallowInterceptTouchEvent(false);
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
    public static final int PAINT_HIGHLIGHT = 15;

    /** paint object used for the limit lines */
    public static final int PAINT_RADAR_WEB = 16;

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
            case PAINT_XLABEL:
                mXLabelPaint = p;
                break;
            case PAINT_YLABEL:
                mYLabelPaint = p;
                break;
            case PAINT_HIGHLIGHT:
//                mHighlightPaint = p;
                break;
            case PAINT_LIMIT_LINE:
//                mLimitLinePaint = p;
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
            case PAINT_XLABEL:
                return mXLabelPaint;
            case PAINT_YLABEL:
                return mYLabelPaint;
            case PAINT_HIGHLIGHT:
//                return mHighlightPaint;
            case PAINT_LIMIT_LINE:
//                return mLimitLinePaint;
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
     * Sets the formatter to be used for drawing the values inside the chart. If
     * no formatter is set, the chart will automatically determine a reasonable
     * formatting (concerning decimals) for all the values that are drawn inside
     * the chart. Set this to NULL to re-enable auto formatting.
     *
     * @param f
     */
    public void setValueFormatter(ValueFormatter f) {
        mValueFormatter = f;

        if (f == null)
            mUseDefaultFormatter = true;
        else
            mUseDefaultFormatter = false;
    }

    /**
     * Returns the formatter used for drawing the values inside the chart.
     *
     * @return
     */
    public ValueFormatter getValueFormatter() {
        return mValueFormatter;
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
     * returns the x-value at the given index
     *
     * @param index
     * @return
     */
    public String getXValue(int index) {
        if (mData == null || mData.getXValCount() <= index)
            return null;
        else
            return mData.getXVals().get(index);
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
        DataSet<? extends Entry> set = mData.getDataSetByLabel(dataSetLabel, true);
        return set.getYVals().get(index).getVal();
    }

    /**
     * returns the y-value for the given x-index and DataSet index
     *
     * @param index
     * @param dataSet
     * @return
     */
    public float getYValue(int xIndex, int dataSetIndex) {
        DataSet<? extends Entry> set = mData.getDataSetByIndex(dataSetIndex);
        return set.getYValForXIndex(xIndex);
    }

    /**
     * returns the DataSet with the given index in the DataSet array held by the
     * ChartData object.
     *
     * @param index
     * @return
     */
    public DataSet<? extends Entry> getDataSetByIndex(int index) {
        return mData.getDataSetByIndex(index);
    }

    /**
     * returns the DataSet with the given label that is stored in the ChartData
     * object.
     *
     * @param type
     * @return
     */
    public DataSet<? extends Entry> getDataSetByLabel(String dataSetLabel) {
        return mData.getDataSetByLabel(dataSetLabel, true);
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
        return mData.getDataSetByIndex(0).getYVals().get(index);
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
        return mData.getDataSetByLabel(dataSetLabel, true).getYVals().get(index);
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
        return mData.getDataSetByIndex(dataSetIndex).getEntryForXIndex(xIndex);
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
    public ArrayList<SelInfo> getYValsAtIndex(int xIndex) {

        ArrayList<SelInfo> vals = new ArrayList<SelInfo>();

        for (int i = 0; i < mData.getDataSetCount(); i++) {

            // extract all y-values from all DataSets at the given x-index
            float yVal = mData.getDataSetByIndex(i).getYValForXIndex(xIndex);

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

        for (int i = 0; i < mData.getDataSetCount(); i++) {

            DataSet<? extends Entry> set = mData.getDataSetByIndex(i);

            Entry e = set.getEntryForXIndex(xIndex);

            if (e != null) {
                vals.add(e);
            }
        }

        return vals;
    }

    /**
     * Returns the ChartData object that ORIGINALLY has been set for the chart.
     * It contains all data in an unaltered state, before any filtering
     * algorithms have been applied.
     *
     * @return
     */
    public T getData() {
        return mData;
    }

    /**
     * returns the percentage the given value has of the total y-value sum
     *
     * @param val
     * @return
     */
    public float getPercentOfTotal(float val) {
        return val / mData.getYValueSum() * 100f;
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
     * Returns the ViewPortHandler of the chart that is responsible for the
     * content area of the chart and its offsets and dimensions.
     * 
     * @return
     */
    public ViewPortHandler getViewPortHandler() {
        return mViewPortHandler;
    }

    /**
     * Returns the bitmap that represents the chart.
     *
     * @return
     */
    public Bitmap getChartBitmap() {
        // Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
        // Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        // Get the view's background
        Drawable bgDrawable = getBackground();
        if (bgDrawable != null)
            // has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            // does not have background drawable, then draw white background on
            // the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        draw(canvas);
        // return the bitmap
        return returnedBitmap;
    }

    /**
     * Saves the current chart state with the given name to the given path on
     * the sdcard leaving the path empty "" will put the saved file directly on
     * the SD card chart is saved as a PNG image, example:
     * saveToPath("myfilename", "foldername1/foldername2");
     *
     * @param title
     * @param pathOnSD e.g. "folder1/folder2/folder3"
     * @return returns true on success, false on error
     */
    public boolean saveToPath(String title, String pathOnSD) {

        Bitmap b = getChartBitmap();

        OutputStream stream = null;
        try {
            stream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()
                    + pathOnSD + "/" + title
                    + ".png");

            /*
             * Write bitmap to file using JPEG or PNG and 40% quality hint for
             * JPEG.
             */
            b.compress(CompressFormat.PNG, 40, stream);

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

            Bitmap b = getChartBitmap();

            b.compress(Bitmap.CompressFormat.JPEG, quality, out); // control
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

        // store the details
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

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        prepareContentRect();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout(left, top, right, bottom);
        }
        //
        // prepareContentRect();
        // Log.i(LOG_TAG,
        // "onLayout(), width: " + mContentRect.width() + ", height: " +
        // mContentRect.height());
        //
        // calculateOffsets();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.i("", "OnSizeChanged()");

        if (w > 0 && h > 0 && w < 10000 && h < 10000) {
            // create a new bitmap with the new dimensions
            mDrawBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
            mDrawCanvas = new Canvas(mDrawBitmap);
            mViewPortHandler = new ViewPortHandler(w, h);
        }

        // prepare content rect and matrices
        prepareContentRect();
        prepare();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Default formatter used for formatting values. Uses a DecimalFormat with
     * pre-calculated number of digits (depending on max and min value).
     *
     * @author Philipp Jahoda
     */
    private class DefaultValueFormatter implements ValueFormatter {

        /** decimalformat for formatting */
        private DecimalFormat mFormat;

        public DefaultValueFormatter(DecimalFormat f) {
            mFormat = f;
        }

        @Override
        public String getFormattedValue(float value) {
            // avoid memory allocations here (for performance)
            return mFormat.format(value);
        }
    }

    @Override
    public View getChartView() {
        return this;
    }

    @Override
    public PointF getCenterOfView() {
        return getCenter();
    }

    // @Override
    // protected void onAttachedToWindow() {
    // super.onAttachedToWindow();
    // if (isInEditMode()) {
    // initWithDummyData();
    // }
    // }
}
