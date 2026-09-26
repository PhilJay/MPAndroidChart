package com.github.mikephil.charting.test

import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class BarDataTest {

    @Test
    fun groupBars() {
        val groupSpace = 5f
        val barSpace = 1f

        val values1 = mutableListOf<BarEntry<*>>()
        val values2 = mutableListOf<BarEntry<*>>()
        for (i in 0 until 5) {
            values1.add(BarEntry(i.toFloat(), 50f))
            values2.add(BarEntry(i.toFloat(), 60f))
        }

        val data = BarData(BarDataSet(values1, "Set1"), BarDataSet(values2, "Set2"))
        data.barWidth = 10f

        assertEquals(27f, data.getGroupWidth(groupSpace, barSpace), 0.01f)
        assertEquals(0f, values1[0].x, 0.01f)
        assertEquals(1f, values1[1].x, 0.01f)

        data.groupBars(1000f, groupSpace, barSpace)

        assertEquals(1008f, values1[0].x, 0.01f)
        assertEquals(1019f, values2[0].x, 0.01f)
        assertEquals(1035f, values1[1].x, 0.01f)
        assertEquals(1046f, values2[1].x, 0.01f)

        data.groupBars(-1000f, groupSpace, barSpace)

        assertEquals(-992f, values1[0].x, 0.01f)
        assertEquals(-981f, values2[0].x, 0.01f)
        assertEquals(-965f, values1[1].x, 0.01f)
        assertEquals(-954f, values2[1].x, 0.01f)

        data.barWidth = 20f
        assertEquals(47f, data.getGroupWidth(groupSpace, barSpace), 0.01f)

        data.barWidth = 10f
        data.groupBars(-20f, groupSpace, barSpace)

        assertEquals(-12f, values1[0].x, 0.01f)
        assertEquals(-1f, values2[0].x, 0.01f)
        assertEquals(15f, values1[1].x, 0.01f)
        assertEquals(26f, values2[1].x, 0.01f)
    }
}
