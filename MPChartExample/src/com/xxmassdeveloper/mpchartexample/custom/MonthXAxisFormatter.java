package com.xxmassdeveloper.mpchartexample.custom;

import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by Philipp Jahoda on 14/09/15.
 */
public class MonthXAxisFormatter implements XAxisValueFormatter {

    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    public MonthXAxisFormatter() {
        // maybe do something here or provide parameters in constructor

    }

    @Override
    public String getXValue(float xValue, float xRange, float xPosition, ViewPortHandler viewPortHandler) {

        float percent = xValue / xRange;
        return mMonths[(int) (mMonths.length * percent)];
    }
}
