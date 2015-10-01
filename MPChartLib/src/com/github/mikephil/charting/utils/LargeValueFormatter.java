
package com.github.mikephil.charting.utils;

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
public class LargeValueFormatter implements ValueFormatter {

    private static final String[] SUFFIX = new String[] {
            "", "k", "m", "b", "t"
    };
    private static final int MAX_LENGTH = 4;
    private static String[] customSUFFIX = null;
    private static boolean useCustomSuffix = false;


    private DecimalFormat mFormat;
    private String mText = "";

    public LargeValueFormatter() {
        mFormat = new DecimalFormat("###E0");
    }

    /**
     * Creates a formatter that appends a specified text to the result string
     * @param appendix a text that will be appended
     */
    public LargeValueFormatter(String appendix) {
        this();
        mText = appendix;
    }

    @Override
    public String getFormattedValue(float value) {
        return makePretty(value) + mText;
    }

    /**
     * Set custom Suffix for the language of the country
     * @param suff new suffix
     */
    public void setCustomSuffix(String[] suff) {
        if (suff.length == 5) {
            useCustomSuffix = true;
            customSUFFIX = suff;
        }
    }

    /**
     * Remove custom Suffix
     */
    public void removeCustomSuffix() {
        useCustomSuffix = false;
        customSUFFIX = null;
    }

    /**
     * Check state for custom Suffix
     * @return state
     */
    public boolean isUseCustomSuffix(){
        return useCustomSuffix;
    }

    /**
     * Formats each number properly. Special thanks to Roman Gromov
     * (https://github.com/romangromov) for this piece of code.
     */
    private String makePretty(double number) {

        String r = mFormat.format(number);

        if (useCustomSuffix) r = r.replaceAll("E[0-9]", customSUFFIX[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        else r = r.replaceAll("E[0-9]", SUFFIX[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);

        while (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) {
            r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
        }

        return r;
    }
}
