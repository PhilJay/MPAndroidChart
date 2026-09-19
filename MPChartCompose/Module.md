# Module MPChartCompose

The Jetpack Compose wrappers around the charts.

One composable per chart type, each taking the data object the view API uses, plus a `ChartState` you can read and drive from Compose.

```kotlin
val state = rememberChartState()

LineChart(
    data = lineData,
    state = state,
    modifier = Modifier.fillMaxWidth().height(220.dp),
)
```

The composables host the real chart view, so everything in [MPChartLib](../MPChartLib/index.html) works inside their `setup` and `update` lambdas. The module adds what Compose needs on top: state instead of listeners, a composable marker, and conversions between Compose colors, fonts and painters and the integers, typefaces and drawables the charts take.

It needs `minSdk 23` and Compose enabled in your build file, and it brings `MPChartLib`, the Compose BOM and `compose-ui` with it.

# Package com.github.mikephil.charting.compose

Every public member of the module: the nine chart composables, `ChartState` and `rememberChartState`, `ComposeMarker`, and the interop helpers for colors, typefaces and painters.
