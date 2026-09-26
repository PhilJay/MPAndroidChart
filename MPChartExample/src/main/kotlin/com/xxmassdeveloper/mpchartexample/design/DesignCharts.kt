package com.xxmassdeveloper.mpchartexample.design

import android.content.Context
import android.graphics.Paint
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BubbleChart
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlin.math.roundToInt
import kotlin.random.Random

/** One card of the Design screen: a titled chart in the Nightfall style. */
class DesignCard(val title: String, val subtitle: String, val delta: String?, val chart: View)

/** Builds every chart of the Nightfall design. Data is fixed so screenshots repeat exactly. */
object DesignCharts {

    private val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    fun all(context: Context, theme: Nightfall): List<DesignCard> = listOf(
        line(context, theme),
        bar(context, theme),
        horizontalBar(context, theme),
        pie(context, theme),
        stackedBar(context, theme),
        candle(context, theme),
        radar(context, theme),
        combined(context, theme),
        bubble(context, theme),
        scatter(context, theme),
        fullWidthLine(context, theme),
    )

    fun line(context: Context, theme: Nightfall): DesignCard {
        val values = listOf(42f, 48f, 45f, 60f, 58f, 72f, 70f, 84f, 79f, 92f, 88f, 97f)
        val set = LineDataSet(values.mapIndexed { i, v -> Entry(i.toFloat(), v) }, "Revenue").apply { nightfallLine(theme) }
        val chart = LineChart(context).apply {
            nightfallBase(theme)
            xAxis.valueFormatter = IndexAxisValueFormatter(months)
            nightfallPaddedX(months.size)
            axisLeft.axisMinimum = 30f
            axisLeft.axisMaximum = 110f
            axisLeft.labelCount = 5
            axisLeft.isForceLabelsEnabled = true
            data = LineData(set)
            marker = PillMarkerView(context, theme) { e -> "${months[e.x.toInt()]} · ${e.y.roundToInt()}" }
            highlightValue(9f, 0)
        }
        return DesignCard("Revenue", "Monthly, in k€", "+9.4%", chart)
    }

    /** The same line, but spread over the full width instead of leaving half a slot at each side. */
    fun fullWidthLine(context: Context, theme: Nightfall): DesignCard {
        val values = listOf(18f, 26f, 22f, 34f, 30f, 46f, 52f, 44f, 58f, 66f, 62f, 78f, 74f, 88f)
        val hours = List(values.size) { "%02d".format((it * 2) % 24) }
        val set = LineDataSet(values.mapIndexed { i, v -> Entry(i.toFloat(), v) }, "Traffic").apply {
            nightfallLine(theme, Nightfall.green)
        }
        val chart = LineChart(context).apply {
            nightfallBase(theme)
            xAxis.valueFormatter = IndexAxisValueFormatter(hours)
            nightfallFullWidthX(values.size)
            xAxis.labelCount = 7
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 100f
            axisLeft.labelCount = 5
            axisLeft.isForceLabelsEnabled = true
            data = LineData(set)
        }
        return DesignCard("Traffic", "Full width, no side padding", null, chart)
    }

    fun bar(context: Context, theme: Nightfall): DesignCard {
        val values = listOf(38f, 52f, 46f, 70f, 64f, 82f, 58f, 74f)
        val selected = 5
        val set = BarDataSet(values.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }, "Orders").apply { nightfallBars(selected = selected) }
        val chart = BarChart(context).apply {
            nightfallBase(theme)
            xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Q1", "Q2", "Q3", "Q4", "Q1", "Q2", "Q3", "Q4"))
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 100f
            axisLeft.labelCount = 5
            axisLeft.isForceLabelsEnabled = true
            data = BarData(set).apply { barWidth = 0.55f }
            marker = PillMarkerView(context, theme) { e -> e.y.roundToInt().toString() }
            highlightValue(selected.toFloat(), 0)
        }
        return DesignCard("Orders per quarter", "2025 to 2026", null, chart)
    }

    fun horizontalBar(context: Context, theme: Nightfall): DesignCard {
        val items = listOf("Rent" to 64f, "Food" to 48f, "Travel" to 36f, "Fun" to 28f, "Other" to 18f)
        val entries = items.mapIndexed { i, (_, v) -> BarEntry((items.size - 1 - i).toFloat(), v) }
        val set = BarDataSet(entries, "Budget").apply {
            barCornerRadius = 11f
            barShadowColor = theme.track
            colors = items.indices.reversed().map { Nightfall.palette[it % Nightfall.palette.size] }
            valueTextColor = theme.text
            valueTextSize = 11f
            valueFormatter = IValueFormatter { value, _, _, _ -> "${value.roundToInt()}%" }
        }
        val chart = HorizontalBarChart(context).apply {
            nightfallBase(theme)
            isDrawBarShadowEnabled = true
            xAxis.valueFormatter = IndexAxisValueFormatter(items.map { it.first }.reversed())
            xAxis.labelCount = items.size
            xAxis.granularity = 1f
            xAxis.textColor = theme.text
            xAxis.textSize = 11f
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 80f
            setExtraOffsets(4f, 4f, 4f, 4f)
            data = BarData(set).apply { barWidth = 0.5f }
        }
        return DesignCard("Budget", "Share of monthly spend", null, chart)
    }

    fun pie(context: Context, theme: Nightfall): DesignCard {
        val items = listOf("Mobile" to 44f, "Web" to 28f, "Desktop" to 18f, "Other" to 10f)
        val set = PieDataSet(items.map { (label, v) -> PieEntry(v, label) }, "").apply { nightfallSlices() }
        val chart = PieChart(context).apply {
            nightfallDonut(theme)
            centerText = SpannableString("44%\nMobile").apply {
                setSpan(RelativeSizeSpan(2.2f), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(StyleSpan(android.graphics.Typeface.BOLD), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(ForegroundColorSpan(theme.muted), 4, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            data = PieData(set)
        }
        return DesignCard("Sessions by platform", "Last 30 days", null, chart)
    }

    fun stackedBar(context: Context, theme: Nightfall): DesignCard {
        val stacks = listOf(
            listOf(30f, 20f, 12f), listOf(42f, 18f, 16f), listOf(36f, 26f, 10f), listOf(50f, 22f, 20f),
            listOf(44f, 30f, 14f), listOf(58f, 24f, 18f), listOf(40f, 20f, 22f), listOf(52f, 28f, 16f),
        )
        val set = BarDataSet(stacks.mapIndexed { i, s -> BarEntry(i.toFloat(), s) }, "Spend").apply {
            barCornerRadius = 6f
            colors = listOf(Nightfall.accent, Nightfall.coral, Nightfall.amber)
            isDrawValuesEnabled = false
            highlightAlpha = 0
        }
        val chart = BarChart(context).apply {
            nightfallBase(theme)
            xAxis.valueFormatter = IndexAxisValueFormatter(months.take(8))
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 100f
            axisLeft.labelCount = 5
            axisLeft.isForceLabelsEnabled = true
            data = BarData(set).apply { barWidth = 0.55f }
        }
        return DesignCard("Spend by category", "Stacked, in k€", null, chart)
    }

    fun candle(context: Context, theme: Nightfall): DesignCard {
        val random = Random(11)
        var price = 118f
        val entries = List(20) { i ->
            val open = price
            val close = open + random.nextInt(-8, 10)
            val high = maxOf(open, close) + random.nextInt(1, 6)
            val low = minOf(open, close) - random.nextInt(1, 6)
            price = close
            CandleEntry(i.toFloat(), high, low, open, close)
        }
        val set = CandleDataSet(entries, "ACME").apply {
            shadowColorSameAsCandle = true
            shadowWidth = 1.5f
            increasingColor = Nightfall.green
            increasingPaintStyle = Paint.Style.FILL
            decreasingColor = Nightfall.coral
            decreasingPaintStyle = Paint.Style.FILL
            neutralColor = Nightfall.amber
            barSpace = 0.25f
            isDrawValuesEnabled = false
            isHighlightEnabled = false
        }
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri")
        val chart = CandleStickChart(context).apply {
            nightfallBase(theme)
            xAxis.valueFormatter = IAxisValueFormatter { value, _ -> days[(value.roundToInt() / 2) % days.size] }
            xAxis.labelCount = 10
            xAxis.granularity = 2f
            axisLeft.axisMinimum = 80f
            axisLeft.axisMaximum = 160f
            axisLeft.labelCount = 5
            axisLeft.isForceLabelsEnabled = true
            data = CandleData(set)
        }
        return DesignCard("ACME Corp", "5 min candles", "+2.1%", chart)
    }

    fun radar(context: Context, theme: Nightfall): DesignCard {
        val labels = listOf("Speed", "Power", "Range", "Agility", "Stealth", "Armor")
        fun set(values: List<Float>, label: String, color: Int) = RadarDataSet(values.map { RadarEntry(it) }, label).apply {
            this.color = color
            fillColor = color
            fillAlpha = 56
            isDrawFilledEnabled = true
            lineWidth = 2.5f
            cornerRadius = 10f
            isDrawValuesEnabled = false
            isDrawHighlightCircleEnabled = false
            setDrawHighlightIndicators(false)
        }
        val chart = RadarChart(context).apply {
            nightfallBase(theme)
            webLineWidth = 1f
            webLineWidthInner = 1f
            webColor = theme.muted
            webColorInner = theme.muted
            webAlpha = 40
            isRotationEnabled = false
            yAxis.isDrawLabelsEnabled = false
            yAxis.axisMinimum = 0f
            yAxis.axisMaximum = 100f
            yAxis.labelCount = 4
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.textColor = theme.muted
            xAxis.textSize = 10f
            legend.apply {
                isEnabled = true
                form = Legend.LegendForm.SQUARE
                formSize = 10f
                textColor = theme.text
                textSize = 11f
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                xEntrySpace = 16f
            }
            data = RadarData(
                set(listOf(50f, 85f, 40f, 90f, 80f, 45f), "Last week", Nightfall.violet),
                set(listOf(90f, 60f, 80f, 70f, 50f, 85f), "This week", Nightfall.accent),
            )
        }
        return DesignCard("Team profile", "Skills, 0 to 100", null, chart)
    }

    fun combined(context: Context, theme: Nightfall): DesignCard {
        val bars = listOf(40f, 55f, 48f, 66f, 60f, 78f, 70f, 86f, 74f, 90f, 82f, 95f)
        val line = listOf(30f, 42f, 38f, 50f, 52f, 60f, 58f, 66f, 64f, 72f, 70f, 80f)
        val barSet = BarDataSet(bars.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }, "Users").apply {
            color = Nightfall.withAlpha(Nightfall.violet, 140)
            barCornerRadius = 4f
            isDrawValuesEnabled = false
            highlightAlpha = 0
        }
        val lineSet = LineDataSet(line.mapIndexed { i, v -> Entry(i.toFloat(), v) }, "Revenue").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.18f
            color = Nightfall.amber
            lineWidth = 3f
            circleRadius = 4f
            circleHoleRadius = 2f
            circleColor = Nightfall.amber
            circleHoleColor = theme.card
            isDrawValuesEnabled = false
            isHighlightEnabled = false
        }
        val chart = CombinedChart(context).apply {
            nightfallBase(theme)
            drawOrder = listOf(CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE)
            xAxis.valueFormatter = IndexAxisValueFormatter(months)
            xAxis.labelCount = 12
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = 11.5f
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 100f
            axisLeft.labelCount = 5
            axisLeft.isForceLabelsEnabled = true
            data = CombinedData().apply {
                barData = BarData(barSet).apply { barWidth = 0.5f }
                lineData = LineData(lineSet)
            }
        }
        return DesignCard("Growth", "Bars: users · line: revenue", null, chart)
    }

    fun bubble(context: Context, theme: Nightfall): DesignCard {
        val random = Random(5)
        val entries = List(16) {
            BubbleEntry(random.nextInt(0, 60) / 10f, random.nextInt(15, 95).toFloat(), random.nextInt(8, 40).toFloat())
        }.sortedBy { it.x }
        val set = BubbleDataSet(entries, "Deals").apply {
            colors = Nightfall.palette.take(4).map { Nightfall.withAlpha(it, 150) }
            isDrawValuesEnabled = false
            isHighlightEnabled = false
        }
        val chart = BubbleChart(context).apply {
            nightfallBase(theme)
            xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Q1", "Q2", "Q3", "Q4", "Q1", "Q2", "Q3"))
            xAxis.granularity = 1f
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = 6.5f
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 100f
            axisLeft.labelCount = 5
            axisLeft.isForceLabelsEnabled = true
            data = BubbleData(set)
        }
        return DesignCard("Deal size", "By probability", null, chart)
    }

    fun scatter(context: Context, theme: Nightfall): DesignCard {
        val random = Random(3)
        fun set(label: String, color: Int) = ScatterDataSet(List(15) { Entry(random.nextInt(0, 100) / 10f, random.nextInt(5, 98).toFloat()) }.sortedBy { it.x }, label).apply {
            setScatterShape(ScatterChart.ScatterShape.CIRCLE)
            scatterShapeSize = 9f
            this.color = Nightfall.withAlpha(color, 217)
            isDrawValuesEnabled = false
            isHighlightEnabled = false
        }
        val chart = ScatterChart(context).apply {
            nightfallBase(theme)
            xAxis.labelCount = 6
            xAxis.axisMinimum = 0f
            xAxis.axisMaximum = 10f
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 100f
            axisLeft.labelCount = 5
            axisLeft.isForceLabelsEnabled = true
            data = ScatterData(set("A", Nightfall.accent), set("B", Nightfall.coral), set("C", Nightfall.amber))
        }
        return DesignCard("Samples", "Three data sets", null, chart)
    }
}
