
package com.xxmassdeveloper.mpchartexample;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;

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
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;
import com.xxmassdeveloper.mpchartexample.notimportant.MainActivity;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class CombinedChartActivityTest3 extends DemoBase {

    private CombinedChart chart;
    private final int count = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_combined);

        setTitle("CombinedChartActivity - Test3");

        chart = findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);



        // draw bars behind lines
        CombinedChart.DrawOrder[] orders = new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        };
        chart.setDrawOrder(orders);

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
        data.setData(generateBarData());
        data.setData(generateCandleData());
        data.setData(generateScatterData());
        data.setValueTypeface(tfLight);

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        chart.setData(data);

        chart.invalidate();

//        Highlight highlight = chart.getHighlightByTouchPoint(MPPointF.getInstance().x,MPPointF.getInstance().y);
//
//        System.out.println("highlight: " + highlight.getX() + " " + highlight.getY());


        //Test for getDrawOrder
        CombinedChart.DrawOrder[] drawOrders = chart.getDrawOrder();
        Assert.assertArrayEquals(drawOrders,orders);

        //Test for get value
        try{
            BarData barData = chart.getBarData();
        }catch (Exception e){
            throw new AssertionError("CombinedChart getBarData did not get data properly");
        }

        try{
            BubbleData bubbleData = chart.getBubbleData();
        }catch (Exception e){
            throw new AssertionError("CombinedChart getBubbleData did not get data properly");
        }

        try{
            CandleData candleData = chart.getCandleData();
        }catch (Exception e){
            throw new AssertionError("CombinedChart getCandleData did not get data properly");
        }

        try{
            CombinedData combinedData = chart.getCombinedData();
        }catch (Exception e){
            throw new AssertionError("CombinedChart getCombinedData did not get data properly");
        }

        try{
            LineData lineData = chart.getLineData();
        }catch (Exception e){
            throw new AssertionError("CombinedChart getLineData did not get data properly");
        }

        try{
            ScatterData scatterData = chart.getScatterData();
        }catch (Exception e){
            throw new AssertionError("CombinedChart getScatterData did not get data properly");
        }



    }

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

        List<BarEntry> barValues = new ArrayList<>();
        for (int index = 0; index < count; index++)
            barValues.add(new BarEntry(index + 0.5f, getRandom(15, 5)));

        BarDataSet barDataSet = new BarDataSet(barValues, "BarDataSet");
        BarData bar = new BarData(barDataSet);


        barDataSet.setColors(Color.rgb(61, 165, 255), Color.rgb(23, 197, 255));
        barDataSet.setValueTextColor(Color.rgb(61, 165, 255));
        barDataSet.setValueTextSize(10f);
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        return bar;
    }

    private ScatterData generateScatterData() {

        List<Entry> scatterValues = new ArrayList<>();

        for (int index = 0; index < count; index++)
            scatterValues.add(new Entry(index + 0.5f, getRandom(15,60)));


        ScatterDataSet scatterDataSet = new ScatterDataSet(scatterValues, "ScatterDataSet");
        ScatterData scatter = new ScatterData(scatterDataSet);


        scatterDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        scatterDataSet.setScatterShapeSize(8f);
        scatterDataSet.setDrawValues(true);
        scatterDataSet.setValueTextSize(10f);
        scatterDataSet.setValueTextColor(Color.rgb(240, 238, 70));

        scatterDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        return scatter;
    }



    private CandleData generateCandleData() {

        // Candle Data
        // CandleEntry(float x, float shadowH, float shadowL, float open, float close)
        // y = (shadowH + shadowL) / 2f
        List<CandleEntry> candleValues = new ArrayList<>();

        for (int index = 0; index < count; index++){
            float shadowL = getRandom(10, 70);
            float close = shadowL + getRandom(10, 0);
            float open = close + getRandom(10, 0);
            float shadowH = open + getRandom(10, 0);
            candleValues.add(new CandleEntry(index + 0.5f, shadowH, shadowL,open,close));
        }
        CandleDataSet candleDataSet = new CandleDataSet(candleValues, "CandleDataSet");
        CandleData candle = new CandleData(candleDataSet);

        candleDataSet.setDecreasingColor(Color.rgb(142, 150, 175));
        candleDataSet.setShadowColor(Color.DKGRAY);
        candleDataSet.setBarSpace(0.3f);
        candleDataSet.setValueTextSize(10f);
        candleDataSet.setDrawValues(false);
        return candle;
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

    @Override public void onBackPressed(){
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        if (i != null) startActivity(i);
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
