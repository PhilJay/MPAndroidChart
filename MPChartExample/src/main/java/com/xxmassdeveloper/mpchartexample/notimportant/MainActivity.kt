package com.xxmassdeveloper.mpchartexample.notimportant

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import android.graphics.Typeface
import android.os.Bundle
import com.xxmassdeveloper.mpchartexample.R
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.github.mikephil.charting.charts.Chart
import com.xxmassdeveloper.mpchartexample.notimportant.ContentItem
import android.annotation.SuppressLint
import android.widget.AdapterView.OnItemClickListener
import com.xxmassdeveloper.mpchartexample.notimportant.MyAdapter
import android.content.Intent
import android.net.Uri
import android.view.*
import android.widget.*
import com.github.mikephil.charting.utils.Utils
import com.xxmassdeveloper.mpchartexample.LineChartActivity1
import com.xxmassdeveloper.mpchartexample.MultiLineChartActivity
import com.xxmassdeveloper.mpchartexample.LineChartActivity2
import com.xxmassdeveloper.mpchartexample.InvertedLineChartActivity
import com.xxmassdeveloper.mpchartexample.CubicLineChartActivity
import com.xxmassdeveloper.mpchartexample.LineChartActivityColored
import com.xxmassdeveloper.mpchartexample.PerformanceLineChart
import com.xxmassdeveloper.mpchartexample.FilledLineActivity
import com.xxmassdeveloper.mpchartexample.BarChartActivity
import com.xxmassdeveloper.mpchartexample.AnotherBarActivity
import com.xxmassdeveloper.mpchartexample.BarChartActivityMultiDataset
import com.xxmassdeveloper.mpchartexample.HorizontalBarChartActivity
import com.xxmassdeveloper.mpchartexample.StackedBarActivity
import com.xxmassdeveloper.mpchartexample.BarChartPositiveNegative
import com.xxmassdeveloper.mpchartexample.HorizontalBarNegativeChartActivity
import com.xxmassdeveloper.mpchartexample.StackedBarActivityNegative
import com.xxmassdeveloper.mpchartexample.BarChartActivitySinus
import com.xxmassdeveloper.mpchartexample.PieChartActivity
import com.xxmassdeveloper.mpchartexample.PiePolylineChartActivity
import com.xxmassdeveloper.mpchartexample.HalfPieChartActivity
import com.xxmassdeveloper.mpchartexample.CombinedChartActivity
import com.xxmassdeveloper.mpchartexample.ScatterChartActivity
import com.xxmassdeveloper.mpchartexample.BubbleChartActivity
import com.xxmassdeveloper.mpchartexample.CandleStickChartActivity
import com.xxmassdeveloper.mpchartexample.RadarChartActivity
import com.xxmassdeveloper.mpchartexample.ListViewMultiChartActivity
import com.xxmassdeveloper.mpchartexample.fragments.SimpleChartDemo
import com.xxmassdeveloper.mpchartexample.ScrollViewActivity
import com.xxmassdeveloper.mpchartexample.ListViewBarChartActivity
import com.xxmassdeveloper.mpchartexample.DynamicalAddingActivity
import com.xxmassdeveloper.mpchartexample.RealtimeLineChartActivity
import com.xxmassdeveloper.mpchartexample.LineChartTime
import java.util.ArrayList

class MainActivity : AppCompatActivity(), OnItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)
        title = "MPAndroidChart Example"

        // initialize the utilities
        Utils.init(this)
        val objects = ArrayList<ContentItem>()

        ////
        objects.add(0, ContentItem("Line Charts"))
        objects.add(1, ContentItem("Basic", "Simple line chart."))
        objects.add(2, ContentItem("Multiple", "Show multiple data sets."))
        objects.add(3, ContentItem("Dual Axis", "Line chart with dual y-axes."))
        objects.add(4, ContentItem("Inverted Axis", "Inverted y-axis."))
        objects.add(5, ContentItem("Cubic", "Line chart with a cubic line shape."))
        objects.add(6, ContentItem("Colorful", "Colorful line chart."))
        objects.add(7, ContentItem("Performance", "Render 30.000 data points smoothly."))
        objects.add(8, ContentItem("Filled", "Colored area between two lines."))

        ////
        objects.add(9, ContentItem("Bar Charts"))
        objects.add(10, ContentItem("Basic", "Simple bar chart."))
        objects.add(11, ContentItem("Basic 2", "Variation of the simple bar chart."))
        objects.add(12, ContentItem("Multiple", "Show multiple data sets."))
        objects.add(13, ContentItem("Horizontal", "Render bar chart horizontally."))
        objects.add(14, ContentItem("Stacked", "Stacked bar chart."))
        objects.add(15, ContentItem("Negative", "Positive and negative values with unique colors."))
        objects.add(
            16,
            ContentItem(
                "Negative Horizontal",
                "demonstrates how to create a HorizontalBarChart with positive and negative values."
            )
        )
        objects.add(17, ContentItem("Stacked 2", "Stacked bar chart with negative values."))
        objects.add(18, ContentItem("Sine", "Sine function in bar chart format."))

        ////
        objects.add(19, ContentItem("Pie Charts"))
        objects.add(20, ContentItem("Basic", "Simple pie chart."))
        objects.add(21, ContentItem("Value Lines", "Stylish lines drawn outward from slices."))
        objects.add(22, ContentItem("Half Pie", "180° (half) pie chart."))

        ////
        objects.add(23, ContentItem("Other Charts"))
        objects.add(24, ContentItem("Combined Chart", "Bar and line chart together."))
        objects.add(25, ContentItem("Scatter Plot", "Simple scatter plot."))
        objects.add(26, ContentItem("Bubble Chart", "Simple bubble chart."))
        objects.add(27, ContentItem("Candlestick", "Simple financial chart."))
        objects.add(28, ContentItem("Radar Chart", "Simple web chart."))

        ////
        objects.add(29, ContentItem("Scrolling Charts"))
        objects.add(30, ContentItem("Multiple", "Various types of charts as fragments."))
        objects.add(31, ContentItem("View Pager", "Swipe through different charts."))
        objects.add(32, ContentItem("Tall Bar Chart", "Bars bigger than your screen!"))
        objects.add(33, ContentItem("Many Bar Charts", "More bars than your screen can handle!"))

        ////
        objects.add(34, ContentItem("Even More Line Charts"))
        objects.add(35, ContentItem("Dynamic", "Build a line chart by adding points and sets."))
        objects.add(36, ContentItem("Realtime", "Add data points in realtime."))
        objects.add(
            37,
            ContentItem("Hourly", "Uses the current time to add a data point for each hour.")
        )
        //objects.add(38, new ContentItem("Realm.io Examples", "See more examples that use Realm.io mobile database."));
        val adapter = MyAdapter(this, objects)
        val lv = findViewById<ListView>(R.id.listView1)
        lv.adapter = adapter
        lv.onItemClickListener = this
    }

    override fun onItemClick(av: AdapterView<*>?, v: View, pos: Int, arg3: Long) {
        var i: Intent? = null
        when (pos) {
            1 -> i = Intent(this, LineChartActivity1::class.java)
            2 -> i = Intent(this, MultiLineChartActivity::class.java)
            3 -> i = Intent(this, LineChartActivity2::class.java)
            4 -> i = Intent(this, InvertedLineChartActivity::class.java)
            5 -> i = Intent(this, CubicLineChartActivity::class.java)
            6 -> i = Intent(this, LineChartActivityColored::class.java)
            7 -> i = Intent(this, PerformanceLineChart::class.java)
            8 -> i = Intent(this, FilledLineActivity::class.java)
            10 -> i = Intent(this, BarChartActivity::class.java)
            11 -> i = Intent(this, AnotherBarActivity::class.java)
            12 -> i = Intent(this, BarChartActivityMultiDataset::class.java)
            13 -> i = Intent(this, HorizontalBarChartActivity::class.java)
            14 -> i = Intent(this, StackedBarActivity::class.java)
            15 -> i = Intent(this, BarChartPositiveNegative::class.java)
            16 -> i = Intent(this, HorizontalBarNegativeChartActivity::class.java)
            17 -> i = Intent(this, StackedBarActivityNegative::class.java)
            18 -> i = Intent(this, BarChartActivitySinus::class.java)
            20 -> i = Intent(this, PieChartActivity::class.java)
            21 -> i = Intent(this, PiePolylineChartActivity::class.java)
            22 -> i = Intent(this, HalfPieChartActivity::class.java)
            24 -> i = Intent(this, CombinedChartActivity::class.java)
            25 -> i = Intent(this, ScatterChartActivity::class.java)
            26 -> i = Intent(this, BubbleChartActivity::class.java)
            27 -> i = Intent(this, CandleStickChartActivity::class.java)
            28 -> i = Intent(this, RadarChartActivity::class.java)
            30 -> i = Intent(this, ListViewMultiChartActivity::class.java)
            31 -> i = Intent(this, SimpleChartDemo::class.java)
            32 -> i = Intent(this, ScrollViewActivity::class.java)
            33 -> i = Intent(this, ListViewBarChartActivity::class.java)
            35 -> i = Intent(this, DynamicalAddingActivity::class.java)
            36 -> i = Intent(this, RealtimeLineChartActivity::class.java)
            37 -> i = Intent(this, LineChartTime::class.java)
        }
        i?.let { startActivity(it) }
        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i: Intent
        when (item.itemId) {
            R.id.viewGithub -> {
                i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart")
                startActivity(i)
            }
            R.id.report -> {
                i = Intent(
                    Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "philjay.librarysup@gmail.com", null
                    )
                )
                i.putExtra(Intent.EXTRA_SUBJECT, "MPAndroidChart Issue")
                i.putExtra(Intent.EXTRA_TEXT, "Your error report here...")
                startActivity(Intent.createChooser(i, "Report Problem"))
            }
            R.id.website -> {
                i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("http://at.linkedin.com/in/philippjahoda")
                startActivity(i)
            }
        }
        return true
    }
}