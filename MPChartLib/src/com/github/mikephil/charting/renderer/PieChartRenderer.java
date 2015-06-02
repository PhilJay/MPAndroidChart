
package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

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
    private TextPaint mCenterTextPaint;

    private StaticLayout mCenterTextLayout;
    private String mCenterTextLastValue;
    private RectF mCenterTextLastBounds = new RectF();
    private RectF[] mRectBuffer = { new RectF(), new RectF(), new RectF() };

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

        mCenterTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
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

    public TextPaint getPaintCenterText() {
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
            } else
                return;
        }

        mDrawBitmap.eraseColor(Color.TRANSPARENT);

        PieData pieData = mChart.getData();

        for (PieDataSet set : pieData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(Canvas c, PieDataSet dataSet) {

        float angle = mChart.getRotationAngle();

        List<Entry> entries = dataSet.getYVals();
        float[] drawAngles = mChart.getDrawAngles();

        for (int j = 0; j < entries.size(); j++) {

            float newangle = drawAngles[j];
            float sliceSpace = dataSet.getSliceSpace();

            Entry e = entries.get(j);

            // draw only if the value is greater than zero
            if ((Math.abs(e.getVal()) > 0.000001)) {

                if (!mChart.needsHighlight(e.getXIndex(),
                        mChart.getData().getIndexOfDataSet(dataSet))) {

                    mRenderPaint.setColor(dataSet.getColor(j));
                    mBitmapCanvas.drawArc(mChart.getCircleBox(),
                            (angle + sliceSpace / 2f) * mAnimator.getPhaseY(),
                            (newangle - sliceSpace / 2f) * mAnimator.getPhaseY(),
                            true, mRenderPaint);
                }
            }

            angle += newangle * mAnimator.getPhaseX();
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
        // drawCircles(c);
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

            if (mChart.isCenterTextWordWrapEnabled()) {

                float innerRadius = mChart.isDrawHoleEnabled() && mChart.isHoleTransparent() ? mChart.getRadius() * (mChart.getHoleRadius() / 100f) : mChart.getRadius();

                RectF holeRect = mRectBuffer[0];
                holeRect.left = center.x - innerRadius;
                holeRect.top = center.y - innerRadius;
                holeRect.right = center.x + innerRadius;
                holeRect.bottom = center.y + innerRadius;
                RectF boundingRect = mRectBuffer[1];
                boundingRect.set(holeRect);

                float radiusPercent = mChart.getCenterTextRadiusPercent();
                if (radiusPercent > 0.0) {
                    boundingRect.inset((boundingRect.width() - boundingRect.width() * radiusPercent) / 2.f,
                            (boundingRect.height() - boundingRect.height() * radiusPercent) / 2.f);
                }

                if (!centerText.equals(mCenterTextLastValue) || !boundingRect.equals(mCenterTextLastBounds)) {

                    // Next time we won't recalculate StaticLayout...
                    mCenterTextLastBounds.set(boundingRect);
                    mCenterTextLastValue = centerText;

                    // If width is 0, it will crash. Always have a minimum of 1
                    mCenterTextLayout = new StaticLayout(centerText, 0, centerText.length(),
                            mCenterTextPaint,
                            (int)Math.max(Math.ceil(mCenterTextLastBounds.width()), 1.f),
                            Layout.Alignment.ALIGN_NORMAL, 1.f, 0.f, false);
                }

                // I wish we could make an ellipse clipping path on Android to clip to the hole...
                // If we ever find out how, this is the place to add it, based on holeRect

                //float layoutWidth = Utils.getStaticLayoutMaxWidth(mCenterTextLayout);
                float layoutHeight = mCenterTextLayout.getHeight();

                c.save();
                c.translate(boundingRect.centerX(), boundingRect.top + (boundingRect.height() - layoutHeight) / 2.f);
                mCenterTextLayout.draw(c);
                c.restore();

            } else {

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

            PieDataSet set = mChart.getData()
                    .getDataSetByIndex(indices[i]
                            .getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            if (xIndex == 0)
                angle = rotationAngle;
            else
                angle = rotationAngle + absoluteAngles[xIndex - 1];

            angle *= mAnimator.getPhaseY();

            float sliceDegrees = drawAngles[xIndex];

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

    /**
     * This gives all pie-slices a rounded edge.
     * 
     * @param c
     */
    protected void drawRoundedSlices(Canvas c) {

        if (!mChart.isDrawRoundedSlicesEnabled())
            return;

        PieDataSet dataSet = mChart.getData().getDataSet();

        if (!dataSet.isVisible())
            return;
        
        PointF center = mChart.getCenterCircleBox();
        float r = mChart.getRadius();

        // calculate the radius of the "slice-circle"
        float circleRadius = (r - (r * mChart.getHoleRadius() / 100f)) / 2f;

        List<Entry> entries = dataSet.getYVals();
        float[] drawAngles = mChart.getDrawAngles();
        float angle = mChart.getRotationAngle();

        for (int j = 0; j < entries.size(); j++) {

            float newangle = drawAngles[j];

            Entry e = entries.get(j);

            // draw only if the value is greater than zero
            if ((Math.abs(e.getVal()) > 0.000001)) {

                float x = (float) ((r - circleRadius)
                        * Math.cos(Math.toRadians((angle + newangle)
                                * mAnimator.getPhaseY())) + center.x);
                float y = (float) ((r - circleRadius)
                        * Math.sin(Math.toRadians((angle + newangle)
                                * mAnimator.getPhaseY())) + center.y);

                mRenderPaint.setColor(dataSet.getColor(j));
                mBitmapCanvas.drawCircle(x, y, circleRadius, mRenderPaint);
            }

            angle += newangle * mAnimator.getPhaseX();
        }
    }
}
