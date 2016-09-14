
package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Predefined value-formatter that formats large numbers in a pretty way.
 * Outputs: 856 = 856; 1000 = 1k; 5821 = 5.8k; 10500 = 10k; 101800 = 102k;
 * 2000000 = 2m; 7800000 = 7.8m; 92150000 = 92m; 123200000 = 123m; 9999999 =
 * 10m; 1000000000 = 1b; Special thanks to Roman Gromov
 * (https://github.com/romangromov) for this piece of code.
 *
 * @author Philipp Jahoda
 * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
 */
public class LargeValueFormatter implements IValueFormatter, IAxisValueFormatter
{

    private static String[] SUFFIX = new String[]{
            "", "k", "m", "b", "t"
    };
    private static final int MAX_LENGTH = 5;
    private DecimalFormat mFormat;
    private String mText = "";

    public LargeValueFormatter() {
        mFormat = new DecimalFormat("###E00");
    }

    /**
     * Creates a formatter that appends a specified text to the result string
     *
     * @param appendix a text that will be appended
     */
    public LargeValueFormatter(String appendix) {
        this();
        mText = appendix;
    }

    // IValueFormatter
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return makePretty(value) + mText;
    }

    // IAxisValueFormatter
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return makePretty(value) + mText;
    }

    /**
     * Set an appendix text to be added at the end of the formatted value.
     *
     * @param appendix
     */
    public void setAppendix(String appendix) {
        this.mText = appendix;
    }

    /**
     * Set custom suffix to be appended after the values.
     * Default suffix: ["", "k", "m", "b", "t"]
     *
     * @param suff new suffix
     */
    public void setSuffix(String[] suff) {
        SUFFIX = suff;
    }

    /**
     * Formats each number properly. Special thanks to Roman Gromov
     * (https://github.com/romangromov) for this piece of code.
     */
    private String makePretty(double number) {

        String r = mFormat.format(number);

        int exponent = Integer.valueOf(r.substring(r.length() - 2));

        r = r.replaceAll("E[0-9][0-9]", "");

        // Check that we have an appropriate suffix.
        if (exponent / 3 < SUFFIX.length) {
            String suffix = SUFFIX[exponent / 3];
            // Ensure that number will fit with the suffix and won't end with a decimal point.
            while (r.length() + suffix.length() > MAX_LENGTH || r.charAt(r.length() - 1) == '.') {
                r = r.substring(0, r.length() - 1);
            }
            r += suffix;
        // If there's not an appropriate suffix, fallback to scientific notation.
        } else {
            // Assume the suffix will take up three characters.
            int numLength = MAX_LENGTH - 3;

            int decimal = r.indexOf('.');
            // If the decimal is before the character limit, append extra digits.
            if (decimal < numLength) {
                String digits = r.substring(decimal + 1, numLength + 1);
                exponent -= digits.length();
                r = r.substring(0, decimal) + digits;
            } else {
                r = r.substring(0, decimal);
            }

            while (r.length() > numLength) {
                // Remove a digit and increment the exponent.
                r = r.substring(0, r.length() - 1);
                exponent++;
            }
            r += "E" + exponent;
        }

        return r;
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
