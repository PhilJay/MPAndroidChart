![MPAndroidChart](design/logo/png/banner.png)

[![Release](https://img.shields.io/github/release/PhilJay/MPAndroidChart.svg?style=flat)](https://jitpack.io/#PhilJay/MPAndroidChart)
[![API](https://img.shields.io/badge/API-23%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=23)

A chart library for Android, written in Kotlin, with a Jetpack Compose module. Line, bar, horizontal bar, pie, scatter, candlestick, bubble, radar and combined charts with zooming, dragging, highlighting, markers, animations and full styling control.

[Charts](https://github.com/danielgindi/Charts) is the iOS version of this library.

## Gallery

The example app opens with every chart type in one consistent style, in a dark and a light variant, drawn by the library. The styling code is in `MPChartExample/src/main/kotlin/com/xxmassdeveloper/mpchartexample/design/`.

![Every chart type, dark](screenshots/nightfall/nightfall-dark.png)

![Every chart type, light](screenshots/nightfall/nightfall-light.png)

## Documentation

The guides at [philjay.cc](https://philjay.cc/mpandroidchart/docs/) cover the library in 35 chapters, from a first chart to theming, Compose, custom renderers and troubleshooting. Every class, function and property also has KDoc, and the generated API reference is served by JitPack for each release: [MPChartLib](https://jitpack.io/com/github/PhilJay/MPAndroidChart/MPChartLib/v4.0.0-beta01/javadoc/) and [MPChartCompose](https://jitpack.io/com/github/PhilJay/MPAndroidChart/MPChartCompose/v4.0.0-beta01/javadoc/). To build it locally run `./gradlew dokkaGenerate` and open `build/dokka/html/index.html`.

## Requirements

minSdk 23, compileSdk 35, Java 17 and Kotlin 2.0 or newer. The Compose module needs compileSdk 37, because Compose itself does. Java callers need no Kotlin at all. Coming from the Java 3.x versions? Read [MIGRATION.md](MIGRATION.md) or the longer [migration guide](https://philjay.cc/mpandroidchart/docs/migration/). [CHANGELOG.md](CHANGELOG.md) lists what changed in 4.0.

## Install

4.0 is a rewrite in Kotlin and is currently a beta. Its coordinates differ from 3.x, so a project on `com.github.PhilJay:MPAndroidChart:v3.1.0` keeps building untouched. Pin the version, do not track the newest one.

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
    }
}

// build.gradle.kts
dependencies {
    implementation("com.github.PhilJay.MPAndroidChart:MPChartLib:v4.0.0-beta01")
    implementation("com.github.PhilJay.MPAndroidChart:MPChartCompose:v4.0.0-beta01") // only for Compose
}
```

## Quick start with Views

Put a chart in a layout:

```xml
<com.github.mikephil.charting.charts.LineChart
    android:id="@+id/chart"
    android:layout_width="match_parent"
    android:layout_height="300dp" />
```

Give it data. Entries are points, a data set is one series with its styling, and the data object holds all series of a chart:

```kotlin
val entries = listOf(Entry(0f, 4f), Entry(1f, 8f), Entry(2f, 6f))
val set = LineDataSet(entries, "Sales").apply {
    color = Color.BLUE
    lineWidth = 2f
    mode = LineDataSet.Mode.CUBIC_BEZIER
    isDrawValuesEnabled = false
}
chart.data = LineData(set)
chart.animateX(500)
```

Every chart type follows the same pattern: `BarEntry` into `BarDataSet` into `BarData` for a `BarChart`, `PieEntry` into `PieDataSet` into `PieData` for a `PieChart`, and so on. Stacked bars take a list of values: `BarEntry(0f, listOf(3f, 5f))`.

Configuration is done through properties:

```kotlin
chart.apply {
    description.isEnabled = false
    legend.form = Legend.LegendForm.LINE
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.granularity = 1f
    axisRight.isEnabled = false
    axisLeft.axisMinimum = 0f
    axisLeft.addLimitLine(LimitLine(10f, "Target"))
    isDragEnabled = true
    isScaleEnabled = true
    isPinchZoomEnabled = true
}
```

Formatters and listeners are lambdas:

```kotlin
chart.xAxis.valueFormatter = IAxisValueFormatter { value, _ -> months[value.toInt()] }
set.valueFormatter = IValueFormatter { value, _, _, _ -> "%.1f".format(value) }
chart.onValueSelected { entry, highlight -> showDetails(entry) }
```

To change data later, replace it with `chart.data = LineData(newSet)`, or change the entries in place and call `chart.notifyDataSetChanged()`. One call is enough, it also redraws.

Markers show a view at the highlighted entry. Extend `MarkerView` with your own layout and assign it to `chart.marker`. Save a chart with `chart.saveToGallery("chart.png")`.

## Quick start with Compose

Every chart type has a composable with the same name. Pass the data as state, configure the view once in `setup`, and read selection and viewport back through the state:

```kotlin
val state = rememberChartState()
LaunchedEffect(lineData) { state.animateX(500) }

LineChart(
    data = lineData,
    modifier = Modifier.fillMaxWidth().height(300.dp),
    state = state,
    contentDescription = "Sales per month",
    marker = { entry, _ -> Text("${entry.y}") },
    setup = {
        description.isEnabled = false
        axisRight.isEnabled = false
    },
)
Text("Selected: ${state.selectedEntry?.y}")
Button(onClick = { state.zoomIn() }) { Text("Zoom in") }
```

- A new `data` instance is applied to the chart. After changing data in place, call `state.notifyDataChanged()`.
- `setup` runs once, so values it reads are frozen. Use `update` for anything that depends on theme or state.
- `ChartState` exposes `selectedEntry`, `lowestVisibleX`, `highestVisibleX`, `zoomX`, `zoomY` and `rotationAngle`, and offers `highlight`, `zoomIn`, `zoomOut`, `fitScreen`, `moveViewToX` and the animate functions. `rememberChartState` survives configuration changes. One state belongs to one chart.
- `setColors(List<Color>)`, `rememberTypeface(fontFamily)` and `rememberDrawable(painter, size)` turn Compose colors, fonts and painters into what the library takes. For a single color use `toArgb()`.
- Android Studio previews show a placeholder in place of the chart.

## Typed payloads

Entries can carry your own object. `Entry(1f, 2f)` has no payload, `Entry(1f, 2f, data = order)` is an `Entry<Order>`, and a `LineDataSet<Order>` built from such entries returns the payload typed:

```kotlin
val order: Order? = set.getEntryForXValue(1f)?.data
```

## Example app

The `MPChartExample` module in this repository shows every chart type and feature, starting with the showcase below, plus a Compose screen. Open the project in Android Studio and run it.

## Questions and issues

The issue tracker is for bugs and feature requests. Ask usage questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/mpandroidchart) with the `mpandroidchart` tag.

## Support the project

If this library helps you, [a donation](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=EGBENAC5XBCKS) is appreciated.

## License

Copyright 2014-2026 Philipp Jahoda

Licensed under the Apache License, Version 2.0. [LICENSE](LICENSE) holds the full text and [NOTICE](NOTICE) the attribution.

## Special thanks

- [danielgindi](https://github.com/danielgindi) - Daniel Gindi
- [mikegr](https://github.com/mikegr) - Michael Greifeneder
- [tony](https://github.com/tonypatino-monoclesociety) - Tony
- [almic](https://github.com/almic) - Mick A.
- [jitpack.io](https://github.com/jitpack-io) - JitPack.io
