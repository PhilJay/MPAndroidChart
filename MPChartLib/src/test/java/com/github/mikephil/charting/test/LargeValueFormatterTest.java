package com.github.mikephil.charting.test;

import com.github.mikephil.charting.formatter.LargeValueFormatter;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by philipp on 06/06/16.
 */
public class LargeValueFormatterTest {

    @Test
    public void test() {

        LargeValueFormatter formatter = new LargeValueFormatter();

        String result = formatter.getFormattedValue(5f);
        assertEquals("5", result);

        result = formatter.getFormattedValue(5.5f);
        assertEquals("5.5", result);

        result = formatter.getFormattedValue(50f);
        assertEquals("50", result);

        result = formatter.getFormattedValue(50.5f);
        assertEquals("50.5", result);

        result = formatter.getFormattedValue(500f);
        assertEquals("500", result);

        result = formatter.getFormattedValue(1100f);
        assertEquals("1.1k", result);

        result = formatter.getFormattedValue(10000f);
        assertEquals("10k", result);

        result = formatter.getFormattedValue(10500f);
        assertEquals("10.5k", result);

        result = formatter.getFormattedValue(100000f);
        assertEquals("100k", result);

        result = formatter.getFormattedValue(1000000f);
        assertEquals("1m", result);

        result = formatter.getFormattedValue(1500000f);
        assertEquals("1.5m", result);

        result = formatter.getFormattedValue(9500000f);
        assertEquals("9.5m", result);

        result = formatter.getFormattedValue(22200000f);
        assertEquals("22.2m", result);

        result = formatter.getFormattedValue(222000000f);
        assertEquals("222m", result);

        result = formatter.getFormattedValue(1000000000f);
        assertEquals("1b", result);

        result = formatter.getFormattedValue(9900000000f);
        assertEquals("9.9b", result);

        result = formatter.getFormattedValue(99000000000f);
        assertEquals("99b", result);

        result = formatter.getFormattedValue(99500000000f);
        assertEquals("99.5b", result);

        result = formatter.getFormattedValue(999000000000f);
        assertEquals("999b", result);

        result = formatter.getFormattedValue(1000000000000f);
        assertEquals("1t", result);

        formatter.setSuffix(new String[]{"", "k", "m", "b", "t", "q"}); // quadrillion support
        result = formatter.getFormattedValue(1000000000000000f);
        assertEquals("1q", result);

        result = formatter.getFormattedValue(1100000000000000f);
        assertEquals("1.1q", result);

        result = formatter.getFormattedValue(10000000000000000f);
        assertEquals("10q", result);

        result = formatter.getFormattedValue(13300000000000000f);
        assertEquals("13.3q", result);

        result = formatter.getFormattedValue(100000000000000000f);
        assertEquals("100q", result);
    }
}
