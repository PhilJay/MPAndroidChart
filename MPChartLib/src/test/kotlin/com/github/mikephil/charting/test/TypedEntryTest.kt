package com.github.mikephil.charting.test

import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TypedEntryTest {

    private data class Order(val id: Int)

    @Test
    fun payloadTypeFlowsFromEntriesToDataSetAndBack() {
        val set = LineDataSet(listOf(Entry(0f, 1f, data = Order(7)), Entry(1f, 2f)), "orders")
        val order: Order? = set.getEntryForXValue(0f)?.data
        assertEquals(Order(7), order)
        assertNull(set.getEntryForXValue(1f)?.data)

        val data = LineData(set)
        val untyped: Any? = data.dataSets[0].getEntryForIndex(0).data
        assertEquals(Order(7), untyped)

        val copied = set.copy() as LineDataSet<Order>
        assertEquals(Order(7), copied.entries[0].data)
    }

    @Test
    fun copyKeepsConfigurationAndStackValues() {
        val set = BarDataSet(listOf(BarEntry(0f, listOf(1f, 2f)), BarEntry(1f, 4f)), "stacks").apply {
            colors = listOf(1, 2, 3)
            stackLabels = listOf("a", "b")
            isDrawValuesEnabled = false
            barCornerRadius = 7f
        }
        val copied = set.copy() as BarDataSet<Nothing>
        assertEquals(listOf(1, 2, 3), copied.colors)
        assertEquals(listOf("a", "b"), copied.stackLabels)
        assertEquals(false, copied.isDrawValuesEnabled)
        assertEquals(listOf(1f, 2f), copied.entries[0].stackValues)
        assertEquals(2, copied.stackSize)
        assertEquals(7f, copied.barCornerRadius, 0.001f)

        val line = LineDataSet(listOf(Entry(0f, 1f)), "line").apply { isDrawHighlightCircleEnabled = true; highlightCircleRadius = 6f }
        val lineCopy = line.copy() as LineDataSet<Nothing>
        assertEquals(true, lineCopy.isDrawHighlightCircleEnabled)
        assertEquals(6f, lineCopy.highlightCircleRadius, 0.001f)
    }
}
