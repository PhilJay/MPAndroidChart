# Point decimation for large data sets

Draw only the points a pixel column can actually show, so a frame draws what the screen can hold instead of everything on it. Line, scatter and candle charts in the first pass; bar charts are a separate piece of work because sub-pixel bars need merging rather than an envelope.

## Why

Measured on the API 36 arm64 emulator, panning one 200k point line chart while varying only how many points are on screen:

| Points on screen | ms per frame |
| --- | --- |
| 500 | 2.45 |
| 1,000 | 3.07 |
| 2,000 | 4.89 |
| 5,000 | 9.84 |
| 10,000 | 20.42 |
| 20,000 | 29.59 |

About 1.8 microseconds per visible point plus 1.8ms of fixed overhead. The data set size never enters into it: the renderers already clip to `xBounds`, so what costs is points on screen, not points held. Scrolling a million point set at normal zoom is already smooth. What is not smooth is zooming out, where a full extent draw of 100k points takes 157ms, about nine dropped frames.

A 60Hz frame is 16.67ms and a chart realistically gets about half of it. What was built does not reach that, and cannot. Drawing the whole extent of a data set, median of nine draws on the same emulator, with decimation on and off:

| Data set | on | off |
| --- | --- | --- |
| 10,000 points | 16.28 ms | 21.43 ms |
| 50,000 points | 54.74 ms | 85.13 ms |
| 100,000 points | 105.05 ms | 166.49 ms |
| 50,000 points, 8 colours | 60.27 ms | 144.16 ms |

A 1.58x win at 100k, and 2.39x with eight colours, where one draw call per segment is what costs. Worth having, and still far from a 16.67ms frame at these sizes.

Read the ratios, not the absolutes. These are emulator numbers and they move with whatever else the host is doing: a later run of the same tests on the same emulator came back about four times faster across the board, with the ratios holding. Landing somewhere else on a re-run is the measurement moving, not a regression.

## Goal

Drop the points a pixel column cannot show, so the drawing shrinks to what the screen can hold: on a 1080px chart roughly 2,000 to 4,200 drawn points whatever the set holds.

The frame does not become that cheap, because the reduction runs per frame and reads the x value and the y range of every visible entry. That scan is the floor: a frame stays O(visible entries) and never becomes O(pixel width). Decimation removes the drawing above the floor, not the floor.

## The reducer

One shared component decides which entries are worth drawing. Line, scatter and candle differ in geometry, not in that decision.

**It returns entry indices, never synthetic points.** Every drawn point is a real entry, so colours, circles, value labels, highlighting and the fill path all keep referring to entries that exist. There is no second coordinate space to keep in sync.

For each pixel column, over the entries that fall in it:

- Four point mode keeps `first`, `argmin(y)`, `argmax(y)` and `last`, sorted by index and deduplicated.
- Two point mode keeps `argmin(y)` and `argmax(y)`, sorted by index and deduplicated.

Deduplication gives the property the whole design leans on: at about one entry per column all four candidates are the same index, so the reducer returns the original points and the transition into decimation is continuous. Nothing pops as the user pinches.

Candle charts use `low` and `high` in place of `y`. That is the only per type variation, handled by a small accessor rather than three copies of the loop. If per entry dispatch shows up in the benchmark, the loop gets split per type instead.

Column assignment never touches the matrix: `column = (entry.x - firstVisibleX) / valuesPerPixel`, where `valuesPerPixel = visibleXRange / contentWidthPx`. Discarded points are never transformed.

The index buffer is a grow-only field sized to `4 * contentWidthPx + 8`.

## Reduce per frame, do not cache

The reduction is a flat pass over the visible entries: one entry fetch and two float comparisons each. At 100k it is most of what a decimated frame costs, since only a few thousand points are left to draw, and it is still well under the 166ms an undecimated draw takes. A cache keyed on the viewport would miss on every frame while panning, because the key changes every frame, so it would buy nothing in the case that matters. A precomputed multi resolution pyramid would have to be rebuilt on every append, which breaks appending 100 entries per second.

Both remain open if measurement later demands them.

## API

```kotlin
// BarLineScatterCandleBubbleDataProvider
val isDecimationEnabled: Boolean get() = true

// BarLineChartBase
override var isDecimationEnabled = true
```

One switch, per chart, on by default. The interface default implementation means anyone implementing the provider themselves needs no change. Pie and radar never see it.

The thresholds stay internal constants rather than public settings: decimation activates above one point per pixel, and switches from four point to two point mode above eight points per pixel. They can be exposed later if a real case needs them.

## Tuning

`ACTIVATION_POINTS_PER_PIXEL = 1` and `COARSE_POINTS_PER_PIXEL = 8`, the values the design started with, kept after looking at the result.

`DecimationImageTest` renders the same 100k point line decimated and plain at 2, 5, 10 and 50 points per pixel. The pairs on either side of the coarse boundary are the ones that decide it. At 5 points per pixel, four point mode, decimated and plain are indistinguishable. At 50 points per pixel, two point mode, they are indistinguishable as well. The boundary holds from both sides, so there is nothing to move it towards.

Eight is a default, not a law. It sits in `PointReducer` next to the activation threshold, where anyone can revisit it, and a chart whose marks are much wider than a line may well want a different one.

## Renderer integration

The reduction lives on `BarLineScatterCandleBubbleRenderer` next to `xBounds`, as a `protected` reducer and one `reduceVisible` call, so the three renderers and anyone subclassing them read the same index list.

- `LineChartRenderer`: `drawLinear`, the multi colour branch, `drawLinearFill` and `drawCircles` iterate the index list instead of `xBounds.min .. max`. The fill must use the same indices as the stroke or the two disagree.
- `ScatterChartRenderer`: same, and it is also fixed to use `xBounds`. It currently loops from index 0 over every entry and only breaks once past the right edge, so panning a 100k scatter to its end transforms 100k points per frame.
- `CandleStickChartRenderer`: same, with the wick and body buffers built from the index list.

Cubic mode draws a spline through the decimated points. The deduplication property above makes that safe: at the activation threshold the decimated set is the original set, so the curve does not jump. It drifts only well past one point per pixel, where sub pixel curvature is invisible. No hysteresis is needed.

## What does not change

Highlighting, `getEntryForIndex`, the binary search and `maxVisibleCount` value gating all keep working untouched, because decimation only ever selects real indices. Highlighting does not consult drawn geometry at all.

Circle culling is the one thing that moves. Circles are picked from the reduced indices and dropped when another circle would cover them, and both follow the switch, so turning decimation off draws a circle at every visible entry again.

## Testing

The reducer is pure logic with no `Canvas`, so it unit tests in `MPChartLib/src/test`:

1. Per column extremes survive, so a single tall spike is never dropped.
2. Below one point per pixel the output equals the input.
3. Output indices are strictly increasing.

Instrumented, in `MPChartBenchmark`, on top of the four renderer tests already there:

4. With decimation off, rendered output is unchanged from today.
5. Highlighting returns the same entry whether decimation is on or off.

The benchmark gains a decimation on and off dimension, which is the A/B measurement. Rendered before and after images at several densities are produced during implementation, because no assertion replaces looking at the chart.

## Risks

- Fill and stroke diverge if the fill path is missed. One test per line mode compares the columns a filled data set paints with the columns its stroke alone paints.
- Visual acceptance is the real unknown. The eight points per pixel threshold started as an estimate and was checked against rendered images, see Tuning.
- Scatter shapes are much larger than a one pixel line, so they may want their own threshold. Scatter uses the shared thresholds; a shape aware threshold is future work.
