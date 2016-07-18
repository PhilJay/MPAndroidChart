
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

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

    /**
     * Groups all BarDataSet objects this data object holds together by modifying the x-position of their entries.
     * Previously set x-positions of entries will be overwritten. Leaves space between bars and groups as specified
     * by the parameters.
     * Do not forget to call notifyDataSetChanged() on your BarChart object after calling this method.
     *
     * @param fromX      the starting point on the x-axis where the grouping should begin
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

        float interval = getGroupWidth(groupSpace, barSpace);

        for (int i = 0; i < maxEntryCount; i++) {

            float start = fromX;
            fromX += groupSpaceWidthHalf;

            IBarDataSet set;
            final int setCountJ = mDataSets.size();
            for(int j = 0 ; j < setCountJ ; j++){
                set = mDataSets.get(j);

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
            float end = fromX;
            float innerInterval = end - start;
            float diff = interval - innerInterval;

            // correct rounding errors
            if (diff > 0 || diff < 0) {
                fromX += diff;
            }
        }

        notifyDataChanged();
    }

    /**
     * In case of grouped bars, this method returns the space an individual group of bar needs on the x-axis.
     *
     * @param groupSpace
     * @param barSpace
     * @return
     */
    public float getGroupWidth(float groupSpace, float barSpace) {
        return mDataSets.size() * (mBarWidth + barSpace) + groupSpace;
    }
}
