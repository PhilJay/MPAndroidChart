package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;

import java.text.DecimalFormat;

/**
 * Created by philipp on 02/06/16.
 */
public class DefaultAxisValueFormatter implements AxisValueFormatter {

    /**
     * FormattedStringFormat for formatting and caching
     */
    protected FormattedStringCache<Float, Float> mFormattedStringCache;

    /**
     * the number of decimal digits this formatter uses
     */
    protected int digits = 0;

    /**
     * Constructor that specifies to how many digits the value should be
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

        mFormattedStringCache = new FormattedStringCache<>(new DecimalFormat("###,###,###,##0" + b.toString()));
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        // TODO: There should be a better way to do this.  Floats are not the best keys...
        return mFormattedStringCache.getFormattedString(value, value);

    }

    @Override
    public int getDecimalDigits() {
        return digits;
    }
}
