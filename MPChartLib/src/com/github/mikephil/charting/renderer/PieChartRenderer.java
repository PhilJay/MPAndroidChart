
package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class PieChartRenderer extends DataRenderer {

    protected PieChart mChart;

    /**
     * paint for the hole in the center of the pie chart and the transparent
     * circle
     */
    protected Paint mHolePaint;
    protected Paint mTransparentCirclePaint;

    /**
     * paint object for the text that can be displayed in the center of the
     * chart
     */
    private Paint mCenterTextPaint;

    /** Bitmap for drawing the center hole */
    protected Bitmap mDrawBitmap;

    protected Canvas mBitmapCanvas;

    public PieChartRenderer(PieChart chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mHolePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHolePaint.setColor(Color.WHITE);
        mHolePaint.setStyle(Style.FILL);

        mTransparentCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTransparentCirclePaint.setColor(Color.WHITE);
        mTransparentCirclePaint.setStyle(Style.FILL);

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

    public Paint getPaintTransparentCircle() {
        return mTransparentCirclePaint;
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

        int width = (int) mViewPortHandler.getChartWidth();
        int height = (int) mViewPortHandler.getChartHeight();

        if (mDrawBitmap == null
                || (mDrawBitmap.getWidth() != width)
                || (mDrawBitmap.getHeight() != height)) {

            if (width > 0 && height > 0) {

                mDrawBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                mBitmapCanvas = new Canvas(mDrawBitmap);
            }
        }

        // Paint p = new Paint();
        // p.setStyle(Paint.Style.FILL);
        // p.setColor(Color.BLACK);
        // c.drawRect(mChart.getCircleBox(), p);

        mDrawBitmap.eraseColor(Color.TRANSPARENT);

        PieData pieData = mChart.getData();

        for (PieDataSet set : pieData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(Canvas c, PieDataSet dataSet) {

        float angle = mChart.getRotationAngle();

        int cnt = 0;

        List<Entry> entries = dataSet.getYVals();
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
                    mBitmapCanvas.drawArc(mChart.getCircleBox(),
                            (angle + sliceSpace / 2f) * mAnimator.getPhaseY(),
                            newangle * mAnimator.getPhaseY()
                                    - sliceSpace / 2f, true, mRenderPaint);
                }
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

        float off = r / 10f * 3.6f;

        if (mChart.isDrawHoleEnabled()) {
            off = (r - (r / 100f * mChart.getHoleRadius())) / 2f;
        }

        r -= off; // offset to keep things inside the chart

        PieData data = mChart.getData();
        List<PieDataSet> dataSets = data.getDataSets();
        boolean drawXVals = mChart.isDrawSliceTextEnabled();

        int cnt = 0;

        for (int i = 0; i < dataSets.size(); i++) {

            PieDataSet dataSet = dataSets.get(i);

            if (!dataSet.isDrawValuesEnabled() && !drawXVals)
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            List<Entry> entries = dataSet.getYVals();

            for (int j = 0, maxEntry = Math.min(
                    (int) Math.ceil(entries.size() * mAnimator.getPhaseX()), entries.size()); j < maxEntry; j++) {

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

                float lineHeight = Utils.calcTextHeight(mValuePaint, val)
                        + Utils.convertDpToPixel(4f);

                boolean drawYVals = dataSet.isDrawValuesEnabled();

                // draw everything, depending on settings
                if (drawXVals && drawYVals) {

                    c.drawText(val, x, y, mValuePaint);
                    if (j < data.getXValCount())
                        c.drawText(data.getXVals().get(j), x, y + lineHeight,
                                mValuePaint);

                } else if (drawXVals && !drawYVals) {
                    if (j < data.getXValCount())
                        c.drawText(data.getXVals().get(j), x, y + lineHeight / 2f, mValuePaint);
                } else if (!drawXVals && drawYVals) {

                    c.drawText(val, x, y + lineHeight / 2f, mValuePaint);
                }

                cnt++;
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
        drawHole(c);
        c.drawBitmap(mDrawBitmap, 0, 0, mRenderPaint);
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

            if (transparentCircleRadius > holeRadius && mAnimator.getPhaseX() >= 1f
                    && mAnimator.getPhaseY() >= 1f) {

                int color = mTransparentCirclePaint.getColor();

                // make transparent
                mTransparentCirclePaint.setColor(color & 0x60FFFFFF);

                // draw the transparent-circle
                mBitmapCanvas.drawCircle(center.x, center.y,
                        radius / 100 * transparentCircleRadius, mTransparentCirclePaint);

                mTransparentCirclePaint.setColor(color);
            }

            // draw the hole-circle
            mBitmapCanvas.drawCircle(center.x, center.y,
                    radius / 100 * holeRadius, mHolePaint);
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

            float maxlineheight = 0f;

            // calc the maximum line height
            for (String line : lines) {
                float curHeight = Utils.calcTextHeight(mCenterTextPaint, line);
                if (curHeight > maxlineheight)
                    maxlineheight = curHeight;
            }

            float linespacing = maxlineheight * 0.25f;

            float totalheight = maxlineheight * lines.length - linespacing * (lines.length - 1);

            int cnt = lines.length;

            float y = center.y;

            for (int i = 0; i < lines.length; i++) {

                String line = lines[lines.length - i - 1];

                c.drawText(line, center.x, y
                        + maxlineheight * cnt - totalheight / 2f,
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
            mBitmapCanvas.drawArc(highlighted, angle + set.getSliceSpace() / 2f, sliceDegrees
                    * mAnimator.getPhaseY()
                    - set.getSliceSpace() / 2f, true, mRenderPaint);
        }
    }
}
