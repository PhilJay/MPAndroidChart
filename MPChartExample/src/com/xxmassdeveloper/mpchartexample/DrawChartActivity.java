// TODO: Finish and add to main activity list
package com.xxmassdeveloper.mpchartexample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.listener.OnDrawListener;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;
import java.util.List;

/**
 * This Activity demonstrates drawing into the Chart with the finger. Both line,
 * bar and scatter charts can be used for drawing.
 *
 * @author Philipp Jahoda
 */
public class DrawChartActivity extends DemoBase implements OnChartValueSelectedListener,
        OnDrawListener {

    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_draw_chart);

        setTitle("DrawChartActivity");

        chart = findViewById(R.id.chart1);

        // listener for selecting and drawing
        chart.setOnChartValueSelectedListener(this);
        chart.setOnDrawListener(this);

        // if disabled, drawn data sets with the finger will not be automatically
        // finished
        // chart.setAutoFinish(true);
        chart.setDrawGridBackground(false);

        // add dummy-data to the chart
        initWithDummyData();

        XAxis xl = chart.getXAxis();
        xl.setTypeface(tfRegular);
        xl.setAvoidFirstLastClipping(true);

        YAxis yl = chart.getAxisLeft();
        yl.setTypeface(tfRegular);

        chart.getLegend().setEnabled(false);

        // chart.setYRange(-40f, 40f, true);
        // call this to reset the changed y-range
        // chart.resetYRange(true);
    }

    private void initWithDummyData() {

        ArrayList<Entry> values = new ArrayList<>();

        // create a dataset and give it a type (0)
        LineDataSet set1 = new LineDataSet(values, "DataSet");
        set1.setLineWidth(3f);
        set1.setCircleRadius(5f);

        // create a data object with the data sets
        LineData data = new LineData(set1);

        chart.setData(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.draw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                List<ILineDataSet> sets = chart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setDrawValues(!set.isDrawValuesEnabled());
                }

                chart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if(chart.getData() != null) {
                    chart.getData().setHighlightEnabled(!chart.getData().isHighlightEnabled());
                    chart.invalidate();
                }
                break;
            }
            case R.id.actionTogglePinch: {
                if (chart.isPinchZoomEnabled())
                    chart.setPinchZoom(false);
                else
                    chart.setPinchZoom(true);

                chart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                chart.setAutoScaleMinMaxEnabled(!chart.isAutoScaleMinMaxEnabled());
                chart.notifyDataSetChanged();
                break;
            }
            case R.id.actionSave: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveToGallery();
                } else {
                    requestStoragePermission(chart);
                }
                break;
            }
        }
        return true;
    }

    @Override
    public void saveToGallery() {
        saveToGallery(chart, "DrawChartActivity");
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
    }

    /** callback for each new entry drawn with the finger */
    @Override
    public void onEntryAdded(Entry entry) {
        Log.i(Chart.LOG_TAG, entry.toString());
    }

    /** callback when a DataSet has been drawn (when lifting the finger) */
    @Override
    public void onDrawFinished(DataSet<?> dataSet) {
        Log.i(Chart.LOG_TAG, "DataSet drawn. " + dataSet.toSimpleString());

        // prepare the legend again
        chart.getLegendRenderer().computeLegend(chart.getData());
    }

    @Override
    public void onEntryMoved(Entry entry) {
        Log.i(Chart.LOG_TAG, "Point moved " + entry.toString());
    }
}
