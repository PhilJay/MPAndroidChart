package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.interfaces.dataprovider.BubbleDataProvider;

public class BubbleHighlighter extends ChartHighlighter<BubbleDataProvider> {

    public BubbleHighlighter(BubbleDataProvider chart) {
        super(chart);
    }

    /**
     * Calculates the distance between the two given points.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    @Override
    protected float getDistance(float x1, float y1, float x2, float y2) {
        return Math.abs(y1 - y2);
    }
}
