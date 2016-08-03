package com.github.mikephil.charting.test;

import com.github.mikephil.charting.formatter.FormattedStringCache;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by Tony Patino on 6/30/16.
 */
public class FormattedStringCacheTest {

    private DecimalFormat decimalFormat;

    @Test
    public void testPrimFloat() {
        FormattedStringCache.PrimFloat cache = new FormattedStringCache.PrimFloat(decimalFormat);

        String s = null;

        s = cache.getFormattedValue(1.0f);

        Assert.assertEquals("1.00", s);

        s = cache.getFormattedValue(1.0f);

        Assert.assertEquals("1.00", s);


        s = cache.getFormattedValue(1.3f);

        Assert.assertEquals("1.30", s);


        s = cache.getFormattedValue(1.3f);

        Assert.assertEquals("1.30", s);


        s = cache.getFormattedValue(1.0f);

        Assert.assertEquals("1.00", s);

        for (int i = 0; i < 100; i++) {
            float f = 0.75f + i;
            s = cache.getFormattedValue(f);
            Assert.assertEquals(i + ".75", s);
        }


        s = cache.getFormattedValue(1.5323234f);
        Assert.assertEquals("1.53", s);


        s = cache.getFormattedValue(1.31f);
        Assert.assertEquals("1.31", s);

        s = cache.getFormattedValue(1.3111111f);
        Assert.assertEquals("1.31", s);

    }

    @Test
    public void testPrimDouble() {
        FormattedStringCache.PrimDouble cache = new FormattedStringCache.PrimDouble(decimalFormat);

        String s = null;

        s = cache.getFormattedValue(1.0d);

        Assert.assertEquals("1.00", s);

        s = cache.getFormattedValue(1.0d);

        Assert.assertEquals("1.00", s);


        s = cache.getFormattedValue(1.3d);

        Assert.assertEquals("1.30", s);


        s = cache.getFormattedValue(1.3d);

        Assert.assertEquals("1.30", s);


        s = cache.getFormattedValue(1.0d);

        Assert.assertEquals("1.00", s);

        for (int i = 0; i < 100; i++) {
            double f = 0.75f + i;
            s = cache.getFormattedValue(f);
            Assert.assertEquals(i + ".75", s);
        }


        s = cache.getFormattedValue(1.5323234d);
        Assert.assertEquals("1.53", s);


        s = cache.getFormattedValue(1.31d);
        Assert.assertEquals("1.31", s);

        s = cache.getFormattedValue(1.3111111d);
        Assert.assertEquals("1.31", s);

    }

    @Test
    public void testPrimIntFloat() {


        FormattedStringCache.PrimIntFloat cache = new FormattedStringCache.PrimIntFloat(decimalFormat);

        String s = null;

        s = cache.getFormattedValue(1.0f, 0);

        Assert.assertEquals("1.00", s);

        s = cache.getFormattedValue(1.0f, 0);

        Assert.assertEquals("1.00", s);


        s = cache.getFormattedValue(1.3f, 1);

        Assert.assertEquals("1.30", s);


        s = cache.getFormattedValue(1.3f, 1);

        Assert.assertEquals("1.30", s);


        s = cache.getFormattedValue(1.3f, 0);

        Assert.assertEquals("1.30", s);


        s = cache.getFormattedValue(1.0f, 1);

        Assert.assertEquals("1.00", s);

        for (int i = 0; i < 100; i++) {
            float f = 0.75f + i;
            s = cache.getFormattedValue(f, i);
            Assert.assertEquals(i + ".75", s);
        }


        s = cache.getFormattedValue(1.5323234f, 200);
        Assert.assertEquals("1.53", s);


        s = cache.getFormattedValue(1.31f, 300);
        Assert.assertEquals("1.31", s);

        s = cache.getFormattedValue(1.3111111f, 300);
        Assert.assertEquals("1.31", s);


        s = cache.getFormattedValue(1.3111111f, 400);
        Assert.assertEquals("1.31", s);


        s = cache.getFormattedValue(1.3111111f, 500);
        Assert.assertEquals("1.31", s);


        s = cache.getFormattedValue(1.3111111f, 5000);
        Assert.assertEquals("1.31", s);


        s = cache.getFormattedValue(1.31f, 0);
        Assert.assertEquals("1.31", s);


        s = cache.getFormattedValue(1.31f, 1);
        Assert.assertEquals("1.31", s);
    }

    @Before
    public void initializeDecimalFormatter() {
        int digits = 2;

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        decimalFormat = new DecimalFormat("###,###,###,##0" + b.toString(), otherSymbols);
    }

    @Test
    public void testGenericKV() {

        this.genericIntFloat();
        this.genericDoubleFloat();
        this.genericObjectFloat();
    }

    private void genericObjectFloat() {
        FormattedStringCache.Generic<Object, Float> cache = new FormattedStringCache.Generic<>(decimalFormat);

        String s = null;


        Object obj0 = new Object();
        Object obj1 = new Object();
        Object obj2 = new Object();

        s = cache.getFormattedValue(10f, obj0);

        Assert.assertEquals("10.00", s);


        s = cache.getFormattedValue(10f, obj0);

        Assert.assertEquals("10.00", s);


        s = cache.getFormattedValue(11f, obj1);

        Assert.assertEquals("11.00", s);

        s = cache.getFormattedValue(10f, obj2);

        Assert.assertEquals("10.00", s);


        s = cache.getFormattedValue(11f, obj0);

        Assert.assertEquals("11.00", s);


    }

    private void genericDoubleFloat() {
        FormattedStringCache.Generic<Double, Float> cache = new FormattedStringCache.Generic<>(decimalFormat);

        String s = null;

        s = cache.getFormattedValue(1.0f, 0d);

        Assert.assertEquals("1.00", s);

        s = cache.getFormattedValue(1.0f, 0d);

        Assert.assertEquals("1.00", s);


        s = cache.getFormattedValue(1.3f, 1d);

        Assert.assertEquals("1.30", s);


        s = cache.getFormattedValue(1.3f, 1d);

        Assert.assertEquals("1.30", s);


        s = cache.getFormattedValue(1.3f, 0d);

        Assert.assertEquals("1.30", s);


        s = cache.getFormattedValue(1.0f, 1d);

        Assert.assertEquals("1.00", s);

        for (int i = 0; i < 100; i++) {
            float f = 0.75f + i;
            s = cache.getFormattedValue(f, (double) i);
            Assert.assertEquals(i + ".75", s);
        }


        s = cache.getFormattedValue(1.5323234f, 200d);
        Assert.assertEquals("1.53", s);


        s = cache.getFormattedValue(1.31f, 300d);
        Assert.assertEquals("1.31", s);

        s = cache.getFormattedValue(1.3111111f, 300d);
        Assert.assertEquals("1.31", s);


        s = cache.getFormattedValue(1.3111111f, 400d);
        Assert.assertEquals("1.31", s);


        s = cache.getFormattedValue(1.3111111f, 500d);
        Assert.assertEquals("1.31", s);


        s = cache.getFormattedValue(1.3111111f, 5000d);
        Assert.assertEquals("1.31", s);


        s = cache.getFormattedValue(1.31f, 0d);
        Assert.assertEquals("1.31", s);


        s = cache.getFormattedValue(1.31f, 1d);
        Assert.assertEquals("1.31", s);

    }

    private void genericIntFloat() {
        FormattedStringCache.Generic<Integer, Float> cache = new FormattedStringCache.Generic<>(decimalFormat);

        String s = null;

        s = cache.getFormattedValue(1.0f, 0);

        Assert.assertEquals("1.00", s);

        s = cache.getFormattedValue(1.0f, 0);

        Assert.assertEquals("1.00", s);


        s = cache.getFormattedValue(1.3f, 1);

        Assert.assertEquals("1.30", s);


        s = cache.getFormattedValue(1.3f, 1);

        Assert.assertEquals("1.30", s);


        s = cache.getFormattedValue(1.3f, 0);

        Assert.assertEquals("1.30", s);


        s = cache.getFormattedValue(1.0f, 1);

        Assert.assertEquals("1.00", s);

        for (int i = 0; i < 100; i++) {
            float f = 0.75f + i;
            s = cache.getFormattedValue(f, i);
            Assert.assertEquals(i + ".75", s);
        }


        s = cache.getFormattedValue(1.5323234f, 200);
        Assert.assertEquals("1.53", s);


        s = cache.getFormattedValue(1.31f, 300);
        Assert.assertEquals("1.31", s);

        s = cache.getFormattedValue(1.3111111f, 300);
        Assert.assertEquals("1.31", s);


        s = cache.getFormattedValue(1.3111111f, 400);
        Assert.assertEquals("1.31", s);


        s = cache.getFormattedValue(1.3111111f, 500);
        Assert.assertEquals("1.31", s);


        s = cache.getFormattedValue(1.3111111f, 5000);
        Assert.assertEquals("1.31", s);


        s = cache.getFormattedValue(1.31f, 0);
        Assert.assertEquals("1.31", s);


        s = cache.getFormattedValue(1.31f, 1);
        Assert.assertEquals("1.31", s);
    }

}
