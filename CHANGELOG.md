# Changelog

## 4.0.1

### Added

- `YAxis.labelRotationAngle` rotates the y axis labels, like `XAxis.labelRotationAngle` does for the x axis. The chart reserves the width the rotated labels need.
- `PieChart.isRoundedSlicesReversed` rounds the slice ends the other way around.
- `PieChart.isTransparentCircleClippedToSlices` draws the translucent ring around the hole only over the slices, so the space between slices stays open.
- Pie entry labels and values that contain a line break are drawn as several lines.
- `chart.toBitmap(width, height)` draws the chart at any size, also a chart built only in code that was never laid out. A chart on screen is laid out at that size for the drawing and put back afterwards.

### Fixed

- A tap inside a bar of a combined chart selected a nearby line point instead of the bar.
- In a bar chart whose grouped data sets use different y axes, a tap often selected the bar of the other axis.
- Limit line labels at the edge of the content area were cut off. Labels of limit lines outside the visible area are no longer drawn.
- Auto scaling skipped a y axis that was disabled, even when data sets depend on it.
- Building a chart on a thread without a Looper, for example with an asynchronous layout inflater, crashed. The gesture detector is now created on the first touch.
- The default value formatter rebuilt its number format on every data change, even when the number of decimals stayed the same.

## 4.0.0

A rewrite of the library in Kotlin. Same class, package and interface names, so imports survive; the call sites change. [MIGRATION.md](MIGRATION.md) lists the changes that need edits in your code, and the guides at [philjay.cc/mpandroidchart/docs](https://philjay.cc/mpandroidchart/docs/) cover the whole library in 35 chapters.

### Requirements

minSdk 23, compileSdk 35, Java 17 and Kotlin 2.0 or newer. The Compose module needs compileSdk 37, because Compose itself does. Built with Gradle 9.7, AGP 9.4 and Kotlin 2.2. Both modules are compiled for Kotlin 2.0, so Kotlin 2.0 projects can read them.

### The API

- Every getter and setter pair is a property, and boolean flags read `isXxxEnabled`.
- Entries carry a typed payload: `Entry(x, y)` has none, `Entry(x, y, data = order)` is an `Entry<Order>`.
- Colors, labels, stack labels, draw orders and gradient fills are lists instead of arrays.
- Formatters and listeners are `fun interface` declarations, so a lambda is enough, and `chart.onValueSelected { entry, highlight -> }` is the short form of the selection listener.
- `chart.notifyDataSetChanged()` alone recalculates and redraws.
- The `PAINT_*` constants are gone, replaced by named paint properties such as `infoPaint` and `holePaint`.
- Sizes given in dp are stored in dp and converted when drawing.

### Added

- `MPChartCompose`, a Jetpack Compose module: one composable per chart type, `ChartState` for reading and driving a chart from Compose, `ComposeMarker` for markers written as composables, and helpers that bridge Compose colors, typefaces and painters.
- `BarLineChartBase.isDecimationEnabled`, off by default. It drops entries that share a pixel column while drawing, which pays off for bars, scatter and bubble charts and lines with a color per segment.
- Rounded bars through `BarDataSet.barCornerRadius`.
- A ring and halo around the highlighted point of a line chart.
- KDoc on every public class, function and property, published as an API reference through JitPack.
- Rounded polygon corners on a radar chart through `RadarDataSet.cornerRadius`.
- `HighlightLineSpan` on line, scatter, candle and radar data sets: `verticalHighlightIndicatorSpan` and `horizontalHighlightIndicatorSpan` draw the highlight line edge to edge, up to the entry, or from the entry onwards.
- `BarDataSet.isStackSectionsRounded`, which gives every section of a stacked bar its own rounded corners instead of rounding the bar as a whole.
- `IValueFormatter.getStackedFormattedValue`, which the bar renderers call for the values of a stacked entry. It receives the position in the stack, so a formatter can tell two equal values apart. The default forwards to `getFormattedValue`.
- An example app screen for each of the new pieces: the Nightfall design section, two Compose screens, a custom renderer, and one screen written in Java. The line and stacked bar screens toggle the new highlight line span and rounded stack sections from their menu.
- `chart.animateValue(entry, toY, durationMillis)` animates one entry to a new y value, and `chart.animateDataChange(newData, durationMillis)` sets new data and moves every entry from the value it replaces, including stack values, candles, bubble sizes and pie slices. The axes follow on every frame. In Compose, `animateChanges = true` on a chart composable does this for every new data instance, and `ChartState.animateDataChange` does it on demand. The example app has an "Animated data changes" screen.
- An empty state instead of the yellow "No chart data available.": a faint outline of the chart type above "No data yet" in slate grey, shown while there is no data or data without entries. `isLoading` and `loadingText` turn it into a pulsing loading state, and `noDataTextSize`, `noDataIcon`, `noDataIconColor`, `isNoDataIconEnabled` and an overridable `drawEmptyState` customize it.
- `animateX`, `animateY`, `animateXY` and `spin` take an optional `onEnd` callback, `chart.isAnimating` tells whether anything runs, and `chart.stopAnimations()` ends every animation at its final state.

### Changed

- Starting an animation cancels the running one of the same kind instead of letting both fight over the chart; the cancelled one still calls its `onEnd`. A chart leaving the window ends its animations at their final state, so an animation no longer keeps a removed chart alive.

- Value labels are decided per data set from the entries inside the visible x range, rather than from the entry count of the whole chart. A combined chart, or any chart with several data sets, used to lose every label once the sets added up to `maxVisibleCount`, which is 100 by default. Charts that quietly hid their labels will now draw them; lower `maxVisibleCount` to get the old density back.
- The x axis keeps its labels at least one label width apart, so long labels no longer overlap. A chart that asked for more labels than fit now gets fewer of them. `AxisRenderer.minimumInterval` is the hook for a renderer of your own.
- `autoScale()` is public, so a chart can be rescaled from outside without a subclass.
- A radar chart fills its view better. It used to keep as much space above and below itself as its widest axis label, where only half that plus a label's height is needed, and it left four times the legend text size between itself and a legend, where a pie chart leaves two.
- Rotating a pie or radar chart starts only when the finger goes down on the drawn chart: inside its radius, and outside the hole of a pie. A drag that starts in the hole or in a corner leaves the chart alone.
- `ScatterDataSet.scatterShapeSize`, `scatterShapeHoleRadius` and `ComposeMarker.offset` are in dp like every other size. The default shape size is 7.5 dp.
- `PieChart.centerTextSizePixels` is gone. Use `centerTextSize`, which is in dp.
- `PieData.dataSet` is nullable and reads null while no set is assigned, instead of throwing.
- `LargeValueFormatter.suffix` is a `List<String>`.
- `ChartState.highlight` takes a `dataIndex`, so it selects on a combined chart, and `rememberChartState` restores the y scroll position as well as the x.
- A line skips a circle that the previous circle would cover, and a candle chart with more than two candles per pixel column keeps only the first, lowest, highest and last of each column, with or without decimation. At 100,000 entries the first frame of a line with circles took 24 seconds and a candle chart 38; both now take well under one.
- Highlight per drag starts once the finger moved past the system touch slop along the x axis. A vertical swipe over a chart in a list scrolls the list and leaves the selection alone.
- `setVisibleXRange` and `setVisibleYRange` allow any range between the two limits. They used the minimum as the zoom out limit too, which pinned the zoom.
- Pie slices and percentages are shares of the sum of absolute values, so negative values no longer make slices overlap.
- Values that are infinite are left out of the axis ranges and drawn as gaps, like NaN, instead of collapsing the axis.

### Fixed

- Crashes and hangs found by feeding every chart type hostile data and calls on a device: NaN x values looped forever in lookups, highlights past the data (or left over after it shrank) threw in pie, radar, bar, marker and tap code, an empty `PieData` and an empty candle set threw while drawing, radar taps with sets of different lengths threw, NaN touch math poisoned the zoom and rotation, and a tiny y range ran the formatter out of memory.
- A chart removed while animating, or after a move or zoom job, stayed reachable. Charts in a `RecyclerView` or `LazyColumn` are now released once the list is gone.
- A chart never measured its child views, so a Compose marker left a layout pending for good and Compose UI tests never went idle.
- A line chart read every float of its point buffer on every frame looking for gaps, which at a hundred thousand entries cost more than the drawing. A data set remembers whether it holds a value that is not a number, and only then is the buffer checked: a full extent draw of 100,000 points went from 180 ms to 89 ms.
- A transparent line color faded the whole chart: the offscreen bitmap was composed with the last data set's paint, so its alpha applied to every curved line, dashed line and fill.
- Value labels above the bars of a horizontal bar chart disappeared as soon as the chart was zoomed in, because the label was culled by the bar's base rather than by the bar.
- A highlighted slice of a pie chart with rounded slices was not drawn at all, so a tap did nothing.
- The bar renderers threw when the data changed without `notifyDataSetChanged()`, or when a renderer was installed after the data. They rebuild their buffers when the data no longer fits.
- `YAxisRenderer` read past the positions it had computed when the axis changed while it was drawing.
- `StackedValueFormatter` decided the top of a stack by comparing values, so a stack holding the same value twice drew the total twice.
- Forced labels ignored the axis granularity, which gave fractional steps and repeated labels on an integer axis.
- The offsets of an auto-scaling chart were computed before the axis labels, so the gutter was always one frame behind while panning and long labels were clipped.
- Highlight per drag did nothing while dragging was disabled, because the touch listener returned before reaching it.
- A pinch that ran into the minimum or maximum scale made the chart jump, because the scale was clamped only after the translation had been computed for the larger scale.
- A line fill drawn with a `fillDrawable` showed a seam every 128 entries.
- A very long line was handed to the canvas in one draw call; it is drawn in chunks now.
- `LegendRenderer` read past the measured label sizes when the legend entries or orientation changed after the last measuring pass.
- A scatter shape's hole radius was converted to pixels a second time, so the hole did not match the shape size.
- `XAxisRenderer.drawLabel` was not open, which broke the usual way of drawing custom x labels.
- A chart whose values were all equal and above about 33 million drew nothing, because the empty range was widened by 1, which a float that large does not notice. The widening now scales with the value.
- One entry without a value no longer erases the rest of a line. The renderer draws each run of complete segments on its own, so a missing value leaves a gap instead of dropping the whole line.
- Long running charts grew the shared object pools on every frame. The axis renderers reuse their points instead of allocating and recycling one per pass, the pools have an upper bound, the line renderer drops the cached circle bitmaps of data sets the chart no longer holds, and a chart that never gets a size keeps at most 64 waiting viewport jobs.
- `Utils.formatNumber` threw for a digit count of 10.
- `LargeValueFormatter` picked the suffix from the unrounded value, so 999,950 printed as 1000k.
- The scatter renderer used one color per two entries, and placed value labels in a different unit than the shapes.
- `Fill.alpha` was ignored on opaque colors because the alpha byte was shifted with sign extension.
- The radar renderer set the stroke color once per entry although the polygon is a single path.
- `HorizontalBarChart` ignored `isHighlightFullBarEnabled` on a tap and handed out a pooled point from `getPosition`.
- A cancelled gesture did not hand scrolling back to the parent view.
- Line charts created, cleared and composed a chart sized bitmap on every frame even when no line needed one, and the circle cache did not notice a change of radius, hole or color.
- Pie and radar charts never called `initBuffers`, so a custom renderer for them was left uninitialised.
- `AxisBase.longestLabel` and `getFormattedLabel` read past the computed label count and could return a stale label.
- `ChartData.getEntryForHighlight` and the `CombinedData` lookups threw on a negative index instead of returning null.
- Empty color, value text color, circle color and fill lists fall back to black or an empty fill instead of throwing, and a gradient with one color draws flat.
- `BarDataSet` recalculated its stack size without resetting it, and its `copy()` left out the bar border color and the fills.
- `BaseDataSet.copy()` left out the value typeface, and `RadarDataSet.copy()` the outer highlight circle radius.
- A custom legend was not measured on a radar chart.
- `Chart.toBitmap()` dropped the alpha channel, and `saveToGallery` left its MediaStore row behind when writing failed.
- `XAxisRenderer` ignored the typeface of a limit line.
- The horizontal bar buffer stacked a zero value differently from the vertical one.

### Removed

- `OnDrawListener` and `Chart.onDrawListener`, which no chart ever called.
- `ColorFormatter`, which nothing in the library read.
- `RadarData.labels`, which nothing read; the web labels come from the x axis formatter.
- `saveToPath`, replaced by `saveToGallery` writing through the MediaStore.
- The `ValueFormatter` base class and `GradientColor` of 3.1.0, replaced by the formatter interfaces and `Fill`.
- `OnDrawLineChartTouchListener`, `ApproximatorN`, `Vector3` and `DrawingDataSetNotCreatedException`, which nothing used.
- The members deprecated in 3.x, the unused range fields on the buffers and the unused paint on `DataRenderer`.

### Reported issues and pull requests closed by this release

[#5243](https://github.com/PhilJay/MPAndroidChart/issues/5243), [#5244](https://github.com/PhilJay/MPAndroidChart/issues/5244), [#5251](https://github.com/PhilJay/MPAndroidChart/issues/5251), [#5284](https://github.com/PhilJay/MPAndroidChart/issues/5284), [#5354](https://github.com/PhilJay/MPAndroidChart/issues/5354), [#5362](https://github.com/PhilJay/MPAndroidChart/issues/5362), [#5370](https://github.com/PhilJay/MPAndroidChart/issues/5370), [#5374](https://github.com/PhilJay/MPAndroidChart/issues/5374), [#5391](https://github.com/PhilJay/MPAndroidChart/issues/5391), [#5397](https://github.com/PhilJay/MPAndroidChart/issues/5397), [#5407](https://github.com/PhilJay/MPAndroidChart/issues/5407), [#5415](https://github.com/PhilJay/MPAndroidChart/issues/5415), [#5432](https://github.com/PhilJay/MPAndroidChart/issues/5432), [#5438](https://github.com/PhilJay/MPAndroidChart/issues/5438), [#5447](https://github.com/PhilJay/MPAndroidChart/issues/5447), [#5448](https://github.com/PhilJay/MPAndroidChart/issues/5448), [#5453](https://github.com/PhilJay/MPAndroidChart/issues/5453), [#5458](https://github.com/PhilJay/MPAndroidChart/issues/5458), [#5459](https://github.com/PhilJay/MPAndroidChart/issues/5459), [#5471](https://github.com/PhilJay/MPAndroidChart/issues/5471), [#5476](https://github.com/PhilJay/MPAndroidChart/issues/5476), [#5477](https://github.com/PhilJay/MPAndroidChart/issues/5477), [#5491](https://github.com/PhilJay/MPAndroidChart/issues/5491), [#5492](https://github.com/PhilJay/MPAndroidChart/issues/5492), [#5493](https://github.com/PhilJay/MPAndroidChart/issues/5493), [#5497](https://github.com/PhilJay/MPAndroidChart/issues/5497), [#5500](https://github.com/PhilJay/MPAndroidChart/issues/5500), [#5506](https://github.com/PhilJay/MPAndroidChart/issues/5506), [#5513](https://github.com/PhilJay/MPAndroidChart/issues/5513), [#5514](https://github.com/PhilJay/MPAndroidChart/issues/5514), [#5523](https://github.com/PhilJay/MPAndroidChart/issues/5523), [#5344](https://github.com/PhilJay/MPAndroidChart/issues/5344), [#5360](https://github.com/PhilJay/MPAndroidChart/issues/5360).

### Other

- `LICENSE` now holds the full Apache 2.0 text instead of the short notice, so licence scanners recognise it, and `NOTICE` carries the attribution.
