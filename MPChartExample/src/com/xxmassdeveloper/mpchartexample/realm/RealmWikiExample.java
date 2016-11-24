package com.xxmassdeveloper.mpchartexample.realm;

import android.os.Bundle;
import android.view.WindowManager;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.realm.implementation.RealmBarDataSet;
import com.github.mikephil.charting.data.realm.implementation.RealmLineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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

        lineChart.setExtraBottomOffset(5f);
        barChart.setExtraBottomOffset(5f);

        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setLabelCount(5);
        lineChart.getXAxis().setGranularity(1f);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setLabelCount(5);
        barChart.getXAxis().setGranularity(1f);
    }

    @Override
    protected void onResume() {
        super.onResume(); // setup realm

        mRealm.beginTransaction();

        // write some demo-data into the realm.io database
        Score score1 = new Score(100f, 0f, "Peter");
        mRealm.copyToRealm(score1);
        Score score2 = new Score(110f, 1f, "Lisa");
        mRealm.copyToRealm(score2);
        Score score3 = new Score(130f, 2f, "Dennis");
        mRealm.copyToRealm(score3);
        Score score4 = new Score(70f, 3f, "Luke");
        mRealm.copyToRealm(score4);
        Score score5 = new Score(80f, 4f, "Sarah");
        mRealm.copyToRealm(score5);

        mRealm.commitTransaction();

        // add data to the chart
        setData();
    }

    private void setData() {

        // LINE-CHART
        final RealmResults<Score> results = mRealm.where(Score.class).findAll();


        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return results.get((int) value).getPlayerName();
            }
        };

        lineChart.getXAxis().setValueFormatter(formatter);
        barChart.getXAxis().setValueFormatter(formatter);

        RealmLineDataSet<Score> lineDataSet = new RealmLineDataSet<Score>(results, "scoreNr", "totalScore");
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setLabel("Result Scores");
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setColor(ColorTemplate.rgb("#FF5722"));
        lineDataSet.setCircleColor(ColorTemplate.rgb("#FF5722"));
        lineDataSet.setLineWidth(1.8f);
        lineDataSet.setCircleRadius(3.6f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet);

        LineData lineData = new LineData(dataSets);
        styleData(lineData);

        // set data
        lineChart.setData(lineData);
        lineChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);


        // BAR-CHART
        RealmBarDataSet<Score> barDataSet = new RealmBarDataSet<Score>(results, "scoreNr", "totalScore");
        barDataSet.setColors(new int[]{ColorTemplate.rgb("#FF5722"), ColorTemplate.rgb("#03A9F4")});
        barDataSet.setLabel("Realm BarDataSet");

        ArrayList<IBarDataSet> barDataSets = new ArrayList<IBarDataSet>();
        barDataSets.add(barDataSet);

        BarData barData = new BarData(barDataSets);
        styleData(barData);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);
    }
}
