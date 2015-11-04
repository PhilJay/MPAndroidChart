
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.graphics.Path;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.ScatterBuffer;
import com.github.mikephil.charting.charts.ScatterChart.ScatterShape;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.ScatterDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class ScatterChartRenderer extends LineScatterCandleRadarRenderer {

    protected ScatterDataProvider mChart;

    protected ScatterBuffer[] mScatterBuffers;

    public ScatterChartRenderer(ScatterDataProvider chart, ChartAnimator animator,
                                ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mRenderPaint.setStrokeWidth(Utils.convertDpToPixel(1f));
    }

    @Override
    public void initBuffers() {

        ScatterData scatterData = mChart.getScatterData();

        mScatterBuffers = new ScatterBuffer[scatterData.getDataSetCount()];

        for (int i = 0; i < mScatterBuffers.length; i++) {
            IScatterDataSet set = scatterData.getDataSetByIndex(i);
            mScatterBuffers[i] = new ScatterBuffer(set.getEntryCount() * 2);
        }
    }

    @Override
    public void drawData(Canvas c) {

        ScatterData scatterData = mChart.getScatterData();

        for (IScatterDataSet set : scatterData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(Canvas c, IScatterDataSet dataSet) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float shapeHalf = dataSet.getScatterShapeSize() / 2f;

        ScatterShape shape = dataSet.getScatterShape();

        ScatterBuffer buffer = mScatterBuffers[mChart.getScatterData().getIndexOfDataSet(
                dataSet)];
        buffer.setPhases(phaseX, phaseY);
        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        switch (shape) {
            case SQUARE:

                mRenderPaint.setStyle(Style.FILL);

                for (int i = 0; i < buffer.size(); i += 2) {

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[i]))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[i])
                            || !mViewPortHandler.isInBoundsY(buffer.buffer[i + 1]))
                        continue;

                    mRenderPaint.setColor(dataSet.getColor(i / 2));
                    c.drawRect(buffer.buffer[i] - shapeHalf,
                            buffer.buffer[i + 1] - shapeHalf, buffer.buffer[i]
                                    + shapeHalf, buffer.buffer[i + 1]
                                    + shapeHalf, mRenderPaint);
                }
                break;
            case CIRCLE:

                mRenderPaint.setStyle(Style.FILL);

                for (int i = 0; i < buffer.size(); i += 2) {

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[i]))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[i])
                            || !mViewPortHandler.isInBoundsY(buffer.buffer[i + 1]))
                        continue;

                    mRenderPaint.setColor(dataSet.getColor(i / 2));
                    c.drawCircle(buffer.buffer[i], buffer.buffer[i + 1], shapeHalf,
                            mRenderPaint);
                }
                break;
            case TRIANGLE:

                mRenderPaint.setStyle(Style.FILL);

                // create a triangle path
                Path tri = new Path();

                for (int i = 0; i < buffer.size(); i += 2) {

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[i]))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[i])
                            || !mViewPortHandler.isInBoundsY(buffer.buffer[i + 1]))
                        continue;

                    mRenderPaint.setColor(dataSet.getColor(i / 2));
                    tri.moveTo(buffer.buffer[i], buffer.buffer[i + 1] - shapeHalf);
                    tri.lineTo(buffer.buffer[i] + shapeHalf, buffer.buffer[i + 1] + shapeHalf);
                    tri.lineTo(buffer.buffer[i] - shapeHalf, buffer.buffer[i + 1] + shapeHalf);
                    tri.close();

                    c.drawPath(tri, mRenderPaint);
                    tri.reset();
                }
                break;
            case CROSS:

                mRenderPaint.setStyle(Style.STROKE);

                for (int i = 0; i < buffer.size(); i += 2) {

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[i]))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[i])
                            || !mViewPortHandler.isInBoundsY(buffer.buffer[i + 1]))
                        continue;

                    mRenderPaint.setColor(dataSet.getColor(i / 2));

                    c.drawLine(buffer.buffer[i] - shapeHalf, buffer.buffer[i + 1],
                            buffer.buffer[i] + shapeHalf,
                            buffer.buffer[i + 1], mRenderPaint);
                    c.drawLine(buffer.buffer[i], buffer.buffer[i + 1] - shapeHalf,
                            buffer.buffer[i], buffer.buffer[i + 1]
                                    + shapeHalf, mRenderPaint);
                }
                break;
            default:
                break;
        }

        // else { // draw the custom-shape
        //
        // Path customShape = dataSet.getCustomScatterShape();
        //
        // for (int j = 0; j < entries.size() * mAnimator.getPhaseX(); j += 2) {
        //
        // Entry e = entries.get(j / 2);
        //
        // if (!fitsBounds(e.getXIndex(), mMinX, mMaxX))
        // continue;
        //
        // if (customShape == null)
        // return;
        //
        // mRenderPaint.setColor(dataSet.getColor(j));
        //
        // Path newPath = new Path(customShape);
        // newPath.offset(e.getXIndex(), e.getVal());
        //
        // // transform the provided custom path
        // trans.pathValueToPixel(newPath);
        // c.drawPath(newPath, mRenderPaint);
        // }
        // }
    }

    @Override
    public void drawValues(Canvas c) {

        // if values are drawn
        if (mChart.getScatterData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX()) {

            List<IScatterDataSet> dataSets = mChart.getScatterData().getDataSets();

            for (int i = 0; i < mChart.getScatterData().getDataSetCount(); i++) {

                IScatterDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                float[] positions = mChart.getTransformer(dataSet.getAxisDependency())
                        .generateTransformedValuesScatter(dataSet,
                                mAnimator.getPhaseY());

                float shapeSize = dataSet.getScatterShapeSize();

                for (int j = 0; j < positions.length * mAnimator.getPhaseX(); j += 2) {

                    if (!mViewPortHandler.isInBoundsRight(positions[j]))
                        break;

                    // make sure the lines don't do shitty things outside bounds
                    if ((!mViewPortHandler.isInBoundsLeft(positions[j])
                            || !mViewPortHandler.isInBoundsY(positions[j + 1])))
                        continue;

                    Entry entry = dataSet.getEntryForIndex(j / 2);

                    drawValue(c, dataSet.getValueFormatter(), entry.getVal(), entry, i, positions[j],
                            positions[j + 1] - shapeSize);
                }
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        for (int i = 0; i < indices.length; i++) {

            IScatterDataSet set = mChart.getScatterData().getDataSetByIndex(indices[i]
                    .getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            int xIndex = indices[i].getXIndex(); // get the
            // x-position


            if (xIndex > mChart.getXChartMax() * mAnimator.getPhaseX())
                continue;

            final float yVal = set.getYValForXIndex(xIndex);
            if (yVal == Float.NaN)
                continue;

            float y = yVal * mAnimator.getPhaseY();

            float[] pts = new float[]{
                    xIndex, y
            };

            mChart.getTransformer(set.getAxisDependency()).pointValuesToPixel(pts);

            // draw the lines
            drawHighlightLines(c, pts, set);
        }
    }
}
