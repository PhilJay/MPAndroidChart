package com.xxmassdeveloper.mpchartexample.realm;

import android.os.Bundle;
import android.view.WindowManager;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.realm.implementation.RealmBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.xxmassdeveloper.mpchartexample.R;
import com.xxmassdeveloper.mpchartexample.custom.RealmDemoData;

import java.util.ArrayList;

import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public class RealmDatabaseActivityBubble extends RealmBaseActivity {

    private BubbleChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bubblechart_noseekbar);

        mChart = findViewById(R.id.chart1);
        setup(mChart);

        mChart.getXAxis().setDrawGridLines(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.setPinchZoom(true);
    }

    @Override
    protected void onResume() {
        super.onResume(); // setup realm

        // write some demo-data into the realm.io database
        writeToDBBubble(10);

        // add data to the chart
        setData();
    }

    private void setData() {

        RealmResults<RealmDemoData> result = mRealm.where(RealmDemoData.class).findAll();

        RealmBubbleDataSet<RealmDemoData> set = new RealmBubbleDataSet<>(result, "xValue", "yValue", "bubbleSize");
        set.setLabel("Realm BubbleDataSet");
        set.setColors(ColorTemplate.COLORFUL_COLORS, 110);

        ArrayList<IBubbleDataSet> dataSets = new ArrayList<>();
        dataSets.add(set); // add the dataset

        // create a data object with the dataset list
        BubbleData data = new BubbleData(dataSets);
        styleData(data);

        // set data
        mChart.setData(data);
        mChart.animateY(1400, Easing.EaseInOutQuart);
    }
}
