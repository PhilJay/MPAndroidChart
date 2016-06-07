package com.github.mikephil.charting.test;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

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

        LineData lineData = new LineData();

        assertEquals(Float.MAX_VALUE, lineData.getYMin(), 0.01f);
        assertEquals(-Float.MAX_VALUE, lineData.getYMax(), 0.01f);

        assertEquals(Float.MAX_VALUE, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f);
        assertEquals(-Float.MAX_VALUE, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f);

        assertEquals(Float.MAX_VALUE, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f);
        assertEquals(-Float.MAX_VALUE, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f);

        assertEquals(0, lineData.getDataSetCount());

        List<Entry> lineEntries1 = new ArrayList<Entry>();
        lineEntries1.add(new Entry(10, 90));
        lineEntries1.add(new Entry(1000, 1000));

        LineDataSet lineSet1 = new LineDataSet(lineEntries1, "");

        lineData.addDataSet(lineSet1);

        assertEquals(1, lineData.getDataSetCount());
        assertEquals(2, lineSet1.getEntryCount());
        assertEquals(2, lineData.getEntryCount());

        assertEquals(10, lineData.getXMin(), 0.01f);
        assertEquals(1000f, lineData.getXMax(), 0.01f);

        assertEquals(90, lineData.getYMin(), 0.01f);
        assertEquals(1000, lineData.getYMax(), 0.01f);

        assertEquals(90, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f);
        assertEquals(1000f, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f);

        assertEquals(90, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f);
        assertEquals(1000, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f);

        List<Entry> lineEntries2 = new ArrayList<Entry>();
        lineEntries2.add(new Entry(-1000, 2000));
        lineEntries2.add(new Entry(2000, -3000));

        Entry e = new Entry(-1000, 2500);
        lineEntries2.add(e);

        LineDataSet lineSet2 = new LineDataSet(lineEntries2, "");
        lineSet2.setAxisDependency(YAxis.AxisDependency.RIGHT);

        lineData.addDataSet(lineSet2);

        assertEquals(2, lineData.getDataSetCount());
        assertEquals(3, lineSet2.getEntryCount());
        assertEquals(5, lineData.getEntryCount());

        assertEquals(-1000, lineData.getXMin(), 0.01f);
        assertEquals(2000, lineData.getXMax(), 0.01f);

        assertEquals(-3000, lineData.getYMin(), 0.01f);
        assertEquals(2500, lineData.getYMax(), 0.01f);

        assertEquals(90, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f);
        assertEquals(1000f, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f);

        assertEquals(-3000, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f);
        assertEquals(2500, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f);

        assertTrue(lineData.removeEntry(e, 1));

        assertEquals(-1000, lineData.getXMin(), 0.01f);
        assertEquals(2000, lineData.getXMax(), 0.01f);

        assertEquals(-3000, lineData.getYMin(), 0.01f);
        assertEquals(2000, lineData.getYMax(), 0.01f);

        assertEquals(90, lineData.getYMin(YAxis.AxisDependency.LEFT), 0.01f);
        assertEquals(1000f, lineData.getYMax(YAxis.AxisDependency.LEFT), 0.01f);

        assertEquals(-3000, lineData.getYMin(YAxis.AxisDependency.RIGHT), 0.01f);
        assertEquals(2000, lineData.getYMax(YAxis.AxisDependency.RIGHT), 0.01f);
    }
}
