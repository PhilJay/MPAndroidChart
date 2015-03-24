package com.github.mikephil.charting.data;

import java.util.ArrayList;

/**
 * Data object that encapsulates all data associated with a OHLCChart.
 *
 * @author Maximilian Peitz
 */
public class OHLCDataSet extends CandleDataSet {

    public OHLCDataSet(ArrayList<CandleEntry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public DataSet<CandleEntry> copy() {

        ArrayList<CandleEntry> yVals = new ArrayList<CandleEntry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(((CandleEntry) mYVals.get(i)).copy());
        }

        OHLCDataSet copied = new OHLCDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mShadowWidth = mShadowWidth;
        copied.mBodySpace = mBodySpace;
        copied.mHighLightColor = mHighLightColor;

        return copied;
    }
}
