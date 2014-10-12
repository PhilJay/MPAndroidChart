
package com.github.mikephil.charting.utils;

import com.github.mikephil.charting.data.Entry;

/**
 * Interface that can be used to return a customized color instead of setting
 * colors via the setColor(...) method of the DataSet.
 * 
 * @author Philipp Jahoda
 */
public interface ColorFormatter {

    public int getColor(Entry e, int index);
}