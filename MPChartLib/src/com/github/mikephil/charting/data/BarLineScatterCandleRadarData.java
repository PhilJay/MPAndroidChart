
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.utils.LimitLine;

import java.util.ArrayList;

/**
 * Baseclass for all Line, Bar, Radar and ScatterData. Supports LimitLines.
 * 
 * @author Philipp Jahoda
 */
public abstract class BarLineScatterCandleRadarData<T extends BarLineScatterCandleRadarDataSet<? extends Entry>>
        extends ChartData<T> {

    /** array of limit-lines that are set for this data object */
    private ArrayList<LimitLine> mLimitLines;
    
    public BarLineScatterCandleRadarData(ArrayList<String> xVals) {
        super(xVals);
    }
    
    public BarLineScatterCandleRadarData(String[] xVals) {
        super(xVals);
    }

    public BarLineScatterCandleRadarData(ArrayList<String> xVals, ArrayList<T> sets) {
        super(xVals, sets);
    }

    public BarLineScatterCandleRadarData(String[] xVals, ArrayList<T> sets) {
        super(xVals, sets);
    }

    /**
     * Adds a new LimitLine to the data.
     * 
     * @param limitLine
     */
    public void addLimitLine(LimitLine limitLine) {
        if (mLimitLines == null)
            mLimitLines = new ArrayList<LimitLine>();
        mLimitLines.add(limitLine);
        updateMinMax();
    }

    /**
     * Adds a new array of LimitLines.
     * 
     * @param lines
     */
    public void addLimitLines(ArrayList<LimitLine> lines) {
        mLimitLines = lines;
        updateMinMax();
    }

    /**
     * Resets the limit lines array to null. Causes no more limit lines to be
     * set for this data object.
     */
    public void resetLimitLines() {
        mLimitLines = null;
        calcMinMax(mDataSets);
    }

    /**
     * Returns the LimitLine array of this data object.
     * 
     * @return
     */
    public ArrayList<LimitLine> getLimitLines() {
        return mLimitLines;
    }

    /**
     * Returns the LimitLine from the limitlines array at the specified index.
     * 
     * @param index
     * @return
     */
    public LimitLine getLimitLine(int index) {
        if (mLimitLines == null || mLimitLines.size() <= index)
            return null;
        else
            return mLimitLines.get(index);
    }

    /**
     * Updates the min and max y-value according to the set limits.
     */
    private void updateMinMax() {

        if (mLimitLines == null)
            return;

        for (int i = 0; i < mLimitLines.size(); i++) {

            LimitLine l = mLimitLines.get(i);

            if (l.getLimit() > mYMax)
                mYMax = l.getLimit();

            if (l.getLimit() < mYMin)
                mYMin = l.getLimit();
        }
    }
}
