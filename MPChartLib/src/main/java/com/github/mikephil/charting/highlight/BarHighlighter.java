package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.MPPointD;

/**
 * Created by Philipp Jahoda on 22/07/15.
 */
public class BarHighlighter extends ChartHighlighter<BarDataProvider> {

    public BarHighlighter(BarDataProvider chart) {
        super(chart);
    }

    @Override
    public Highlight getHighlight(float x, float y) {
        Highlight high = super.getHighlight(x, y);

        if (high == null) {
            return null;
        }

        MPPointD pos = getValsForTouch(x, y);
        BarData barData = mChart.getBarData();
        IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());

        if (set.isStacked()) {
            return getStackedHighlight(high,
                    set,
                    (float) pos.x,
                    (float) pos.y);
        }

        // ─── TRANSLATED DATA-BOUNDS FIX ───
        BarEntry entry = set.getEntryForXValue(high.getX(), high.getY());
        if (entry != null && !set.isStacked()) {

            // 1. Check Horizontal (X) Bounds: Ensure touch x is within the width of this specific bar slot
            float barWidthHalf = barData.getBarWidth() / 2f;
            float barLeft = entry.getX() - barWidthHalf;
            float barRight = entry.getX() + barWidthHalf;

            if (pos.x < barLeft || pos.x > barRight) {
                MPPointD.recycleInstance(pos);
                return null;
            }

            // 2. Check Vertical (Y) Bounds: Ensure touch y is between 0 and the bar value
            float val = entry.getY();
            if (val >= 0) {
                if (pos.y > val || pos.y < 0) {
                    MPPointD.recycleInstance(pos);
                    return null;
                }
            } else {
                if (pos.y < val || pos.y > 0) {
                    MPPointD.recycleInstance(pos);
                    return null;
                }
            }
        }
        // ─── END OF FIX ───

        MPPointD.recycleInstance(pos);
        return high;
    }

    /**
     * This method creates the Highlight object that also indicates which value of a stacked BarEntry has been
     * selected.
     *
     * @param high the Highlight to work with looking for stacked values
     * @param set
     * @param xVal
     * @param yVal
     * @return
     */
    public Highlight getStackedHighlight(Highlight high, IBarDataSet set, float xVal, float yVal) {

        BarEntry entry = set.getEntryForXValue(xVal, yVal);

        if (entry == null)
            return null;

        if (entry.getYVals() == null) {
            return high;
        } else {
            Range[] ranges = entry.getRanges();

            if (ranges.length > 0) {

                boolean isHorizontal = mChart instanceof com.github.mikephil.charting.charts.HorizontalBarChart;

                if (isHorizontal) {
                    float barWidthHalf = mChart.getBarData().getBarWidth() / 2f;
                    float barBottomEdge = entry.getX() - barWidthHalf;
                    float barTopEdge = entry.getX() + barWidthHalf;

                    // yVal is category axis for horizontal — but it's actually passed as xVal here
                    // Use xVal for the category check, yVal for the value check
                    if (xVal < barBottomEdge || xVal > barTopEdge) {
                        android.util.Log.d("HIGHLIGHT", "KILLED by gaps check");
                        return null;
                    }

                    float stackMin = ranges[0].from;
                    float stackMax = ranges[ranges.length - 1].to;


// Extra check: reject if tap is in the gap between bar end and axis


                    if (yVal < stackMin || yVal > stackMax) {
                        return null;
                    }
                } else {
                    // ─── STACK 1: VERTICAL STACKED CHART ───
                    // For Vertical charts: xVal = Dataset Column Entry Index, yVal = Value Metric

                    // 1. Gaps Check: Check if touch xVal falls within the vertical column thickness
                    float barWidthHalf = mChart.getBarData().getBarWidth() / 2f;
                    float barLeftEdge = entry.getX() - barWidthHalf;
                    float barRightEdge = entry.getX() + barWidthHalf;

                    if (xVal < barLeftEdge || xVal > barRightEdge) {
                        return null;
                    }

                    // 2. Value Bounds Check: Check if touch yVal falls within the vertical bars' combined height
                    float stackBottom = ranges[0].from;
                    float stackTop = ranges[ranges.length - 1].to;

                    if (entry.getY() >= 0) {
                        if (yVal > stackTop || yVal < 0) return null;
                    } else {
                        if (yVal < stackBottom || yVal > 0) return null;
                    }
                }

                // Route search to find the closest nested segment index based on actual orientation
                int stackIndex = getClosestStackIndex(ranges, isHorizontal ? yVal : xVal);
                android.util.Log.d("HIGHLIGHT", "yVal=" + yVal + " stackIndex=" + stackIndex + " ranges[0]=" + ranges[0].from + "-" + ranges[0].to + " ranges[1]=" + ranges[1].from + "-" + ranges[1].to);

                MPPointD pixels;
                if (isHorizontal) {
                    float highlightVal = (ranges[stackIndex].from < 0)
                            ? ranges[stackIndex].from
                            : ranges[stackIndex].to;
                    pixels = mChart.getTransformer(set.getAxisDependency()).getPixelForValues(highlightVal, high.getX());
                } else {
                    // For Vertical: entry data representation represents X-pixels, value metrics represent Y-pixels
                    pixels = mChart.getTransformer(set.getAxisDependency()).getPixelForValues(high.getX(), ranges[stackIndex].to);
                }

                Highlight stackedHigh = new Highlight(
                        entry.getX(),
                        entry.getY(),
                        (float) pixels.x,
                        (float) pixels.y,
                        high.getDataSetIndex(),
                        stackIndex,
                        high.getAxis()
                );

                MPPointD.recycleInstance(pixels);

                return stackedHigh;
            }
        }

        return null;
    }
    /**
     * Returns the index of the closest value inside the values array / ranges (stacked barchart) to the value
     * given as
     * a parameter.
     *
     * @param ranges
     * @param value
     * @return
     */
    protected int getClosestStackIndex(Range[] ranges, float value) {

        if (ranges == null || ranges.length == 0)
            return 0;

        int stackIndex = 0;

        for (Range range : ranges) {
            if (range.contains(value))
                return stackIndex;
            else
                stackIndex++;
        }

        // Fallback: find the range whose midpoint is closest to value
        int closest = 0;
        float closestDist = Float.MAX_VALUE;

        for (int i = 0; i < ranges.length; i++) {
            float mid = (ranges[i].from + ranges[i].to) / 2f;
            float dist = Math.abs(mid - value);
            if (dist < closestDist) {
                closestDist = dist;
                closest = i;
            }
        }

        return closest;
    }

//    /**
//     * Splits up the stack-values of the given bar-entry into Range objects.
//     *
//     * @param entry
//     * @return
//     */
//    protected Range[] getRanges(BarEntry entry) {
//
//        float[] values = entry.getYVals();
//
//        if (values == null || values.length == 0)
//            return new Range[0];
//
//        Range[] ranges = new Range[values.length];
//
//        float negRemain = -entry.getNegativeSum();
//        float posRemain = 0f;
//
//        for (int i = 0; i < ranges.length; i++) {
//
//            float value = values[i];
//
//            if (value < 0) {
//                ranges[i] = new Range(negRemain, negRemain + Math.abs(value));
//                negRemain += Math.abs(value);
//            } else {
//                ranges[i] = new Range(posRemain, posRemain + value);
//                posRemain += value;
//            }
//        }
//
//        return ranges;
//    }

    @Override
    protected float getDistance(float x1, float y1, float x2, float y2) {
        return Math.abs(x1 - x2);
    }

    @Override
    protected BarLineScatterCandleBubbleData getData() {
        return mChart.getBarData();
    }
}
