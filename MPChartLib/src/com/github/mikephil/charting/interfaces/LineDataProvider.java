
package com.github.mikephil.charting.interfaces;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.utils.FillFormatter;

public interface LineDataProvider extends BarLineScatterCandleDataProvider {

    public LineData getLineData();

    /**
     * Sets a custom FillFormatter to the chart that handles the position of the
     * filled-line for each DataSet. Set this to null to use the default logic.
     * 
     * @param formatter
     */
    public void setFillFormatter(FillFormatter formatter);

    /**
     * Returns the FillFormatter that handles the position of the filled-line.
     * 
     * @return
     */
    public FillFormatter getFillFormatter();

    /**
     * Returns the inset the chart should have on the x-axis. Inset of e.g. 1f
     * would mean all values of this LineChart are shifted 1 x-index to the
     * right.
     * 
     * @return
     */
    public float getXAxisInset();
}
