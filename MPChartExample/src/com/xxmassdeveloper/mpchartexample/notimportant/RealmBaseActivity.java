package com.xxmassdeveloper.mpchartexample.notimportant;

import android.graphics.Typeface;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.YAxis;
import com.xxmassdeveloper.mpchartexample.custom.RealmDemoData;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Philipp Jahoda on 05/11/15.
 */
public abstract class RealmBaseActivity extends DemoBase {

    protected Realm mRealm;

    protected void setup(BarLineChartBase mChart) {

        mChart.setDrawGridBackground(false);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(220f);
        leftAxis.setAxisMinValue(-50f);
        leftAxis.setStartAtZero(false);
        leftAxis.setTypeface(tf);

        mChart.getXAxis().setTypeface(tf);

        mChart.getAxisRight().setEnabled(false);
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

        for(int i = 0; i < objectCount; i++) {

            RealmDemoData d = new RealmDemoData(30f + (float) (Math.random() * 100.0), i, "" + i);
            mRealm.copyToRealm(d);
        }

        mRealm.commitTransaction();
    }

    protected void writeToDBStack(int objectCount) {

        mRealm.beginTransaction();

        mRealm.clear(RealmDemoData.class);

        for(int i = 0; i < objectCount; i++) {

            float val1 = 20f + (float) (Math.random() * 50.0);
            float val2 = 20f + (float) (Math.random() * 50.0);
            float val3 = 20f + (float) (Math.random() * 50.0);
            float [] stack = new float[] {val1, val2, val3};

            RealmDemoData d = new RealmDemoData(stack, i, "" + i);
            mRealm.copyToRealm(d);
        }

        mRealm.commitTransaction();
    }
}
