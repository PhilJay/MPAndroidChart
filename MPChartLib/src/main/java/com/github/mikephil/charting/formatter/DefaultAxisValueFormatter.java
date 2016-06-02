package com.github.mikephil.charting.formatter;

import java.text.DecimalFormat;

/**
 * Created by philipp on 02/06/16.
 */
public class DefaultAxisValueFormatter implements AxisValueFormatter {

    /**
     * decimalformat for formatting
     */
    protected DecimalFormat mFormat;

    protected int digits = 0;

    /**
     * Constructor that specifies to how many digits the yValue should be
     * formatted.
     *
     * @param digits
     */
    public DefaultAxisValueFormatter(int digits) {
        this.digits = digits;

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        mFormat = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    @Override
    public int getDecimalDigits() {
        return digits;
    }
}
