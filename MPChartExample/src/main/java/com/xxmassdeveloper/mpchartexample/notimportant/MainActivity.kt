package com.xxmassdeveloper.mpchartexample.notimportant

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.utils.Utils
import com.xxmassdeveloper.mpchartexample.*
import com.xxmassdeveloper.mpchartexample.fragments.ViewPagerSimpleChartDemo

class MainActivity : AppCompatActivity(), OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        // initialize the utilities
        Utils.init(this)
        val adapter = MyAdapter(this, menuItems)
        val lv = findViewById<ListView>(R.id.listView1)
        lv.adapter = adapter
        lv.onItemClickListener = this
    }

    override fun onItemClick(av: AdapterView<*>?, v: View, pos: Int, arg3: Long) {
        val intent = Intent(this, menuItems[pos].clazz)
        startActivity(intent)
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
                i.data = Uri.parse("https://github.com/AppDevNext/AndroidChart")
                startActivity(i)
            }
            R.id.report -> {
                i = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "philjay.librarysup@gmail.com", null))
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

    companion object {
        val menuItems = ArrayList<ContentItem<out DemoBase>>().apply {
            add(0, ContentItem("Line Charts"))
            add(0, ContentItem("Line Charts"))
            add(1, ContentItem("Basic", "Simple line chart.", LineChartActivity1::class.java))
            add(2, ContentItem("Multiple", "Show multiple data sets.", MultiLineChartActivity::class.java))
            add(3, ContentItem("Dual Axis", "Line chart with dual y-axes.", LineChartDualAxisActivity::class.java))
            add(4, ContentItem("Inverted Axis", "Inverted y-axis.", InvertedLineChartActivity::class.java))
            add(5, ContentItem("Cubic", "Line chart with a cubic line shape.", CubicLineChartActivity::class.java))
            add(6, ContentItem("Colorful", "Colorful line chart.", LineChartActivityColored::class.java))
            add(7, ContentItem("Performance", "Render 30.000 data points smoothly.", PerformanceLineChart::class.java))
            add(8, ContentItem("Filled", "Colored area between two lines.", FilledLineActivity::class.java))

            add(9, ContentItem("Bar Charts"))
            add(10, ContentItem("Basic", "Simple bar chart.", BarChartActivity::class.java))
            add(11, ContentItem("Basic 2", "Variation of the simple bar chart.", AnotherBarActivity::class.java))
            add(12, ContentItem("Multiple", "Show multiple data sets.", BarChartActivityMultiDataset::class.java))
            add(13, ContentItem("Horizontal", "Render bar chart horizontally.", HorizontalBarChartActivity::class.java))
            add(14, ContentItem("Stacked", "Stacked bar chart.", StackedBarActivity::class.java))
            add(15, ContentItem("Negative", "Positive and negative values with unique colors.", BarChartPositiveNegative::class.java))
            //objects.add(16, ContentItem("Negative Horizontal", "demonstrates how to create a HorizontalBarChart with positive and negative values."))
            add(17, ContentItem("Stacked 2", "Stacked bar chart with negative values.", StackedBarActivityNegative::class.java))
            add(18, ContentItem("Sine", "Sine function in bar chart format.", BarChartActivitySinus::class.java))

            add(19, ContentItem("Pie Charts"))
            add(20, ContentItem("Basic", "Simple pie chart.", PieChartActivity::class.java))
            add(21, ContentItem("Value Lines", "Stylish lines drawn outward from slices.", PiePolylineChartActivity::class.java))
            add(22, ContentItem("Half Pie", "180° (half) pie chart.", HalfPieChartActivity::class.java))
            add(23, ContentItem("Specific positions", "This demonstrates how to pass a list of specific positions for lines and labels on x and y axis", SpecificPositionsLineChartActivity::class.java))

            add(24, ContentItem("Other Charts"))
            add(25, ContentItem("Combined Chart", "Bar and line chart together.", CombinedChartActivity::class.java))
            add(26, ContentItem("Scatter Plot", "Simple scatter plot.", ScatterChartActivity::class.java))
            add(27, ContentItem("Bubble Chart", "Simple bubble chart.", BubbleChartActivity::class.java))
            add(28, ContentItem("Candlestick", "Simple financial chart.", CandleStickChartActivity::class.java))
            add(29, ContentItem("Radar Chart", "Simple web chart.", RadarChartActivity::class.java))

            add(30, ContentItem("Scrolling Charts"))
            add(31, ContentItem("Multiple", "Various types of charts as fragments.", ListViewMultiChartActivity::class.java))
            add(32, ContentItem("View Pager", "Swipe through different charts.", ViewPagerSimpleChartDemo::class.java))
            add(33, ContentItem("Tall Bar Chart", "Bars bigger than your screen!", ScrollViewActivity::class.java))
            add(34, ContentItem("Many Bar Charts", "More bars than your screen can handle!", ListViewBarChartActivity::class.java))

            add(35, ContentItem("Even More Line Charts"))
            add(36, ContentItem("Dynamic", "Build a line chart by adding points and sets.", DynamicalAddingActivity::class.java))
            add(37, ContentItem("Realtime", "Add data points in realtime.", RealtimeLineChartActivity::class.java))
            add(38, ContentItem("Hourly", "Uses the current time to add a data point for each hour.", LineChartTime::class.java))
            //add(39, new ContentItem("Realm.io Examples", "See more examples that use Realm.io mobile database."));
        }
    }
}