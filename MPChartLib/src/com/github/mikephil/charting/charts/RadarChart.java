
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.YLabels;

import java.util.ArrayList;

/**
 * Implementation of the RadarChart, a "net"-like chart. It works best when
 * displaying 5-7 entries per DataSet.
 * 
 * @author Philipp Jahoda
 */
public class RadarChart extends PieRadarChartBase {

    /** paint for drawing the web */
    private Paint mWebPaint;

    private Paint mYLabelPaint;

    private float mOffsetAngle = 270f;

    /** width of the main web lines */
    private float mWebLineWidth = 2.5f;

    /** width of the inner web lines */
    private float mInnerWebLineWidth = 1.5f;

    /** color for the main web lines */
    private int mWebColor = Color.rgb(122, 122, 122);

    /** color for the inner web */
    private int mWebColorInner = Color.rgb(122, 122, 122);

    /** transparency the grid is drawn with (0-255) */
    private int mWebAlpha = 255;

    /** the object reprsenting the y-axis labels */
    private YLabels mYLabels = new YLabels();

    public RadarChart(Context context) {
        super(context);
    }

    public RadarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mWebLineWidth = Utils.convertDpToPixel(2f);
        mInnerWebLineWidth = Utils.convertDpToPixel(1f);

        mWebPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWebPaint.setStyle(Paint.Style.STROKE);

        mYLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mYLabelPaint.setColor(Color.BLACK);
        mYLabelPaint.setTextSize(Utils.convertDpToPixel(10f));
    }

    /**
     * Sets a RadarData object for the chart to display.
     * 
     * @param data
     */
    public void setData(RadarData data) {
        super.setData(data);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDataNotSet)
            return;

        long starttime = System.currentTimeMillis();

        drawWeb();

        drawData();

        drawAdditional();

        drawHighlights();

        drawValues();
        
        drawYLabels();

        drawLegend();

        drawDescription();

        canvas.drawBitmap(mDrawBitmap, 0, 0, mDrawPaint);

        Log.i(LOG_TAG, "RadarChart DrawTime: " + (System.currentTimeMillis() - starttime) + " ms");
    }

    /**
     * Draws the spider web.
     */
    private void drawWeb() {

        float sliceangle = getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = getFactor();

        PointF c = getCenter();

        // draw the web lines that come from the center
        mWebPaint.setStrokeWidth(mWebLineWidth);
        mWebPaint.setColor(mWebColor);
        mWebPaint.setAlpha(mWebAlpha);

        for (int i = 0; i < mCurrentData.getXValCount(); i++) {

            PointF p = getPosition(c, mYChartMax * factor, sliceangle * i + mOffsetAngle);

            mDrawCanvas.drawLine(c.x, c.y, p.x, p.y, mWebPaint);
        }

        // draw the inner-web
        mWebPaint.setStrokeWidth(mInnerWebLineWidth);
        mWebPaint.setColor(mWebColorInner);
        mWebPaint.setAlpha(mWebAlpha);

        int labelCount = mYLabels.getLabelCount();

        for (int j = 0; j < labelCount; j++) {

            for (int i = 0; i < mCurrentData.getXValCount(); i++) {

                float r = ((mYChartMax / labelCount) * (j + 1)) * factor;

                PointF p1 = getPosition(c, r, sliceangle * i + mOffsetAngle);
                PointF p2 = getPosition(c, r, sliceangle * (i + 1) + mOffsetAngle);

                mDrawCanvas.drawLine(p1.x, p1.y, p2.x, p2.y, mWebPaint);
            }
        }
    }

    @Override
    protected void drawData() {

        ArrayList<RadarDataSet> dataSets = (ArrayList<RadarDataSet>) mCurrentData.getDataSets();

        float sliceangle = getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = getFactor();

        PointF c = getCenter();

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            RadarDataSet dataSet = dataSets.get(i);
            ArrayList<? extends Entry> entries = dataSet.getYVals();

            Path surface = new Path();

            for (int j = 0; j < entries.size(); j++) {

                mRenderPaint.setColor(dataSet.getColor(j));

                Entry e = entries.get(j);

                PointF p = getPosition(c, e.getVal() * factor, sliceangle * j + mOffsetAngle);

                if (j == 0)
                    surface.moveTo(p.x, p.y);
                else
                    surface.lineTo(p.x, p.y);
            }

            surface.close();

            // draw filled
            if (dataSet.isDrawFilledEnabled()) {
                mRenderPaint.setStyle(Paint.Style.FILL);
                mRenderPaint.setAlpha(dataSet.getFillAlpha());
                mDrawCanvas.drawPath(surface, mRenderPaint);
                mRenderPaint.setAlpha(255);
            }

            mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
            mRenderPaint.setStyle(Paint.Style.STROKE);

            // draw the line (only if filled is disabled or alpha is below 255)
            if (!dataSet.isDrawFilledEnabled() || dataSet.getFillAlpha() < 255)
                mDrawCanvas.drawPath(surface, mRenderPaint);
        }
    }

    /**
     * Draws the y-labels of the RadarChart.
     */
    private void drawYLabels() {

        PointF c = getCenter();
        float factor = getFactor();

        int labelCount = mYLabels.getLabelCount();

        for (int j = 0; j < labelCount; j++) {

            for (int i = 0; i < mCurrentData.getXValCount(); i++) {

                float r = ((mYChartMax / labelCount) * (j + 1)) * factor;

                PointF p = getPosition(c, r, mOffsetAngle);
                
                mDrawCanvas.drawText("" + r, p.x, p.y, mYLabelPaint);
            }
        }
    }

    /**
     * Calculates the position on the RadarChart depending on the center of the
     * chart, the value and angle.
     * 
     * @param center
     * @param val
     * @param angle in degrees, converted to radians internally
     * @return
     */
    private PointF getPosition(PointF c, float val, float angle) {

        PointF p = new PointF((float) (c.x + val * Math.cos(Math.toRadians(angle))),
                (float) (c.y + val * Math.sin(Math.toRadians(angle))));
        return p;
    }

    /**
     * Returns the factor that is needed to transform values into pixels.
     * 
     * @return
     */
    public float getFactor() {
        return (float) Math.min(mContentRect.width() / 2, mContentRect.height() / 2)
                / mYChartMax;
    }

    /**
     * Returns the angle that each slice in the radar chart occupies.
     * 
     * @return
     */
    public float getSliceAngle() {
        return 360f / (float) mCurrentData.getXValCount();
    }

    @Override
    protected void drawValues() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void drawHighlights() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void drawAdditional() {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub

    }

    /**
     * Returns the object that represents all y-labels of the RadarChart.
     * 
     * @return
     */
    public YLabels getYLabels() {
        return mYLabels;
    }

    /**
     * Sets the width of the web lines that come from the center.
     * 
     * @param width
     */
    public void setWebLineWidth(float width) {
        mWebLineWidth = Utils.convertDpToPixel(width);
    }

    /**
     * Sets the width of the web lines that are in between the lines coming from
     * the center.
     * 
     * @param width
     */
    public void setWebLineWidthInner(float width) {
        mInnerWebLineWidth = Utils.convertDpToPixel(width);
    }

    /**
     * Sets the transparency (alpha) value for all web lines, default 255 = 100%
     * opaque, 0 = 100% transparent
     * 
     * @param alpha
     */
    public void setWebAlpha(int alpha) {
        mWebAlpha = alpha;
    }

    /**
     * Sets the color for the web lines that come from the center. Don't forget
     * to use getResources().getColor(...) when loading a color from the
     * resources. Default: Color.rgb(122, 122, 122)
     * 
     * @param color
     */
    public void setWebColor(int color) {
        mWebColor = color;
    }

    /**
     * Sets the color for the web lines in between the lines that come from the
     * center. Don't forget to use getResources().getColor(...) when loading a
     * color from the resources. Default: Color.rgb(122, 122, 122)
     * 
     * @param color
     */
    public void setWebColorInner(int color) {
        mWebColorInner = color;
    }

    /**
     * Set an offset for the rotation of the RadarChart in degrees. Default 270f
     * 
     * @param angle
     */
    public void setRotation(float angle) {
        
        angle = Math.abs(angle % 360f);
        mOffsetAngle = angle;
    }

    @Override
    public void setPaint(Paint p, int which) {
        super.setPaint(p, which);

        switch (which) {
            case PAINT_RADAR_WEB:
                mWebPaint = p;
                break;
        }
    }

    @Override
    public Paint getPaint(int which) {
        Paint p = super.getPaint(which);
        if (p != null)
            return p;

        switch (which) {
            case PAINT_RADAR_WEB:
                return mWebPaint;
        }

        return null;
    }
}
