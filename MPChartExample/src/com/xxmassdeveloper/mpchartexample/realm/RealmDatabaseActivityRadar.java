package com.xxmassdeveloper.mpchartexample.realm;

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

    private RadarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_radarchart_noseekbar);

        mChart = (RadarChart) findViewById(R.id.chart1);
        setup(mChart);
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

        RealmResults<RealmDemoData> result = mRealm.allObjects(RealmDemoData.class);

        //RealmBarDataSet<RealmDemoData> set = new RealmBarDataSet<RealmDemoData>(result, "stackValues", "xIndex"); // normal entries
        RealmRadarDataSet<RealmDemoData> set = new RealmRadarDataSet<RealmDemoData>(result, "value", "xIndex"); // stacked entries
        set.setValueTextSize(9f);
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setLabel("Realm RadarDataSet");

        ArrayList<IRadarDataSet> dataSets = new ArrayList<IRadarDataSet>();
        dataSets.add(set); // add the dataset

        // create a data object with the dataset list
        RadarData data = new RadarData(result, "xValue", dataSets);
        styleData(data);

        // set data
        mChart.setData(data);
        mChart.animateY(1400);
    }
}
