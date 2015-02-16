
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Path;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.ScatterChart.ScatterShape;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.ScatterDataProvider;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Transformer;

import java.util.ArrayList;

public class ScatterChartRenderer extends DataRenderer {

    protected ScatterDataProvider mChart;

    public ScatterChartRenderer(ScatterDataProvider chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;
    }

    @Override
    public void drawData(Canvas c) {

        ScatterData scatterData = mChart.getScatterData();

        for (ScatterDataSet set : scatterData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(Canvas c, ScatterDataSet dataSet) {

        ArrayList<Entry> entries = dataSet.getYVals();

        float shapeHalf = dataSet.getScatterShapeSize() / 2f;

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float[] valuePoints = trans.generateTransformedValuesScatter(entries,
                mAnimator.getPhaseY());

        ScatterShape shape = dataSet.getScatterShape();

        for (int j = 0; j < valuePoints.length * mAnimator.getPhaseX(); j += 2) {

            if (!mViewPortHandler.isInBoundsRight(valuePoints[j]))
                break;

            // make sure the lines don't do shitty things outside bounds
            if (j != 0 && !mViewPortHandler.isInBoundsLeft(valuePoints[j - 1])
                    && !mViewPortHandler.isInBoundsY(valuePoints[j + 1]))
                continue;

            // Set the color for the currently drawn value. If the index is
            // out of bounds, reuse colors.
            mRenderPaint.setColor(dataSet.getColor(j));

            if (shape == ScatterShape.SQUARE) {

                c.drawRect(valuePoints[j] - shapeHalf,
                        valuePoints[j + 1] - shapeHalf, valuePoints[j]
                                + shapeHalf, valuePoints[j + 1]
                                + shapeHalf, mRenderPaint);

            } else if (shape == ScatterShape.CIRCLE) {

                c.drawCircle(valuePoints[j], valuePoints[j + 1], shapeHalf,
                        mRenderPaint);

            } else if (shape == ScatterShape.CROSS) {

                c.drawLine(valuePoints[j] - shapeHalf, valuePoints[j + 1],
                        valuePoints[j] + shapeHalf,
                        valuePoints[j + 1], mRenderPaint);
                c.drawLine(valuePoints[j], valuePoints[j + 1] - shapeHalf,
                        valuePoints[j], valuePoints[j + 1]
                                + shapeHalf, mRenderPaint);

            } else if (shape == ScatterShape.TRIANGLE) {

                // create a triangle path
                Path tri = new Path();
                tri.moveTo(valuePoints[j], valuePoints[j + 1] - shapeHalf);
                tri.lineTo(valuePoints[j] + shapeHalf, valuePoints[j + 1] + shapeHalf);
                tri.lineTo(valuePoints[j] - shapeHalf, valuePoints[j + 1] + shapeHalf);
                tri.close();

                c.drawPath(tri, mRenderPaint);

            } else if (shape == ScatterShape.CUSTOM) {

                Path customShape = dataSet.getCustomScatterShape();

                if (customShape == null)
                    return;

                // transform the provided custom path
                trans.pathValueToPixel(customShape);
                c.drawPath(customShape, mRenderPaint);
            }
        }
    }

    @Override
    public void drawValues(Canvas c) {

        // if values are drawn
        if (mChart.getScatterData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX()) {

            ArrayList<ScatterDataSet> dataSets = mChart.getScatterData().getDataSets();

            for (int i = 0; i < mChart.getScatterData().getDataSetCount(); i++) {

                ScatterDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled())
                    continue;

                ArrayList<Entry> entries = dataSet.getYVals();

                float[] positions = mChart.getTransformer(dataSet.getAxisDependency())
                        .generateTransformedValuesScatter(entries,
                                mAnimator.getPhaseY());

                float shapeSize = dataSet.getScatterShapeSize();

                for (int j = 0; j < positions.length * mAnimator.getPhaseX(); j += 2) {

                    if (!mViewPortHandler.isInBoundsRight(positions[j]))
                        break;

                    // make sure the lines don't do shitty things outside bounds
                    if (j != 0 && (!mViewPortHandler.isInBoundsLeft(positions[j])
                            || !mViewPortHandler.isInBoundsY(positions[j + 1])))
                        continue;

                    float val = entries.get(j / 2).getVal();

                    c.drawText(mChart.getValueFormatter().getFormattedValue(val), positions[j],
                            positions[j + 1] - shapeSize,
                            mValuePaint);
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

            ScatterDataSet set = mChart.getScatterData().getDataSetByIndex(indices[i]
                    .getDataSetIndex());

            if (set == null)
                continue;

            mHighlightPaint.setColor(set.getHighLightColor());

            int xIndex = indices[i].getXIndex(); // get the
                                                 // x-position

            if (xIndex > mChart.getDeltaX() * mAnimator.getPhaseX())
                continue;

            float y = set.getYValForXIndex(xIndex) * mAnimator.getPhaseY(); // get
                                                                            // the
            // y-position

            float[] pts = new float[] {
                    xIndex, mChart.getYChartMax(), xIndex, mChart.getYChartMin(), 0, y,
                    mChart.getDeltaX(), y
            };

            mChart.getTransformer(set.getAxisDependency()).pointValuesToPixel(pts);
            // draw the highlight lines
            c.drawLines(pts, mHighlightPaint);
        }
    }
}
