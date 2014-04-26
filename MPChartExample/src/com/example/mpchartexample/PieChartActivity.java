package com.example.mpchartexample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
        
        mSeekBarX.setOnSeekBarChangeListener(this);
        mSeekBarY.setOnSeekBarChangeListener(this);
        
        mChart = (PieChart) findViewById(R.id.chart1);
        mChart.setColorTemplate(new ColorTemplate(ColorTemplate.getColors(this, ColorTemplate.COLORFUL_COLORS)));
        
        mChart.setDrawValues(false);
        mChart.setDrawCenterText(true);

        mChart.setDescription("This is a description."); 
        mChart.setDrawHoleEnabled(true);
        
        mChart.setTouchEnabled(true);
        
        mSeekBarX.setProgress(10);
        mSeekBarY.setProgress(100);
        
//        float diameter = mChart.getDiameter();
//        float radius = mChart.getRadius();
//      
//        Log.i("Piechart", "diameter: " + diameter + ", radius: " + radius);
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
       
        ArrayList<Float> yVals = new ArrayList<Float>();

        for (int i = 0; i < mSeekBarX.getProgress(); i++) {
            float mult = (mSeekBarY.getProgress());
            float val = (float) (Math.random() * mult) + mult / 5;// + (float) ((mult * 0.1) / 10);
            yVals.add(val);
        }
        
        tvX.setText(""+ (mSeekBarX.getProgress()));
        tvY.setText(""+ (mSeekBarY.getProgress()));

        ArrayList<String> xVals = new ArrayList<String>();
        
        for(int i = 0; i < yVals.size(); i++) xVals.add(""+i);
        
        mChart.setData(xVals, yVals);
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
