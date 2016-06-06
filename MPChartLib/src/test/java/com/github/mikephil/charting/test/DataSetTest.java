package com.github.mikephil.charting.test;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterDataSet;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created by philipp on 31/05/16.
 */
public class DataSetTest {


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
