
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

public class PieChartRenderer extends DataRenderer {

    protected PieChart mChart;

    /**
     * paint for the hole in the center of the pie chart and the transparent
     * circle
     */
    private Paint mHolePaint;

    /**
     * paint object for the text that can be displayed in the center of the
     * chart
     */
    private Paint mCenterTextPaint;

    public PieChartRenderer(PieChart chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mHolePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHolePaint.setColor(Color.WHITE);

        mCenterTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterTextPaint.setColor(Color.BLACK);
        mCenterTextPaint.setTextSize(Utils.convertDpToPixel(12f));
        mCenterTextPaint.setTextAlign(Align.CENTER);

        mValuePaint.setTextSize(Utils.convertDpToPixel(13f));
        mValuePaint.setColor(Color.WHITE);
        mValuePaint.setTextAlign(Align.CENTER);
    }

    public Paint getPaintHole() {
        return mHolePaint;
    }

    public Paint getPaintCenterText() {
        return mCenterTextPaint;
    }
    
    @Override
    public void initBuffers() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void drawData(Canvas c) {

        PieData pieData = mChart.getData();

        for (PieDataSet set : pieData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(Canvas c, PieDataSet dataSet) {

        float angle = mChart.getRotationAngle();

        int cnt = 0;

        ArrayList<Entry> entries = dataSet.getYVals();
        float[] drawAngles = mChart.getDrawAngles();

        for (int j = 0; j < entries.size(); j++) {

            float newangle = drawAngles[cnt];
            float sliceSpace = dataSet.getSliceSpace();

            Entry e = entries.get(j);

            // draw only if the value is greater than zero
            if ((Math.abs(e.getVal()) > 0.000001)) {

                if (!mChart.needsHighlight(e.getXIndex(),
                        mChart.getData().getIndexOfDataSet(dataSet))) {

                    mRenderPaint.setColor(dataSet.getColor(j));
                    c.drawArc(mChart.getCircleBox(), angle + sliceSpace / 2f,
                            newangle * mAnimator.getPhaseY()
                                    - sliceSpace / 2f, true, mRenderPaint);
                }

                // if(sliceSpace > 0f) {
                //
                // PointF outer = getPosition(c, radius, angle);
                // PointF inner = getPosition(c, radius * mHoleRadiusPercent
                // / 100f, angle);
                // }
            }

            angle += newangle * mAnimator.getPhaseX();
            cnt++;
        }
    }

    @Override
    public void drawValues(Canvas c) {

        PointF center = mChart.getCenterCircleBox();

        // get whole the radius
        float r = mChart.getRadius();
        float rotationAngle = mChart.getRotationAngle();
        float[] drawAngles = mChart.getDrawAngles();
        float[] absoluteAngles = mChart.getAbsoluteAngles();

        float off = r / 2f;

        if (mChart.isDrawHoleEnabled()) {
            off = (r - (r / 100f * mChart.getHoleRadius())) / 2f;
        }

        r -= off; // offset to keep things inside the chart

        PieData data = mChart.getData();
        ArrayList<PieDataSet> dataSets = data.getDataSets();

        int cnt = 0;

        for (int i = 0; i < dataSets.size(); i++) {

            PieDataSet dataSet = dataSets.get(i);

            if (!dataSet.isDrawValuesEnabled())
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            ArrayList<Entry> entries = dataSet.getYVals();

            for (int j = 0; j < entries.size() * mAnimator.getPhaseX(); j++) {

                // offset needed to center the drawn text in the slice
                float offset = drawAngles[cnt] / 2;

                // calculate the text position
                float x = (float) (r
                        * Math.cos(Math.toRadians((rotationAngle + absoluteAngles[cnt] - offset)
                                * mAnimator.getPhaseY())) + center.x);
                float y = (float) (r
                        * Math.sin(Math.toRadians((rotationAngle + absoluteAngles[cnt] - offset)
                                * mAnimator.getPhaseY())) + center.y);

                float value = mChart.isUsePercentValuesEnabled() ? entries.get(j).getVal()
                        / mChart.getYValueSum() * 100f : entries.get(j).getVal();

                String val = dataSet.getValueFormatter().getFormattedValue(value);

                boolean drawXVals = mChart.isDrawSliceTextEnabled();
                boolean drawYVals = dataSet.isDrawValuesEnabled();

                // draw everything, depending on settings
                if (drawXVals && drawYVals) {

                    // use ascent and descent to calculate the new line
                    // position,
                    // 1.6f is the line spacing
                    float lineHeight = (mValuePaint.ascent() + mValuePaint.descent()) * 1.6f;
                    y -= lineHeight / 2;

                    c.drawText(val, x, y, mValuePaint);
                    if (j < data.getXValCount())
                        c.drawText(data.getXVals().get(j), x, y + lineHeight,
                                mValuePaint);

                } else if (drawXVals && !drawYVals) {
                    if (j < data.getXValCount())
                        c.drawText(data.getXVals().get(j), x, y, mValuePaint);
                } else if (!drawXVals && drawYVals) {

                    c.drawText(val, x, y, mValuePaint);
                }

                cnt++;
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
        drawHole(c);
        drawCenterText(c);
    }

    /**
     * draws the hole in the center of the chart and the transparent circle /
     * hole
     */
    protected void drawHole(Canvas c) {

        if (mChart.isDrawHoleEnabled()) {

            float transparentCircleRadius = mChart.getTransparentCircleRadius();
            float holeRadius = mChart.getHoleRadius();
            float radius = mChart.getRadius();

            PointF center = mChart.getCenterCircleBox();

            int color = mHolePaint.getColor();

            // draw the hole-circle
            c.drawCircle(center.x, center.y,
                    radius / 100 * holeRadius, mHolePaint);

            if (transparentCircleRadius > holeRadius) {

                // make transparent
                mHolePaint.setColor(color & 0x60FFFFFF);

                // draw the transparent-circle
                c.drawCircle(center.x, center.y,
                        radius / 100 * transparentCircleRadius, mHolePaint);

                mHolePaint.setColor(color);
            }
        }
    }

    /**
     * draws the description text in the center of the pie chart makes most
     * sense when center-hole is enabled
     */
    protected void drawCenterText(Canvas c) {

        String centerText = mChart.getCenterText();

        if (mChart.isDrawCenterTextEnabled() && centerText != null) {

            PointF center = mChart.getCenterCircleBox();

            // get all lines from the text
            String[] lines = centerText.split("\n");

            // calculate the height for each line
            float lineHeight = Utils.calcTextHeight(mCenterTextPaint, lines[0]);
            float linespacing = lineHeight * 0.2f;

            float totalheight = lineHeight * lines.length - linespacing * (lines.length - 1);

            int cnt = lines.length;

            float y = center.y;

            for (int i = 0; i < lines.length; i++) {

                String line = lines[lines.length - i - 1];

                c.drawText(line, center.x, y
                        + lineHeight * cnt - totalheight / 2f,
                        mCenterTextPaint);
                cnt--;
                y -= linespacing;
            }
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        float rotationAngle = mChart.getRotationAngle();
        float angle = 0f;

        float[] drawAngles = mChart.getDrawAngles();
        float[] absoluteAngles = mChart.getAbsoluteAngles();

        for (int i = 0; i < indices.length; i++) {

            // get the index to highlight
            int xIndex = indices[i].getXIndex();
            if (xIndex >= drawAngles.length)
                continue;

            if (xIndex == 0)
                angle = rotationAngle;
            else
                angle = rotationAngle + absoluteAngles[xIndex - 1];

            angle *= mAnimator.getPhaseY();

            float sliceDegrees = drawAngles[xIndex];

            PieDataSet set = mChart.getData()
                    .getDataSetByIndex(indices[i]
                            .getDataSetIndex());

            if (set == null)
                continue;

            float shift = set.getSelectionShift();
            RectF circleBox = mChart.getCircleBox();

            /**
             * Make the box containing current arc larger equally in every
             * dimension, to preserve shape of arc. Code provided by:
             * 
             * @link https://github.com/wogg
             */
            RectF highlighted = new RectF(circleBox.left - shift,
                    circleBox.top - shift,
                    circleBox.right + shift,
                    circleBox.bottom + shift);

            mRenderPaint.setColor(set.getColor(xIndex));

            // redefine the rect that contains the arc so that the
            // highlighted pie is not cut off
            c.drawArc(highlighted, angle + set.getSliceSpace() / 2f, sliceDegrees
                    - set.getSliceSpace() / 2f, true, mRenderPaint);
        }
    }
}
