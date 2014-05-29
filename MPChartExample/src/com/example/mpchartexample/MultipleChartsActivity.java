package com.example.mpchartexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;

public class MultipleChartsActivity extends Activity {

    private LineChart mLineChart;
    private BarChart mBarChart, mBarChart3D;
    private PieChart mPieChart;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_multiplecharts);
        
//        mLineChart = (LineChart) findViewById(R.id.barChart1);
//        mBarChart = (BarChart) findViewById(R.id.barChart2);
//        mPieChart = (PieChart) findViewById(R.id.pieChart1);
//        mBarChart3D = (BarChart) findViewById(R.id.barChart4); 
//        
//        ArrayList<String> xvalsSmall = new ArrayList<String>();
//        ArrayList<String> xvalsLarge = new ArrayList<String>();
//        
//        ArrayList<Float> small = new ArrayList<Float>();
//        small.add(3f);
//        small.add(7f);
//        small.add(17f);
//        small.add(4f);
//        small.add(6f);
//        
//        for(int i = 0; i < small.size(); i++) {
//            xvalsSmall.add("Val"+i);
//        }
//        
//        ArrayList<Float> large = new ArrayList<Float>();
//        
//        for(int i = 0; i < 1000; i++) {
//            large.add((float) (Math.random() * 50));
//            xvalsLarge.add("Val"+i);
//        }
//        
//        mLineChart.setData(xvalsLarge, large);
//        mLineChart.setColorTemplate(new ColorTemplate(ColorTemplate.getColors(this, ColorTemplate.FRESH_COLORS)));
//        mLineChart.setDrawFilled(false);
//        mLineChart.setStartAtZero(false); 
////        mLineChart.setRoundedYLegend(true);
//        mLineChart.setYRange(-10f, 60f);
//        
//        mBarChart.setData(xvalsLarge, large);
//        mBarChart.setColorTemplate(new ColorTemplate(ColorTemplate.getColors(this, ColorTemplate.FRESH_COLORS)));
//        mBarChart.set3DEnabled(false);
//        mBarChart.setMaxVisibleValueCount(10);
//        mBarChart.setRoundedYLegend(true);
//        mBarChart.setDescription("");
//        
//        mPieChart.setData(xvalsSmall, small);
//        mPieChart.setColorTemplate(new ColorTemplate(ColorTemplate.getColors(this, ColorTemplate.FRESH_COLORS)));
////        mChart3.highlightValues(new int[] {0, 1, 2, 3, 4} );
//        mPieChart.setDrawYValues(true);
//        mPieChart.setDrawXValues(false);
//        
//        mBarChart3D.setData(xvalsLarge, large);
//        mBarChart3D.setColorTemplate(new ColorTemplate(ColorTemplate.getColors(this, ColorTemplate.LIBERTY_COLORS)));
//        mBarChart3D.setRoundedYLegend(true);
//        mBarChart3D.setDescription("Description.");
    }
}
