package com.github.mikephil.charting.data;

import java.util.ArrayList;

public class CandleDataSet extends DataSet {

    public CandleDataSet(ArrayList<CandleEntry> yVals, String label) {
        super(yVals, label);
    }
    
    @Override
    public DataSet copy() {
        
        ArrayList<CandleEntry> yVals = new ArrayList<CandleEntry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(((CandleEntry) mYVals.get(i)).copy());
        }
        
        CandleDataSet copied = new CandleDataSet(yVals, getLabel());
        copied.mColors = mColors;
        return copied;
    }

}
