
package com.github.mikephil.charting.data;

import java.util.ArrayList;

public class RadarDataSet extends LineRadarDataSet {
    

    public RadarDataSet(ArrayList<Entry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public DataSet copy() {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(mYVals.get(i).copy());
        }

        RadarDataSet copied = new RadarDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mHighLightColor = mHighLightColor;

        return copied;
    }
  
    
}
