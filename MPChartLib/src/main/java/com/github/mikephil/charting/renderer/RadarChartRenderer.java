
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class RadarChartRenderer extends LineRadarRenderer {

    protected RadarChart mChart;

    /**
     * paint for drawing the web
     */
    protected Paint mWebPaint;
    protected Paint mHighlightCirclePaint;

    public RadarChartRenderer(RadarChart chart, ChartAnimator animator,
                              ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));

        mWebPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWebPaint.setStyle(Paint.Style.STROKE);

        mHighlightCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public Paint getWebPaint() {
        return mWebPaint;
    }

    @Override
    public void initBuffers() {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawData(Canvas c) {

        RadarData radarData = mChart.getData();

        int mostEntries = radarData.getMaxEntryCountSet().getEntryCount();

        for (IRadarDataSet set : radarData.getDataSets()) {

            if (set.isVisible() && set.getEntryCount() > 0) {
                drawDataSet(c, set, mostEntries);
            }
        }
    }

    /**
     * Draws the RadarDataSet
     *
     * @param c
     * @param dataSet
     * @param mostEntries the entry count of the dataset with the most entries
     */
    protected void drawDataSet(Canvas c, IRadarDataSet dataSet, int mostEntries) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        PointF center = mChart.getCenterOffsets();

        Path surface = new Path();

        boolean hasMovedToPoint = false;

        for (int j = 0; j < dataSet.getEntryCount(); j++) {

            mRenderPaint.setColor(dataSet.getColor(j));

            RadarEntry e = dataSet.getEntryForIndex(j);

            PointF p = Utils.getPosition(
                    center,
                    (e.getY() - mChart.getYChartMin()) * factor * phaseY,
                    sliceangle * j * phaseX + mChart.getRotationAngle());

            if (Float.isNaN(p.x))
                continue;

            if (!hasMovedToPoint) {
                surface.moveTo(p.x, p.y);
                hasMovedToPoint = true;
            } else
                surface.lineTo(p.x, p.y);
        }

        if (dataSet.getEntryCount() > mostEntries) {
            // if this is not the largest set, draw a line to the center before closing
            surface.lineTo(center.x, center.y);
        }

        surface.close();

        if (dataSet.isDrawFilledEnabled()) {

            final Drawable drawable = dataSet.getFillDrawable();
            if (drawable != null) {

                drawFilledPath(c, surface, drawable);
            } else {

                drawFilledPath(c, surface, dataSet.getFillColor(), dataSet.getFillAlpha());
            }
        }

        mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
        mRenderPaint.setStyle(Paint.Style.STROKE);

        // draw the line (only if filled is disabled or alpha is below 255)
        if (!dataSet.isDrawFilledEnabled() || dataSet.getFillAlpha() < 255)
            c.drawPath(surface, mRenderPaint);
    }

    @Override
    public void drawValues(Canvas c) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        PointF center = mChart.getCenterOffsets();

        float yoffset = Utils.convertDpToPixel(5f);

        for (int i = 0; i < mChart.getData().getDataSetCount(); i++) {

            IRadarDataSet dataSet = mChart.getData().getDataSetByIndex(i);

            if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            for (int j = 0; j < dataSet.getEntryCount(); j++) {

                RadarEntry entry = dataSet.getEntryForIndex(j);

                PointF p = Utils.getPosition(
                        center,
                        (entry.getY() - mChart.getYChartMin()) * factor * phaseY,
                        sliceangle * j * phaseX + mChart.getRotationAngle());

                drawValue(c, dataSet.getValueFormatter(), entry.getY(), entry, i, p.x, p.y - yoffset, dataSet.getValueTextColor
                        (j));
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
        drawWeb(c);
    }

    protected void drawWeb(Canvas c) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();
        float rotationangle = mChart.getRotationAngle();

        PointF center = mChart.getCenterOffsets();

        // draw the web lines that come from the center
        mWebPaint.setStrokeWidth(mChart.getWebLineWidth());
        mWebPaint.setColor(mChart.getWebColor());
        mWebPaint.setAlpha(mChart.getWebAlpha());

        final int xIncrements = 1 + mChart.getSkipWebLineCount();
        int maxEntryCount = mChart.getData().getMaxEntryCountSet().getEntryCount();

        for (int i = 0; i < maxEntryCount; i += xIncrements) {

            PointF p = Utils.getPosition(
                    center,
                    mChart.getYRange() * factor,
                    sliceangle * i + rotationangle);

            c.drawLine(center.x, center.y, p.x, p.y, mWebPaint);
        }

        // draw the inner-web
        mWebPaint.setStrokeWidth(mChart.getWebLineWidthInner());
        mWebPaint.setColor(mChart.getWebColorInner());
        mWebPaint.setAlpha(mChart.getWebAlpha());

        int labelCount = mChart.getYAxis().mEntryCount;

        for (int j = 0; j < labelCount; j++) {

            for (int i = 0; i < mChart.getData().getEntryCount(); i++) {

                float r = (mChart.getYAxis().mEntries[j] - mChart.getYChartMin()) * factor;

                PointF p1 = Utils.getPosition(center, r, sliceangle * i + rotationangle);
                PointF p2 = Utils.getPosition(center, r, sliceangle * (i + 1) + rotationangle);

                c.drawLine(p1.x, p1.y, p2.x, p2.y, mWebPaint);
            }
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        PointF center = mChart.getCenterOffsets();

        RadarData radarData = mChart.getData();

        for (Highlight high : indices) {

            IRadarDataSet set = radarData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            RadarEntry e = set.getEntryForIndex((int) high.getX());

            if (!isInBoundsX(e, set))
                continue;

            float y = (e.getY() - mChart.getYChartMin());

            PointF p = Utils.getPosition(center, y * factor * mAnimator.getPhaseY(),
                    sliceangle * high.getX() * mAnimator.getPhaseX() + mChart.getRotationAngle());

            high.setDraw(p.x, p.y);

            // draw the lines
            drawHighlightLines(c, p.x, p.y, set);

            if (set.isDrawHighlightCircleEnabled()) {

                if (!Float.isNaN(p.x) && !Float.isNaN(p.y)) {

                    int strokeColor = set.getHighlightCircleStrokeColor();
                    if (strokeColor == ColorTemplate.COLOR_NONE) {
                        strokeColor = set.getColor(0);
                    }

                    if (set.getHighlightCircleStrokeAlpha() < 255) {
                        strokeColor = ColorTemplate.getColorWithAlphaComponent(strokeColor, set.getHighlightCircleStrokeAlpha());
                    }

                    drawHighlightCircle(c,
                            p,
                            set.getHighlightCircleInnerRadius(),
                            set.getHighlightCircleOuterRadius(),
                            set.getHighlightCircleFillColor(),
                            strokeColor,
                            set.getHighlightCircleStrokeWidth());
                }
            }
        }
    }

    public void drawHighlightCircle(Canvas c,
                                    PointF point,
                                    float innerRadius,
                                    float outerRadius,
                                    int fillColor,
                                    int strokeColor,
                                    float strokeWidth) {
        c.save();

        outerRadius = Utils.convertDpToPixel(outerRadius);
        innerRadius = Utils.convertDpToPixel(innerRadius);

        if (fillColor != ColorTemplate.COLOR_NONE) {
            Path p = new Path();
            p.addCircle(point.x, point.y, outerRadius, Path.Direction.CW);
            if (innerRadius > 0.f) {
                p.addCircle(point.x, point.y, innerRadius, Path.Direction.CCW);
            }
            mHighlightCirclePaint.setColor(fillColor);
            mHighlightCirclePaint.setStyle(Paint.Style.FILL);
            c.drawPath(p, mHighlightCirclePaint);
        }

        if (strokeColor != ColorTemplate.COLOR_NONE) {
            mHighlightCirclePaint.setColor(strokeColor);
            mHighlightCirclePaint.setStyle(Paint.Style.STROKE);
            mHighlightCirclePaint.setStrokeWidth(Utils.convertDpToPixel(strokeWidth));
            c.drawCircle(point.x, point.y, outerRadius, mHighlightCirclePaint);
        }

        c.restore();
    }
}
