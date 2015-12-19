package com.xxmassdeveloper.mpchartexample.realm;

import android.os.Bundle;
import android.view.WindowManager;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.realm.implementation.RealmBarData;
import com.github.mikephil.charting.data.realm.implementation.RealmBarDataSet;
import com.github.mikephil.charting.data.realm.implementation.RealmLineData;
import com.github.mikephil.charting.data.realm.implementation.RealmLineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.xxmassdeveloper.mpchartexample.R;

import java.util.ArrayList;

import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 18/12/15.
 */
public class RealmWikiExample extends RealmBaseActivity {

    private LineChart lineChart;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_realm_wiki);

        lineChart = (LineChart) findViewById(R.id.lineChart);
        barChart = (BarChart) findViewById(R.id.barChart);
        setup(lineChart);
        setup(barChart);

        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
    }

    @Override
    protected void onResume() {
        super.onResume(); // setup realm

        mRealm.beginTransaction();

        // write some demo-data into the realm.io database
        Score score1 = new Score(100f, 0, "Peter");
        mRealm.copyToRealm(score1);
        Score score2 = new Score(110f, 1, "Lisa");
        mRealm.copyToRealm(score2);
        Score score3 = new Score(130f, 2, "Dennis");
        mRealm.copyToRealm(score3);
        Score score4 = new Score(70f, 3, "Luke");
        mRealm.copyToRealm(score4);
        Score score5 = new Score(80f, 4, "Sarah");
        mRealm.copyToRealm(score5);

        mRealm.commitTransaction();

        // add data to the chart
        setData();
    }

    private void setData() {

        // LINE-CHART
        RealmResults<Score> results = mRealm.allObjects(Score.class);

        RealmLineDataSet<Score> lineDataSet = new RealmLineDataSet<Score>(results, "totalScore", "scoreNr");
        lineDataSet.setDrawCubic(false);
        lineDataSet.setLabel("Realm LineDataSet");
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setColor(ColorTemplate.rgb("#FF5722"));
        lineDataSet.setCircleColor(ColorTemplate.rgb("#FF5722"));
        lineDataSet.setLineWidth(1.8f);
        lineDataSet.setCircleSize(3.6f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet);

        RealmLineData lineData = new RealmLineData(results, "playerName", dataSets);
        styleData(lineData);

        // set data
        lineChart.setData(lineData);
        lineChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);


        // BAR-CHART
        RealmBarDataSet<Score> barDataSet = new RealmBarDataSet<Score>(results, "totalScore", "scoreNr");
        barDataSet.setColors(new int[]{ColorTemplate.rgb("#FF5722"), ColorTemplate.rgb("#03A9F4")});
        barDataSet.setLabel("Realm BarDataSet");

        ArrayList<IBarDataSet> barDataSets = new ArrayList<IBarDataSet>();
        barDataSets.add(barDataSet);

        RealmBarData barData = new RealmBarData(results, "playerName", barDataSets);
        styleData(barData);

        barChart.setData(barData);
        barChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);
    }
}
