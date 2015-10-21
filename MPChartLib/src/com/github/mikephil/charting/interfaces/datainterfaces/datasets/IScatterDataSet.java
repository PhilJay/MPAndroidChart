package com.github.mikephil.charting.interfaces.datainterfaces.datasets;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by philipp on 21/10/15.
 */
public interface IScatterDataSet extends IBarLineScatterCandleBubbleDataSet<Entry> {

    /**
     * returns the currently set scatter shape size
     *
     * @return
     */
    float getScatterShapeSize();
}
