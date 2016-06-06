package com.github.mikephil.charting.test;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created by philipp on 06/06/16.
 */
public class ChartDataTest {

    @Test
    public void testDynamicChartData() {

        List<Entry> entries1 = new ArrayList<Entry>();
        entries1.add(new Entry(10, 10));
        entries1.add(new Entry(15, -2));
        entries1.add(new Entry(21, 50));

        ScatterDataSet set1 = new ScatterDataSet(entries1, "");

        List<Entry> entries2 = new ArrayList<Entry>();
        entries2.add(new Entry(-1, 10));
        entries2.add(new Entry(10, 2));
        entries2.add(new Entry(20, 5));

        ScatterDataSet set2 = new ScatterDataSet(entries2, "");

        ScatterData data = new ScatterData(set1, set2);

        assertEquals(-2, data.getYMin(YAxis.AxisDependency.LEFT), 0.01f);
        assertEquals(50f, data.getYMax(YAxis.AxisDependency.LEFT), 0.01f);

        assertEquals(6, data.getEntryCount());

        assertEquals(-1f, data.getXMin(), 0.01f);
        assertEquals(21f, data.getXMax(), 0.01f);

        assertEquals(-2f, data.getYMin(), 0.01f);
        assertEquals(50f, data.getYMax(), 0.01f);

        assertEquals(3, data.getMaxEntryCountSet().getEntryCount());


        // now add and remove values
        data.addEntry(new Entry(-10, -10), 0);

        assertEquals(set1, data.getMaxEntryCountSet());
        assertEquals(4, data.getMaxEntryCountSet().getEntryCount());

        assertEquals(-10f, data.getYMin(YAxis.AxisDependency.LEFT), 0.01f);
        assertEquals(50f, data.getYMax(YAxis.AxisDependency.LEFT), 0.01f);

        assertEquals(-10f, data.getXMin(), 0.01f);
        assertEquals(21f, data.getXMax(), 0.01f);

        assertEquals(-10f, data.getYMin(), 0.01f);
        assertEquals(50f, data.getYMax(), 0.01f);

        data.addEntry(new Entry(-100, 100), 0);
        data.addEntry(new Entry(0, -100), 0);

        assertEquals(-100f, data.getYMin(YAxis.AxisDependency.LEFT), 0.01f);
        assertEquals(100f, data.getYMax(YAxis.AxisDependency.LEFT), 0.01f);

        // right axis will adapt left axis values if no right axis values are present
        assertEquals(-100, data.getYMin(YAxis.AxisDependency.RIGHT), 0.01f);
        assertEquals(100f, data.getYMax(YAxis.AxisDependency.RIGHT), 0.01f);

        List<Entry> entries3 = new ArrayList<Entry>();
        entries3.add(new Entry(0, 200));
        entries3.add(new Entry(0, -50));

        ScatterDataSet set3 = new ScatterDataSet(entries3, "");
        set3.setAxisDependency(YAxis.AxisDependency.RIGHT);

        data.addDataSet(set3);

        assertEquals(3, data.getDataSetCount());

        assertEquals(-100f, data.getYMin(YAxis.AxisDependency.LEFT), 0.01f);
        assertEquals(100f, data.getYMax(YAxis.AxisDependency.LEFT), 0.01f);

        assertEquals(-50f, data.getYMin(YAxis.AxisDependency.RIGHT), 0.01f);
        assertEquals(200f, data.getYMax(YAxis.AxisDependency.RIGHT), 0.01f);
    }
}
