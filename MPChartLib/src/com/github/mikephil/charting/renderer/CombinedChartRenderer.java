
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.utils.Highlight;

public class CombinedChartRenderer extends DataRenderer {

    private LineChartRenderer mLineRenderer;
    private BarChartRenderer mBarRenderer;
    private CandleStickChartRenderer mCandleRenderer;
    private ScatterChartRenderer mScatterRenderer;

    public CombinedChartRenderer(CombinedChart chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        
        if (chart.getLineData() != null)
            mLineRenderer = createLineChartRenderer(chart, animator, viewPortHandler);

        if (chart.getBarData() != null)
            mBarRenderer = createBarChartRenderer(chart, animator, viewPortHandler);

        if (chart.getScatterData() != null)
            mScatterRenderer = createScatterChartRenderer(chart, animator, viewPortHandler);

        if (chart.getCandleData() != null)
            mCandleRenderer = createCandleStickChartRenderer(chart, animator, viewPortHandler);
    }

    protected CandleStickChartRenderer createCandleStickChartRenderer(CombinedChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        return new CandleStickChartRenderer(chart, animator, viewPortHandler);
    }

    protected ScatterChartRenderer createScatterChartRenderer(CombinedChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        return new ScatterChartRenderer(chart, animator, viewPortHandler);
    }

    protected BarChartRenderer createBarChartRenderer(CombinedChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        return new BarChartRenderer(chart, animator, viewPortHandler);
    }

    protected LineChartRenderer createLineChartRenderer(CombinedChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        return new LineChartRenderer(chart, animator, viewPortHandler);
    }

  @Override
    public void initBuffers() {
        if (mBarRenderer != null)
            mBarRenderer.initBuffers();

        if (mCandleRenderer != null)
            mCandleRenderer.initBuffers();

        if (mLineRenderer != null)
            mLineRenderer.initBuffers();

        if (mScatterRenderer != null)
            mScatterRenderer.initBuffers();
    }

    @Override
    public void drawData(Canvas c) {

        if (mBarRenderer != null)
            mBarRenderer.drawData(c);

        if (mCandleRenderer != null)
            mCandleRenderer.drawData(c);

        if (mLineRenderer != null)
            mLineRenderer.drawData(c);

        if (mScatterRenderer != null)
            mScatterRenderer.drawData(c);
    }

    @Override
    public void drawValues(Canvas c) {

        if (mBarRenderer != null)
            mBarRenderer.drawValues(c);

        if (mCandleRenderer != null)
            mCandleRenderer.drawValues(c);

        if (mLineRenderer != null)
            mLineRenderer.drawValues(c);

        if (mScatterRenderer != null)
            mScatterRenderer.drawValues(c);
    }

    @Override
    public void drawExtras(Canvas c) {

        if (mBarRenderer != null)
            mBarRenderer.drawExtras(c);

        if (mCandleRenderer != null)
            mCandleRenderer.drawExtras(c);

        if (mLineRenderer != null)
            mLineRenderer.drawExtras(c);

        if (mScatterRenderer != null)
            mScatterRenderer.drawExtras(c);
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        // mBarRenderer.drawHighlighted(c, indices);
        // mCandleRenderer.drawHighlighted(c, indices);
        // mLineRenderer.drawHighlighted(c, indices);
        // mScatterRenderer.drawHighlighted(c, indices);
    }

}
