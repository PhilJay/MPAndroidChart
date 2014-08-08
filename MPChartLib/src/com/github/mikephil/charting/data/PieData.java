
package com.github.mikephil.charting.data;

import java.util.ArrayList;

public class PieData extends ChartData {

    /** the maximum shift distance across all PieDataSets this object holds */
    private float mMaxShift = 0f;

    public PieData(ArrayList<String> xVals, ArrayList<PieDataSet> dataSets) {
        super(xVals, dataSets);

        calcMaxShift(dataSets);
    }

    public PieData(String[] xVals, ArrayList<PieDataSet> dataSets) {
        super(xVals, dataSets);

        calcMaxShift(dataSets);
    }

    public PieData(ArrayList<String> xVals, PieDataSet dataSet) {
        super(xVals, toArrayList(dataSet));

        mMaxShift = dataSet.getSelectionShift();
    }

    public PieData(String[] xVals, PieDataSet dataSet) {
        super(xVals, toArrayList(dataSet));

        mMaxShift = dataSet.getSelectionShift();
    }

    /**
     * calculates the maximum shift distance across all DataSets
     * 
     * @param sets
     */
    private void calcMaxShift(ArrayList<PieDataSet> sets) {

        mMaxShift = 0;

        for (int i = 0; i < sets.size(); i++) {
            if (sets.get(i).getSelectionShift() > mMaxShift)
                mMaxShift = sets.get(i).getSelectionShift();
        }
    }

    /**
     * returns the maximum shift distance / selection distance across all
     * PieDataSets this object holds
     * 
     * @return
     */
    public float getMaxShift() {
        return mMaxShift;
    }
}
