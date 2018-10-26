package com.xxmassdeveloper.mpchartexample.realm;

import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.realm.implementation.RealmRadarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.xxmassdeveloper.mpchartexample.R;
import com.xxmassdeveloper.mpchartexample.custom.RealmDemoData;

import java.util.ArrayList;

import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public class RealmDatabaseActivityRadar extends RealmBaseActivity {

    private RadarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_radarchart);

        chart = findViewById(R.id.chart1);
        setup(chart);

        chart.getYAxis().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.setWebAlpha(180);
        chart.setWebColorInner(Color.DKGRAY);
        chart.setWebColor(Color.GRAY);
    }

    @Override
    protected void onResume() {
        super.onResume(); // setup realm

        // write some demo-data into the realm.io database
        writeToDB(7);

        // add data to the chart
        setData();
    }

    private void setData() {

        RealmResults<RealmDemoData> result = mRealm.where(RealmDemoData.class).findAll();

        //RealmBarDataSet<RealmDemoData> set = new RealmBarDataSet<>(result, "stackValues", "xIndex"); // normal entries
        RealmRadarDataSet<RealmDemoData> set = new RealmRadarDataSet<>(result, "yValue"); // stacked entries
        set.setLabel("Realm RadarDataSet");
        set.setDrawFilled(true);
        set.setColor(ColorTemplate.rgb("#009688"));
        set.setFillColor(ColorTemplate.rgb("#009688"));
        set.setFillAlpha(130);
        set.setLineWidth(2f);

        ArrayList<IRadarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set); // add the dataset

        // create a data object with the dataset list
        RadarData data = new RadarData(dataSets);
        styleData(data);

        // set data
        chart.setData(data);
        chart.animateY(1400);
    }
}
