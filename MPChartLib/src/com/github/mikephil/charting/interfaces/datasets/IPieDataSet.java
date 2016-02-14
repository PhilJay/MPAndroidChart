package com.github.mikephil.charting.interfaces.datasets;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by Philipp Jahoda on 03/11/15.
 */
public interface IPieDataSet extends IDataSet<Entry> {

    /**
     * Returns the space that is set to be between the piechart-slices of this
     * DataSet, in pixels.
     *
     * @return
     */
    float getSliceSpace();

    /**
     * Returns the distance a highlighted piechart slice is "shifted" away from
     * the chart-center in dp.
     *
     * @return
     */
    float getSelectionShift();
}

