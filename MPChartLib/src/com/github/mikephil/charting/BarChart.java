
package com.github.mikephil.charting;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.ArrayList;

public class BarChart extends BarLineChartBase {

    /** space indicator between the bars 0.1f == 10 % */
    private float mBarSpace = 0.1f;

    /** indicates the angle of the 3d effect */
    private float mSkew = 0.3f;

    /** indicates how much the 3d effect goes back */
    private float mDepth = 0.3f;

    /** flag the enables or disables 3d bars */
    private boolean m3DEnabled = true;

    public BarChart(Context context) {
        super(context);
    }

    public BarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /** array that holds all the colors for the top 3D effect */
    private int[] mTopColors;

    /** array that holds all the colors for the side 3D effect */
    private int[] mSideColors;

    @Override
    protected void prepareDataPaints(ColorTemplate ct) {

        // prepare the paints
        mDrawPaints = new Paint[ct.getColors().size()];

        for (int i = 0; i < ct.getColors().size(); i++) {
            mDrawPaints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            mDrawPaints[i].setStyle(Style.FILL);
            mDrawPaints[i].setColor(ct.getColors().get(i));
        }

        // generate the colors for the 3D effect
        mTopColors = new int[mDrawPaints.length];
        mSideColors = new int[mDrawPaints.length];

        float[] hsv = new float[3];

        for (int i = 0; i < mSideColors.length; i++) {

            // extract the color
            int c = mDrawPaints[i].getColor();
            Color.colorToHSV(c, hsv); // convert to hsv

            // make brighter
            hsv[1] = hsv[1] - 0.1f; // less saturation
            hsv[2] = hsv[2] + 0.1f; // more brightness

            // convert back
            c = Color.HSVToColor(hsv);

            // assign
            mTopColors[i] = c;

            // get color again
            c = mDrawPaints[i].getColor();

            // convert
            Color.colorToHSV(c, hsv);

            // make darker
            hsv[1] = hsv[1] + 0.1f; // more saturation
            hsv[2] = hsv[2] - 0.1f; // less brightness

            // reassing
            c = Color.HSVToColor(hsv);

            mSideColors[i] = c;
        }
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        // increase deltax by 1 because the bars have a width of 1
        mDeltaX++;
    }
    
    @Override
    protected void drawHighlights() {
        
        // if there are values to highlight and highlighnting is enabled, do it
        if(mHighlightEnabled && valuesToHighlight()) {
            
            for(int i = 0; i < mIndicesToHightlight.length; i++) {
                
                
            }
        }
    }

    private RectF mBarRect = new RectF();

    @Override
    protected void drawData() {

        ArrayList<Path> topPaths = new ArrayList<Path>();
        ArrayList<Path> sidePaths = new ArrayList<Path>();

        if (m3DEnabled) {

            float[] pts = new float[] {
                    0f, 0f, 1f, 0f
            };

            // calculate the depth depending on scale

            transformPointArray(pts);

            pts[3] = pts[2] - pts[0];
            pts[2] = 0f;
            pts[1] = 0f;
            pts[0] = 0f;

            Matrix invert = new Matrix();

            mMatrixOffset.invert(invert);
            invert.mapPoints(pts);

            mMatrixTouch.invert(invert);
            invert.mapPoints(pts);

            mMatrixValueToPx.invert(invert);
            invert.mapPoints(pts);

            float depth = Math.abs(pts[3] - pts[1]) * mDepth;

            for (int i = 0; i < mYVals.size(); i++) {

                float y = mYVals.get(i);
                float left = i + mBarSpace / 2f;
                float right = i + 1f - mBarSpace / 2f;
                float top = y >= 0 ? y : 0;

                // create the 3D effect paths for the top and side
                Path topPath = new Path();
                topPath.moveTo(left, top);
                topPath.lineTo(left + mSkew, top + depth);
                topPath.lineTo(right + mSkew, top + depth);
                topPath.lineTo(right, top);

                topPaths.add(topPath);

                Path sidePath = new Path();
                sidePath.moveTo(right, top);
                sidePath.lineTo(right + mSkew, top + depth);
                sidePath.lineTo(right + mSkew, depth);
                sidePath.lineTo(right, 0);

                sidePaths.add(sidePath);
            }

            transformPaths(topPaths);
            transformPaths(sidePaths);
        }

        // do the drawing
        for (int i = 0; i < mYVals.size(); i++) {

            Paint paint = mDrawPaints[i % mDrawPaints.length];

            float y = mYVals.get(i);
            float left = i + mBarSpace / 2f;
            float right = i + 1f - mBarSpace / 2f;
            float top = y >= 0 ? y : 0;
            float bottom = y <= 0 ? y : 0;

            mBarRect.set(left, top, right, bottom);

            transformRect(mBarRect);

            mDrawCanvas.drawRect(mBarRect, paint);

            if (m3DEnabled) {

                int c = paint.getColor();

                paint.setColor(mTopColors[i % mTopColors.length]);
                mDrawCanvas.drawPath(topPaths.get(i), paint);

                paint.setColor(mSideColors[i % mSideColors.length]);
                mDrawCanvas.drawPath(sidePaths.get(i), paint);

                paint.setColor(c);
            }
        }
    }

    @Override
    protected void drawValues() {

        // if values are drawn
        if (mDrawYValues && mYVals.size() < mMaxVisibleCount * mScaleX) {

            float[] valuePoints = new float[mYVals.size() * 2];

            for (int i = 0; i < valuePoints.length; i += 2) {
                valuePoints[i] = i / 2 + 0.5f; // add 0.5f too keep the values
                                               // centered on top of the bars
                valuePoints[i + 1] = mYVals.get(i / 2);
            }

            transformPointArray(valuePoints);

            for (int i = 0; i < valuePoints.length; i += 2) {
                mDrawCanvas.drawText(
                        mFormatValue.format(mYVals.get(i / 2)),
                        valuePoints[i], valuePoints[i + 1] - 12, mValuePaint);
            }
        }
    }

    /**
     * sets the skew (default 0.3f), the skew indicates how much the 3D effect
     * of the chart is turned to the right
     * 
     * @param skew
     */
    public void setSkew(float skew) {
        this.mSkew = skew;
    }

    /**
     * returns the skew value that indicates how much the 3D effect is turned to
     * the right
     * 
     * @return
     */
    public float getSkew() {
        return mSkew;
    }

    /**
     * set the depth of the chart (default 0.3f), the depth indicates how much
     * the 3D effect of the chart goes back
     * 
     * @param depth
     */
    public void setDepth(float depth) {
        this.mDepth = depth;
    }

    /**
     * returhs the depth, which indicates how much the 3D effect goes back
     * 
     * @return
     */
    public float getDepth() {
        return mDepth;
    }

    /**
     * returns the space between bars in percent of the whole width of one value
     * 
     * @return
     */
    public float getBarSpace() {
        return mBarSpace * 100f;
    }

    /**
     * sets the space between the bars in percent of the total bar width
     * 
     * @param percent
     */
    public void setBarSpace(float percent) {
        mBarSpace = percent / 100f;
    }

    /**
     * if enabled, chart will be drawn in 3d
     * 
     * @param enabled
     */
    public void set3DEnabled(boolean enabled) {
        this.m3DEnabled = enabled;
    }

    /**
     * returns true if 3d bars is enabled, false if not
     * 
     * @return
     */
    public boolean is3DEnabled() {
        return m3DEnabled;
    }

    /**
     * returns the top colors that define the color of the top 3D effect path
     * 
     * @return
     */
    public int[] getTopColors() {
        return mTopColors;
    }

    /**
     * returns the side colors that define the color of the side 3D effect path
     * 
     * @return
     */
    public int[] getSideColors() {
        return mSideColors;
    }

    @Override
    protected void drawAdditional() {
    }
}
