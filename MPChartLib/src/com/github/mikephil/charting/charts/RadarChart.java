
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.utils.Legend.LegendPosition;
import com.github.mikephil.charting.utils.LimitLine;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;

import java.util.ArrayList;

/**
 * Implementation of the RadarChart, a "spidernet"-like chart. It works best
 * when displaying 5-10 entries per DataSet.
 * 
 * @author Philipp Jahoda
 */
public class RadarChart extends PieRadarChartBase<RadarData> {

    /** paint for drawing the web */
    private Paint mWebPaint;

    /** width of the main web lines */
    private float mWebLineWidth = 2.5f;

    /** width of the inner web lines */
    private float mInnerWebLineWidth = 1.5f;

    /** color for the main web lines */
    private int mWebColor = Color.rgb(122, 122, 122);

    /** color for the inner web */
    private int mWebColorInner = Color.rgb(122, 122, 122);

    /** transparency the grid is drawn with (0-255) */
    private int mWebAlpha = 150;

    /** flag indicating if the y-labels should be drawn or not */
    protected boolean mDrawYLabels = true;

    /** flag indicating if the x-labels should be drawn or not */
    protected boolean mDrawXLabels = true;

    /** the object reprsenting the y-axis labels */
    private YLabels mYLabels = new YLabels();

    /** the object representing the x-axis labels */
    private XLabels mXLabels = new XLabels();

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

        mWebLineWidth = Utils.convertDpToPixel(1.5f);
        mInnerWebLineWidth = Utils.convertDpToPixel(0.75f);

        mWebPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWebPaint.setStyle(Paint.Style.STROKE);

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));
    }

    @Override
    protected void calcMinMax(boolean fixedValues) {
        super.calcMinMax(fixedValues);

        mYChartMin = 0;
    }

    @Override
    public void prepare() {
        super.prepare();

        prepareXLabels();
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

            mLegend.setOffsetBottom(mLegendLabelPaint.getTextSize() * 5.5f);
        }

        mLegend.setOffsetTop(mOffsetTop);
        mLegend.setOffsetLeft(mOffsetLeft);

        if (mDrawLegend) {

            mOffsetBottom = Math.max(mXLabels.mLabelWidth, mOffsetBottom);
            mOffsetTop = Math.max(mXLabels.mLabelWidth, mOffsetTop);
            mOffsetRight = Math.max(mXLabels.mLabelWidth, mOffsetRight);
            mOffsetLeft = Math.max(mXLabels.mLabelWidth, mOffsetLeft);

            mOffsetBottom = Math.max(mOffsetBottom, mLegend.getOffsetBottom());
            mOffsetRight = Math.max(mOffsetRight, mLegend.getOffsetRight() / 3 * 2);
        }

        applyCalculatedOffsets();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDataNotSet)
            return;

        long starttime = System.currentTimeMillis();

        prepareYLabels();

        drawXLabels();

        drawWeb();
       
        drawLimitLines();
        
        drawData();

        drawAdditional();

        drawHighlights();

        drawYLabels();

        drawValues();

        drawLegend();

        drawDescription();

        drawMarkers();

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

        PointF c = getCenterOffsets();

        // draw the web lines that come from the center
        mWebPaint.setStrokeWidth(mWebLineWidth);
        mWebPaint.setColor(mWebColor);
        mWebPaint.setAlpha(mWebAlpha);

        for (int i = 0; i < mCurrentData.getXValCount(); i++) {

            PointF p = getPosition(c, mYChartMax * factor, sliceangle * i + mRotationAngle);

            mDrawCanvas.drawLine(c.x, c.y, p.x, p.y, mWebPaint);
        }

        // draw the inner-web
        mWebPaint.setStrokeWidth(mInnerWebLineWidth);
        mWebPaint.setColor(mWebColorInner);
        mWebPaint.setAlpha(mWebAlpha);

        int labelCount = mYLabels.mEntryCount;

        for (int j = 0; j < labelCount; j++) {

            for (int i = 0; i < mCurrentData.getXValCount(); i++) {

                float r = ((mYChartMax / labelCount) * (j + 1)) * factor;

                PointF p1 = getPosition(c, r, sliceangle * i + mRotationAngle);
                PointF p2 = getPosition(c, r, sliceangle * (i + 1) + mRotationAngle);

                mDrawCanvas.drawLine(p1.x, p1.y, p2.x, p2.y, mWebPaint);
            }
        }
    }

    @Override
    protected void drawData() {

        ArrayList<RadarDataSet> dataSets = mCurrentData.getDataSets();

        float sliceangle = getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = getFactor();

        PointF c = getCenterOffsets();

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            RadarDataSet dataSet = dataSets.get(i);
            ArrayList<Entry> entries = dataSet.getYVals();

            Path surface = new Path();

            for (int j = 0; j < entries.size(); j++) {

                mRenderPaint.setColor(dataSet.getColor(j));

                Entry e = entries.get(j);

                PointF p = getPosition(c, e.getVal() * factor, sliceangle * j + mRotationAngle);

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
     * Draws the limit lines if there are one.
     */
    private void drawLimitLines() {

        ArrayList<LimitLine> limitLines = mOriginalData.getLimitLines();

        if (limitLines == null)
            return;

        float sliceangle = getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = getFactor();

        PointF c = getCenterOffsets();

        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            mLimitLinePaint.setColor(l.getLineColor());
            mLimitLinePaint.setPathEffect(l.getDashPathEffect());
            mLimitLinePaint.setStrokeWidth(l.getLineWidth());
            
            float r = l.getLimit() * factor;
            
            Path limitPath = new Path();

            for (int j = 0; j < mCurrentData.getXValCount(); j++) {

                PointF p = getPosition(c, r, sliceangle * j + mRotationAngle);
                
                if (j == 0)
                    limitPath.moveTo(p.x, p.y);
                else
                    limitPath.lineTo(p.x, p.y);
            }
            
            limitPath.close();
            
            mDrawCanvas.drawPath(limitPath, mLimitLinePaint);
        }       
    }

    /**
     * Calculates the required maximum y-value in order to be able to provide
     * the desired number of label entries and rounded label values.
     */
    private void prepareYLabels() {

        int labelCount = mYLabels.getLabelCount();
        double range = mCurrentData.getYMax() - mYChartMin;

        double rawInterval = range / labelCount;
        double interval = Utils.roundToNextSignificant(rawInterval);
        double intervalMagnitude = Math.pow(10, (int) Math.log10(interval));
        int intervalSigDigit = (int) (interval / intervalMagnitude);
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or
            // 90
            interval = Math.floor(10 * intervalMagnitude);
        }

        double first = Math.ceil(mYChartMin / interval) * interval;
        double last = Utils.nextUp(Math.floor(mCurrentData.getYMax() / interval) * interval);

        double f;
        int n = 0;
        for (f = first; f <= last; f += interval) {
            ++n;
        }

        mYLabels.mEntryCount = n;

        mYChartMax = (float) interval * n;

        // calc delta
        mDeltaY = Math.abs(mYChartMax - mYChartMin);
    }

    /**
     * Draws the y-labels of the RadarChart.
     */
    private void drawYLabels() {

        if (!mDrawYLabels)
            return;

        mYLabelPaint.setTypeface(mYLabels.getTypeface());
        mYLabelPaint.setTextSize(mYLabels.getTextSize());
        mYLabelPaint.setColor(mYLabels.getTextColor());

        PointF c = getCenterOffsets();
        float factor = getFactor();

        int labelCount = mYLabels.mEntryCount;

        for (int j = 0; j < labelCount; j++) {

            float r = ((mYChartMax / labelCount) * j) * factor;

            PointF p = getPosition(c, r, mRotationAngle);

            float val = r / factor;
            
            String label = Utils.formatNumber(val, mYLabels.mDecimals,
                    mYLabels.isSeparateThousandsEnabled());
            
            mDrawCanvas.drawText(label, p.x + 10, p.y - 5, mYLabelPaint);
        }
    }

    /**
     * setup the x-axis labels
     */
    private void prepareXLabels() {

        StringBuffer a = new StringBuffer();

        int max = (int) Math.round(mCurrentData.getXValAverageLength());

        for (int i = 0; i < max; i++) {
            a.append("h");
        }

        mXLabels.mLabelWidth = Utils.calcTextWidth(mXLabelPaint, a.toString());
        mXLabels.mLabelHeight = Utils.calcTextWidth(mXLabelPaint, "Q");
    }

    /**
     * Draws the x-labels of the chart.
     */
    private void drawXLabels() {

        if (!mDrawXLabels)
            return;

        mXLabelPaint.setTypeface(mXLabels.getTypeface());
        mXLabelPaint.setTextSize(mXLabels.getTextSize());
        mXLabelPaint.setColor(mXLabels.getTextColor());

        float sliceangle = getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = getFactor();

        PointF c = getCenterOffsets();

        for (int i = 0; i < mCurrentData.getXValCount(); i++) {

            String text = mCurrentData.getXVals().get(i);

            float angle = (sliceangle * i + mRotationAngle) % 360f;

            PointF p = getPosition(c, mYChartMax * factor + mXLabels.mLabelWidth / 2f, angle);

            mDrawCanvas.drawText(text, p.x, p.y + mXLabels.mLabelHeight / 2f, mXLabelPaint);
        }
    }

    @Override
    protected void drawValues() {

        // if values are drawn
        if (mDrawYValues) {

            float sliceangle = getSliceAngle();

            // calculate the factor that is needed for transforming the value to
            // pixels
            float factor = getFactor();

            PointF c = getCenterOffsets();

            float yoffset = Utils.convertDpToPixel(5f);

            for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

                RadarDataSet dataSet = mCurrentData.getDataSetByIndex(i);
                ArrayList<Entry> entries = dataSet.getYVals();

                for (int j = 0; j < entries.size(); j++) {

                    Entry e = entries.get(j);

                    PointF p = getPosition(c, e.getVal() * factor, sliceangle * j + mRotationAngle);

                    mDrawCanvas.drawText(mValueFormat.format(e.getVal()),
                            p.x, p.y - yoffset, mValuePaint);
                }
            }
        }
    }

    @Override
    protected void drawHighlights() {

        // if there are values to highlight and highlighnting is enabled, do it
        if (mHighlightEnabled && valuesToHighlight()) {
            
            float sliceangle = getSliceAngle();
            float factor = getFactor();

            PointF c = getCenterOffsets();

            for (int i = 0; i < mIndicesToHightlight.length; i++) {

                RadarDataSet set = mCurrentData
                        .getDataSetByIndex(mIndicesToHightlight[i]
                                .getDataSetIndex());

                mHighlightPaint.setColor(set.getHighLightColor());

                // get the index to highlight
                int xIndex = mIndicesToHightlight[i].getXIndex();

                Entry e = set.getEntryForXIndex(xIndex);
                int j = set.getEntryPosition(e);
                float y = e.getVal();

                PointF p = getPosition(c, y * factor, sliceangle * j + mRotationAngle);

                float[] pts = new float[] {
                        p.x, 0, p.x, getHeight(), 0, p.y, getWidth(), p.y
                };

                mDrawCanvas.drawLines(pts, mHighlightPaint);
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
    public int getIndexForAngle(float angle) {

        // take the current angle of the chart into consideration
        float a = (angle - mRotationAngle + 360) % 360f;

        float sliceangle = getSliceAngle();

        for (int i = 0; i < mCurrentData.getXValCount(); i++) {
            if (sliceangle * (i + 1) - sliceangle / 2f > a)
                return i;
        }

        return 0;
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
     * Returns the object that represents all x-labels of the RadarChart.
     * 
     * @return
     */
    public XLabels getXLabels() {
        return mXLabels;
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
     * Sets the transparency (alpha) value for all web lines, default: 150, 255
     * = 100% opaque, 0 = 100% transparent
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
     * set this to true to enable drawing the y-labels, false if not
     * 
     * @param enabled
     */
    public void setDrawYLabels(boolean enabled) {
        mDrawYLabels = enabled;
    }

    /**
     * set this to true to enable drawing the x-labels, false if not
     * 
     * @param enabled
     */
    public void setDrawXLabels(boolean enabled) {
        mDrawXLabels = enabled;
    }

    /**
     * Returns true if drawing y-labels is enabled, false if not.
     * 
     * @return
     */
    public boolean isDrawYLabelsEnabled() {
        return mDrawYLabels;
    }

    /**
     * Returns true if drawing x-labels is enabled, false if not.
     * 
     * @return
     */
    public boolean isDrawXLabelsEnabled() {
        return mDrawXLabels;
    }

    @Override
    public float getRadius() {
        if (mContentRect == null)
            return 0;
        else
            return Math.min(mContentRect.width() / 2f, mContentRect.height() / 2f);
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
