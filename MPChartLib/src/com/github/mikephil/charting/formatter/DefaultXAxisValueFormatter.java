package com.github.mikephil.charting.formatter;

import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;

import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 14/09/15.
 * Default formatter class for adjusting x-values before drawing them.
 * This simply returns the original value unmodified.
 */
public class DefaultXAxisValueFormatter implements XAxisValueFormatter {
    private TextPaint mTextPaint;
    public DefaultXAxisValueFormatter() {
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
        /** This will truncate long text based on the available space mentioned on xLabelRect */
        return TextUtils.ellipsize(original, mTextPaint,
                viewPortHandler.getXLabelRect().right - viewPortHandler.getXLabelRect().left,
                TextUtils.TruncateAt.MIDDLE).toString();
    }
}
