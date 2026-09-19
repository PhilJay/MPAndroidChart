package com.xxmassdeveloper.mpchartexample.notimportant

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.utils.Utils
import com.xxmassdeveloper.mpchartexample.AnotherBarActivity
import com.xxmassdeveloper.mpchartexample.BarChartActivity
import com.xxmassdeveloper.mpchartexample.BarChartActivityMultiDataset
import com.xxmassdeveloper.mpchartexample.BarChartActivitySinus
import com.xxmassdeveloper.mpchartexample.BarChartPositiveNegative
import com.xxmassdeveloper.mpchartexample.BarPerformanceActivity
import com.xxmassdeveloper.mpchartexample.BubbleChartActivity
import com.xxmassdeveloper.mpchartexample.CandleStickChartActivity
import com.xxmassdeveloper.mpchartexample.CombinedChartActivity
import com.xxmassdeveloper.mpchartexample.ComposeChartActivity
import com.xxmassdeveloper.mpchartexample.ComposeListActivity
import com.xxmassdeveloper.mpchartexample.CubicLineChartActivity
import com.xxmassdeveloper.mpchartexample.CustomRendererActivity
import com.xxmassdeveloper.mpchartexample.DynamicalAddingActivity
import com.xxmassdeveloper.mpchartexample.FilledLineActivity
import com.xxmassdeveloper.mpchartexample.HalfPieChartActivity
import com.xxmassdeveloper.mpchartexample.HorizontalBarChartActivity
import com.xxmassdeveloper.mpchartexample.HorizontalBarNegativeChartActivity
import com.xxmassdeveloper.mpchartexample.InvertedLineChartActivity
import com.xxmassdeveloper.mpchartexample.JavaChartActivity
import com.xxmassdeveloper.mpchartexample.LineChartActivity1
import com.xxmassdeveloper.mpchartexample.LineChartActivity2
import com.xxmassdeveloper.mpchartexample.LineChartActivityColored
import com.xxmassdeveloper.mpchartexample.LineChartTime
import com.xxmassdeveloper.mpchartexample.LinePerformanceActivity
import com.xxmassdeveloper.mpchartexample.ListViewBarChartActivity
import com.xxmassdeveloper.mpchartexample.ListViewMultiChartActivity
import com.xxmassdeveloper.mpchartexample.MultiLineChartActivity
import com.xxmassdeveloper.mpchartexample.PerformanceLineChart
import com.xxmassdeveloper.mpchartexample.PieChartActivity
import com.xxmassdeveloper.mpchartexample.PiePolylineChartActivity
import com.xxmassdeveloper.mpchartexample.R
import com.xxmassdeveloper.mpchartexample.RadarChartActivity
import com.xxmassdeveloper.mpchartexample.RealtimeLineChartActivity
import com.xxmassdeveloper.mpchartexample.ScatterChartActivity
import com.xxmassdeveloper.mpchartexample.ScrollViewActivity
import com.xxmassdeveloper.mpchartexample.StackedBarActivity
import com.xxmassdeveloper.mpchartexample.StackedBarActivityNegative
import com.xxmassdeveloper.mpchartexample.databinding.ActivityMainBinding
import com.xxmassdeveloper.mpchartexample.design.DesignActivity
import com.xxmassdeveloper.mpchartexample.fragments.SimpleChartDemo

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).hide(WindowInsetsCompat.Type.statusBars())
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.padForWindowInsets()
        title = "MPAndroidChart Example"
        supportActionBar?.apply {
            setIcon(R.drawable.ic_logo)
            setDisplayShowHomeEnabled(true)
        }

        Utils.init(this)

        val entries: List<Pair<ContentItem, (() -> Intent)?>> = listOf(
            section("All chart types"),
            item("Dark style", "Every chart type in one consistent dark style.") { DesignActivity.intent(this, dark = true) },
            item("Light style", "The same charts on a light background.") { DesignActivity.intent(this, dark = false) },
            section("Jetpack Compose"),
            item("Compose", "Line, pie and bar charts driven by Compose state.", ComposeChartActivity::class.java),
            item("Compose list", "A chart per row in a LazyColumn.", ComposeListActivity::class.java),
            section("Performance"),
            item("Line chart", "Pan and zoom a line chart while a readout shows what each draw costs.", LinePerformanceActivity::class.java),
            item("Bar chart", "Pan and zoom a bar chart while a readout shows what each draw costs.", BarPerformanceActivity::class.java),
            section("Line Charts"),
            item("Basic", "Simple line chart.", LineChartActivity1::class.java),
            item("Multiple", "Show multiple data sets.", MultiLineChartActivity::class.java),
            item("Dual Axis", "Line chart with dual y-axes.", LineChartActivity2::class.java),
            item("Inverted Axis", "Inverted y-axis.", InvertedLineChartActivity::class.java),
            item("Cubic", "Line chart with a cubic line shape.", CubicLineChartActivity::class.java),
            item("Colorful", "Colorful line chart.", LineChartActivityColored::class.java),
            item("Performance", "Render 30.000 data points smoothly.", PerformanceLineChart::class.java),
            item("Filled", "Colored area between two lines.", FilledLineActivity::class.java),
            item("From Java", "The same chart written in Java instead of Kotlin.", JavaChartActivity::class.java),
            section("Bar Charts"),
            item("Basic", "Simple bar chart.", BarChartActivity::class.java),
            item("Basic 2", "Variation of the simple bar chart.", AnotherBarActivity::class.java),
            item("Multiple", "Show multiple data sets.", BarChartActivityMultiDataset::class.java),
            item("Horizontal", "Render bar chart horizontally.", HorizontalBarChartActivity::class.java),
            item("Stacked", "Stacked bar chart.", StackedBarActivity::class.java),
            item("Negative", "Positive and negative values with unique colors.", BarChartPositiveNegative::class.java),
            item("Negative Horizontal", "demonstrates how to create a HorizontalBarChart with positive and negative values.", HorizontalBarNegativeChartActivity::class.java),
            item("Stacked 2", "Stacked bar chart with negative values.", StackedBarActivityNegative::class.java),
            item("Sine", "Sine function in bar chart format.", BarChartActivitySinus::class.java),
            item("Custom renderer", "Bar values drawn on a badge by a renderer of your own.", CustomRendererActivity::class.java),
            section("Pie Charts"),
            item("Basic", "Simple pie chart.", PieChartActivity::class.java),
            item("Value Lines", "Stylish lines drawn outward from slices.", PiePolylineChartActivity::class.java),
            item("Half Pie", "180° (half) pie chart.", HalfPieChartActivity::class.java),
            section("Other Charts"),
            item("Combined Chart", "Bar and line chart together.", CombinedChartActivity::class.java),
            item("Scatter Plot", "Simple scatter plot.", ScatterChartActivity::class.java),
            item("Bubble Chart", "Simple bubble chart.", BubbleChartActivity::class.java),
            item("Candlestick", "Simple financial chart.", CandleStickChartActivity::class.java),
            item("Radar Chart", "Simple web chart.", RadarChartActivity::class.java),
            section("Scrolling Charts"),
            item("Multiple", "Various types of charts as fragments.", ListViewMultiChartActivity::class.java),
            item("View Pager", "Swipe through different charts.", SimpleChartDemo::class.java),
            item("Tall Bar Chart", "Bars bigger than your screen!", ScrollViewActivity::class.java),
            item("Many Bar Charts", "More bars than your screen can handle!", ListViewBarChartActivity::class.java),
            section("Even More Line Charts"),
            item("Dynamic", "Build a line chart by adding points and sets.", DynamicalAddingActivity::class.java),
            item("Realtime", "Add data points in realtime.", RealtimeLineChartActivity::class.java),
            item("Hourly", "Uses the current time to add a data point for each hour.", LineChartTime::class.java),
        )

        binding.listView1.adapter = MyAdapter(this, entries.map { it.first })
        binding.listView1.setOnItemClickListener { _, _, position, _ ->
            entries[position].second?.let { open(it()) }
        }
    }

    private fun section(name: String): Pair<ContentItem, (() -> Intent)?> = ContentItem(name, isSection = true) to null

    private fun item(name: String, description: String, activity: Class<*>): Pair<ContentItem, (() -> Intent)?> =
        ContentItem(name, description) to { Intent(this, activity) }

    private fun item(name: String, description: String, intent: () -> Intent): Pair<ContentItem, (() -> Intent)?> =
        ContentItem(name, description) to intent

    @Suppress("DEPRECATION")
    private fun open(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/PhilJay/MPAndroidChart")))
            }
            R.id.report -> {
                val i = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "philjay.librarysup@gmail.com", null))
                i.putExtra(Intent.EXTRA_SUBJECT, "MPAndroidChart Issue")
                i.putExtra(Intent.EXTRA_TEXT, "Your error report here...")
                startActivity(Intent.createChooser(i, "Report Problem"))
            }
            R.id.website -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://at.linkedin.com/in/philippjahoda")))
            }
        }
        return true
    }
}
