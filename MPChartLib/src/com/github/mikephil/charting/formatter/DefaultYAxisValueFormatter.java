package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.YAxis;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Philipp Jahoda on 20/09/15.
 * Default formatter used for formatting labels of the YAxis. Uses a DecimalFormat with
 * pre-calculated number of digits (depending on max and min value).
 */
public class DefaultYAxisValueFormatter implements YAxisValueFormatter {

    /** decimalformat for formatting */
    private DecimalFormat mFormat;

    /**
     * Constructor that specifies to how many digits the value should be
     * formatted.
     *
     * @param digits
     */
    public DefaultYAxisValueFormatter(int digits) {

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        mFormat = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    private ArrayList<Float> cachedValues = new ArrayList<>();
    private ArrayList<String> cachedStrings = new ArrayList<>();
    @Override
    public String getFormattedValue(float value, YAxis yAxis, int position) {
        boolean hasValueAtPosition = true;
        if(cachedValues.size() <= position){
            int p = position;
            while(p >= 0){
                if(p == 0){
                    cachedValues.add(value);
                    cachedStrings.add("");
                }else{
                    cachedValues.add(Float.NaN);
                    cachedStrings.add("");
                }
                p--;
            }
            hasValueAtPosition = false;
        }

        if(hasValueAtPosition) {
            Float cachedValue = cachedValues.get(position);
            hasValueAtPosition = !(cachedValue == null || cachedValue != value);
        }

        if(!hasValueAtPosition){
            cachedValues.set(position, value);
            cachedStrings.set(position, mFormat.format(value));
        }

        return cachedStrings.get(position);
    }
}
