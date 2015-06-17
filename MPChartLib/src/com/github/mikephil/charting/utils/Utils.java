
package com.github.mikephil.charting.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import com.github.mikephil.charting.components.YAxis.AxisDependency;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Utilities class that has some helper methods. Needs to be initialized by
 * calling Utils.init(...) before usage. Inside the Chart.init() method, this is
 * done, if the Utils are used before that, Utils.init(...) needs to be called
 * manually.
 * 
 * @author Philipp Jahoda
 */
public abstract class Utils {

    private static DisplayMetrics mMetrics;
    private static int mMinimumFlingVelocity = 50;
    private static int mMaximumFlingVelocity = 8000;

    /**
     * initialize method, called inside the Chart.init() method.
     * 
     * @param res
     */
    @SuppressWarnings("deprecation")
    public static void init(Context context) {

        if (context == null) {
            // noinspection deprecation
            mMinimumFlingVelocity = ViewConfiguration.getMinimumFlingVelocity();
            // noinspection deprecation
            mMaximumFlingVelocity = ViewConfiguration.getMaximumFlingVelocity();

            Log.e("MPAndroidChart, Utils.init(...)", "PROVIDED CONTEXT OBJECT IS NULL");

        } else {
            ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
            mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
            mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();

            Resources res = context.getResources();
            mMetrics = res.getDisplayMetrics();
        }
    }

    /**
     * initialize method, called inside the Chart.init() method. backwards
     * compatibility - to not break existing code
     *
     * @param res
     */
    @Deprecated
    public static void init(Resources res) {

        mMetrics = res.getDisplayMetrics();

        // noinspection deprecation
        mMinimumFlingVelocity = ViewConfiguration.getMinimumFlingVelocity();
        // noinspection deprecation
        mMaximumFlingVelocity = ViewConfiguration.getMaximumFlingVelocity();
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
     * density. NEEDS UTILS TO BE INITIALIZED BEFORE USAGE.
     * 
     * @param dp A value in dp (density independent pixels) unit. Which we need
     *            to convert into pixels
     * @return A float value to represent px equivalent to dp depending on
     *         device density
     */
    public static float convertDpToPixel(float dp) {

        if (mMetrics == null) {

            Log.e("MPChartLib-Utils",
                    "Utils NOT INITIALIZED. You need to call Utils.init(...) at least once before calling Utils.convertDpToPixel(...). Otherwise conversion does not take place.");
            return dp;
            // throw new IllegalStateException(
            // "Utils NOT INITIALIZED. You need to call Utils.init(...) at least once before calling Utils.convertDpToPixel(...).");
        }

        DisplayMetrics metrics = mMetrics;
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent
     * pixels. NEEDS UTILS TO BE INITIALIZED BEFORE USAGE.
     * 
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px) {

        if (mMetrics == null) {

            Log.e("MPChartLib-Utils",
                    "Utils NOT INITIALIZED. You need to call Utils.init(...) at least once before calling Utils.convertPixelsToDp(...). Otherwise conversion does not take place.");
            return px;
            // throw new IllegalStateException(
            // "Utils NOT INITIALIZED. You need to call Utils.init(...) at least once before calling Utils.convertPixelsToDp(...).");
        }

        DisplayMetrics metrics = mMetrics;
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
        return (int) paint.measureText(demoText);
    }

    /**
     * calculates the approximate height of a text, depending on a demo text
     * avoid repeated calls (e.g. inside drawing methods)
     *
     * @param paint
     * @param demoText
     * @return
     */
    public static int calcTextHeight(Paint paint, String demoText) {

        Rect r = new Rect();
        paint.getTextBounds(demoText, 0, demoText.length(), r);
        return r.height();
    }

    public static float getLineHeight(Paint paint) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        return metrics.descent - metrics.ascent;
    }

    public static float getLineSpacing(Paint paint) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        return metrics.ascent - metrics.top + metrics.bottom;
    }

    /**
     * calculates the approximate size of a text, depending on a demo text
     * avoid repeated calls (e.g. inside drawing methods)
     *
     * @param paint
     * @param demoText
     * @return
     */
    public static FSize calcTextSize(Paint paint, String demoText) {

        Rect r = new Rect();
        paint.getTextBounds(demoText, 0, demoText.length(), r);
        return new FSize(r.width(), r.height());
    }

    // /**
    // * returns the appropriate number of format digits for a delta value
    // *
    // * @param delta
    // * @return
    // */
    // public static int getFormatDigits(float delta) {
    //
    // if (delta < 0.1) {
    // return 6;
    // } else if (delta <= 1) {
    // return 4;
    // } else if (delta < 4) {
    // return 3;
    // } else if (delta < 20) {
    // return 2;
    // } else if (delta < 60) {
    // return 1;
    // } else {
    // return 0;
    // }
    // }

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

                if (decimalPointAdded) {

                    if ((charCount - digitCount) % 4 == 0) {
                        out[ind--] = '.';
                        charCount++;
                    }

                } else {

                    if ((charCount - digitCount) % 4 == 3) {
                        out[ind--] = '.';
                        charCount++;
                    }
                }
            }
        }

        // if number around zero (between 1 and -1)
        if (zero) {
            out[ind--] = '0';
            charCount += 1;
        }

        // if the number is negative
        if (neg) {
            out[ind--] = '-';
            charCount += 1;
        }

        int start = out.length - charCount;

        // use this instead of "new String(...)" because of issue < Android 4.0
        return String.valueOf(out, start, out.length - start);
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

    /**
     * Returns the appropriate number of decimals to be used for the provided
     * number.
     * 
     * @param number
     * @return
     */
    public static int getDecimals(float number) {

        float i = roundToNextSignificant(number);
        return (int) Math.ceil(-Math.log10(i)) + 2;
    }

    /**
     * Converts the provided Integer List to an int array.
     * 
     * @param integers
     * @return
     */
    public static int[] convertIntegers(List<Integer> integers) {

        int[] ret = new int[integers.size()];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i).intValue();
        }

        return ret;
    }

    /**
     * Converts the provided String List to a String array.
     * 
     * @param labels
     * @return
     */
    public static String[] convertStrings(List<String> strings) {

        String[] ret = new String[strings.size()];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = strings.get(i);
        }

        return ret;
    }

    /**
     * Replacement for the Math.nextUp(...) method that is only available in
     * HONEYCOMB and higher. Dat's some seeeeek sheeet.
     * 
     * @param d
     * @return
     */
    public static double nextUp(double d) {
        if (d == Double.POSITIVE_INFINITY)
            return d;
        else {
            d += 0.0d;
            return Double.longBitsToDouble(Double.doubleToRawLongBits(d) +
                    ((d >= 0.0d) ? +1L : -1L));
        }
    }

    /**
     * Returns the index of the DataSet that contains the closest value on the
     * y-axis. This is needed for highlighting.
     * 
     * @param valsAtIndex all the values at a specific index
     * @return
     */
    public static int getClosestDataSetIndex(List<SelInfo> valsAtIndex, float val,
            AxisDependency axis) {

        int index = -1;
        float distance = Float.MAX_VALUE;

        for (int i = 0; i < valsAtIndex.size(); i++) {

            SelInfo sel = valsAtIndex.get(i);

            if (axis == null || sel.dataSet.getAxisDependency() == axis) {

                float cdistance = Math.abs((float) sel.val - val);
                if (cdistance < distance) {
                    index = valsAtIndex.get(i).dataSetIndex;
                    distance = cdistance;
                }
            }
        }

        // Log.i(LOG_TAG, "Closest DataSet index: " + index);

        return index;
    }

    /**
     * Returns the minimum distance from a touch-y-value (in pixels) to the
     * closest y-value (in pixels) that is displayed in the chart.
     * 
     * @param valsAtIndex
     * @param val
     * @param axis
     * @return
     */
    public static float getMinimumDistance(List<SelInfo> valsAtIndex, float val,
            AxisDependency axis) {

        float distance = Float.MAX_VALUE;

        for (int i = 0; i < valsAtIndex.size(); i++) {

            SelInfo sel = valsAtIndex.get(i);

            if (sel.dataSet.getAxisDependency() == axis) {

                float cdistance = Math.abs((float) sel.val - val);
                if (cdistance < distance) {
                    distance = cdistance;
                }
            }
        }

        return distance;
    }

    /**
     * Calculates the position around a center point, depending on the distance
     * from the center, and the angle of the position around the center.
     * 
     * @param center
     * @param dist
     * @param angle in degrees, converted to radians internally
     * @return
     */
    public static PointF getPosition(PointF center, float dist, float angle) {

        PointF p = new PointF((float) (center.x + dist * Math.cos(Math.toRadians(angle))),
                (float) (center.y + dist * Math.sin(Math.toRadians(angle))));
        return p;
    }

    public static void velocityTrackerPointerUpCleanUpIfNecessary(MotionEvent ev,
            VelocityTracker tracker) {

        // Check the dot product of current velocities.
        // If the pointer that left was opposing another velocity vector, clear.
        tracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
        final int upIndex = ev.getActionIndex();
        final int id1 = ev.getPointerId(upIndex);
        final float x1 = tracker.getXVelocity(id1);
        final float y1 = tracker.getYVelocity(id1);
        for (int i = 0, count = ev.getPointerCount(); i < count; i++) {
            if (i == upIndex)
                continue;

            final int id2 = ev.getPointerId(i);
            final float x = x1 * tracker.getXVelocity(id2);
            final float y = y1 * tracker.getYVelocity(id2);

            final float dot = x + y;
            if (dot < 0) {
                tracker.clear();
                break;
            }
        }
    }

    /**
     * Original method view.postInvalidateOnAnimation() only supportd in API >=
     * 16, This is a replica of the code from ViewCompat.
     *
     * @param view
     */
    @SuppressLint("NewApi")
    public static void postInvalidateOnAnimation(View view) {
        if (Build.VERSION.SDK_INT >= 16)
            view.postInvalidateOnAnimation();
        else
            view.postInvalidateDelayed(10);
    }

    public static int getMinimumFlingVelocity() {
        return mMinimumFlingVelocity;
    }

    public static int getMaximumFlingVelocity() {
        return mMaximumFlingVelocity;
    }

    /** returns an angle between 0.f < 360.f (not less than zero, less than 360) */
    public static float getNormalizedAngle(float angle) {
        while (angle < 0.f)
            angle += 360.f;

        return angle % 360.f;
    }
}
