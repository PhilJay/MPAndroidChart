package com.github.mikephil.charting.test

import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.ViewPortHandler
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.StackedValueFormatter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale

class FormatterTest {

    @Before
    fun useUsLocale() {
        Locale.setDefault(Locale.US)
    }

    @Test
    fun defaultValueFormatterUsesRequestedDigits() {
        assertEquals("1,234", DefaultValueFormatter(0).getFormattedValue(1234.4f, Entry(0f, 0f), 0, ViewPortHandler()))
        assertEquals("1,234.40", DefaultValueFormatter(2).getFormattedValue(1234.4f, Entry(0f, 0f), 0, ViewPortHandler()))
    }

    @Test
    fun percentFormatterAppendsPercentSign() {
        assertEquals("12.3 %", PercentFormatter().getFormattedValue(12.34f, Entry(0f, 0f), 0, ViewPortHandler()))
        assertEquals("12.3 %", PercentFormatter().getFormattedValue(12.34f, XAxis()))
    }

    @Test
    fun stackedValueFormatterShowsOnlyTheTopOfAStack() {
        val stack = BarEntry(0f, listOf(1f, 2f, 3f))
        val topOnly = StackedValueFormatter(false, " kg", 1)
        assertEquals("", topOnly.getFormattedValue(1f, stack, 0, ViewPortHandler()))
        assertEquals("6.0 kg", topOnly.getFormattedValue(3f, stack, 0, ViewPortHandler()))
        val whole = StackedValueFormatter(true, "", 0)
        assertEquals("2", whole.getFormattedValue(2f, stack, 0, ViewPortHandler()))
    }

    @Test
    fun stackedValueFormatterTellsRepeatedValuesApartByPosition() {
        val stack = BarEntry(0f, listOf(1f, 2f, 2f))
        val topOnly = StackedValueFormatter(false, " kg", 1)
        assertEquals("", topOnly.getStackedFormattedValue(2f, 1, stack, 0, ViewPortHandler()))
        assertEquals("5.0 kg", topOnly.getStackedFormattedValue(2f, 2, stack, 0, ViewPortHandler()))
    }

    @Test
    fun indexAxisValueFormatterMapsWholeIndicesOnly() {
        val formatter = IndexAxisValueFormatter(listOf("Jan", "Feb", "Mar"))
        assertEquals("Feb", formatter.getFormattedValue(1f, XAxis()))
        assertEquals("", formatter.getFormattedValue(1.5f, XAxis()))
        assertEquals("", formatter.getFormattedValue(3f, XAxis()))
        assertEquals("", formatter.getFormattedValue(-1f, XAxis()))
    }
}
