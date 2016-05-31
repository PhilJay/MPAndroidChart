
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data object that represents all data for the BarChart.
 *
 * @author Philipp Jahoda
 */
public class BarData extends BarLineScatterCandleBubbleData<IBarDataSet> {

    /**
     * the width of the bars on the x-axis, in values (not pixels)
     */
    private float mBarWidth = 1f;

    // /**
    // * The maximum space (in pixels on the screen) a single bar can consume.
    // */
    // private float mMaximumBarWidth = 100f;

    public BarData() {
        super();
    }

    public BarData(IBarDataSet... dataSets) {
        super(dataSets);
    }

    public BarData(List<IBarDataSet> dataSets) {
        super(dataSets);
    }

    /**
     * Returns the space that is left out between groups of bars. Always returns
     * 0 if the BarData object only contains one DataSet (because for one
     * DataSet, there is no group-space needed).
     *
     * @return
     */
    public float getGroupSpace() {
        return 0f;
    }

    /**
     * Sets the width each bar should have on the x-axis (in values, not pixels).
     * Default 1f
     *
     * @param mBarWidth
     */
    public void setBarWidth(float mBarWidth) {
        this.mBarWidth = mBarWidth;
    }

    public float getBarWidth() {
        return mBarWidth;
    }

    //    /**
//     * Returns true if this BarData object contains grouped DataSets (more than
//     * 1 DataSet).
//     *
//     * @return
//     */
//    public boolean isGrouped() {
//        return mDataSets.size() > 1 ? true : false;
//    }

    /**
     * Groups all BarDataSet objects this data object holds together. Leaves space as specified by the parameters.
     *
     * @param fromX
     * @param groupSpace the space between groups of bars in values (not pixels) e.g. 0.8f for bar width 1f
     * @param barSpace   the space between individual bars in values (not pixels) e.g. 0.1f for bar width 1f
     */
    public void groupBars(float fromX, float groupSpace, float barSpace) {

        int setCount = mDataSets.size();
        if (setCount <= 1) {
            throw new RuntimeException("BarData needs to hold at least 2 BarDataSets to allow grouping.");
        }

        IBarDataSet max = getMaxEntryCountSet();
        int maxEntryCount = max.getEntryCount();

        float groupSpaceWidthHalf = groupSpace / 2f;
        float barSpaceHalf = barSpace / 2f;
        float barWidthHalf = mBarWidth / 2f;

        for (int i = 0; i < maxEntryCount; i++) {

            fromX += groupSpaceWidthHalf;

            for (IBarDataSet set : mDataSets) {

                fromX += barSpaceHalf;
                fromX += barWidthHalf;

                if (i < set.getEntryCount()) {

                    BarEntry entry = set.getEntryForIndex(i);

                    if (entry != null) {
                        entry.setX(fromX);
                    }
                }

                fromX += barWidthHalf;
                fromX += barSpaceHalf;
            }

            fromX += groupSpaceWidthHalf;
        }

        notifyDataChanged();
    }

    public float getIntervalWidth(float groupSpace, float barSpace) {
        return mDataSets.size() * (mBarWidth + barSpace) + groupSpace;
    }

    //
    // /**
    // * Sets the maximum width (in density pixels) a single bar in the barchart
    // * should consume.
    // *
    // * @param max
    // */
    // public void setBarWidthMaximum(float max) {
    // mMaximumBarWidth = Utils.convertDpToPixel(max);
    // }
    //
    // /**
    // * Returns the maximum width (in density pixels) a single bar in the
    // * barchart should consume.
    // *
    // * @return
    // */
    // public float getBarWidthMaximum() {
    // return mMaximumBarWidth;
    // }
}
