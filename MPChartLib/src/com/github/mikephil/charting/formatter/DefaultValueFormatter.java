
package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Default formatter used for formatting values inside the chart. Uses a DecimalFormat with
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

    private ArrayList<Float> cachedValues = new ArrayList<>();
    private ArrayList<String> cachedStrings = new ArrayList<>();
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

        boolean hasValueAtdataSetIndex = true;
        if(cachedValues.size() <= dataSetIndex){
            int p = dataSetIndex;
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
            hasValueAtdataSetIndex = false;
        }

        if(hasValueAtdataSetIndex) {
            Float cachedValue = cachedValues.get(dataSetIndex);
            hasValueAtdataSetIndex = !(cachedValue == null || cachedValue != value);
        }

        if(!hasValueAtdataSetIndex){
            cachedValues.set(dataSetIndex, value);
            cachedStrings.set(dataSetIndex, mFormat.format(value));
        }

        return cachedStrings.get(dataSetIndex);
    }
}
