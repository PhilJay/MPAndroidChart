
package com.github.mikephil.charting.utils;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
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
     * 
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
     * This method converts dp unit to equivalent pixels, depending on device
     * density.
     * 
     * @param dp A value in dp (density independent pixels) unit. Which we need
     *            to convert into pixels
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
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px) {
        DisplayMetrics metrics = mRes.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }
    
    /**
     * calculates the approximate width of a text, depending on a demo text
     * avoid repeated calls (e.g. inside drawing methods)
     * 
     * @param paint
     * @param demoText
     * @return
     */
    public static int calcTextWidth(Paint paint, String demoText) {

        Rect r = new Rect();
        paint.getTextBounds(demoText, 0, demoText.length(), r);
        return r.width();
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

    /**
     * Math.pow(...) is very expensive, so avoid calling it and create it
     * yourself.
     */
    private static final int POW_10[] = {
            1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000
    };

    /**
     * Formats the given number to the given number of decimals, and returns the
     * number as a string, maximum 35 characters.
     * 
     * @param number
     * @param digitCount
     * @param separateTousands set this to true to separate thousands values
     * @return
     */
    public static String formatNumber(float number, int digitCount, boolean separateThousands) {

        char[] out = new char[35];

        boolean neg = false;
        if (number == 0) {
            return "0";
        }

        boolean zero = false;
        if (number < 1 && number > -1) {
            zero = true;
        }

        if (number < 0) {
            neg = true;
            number = -number;
        }

        if (digitCount > POW_10.length) {
            digitCount = POW_10.length - 1;
        }

        number *= POW_10[digitCount];
        long lval = Math.round(number);
        int ind = out.length - 1;
        int charCount = 0;
        boolean decimalPointAdded = false;

        while (lval != 0 || charCount < (digitCount + 1)) {
            int digit = (int) (lval % 10);
            lval = lval / 10;
            out[ind--] = (char) (digit + '0');
            charCount++;

            // add decimal point
            if (charCount == digitCount) {
                out[ind--] = ',';
                charCount++;
                decimalPointAdded = true;
                
            // add thousand separators
            } else if (separateThousands && lval != 0 && charCount > digitCount) {
                
                if(decimalPointAdded) {
                    
                    if((charCount - digitCount) % 4 == 0) {
                        out[ind--] = '.';
                        charCount++;
                    }
                    
                } else {
                 
                    if((charCount - digitCount) % 4 == 3) {
                        out[ind--] = '.';
                        charCount++;
                    }
                }
            }
        }

        // if number around zero (between 1 and -1)
        if (zero)
            out[ind--] = '0';

        // if the number is negative
        if (neg)
            out[ind--] = '-';

        return new String(out);
    }

    /**
     * rounds the given number to the next significant number
     * 
     * @param number
     * @return
     */
    public static float roundToNextSignificant(double number) {
        final float d = (float) Math.ceil((float) Math.log10(number < 0 ? -number : number));
        final int pw = 1 - (int) d;
        final float magnitude = (float) Math.pow(10, pw);
        final long shifted = Math.round(number * magnitude);
        return shifted / magnitude;
    }
}
