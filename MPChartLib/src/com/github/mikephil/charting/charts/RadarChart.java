
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.Legend.LegendPosition;

import java.util.ArrayList;

/**
 * Implementation of the RadarChart, a "net"-like chart. It works best when
 * displaying 5-7 entries per DataSet.
 * 
 * @author Philipp Jahoda
 */
public class RadarChart extends Chart {

    private Paint mWebPaint;

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

        mWebPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWebPaint.setStrokeWidth(Utils.convertDpToPixel(1.5f));
        mWebPaint.setColor(Color.rgb(122, 122, 122));
        mWebPaint.setStyle(Paint.Style.STROKE);
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

        drawLegend();

        drawDescription();

        canvas.drawBitmap(mDrawBitmap, 0, 0, mDrawPaint);

        Log.i(LOG_TAG, "RadarChart DrawTime: " + (System.currentTimeMillis() - starttime) + " ms");
    }

    @Override
    public void prepare() {

        if (mDataNotSet)
            return;

        calcMinMax(false);

        // // calculate how many digits are needed
        // calcFormats();

        prepareLegend();
    }

    @Override
    protected void calculateOffsets() {

        if (mLegend == null)
            return;

        // setup offsets for legend
        if (mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART) {

            mLegend.setOffsetRight(mLegend.getMaximumEntryLength(mLegendLabelPaint));
            mLegendLabelPaint.setTextAlign(Align.LEFT);

        } else if (mLegend.getPosition() == LegendPosition.BELOW_CHART_LEFT
                || mLegend.getPosition() == LegendPosition.BELOW_CHART_RIGHT
                || mLegend.getPosition() == LegendPosition.BELOW_CHART_CENTER) {

            mLegend.setOffsetBottom(mLegendLabelPaint.getTextSize() * 3.5f);
        }

        if (mDrawLegend) {

            mOffsetBottom = Math.max(mOffsetBottom, mLegend.getOffsetBottom());
            mOffsetRight = Math.max(mOffsetRight, mLegend.getOffsetRight() / 3 * 2);
        }

        mLegend.setOffsetTop(mOffsetTop);
        mLegend.setOffsetLeft(mOffsetLeft);

        prepareContentRect();

        float scaleX = (float) ((getWidth() - mOffsetLeft - mOffsetRight) / mDeltaX);
        float scaleY = (float) ((getHeight() - mOffsetBottom - mOffsetTop) / mDeltaY);

        Matrix val = new Matrix();
        val.postTranslate(0, -mYChartMin);
        val.postScale(scaleX, -scaleY);

        mMatrixValueToPx.set(val);

        Matrix offset = new Matrix();
        offset.postTranslate(mOffsetLeft, getHeight() - mOffsetBottom);

        mMatrixOffset.set(offset);
    }

    /**
     * Draws the spider web.
     */
    private void drawWeb() {

        float sliceangle = 360 / (float) mCurrentData.getXValCount();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = getFactor();

        PointF c = getCenter();

        for (int i = 0; i < mCurrentData.getXValCount(); i++) {

            PointF p = getPosition(c, mCurrentData.getYMax() * factor, sliceangle * i);

            mDrawCanvas.drawLine(c.x, c.y, p.x, p.y, mWebPaint);
        }
    }

    @Override
    protected void drawData() {

        ArrayList<RadarDataSet> dataSets = (ArrayList<RadarDataSet>) mCurrentData.getDataSets();

        float sliceangle = 360f / (float) mCurrentData.getXValCount();

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

                PointF p = getPosition(c, e.getVal() * factor, sliceangle * j);

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
