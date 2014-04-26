
package com.github.mikephil.charting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * A simple Chart-View. Standalone.
 * 
 * @author Philipp Jahoda
 */
public abstract class Chart extends View {

    protected static final String LOG_TAG = "MPChart";
    
    protected int mColorDarkBlue = Color.rgb(41, 128, 186);
    protected int mColorDarkRed = Color.rgb(232, 76, 59);
    
    /** the total sum of all y-values */
    protected float mYValueSum = 0f;

    /** chart offset to the left */
    protected int mOffsetLeft = 35;

    /** chart toffset to the top */
    protected int mOffsetTop = 25;

    /** chart offset to the right */
    protected int mOffsetRight = 20;

    /** chart offset to the bottom */
    protected int mOffsetBottom = 15;

    /** list that holds all values of the x-axis */
    protected ArrayList<String> mXVals;

    /** list that holds all values of the y-axis */
    protected ArrayList<Float> mYVals;

    /** final bitmap that contains all information and is drawn to the screen */
    protected Bitmap mDrawBitmap;

    /** the canvas that is used for drawing on the bitmap */
    protected Canvas mDrawCanvas;

    /** the lowest value the chart can display */
    protected float mYChartMin = 0.0f;
    
    /** the highest value the chart can display */
    protected float mYChartMax = 0.0f;

    /** maximum y-value in the y-value array */
    protected float mYMax = 0.0f;

    /** the minimum y-value in the y-value array */
    protected float mYMin = 0.0f;
    
    protected ColorTemplate mColorTemplate;

    protected Paint mDrawPaint;
    protected Paint mDescPaint;
    protected Paint mInfoPaint;
    protected Paint mValuePaint;
    
    protected Paint[] mDrawPaints;

    /** description text that appears in the bottom right corner of the chart */
    protected String mDescription = "Description.";
    
    /** flag that indicates if the chart has been fed with data yet */
    protected boolean mDataNotSet = true;

    /** the range of y-values the chart displays */
    protected float mDeltaY = 1f;

    /** the number of x-values the chart displays */
    protected float mDeltaX = 1f;
    
    /** contains the current scale factor of the x-axis */
    protected float mScaleX = 1f;

    /** matrix to map the values to the screen pixels */
    protected Matrix mMatrixValueToPx;

    /** matrix for handling the different offsets of the chart */
    protected Matrix mMatrixOffset;

    /** matrix used for touch events */
    protected Matrix mMatrixTouch = new Matrix();

    /** the default draw color (some kind of light blue) */
    protected int mDrawColor = Color.rgb(56, 199, 240);
    
    /** if true, touch gestures are enabled on the chart */
    protected boolean mTouchEnabled = true;

    /** if true, values are drawn on the chart */
    protected boolean mDrawValues = true;

    /** this rectangle defines the area in which graph values can be drawn */
    protected Rect mContentRect;

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

        setBackgroundColor(Color.WHITE);

        // do screen density conversions
        mOffsetBottom = (int) Utils.convertDpToPixel(mOffsetBottom);
        mOffsetLeft = (int) Utils.convertDpToPixel(mOffsetLeft);
        mOffsetRight = (int) Utils.convertDpToPixel(mOffsetRight);
        mOffsetTop = (int) Utils.convertDpToPixel(mOffsetTop);

        mXVals = new ArrayList<String>();
        mYVals = new ArrayList<Float>();

        mDrawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mDescPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDescPaint.setColor(Color.BLACK);
        mDescPaint.setTextAlign(Align.RIGHT);
        mDescPaint.setTextSize(Utils.convertDpToPixel(9f));

        mInfoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInfoPaint.setColor(Color.rgb(247, 189, 51)); // orange
        mInfoPaint.setTextAlign(Align.CENTER);
        mInfoPaint.setTextSize(Utils.convertDpToPixel(12f));

        mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint.setColor(Color.rgb(186, 89, 248)); // orange
        mValuePaint.setTextAlign(Align.CENTER);
        mValuePaint.setTextSize(Utils.convertDpToPixel(9f));
    }

    /**
     * add data to the chart the x-vals and y-vals arraylists need to be of the
     * same lentgth
     * 
     * @param xVals
     * @param yVals
     */
    public void setData(ArrayList<String> xVals, ArrayList<Float> yVals) {

        if (xVals == null || xVals.size() <= 1 || yVals == null || yVals.size() <= 1) {
            Log.e(LOG_TAG,
                    "Cannot set data for chart. Provided chart values are null or contain less than 2 entries.");
            mDataNotSet = true;
            return;
        }

        // LET THE CHART KNOW THERE IS DATA
        mDataNotSet = false;

        this.mXVals = xVals;
        this.mYVals = yVals;

        prepare();
    }
    
    /**
     * prepares all the paint objects that are used for drawing
     * @param ct
     */
    protected abstract void prepareDataPaints(ColorTemplate ct);
    
    /**
     * does needed preparations for drawing
     */
    protected abstract void prepare();

    /**
     * calcualtes the y-min and y-max value and the y-delta and x-delta value
     */
    protected void calcMinMax() {

        mYMin = mYVals.get(0);

        for (int i = 0; i < mYVals.size(); i++) {
            if (mYVals.get(i) < mYMin)
                mYMin = mYVals.get(i);
        }

        mYMax = mYVals.get(0);

        for (int i = 0; i < mYVals.size(); i++) {
            if (mYVals.get(i) > mYMax)
                mYMax = mYVals.get(i);
        }

        mYChartMin = mYMin;
        
        // calc delta
        mDeltaY = mYMax - mYChartMin;
        mYChartMax = mYChartMin + mDeltaY;

        mDeltaX = mXVals.size() - 1;
        
        calcYValueSum();
    }
    
    /**
     * calculates the sum of all y-values
     */
    private void calcYValueSum() {
        
        mYValueSum = 0;
        
        for(int i = 0; i < mYVals.size(); i++) {
            mYValueSum += Math.abs(mYVals.get(i));
        }
    }

    private boolean mFirstDraw = true;
    private boolean mContentRectSetup = false;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if(!mContentRectSetup) {
            mContentRectSetup = true;
            prepareContentRect();
        }

        if (mDataNotSet) { // check if there is data

            // if no data, inform the user
            canvas.drawText("No chart data available.", getWidth() / 2, getHeight() / 2, mInfoPaint);
            return;
        }

        if (mFirstDraw) {
            mFirstDraw = false;
            prepareMatrix();
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
        mMatrixValueToPx = new Matrix();
        mMatrixValueToPx.reset();
        mMatrixValueToPx.postTranslate(0, -mYChartMin);
        mMatrixValueToPx.postScale(scaleX, -scaleY);
        mMatrixOffset = new Matrix();
        mMatrixOffset.postTranslate(mOffsetLeft, getHeight() - mOffsetBottom);
//        mXLegendRect = new Rect(mOffsetLeft-20, 0, getWidth() - mOffsetRight + 20, mOffsetTop);
        
//        calcModulus();
    }
    
    /**
     * sets up the content rect that restricts the chart surface
     */
    protected void prepareContentRect() {
        // create the content rect
        mContentRect = new Rect(mOffsetLeft, mOffsetTop, getWidth() - mOffsetRight,
                getHeight() - mOffsetBottom);
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
     * @param paths
     */
    protected void transformPaths(ArrayList<Path> paths) {
        
        for(int i = 0; i < paths.size(); i++) {
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
     * @param r
     */
    protected void transformRect(RectF r) {

        mMatrixValueToPx.mapRect(r);
        mMatrixTouch.mapRect(r);
        mMatrixOffset.mapRect(r);
    }
    
    /**
     * transforms multiple rects with all matrices
     * @param rects
     */
    protected void transformRects(ArrayList<RectF> rects) {

        for(int i = 0; i < rects.size(); i++) transformRect(rects.get(i));
    }
    
    /**
     * transforms the given rect objects with the touch matrix only
     * @param paths
     */
    protected void transformRectsTouch(ArrayList<RectF> rects) {
        for(int i = 0; i < rects.size(); i++) {
            mMatrixTouch.mapRect(rects.get(i));
        }
    }     
    
    /**
     * transforms the given path objects with the touch matrix only
     * @param paths
     */
    protected void transformPathsTouch(ArrayList<Path> paths) {
        for(int i = 0; i < paths.size(); i++) {
            paths.get(i).transform(mMatrixTouch);
        }
    }

    /**
     * transform an array of points with all matrixes except the touch matrix
     * --> use this if the transformed values are not effected by touch gestures
     * 
     * @param pts
     */
    protected void transformPointArrayNoTouch(float[] pts) {

        mMatrixValueToPx.mapPoints(pts);
        // mMatrixTouch.mapPoints(pts);
        mMatrixOffset.mapPoints(pts);
    }
    
    /** the x-position the marker appears on */
    protected int mMarkerPosX = 100;
    
    /** the y-postion the marker appears on */
    protected int mMarkerPosY = 100;
    
    /** the view that represents the marker */
    protected RelativeLayout mMarkerView;
    
    /**
     * draws the view that is displayed when the chart is clicked
     */
    protected void drawMarkerView() {
        
        if(mMarkerView == null) return;
        
        mDrawCanvas.translate(mMarkerPosX, mMarkerPosY);
        mMarkerView.draw(mDrawCanvas);
        mDrawCanvas.translate(-mMarkerPosX, -mMarkerPosY);
    }

    /**
     * draws the description text in the bottom right corner of the chart
     */
    protected void drawDescription() {

        mDrawCanvas.drawText(mDescription, getWidth() - mOffsetRight - 10, getHeight()
                - mOffsetBottom - 10, mDescPaint);
    }
    
    /**
     * calculates the approximate width of a text, depending on a demo text
     * @param paint
     * @param demoText
     * @return
     */
    protected int calcTextWidth(Paint paint, String demoText) {
        
        Rect r = new Rect();
        paint.getTextBounds(demoText, 0, demoText.length(), r);
        return r.width();
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
     * highlights the value at the given index of the values list
     * @param indices
     */
    public abstract void highlightValues(int[] indices);

    /**
     * ################ ################ ################ ################
     */
    /** CODE BELOW THIS RELATED TO SCALING AND GESTURES */

    /** touchlistener that handles touches and gestures on the chart */
    protected OnTouchListener mListener;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        if(mListener == null) return false;
        
        // check if touch gestures are enabled
        if(!mTouchEnabled) return false;
        else return mListener.onTouch(this, event);
    }

    public void disableScroll() {
        ViewParent parent = getParent();
        parent.requestDisallowInterceptTouchEvent(true);
    }

    public void enableScroll() {
        ViewParent parent = getParent();
        parent.requestDisallowInterceptTouchEvent(false);
    }

    /**
     * call this method to refresh the graph with a given touch matrix
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
     * @param matrix
     */
    protected void limitTransAndScale(Matrix matrix) {
        
        float[] vals = new float[9];
        matrix.getValues(vals);
        
        float curTransX = vals[Matrix.MTRANS_X];
        float curScaleX = vals[Matrix.MSCALE_X];
        
        // minimum scale is 1f
        mScaleX = Math.max(1f, Math.min(getMaxScale(), curScaleX)); 
        
        float maxTransX = -(float) mContentRect.width() * (mScaleX - 1f);
        float newTransX = Math.min(Math.max(curTransX, maxTransX), 0);
        
        vals[Matrix.MTRANS_X] = newTransX;
        vals[Matrix.MSCALE_X] = mScaleX;

        matrix.setValues(vals);
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW THIS ONLY GETTERS AND SETTERS */

    /**
     * set a new (e.g. custom) charttouchlistener
     * NOTE: make sure to setTouchEnabled(true); if you need touch gestures on the chart
     * 
     * @param l
     */
    public void setOnTouchListener(OnTouchListener l) {
        this.mListener = l;
    }
        
    /**
     * returns the total value (sum) of all y-values
     * @return
     */
    public float getYValueSum() {
        return mYValueSum;
    }
    
    /**
     * returns the current x-scale value
     */
    public float getScaleX() {
        return mScaleX;
    }
    
    /**
     * calcualtes the maximum scale value depending on the number of x-values,
     * maximum scale is numberOfXvals / 2
     * @return
     */
    public float getMaxScale() {
        return mDeltaX / 2f;
    }
    
    /**
     * returns the current y-max value in the y-values array
     * 
     * @return
     */
    public float getYMax() {
        return mYMax;
    }

    /**
     * returns the current minimum y-value that is visible on the chart - bottom
     * line
     * 
     * @return
     */
    public float getYChartMin() {
        return mYChartMin;
    }
    
    /**
     * returns the current maximum y-value that is visible on the chart - can be displayed by the chart
     * @return
     */
    public float getYChartMax() {
        return mYChartMax;
    }

    /**
     * returns the current y-min value in the y-values array
     * 
     * @return
     */
    public float getYMin() {
        return mYMin;
    }
        
    /**
     * returns the center point of the chart
     * @return
     */
    public PointF getCenter() {
        return new PointF(getWidth() / 2, getHeight() / 2);
    }

    /**
     * returns the left offset of the chart in pixels
     * 
     * @return
     */
    public float getOffsetLeft() {
        return mOffsetLeft;
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
     * set a new draw color for the chart values (line and filled) default is
     * Color.rgb(56, 199, 240)
     * 
     * @param color
     */
    public void setDrawColor(int color) {
        mDrawColor = color;
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
     * sets the offsets of the graph in every direction provide density pixels
     * -> they are then rendered to pixels inside the chart
     * 
     * @param left
     * @param right
     * @param top
     * @param bottom
     */
    public void setGraphOffsets(int left, int right, int top, int bottom) {

        mOffsetBottom = (int) Utils.convertDpToPixel(bottom);
        mOffsetLeft = (int) Utils.convertDpToPixel(left);
        mOffsetRight = (int) Utils.convertDpToPixel(right);
        mOffsetTop = (int) Utils.convertDpToPixel(top);
    }
    
    /**
     * set this to false to disable gestures on the chart, default: true
     * @param enabled
     */
    public void setTouchEnabled(boolean enabled) {
        this.mTouchEnabled = enabled;
    }

    /**
     * set this to true to draw values on the chart NOTE: if more than 100
     * values are on the screen, values will not be drawn, even if enabled
     * 
     * @param enabled
     */
    public void setDrawValues(boolean enabled) {
        this.mDrawValues = enabled;
    }

//    /**
//     * set this to true to make the x-legend exactly fill the whole chart with
//     * all values being exacly correct, if the x-legend fits, it means that all
//     * values use exactly the whole chart width, this can however lead to an
//     * increased or decreased number of x-legend grid lines -> e.g. if the
//     * number of x-values is a prime number only the first and last x-legend
//     * grid line will be created. Nevertheless, the chart will always try to get
//     * as close as possible to the actually specified number of x-legend grid
//     * lines. if set to false, the chart will use exactly the specified number
//     * of x-legend grid lines. This can however lead to small incorrectness of
//     * the gridlines if the number of x-entries cannot be divided through the
//     * number of X-legend entries. default: enabled true
//     * 
//     * @param enabled
//     */
//    public void setFitXLegend(boolean enabled) {
//        this.mFitXLegend = enabled;
//        prepare();
//    }
    
    /**
     * sets the y- starting and ending value
     * @param start
     * @param end
     */
    public void setYStartEnd(float start, float end) {
        mYChartMin = start;
        mDeltaY = end - start;
    }
    
    /**
     * sets a colortemplate for the chart
     * @param ct
     */
    public void setColorTemplate(ColorTemplate ct) {
        this.mColorTemplate = ct;
        
        prepareDataPaints(ct);
    }
    
    /**
     * sets the view that is displayed when a value is clicked on the chart
     * @param v
     */
    public void setMarkerView(View v) {
        
        mMarkerView = new RelativeLayout(getContext());
        mMarkerView.addView(v);

        mMarkerView.measure(mDrawCanvas.getWidth(), mDrawCanvas.getHeight());
        mMarkerView.layout(0, 0, mDrawCanvas.getWidth(), mDrawCanvas.getHeight());
    }

    /** static fields as identifiers for all the available paint objects */

    public static final int PAINT_LINE = 1;
    public static final int PAINT_LINE_FILLED = 2;
    public static final int PAINT_GRID = 3;
    public static final int PAINT_GRID_BACKGROUND = 4;
    public static final int PAINT_YLEGEND = 5;
    public static final int PAINT_XLEGEND = 6;
    public static final int PAINT_INFO = 7;
    public static final int PAINT_VALUES = 8;
    public static final int PAINT_CIRCLES_OUTER = 9;
    public static final int PAINT_CIRCLES_INNER = 10;
    public static final int PAINT_DESCRIPTION = 11;
    public static final int PAINT_OUTLINE = 12;

    /**
     * set a new paint object for the specified parameter in the chart e.g.
     * Chart.PAINT_LINE
     * 
     * @param p --> Chart.PAINT_LINE, Chart.PAINT_GRID, Chart.PAINT_VALUES, ...
     * @param which
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
        }
    }
//
//    /**
//     * returns true if fitting x legend is enabled, false if not if the x-legend
//     * fits, it means that all values use exactly the whole chart width
//     * 
//     * @return
//     */
//    public boolean isFitXLegendEnabled() {
//        return mFitXLegend;
//    }

    /**
     * returns true if value drawing is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawValuesEnabled() {
        return mDrawValues;
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
            stream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + pathOnSD + "/" + title + ".png");

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
}
