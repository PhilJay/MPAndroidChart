package com.xxmassdeveloper.mpchartexample.custom;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by Philipp Jahoda on 14/09/15.
 *
 * @deprecated The {@link MyAxisValueFormatter} does exactly the same thing and is more functional.
 */
@Deprecated
public class MyCustomXAxisValueFormatter extends ValueFormatter
{

    private final DecimalFormat mFormat;
    private final ViewPortHandler mViewPortHandler;

    public MyCustomXAxisValueFormatter(ViewPortHandler viewPortHandler) {
        mViewPortHandler = viewPortHandler;
        // maybe do something here or provide parameters in constructor
        mFormat = new DecimalFormat("###,###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value) {

        //Log.i("TRANS", "x: " + viewPortHandler.getTransX() + ", y: " + viewPortHandler.getTransY());

        // e.g. adjust the x-axis values depending on scale / zoom level
        final float xScale = mViewPortHandler.getScaleX();
        if (xScale > 5)
            return "4";
        else if (xScale > 3)
            return "3";
        else if (xScale > 1)
            return "2";
        else
            return mFormat.format(value);
    }
}
