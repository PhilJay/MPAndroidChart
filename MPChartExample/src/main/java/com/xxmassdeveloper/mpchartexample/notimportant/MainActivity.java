
package com.xxmassdeveloper.mpchartexample.notimportant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.github.mikephil.charting.utils.Utils;
import com.xxmassdeveloper.mpchartexample.AnotherBarActivity;
import com.xxmassdeveloper.mpchartexample.BarChartActivity;
import com.xxmassdeveloper.mpchartexample.BarChartActivityMultiDataset;
import com.xxmassdeveloper.mpchartexample.BarChartActivitySinus;
import com.xxmassdeveloper.mpchartexample.BarChartPositiveNegative;
import com.xxmassdeveloper.mpchartexample.BubbleChartActivity;
import com.xxmassdeveloper.mpchartexample.CandleStickChartActivity;
import com.xxmassdeveloper.mpchartexample.CombinedChartActivity;
import com.xxmassdeveloper.mpchartexample.CubicLineChartActivity;
import com.xxmassdeveloper.mpchartexample.DynamicalAddingActivity;
import com.xxmassdeveloper.mpchartexample.FilledLineActivity;
import com.xxmassdeveloper.mpchartexample.HalfPieChartActivity;
import com.xxmassdeveloper.mpchartexample.HighlightBubbleChart;
import com.xxmassdeveloper.mpchartexample.HorizontalBarChartActivity;
import com.xxmassdeveloper.mpchartexample.InvertedLineChartActivity;
import com.xxmassdeveloper.mpchartexample.LineChartActivity1;
import com.xxmassdeveloper.mpchartexample.LineChartActivityColored;
import com.xxmassdeveloper.mpchartexample.LineChartTime;
import com.xxmassdeveloper.mpchartexample.ListViewBarChartActivity;
import com.xxmassdeveloper.mpchartexample.ListViewMultiChartActivity;
import com.xxmassdeveloper.mpchartexample.MultiLineChartActivity;
import com.xxmassdeveloper.mpchartexample.PerformanceLineChart;
import com.xxmassdeveloper.mpchartexample.PieChartActivity;
import com.xxmassdeveloper.mpchartexample.PiePolylineChartActivity;
import com.xxmassdeveloper.mpchartexample.R;
import com.xxmassdeveloper.mpchartexample.RadarChartActivity;
import com.xxmassdeveloper.mpchartexample.RealtimeLineChartActivity;
import com.xxmassdeveloper.mpchartexample.ScatterChartActivity;
import com.xxmassdeveloper.mpchartexample.ScrollViewActivity;
import com.xxmassdeveloper.mpchartexample.StackedBarActivity;
import com.xxmassdeveloper.mpchartexample.StackedBarActivityNegative;
import com.xxmassdeveloper.mpchartexample.fragments.SimpleChartDemo;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {
    ArrayList<ContentItem> mActivities = new ArrayList<>();
    int mPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        setTitle("MPAndroidChart Example");

        // initialize the utilities
        Utils.init(this);

        addHeader("Highlight Axes");
        addExample("Bubble Chart", "Highlight in two lines of code", HighlightBubbleChart.class);

        addHeader("Line Charts");

        addExample("Basic", "Simple line chart.", LineChartActivity1.class);
        addExample("Multiple", "Show multiple data sets.", MultiLineChartActivity.class);
        addExample("Dual Axis", "Line chart with dual y-axes.", MultiLineChartActivity.class);
        addExample("Inverted Axis", "Inverted y-axis.", InvertedLineChartActivity.class);
        addExample("Cubic", "Line chart with a cubic line shape.", CubicLineChartActivity.class);
        addExample("Colorful", "Colorful line chart.", LineChartActivityColored.class);
        addExample("Performance", "Render 30.000 data points smoothly.", PerformanceLineChart.class);
        addExample("Filled", "Colored area between two lines.", FilledLineActivity.class);

        ////
        addHeader("Bar Charts");

        addExample("Basic", "Simple bar chart.", BarChartActivity.class);
        addExample("Basic 2", "Variation of the simple bar chart.", AnotherBarActivity.class);
        addExample("Multiple", "Show multiple data sets.", BarChartActivityMultiDataset.class);
        addExample("Horizontal", "Render bar chart horizontally.", HorizontalBarChartActivity.class);
        addExample("Stacked", "Stacked bar chart.", StackedBarActivity.class);
        addExample("Negative", "Positive and negative values with unique colors.", BarChartPositiveNegative.class);
        addExample("Stacked 2", "Stacked bar chart with negative values.", StackedBarActivityNegative.class);
        addExample("Sine", "Sine function in bar chart format.", BarChartActivitySinus.class);

        ////
        addHeader("Pie Charts");

        addExample("Basic", "Simple pie chart.", PieChartActivity.class);
        addExample("Value Lines", "Stylish lines drawn outward from slices.", PiePolylineChartActivity.class);
        addExample("Half Pie", "180° (half) pie chart.", HalfPieChartActivity.class);

        ////
        addHeader("Other Charts");

        addExample("Combined Chart", "Bar and line chart together.", CombinedChartActivity.class);
        addExample("Scatter Plot", "Simple scatter plot.", ScatterChartActivity.class);
        addExample("Bubble Chart", "Simple bubble chart.", BubbleChartActivity.class);
        addExample("Candlestick", "Simple financial chart.", CandleStickChartActivity.class);
        addExample("Radar Chart", "Simple web chart.", RadarChartActivity.class);

        ////
        addHeader("Scrolling Charts");

        addExample("Multiple", "Various types of charts as fragments.", ListViewMultiChartActivity.class);
        addExample("View Pager", "Swipe through different charts.", SimpleChartDemo.class);
        addExample("Tall Bar Chart", "Bars bigger than your screen!", ScrollViewActivity.class);
        addExample("Many Bar Charts", "More bars than your screen can handle!", ListViewBarChartActivity.class);

        ////
        addHeader("Even More Line Charts");

        addExample("Dynamic", "Build a line chart by adding points and sets.", DynamicalAddingActivity.class);
        addExample("Realtime", "Add data points in realtime.", RealtimeLineChartActivity.class);
        addExample("Hourly", "Uses the current time to add a data point for each hour.", LineChartTime.class);
        //addItem("Realm.io Examples", "See more examples that use Realm.io mobile database.", RealmMainActivity.class, position++));


        MyAdapter adapter = new MyAdapter(this, mActivities);


        ListView lv = findViewById(R.id.listView1);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long arg3) {
        ContentItem example = mActivities.get(pos);
        if (example.klass == null)
            return;
        Intent intent = new Intent(this, mActivities.get(pos).klass);
        startActivity(intent);

        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent i;

        switch (item.getItemId()) {
            case R.id.viewGithub:
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart"));
                startActivity(i);
                break;
            case R.id.report:
                i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "philjay.librarysup@gmail.com", null));
                i.putExtra(Intent.EXTRA_SUBJECT, "MPAndroidChart Issue");
                i.putExtra(Intent.EXTRA_TEXT, "Your error report here...");
                startActivity(Intent.createChooser(i, "Report Problem"));
                break;
            case R.id.website:
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://at.linkedin.com/in/philippjahoda"));
                startActivity(i);
                break;
        }

        return true;
    }

    private void addHeader(String name) {
        ContentItem item = new ContentItem(name, mPosition);
        mActivities.add(mPosition++, item);
    }

    private void addExample(String name, String description, Class klazz) {
        ContentItem item = new ContentItem(name, description, mPosition, klazz);
        mActivities.add(mPosition++, item);
    }
}
