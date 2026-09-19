package com.github.mikephil.charting.compose

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IDataSet

private class MarkerSlot {
    var marker: ComposeMarker? = null
}

/**
 * Hosts a chart view inside Compose; the public chart composables in Charts.kt delegate here.
 * [setup] runs once when the view is created and before any data is set, so values it reads are frozen.
 * [update] runs when [data], [state], [contentDescription], [marker] or the lambda itself change. A new [data] instance is applied to the
 * chart; the same instance is left alone, call [ChartState.notifyDataChanged] after changing it
 * in place. [marker] is wrapped in a [ComposeMarker] that is reused while the chart lives.
 * Android Studio previews show a placeholder labelled [previewLabel] because the view needs a
 * real display.
 */
@Composable
internal fun <C : Chart<D>, D : ChartData<out IDataSet<out Entry<*>>>> ChartView(
    data: D?,
    modifier: Modifier,
    state: ChartState,
    contentDescription: String?,
    marker: (@Composable (Entry<*>, Highlight) -> Unit)?,
    previewLabel: String,
    create: (Context) -> C,
    setup: C.() -> Unit,
    update: C.() -> Unit,
) {
    val semanticsModifier = if (contentDescription == null) modifier else modifier.semantics { this.contentDescription = contentDescription }
    if (LocalInspectionMode.current) {
        ChartPlaceholder(previewLabel, semanticsModifier)
        return
    }
    val markerSlot = remember { MarkerSlot() }
    DisposableEffect(state) {
        onDispose { state.detach() }
    }
    AndroidView(
        factory = { context -> create(context).apply(setup).also(state::attach) },
        modifier = semanticsModifier,
        update = { chart ->
            state.attach(chart)
            chart.contentDescription = contentDescription
            if (chart.data !== data) chart.data = data
            applyMarker(chart, marker, markerSlot)
            chart.update()
            chart.invalidate()
            state.refresh()
        },
        onRelease = { chart ->
            state.detach()
            markerSlot.marker?.detach()
            markerSlot.marker = null
        },
    )
}

private fun applyMarker(chart: Chart<*>, content: (@Composable (Entry<*>, Highlight) -> Unit)?, slot: MarkerSlot) {
    val existing = slot.marker
    when {
        content == null -> {
            existing?.detach()
            slot.marker = null
        }
        existing != null && chart.marker === existing -> existing.content = content
        else -> {
            existing?.detach()
            slot.marker = ComposeMarker(chart, content).also { chart.marker = it }
        }
    }
}

@Composable
private fun ChartPlaceholder(label: String, modifier: Modifier) {
    val dark = isSystemInDarkTheme()
    val background = if (dark) Color(0xFF2B2B2B) else Color(0xFFF5F5F5)
    val border = if (dark) Color(0xFF616161) else Color(0xFFBDBDBD)
    val text = if (dark) Color(0xFFBDBDBD) else Color(0xFF616161)
    Box(
        modifier = modifier.background(background).border(1.dp, border),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val stroke = Stroke(width = 2.dp.toPx())
            drawLine(border, Offset(0f, size.height), Offset(size.width, size.height), stroke.width)
            drawLine(border, Offset(0f, 0f), Offset(0f, size.height), stroke.width)
            val path = Path()
            val points = listOf(0.1f, 0.6f, 0.35f, 0.8f, 0.5f, 0.9f, 0.3f)
            points.forEachIndexed { index, y ->
                val x = size.width * index / (points.size - 1)
                if (index == 0) path.moveTo(x, size.height * (1f - y)) else path.lineTo(x, size.height * (1f - y))
            }
            drawPath(path, Color(0xFF64B5F6), style = stroke)
        }
        BasicText(label, style = TextStyle(color = text, fontSize = 14.sp))
    }
}
