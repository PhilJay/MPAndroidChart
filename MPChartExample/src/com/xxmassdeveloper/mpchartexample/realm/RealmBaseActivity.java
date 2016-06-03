package com.xxmassdeveloper.mpchartexample.realm;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.xxmassdeveloper.mpchartexample.custom.RealmDemoData;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Philipp Jahoda on 05/11/15.
 */
public abstract class RealmBaseActivity extends DemoBase {

    protected Realm mRealm;

    protected Typeface mTf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Realm.io Examples");
    }

    protected void setup(Chart<?> chart) {

        mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        // no description text
        chart.setDescription("");
        chart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        chart.setTouchEnabled(true);

        if (chart instanceof BarLineChartBase) {

            BarLineChartBase mChart = (BarLineChartBase) chart;

            mChart.setDrawGridBackground(false);

            // enable scaling and dragging
            mChart.setDragEnabled(true);
            mChart.setScaleEnabled(true);

            // if disabled, scaling can be done on xPx- and yPx-axis separately
            mChart.setPinchZoom(false);

            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
            leftAxis.setTypeface(mTf);
            leftAxis.setTextSize(8f);
            leftAxis.setTextColor(Color.DKGRAY);
            leftAxis.setValueFormatter(new PercentFormatter());

            XAxis xAxis = mChart.getXAxis();
            xAxis.setTypeface(mTf);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(8f);
            xAxis.setTextColor(Color.DKGRAY);

            mChart.getAxisRight().setEnabled(false);
        }
    }

    protected void styleData(ChartData data) {
        data.setValueTypeface(mTf);
        data.setValueTextSize(8f);
        data.setValueTextColor(Color.DKGRAY);
        data.setValueFormatter(new PercentFormatter());
    }

    @Override
    protected void onResume() {
        super.onResume();

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("myrealm.realm")
                .build();

        Realm.deleteRealm(config);

        Realm.setDefaultConfiguration(config);

        mRealm = Realm.getInstance(config);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRealm.close();
    }

    protected void writeToDB(int objectCount) {

        mRealm.beginTransaction();

        mRealm.clear(RealmDemoData.class);

        for (int i = 0; i < objectCount; i++) {

            float value = 40f + (float) (Math.random() * 60f);

            RealmDemoData d = new RealmDemoData(i, value, i, "" + i);
            mRealm.copyToRealm(d);
        }

        mRealm.commitTransaction();
    }

    protected void writeToDBStack(int objectCount) {

        mRealm.beginTransaction();

        mRealm.clear(RealmDemoData.class);

        for (int i = 0; i < objectCount; i++) {

            float val1 = 34f + (float) (Math.random() * 12.0f);
            float val2 = 34f + (float) (Math.random() * 12.0f);
            float[] stack = new float[]{val1, val2, 100 - val1 - val2};

            RealmDemoData d = new RealmDemoData(i, stack, i, "" + i);
            mRealm.copyToRealm(d);
        }

        mRealm.commitTransaction();
    }

    protected void writeToDBCandle(int objectCount) {

        mRealm.beginTransaction();

        mRealm.clear(RealmDemoData.class);

        for (int i = 0; i < objectCount; i++) {

            float mult = 50;
            float val = (float) (Math.random() * 40) + mult;

            float high = (float) (Math.random() * 9) + 8f;
            float low = (float) (Math.random() * 9) + 8f;

            float open = (float) (Math.random() * 6) + 1f;
            float close = (float) (Math.random() * 6) + 1f;

            boolean even = i % 2 == 0;

            RealmDemoData d = new RealmDemoData(i, val + high, val - low, even ? val + open : val - open,
                    even ? val - close : val + close, i, i + "");

            mRealm.copyToRealm(d);
        }

        mRealm.commitTransaction();
    }

    protected void writeToDBBubble(int objectCount) {

        mRealm.beginTransaction();

        mRealm.clear(RealmDemoData.class);

        for (int i = 0; i < objectCount; i++) {

            float value = 30f + (float) (Math.random() * 100.0);
            float size = 15f + (float) (Math.random() * 20.0);

            RealmDemoData d = new RealmDemoData(i, value, size, "" + i);
            mRealm.copyToRealm(d);
        }

        mRealm.commitTransaction();
    }

    protected void writeToDBPie() {

        mRealm.beginTransaction();

        mRealm.clear(RealmDemoData.class);

        float value1 = 15f + (float) (Math.random() * 8f);
        float value2 = 15f + (float) (Math.random() * 8f);
        float value3 = 15f + (float) (Math.random() * 8f);
        float value4 = 15f + (float) (Math.random() * 8f);
        float value5 = 100f - value1 - value2 - value3 - value4;

        float[] values = new float[] { value1, value2, value3, value4, value5 };
        String[] xValues = new String[]{ "iOS", "Android", "WP 10", "BlackBerry", "Other"};

        for (int i = 0; i < values.length; i++) {
            RealmDemoData d = new RealmDemoData(i, values[i], i, xValues[i]);
            mRealm.copyToRealm(d);
        }

        mRealm.commitTransaction();
    }
}
