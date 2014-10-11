
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
 */
public class LargeValueFormatter implements ValueFormatter {

    private static String[] SUFFIX = new String[] {
            "", "k", "m", "b", "t"
    };

    private static int MAX_LENGTH = 4;

    private DecimalFormat mFormat;

    public LargeValueFormatter() {

        mFormat = new DecimalFormat("###E0");
    }

    @Override
    public String getFormattedValue(float value) {
        return makePretty(value);
    }

    /**
     * Formats each number properly. Special thanks to Roman Gromov
     * (https://github.com/romangromov) for this piece of code.
     */
    private String makePretty(double number) {

        String r = mFormat.format(number);

        r = r.replaceAll("E[0-9]", SUFFIX[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);

        while (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) {
            r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
        }

        return r;
    }
}
