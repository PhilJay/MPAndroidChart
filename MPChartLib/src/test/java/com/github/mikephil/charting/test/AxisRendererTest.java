package com.github.mikephil.charting.test;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.renderer.AxisRenderer;
import com.github.mikephil.charting.renderer.YAxisRenderer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created by philipp on 31/05/16.
 */
public class AxisRendererTest {


    @Test
    public void testComputeAxisValues() {

        YAxis yAxis = new YAxis();
        yAxis.setLabelCount(6);
        AxisRenderer renderer = new YAxisRenderer(null, yAxis, null);

        renderer.computeAxis(0, 100, false);
        float[] entries = yAxis.mEntries;

        assertEquals(6, entries.length);
        assertEquals(20, entries[1] - entries[0], 0.01); // interval 20
        assertEquals(0, entries[0], 0.01);
        assertEquals(100, entries[entries.length - 1], 0.01);

        yAxis = new YAxis();
        yAxis.setLabelCount(6);
        yAxis.setGranularity(50f);
        renderer = new YAxisRenderer(null, yAxis, null);

        renderer.computeAxis(0, 100, false);
        entries = yAxis.mEntries;

        assertEquals(3, entries.length);
        assertEquals(50, entries[1] - entries[0], 0.01); // interval 50
        assertEquals(0, entries[0], 0.01);
        assertEquals(100, entries[entries.length - 1], 0.01);

        yAxis = new YAxis();
        yAxis.setLabelCount(5, true);
        renderer = new YAxisRenderer(null, yAxis, null);

        renderer.computeAxis(0, 100, false);
        entries = yAxis.mEntries;

        assertEquals(5, entries.length);
        assertEquals(25, entries[1] - entries[0], 0.01); // interval 25
        assertEquals(0, entries[0], 0.01);
        assertEquals(100, entries[entries.length - 1], 0.01);

        yAxis = new YAxis();
        yAxis.setLabelCount(5, true);
        renderer = new YAxisRenderer(null, yAxis, null);

        renderer.computeAxis(0, 0.01f, false);
        entries = yAxis.mEntries;

        assertEquals(5, entries.length);
        assertEquals(0.0025, entries[1] - entries[0], 0.0001);
        assertEquals(0, entries[0], 0.0001);
        assertEquals(0.01, entries[entries.length - 1], 0.0001);

        yAxis = new YAxis();
        yAxis.setLabelCount(5, false);
        renderer = new YAxisRenderer(null, yAxis, null);

        renderer.computeAxis(0, 0.01f, false);
        entries = yAxis.mEntries;

        assertEquals(5, entries.length);
        assertEquals(0.0020, entries[1] - entries[0], 0.0001);
        assertEquals(0, entries[0], 0.0001);
        assertEquals(0.0080, entries[entries.length - 1], 0.0001);

        yAxis = new YAxis();
        yAxis.setLabelCount(6);
        renderer = new YAxisRenderer(null, yAxis, null);

        renderer.computeAxis(-50, 50, false);
        entries = yAxis.mEntries;

        assertEquals(5, entries.length);
        assertEquals(-40, entries[0], 0.0001);
        assertEquals(0, entries[2], 0.0001);
        assertEquals(40, entries[entries.length - 1], 0.0001);

        yAxis = new YAxis();
        yAxis.setLabelCount(6);
        renderer = new YAxisRenderer(null, yAxis, null);

        renderer.computeAxis(-50, 100, false);
        entries = yAxis.mEntries;

        assertEquals(5, entries.length);
        assertEquals(-30, entries[0], 0.0001);
        assertEquals(30, entries[2], 0.0001);
        assertEquals(90, entries[entries.length - 1], 0.0001);
    }
}
