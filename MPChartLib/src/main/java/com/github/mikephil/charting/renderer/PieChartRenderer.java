
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
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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
    protected Paint mValueLinePaint;

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

        mValueLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValueLinePaint.setStyle(Style.STROKE);
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
            float sweepAngle) {
        final float angleMiddle = startAngle + sweepAngle / 2.f;

        // Other point of the arc
        float arcEndPointX = center.x + radius * (float) Math.cos((startAngle + sweepAngle) * Utils.FDEG2RAD);
        float arcEndPointY = center.y + radius * (float) Math.sin((startAngle + sweepAngle) * Utils.FDEG2RAD);

        // Middle point on the arc
        float arcMidPointX = center.x + radius * (float) Math.cos(angleMiddle * Utils.FDEG2RAD);
        float arcMidPointY = center.y + radius * (float) Math.sin(angleMiddle * Utils.FDEG2RAD);

        // This is the base of the contained triangle
        double basePointsDistance = Math.sqrt(
                Math.pow(arcEndPointX - arcStartPointX, 2) +
                        Math.pow(arcEndPointY - arcStartPointY, 2));

        // After reducing space from both sides of the "slice",
        //   the angle of the contained triangle should stay the same.
        // So let's find out the height of that triangle.
        float containedTriangleHeight = (float) (basePointsDistance / 2.0 *
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
        final PointF center = mChart.getCenterCircleBox();
        final float radius = mChart.getRadius();
        final boolean drawInnerArc = mChart.isDrawHoleEnabled() && !mChart.isDrawSlicesUnderHoleEnabled();
        final float userInnerRadius = drawInnerArc
                ? radius * (mChart.getHoleRadius() / 100.f)
                : 0.f;

        int visibleAngleCount = 0;
        for (int j = 0; j < entryCount; j++) {
            // draw only if the value is greater than zero
            if ((Math.abs(dataSet.getEntryForIndex(j).getY()) > 0.000001)) {
                visibleAngleCount++;
            }
        }

        final float sliceSpace = visibleAngleCount <= 1 ? 0.f : dataSet.getSliceSpace();

        for (int j = 0; j < entryCount; j++) {

            float sliceAngle = drawAngles[j];
            float innerRadius = userInnerRadius;

            Entry e = dataSet.getEntryForIndex(j);

            // draw only if the value is greater than zero
            if ((Math.abs(e.getY()) > 0.000001)) {

                if (!mChart.needsHighlight((int) e.getX(),
                        mChart.getData().getIndexOfDataSet(dataSet))) {

                    final boolean accountForSliceSpacing = sliceSpace > 0.f && sliceAngle <= 180.f;

                    mRenderPaint.setColor(dataSet.getColor(j));

                    final float sliceSpaceAngleOuter = visibleAngleCount == 1 ?
                            0.f :
                            sliceSpace / (Utils.FDEG2RAD * radius);
                    final float startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter / 2.f) * phaseY;
                    float sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY;
                    if (sweepAngleOuter < 0.f) {
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

                    // API < 21 does not receive floats in addArc, but a RectF
                    mInnerRectBuffer.set(
                            center.x - innerRadius,
                            center.y - innerRadius,
                            center.x + innerRadius,
                            center.y + innerRadius);

                    if (drawInnerArc &&
                            (innerRadius > 0.f || accountForSliceSpacing)) {

                        if (accountForSliceSpacing) {
                            float minSpacedRadius =
                                    calculateMinimumRadiusForSpacedSlice(
                                            center, radius,
                                            sliceAngle * phaseY,
                                            arcStartPointX, arcStartPointY,
                                            startAngleOuter,
                                            sweepAngleOuter);

                            if (minSpacedRadius < 0.f)
                                minSpacedRadius = -minSpacedRadius;

                            innerRadius = Math.max(innerRadius, minSpacedRadius);
                        }

                        final float sliceSpaceAngleInner = visibleAngleCount == 1 || innerRadius == 0.f ?
                                0.f :
                                sliceSpace / (Utils.FDEG2RAD * innerRadius);
                        final float startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner / 2.f) * phaseY;
                        float sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY;
                        if (sweepAngleInner < 0.f) {
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
                    } else {

                        if (sweepAngleOuter % 360f != 0.f) {
                            if (accountForSliceSpacing) {

                                float angleMiddle = startAngleOuter + sweepAngleOuter / 2.f;

                                float sliceSpaceOffset =
                                        calculateMinimumRadiusForSpacedSlice(
                                                center,
                                                radius,
                                                sliceAngle * phaseY,
                                                arcStartPointX,
                                                arcStartPointY,
                                                startAngleOuter,
                                                sweepAngleOuter);

                                float arcEndPointX = center.x +
                                        sliceSpaceOffset * (float) Math.cos(angleMiddle * Utils.FDEG2RAD);
                                float arcEndPointY = center.y +
                                        sliceSpaceOffset * (float) Math.sin(angleMiddle * Utils.FDEG2RAD);

                                mPathBuffer.lineTo(
                                        arcEndPointX,
                                        arcEndPointY);

                            } else {
                                mPathBuffer.lineTo(
                                        center.x,
                                        center.y);
                            }
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
        float radius = mChart.getRadius();
        float rotationAngle = mChart.getRotationAngle();
        float[] drawAngles = mChart.getDrawAngles();
        float[] absoluteAngles = mChart.getAbsoluteAngles();

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        final float holeRadiusPercent = mChart.getHoleRadius() / 100.f;
        float labelRadiusOffset = radius / 10f * 3.6f;

        if (mChart.isDrawHoleEnabled()) {
            labelRadiusOffset = (radius - (radius * holeRadiusPercent)) / 2f;
        }

        final float labelRadius = radius - labelRadiusOffset;

        PieData data = mChart.getData();
        List<IPieDataSet> dataSets = data.getDataSets();

        float yValueSum = data.getYValueSum();

        boolean drawXVals = mChart.isDrawSliceTextEnabled();

        float angle;
        int xIndex = 0;

        c.save();

        for (int i = 0; i < dataSets.size(); i++) {

            IPieDataSet dataSet = dataSets.get(i);

            final boolean drawYVals = dataSet.isDrawValuesEnabled();

            if (!drawYVals && !drawXVals)
                continue;

            final PieDataSet.ValuePosition xValuePosition = dataSet.getXValuePosition();
            final PieDataSet.ValuePosition yValuePosition = dataSet.getYValuePosition();

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            float lineHeight = Utils.calcTextHeight(mValuePaint, "Q")
                    + Utils.convertDpToPixel(4f);

            ValueFormatter formatter = dataSet.getValueFormatter();

            int entryCount = dataSet.getEntryCount();

            mValueLinePaint.setColor(dataSet.getValueLineColor());
            mValueLinePaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getValueLineWidth()));

            float offset = Utils.convertDpToPixel(5.f);

            for (int j = 0; j < entryCount; j++) {

                PieEntry entry = dataSet.getEntryForIndex(j);

                if (xIndex == 0)
                    angle = 0.f;
                else
                    angle = absoluteAngles[xIndex - 1] * phaseX;

                final float sliceAngle = drawAngles[xIndex];
                final float sliceSpace = dataSet.getSliceSpace();
                final float sliceSpaceMiddleAngle = sliceSpace / (Utils.FDEG2RAD * labelRadius);

                // offset needed to center the drawn text in the slice
                final float angleOffset = (sliceAngle - sliceSpaceMiddleAngle / 2.f) / 2.f;

                angle = angle + angleOffset;

                final float transformedAngle = rotationAngle + angle * phaseY;

                float value = mChart.isUsePercentValuesEnabled() ? entry.getY()
                        / yValueSum * 100f : entry.getY();

                final float sliceXBase = (float) Math.cos(transformedAngle * Utils.FDEG2RAD);
                final float sliceYBase = (float) Math.sin(transformedAngle * Utils.FDEG2RAD);

                final boolean drawXOutside = drawXVals &&
                        xValuePosition == PieDataSet.ValuePosition.OUTSIDE_SLICE;
                final boolean drawYOutside = drawYVals &&
                        yValuePosition == PieDataSet.ValuePosition.OUTSIDE_SLICE;
                final boolean drawXInside = drawXVals &&
                        xValuePosition == PieDataSet.ValuePosition.INSIDE_SLICE;
                final boolean drawYInside = drawYVals &&
                        yValuePosition == PieDataSet.ValuePosition.INSIDE_SLICE;

                if (drawXOutside || drawYOutside) {

                    final float valueLineLength1 = dataSet.getValueLinePart1Length();
                    final float valueLineLength2 = dataSet.getValueLinePart2Length();
                    final float valueLinePart1OffsetPercentage = dataSet.getValueLinePart1OffsetPercentage() / 100.f;

                    float pt2x, pt2y;
                    float labelPtx, labelPty;

                    float line1Radius;

                    if (mChart.isDrawHoleEnabled())
                        line1Radius = (radius - (radius * holeRadiusPercent))
                                * valueLinePart1OffsetPercentage
                                + (radius * holeRadiusPercent);
                    else
                        line1Radius = radius * valueLinePart1OffsetPercentage;

                    final float polyline2Width = dataSet.isValueLineVariableLength()
                            ? labelRadius * valueLineLength2 * (float) Math.abs(Math.sin(
                            transformedAngle * Utils.FDEG2RAD))
                            : labelRadius * valueLineLength2;

                    final float pt0x = line1Radius * sliceXBase + center.x;
                    final float pt0y = line1Radius * sliceYBase + center.y;

                    final float pt1x = labelRadius * (1 + valueLineLength1) * sliceXBase + center.x;
                    final float pt1y = labelRadius * (1 + valueLineLength1) * sliceYBase + center.y;

                    if (transformedAngle % 360.0 >= 90.0 && transformedAngle % 360.0 <= 270.0) {
                        pt2x = pt1x - polyline2Width;
                        pt2y = pt1y;
                        mValuePaint.setTextAlign(Align.RIGHT);
                        labelPtx = pt2x - offset;
                        labelPty = pt2y;
                    } else {
                        pt2x = pt1x + polyline2Width;
                        pt2y = pt1y;
                        mValuePaint.setTextAlign(Align.LEFT);
                        labelPtx = pt2x + offset;
                        labelPty = pt2y;
                    }

                    if (dataSet.getValueLineColor() != ColorTemplate.COLOR_NONE) {
                        c.drawLine(pt0x, pt0y, pt1x, pt1y, mValueLinePaint);
                        c.drawLine(pt1x, pt1y, pt2x, pt2y, mValueLinePaint);
                    }

                    // draw everything, depending on settings
                    if (drawXOutside && drawYOutside) {

                        drawValue(c,
                                formatter,
                                value,
                                entry,
                                0,
                                labelPtx,
                                labelPty,
                                dataSet.getValueTextColor(j));

                        if (j < data.getEntryCount() && entry.getLabel() != null) {
                            c.drawText(entry.getLabel(), labelPtx, labelPty + lineHeight,
                                    mValuePaint);
                        }

                    } else if (drawXOutside) {
                        if (j < data.getEntryCount() && entry.getLabel() != null) {
                            mValuePaint.setColor(dataSet.getValueTextColor(j));
                            c.drawText(entry.getLabel(), labelPtx, labelPty + lineHeight / 2.f, mValuePaint);
                        }
                    } else if (drawYOutside) {

                        drawValue(c, formatter, value, entry, 0, labelPtx, labelPty + lineHeight / 2.f, dataSet
                                .getValueTextColor(j));
                    }
                }

                if (drawXInside || drawYInside) {
                    // calculate the text position
                    float x = labelRadius * sliceXBase + center.x;
                    float y = labelRadius * sliceYBase + center.y;

                    mValuePaint.setTextAlign(Align.CENTER);

                    // draw everything, depending on settings
                    if (drawXInside && drawYInside) {

                        drawValue(c, formatter, value, entry, 0, x, y, dataSet.getValueTextColor(j));

                        if (j < data.getEntryCount() && entry.getLabel() != null) {
                            c.drawText(entry.getLabel(), x, y + lineHeight,
                                    mValuePaint);
                        }

                    } else if (drawXInside) {
                        if (j < data.getEntryCount() && entry.getLabel() != null) {
                            mValuePaint.setColor(dataSet.getValueTextColor(j));
                            c.drawText(entry.getLabel(), x, y + lineHeight / 2f, mValuePaint);
                        }
                    } else if (drawYInside) {

                        drawValue(c, formatter, value, entry, 0, x, y + lineHeight / 2f, dataSet.getValueTextColor(j));
                    }
                }

                xIndex++;
            }
        }

        c.restore();
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

            float innerRadius = mChart.isDrawHoleEnabled() && !mChart.isDrawSlicesUnderHoleEnabled()
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
        final boolean drawInnerArc = mChart.isDrawHoleEnabled() && !mChart.isDrawSlicesUnderHoleEnabled();
        final float userInnerRadius = drawInnerArc
                ? radius * (mChart.getHoleRadius() / 100.f)
                : 0.f;

        final RectF highlightedCircleBox = new RectF();

        for (int i = 0; i < indices.length; i++) {

            // get the index to highlight
            float x = indices[i].getX();

            if (x >= drawAngles.length)
                continue;

            IPieDataSet set = mChart.getData()
                    .getDataSetByIndex(indices[i]
                            .getDataSetIndex());

            int entryIndex = set.getEntryIndex(x, DataSet.Rounding.CLOSEST);

            if (set == null || !set.isHighlightEnabled())
                continue;

            final int entryCount = set.getEntryCount();
            int visibleAngleCount = 0;
            for (int j = 0; j < entryCount; j++) {
                // draw only if the value is greater than zero
                if ((Math.abs(set.getEntryForIndex(j).getY()) > 0.000001)) {
                    visibleAngleCount++;
                }
            }

            if (x == 0)
                angle = 0.f;
            else
                angle = absoluteAngles[entryIndex - 1] * phaseX;

            final float sliceSpace = visibleAngleCount <= 1 ? 0.f : set.getSliceSpace();

            float sliceAngle = drawAngles[entryIndex];
            float innerRadius = userInnerRadius;

            float shift = set.getSelectionShift();
            final float highlightedRadius = radius + shift;
            highlightedCircleBox.set(mChart.getCircleBox());
            highlightedCircleBox.inset(-shift, -shift);

            final boolean accountForSliceSpacing = sliceSpace > 0.f && sliceAngle <= 180.f;

            mRenderPaint.setColor(set.getColor(entryIndex));

            final float sliceSpaceAngleOuter = visibleAngleCount == 1 ?
                    0.f :
                    sliceSpace / (Utils.FDEG2RAD * radius);

            final float sliceSpaceAngleShifted = visibleAngleCount == 1 ?
                    0.f :
                    sliceSpace / (Utils.FDEG2RAD * highlightedRadius);

            final float startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter / 2.f) * phaseY;
            float sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY;
            if (sweepAngleOuter < 0.f) {
                sweepAngleOuter = 0.f;
            }

            final float startAngleShifted = rotationAngle + (angle + sliceSpaceAngleShifted / 2.f) * phaseY;
            float sweepAngleShifted = (sliceAngle - sliceSpaceAngleShifted) * phaseY;
            if (sweepAngleShifted < 0.f) {
                sweepAngleShifted = 0.f;
            }

            mPathBuffer.reset();

            if (sweepAngleOuter % 360f == 0.f) {
                // Android is doing "mod 360"
                mPathBuffer.addCircle(center.x, center.y, highlightedRadius, Path.Direction.CW);
            } else {

                mPathBuffer.moveTo(
                        center.x + highlightedRadius * (float) Math.cos(startAngleShifted * Utils.FDEG2RAD),
                        center.y + highlightedRadius * (float) Math.sin(startAngleShifted * Utils.FDEG2RAD));

                mPathBuffer.arcTo(
                        highlightedCircleBox,
                        startAngleShifted,
                        sweepAngleShifted
                );
            }

            float sliceSpaceRadius = 0.f;
            if (accountForSliceSpacing) {
                sliceSpaceRadius =
                        calculateMinimumRadiusForSpacedSlice(
                                center, radius,
                                sliceAngle * phaseY,
                                center.x + radius * (float) Math.cos(startAngleOuter * Utils.FDEG2RAD),
                                center.y + radius * (float) Math.sin(startAngleOuter * Utils.FDEG2RAD),
                                startAngleOuter,
                                sweepAngleOuter);
            }

            // API < 21 does not receive floats in addArc, but a RectF
            mInnerRectBuffer.set(
                    center.x - innerRadius,
                    center.y - innerRadius,
                    center.x + innerRadius,
                    center.y + innerRadius);

            if (drawInnerArc &&
                    (innerRadius > 0.f || accountForSliceSpacing)) {

                if (accountForSliceSpacing) {
                    float minSpacedRadius = sliceSpaceRadius;

                    if (minSpacedRadius < 0.f)
                        minSpacedRadius = -minSpacedRadius;

                    innerRadius = Math.max(innerRadius, minSpacedRadius);
                }

                final float sliceSpaceAngleInner = visibleAngleCount == 1 || innerRadius == 0.f ?
                        0.f :
                        sliceSpace / (Utils.FDEG2RAD * innerRadius);
                final float startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner / 2.f) * phaseY;
                float sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY;
                if (sweepAngleInner < 0.f) {
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
            } else {

                if (sweepAngleOuter % 360f != 0.f) {

                    if (accountForSliceSpacing) {
                        final float angleMiddle = startAngleOuter + sweepAngleOuter / 2.f;

                        final float arcEndPointX = center.x +
                                sliceSpaceRadius * (float) Math.cos(angleMiddle * Utils.FDEG2RAD);
                        final float arcEndPointY = center.y +
                                sliceSpaceRadius * (float) Math.sin(angleMiddle * Utils.FDEG2RAD);

                        mPathBuffer.lineTo(
                                arcEndPointX,
                                arcEndPointY);

                    } else {

                        mPathBuffer.lineTo(
                                center.x,
                                center.y);
                    }

                }

            }

            mPathBuffer.close();

            mBitmapCanvas.drawPath(mPathBuffer, mRenderPaint);
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
            if ((Math.abs(e.getY()) > 0.000001)) {

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
        if (mBitmapCanvas != null) {
            mBitmapCanvas.setBitmap(null);
            mBitmapCanvas = null;
        }
        if (mDrawBitmap != null) {
            mDrawBitmap.get().recycle();
            mDrawBitmap.clear();
            mDrawBitmap = null;
        }
    }
}
