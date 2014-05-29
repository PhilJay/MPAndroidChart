package com.github.mikephil.charting.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.text.DecimalFormat;

/**
 * utilities class that has some helper methods
 * 
 * @author Philipp Jahoda
 */
public abstract class Utils {
    
    private static Resources mRes;
    
    /**
     * initialize method, called inside the Char.init() method.
     * @param res
     */
    public static void init(Resources res) {
        mRes = res;
    }

    /**
     * format a number properly with the given number of digits
     * 
     * @param number the number to format
     * @param digits the number of digits
     * @return
     */
    public static String formatDecimal(double number, int digits) {

        StringBuffer a = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0) 
                a.append(".");
            a.append("0");
        }

        DecimalFormat nf = new DecimalFormat("###,###,###,##0" + a.toString());
        String formatted = nf.format(number);

        return formatted;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on
     * device density.
     * 
     * @param dp A value in dp (density independent pixels) unit. Which we
     *            need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on
     *         device density
     */
    public static float convertDpToPixel(float dp) {
        DisplayMetrics metrics = mRes.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent
     * pixels.
     * 
     * @param px A value in px (pixels) unit. Which we need to convert into
     *            db
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px) {
        DisplayMetrics metrics = mRes.getDisplayMetrics(); 
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    /**
     * returns the appropriate number of format digits for a delta value
     * 
     * @param delta
     * @return
     */
    public static int getFormatDigits(float delta) {

        if (delta < 0.1) {
            return 6;
        } else if (delta <= 1) {
            return 4;
        } else if (delta < 20) {
            return 2;
        } else if (delta < 100) {
            return 1;
        } else {
            return 0;
        }
    }
    
    public static int getPieFormatDigits(float delta) {
        if (delta < 0.01) {
            return 4;
        } else if (delta < 0.1) {
            return 3;
        } else if (delta < 1) {
            return 2;
        } else if (delta < 10) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * returns the appropriate number of format digits for a legend value
     * 
     * @param delta
     * @param bonus - additional digits
     * @return
     */
    public static int getLegendFormatDigits(float step, int bonus) {

        if (step < 0.0000099) {
            return 6 + bonus;
        } else if (step < 0.000099) {
            return 5 + bonus;
        } else if (step < 0.00099) {
            return 4 + bonus;
        } else if (step < 0.0099) {
            return 3 + bonus;
        } else if (step < 0.099) {
            return 2 + bonus;
        } else if (step < 0.99) {
            return 1 + bonus;
        } else {
            return 0 + bonus;
        }
    }
}