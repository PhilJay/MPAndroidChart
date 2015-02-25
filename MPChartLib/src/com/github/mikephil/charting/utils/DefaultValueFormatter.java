
package com.github.mikephil.charting.utils;

import java.text.DecimalFormat;

/**
 * Default formatter used for formatting values. Uses a DecimalFormat with
 * pre-calculated number of digits (depending on max and min value).
 *
 * @author Philipp Jahoda
 */
public class DefaultValueFormatter implements ValueFormatter {

    /** decimalformat for formatting */
    private DecimalFormat mFormat;

    /**
     * Constructor that specifies to how many digits the value should be
     * formatted.
     * 
     * @param digits
     */
    public DefaultValueFormatter(int digits) {

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        mFormat = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    @Override
    public String getFormattedValue(float value) {
        // avoid memory allocations here (for performance)
        return mFormat.format(value);
    }
}
