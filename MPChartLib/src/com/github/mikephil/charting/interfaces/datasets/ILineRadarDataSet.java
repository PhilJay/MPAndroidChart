package com.github.mikephil.charting.interfaces.datasets;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public interface ILineRadarDataSet<T extends Entry> extends ILineScatterCandleRadarDataSet<T> {

    /**
     * Returns the color that is used for filling the line surface area.
     *
     * @return
     */
    int getFillColor();

    /**
     * Returns the alpha value that is used for filling the line surface,
     * default: 85
     *
     * @return
     */
    int getFillAlpha();

    /**
     * Returns the stroke-width of the drawn line
     *
     * @return
     */
    float getLineWidth();

    /**
     * Returns true if filled drawing is enabled, false if not
     *
     * @return
     */
    boolean isDrawFilledEnabled();

    /**
     * Set to true if the DataSet should be drawn filled (surface), and not just
     * as a line, disabling this will give great performance boost! default:
     * false
     *
     * @param enabled
     */
    void setDrawFilled(boolean enabled);
}
