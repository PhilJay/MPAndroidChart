package com.xxmassdeveloper.mpchartexample.agiplan;

import android.os.Bundle;
import android.app.Activity;

import com.github.mikephil.charting.charts.PieChart;
import com.xxmassdeveloper.mpchartexample.R;

public class InvestmentChartActivity extends Activity {

    private PieChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investment_chart);

        mChart = (PieChart) findViewById(R.id.investment_chart);
        InvestmentChart.loadChart(mChart, getBaseContext());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        InvestmentChart.destroyChart();
    }

}
