package com.github.mikephil.charting.compose

import androidx.compose.runtime.saveable.SaverScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ChartStateSaverTest {

    private val scope = SaverScope { true }

    private fun save(state: ChartState) = with(ChartState.Saver) { scope.save(state) }

    @Test
    fun restoredValuesSurviveAnotherSaveBeforeTheChartExists() {
        val withYScroll = listOf(2f, 1.5f, 3f, 90f, 1f, 4f, 20f, 0f, -1f, -1f, 3f, 12f)
        val withoutYScroll = withYScroll.take(10)

        for (saved in listOf(withYScroll, withoutYScroll)) {
            val restored = ChartState.Saver.restore(saved)!!
            assertFalse(restored.isAttached)
            assertEquals(saved, save(restored))
        }
    }

    @Test
    fun malformedSavedValuesAreIgnored() {
        val restored = ChartState.Saver.restore(listOf(2f, 1.5f))!!

        restored.highlight(1f)
        restored.zoomIn()
        assertEquals(listOf(1f, 1f, 0f, 270f, 0f, 0f, Float.NaN, 0f, -1f, -1f, Float.NaN, Float.NaN), save(restored))
    }
}
