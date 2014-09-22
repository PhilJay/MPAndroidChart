
package com.github.mikephil.charting.data;

import java.util.ArrayList;

/**
 * Baseclass for all Line, Bar and ScatterData.
 * 
 * @author Philipp Jahoda
 */
public abstract class BarLineScatterCandleData<T extends BarLineScatterCandleRadarDataSet<? extends Entry>>
        extends BarLineScatterCandleRadarData<T> {

    public BarLineScatterCandleData(ArrayList<String> xVals, ArrayList<T> sets) {
        super(xVals, sets);
    }

    public BarLineScatterCandleData(String[] xVals, ArrayList<T> sets) {
        super(xVals, sets);
    }
}
