package com.github.mikephil.charting.test;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterDataSet;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by philipp on 31/05/16.
 */
public class DataSetTest {

    @Test
    public void testCalcMinMax() {

        List<Entry> entries = new ArrayList<Entry>();
        entries.add(new Entry(10, 10));
        entries.add(new Entry(15, 2));
        entries.add(new Entry(21, 5));

        ScatterDataSet set = new ScatterDataSet(entries, "");

        assertEquals(10f, set.getXMin(), 0.01f);
        assertEquals(21f, set.getXMax(), 0.01f);

        assertEquals(2f, set.getYMin(), 0.01f);
        assertEquals(10f, set.getYMax(), 0.01f);

        assertEquals(3, set.getEntryCount());

        set.addEntry(new Entry(25, 1));

        assertEquals(10f, set.getXMin(), 0.01f);
        assertEquals(25f, set.getXMax(), 0.01f);

        assertEquals(1f, set.getYMin(), 0.01f);
        assertEquals(10f, set.getYMax(), 0.01f);

        assertEquals(4, set.getEntryCount());
    }

    @Test
    public void testAddRemoveEntry() {

        List<Entry> entries = new ArrayList<Entry>();
        entries.add(new Entry(10, 10));
        entries.add(new Entry(15, 2));
        entries.add(new Entry(21, 5));

        ScatterDataSet set = new ScatterDataSet(entries, "");

        assertEquals(3, set.getEntryCount());

        set.addEntryOrdered(new Entry(5, 1));

        assertEquals(4, set.getEntryCount());

        assertEquals(5, set.getXMin(), 0.01f);
        assertEquals(21, set.getXMax(), 0.01f);

        assertEquals(1f, set.getYMin(), 0.01f);
        assertEquals(10f, set.getYMax(), 0.01f);

        assertEquals(5, set.getEntryForIndex(0).getX(), 0.01f);
        assertEquals(1, set.getEntryForIndex(0).getY(), 0.01f);

        set.addEntryOrdered(new Entry(20, 50));

        assertEquals(5, set.getEntryCount());

        assertEquals(20, set.getEntryForIndex(3).getX(), 0.01f);
        assertEquals(50, set.getEntryForIndex(3).getY(), 0.01f);

        assertTrue(set.removeEntry(3));

        assertEquals(4, set.getEntryCount());

        assertEquals(21, set.getEntryForIndex(3).getX(), 0.01f);
        assertEquals(5, set.getEntryForIndex(3).getY(), 0.01f);

        assertEquals(5, set.getEntryForIndex(0).getX(), 0.01f);
        assertEquals(1, set.getEntryForIndex(0).getY(), 0.01f);

        assertTrue(set.removeFirst());

        assertEquals(3, set.getEntryCount());

        assertEquals(10, set.getEntryForIndex(0).getX(), 0.01f);
        assertEquals(10, set.getEntryForIndex(0).getY(), 0.01f);

        set.addEntryOrdered(new Entry(15, 3));

        assertEquals(4, set.getEntryCount());

        assertEquals(15, set.getEntryForIndex(1).getX(), 0.01f);
        assertEquals(3, set.getEntryForIndex(1).getY(), 0.01f);

        assertEquals(21, set.getEntryForIndex(3).getX(), 0.01f);
        assertEquals(5, set.getEntryForIndex(3).getY(), 0.01f);

        assertTrue(set.removeLast());

        assertEquals(3, set.getEntryCount());

        assertEquals(15, set.getEntryForIndex(2).getX(), 0.01f);
        assertEquals(2, set.getEntryForIndex(2).getY(), 0.01f);

        assertTrue(set.removeLast());

        assertEquals(2, set.getEntryCount());

        assertTrue(set.removeLast());

        assertEquals(1, set.getEntryCount());

        assertEquals(10, set.getEntryForIndex(0).getX(), 0.01f);
        assertEquals(10, set.getEntryForIndex(0).getY(), 0.01f);

        assertTrue(set.removeLast());

        assertEquals(0, set.getEntryCount());

        assertFalse(set.removeLast());
        assertFalse(set.removeFirst());
    }

    @Test
    public void testGetEntryForXPos() {

        List<Entry> entries = new ArrayList<Entry>();
        entries.add(new Entry(10, 10));
        entries.add(new Entry(15, 5));
        entries.add(new Entry(21, 5));

        ScatterDataSet set = new ScatterDataSet(entries, "");

        Entry closest = set.getEntryForXPos(17, DataSet.Rounding.CLOSEST);
        assertEquals(15, closest.getX(), 0.01f);
        assertEquals(5, closest.getY(), 0.01f);

        closest = set.getEntryForXPos(17, DataSet.Rounding.DOWN);
        assertEquals(15, closest.getX(), 0.01f);
        assertEquals(5, closest.getY(), 0.01f);

        closest = set.getEntryForXPos(15, DataSet.Rounding.DOWN);
        assertEquals(15, closest.getX(), 0.01f);
        assertEquals(5, closest.getY(), 0.01f);

        closest = set.getEntryForXPos(14, DataSet.Rounding.DOWN);
        assertEquals(10, closest.getX(), 0.01f);
        assertEquals(10, closest.getY(), 0.01f);

        closest = set.getEntryForXPos(17, DataSet.Rounding.UP);
        assertEquals(21, closest.getX(), 0.01f);
        assertEquals(5, closest.getY(), 0.01f);

        closest = set.getEntryForXPos(21, DataSet.Rounding.UP);
        assertEquals(21, closest.getX(), 0.01f);
        assertEquals(5, closest.getY(), 0.01f);

        closest = set.getEntryForXPos(21, DataSet.Rounding.CLOSEST);
        assertEquals(21, closest.getX(), 0.01f);
        assertEquals(5, closest.getY(), 0.01f);
    }
}
