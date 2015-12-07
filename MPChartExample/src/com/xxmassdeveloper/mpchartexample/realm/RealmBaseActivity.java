package com.xxmassdeveloper.mpchartexample.realm;

import android.graphics.Color;
import android.graphics.Typeface;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.formatter.DefaultYAxisValueFormatter;
import com.xxmassdeveloper.mpchartexample.custom.MyValueFormatter;
import com.xxmassdeveloper.mpchartexample.custom.MyYAxisValueFormatter;
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

            // if disabled, scaling can be done on x- and y-axis separately
            mChart.setPinchZoom(false);

            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
            leftAxis.setStartAtZero(false);
            leftAxis.setTypeface(mTf);
            leftAxis.setTextSize(8f);
            leftAxis.setTextColor(Color.DKGRAY);
            leftAxis.setValueFormatter(new MyYAxisValueFormatter());

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
        data.setValueFormatter(new MyValueFormatter());
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

            RealmDemoData d = new RealmDemoData(value, i, "" + i);
            mRealm.copyToRealm(d);
        }

        mRealm.commitTransaction();
    }

    protected void writeToDBStack(int objectCount) {

        mRealm.beginTransaction();

        mRealm.clear(RealmDemoData.class);

        for (int i = 0; i < objectCount; i++) {

            float val1 = 20f + (float) (Math.random() * 50.0);
            float val2 = 20f + (float) (Math.random() * 50.0);
            float val3 = 20f + (float) (Math.random() * 50.0);
            float[] stack = new float[]{val1, val2, val3};

            RealmDemoData d = new RealmDemoData(stack, i, "" + i);
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

            RealmDemoData d = new RealmDemoData(val + high, val - low, even ? val + open : val - open,
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

            RealmDemoData d = new RealmDemoData(value, i, size, "" + i);
            mRealm.copyToRealm(d);
        }

        mRealm.commitTransaction();
    }
}
