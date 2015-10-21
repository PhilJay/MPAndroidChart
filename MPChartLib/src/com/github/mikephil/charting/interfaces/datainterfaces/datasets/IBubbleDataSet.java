package com.github.mikephil.charting.interfaces.datainterfaces.datasets;

import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.utils.Utils;

/**
 * Created by philipp on 21/10/15.
 */
public interface IBubbleDataSet extends IBarLineScatterCandleBubbleDataSet<BubbleEntry> {

    /**
     * Sets the width of the circle that surrounds the bubble when highlighted,
     * in dp.
     *
     * @param width
     */
    void setHighlightCircleWidth(float width);
}
