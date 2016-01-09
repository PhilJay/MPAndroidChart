
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
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
    private CharSequence mCenterTextLastValue;
    private RectF mCenterTextLastBounds = new RectF();
    private RectF[] mRectBuffer = {new RectF(), new RectF(), new RectF()};

    /**
     * Bitmap for drawing the center hole
     */
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
        mTransparentCirclePaint.setAlpha(105);

        mCenterTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mCenterTextPaint.setColor(Color.BLACK);
        mCenterTextPaint.setTextSize(Utils.convertDpToPixel(12f));
        //mCenterTextPaint.setTextAlign(Align.CENTER);

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

        for (IPieDataSet set : pieData.getDataSets()) {

            if (set.isVisible() && set.getEntryCount() > 0)
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(Canvas c, IPieDataSet dataSet) {

        float angle = 0;
        float rotationAngle = mChart.getRotationAngle();

        float[] drawAngles = mChart.getDrawAngles();

        for (int j = 0; j < dataSet.getEntryCount(); j++) {

            float sliceAngle = drawAngles[j];
            float sliceSpace = dataSet.getSliceSpace();

            Entry e = dataSet.getEntryForIndex(j);

            // draw only if the value is greater than zero
            if ((Math.abs(e.getVal()) > 0.000001)) {

                if (!mChart.needsHighlight(e.getXIndex(),
                        mChart.getData().getIndexOfDataSet(dataSet))) {

                    mRenderPaint.setColor(dataSet.getColor(j));
                    mBitmapCanvas.drawArc(mChart.getCircleBox(),
                            rotationAngle + (angle + sliceSpace / 2f) * mAnimator.getPhaseY(),
                            (sliceAngle - sliceSpace / 2f) * mAnimator.getPhaseY(),
                            true, mRenderPaint);
                }
            }

            angle += sliceAngle * mAnimator.getPhaseX();
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
        List<IPieDataSet> dataSets = data.getDataSets();
        boolean drawXVals = mChart.isDrawSliceTextEnabled();

        int cnt = 0;

        for (int i = 0; i < dataSets.size(); i++) {

            IPieDataSet dataSet = dataSets.get(i);

            if (!dataSet.isDrawValuesEnabled() && !drawXVals)
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            float lineHeight = Utils.calcTextHeight(mValuePaint, "Q")
                    + Utils.convertDpToPixel(4f);

            int entryCount = dataSet.getEntryCount();

            for (int j = 0, maxEntry = Math.min(
                    (int) Math.ceil(entryCount * mAnimator.getPhaseX()), entryCount); j < maxEntry; j++) {

                Entry entry = dataSet.getEntryForIndex(j);

                // offset needed to center the drawn text in the slice
                float offset = drawAngles[cnt] / 2;

                float angle = (absoluteAngles[cnt] - offset) * mAnimator.getPhaseY();
                // calculate the text position
                float x = (float) (r
                        * Math.cos(Math.toRadians(rotationAngle + angle))
                        + center.x);
                float y = (float) (r
                        * Math.sin(Math.toRadians(rotationAngle + angle))
                        + center.y);

                float value = mChart.isUsePercentValuesEnabled() ? entry.getVal()
                        / data.getYValueSum() * 100f : entry.getVal();

                ValueFormatter formatter = dataSet.getValueFormatter();

                boolean drawYVals = dataSet.isDrawValuesEnabled();

                // draw everything, depending on settings
                if (drawXVals && drawYVals) {

                    drawValue(c, formatter, value, entry, 0, x, y);

                    if (j < data.getXValCount())
                        c.drawText(data.getXVals().get(j), x, y + lineHeight,
                                mValuePaint);

                } else if (drawXVals && !drawYVals) {
                    if (j < data.getXValCount())
                        c.drawText(data.getXVals().get(j), x, y + lineHeight / 2f, mValuePaint);
                } else if (!drawXVals && drawYVals) {

                    drawValue(c, formatter, value, entry, 0, x, y + lineHeight / 2f);
                }

                cnt++;
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
        // drawCircles(c);
        drawHole(c);
        c.drawBitmap(mDrawBitmap, 0, 0, null);
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

            // only draw the circle if it can be seen (not covered by the hole)
            if (transparentCircleRadius > holeRadius) {

                // get original alpha
                int alpha = mTransparentCirclePaint.getAlpha();
                mTransparentCirclePaint.setAlpha((int) ((float) alpha * mAnimator.getPhaseX() * mAnimator.getPhaseY()));

                // draw the transparent-circle
                mBitmapCanvas.drawCircle(center.x, center.y,
                        radius / 100 * transparentCircleRadius, mTransparentCirclePaint);

                // reset alpha
                mTransparentCirclePaint.setAlpha(alpha);
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

        CharSequence centerText = mChart.getCenterText();

        if (mChart.isDrawCenterTextEnabled() && centerText != null) {

            PointF center = mChart.getCenterCircleBox();

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
                boundingRect.inset(
                        (boundingRect.width() - boundingRect.width() * radiusPercent) / 2.f,
                        (boundingRect.height() - boundingRect.height() * radiusPercent) / 2.f
                );
            }

            if (!centerText.equals(mCenterTextLastValue) || !boundingRect.equals(mCenterTextLastBounds)) {

                // Next time we won't recalculate StaticLayout...
                mCenterTextLastBounds.set(boundingRect);
                mCenterTextLastValue = centerText;

                float width = mCenterTextLastBounds.width();

                // If width is 0, it will crash. Always have a minimum of 1
                mCenterTextLayout = new StaticLayout(centerText, 0, centerText.length(),
                        mCenterTextPaint,
                        (int) Math.max(Math.ceil(width), 1.f),
                        Layout.Alignment.ALIGN_CENTER, 1.f, 0.f, false);
            }

            // I wish we could make an ellipse clipping path on Android to clip to the hole...
            // If we ever find out how, this is the place to add it, based on holeRect

            //float layoutWidth = Utils.getStaticLayoutMaxWidth(mCenterTextLayout);
            float layoutHeight = mCenterTextLayout.getHeight();

            c.save();
            c.translate(boundingRect.left, boundingRect.top + (boundingRect.height() - layoutHeight) / 2.f);
            mCenterTextLayout.draw(c);
            c.restore();

//            }
//
//        else {
//
//
//                // get all lines from the text
//                String[] lines = centerText.toString().split("\n");
//
//                float maxlineheight = 0f;
//
//                // calc the maximum line height
//                for (String line : lines) {
//                    float curHeight = Utils.calcTextHeight(mCenterTextPaint, line);
//                    if (curHeight > maxlineheight)
//                        maxlineheight = curHeight;
//                }
//
//                float linespacing = maxlineheight * 0.25f;
//
//                float totalheight = maxlineheight * lines.length - linespacing * (lines.length - 1);
//
//                int cnt = lines.length;
//
//                float y = center.y;
//
//                for (int i = 0; i < lines.length; i++) {
//
//                    String line = lines[lines.length - i - 1];
//
//
//
//                    c.drawText(line, center.x, y
//                                    + maxlineheight * cnt - totalheight / 2f,
//                            mCenterTextPaint);
//                    cnt--;
//                    y -= linespacing;
//                }
//            }
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

            IPieDataSet set = mChart.getData()
                    .getDataSetByIndex(indices[i]
                            .getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            if (xIndex == 0)
                angle = rotationAngle;
            else
                angle = rotationAngle + absoluteAngles[xIndex - 1];

            angle *= mAnimator.getPhaseX();

            float sliceAngle = drawAngles[xIndex];
            float sliceSpace = set.getSliceSpace();

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
            mBitmapCanvas.drawArc(highlighted,
                    rotationAngle + (angle + sliceSpace / 2f) * mAnimator.getPhaseY(),
                    (sliceAngle - sliceSpace / 2f) * mAnimator.getPhaseY(),
                    true, mRenderPaint);
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

        IPieDataSet dataSet = mChart.getData().getDataSet();

        if (!dataSet.isVisible())
            return;

        PointF center = mChart.getCenterCircleBox();
        float r = mChart.getRadius();

        // calculate the radius of the "slice-circle"
        float circleRadius = (r - (r * mChart.getHoleRadius() / 100f)) / 2f;

        float[] drawAngles = mChart.getDrawAngles();
        float angle = mChart.getRotationAngle();

        for (int j = 0; j < dataSet.getEntryCount(); j++) {

            float sliceAngle = drawAngles[j];

            Entry e = dataSet.getEntryForIndex(j);

            // draw only if the value is greater than zero
            if ((Math.abs(e.getVal()) > 0.000001)) {

                float x = (float) ((r - circleRadius)
                        * Math.cos(Math.toRadians((angle + sliceAngle)
                        * mAnimator.getPhaseY())) + center.x);
                float y = (float) ((r - circleRadius)
                        * Math.sin(Math.toRadians((angle + sliceAngle)
                        * mAnimator.getPhaseY())) + center.y);

                mRenderPaint.setColor(dataSet.getColor(j));
                mBitmapCanvas.drawCircle(x, y, circleRadius, mRenderPaint);
            }

            angle += sliceAngle * mAnimator.getPhaseX();
        }
    }

    /**
     * Releases the drawing bitmap. This should be called when {@link LineChart#onDetachedFromWindow()}.
     */
    public void releaseBitmap() {
        if (mDrawBitmap != null) {
            mDrawBitmap.recycle();
            mDrawBitmap = null;
        }
    }
}
