
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;

import java.util.List;

public class BubbleData extends BarLineScatterCandleBubbleData<IBubbleDataSet> {

    protected int mMinReference;

    public BubbleData() {
        super();
    }

    public BubbleData(IBubbleDataSet... dataSets) {
        super(dataSets);
    }

    public BubbleData(List<IBubbleDataSet> dataSets) {
        super(dataSets);
    }


    /**
     * Sets the width of the circle that surrounds the bubble when highlighted
     * for all DataSet objects this data object contains, in dp.
     * 
     * @param width
     */
    public void setHighlightCircleWidth(float width) {
        for (IBubbleDataSet set : mDataSets) {
            set.setHighlightCircleWidth(width);
        }
    }

    public int getMinReference() {
        return mMinReference;
    }

    public void setMinReference(int mMinReference) {
        this.mMinReference = mMinReference;
    }
}
