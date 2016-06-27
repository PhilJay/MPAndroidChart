package com.github.mikephil.charting.formatter;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by philipp on 02/06/16.
 */
public class DefaultAxisValueFormatter implements AxisValueFormatter {

    /**
     * decimalformat for formatting
     */
    protected DecimalFormat mFormat;

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

        mFormat = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    private ArrayList<Float> cachedValues = new ArrayList<>();
    private ArrayList<String> cachedStrings = new ArrayList<>();
    @Override
    public String getFormattedValue(float value, AxisBase axis, int entryIndex) {
        boolean hasValueAtPosition = true;
        if(cachedValues.size() <= entryIndex){
            int p = entryIndex;
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

        Float cachedValue = null;
        if(hasValueAtPosition) {
            cachedValue = cachedValues.get(entryIndex);
            hasValueAtPosition = !(cachedValue == null || cachedValue != value);
        }

        if(!hasValueAtPosition){
            cachedValues.set(entryIndex, value);
            String s =  mFormat.format(value);
            cachedStrings.set(entryIndex, s);

            Log.d("DefaultAxisValueF","no value at position " + entryIndex + " create ( " + s + " ) was ( " + cachedValue +  " )");
        }

        return cachedStrings.get(entryIndex);
    }

    @Override
    public int getDecimalDigits() {
        return digits;
    }
}
