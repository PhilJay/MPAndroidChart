
package com.xxmassdeveloper.mpchartexample;

import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;

public class PerformanceLineChart extends DemoBase implements OnSeekBarChangeListener {

    private LineChart mChart;
    private SeekBar mSeekBarValues;
    private TextView mTvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_performance_linechart);

        mTvCount = (TextView) findViewById(R.id.tvValueCount);
        mSeekBarValues = (SeekBar) findViewById(R.id.seekbarValues);
        mTvCount.setText("500");

        mSeekBarValues.setProgress(500);
        
        mSeekBarValues.setOnSeekBarChangeListener(this);

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // disable value highlighting
        mChart.setHighlightEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // enable/disable highlight indicators (the lines that indicate the
        // highlighted Entry)
        mChart.setHighlightIndicatorEnabled(false);
              
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setDrawAxisLine(false);

        // dont forget to refresh the drawing
        mChart.invalidate();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        mTvCount.setText("" + (mSeekBarValues.getProgress()));

        setData(mSeekBarValues.getProgress(), 500f);
       
        // redraw
        mChart.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 3;// + (float)
                                                           // ((mult *
                                                           // 0.1) / 10);
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        
        set1.setColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setDrawValues(false);
        set1.setDrawCircles(false);

        // create a data object with the datasets
        LineData data = new LineData(xVals, set1);

        // set data
        mChart.setData(data);
        
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setEnabled(false);
    }
}
