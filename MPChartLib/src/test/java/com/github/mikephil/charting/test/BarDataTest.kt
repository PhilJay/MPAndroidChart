package com.github.mikephil.charting.test

import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import org.junit.Assert
import org.junit.Test

class BarDataTest {
    @Test
    fun testGroupBars() {
        val groupSpace = 5f
        val barSpace = 1f

        val values1: MutableList<BarEntry> = ArrayList()
        val values2: MutableList<BarEntry> = ArrayList()

        for (i in 0..4) {
            values1.add(BarEntry(i.toFloat(), 50f))
            values2.add(BarEntry(i.toFloat(), 60f))
        }

        val barDataSet1 = BarDataSet(values1, "Set1")
        val barDataSet2 = BarDataSet(values2, "Set2")

        val data = BarData(barDataSet1, barDataSet2)
        data.barWidth = 10f

        var groupWidth = data.getGroupWidth(groupSpace, barSpace)
        Assert.assertEquals(27f, groupWidth, 0.01f)

        Assert.assertEquals(0f, values1[0].x, 0.01f)
        Assert.assertEquals(1f, values1[1].x, 0.01f)

        data.groupBars(1000f, groupSpace, barSpace)

        // 1000 + 2.5 + 0.5 + 5
        Assert.assertEquals(1008f, values1[0].x, 0.01f)
        Assert.assertEquals(1019f, values2[0].x, 0.01f)
        Assert.assertEquals(1035f, values1[1].x, 0.01f)
        Assert.assertEquals(1046f, values2[1].x, 0.01f)

        data.groupBars(-1000f, groupSpace, barSpace)

        Assert.assertEquals(-992f, values1[0].x, 0.01f)
        Assert.assertEquals(-981f, values2[0].x, 0.01f)
        Assert.assertEquals(-965f, values1[1].x, 0.01f)
        Assert.assertEquals(-954f, values2[1].x, 0.01f)

        data.barWidth = 20f
        groupWidth = data.getGroupWidth(groupSpace, barSpace)
        Assert.assertEquals(47f, groupWidth, 0.01f)

        data.barWidth = 10f
        data.groupBars(-20f, groupSpace, barSpace)

        Assert.assertEquals(-12f, values1[0].x, 0.01f)
        Assert.assertEquals(-1f, values2[0].x, 0.01f)
        Assert.assertEquals(15f, values1[1].x, 0.01f)
        Assert.assertEquals(26f, values2[1].x, 0.01f)
    }
}
