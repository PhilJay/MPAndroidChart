package com.xxmassdeveloper.mpchartexample.custom;

import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by Philipp Jahoda on 14/09/15.
 */
public class MyCustomXAxisValueFormatter implements XAxisValueFormatter {

    private DecimalFormat mFormat;

    public MyCustomXAxisValueFormatter() {
        // maybe do something here or provide parameters in constructor
        mFormat = new DecimalFormat("###,###,###,##0.0");
    }

    @Override
    public String getXValue(float xValue, float xRange, float xPosition, ViewPortHandler viewPortHandler) {

        //Log.i("TRANS", "xPx: " + viewPortHandler.getTransX() + ", yPx: " + viewPortHandler.getTransY());

        // e.g. adjust the xPx-axis values depending on scale / zoom level
        if (viewPortHandler.getScaleX() > 5)
            return "4";
        else if (viewPortHandler.getScaleX() > 3)
            return "3";
        else if (viewPortHandler.getScaleX() > 1)
            return "2";
        else
            return mFormat.format(xValue);
    }

    @Override
    public int getDecimalDigits() {
        return 1;
    }
}
