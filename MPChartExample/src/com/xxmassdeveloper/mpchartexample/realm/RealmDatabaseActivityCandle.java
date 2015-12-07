package com.xxmassdeveloper.mpchartexample.realm;

import android.os.Bundle;
import android.view.WindowManager;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.realm.implementation.RealmCandleDataSet;
import com.github.mikephil.charting.data.realm.implementation.RealmScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.xxmassdeveloper.mpchartexample.R;
import com.xxmassdeveloper.mpchartexample.custom.RealmDemoData;

import java.util.ArrayList;

import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public class RealmDatabaseActivityCandle extends RealmBaseActivity {

    private CandleStickChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_candlechart_noseekbar);

        mChart = (CandleStickChart) findViewById(R.id.chart1);
        setup(mChart);
    }

    @Override
    protected void onResume() {
        super.onResume(); // setup realm

        // write some demo-data into the realm.io database
        writeToDBCandle(200);

        // add data to the chart
        setData();
    }

    private void setData() {

        RealmResults<RealmDemoData> result = mRealm.allObjects(RealmDemoData.class);

        RealmCandleDataSet<RealmDemoData> set = new RealmCandleDataSet<RealmDemoData>(result, "high", "low", "open", "close", "xIndex");
        set.setValueTextSize(9f);
        set.setLabel("Realm Realm CandleDataSet");

        ArrayList<ICandleDataSet> dataSets = new ArrayList<ICandleDataSet>();
        dataSets.add(set); // add the dataset

        // create a data object with the dataset list
        CandleData data = new CandleData(result, "xValue", dataSets);

        // set data
        mChart.setData(data);
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);
    }
}
