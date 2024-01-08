package com.github.mikephil.charting.interfaces.datasets;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by philipp on 21/10/15.
 * Modified by Gurgen Khachatryan on 08/01/24.
 */
public interface IBarLineScatterCandleBubbleDataSet<T extends Entry> extends IDataSet<T> {

    /**
     * Returns the color that is used for drawing the highlight indicators.
     *
     * @return
     */
    int getHighLightColor();

    /**
     * Returns the color that is used for drawing the circle highlight indicator.
     *
     * @return
     */
    int getHighLightCircleColor();

    /**
     * Returns the color that is used for drawing the circle highlight indicator.
     *
     * @return
     */
    int getHighLightCircleBorderColor();
}
