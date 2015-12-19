package com.xxmassdeveloper.mpchartexample.realm;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.data.realm.implementation.RealmCandleData;
import com.github.mikephil.charting.data.realm.implementation.RealmCandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
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

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
    }

    @Override
    protected void onResume() {
        super.onResume(); // setup realm

        // write some demo-data into the realm.io database
        writeToDBCandle(50);

        // add data to the chart
        setData();
    }

    private void setData() {

        RealmResults<RealmDemoData> result = mRealm.allObjects(RealmDemoData.class);

        RealmCandleDataSet<RealmDemoData> set = new RealmCandleDataSet<RealmDemoData>(result, "high", "low", "open", "close", "xIndex");
        set.setLabel("Realm Realm CandleDataSet");
        set.setShadowColor(Color.DKGRAY);
        set.setShadowWidth(0.7f);
        set.setDecreasingColor(Color.RED);
        set.setDecreasingPaintStyle(Paint.Style.STROKE);
        set.setIncreasingColor(Color.rgb(122, 242, 84));
        set.setIncreasingPaintStyle(Paint.Style.FILL);

        ArrayList<ICandleDataSet> dataSets = new ArrayList<ICandleDataSet>();
        dataSets.add(set); // add the dataset

        // create a data object with the dataset list
        RealmCandleData data = new RealmCandleData(result, "xValue", dataSets);
        styleData(data);

        // set data
        mChart.setData(data);
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);
    }
}
