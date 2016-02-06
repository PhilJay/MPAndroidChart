
package com.xxmassdeveloper.mpchartexample;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BarChartPositiveNegative extends DemoBase {

    protected BarChart mChart;
    private Typeface mTf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart_noseekbar);

        mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mChart = (BarChart) findViewById(R.id.chart1);
        mChart.setExtraTopOffset(-30f);
        mChart.setExtraBottomOffset(10f);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.setDescription("");

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setSpaceBetweenLabels(2);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setTextSize(12f);

        YAxis left = mChart.getAxisLeft();
        left.setDrawLabels(false);
        left.setStartAtZero(false);
        left.setSpaceTop(25f);
        left.setSpaceBottom(25f);
        left.setDrawAxisLine(false);
        left.setDrawGridLines(false);
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);

        // THIS IS THE ORIGINAL DATA YOU WANT TO PLOT
        List<Data> data = new ArrayList<>();
        data.add(new Data(0, -224.1f, "12-29"));
        data.add(new Data(1, 238.5f, "12-30"));
        data.add(new Data(2, 1280.1f, "12-31"));
        data.add(new Data(3, -442.3f, "01-01"));
        data.add(new Data(4, -2280.1f, "01-02"));

        setData(data);
    }

    private void setData(List<Data> dataList) {

        ArrayList<BarEntry> positiveValues = new ArrayList<BarEntry>();
        ArrayList<BarEntry> negativeValues = new ArrayList<BarEntry>();
        String[] dates = new String[dataList.size()];

        for (int i = 0; i < dataList.size(); i++) {

            Data d = dataList.get(i);
            BarEntry entry = new BarEntry(d.yValue, d.xIndex);

            if (d.yValue >= 0)
                positiveValues.add(entry);
            else
                negativeValues.add(entry);

            dates[i] = dataList.get(i).xAxisValue;
        }

        int green = Color.rgb(110, 190, 102);
        int red = Color.rgb(211, 74, 88);

        BarDataSet positive = new BarDataSet(positiveValues, "Positive");
        positive.setBarSpacePercent(35f);
        positive.setColor(red);
        positive.setValueTextColor(red);

        BarDataSet negative = new BarDataSet(negativeValues, "Negative");
        negative.setBarSpacePercent(35f);
        negative.setColor(green);
        negative.setValueTextColor(green);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(positive);
        dataSets.add(negative);

        BarData data = new BarData(dates, dataSets);
        data.setValueTextSize(12f);
        data.setValueTypeface(mTf);
        data.setValueFormatter(new ValueFormatter());

        mChart.setData(data);
        mChart.invalidate();
    }

    /**
     * Demo class representing data.
     */
    private class Data {

        public String xAxisValue;
        public float yValue;
        public int xIndex;

        public Data(int xIndex, float yValue, String xAxisValue) {
            this.xAxisValue = xAxisValue;
            this.yValue = yValue;
            this.xIndex = xIndex;
        }
    }

    private class ValueFormatter implements com.github.mikephil.charting.formatter.ValueFormatter {

        private DecimalFormat mFormat;

        public ValueFormatter() {
            mFormat = new DecimalFormat("######.0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }
    }
}
