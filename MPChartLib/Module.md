# Module MPChartLib

The charts, their data classes and everything that draws them.

A chart is a `View`. You give it a data object, which holds one or more data sets, which hold entries. Everything else, the axes, the legend, the description, the formatters and the renderers, reads from those three. The nine chart types are `LineChart`, `BarChart`, `HorizontalBarChart`, `PieChart`, `ScatterChart`, `CandleStickChart`, `BubbleChart`, `RadarChart` and `CombinedChart`.

```kotlin
val set = LineDataSet(listOf(Entry(0f, 4f), Entry(1f, 8f)), "Sales")
chart.data = LineData(set)
```

Sizes are in dp unless a member says otherwise, and they are converted when drawing. Getter and setter pairs are properties, boolean flags read `isXxxEnabled`, and `chart.notifyDataSetChanged()` recalculates and redraws after a change made in place.

The guides at [philjay.cc](https://philjay.cc/mpandroidchart/docs/) explain the whole library chapter by chapter. Use this reference for the exact members, their units, their defaults and what they throw.

# Package com.github.mikephil.charting.charts

The chart views. `Chart` holds what every type shares, `BarLineChartBase` adds the x and y axes, dragging, zooming and the viewport, and `PieRadarChartBase` adds rotation. Each concrete chart adds its own data type, renderer and highlighter.

# Package com.github.mikephil.charting.data

Entries, data sets and data objects, one of each per chart type. An `Entry` carries an x, a y and an optional typed payload. A `DataSet` carries the entries of one series plus its styling. A `ChartData` carries every set of a chart and the cached value ranges.

# Package com.github.mikephil.charting.data.filter

`Approximator`, which thins a list of points with the Douglas Peucker algorithm. Nothing in the library calls it; use it yourself before handing a very large series to a chart.

# Package com.github.mikephil.charting.components

The pieces drawn around the data: the two y axes, the x axis, the legend, the description, limit lines and the markers. `ComponentBase` holds the text, typeface and offset settings they share.

# Package com.github.mikephil.charting.formatter

Turns numbers into the text a chart draws. `IValueFormatter` formats the label of an entry, `IAxisValueFormatter` the label of an axis, and `IFillFormatter` decides where a filled line stops. All three are functional interfaces, so a lambda is enough.

# Package com.github.mikephil.charting.highlight

Selection. A `Highlight` says which data set and which value are selected, and an `IHighlighter` turns a touch position into one. Each chart family has its own implementation, because a pie selects by angle and a bar by distance along one axis.

# Package com.github.mikephil.charting.renderer

The drawing layer. A `DataRenderer` draws the data, the highlight indicator, the extras and the value labels, an `AxisRenderer` draws one axis, and `LegendRenderer` draws the legend. Subclass one and assign it to the chart to change how something is drawn.

# Package com.github.mikephil.charting.renderer.scatter

The shapes a scatter chart draws. `IShapeRenderer` is a functional interface with one method, so the smallest custom drawing hook in the library is a lambda.

# Package com.github.mikephil.charting.interfaces.datasets

What the renderers read from a data set. Implement one of these instead of subclassing `DataSet` when your entries come from somewhere else, such as a domain object list or a cursor.

# Package com.github.mikephil.charting.interfaces.dataprovider

What the renderers read from a chart. A renderer is written against one of these rather than against a chart class, which is how a combined chart reuses all five data renderers unchanged.

# Package com.github.mikephil.charting.animation

`ChartAnimator` and the easing curves. The animator runs `phaseX` and `phaseY` from 0 to 1 and the renderers multiply by them, which is what makes a chart grow into place. It also moves entry values: `chart.animateValue(entry, toY, 500)` animates one entry and `chart.animateDataChange(newData, 500)` moves every entry from its old value to its new one. A new animation cancels the running one of the same kind, every animation takes an `onEnd` callback, and `stopAnimations()` or leaving the window ends them all at their final state.

# Package com.github.mikephil.charting.listener

Touch handling and callbacks: the gesture listeners of the two chart families, the value selected listener and the gesture listener your code implements.

# Package com.github.mikephil.charting.jobs

Work that waits until the chart has a size. Moving, zooming and centering on a value need the value to pixel matrices, so they run as jobs rather than immediately.

# Package com.github.mikephil.charting.buffer

Reusable float arrays that hold the coordinates of the bars while they are drawn, so a draw pass allocates nothing.

# Package com.github.mikephil.charting.utils

The shared helpers: unit conversion, number formatting, the transformer between values and pixels, the viewport handler, and the pooled point and size types.
