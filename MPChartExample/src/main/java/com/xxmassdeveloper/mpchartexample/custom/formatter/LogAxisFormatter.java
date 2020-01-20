package com.xxmassdeveloper.mpchartexample.custom.formatter;

import android.util.Log;

import com.github.mikephil.charting.formatter.LargeValueFormatter;

public class LogAxisFormatter extends LargeValueFormatter {

    public LogAxisFormatter() {
        super();
    }

    public float encode(float y) {
        float f = (float) Math.log(y);
        Log.i("LogAxisFormatter", String.format("encode %.4f -> %.4f", y, f));
        return f;
    }
    public float decode(float y) {
        float f = (float) Math.pow(Math.E, y);
        Log.i("LogAxisFormatter", String.format("decode %.4f -> %.4f", y, f));
        return f;
    }

    @Override
    public String getFormattedValue(float value) {
        // avoid memory allocations here (for performance)
        return super.getFormattedValue(decode(value));
    }
}
