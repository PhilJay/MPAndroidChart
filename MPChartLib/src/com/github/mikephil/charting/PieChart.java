package com.github.mikephil.charting;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.text.DecimalFormat;

public class PieChart extends Chart {
  
    private RectF mCircleBox;
    
    private float mChartAngle = 0f;
    
    private float[] mDrawAngles;
    private float[] mAbsoluteAngles;
    
    private boolean mDrawHole = true;
    
    private String mCenterTextLine1 = "Total Value";
    private String mCenterTextLine2 = "";
       
    private float mShift = 20f;
    
    /** if enabled, centertext is drawn */
    private boolean mDrawCenterText = true;
    
    /**
     * array of integers that reference the highlighted slices in the pie chart
     */
    private int[] mIndicesToHightlight = new int[0];
    
    /**
     * paint for the hole in the center of the pie chart
     */
    private Paint mHolePaint;
    
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
        // TODO Auto-generated method stub
        super.init();
        
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
        
        mListener = new PieChartTouchListener(this);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        long starttime = System.currentTimeMillis();
        
//        PointF center = getCenter();
        
//        mDrawCanvas.save();
//        
//        // apply the rotation if there is one
//        mDrawCanvas.rotate(mRotation, center.x, center.y);
        
        drawData();
        drawAdditional();

//        mDrawCanvas.restore();
        
        drawValues();
        
        drawDescription();
        
        drawCenterText();
        
        drawMarkerView();          

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
        mCenterTextLine2 = "" + (int) getYValueSum();

        // calculate how many digits are needed
        calcFormats();

        prepareMatrixAndContent();

        Log.i(LOG_TAG, "xVals: " + mXVals.size() + ", yVals: " + mYVals.size());
    }

    /** the decimalformat responsible for formatting the values in the chart */
    protected DecimalFormat mFormatValue = null;

    /**
     * the number of digits values that are drawn in the chart are formatted
     * with
     */
    protected int mValueFormatDigits = 1;
    
    /**
     * calculates the required number of digits for the y-legend and for the
     * values that might be drawn in the chart (if enabled)
     */
    protected void calcFormats() {
        
        mValueFormatDigits = Utils.getPieFormatDigits(mDeltaY);

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
     * sets the starting angle of the rotation, this is only used by the touch listener
     * @param x
     * @param y
     */
    public void setStartAngle(float x, float y) {

        mStartAngle = getAngleForPoint(x, y);

        // take the current angle into consideration
        mStartAngle -= mChartAngle;
    }
    
    /**
     * updates the view rotation depending on the given touch position, also takes the starting angle into consideration
     * @param x
     * @param y
     */
    public void updateRotation(float x, float y) {
        
        mChartAngle = getAngleForPoint(x, y);
        
        // take the offset into consideration
        mChartAngle -= mStartAngle; 
        
        mChartAngle = (mChartAngle +360f) % 360f;
    }
    
    @Override
    protected void prepareMatrixAndContent() {
        super.prepareMatrixAndContent();
        
        float diameter = getDiameter();
        
        int width = mContentRect.width() + mOffsetLeft + mOffsetRight;
        
        // create the circle box that will contain the pie-chart
        mCircleBox = new RectF(width / 2 - diameter / 2 + mShift, mOffsetTop  + mShift, width / 2 + diameter / 2 - mShift, diameter + mOffsetTop - mShift);
    }

    @Override
    protected void prepareDataPaints(ColorTemplate ct) {
       
        mDrawPaints = new Paint[ct.getColors().size()];

        // setup all paint objects
        for (int i = 0; i < ct.getColors().size(); i++) {
            mDrawPaints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            mDrawPaints[i].setStyle(Style.FILL);
            mDrawPaints[i].setColor(ct.getColors().get(i));
        }
    }
    
    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        calcAngles();
    }
    
    /**
     * calculates the needed angles for the chart slices
     */
    private void calcAngles() {
        
        mDrawAngles = new float[mYVals.size()];
        mAbsoluteAngles = new float[mYVals.size()];
        
        for(int i = 0; i < mYVals.size(); i++) {
            mDrawAngles[i] = calcAngle(mYVals.get(i));
            
            if(i > 0)
               mAbsoluteAngles[i] = mAbsoluteAngles[i-1] + mDrawAngles[i];
            else mAbsoluteAngles[i] = mDrawAngles[i];
        }
    }

    @Override
    protected void drawData() {
        
        Log.i(LOG_TAG, "chartangle: " + mChartAngle);
        
        float angle = mChartAngle;

        for(int i = 0; i < mYVals.size(); i++) {
            
            float newanlge = mDrawAngles[i];
            
            if(needsHighlight(i)) { // if true, highlight the slice
                float shiftangle = (float) Math.toRadians(angle + newanlge / 2f);
                
                float xShift = mShift * (float) Math.cos(shiftangle);
                float yShift = mShift * (float) Math.sin(shiftangle);
                
                RectF highlighted = new RectF(mCircleBox.left + xShift, mCircleBox.top + yShift, mCircleBox.right + xShift, mCircleBox.bottom + yShift);
                
                // redefine the rect that contains the arc so that the highlighted pie is not cut off
                mDrawCanvas.drawArc(highlighted, angle, newanlge, true, mDrawPaints[i % mDrawPaints.length]);
                
            } else {

                mDrawCanvas.drawArc(mCircleBox, angle, newanlge, true, mDrawPaints[i % mDrawPaints.length]);   
            }
            angle += newanlge;
        }
        
        if(mDrawHole) {
            
            mDrawCanvas.drawCircle(mContentRect.width() / 2, mContentRect.height() / 2, mContentRect.width() / 8, mHolePaint);
        }
    }
    
    /**
     * draws the description text in the center of the pie chart
     * makes most sense when center-hole is enabled
     */
    private void drawCenterText() {
        
        PointF c = getCenter();
        
        mDrawCanvas.drawText(mCenterTextLine1, c.x, c.y, mCenterTextPaint);
        mDrawCanvas.drawText(mCenterTextLine2, c.x, c.y + mCenterTextPaint.getTextSize() + 3, mCenterTextPaint);
    }
    
    
    @Override
    protected void drawValues() {
        
        PointF center = getCenter();
        
        float off = mCircleBox.width() / 8;
        
        // increase offset if there is no hole
        if(!mDrawHole) off += off / 2;
        
        // get the radius
        float r = mCircleBox.width() / 2 - off; // offset to keep things inside the chart
        
        for(int i = 0; i < mYVals.size(); i++) {
            
            // offset needed to center the drawn text in the slice
            float offset = mDrawAngles[i] / 2;
            
            // calculate the text position
            float x = (float) (r * Math.cos(Math.toRadians(mChartAngle + mAbsoluteAngles[i] - offset)) + center.x);
            float y = (float) (r * Math.sin(Math.toRadians(mChartAngle + mAbsoluteAngles[i] - offset)) + center.y);
            
            if(y > center.y) {
                y += 10;
                x += 3;
            }
            
            mDrawCanvas.drawText(mFormatValue.format(mYVals.get(i)), x, y, mValuePaint);
        }
    }
    
    /**
     * checks if the given index is set for highlighting or not
     * @param index
     * @return
     */
    private boolean needsHighlight(int index) {

        for (int i = 0; i < mIndicesToHightlight.length; i++)
            if (mIndicesToHightlight[i] == index)
                return true;

        return false;
    }

    /**
     * calculates the needed angle for a given value
     * @param value
     * @return
     */
    private float calcAngle(float value) {
        return value / mYValueSum * 360f;
    }
    
    @Override
    public void highlightValues(int[] indices) {
        
        mIndicesToHightlight = indices;
        invalidate();
    }
    
    /**
     * returns the chart value index for the given angle
     * (returns the correct "slice"-index)
     * @param angle
     * @param x
     * @param y
     * @return
     */
    public int getIndexForAngle(float angle, float x, float y) {
                
//        Log.i(LOG_TAG, "rawangle: " + angle);
        PointF c = getCenter();
        
        if(y < c.y) { // if we are above the center on the y-axis
            angle = 360f - angle;
        } 
        
        angle = Math.abs(angle - mChartAngle) % 360f;
//        Log.i(LOG_TAG, "angle: " + angle + ", chartangle: " + mChartAngle);
        
        for(int i = 0; i < mAbsoluteAngles.length; i++) {
            if(mAbsoluteAngles[i] > angle) return i;
        }
        
        return -1; // return -1 if no index found
    }
    
    public int getIndexForAngle(float angle) {
        
        float a = (angle - mChartAngle + 360) % 360f;
        Log.i("getindex", ""+a);
      
      for(int i = 0; i < mAbsoluteAngles.length; i++) {
          if(mAbsoluteAngles[i] >  a) return i;
      }
      
      return -1; // return -1 if no index found
  }
    
    /**
     * returns an integer array of all the different angles the chart slices have
     * the angles in the returned array determine how much space (of 360°) each slice takes
     * @return
     */
    public float[] getDrawAngles() {
        return mDrawAngles;
    }
    
    /**
     * returns the absolute angles of the different chart slices
     * (where the slices end)
     * @return
     */
    public float[] getAbsoluteAngles() {
        return mAbsoluteAngles;
    }
    
    /**
     * set a new starting angle for the pie chart (0-360)
     * default is 0° --> right side
     * @param angle
     */
    public void setStartAngle(float angle) {
        mChartAngle = angle;
    }
    
    /**
     * gets the current rotation angle of the pie chart
     * @return
     */
    public float getCurrentRotation() {
        return mChartAngle;
    }
    
    /**
     * sets the distance of the highlighted value to the piechart
     * @param shift
     */
    public void setShift(float shift) {
        mShift = Utils.convertDpToPixel(shift);
    }
    
    /**
     * set this to true to draw the pie center empty
     * @param enabled
     */
    public void setDrawHoleEnabled(boolean enabled) {
        this.mDrawHole = enabled;
    }
    
    /**
     * returns true if the hole in the center of the pie-chart is set to be visible, false if not
     * @return
     */
    public boolean isDrawHoleEnabled() {
        return mDrawHole;
    }
    
    /**
     * sets the text that is displayed in the center of the pie-chart
     * (2 lines available)
     * @param line1
     * @param line2
     */
    public void setCenterText(String line1, String line2) {
        mCenterTextLine1 = line1;
        mCenterTextLine2 = line2;
    }
    
    /**
     * set this to true to draw the text that is displayed in the center of the pie chart
     * @param enabled
     */
    public void setDrawCenterText(boolean enabled) {
        this.mDrawCenterText = enabled;
    }
    
    /**
     * returns true if drawing the center text is enabled
     * @return
     */
    public boolean isDrawCenterTextEnabled() {
        return mDrawCenterText;
    }
    
    /**
     * returns the radius of the pie-chart
     * @return
     */
    public float getRadius() {
        return mCircleBox.width() / 2f;
    }
    
    /**
     * returns the diameter of the pie-chart
     * @return
     */
    public float getDiameter() {
        return Math.min(mContentRect.width(), mContentRect.height());
    }
    
    /**
     * returns the angle relative to the chart center for the given point on the chart in degrees
     * @param x
     * @param y
     * @return
     */
    public float getAngleForPoint(float x, float y) {
        
        PointF c = getCenter();
        double tx = x - c.x, ty = y - c.y;
        double t_length = Math.sqrt(tx*tx + ty*ty);
        double r = Math.acos(ty / t_length);
        
        float angle = (float) Math.toDegrees(r);
        
        if(x > getCenter().x) angle = 360f - angle;

        angle = angle + 90f;
        
        if(angle > 360f) angle = angle - 360f;       
        
        Log.i("GETANGLE", ""+angle);
        return angle;
    }
    
    /**
     * returns the distance of a certain point on the chart to the center of the piechart
     * @param x
     * @param y
     * @return
     */
    public float distanceToCenter(float x, float y) {
        
        PointF c = getCenter();
        
        float dist = 0f;
        
        float xDist = 0f;
        float yDist = 0f;
        
        if(x > c.x) {
            xDist = x - c.x;
        } else {
            xDist = c.x - x;
        }
        
        if(y > c.y) {
            yDist = y - c.y;
        } else {
            yDist = c.y - y;
        }
        
        // pythagoras
        dist = (float) Math.sqrt(Math.pow(xDist, 2.0) + Math.pow(yDist, 2.0));
        
        return dist;
    }

    @Override
    protected void drawAdditional() { }
}
