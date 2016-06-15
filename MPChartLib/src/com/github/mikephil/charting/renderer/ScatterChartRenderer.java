
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.ScatterBuffer;
import com.github.mikephil.charting.charts.ScatterChart;
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

        float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));
        float phaseY = mAnimator.getPhaseY();

        final float shapeSize = Utils.convertDpToPixel(dataSet.getScatterShapeSize());
        final int shapeHoleColor = dataSet.getScatterShapeHoleColor();

        String shape = dataSet.getScatterShape();

        ScatterBuffer buffer = mScatterBuffers[mChart.getScatterData().getIndexOfDataSet(
                dataSet)];
        buffer.setPhases(phaseX, phaseY);
        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        ScatterChart.getShapeRenderer(shape)
                .renderShape(c, dataSet,mViewPortHandler, buffer, mRenderPaint, shapeHoleColor, shapeSize);


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

                float shapeSize = Utils.convertDpToPixel(dataSet.getScatterShapeSize());

                for (int j = 0; j < positions.length * mAnimator.getPhaseX(); j += 2) {

                    if (!mViewPortHandler.isInBoundsRight(positions[j]))
                        break;

                    // make sure the lines don't do shitty things outside bounds
                    if ((!mViewPortHandler.isInBoundsLeft(positions[j])
                            || !mViewPortHandler.isInBoundsY(positions[j + 1])))
                        continue;

                    Entry entry = dataSet.getEntryForIndex(j / 2);

                    drawValue(c, dataSet.getValueFormatter(), entry.getVal(), entry, i, positions[j],
                            positions[j + 1] - shapeSize, dataSet.getValueTextColor(j / 2));
                }
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        ScatterData scatterData = mChart.getScatterData();

        for (Highlight high : indices) {

            final int minDataSetIndex = high.getDataSetIndex() == -1
                    ? 0
                    : high.getDataSetIndex();
            final int maxDataSetIndex = high.getDataSetIndex() == -1
                    ? scatterData.getDataSetCount()
                    : (high.getDataSetIndex() + 1);
            if (maxDataSetIndex - minDataSetIndex < 1) continue;

            for (int dataSetIndex = minDataSetIndex;
                 dataSetIndex < maxDataSetIndex;
                 dataSetIndex++) {

                IScatterDataSet set = scatterData.getDataSetByIndex(dataSetIndex);

                if (set == null || !set.isHighlightEnabled())
                    continue;

                int xIndex = high.getXIndex(); // get the
                // x-position


                if (xIndex > mChart.getXChartMax() * mAnimator.getPhaseX())
                    continue;

                final float yVal = set.getYValForXIndex(xIndex);
                if (Float.isNaN(yVal))
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
}
