
package com.xxmassdeveloper.mpchartexample;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;
import com.xxmassdeveloper.mpchartexample.notimportant.MainActivity;

import org.junit.*;
import java.util.ArrayList;

public class CombinedChartActivityTest1 extends DemoBase {

    private CombinedChart chart;
    private final int count = 12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_combined);

        setTitle("CombinedChartActivity - Test1");

        chart = findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);

        if (chart.isDrawBarShadowEnabled()) {
            throw new AssertionError("CombinedChart initialized with draw bar shadow enabled");
        }

        chart.setDrawBarShadow(false);

        if (chart.isDrawBarShadowEnabled()) {
            throw new AssertionError("CombinedChart setDrawBarShadow did not overwrite original value");
        }

        if (!chart.isDrawValueAboveBarEnabled()) {
            throw new AssertionError("CombinedChart initialized with draw value disabled");
        }

        chart.setDrawValueAboveBar(true);

        if (!chart.isDrawValueAboveBarEnabled()) {
            throw new AssertionError("CombinedChart setDrawValueAboveBar did not overwrite original value");
        }

        if (chart.isHighlightFullBarEnabled()) {
            throw new AssertionError("CombinedChart initialized with highlightFullBar enabled");
        }

        chart.setHighlightFullBarEnabled(false);

        if (chart.isHighlightFullBarEnabled()) {
            throw new AssertionError("CombinedChart setHighlightFullBar did not overwrite original value");
        }

        chart.setDrawBarShadow(true);

        if (!chart.isDrawBarShadowEnabled()) {
            throw new AssertionError("CombinedChart setDrawValueAboveBar did not overwrite original value");
        }

        chart.setDrawBarShadow(true);

        if (!chart.isDrawBarShadowEnabled()) {
            throw new AssertionError("CombinedChart setDrawValueAboveBar did not overwrite original value");
        }

        chart.setDrawBarShadow(false);

        if (chart.isDrawBarShadowEnabled()) {
            throw new AssertionError("CombinedChart setDrawValueAboveBar did not overwrite original value");
        }

        chart.setDrawValueAboveBar(false);

        if (chart.isDrawValueAboveBarEnabled()) {
            throw new AssertionError("CombinedChart setDrawValueAboveBar did not overwrite original value");
        }

        chart.setDrawValueAboveBar(false);

        if (chart.isDrawValueAboveBarEnabled()) {
            throw new AssertionError("CombinedChart setDrawValueAboveBar did not overwrite original value");
        }

        chart.setDrawValueAboveBar(true);

        if (!chart.isDrawValueAboveBarEnabled()) {
            throw new AssertionError("CombinedChart setDrawValueAboveBar did not overwrite original value");
        }

        chart.setHighlightFullBarEnabled(true);

        if (!chart.isHighlightFullBarEnabled()) {
            throw new AssertionError("CombinedChart setHighlightFullBar did not overwrite original value");
        }

        chart.setHighlightFullBarEnabled(true);

        if (!chart.isHighlightFullBarEnabled()) {
            throw new AssertionError("CombinedChart setHighlightFullBar did not overwrite original value");
        }

        chart.setHighlightFullBarEnabled(false);

        if (chart.isHighlightFullBarEnabled()) {
            throw new AssertionError("CombinedChart setHighlightFullBar did not overwrite original value");
        }

        // draw bars behind lines
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return months[(int) value % months.length];
            }
        });

        CombinedData data = new CombinedData();

        data.setData(generateLineData());
        data.setData(generateBubbleData());
        data.setValueTypeface(tfLight);

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        if (chart.getData() != null) {
            throw new AssertionError("CombinedChart did not start with null data");
        }

        chart.setData(data);

        if (chart.getData() == null) {
            throw new AssertionError("CombinedChart did not set data properly");
        }

        CombinedData data2 = new CombinedData();
        data2.setData(generateLineData());
        data2.setData(generateBarData());

        chart.setData(data2);
        if (chart.getData() == null) {
            throw new AssertionError("CombinedChart did not set data properly");
        }
        else if (chart.getData() == data) {
            throw new AssertionError("CombinedChart setData did not overwrite original value");
        }

        chart.invalidate();


    }


    @Override public void onBackPressed(){
        super.onBackPressed();
        Intent i = new Intent(this, CombinedChartActivityTest2.class);
        if (i != null) startActivity(i);
    }

//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        startActivity(new Intent(this, CombinedChartActivityTest2.class));
//    }
    

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<>();

        for (int index = 0; index < count; index++)
            entries.add(new Entry(index + 0.5f, getRandom(15, 5)));

        LineDataSet set = new LineDataSet(entries, "Line DataSet");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData() {

        ArrayList<BarEntry> entries1 = new ArrayList<>();
        ArrayList<BarEntry> entries2 = new ArrayList<>();

        for (int index = 0; index < count; index++) {
            entries1.add(new BarEntry(0, getRandom(25, 25)));

            // stacked
            entries2.add(new BarEntry(0, new float[]{getRandom(13, 12), getRandom(13, 12)}));
        }

        BarDataSet set1 = new BarDataSet(entries1, "Bar 1");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarDataSet set2 = new BarDataSet(entries2, "");
        set2.setStackLabels(new String[]{"Stack 1", "Stack 2"});
        set2.setColors(Color.rgb(61, 165, 255), Color.rgb(23, 197, 255));
        set2.setValueTextColor(Color.rgb(61, 165, 255));
        set2.setValueTextSize(10f);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData d = new BarData(set1, set2);
        d.setBarWidth(barWidth);

        // make this BarData object grouped
        d.groupBars(0, groupSpace, barSpace); // start at x = 0

        return d;
    }

    private BubbleData generateBubbleData() {

        BubbleData bd = new BubbleData();

        ArrayList<BubbleEntry> entries = new ArrayList<>();

        for (int index = 0; index < count; index++) {
            float y = getRandom(10, 105);
            float size = getRandom(100, 105);
            entries.add(new BubbleEntry(index + 0.5f, y, size));
        }

        BubbleDataSet set = new BubbleDataSet(entries, "Bubble DataSet");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.WHITE);
        set.setHighlightCircleWidth(1.5f);
        set.setDrawValues(true);
        bd.addDataSet(set);

        return bd;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.combined, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewGithub: {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/CombinedChartActivity.java"));
                startActivity(i);
                break;
            }
            case R.id.actionToggleLineValues: {
                for (IDataSet set : chart.getData().getDataSets()) {
                    if (set instanceof LineDataSet)
                        set.setDrawValues(!set.isDrawValuesEnabled());
                }

                chart.invalidate();
                break;
            }
            case R.id.actionToggleBarValues: {
                for (IDataSet set : chart.getData().getDataSets()) {
                    if (set instanceof BarDataSet)
                        set.setDrawValues(!set.isDrawValuesEnabled());
                }

                chart.invalidate();
                break;
            }
            case R.id.actionRemoveDataSet: {
                int rnd = (int) getRandom(chart.getData().getDataSetCount(), 0);
                chart.getData().removeDataSet(chart.getData().getDataSetByIndex(rnd));
                chart.getData().notifyDataChanged();
                chart.notifyDataSetChanged();
                chart.invalidate();
                break;
            }
        }
        return true;
    }

    @Override
    public void saveToGallery() { /* Intentionally left empty */ }


}
