
package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
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

import java.lang.ref.WeakReference;
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
    protected WeakReference<Bitmap> mDrawBitmap;

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
                || (mDrawBitmap.get().getWidth() != width)
                || (mDrawBitmap.get().getHeight() != height)) {

            if (width > 0 && height > 0) {

                mDrawBitmap = new WeakReference<Bitmap>(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444));
                mBitmapCanvas = new Canvas(mDrawBitmap.get());
            } else
                return;
        }

        mDrawBitmap.get().eraseColor(Color.TRANSPARENT);

        PieData pieData = mChart.getData();

        for (IPieDataSet set : pieData.getDataSets()) {

            if (set.isVisible() && set.getEntryCount() > 0)
                drawDataSet(c, set);
        }
    }

    private Path mPathBuffer = new Path();
    private RectF mInnerRectBuffer = new RectF();

    protected float calculateMinimumRadiusForSpacedSlice(
            PointF center,
            float radius,
            float angle,
            float arcStartPointX,
            float arcStartPointY,
            float startAngle,
            float sweepAngle)
    {
        final float angleMiddle = startAngle + sweepAngle / 2.f;

        // Other point of the arc
        float arcEndPointX = center.x + radius * (float) Math.cos((startAngle + sweepAngle) * Utils.FDEG2RAD);
        float arcEndPointY = center.y + radius * (float) Math.sin((startAngle + sweepAngle) * Utils.FDEG2RAD);

        // Middle point on the arc
        float arcMidPointX = center.x + radius * (float) Math.cos(angleMiddle * Utils.FDEG2RAD);
        float arcMidPointY = center.y + radius * (float) Math.sin(angleMiddle * Utils.FDEG2RAD);

        // Middle point on straight line between the two point.
        // This is the base of the contained triangle
        double basePointsDistance = Math.sqrt(
                Math.pow(arcEndPointX - arcStartPointX, 2) +
                        Math.pow(arcEndPointY - arcStartPointY, 2));

        // After reducing space from both sides of the "slice",
        //   the angle of the contained triangle should stay the same.
        // So let's find out the height of that triangle.
        float containedTriangleHeight = (float)(basePointsDistance / 2.0 *
                Math.tan((180.0 - angle) / 2.0 * Utils.DEG2RAD));

        // Now we subtract that from the radius
        float spacedRadius = radius - containedTriangleHeight;

        // And now subtract the height of the arc that's between the triangle and the outer circle
        spacedRadius -= Math.sqrt(
                Math.pow(arcMidPointX - (arcEndPointX + arcStartPointX) / 2.f, 2) +
                        Math.pow(arcMidPointY - (arcEndPointY + arcStartPointY) / 2.f, 2));

        return spacedRadius;
    }

    protected void drawDataSet(Canvas c, IPieDataSet dataSet) {

        float angle = 0;
        float rotationAngle = mChart.getRotationAngle();

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        final RectF circleBox = mChart.getCircleBox();

        final int entryCount = dataSet.getEntryCount();
        final float[] drawAngles = mChart.getDrawAngles();
        float sliceSpace = dataSet.getSliceSpace();
        final PointF center = mChart.getCenterCircleBox();
        final float radius = mChart.getRadius();
        final float userInnerRadius = mChart.isDrawHoleEnabled() && isHoleTransparent()
                ? radius * (mChart.getHoleRadius() / 100.f)
                : 0.f;

        int visibleAngleCount = 0;
        for (int j = 0; j < entryCount; j++) {
            // draw only if the value is greater than zero
            if ((Math.abs(dataSet.getEntryForIndex(j).getVal()) > 0.000001)) {
                visibleAngleCount++;
            }
        }

        for (int j = 0; j < entryCount; j++) {

            float sliceAngle = drawAngles[j];
            float innerRadius = userInnerRadius;

            Entry e = dataSet.getEntryForIndex(j);

            // draw only if the value is greater than zero
            if ((Math.abs(e.getVal()) > 0.000001)) {

                if (!mChart.needsHighlight(e.getXIndex(),
                        mChart.getData().getIndexOfDataSet(dataSet))) {

                    mRenderPaint.setColor(dataSet.getColor(j));

                    final float sliceSpaceOuterAngle = visibleAngleCount == 1 ?
                            0.f :
                            sliceSpace / (Utils.FDEG2RAD * radius);
                    final float startAngleOuter = rotationAngle + (angle + sliceSpaceOuterAngle / 2.f) * phaseY;
                    float sweepAngleOuter = (sliceAngle - sliceSpaceOuterAngle) * phaseY;
                    if (sweepAngleOuter < 0.f)
                    {
                        sweepAngleOuter = 0.f;
                    }

                    mPathBuffer.reset();

                    float arcStartPointX = 0.f, arcStartPointY = 0.f;

                    if (sweepAngleOuter % 360f == 0.f) {
                        // Android is doing "mod 360"
                        mPathBuffer.addCircle(center.x, center.y, radius, Path.Direction.CW);
                    } else {

                        arcStartPointX = center.x + radius * (float) Math.cos(startAngleOuter * Utils.FDEG2RAD);
                        arcStartPointY = center.y + radius * (float) Math.sin(startAngleOuter * Utils.FDEG2RAD);

                        mPathBuffer.moveTo(arcStartPointX, arcStartPointY);

                        mPathBuffer.arcTo(
                                circleBox,
                                startAngleOuter,
                                sweepAngleOuter
                        );
                    }

                    if (sliceSpace > 0.f) {
                        innerRadius = Math.max(innerRadius,
                                calculateMinimumRadiusForSpacedSlice(
                                        center, radius,
                                        sliceAngle * phaseY,
                                        arcStartPointX, arcStartPointY,
                                        startAngleOuter,
                                        sweepAngleOuter));
                    }

                    // API < 21 does not receive floats in addArc, but a RectF
                    mInnerRectBuffer.set(
                            center.x - innerRadius,
                            center.y - innerRadius,
                            center.x + innerRadius,
                            center.y + innerRadius);

                    if (innerRadius > 0.0)
                    {
                        final float sliceSpaceInnerAngle = visibleAngleCount == 1 ?
                                0.f :
                                sliceSpace / (Utils.FDEG2RAD * innerRadius);
                        final float startAngleInner = rotationAngle + (angle + sliceSpaceInnerAngle / 2.f) * phaseY;
                        float sweepAngleInner = (sliceAngle - sliceSpaceInnerAngle) * phaseY;
                        if (sweepAngleInner < 0.f)
                        {
                            sweepAngleInner = 0.f;
                        }
                        final float endAngleInner = startAngleInner + sweepAngleInner;

                        if (sweepAngleOuter % 360f == 0.f) {
                            // Android is doing "mod 360"
                            mPathBuffer.addCircle(center.x, center.y, innerRadius, Path.Direction.CCW);
                        } else {

                            mPathBuffer.lineTo(
                                    center.x + innerRadius * (float) Math.cos(endAngleInner * Utils.FDEG2RAD),
                                    center.y + innerRadius * (float) Math.sin(endAngleInner * Utils.FDEG2RAD));

                            mPathBuffer.arcTo(
                                    mInnerRectBuffer,
                                    endAngleInner,
                                    -sweepAngleInner
                            );
                        }
                    }
                    else {

                        if (sweepAngleOuter % 360f != 0.f) {
                            mPathBuffer.lineTo(
                                    center.x,
                                    center.y);
                        }

                    }

                    mPathBuffer.close();

                    mBitmapCanvas.drawPath(mPathBuffer, mRenderPaint);
                }
            }

            angle += sliceAngle * phaseX;
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

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float off = r / 10f * 3.6f;

        if (mChart.isDrawHoleEnabled()) {
            off = (r - (r / 100f * mChart.getHoleRadius())) / 2f;
        }

        r -= off; // offset to keep things inside the chart

        PieData data = mChart.getData();
        List<IPieDataSet> dataSets = data.getDataSets();

        float yValueSum = data.getYValueSum();

        boolean drawXVals = mChart.isDrawSliceTextEnabled();

        float angle;
        int xIndex = 0;

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
                    (int) Math.ceil(entryCount * phaseX), entryCount); j < maxEntry; j++) {

                Entry entry = dataSet.getEntryForIndex(j);

                if (xIndex == 0)
                    angle = 0.f;
                else
                    angle = absoluteAngles[xIndex - 1] * phaseX;

                final float sliceAngle = drawAngles[xIndex];
                final float sliceSpace = dataSet.getSliceSpace();
                final float sliceSpaceMiddleAngle = sliceSpace / (Utils.FDEG2RAD * r);

                // offset needed to center the drawn text in the slice
                final float offset = (sliceAngle - sliceSpaceMiddleAngle / 2.f) / 2.f;

                angle = angle + offset;

                // calculate the text position
                float x = (float) (r
                        * Math.cos(Math.toRadians(rotationAngle + angle))
                        + center.x);
                float y = (float) (r
                        * Math.sin(Math.toRadians(rotationAngle + angle))
                        + center.y);

                float value = mChart.isUsePercentValuesEnabled() ? entry.getVal()
                        / yValueSum * 100f : entry.getVal();

                ValueFormatter formatter = dataSet.getValueFormatter();

                boolean drawYVals = dataSet.isDrawValuesEnabled();

                // draw everything, depending on settings
                if (drawXVals && drawYVals) {

                    drawValue(c, formatter, value, entry, 0, x, y, dataSet.getValueTextColor(j));

                    if (j < data.getXValCount()) {
                        c.drawText(data.getXVals().get(j), x, y + lineHeight,
                                mValuePaint);
                    }

                } else if (drawXVals) {
                    if (j < data.getXValCount()) {
                        mValuePaint.setColor(dataSet.getValueTextColor(j));
                        c.drawText(data.getXVals().get(j), x, y + lineHeight / 2f, mValuePaint);
                    }
                } else if (drawYVals) {

                    drawValue(c, formatter, value, entry, 0, x, y + lineHeight / 2f, dataSet.getValueTextColor(j));
                }

                xIndex++;
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
        // drawCircles(c);
        drawHole(c);
        c.drawBitmap(mDrawBitmap.get(), 0, 0, null);
        drawCenterText(c);
    }

    private Path mHoleCirclePath = new Path();

    /**
     * draws the hole in the center of the chart and the transparent circle /
     * hole
     */
    protected void drawHole(Canvas c) {

        if (mChart.isDrawHoleEnabled()) {

            float radius = mChart.getRadius();
            float holeRadius = radius * (mChart.getHoleRadius() / 100);
            PointF center = mChart.getCenterCircleBox();

            if (Color.alpha(mHolePaint.getColor()) > 0) {
                // draw the hole-circle
                mBitmapCanvas.drawCircle(
                        center.x, center.y,
                        holeRadius, mHolePaint);
            }

            // only draw the circle if it can be seen (not covered by the hole)
            if (Color.alpha(mTransparentCirclePaint.getColor()) > 0 &&
                    mChart.getTransparentCircleRadius() > mChart.getHoleRadius()) {

                int alpha = mTransparentCirclePaint.getAlpha();
                float secondHoleRadius = radius * (mChart.getTransparentCircleRadius() / 100);

                mTransparentCirclePaint.setAlpha((int) ((float) alpha * mAnimator.getPhaseX() * mAnimator.getPhaseY()));

                // draw the transparent-circle
                mHoleCirclePath.reset();
                mHoleCirclePath.addCircle(center.x, center.y, secondHoleRadius, Path.Direction.CW);
                mHoleCirclePath.addCircle(center.x, center.y, holeRadius, Path.Direction.CCW);
                mBitmapCanvas.drawPath(mHoleCirclePath, mTransparentCirclePaint);

                // reset alpha
                mTransparentCirclePaint.setAlpha(alpha);
            }
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

            float innerRadius = mChart.isDrawHoleEnabled() && isHoleTransparent()
                    ? mChart.getRadius() * (mChart.getHoleRadius() / 100f)
                    : mChart.getRadius();

            RectF holeRect = mRectBuffer[0];
            holeRect.left = center.x - innerRadius;
            holeRect.top = center.y - innerRadius;
            holeRect.right = center.x + innerRadius;
            holeRect.bottom = center.y + innerRadius;
            RectF boundingRect = mRectBuffer[1];
            boundingRect.set(holeRect);

            float radiusPercent = mChart.getCenterTextRadiusPercent() / 100f;
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

            //float layoutWidth = Utils.getStaticLayoutMaxWidth(mCenterTextLayout);
            float layoutHeight = mCenterTextLayout.getHeight();

            c.save();
            if (Build.VERSION.SDK_INT >= 18) {
                Path path = new Path();
                path.addOval(holeRect, Path.Direction.CW);
                c.clipPath(path);
            }

            c.translate(boundingRect.left, boundingRect.top + (boundingRect.height() - layoutHeight) / 2.f);
            mCenterTextLayout.draw(c);

            c.restore();
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float angle;
        float rotationAngle = mChart.getRotationAngle();

        float[] drawAngles = mChart.getDrawAngles();
        float[] absoluteAngles = mChart.getAbsoluteAngles();
        final PointF center = mChart.getCenterCircleBox();
        final float radius = mChart.getRadius();
        final float userInnerRadius = mChart.isDrawHoleEnabled() && isHoleTransparent()
                ? radius * (mChart.getHoleRadius() / 100.f)
                : 0.f;

        final RectF highlightedCircleBox = new RectF();

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

            final int entryCount = set.getEntryCount();
            int visibleAngleCount = 0;
            for (int j = 0; j < entryCount; j++) {
                // draw only if the value is greater than zero
                if ((Math.abs(set.getEntryForIndex(j).getVal()) > 0.000001)) {
                    visibleAngleCount++;
                }
            }

            if (xIndex == 0)
                angle = 0.f;
            else
                angle = absoluteAngles[xIndex - 1] * phaseX;

            float sliceSpace = set.getSliceSpace();

            float sliceAngle = drawAngles[xIndex];
            final float sliceSpaceOuterAngle = visibleAngleCount == 1 ?
                    0.f :
                    sliceSpace / (Utils.FDEG2RAD * radius);
            float innerRadius = userInnerRadius;

            float shift = set.getSelectionShift();
            final float highlightedRadius = radius + shift;
            highlightedCircleBox.set(mChart.getCircleBox());
            highlightedCircleBox.inset(-shift, -shift);

            mRenderPaint.setColor(set.getColor(xIndex));

            final float startAngleOuter = rotationAngle + (angle + sliceSpaceOuterAngle / 2.f) * phaseY;
            float sweepAngleOuter = (sliceAngle - sliceSpaceOuterAngle) * phaseY;
            if (sweepAngleOuter < 0.f)
            {
                sweepAngleOuter = 0.f;
            }

            mPathBuffer.reset();
            
            if (sweepAngleOuter % 360f == 0.f) {
                // Android is doing "mod 360"
                mPathBuffer.addCircle(center.x, center.y, highlightedRadius, Path.Direction.CW);
            } else {

                mPathBuffer.moveTo(
                        center.x + highlightedRadius * (float) Math.cos(startAngleOuter * Utils.FDEG2RAD),
                        center.y + highlightedRadius * (float) Math.sin(startAngleOuter * Utils.FDEG2RAD));

                mPathBuffer.arcTo(
                        highlightedCircleBox,
                        startAngleOuter,
                        sweepAngleOuter
                );
            }

            if (sliceSpace > 0.f) {
                innerRadius = Math.max(innerRadius,
                        calculateMinimumRadiusForSpacedSlice(
                                center, radius,
                                sliceAngle * phaseY,
                                center.x + radius * (float) Math.cos(startAngleOuter * Utils.FDEG2RAD),
                                center.y + radius * (float) Math.sin(startAngleOuter * Utils.FDEG2RAD),
                                startAngleOuter,
                                sweepAngleOuter));
            }

            // API < 21 does not receive floats in addArc, but a RectF
            mInnerRectBuffer.set(
                    center.x - innerRadius,
                    center.y - innerRadius,
                    center.x + innerRadius,
                    center.y + innerRadius);

            if (innerRadius > 0.0) {
                final float sliceSpaceInnerAngle = visibleAngleCount == 1 ?
                        0.f :
                        sliceSpace / (Utils.FDEG2RAD * innerRadius);
                final float startAngleInner = rotationAngle + (angle + sliceSpaceInnerAngle / 2.f) * phaseY;
                float sweepAngleInner = (sliceAngle - sliceSpaceInnerAngle) * phaseY;
                if (sweepAngleInner < 0.f)
                {
                    sweepAngleInner = 0.f;
                }
                final float endAngleInner = startAngleInner + sweepAngleInner;

                if (sweepAngleOuter % 360f == 0.f) {
                    // Android is doing "mod 360"
                    mPathBuffer.addCircle(center.x, center.y, innerRadius, Path.Direction.CCW);
                } else {

                    mPathBuffer.lineTo(
                            center.x + innerRadius * (float) Math.cos(endAngleInner * Utils.FDEG2RAD),
                            center.y + innerRadius * (float) Math.sin(endAngleInner * Utils.FDEG2RAD));

                    mPathBuffer.arcTo(
                            mInnerRectBuffer,
                            endAngleInner,
                            -sweepAngleInner
                    );
                }
            }
            else {

                if (sweepAngleOuter % 360f != 0.f) {
                    mPathBuffer.lineTo(
                            center.x,
                            center.y);
                }

            }

            mPathBuffer.close();

            mBitmapCanvas.drawPath(mPathBuffer, mRenderPaint);
        }
    }

    /**
     * Returns true if the hole in the center of the PieChart is transparent,
     * false if not.
     *
     * @return true if hole is transparent.
     */
    private boolean isHoleTransparent() {
        return Color.alpha(mHolePaint.getColor()) <= 0;
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

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

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
                        * phaseY)) + center.x);
                float y = (float) ((r - circleRadius)
                        * Math.sin(Math.toRadians((angle + sliceAngle)
                        * phaseY)) + center.y);

                mRenderPaint.setColor(dataSet.getColor(j));
                mBitmapCanvas.drawCircle(x, y, circleRadius, mRenderPaint);
            }

            angle += sliceAngle * phaseX;
        }
    }

    /**
     * Releases the drawing bitmap. This should be called when {@link LineChart#onDetachedFromWindow()}.
     */
    public void releaseBitmap() {
        if (mDrawBitmap != null) {
            mDrawBitmap.get().recycle();
            mDrawBitmap.clear();
            mDrawBitmap = null;
        }
    }
}
