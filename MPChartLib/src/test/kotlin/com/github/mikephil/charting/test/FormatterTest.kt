package com.github.mikephil.charting.test

import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.ViewPortHandler
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.StackedValueFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
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
    fun defaultValueFormatterRebuildsOnlyWhenDigitsChange() {
        val formatter = object : DefaultValueFormatter(2) {
            val current get() = format
        }
        val built = formatter.current
        formatter.setup(2)
        assertSame(built, formatter.current)
        formatter.setup(3)
        assertNotSame(built, formatter.current)
        assertEquals("1.500", formatter.getFormattedValue(1.5f, Entry(0f, 0f), 0, ViewPortHandler()))
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

    @Test
    fun formattersSurviveEdgeValues() {
        val labels = mutableListOf("Jan", "Feb", "Mar")
        val index = IndexAxisValueFormatter(labels)
        labels.removeAt(2)
        assertEquals("", index.getFormattedValue(2f, XAxis()))
        assertEquals("", index.getFormattedValue(Float.NaN, XAxis()))

        val emptyStack = BarEntry(0f, emptyList())
        assertEquals("", StackedValueFormatter(false, "", 0).getFormattedValue(1f, emptyStack, 0, ViewPortHandler()))

        val large = LargeValueFormatter()
        large.suffix = emptyList()
        assertEquals("5", large.getFormattedValue(5f, XAxis()))
    }
}
