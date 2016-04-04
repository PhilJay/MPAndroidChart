
package com.xxmassdeveloper.mpchartexample;

import android.os.Bundle;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.XAxisValue;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;

public class ScrollViewActivity extends DemoBase {

    private BarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scrollview);

        mChart = (BarChart) findViewById(R.id.chart1);

        mChart.setDescription("");

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setLabelsToSkip(0);
        xAxis.setDrawGridLines(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        
        mChart.getLegend().setEnabled(false);

        setData(10);
    }
    
    private void setData(int count) {
        
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        ArrayList<XAxisValue> xVals = new ArrayList<XAxisValue>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * count) + 15;
            yVals.add(new BarEntry((int) val, i));
            xVals.add(new XAxisValue(i, (int) val + ""));
        }

        BarDataSet set = new BarDataSet(yVals, "Data Set");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setDrawValues(false);

        BarData data = new BarData(xVals, set);

        mChart.setData(data);
        mChart.invalidate();
        mChart.animateY(800);
    }
}
