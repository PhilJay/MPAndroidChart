package com.example.mpchartexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.ColorTemplate;
import com.github.mikephil.charting.PieChart;

import java.util.ArrayList;

public class PieChartActivity extends Activity implements OnSeekBarChangeListener {

    private PieChart mChart; 
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_piechart);
        
        tvX = (TextView) findViewById(R.id.tvXMax);
        tvY = (TextView) findViewById(R.id.tvYMax);
        
        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);        
        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
        
        mSeekBarX.setProgress(10);
        mSeekBarY.setProgress(100);
        
        mSeekBarX.setOnSeekBarChangeListener(this);
        mSeekBarY.setOnSeekBarChangeListener(this);
        
        mChart = (PieChart) findViewById(R.id.chart1);
        mChart.setColorTemplate(new ColorTemplate(ColorTemplate.getColors(this, ColorTemplate.FRESH_COLORS)));
        
//        mChart.setDrawFilled(true);
//        mChart.setRoundedYLegend(false);
//        mChart.setStartAtZero(true);
        mChart.setDrawValues(false);
        mChart.setDrawCenterText(true);

//        mChart.setDrawAdditional(true);
//        mChart.setSpacePercent(20, 10);
//        mChart.setYLegendCount(5);
        mChart.setTouchEnabled(true);
        
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {
            xVals.add(Integer.toString(i - 1)); 
        }

        ArrayList<Float> yVals = new ArrayList<Float>();

        for (int i = 0; i < 5; i++) {
            float val = (float) (Math.random() * 20) + 5;
            yVals.add(val);
        }

        mChart.setDrawHoleEnabled(true);
        mChart.setData(xVals, yVals);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pie, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                if (mChart.isDrawValuesEnabled())
                    mChart.setDrawValues(false);
                else
                    mChart.setDrawValues(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHole: {
                if (mChart.isDrawHoleEnabled())
                    mChart.setDrawHoleEnabled(false);
                else
                    mChart.setDrawHoleEnabled(true);
                mChart.invalidate();
                break;
            } 
            case R.id.actionDrawCenter: {
                if (mChart.isDrawCenterTextEnabled())
                    mChart.setDrawCenterText(false);
                else
                    mChart.setDrawCenterText(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionSave: { 
//                mChart.saveToGallery("title"+System.currentTimeMillis());                
                mChart.saveToPath("title"+System.currentTimeMillis(), "");
                break;
            }
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
       
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 1; i <= mSeekBarX.getProgress()+1; i++) {
            xVals.add((i - 1)+"");
        }

        ArrayList<Float> yVals = new ArrayList<Float>();

        for (int i = 1; i <= mSeekBarX.getProgress()+1; i++) {
            float mult = (mSeekBarY.getProgress()+1);
            float val = (float) (Math.random() * mult);// + (float) ((mult * 0.1) / 10);
            yVals.add(val);
        }
        
        tvX.setText(""+ (mSeekBarX.getProgress() + 1));
        tvY.setText(""+ (mSeekBarY.getProgress() / 10));

        mChart.setData(xVals, yVals);
        mChart.setColorTemplate(new ColorTemplate(ColorTemplate.getColors(this, ColorTemplate.FRESH_COLORS)));
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
}
