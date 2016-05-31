
package com.xxmassdeveloper.mpchartexample;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.XAxisValue;
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
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setExtraTopOffset(-30f);
        mChart.setExtraBottomOffset(10f);
        mChart.setExtraLeftOffset(70f);
        mChart.setExtraRightOffset(70f);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.setDescription("");

        // scaling can now only be done on xPx- and yPx-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setSpaceBetweenLabels(2);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setTextSize(13f);

        YAxis left = mChart.getAxisLeft();
        left.setDrawLabels(false);
        left.setStartAtZero(false);
        left.setSpaceTop(25f);
        left.setSpaceBottom(25f);
        left.setDrawAxisLine(false);
        left.setDrawGridLines(false);
        left.setDrawZeroLine(true); // draw a zero line
        left.setZeroLineColor(Color.GRAY);
        left.setZeroLineWidth(0.7f);
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

        ArrayList<BarEntry> values = new ArrayList<BarEntry>();
        List<Integer> colors = new ArrayList<Integer>();

        int green = Color.rgb(110, 190, 102);
        int red = Color.rgb(211, 74, 88);

        for (int i = 0; i < dataList.size(); i++) {

            Data d = dataList.get(i);
            BarEntry entry = new BarEntry(d.xValue, d.yValue);
            values.add(entry);

            // specific colors
            if (d.yValue >= 0)
                colors.add(red);
            else
                colors.add(green);
        }

        BarDataSet set;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set = (BarDataSet)mChart.getData().getDataSetByIndex(0);
            set.setYVals(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(values, "Values");
            set.setColors(colors);
            set.setValueTextColors(colors);

            BarData data = new BarData(set);
            data.setValueTextSize(13f);
            data.setValueTypeface(mTf);
            data.setValueFormatter(new ValueFormatter());
            data.setBarWidth(0.8f);

            mChart.setData(data);
            mChart.invalidate();
        }
    }

    /**
     * Demo class representing data.
     */
    private class Data {

        public String xAxisValue;
        public float yValue;
        public float xValue;

        public Data(float xValue, float yValue, String xAxisValue) {
            this.xAxisValue = xAxisValue;
            this.yValue = yValue;
            this.xValue = xValue;
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
