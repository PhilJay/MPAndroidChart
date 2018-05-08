package com.github.mikephil.charting.test;

import com.github.mikephil.charting.formatter.LargeValueFormatter;

import org.junit.Test;

import java.util.Locale;

import static junit.framework.Assert.assertEquals;

/**
 * Created by philipp on 06/06/16.
 */
public class LargeValueFormatterTest {

    @Test
    public void test() {

        LargeValueFormatter formatter = new LargeValueFormatter(Locale.ENGLISH);

        String result = formatter.getFormattedValue(5f, null);
        assertEquals("5", result);

        result = formatter.getFormattedValue(5.5f, null);
        assertEquals("5.5", result);

        result = formatter.getFormattedValue(50f, null);
        assertEquals("50", result);

        result = formatter.getFormattedValue(50.5f, null);
        assertEquals("50.5", result);

        result = formatter.getFormattedValue(500f, null);
        assertEquals("500", result);

        result = formatter.getFormattedValue(1100f, null);
        assertEquals("1.1k", result);

        result = formatter.getFormattedValue(10000f, null);
        assertEquals("10k", result);

        result = formatter.getFormattedValue(10500f, null);
        assertEquals("10.5k", result);

        result = formatter.getFormattedValue(100000f, null);
        assertEquals("100k", result);

        result = formatter.getFormattedValue(1000000f, null);
        assertEquals("1m", result);

        result = formatter.getFormattedValue(1500000f, null);
        assertEquals("1.5m", result);

        result = formatter.getFormattedValue(9500000f, null);
        assertEquals("9.5m", result);

        result = formatter.getFormattedValue(22200000f, null);
        assertEquals("22.2m", result);

        result = formatter.getFormattedValue(222000000f, null);
        assertEquals("222m", result);

        result = formatter.getFormattedValue(1000000000f, null);
        assertEquals("1b", result);

        result = formatter.getFormattedValue(9900000000f, null);
        assertEquals("9.9b", result);

        result = formatter.getFormattedValue(99000000000f, null);
        assertEquals("99b", result);

        result = formatter.getFormattedValue(99500000000f, null);
        assertEquals("99.5b", result);

        result = formatter.getFormattedValue(999000000000f, null);
        assertEquals("999b", result);

        result = formatter.getFormattedValue(1000000000000f, null);
        assertEquals("1t", result);

        formatter.setSuffix(new String[]{"", "k", "m", "b", "t", "q"}); // quadrillion support
        result = formatter.getFormattedValue(1000000000000000f, null);
        assertEquals("1q", result);

        result = formatter.getFormattedValue(1100000000000000f, null);
        assertEquals("1.1q", result);

        result = formatter.getFormattedValue(10000000000000000f, null);
        assertEquals("10q", result);

        result = formatter.getFormattedValue(13300000000000000f, null);
        assertEquals("13.3q", result);

        result = formatter.getFormattedValue(100000000000000000f, null);
        assertEquals("100q", result);
    }
}
