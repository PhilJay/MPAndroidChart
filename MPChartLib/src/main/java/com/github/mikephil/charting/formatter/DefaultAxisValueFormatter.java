package com.github.mikephil.charting.formatter;

import java.text.DecimalFormat;

/**
 * Created by philipp on 02/06/16.
 */
public class DefaultAxisValueFormatter {

    /** decimalformat for formatting */
    protected DecimalFormat mFormat;

    /**
     * Constructor that specifies to how many digits the yValue should be
     * formatted.
     *
     * @param digits
     */
    public DefaultAxisValueFormatter(int digits) {

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        mFormat = new DecimalFormat("###,###,###,##0" + b.toString());
    }
}
