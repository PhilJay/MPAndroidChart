package com.github.mikephil.charting.test;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created by philipp on 06/06/16.
 */
public class BarDataTest {

    @Test
    public void testGroupBars() {

        float groupSpace = 5f;
        float barSpace = 1f;

        List<BarEntry> values1 = new ArrayList<>();
        List<BarEntry> values2 = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
            values1.add(new BarEntry(i, 50));
            values2.add(new BarEntry(i, 60));
        }

        BarDataSet barDataSet1 = new BarDataSet(values1, "Set1");
        BarDataSet barDataSet2 = new BarDataSet(values2, "Set2");

        BarData data = new BarData(barDataSet1, barDataSet2);
        data.setBarWidth(10f);

        float groupWidth = data.getGroupWidth(groupSpace, barSpace);
        assertEquals(27f, groupWidth, 0.01f);

        assertEquals(0f, values1.get(0).getX(), 0.01f);
        assertEquals(1f, values1.get(1).getX(), 0.01f);

        data.groupBars(1000, groupSpace, barSpace);

        // 1000 + 2.5 + 0.5 + 5
        assertEquals(1008f, values1.get(0).getX(), 0.01f);
        assertEquals(1019f, values2.get(0).getX(), 0.01f);
        assertEquals(1035f, values1.get(1).getX(), 0.01f);
        assertEquals(1046f, values2.get(1).getX(), 0.01f);

        data.groupBars(-1000, groupSpace, barSpace);

        assertEquals(-992f, values1.get(0).getX(), 0.01f);
        assertEquals(-981f, values2.get(0).getX(), 0.01f);
        assertEquals(-965f, values1.get(1).getX(), 0.01f);
        assertEquals(-954f, values2.get(1).getX(), 0.01f);

        data.setBarWidth(20f);
        groupWidth = data.getGroupWidth(groupSpace, barSpace);
        assertEquals(47f, groupWidth, 0.01f);

        data.setBarWidth(10f);
        data.groupBars(-20, groupSpace, barSpace);

        assertEquals(-12f, values1.get(0).getX(), 0.01f);
        assertEquals(-1f, values2.get(0).getX(), 0.01f);
        assertEquals(15f, values1.get(1).getX(), 0.01f);
        assertEquals(26f, values2.get(1).getX(), 0.01f);
    }
}
