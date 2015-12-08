
package com.github.mikephil.charting.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A PieData object can only represent one DataSet. Unlike all other charts, the
 * legend labels of the PieChart are created from the x-values array, and not
 * from the DataSet labels. Each PieData object can only represent one
 * PieDataSet (multiple PieDataSets inside a single PieChart are not possible).
 * 
 * @author Philipp Jahoda
 */
public class PieData extends ChartData<PieDataSet> {

    public PieData() {
        super();
    }

    public PieData(List<String> xVals) {
        super(xVals);
    }

    public PieData(String[] xVals) {
        super(xVals);
    }

    public PieData(List<String> xVals, PieDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public PieData(String[] xVals, PieDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<PieDataSet> toList(PieDataSet dataSet) {
        List<PieDataSet> sets = new ArrayList<PieDataSet>();
        sets.add(dataSet);
        return sets;
    }

    /**
     * Sets the PieDataSet this data object should represent.
     * 
     * @param dataSet
     */
    public void setDataSet(PieDataSet dataSet) {
        mDataSets.clear();
        mDataSets.add(dataSet);
        init();
    }

    /**
     * Returns the DataSet this PieData object represents. A PieData object can
     * only contain one DataSet.
     * 
     * @return
     */
    public PieDataSet getDataSet() {
        return mDataSets.get(0);
    }

    @Override
    public PieDataSet getDataSetByIndex(int index) {
        return index == 0 ? getDataSet() : null;
    }

    @Override
    public PieDataSet getDataSetByLabel(String label, boolean ignorecase) {
        return ignorecase ? label.equalsIgnoreCase(mDataSets.get(0).getLabel()) ? mDataSets.get(0)
                : null : label.equals(mDataSets.get(0).getLabel()) ? mDataSets.get(0) : null;
    }
}
