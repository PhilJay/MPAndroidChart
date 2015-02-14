
package com.github.mikephil.charting.data;

import java.util.ArrayList;

/**
 * A PieData object can only represent one DataSet. Unlike all other charts, the
 * legend labels of the PieChart are created from the x-values array, and not
 * from the DataSet labels.
 * 
 * @author Philipp Jahoda
 */
public class PieData extends ChartData<PieDataSet> {
    
    public PieData() {
        super();
    }
    
    public PieData(ArrayList<String> xVals) {
        super(xVals);
    }
    
    public PieData(String[] xVals) {
        super(xVals);
    }

    public PieData(ArrayList<String> xVals, PieDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    public PieData(String[] xVals, PieDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }
    
    private static ArrayList<PieDataSet> toArrayList(PieDataSet dataSet) {
        ArrayList<PieDataSet> sets = new ArrayList<PieDataSet>();
        sets.add(dataSet);
        return sets;
    }

    /**
     * Returns the DataSet this PieData object represents.
     * 
     * @return
     */
    public PieDataSet getDataSet() {
        return (PieDataSet) mDataSets.get(0);
    }
}
