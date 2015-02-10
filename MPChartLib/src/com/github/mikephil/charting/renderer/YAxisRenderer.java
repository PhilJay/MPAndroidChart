
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

import com.github.mikephil.charting.interfaces.ChartInterface;
import com.github.mikephil.charting.utils.LimitLine;
import com.github.mikephil.charting.utils.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.utils.PointD;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.github.mikephil.charting.utils.YAxis;
import com.github.mikephil.charting.utils.YAxis.AxisDependency;
import com.github.mikephil.charting.utils.YAxis.YLabelPosition;

import java.util.ArrayList;

public class YAxisRenderer extends AxisRenderer {

    /** paint for the y-label values */
    protected Paint mYAxisPaint;

    /** paint used for the limit lines */
    protected Paint mLimitLinePaint;

    protected YAxis mYAxis;

    public YAxisRenderer(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans) {
        super(viewPortHandler, trans);

        this.mYAxis = yAxis;

        mYAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mYAxisPaint.setColor(Color.BLACK);
        mYAxisPaint.setTextSize(Utils.convertDpToPixel(10f));

        mLimitLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLimitLinePaint.setStyle(Paint.Style.STROKE);
    }

    public void computeAxis(ChartInterface chart) {

        float yMin = 0f;
        float yMax = 0f;

        // calculate the starting and entry point of the y-labels (depending on
        // zoom / contentrect bounds)
        if (mViewPortHandler.contentWidth() > 10 && !mViewPortHandler.isFullyZoomedOutY()) {

            PointD p1 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentTop());
            PointD p2 = mTrans.getValuesByTouchPoint(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentBottom());

            if (!mYAxis.isInverted()) {
                yMin = (float) p2.y;
                yMax = (float) p1.y;
            } else {

                if (!chart.isStartAtZeroEnabled())
                    yMin = (float) Math.min(p1.y, p2.y);
                else
                    yMin = 0;
                yMax = (float) Math.max(p1.y, p2.y);
            }

        } else {

            if (!mYAxis.isInverted()) {
                yMin = chart.getYChartMin();
                yMax = chart.getYChartMax();
            } else {

                if (!chart.isStartAtZeroEnabled())
                    yMin = (float) Math.min(chart.getYChartMax(), chart.getYChartMin());
                else
                    yMin = 0;
                yMax = (float) Math.max(chart.getYChartMax(), chart.getYChartMin());
            }
        }

        computeAxisValues(yMin, yMax);
    }

    /**
     * Sets up the y-axis labels. Computes the desired number of labels between
     * the two given extremes. Unlike the papareXLabels() method, this method
     * needs to be called upon every refresh of the view.
     * 
     * @return
     */
    private void computeAxisValues(float min, float max) {

        float yMin = min;
        float yMax = max;

        int labelCount = mYAxis.getLabelCount();
        double range = Math.abs(yMax - yMin);

        if (labelCount == 0 || range <= 0) {
            mYAxis.mEntries = new float[] {};
            mYAxis.mEntryCount = 0;
            return;
        }

        double rawInterval = range / labelCount;
        double interval = Utils.roundToNextSignificant(rawInterval);
        double intervalMagnitude = Math.pow(10, (int) Math.log10(interval));
        int intervalSigDigit = (int) (interval / intervalMagnitude);
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or
            // 90
            interval = Math.floor(10 * intervalMagnitude);
        }

        // if the labels should only show min and max
        if (mYAxis.isShowOnlyMinMaxEnabled()) {

            mYAxis.mEntryCount = 2;
            mYAxis.mEntries = new float[2];
            mYAxis.mEntries[0] = yMin;
            mYAxis.mEntries[1] = yMax;

        } else {

            double first = Math.ceil(yMin / interval) * interval;
            double last = Utils.nextUp(Math.floor(yMax / interval) * interval);

            double f;
            int i;
            int n = 0;
            for (f = first; f <= last; f += interval) {
                ++n;
            }

            mYAxis.mEntryCount = n;

            if (mYAxis.mEntries.length < n) {
                // Ensure stops contains at least numStops elements.
                mYAxis.mEntries = new float[n];
            }

            for (f = first, i = 0; i < n; f += interval, ++i) {
                mYAxis.mEntries[i] = (float) f;
            }
        }

        if (interval < 1) {
            mYAxis.mDecimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            mYAxis.mDecimals = 0;
        }
    }

    /**
     * draws the y-axis labels to the screen
     */
    @Override
    public void renderAxis(Canvas c) {

        float[] positions = new float[mYAxis.mEntryCount * 2];

        for (int i = 0; i < positions.length; i += 2) {
            // only fill y values, x values are not needed since the y-labels
            // are
            // static on the x-axis
            positions[i + 1] = mYAxis.mEntries[i / 2];
        }

        mTrans.pointValuesToPixel(positions);

        mYAxisPaint.setTypeface(mYAxis.getTypeface());
        mYAxisPaint.setTextSize(mYAxis.getTextSize());
        mYAxisPaint.setColor(mYAxis.getTextColor());

        float xoffset = Utils.convertDpToPixel(5f);
        float yoffset = Utils.calcTextHeight(mYAxisPaint, "A") / 2.5f;

        AxisDependency dependency = mYAxis.getAxisDependency();
        YLabelPosition labelPosition = mYAxis.getLabelPosition();

        float xPos = 0f;

        if (dependency == AxisDependency.LEFT) {

            if (labelPosition == YLabelPosition.OUTSIDE_CHART) {
                mYAxisPaint.setTextAlign(Align.RIGHT);
                xPos = mViewPortHandler.offsetLeft() - xoffset;
            } else {
                mYAxisPaint.setTextAlign(Align.LEFT);
                xPos = mViewPortHandler.offsetLeft() + xoffset;
            }

        } else {

            if (labelPosition == YLabelPosition.OUTSIDE_CHART) {
                mYAxisPaint.setTextAlign(Align.LEFT);
                xPos = mViewPortHandler.contentRight() + xoffset;
            } else {
                mYAxisPaint.setTextAlign(Align.RIGHT);
                xPos = mViewPortHandler.contentRight() - xoffset;
            }
        }

        drawYLabels(c, xPos, positions, yoffset);
    }

    /**
     * draws the y-labels on the specified x-position
     * 
     * @param xPos
     * @param positions
     */
    private void drawYLabels(Canvas c, float xPos, float[] positions, float yOffset) {

        // draw
        for (int i = 0; i < mYAxis.mEntryCount; i++) {

            String text = mYAxis.getFormattedLabel(i);

            if (!mYAxis.isDrawTopYLabelEntryEnabled() && i >= mYAxis.mEntryCount - 1)
                return;

            c.drawText(text, xPos, positions[i * 2 + 1] + yOffset, mYAxisPaint);
        }
    }

    @Override
    public void renderGridLines(Canvas c) {

        if (!mYAxis.isDrawGridLinesEnabled())
            return;

        // pre alloc
        float[] position = new float[2];

        mGridPaint.setColor(mYAxis.getGridColor());

        // draw the horizontal grid
        for (int i = 0; i < mYAxis.mEntryCount; i++) {

            position[1] = mYAxis.mEntries[i];
            mTrans.pointValuesToPixel(position);

            c.drawLine(mViewPortHandler.offsetLeft(), position[1], mViewPortHandler.contentRight(),
                    position[1],
                    mGridPaint);
        }
    }

    /**
     * Draws the LimitLines associated with this axis to the screen.
     * 
     * @param c
     */
    public void renderLimitLines(Canvas c, ValueFormatter valueFormatter) {

        ArrayList<LimitLine> limitLines = mYAxis.getLimitLines();

        if (limitLines == null)
            return;

        float[] pts = new float[4];

        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            pts[1] = l.getLimit();
            pts[3] = l.getLimit();

            mTrans.pointValuesToPixel(pts);

            pts[0] = 0;
            pts[2] = mViewPortHandler.getChartWidth();

            mLimitLinePaint.setColor(l.getLineColor());
            mLimitLinePaint.setPathEffect(l.getDashPathEffect());
            mLimitLinePaint.setStrokeWidth(l.getLineWidth());

            c.drawLines(pts, mLimitLinePaint);

            // if drawing the limit-value is enabled
            if (l.isDrawValueEnabled()) {

                float xOffset = Utils.convertDpToPixel(4f);
                float yOffset = l.getLineWidth() + xOffset;
                String label = valueFormatter.getFormattedValue(l.getLimit());

                mLimitLinePaint.setPathEffect(null);
                mLimitLinePaint.setColor(l.getTextColor());

                if (l.getLabelPosition() == LimitLabelPosition.RIGHT) {

                    mLimitLinePaint.setTextAlign(Align.RIGHT);
                    c.drawText(label, mViewPortHandler.contentRight()
                            - xOffset,
                            pts[1] - yOffset, mLimitLinePaint);

                } else {
                    mLimitLinePaint.setTextAlign(Align.LEFT);
                    c.drawText(label, mViewPortHandler.offsetLeft()
                            + xOffset,
                            pts[1] - yOffset, mLimitLinePaint);
                }
            }
        }
    }
}
