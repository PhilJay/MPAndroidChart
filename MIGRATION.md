# Migrating from 3.x (Java) to 4.0 (Kotlin)

Version 4.0 is a Kotlin rewrite with the same class, package and interface names. These are the changes that need edits in your code.

- Requires minSdk 23, compileSdk 35 (37 with `MPChartCompose`), Java 17 and Kotlin 2.0 or newer.
- Every getter and setter pair is a property: `chart.setDragEnabled(true)` is `chart.isDragEnabled = true`, `axis.getAxisMaximum()` is `axis.axisMaximum`. Boolean flags are named `isXxxEnabled`, so `setDrawValues(false)` is `isDrawValuesEnabled = false` and `setDrawVerticalHighlightIndicator(false)` is `isVerticalHighlightIndicatorEnabled = false`. Functions with parameters keep their names.
- Renamed: `dataSet.values` is `entries`, `highLightColor` and `highLightAlpha` are `highlightColor` and `highlightAlpha`, `getChartBitmap()` is `toBitmap()`, `setOnTouchListener(ChartTouchListener)` is `chartTouchListener`, and `isUsingSliceColorAsValueLineColor` is `isUseValueColorForLineEnabled`.
- The `ValueFormatter` base class of 3.1.0 is gone. Use `IAxisValueFormatter { value, axis -> }` for axes and `IValueFormatter { value, entry, dataSetIndex, viewPortHandler -> }` for data sets.
- `GradientColor` is gone. Bars take `barDataSet.fills = listOf(Fill(startColor, endColor))` or `barDataSet.setGradientColor(startColor, endColor)`.
- Entries carry a typed payload: `Entry(x, y)` has none, `Entry(x, y, data = order)` is an `Entry<Order>`. The payload is read-only. Stack values are `BarEntry(x, listOf(1f, 2f))` and the property is `stackValues`.
- Colors, color templates, stack labels, data set labels, draw orders, formatter values and gradient colors are lists. `set.colors = ColorTemplate.MATERIAL_COLORS` replaces `set.setColors(*ColorTemplate.MATERIAL_COLORS)`.
- `setLabelCount(count, force)` is `labelCount` plus `isForceLabelsEnabled`. Assigning `legend.entries` replaces `setCustom`.
- The empty state says "No data yet" in slate grey under an outline of the chart type, and it also shows for data without entries. Set `noDataText`, `noDataTextColor` or `isNoDataIconEnabled = false` to change it.
- `chart.notifyDataSetChanged()` refreshes the data and redraws, so `data.notifyDataChanged()` and `invalidate()` after it are no longer needed.
- `chart.getScaleX()` is `chart.zoomX`. `View.scaleX` is untouched.
- `setPaint(paint, PAINT_INFO)` and friends are the properties `infoPaint`, `descriptionPaint`, `gridBackgroundPaint`, `legendLabelPaint`, `holePaint` and `centerTextPaint`.
- Formatters and listeners are `fun interface`s and receive non-null arguments. `chart.onValueSelected { entry, highlight -> }` is the lambda form of the selection listener.
- The `highlightValue` overloads are one function with default arguments. The third positional argument is still `dataIndex`; pass `stackIndex` and `callListener` by name: `highlightValue(x, dataSetIndex, callListener = false)`.
- Markers learn their chart when assigned, `marker.chartView = chart` is no longer needed.
- `saveToGallery` writes through MediaStore and needs no storage permission from Android 10 on. `saveToPath` is gone.
- Deprecated members of 3.x were removed. Values in dp are stored as dp and converted when drawing.
- Value labels are now decided per data set from the entries in view, not from the entry count of the whole chart. Charts with several data sets that used to hide every label will draw them; set `chart.maxVisibleCount` lower if that is too dense.
- The x axis keeps its labels at least one label width apart, so a chart with long labels draws fewer of them than `labelCount` asks for. Shorten the labels, rotate them with `xAxis.labelRotationAngle`, or override `minimumInterval` on your own `XAxisRenderer` to get the old density back.
- A pie or radar chart rotates only when the drag starts on the drawn chart, not in the hole or in a corner.
- `StackedValueFormatter` now decides the top of a stack by position. A formatter of your own that needs the position can override `getStackedFormattedValue`; `getFormattedValue` still works and still sees only the value.
- `scatterShapeSize` and `scatterShapeHoleRadius` are in dp, with a default shape size of 7.5 dp. Halve a 3.x pixel value to keep its look on an xhdpi screen.
- `setCenterTextSizePixels` is gone; `centerTextSize` takes dp.
- `PieData.dataSet` is nullable: `pieData.dataSet?.sliceSpace`.
- `LargeValueFormatter.setSuffix(array)` is `suffix = listOf(...)`.
- Jetpack Compose: add `MPChartCompose` and use `LineChart(data = ...)` and the other chart composables with `rememberChartState()`.
