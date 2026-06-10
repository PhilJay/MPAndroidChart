package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.PieRadarChartBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipp on 12/06/16.
 */
public abstract class PieRadarHighlighter<T extends PieRadarChartBase> implements IHighlighter
{

    protected T mChart;

    /**
     * buffer for storing previously highlighted values
     */
    protected List<Highlight> mHighlightBuffer = new ArrayList<Highlight>();

    public PieRadarHighlighter(T chart) {
        this.mChart = chart;
    }

    @Override
    public Highlight getHighlight(float x, float y) {

        float touchDistanceToCenter = mChart.distanceToCenter(x, y);

        // ─── SAFE PROPORTIONAL BOUNDARY FOR PIE CHARTS ───
        float extendedRadius = mChart.getRadius() * 1.25f;

        if (touchDistanceToCenter > extendedRadius) {
            return null;
        }

        float angle = mChart.getAngleForPoint(x, y);

        if (mChart instanceof PieChart) {
            angle /= mChart.getAnimator().getPhaseY();
        }

        int index = mChart.getIndexForAngle(angle);

        // check if the index could be found
        if (index < 0 || index >= mChart.getData().getMaxEntryCountSet().getEntryCount()) {
            return null;
        }

        Highlight hint = getClosestHighlight(index, x, y);

        // ─── NEW RADAR CHART PROXIMITY FILTER ───
        // If it's a Radar Chart, enforce a comfortable 40dp finger-sized touch boundary
        if (!(mChart instanceof PieChart) && hint != null) {
            float density = mChart.getContext().getResources().getDisplayMetrics().density;
            float maxSelectionDistance = 40f * density;

            // Calculate absolute distance between your touch point and the actual drawn vertex
            float distanceToVertex = (float) Math.hypot(x - hint.getXPx(), y - hint.getYPx());

            // If your finger is further than 40dp from the actual point, reject the touch
            if (distanceToVertex > maxSelectionDistance) {
                return null;
            }
        }

        return hint;
    }

    /**
     * Returns the closest Highlight object of the given objects based on the touch position inside the chart.
     *
     * @param index
     * @param x
     * @param y
     * @return
     */
    protected abstract Highlight getClosestHighlight(int index, float x, float y);
}