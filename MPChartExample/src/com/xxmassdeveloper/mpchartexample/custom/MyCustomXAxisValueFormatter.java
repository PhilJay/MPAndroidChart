package com.xxmassdeveloper.mpchartexample.custom;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by Philipp Jahoda on 14/09/15.
 */
public class MyCustomXAxisValueFormatter implements AxisValueFormatter {

    private DecimalFormat mFormat;
    private ViewPortHandler mViewPortHandler;

    public MyCustomXAxisValueFormatter(ViewPortHandler viewPortHandler) {
        mViewPortHandler = viewPortHandler;
        // maybe do something here or provide parameters in constructor
        mFormat = new DecimalFormat("###,###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        //Log.i("TRANS", "x: " + viewPortHandler.getTransX() + ", y: " + viewPortHandler.getTransY());

        // e.g. adjust the x-axis values depending on scale / zoom level
        if (mViewPortHandler.getScaleX() > 5)
            return "4";
        else if (mViewPortHandler.getScaleX() > 3)
            return "3";
        else if (mViewPortHandler.getScaleX() > 1)
            return "2";
        else
            return mFormat.format(value);
    }

    @Override
    public int getDecimalDigits() {
        return 1;
    }
}
