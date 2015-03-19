
package com.github.mikephil.charting.data;

import java.util.List;

/**
 * Baseclass for all Line, Bar and ScatterData.
 * 
 * @author Philipp Jahoda
 */
public abstract class BarLineScatterCandleData<T extends BarLineScatterCandleDataSet<? extends Entry>>
        extends ChartData<T> {
    
    public BarLineScatterCandleData() {
        super();
    }
    
    public BarLineScatterCandleData(List<String> xVals) {
        super(xVals);
    }
    
    public BarLineScatterCandleData(String[] xVals) {
        super(xVals);
    }

    public BarLineScatterCandleData(List<String> xVals, List<T> sets) {
        super(xVals, sets);
    }

    public BarLineScatterCandleData(String[] xVals, List<T> sets) {
        super(xVals, sets);
    }
}
