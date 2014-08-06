package com.github.mikephil.charting.data;

import java.util.ArrayList;

public class PieDataSet extends DataSet {

    public PieDataSet(ArrayList<Entry> yVals, String label) {
        super(yVals, label);
        
    }

    @Override
    public DataSet copy() {
        
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(mYVals.get(i).copy());
        }

        PieDataSet copied = new PieDataSet(yVals, getLabel());
        return copied;
    }
}
