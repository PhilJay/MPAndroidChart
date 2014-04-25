package com.example.mpchartexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.Chart;
import com.github.mikephil.charting.ColorTemplate;
import com.github.mikephil.charting.LineChart;

import java.util.ArrayList;

public class LineChartActivity extends Activity implements OnSeekBarChangeListener {

    private LineChart mChart; 
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_linechart);
        
        tvX = (TextView) findViewById(R.id.tvXMax);
        tvY = (TextView) findViewById(R.id.tvYMax);
        
        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarX.setOnSeekBarChangeListener(this);
        
        
        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
        mSeekBarY.setOnSeekBarChangeListener(this);
        
        mChart = (LineChart) findViewById(R.id.chart1);
//        mChart.setColorTemplate(new ColorTemplate(ColorTemplate.getColors(this, ColorTemplate.LIBERTY_COLORS)));
        
//        mChart.setDrawFilled(true);
//        mChart.setRoundedYLegend(false);
//        mChart.setStartAtZero(true);
        mChart.setDrawValues(false);
        mChart.setLineWidth(5f);
        mChart.setCircleSize(5f);
        mChart.setDrawAdditional(true);
//        mChart.setSpacePercent(20, 10);
        mChart.setYLegendCount(5);
        mChart.setTouchEnabled(true);
        
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 1; i <= 50; i++) {
            xVals.add(Integer.toString(i - 1)); 
        }

        ArrayList<Float> yVals = new ArrayList<Float>();

        for (int i = 1; i <= 50; i++) {
            float val = (float) (Math.random() * 10);
            yVals.add(val);
        }
        
        mSeekBarX.setProgress(50);
        mSeekBarY.setProgress(100);

        mChart.setData(xVals, yVals);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        switch (item.getItemId()) {
            case R.id.actionToggleRound: {
                if (mChart.isYLegendRounded())
                    mChart.setRoundedYLegend(false);
                else
                    mChart.setRoundedYLegend(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleValues: {
                if (mChart.isDrawValuesEnabled())
                    mChart.setDrawValues(false);
                else
                    mChart.setDrawValues(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleFilled: {
                if (mChart.isDrawFilledEnabled())
                    mChart.setDrawFilled(false);
                else
                    mChart.setDrawFilled(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleCircles: {
                if (mChart.isDrawAdditionalEnabled())
                    mChart.setDrawAdditional(false);
                else
                    mChart.setDrawAdditional(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleStartzero: { 
                if (mChart.isStartAtZeroEnabled())
                    mChart.setStartAtZero(false);
                else
                    mChart.setStartAtZero(true); 
                
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleAdjustXLegend: { 
//                if (mChart.isAdjustXLegendEnabled())
//                    mChart.setAdjustXLegend(false);
//                else
//                    mChart.setAdjustXLegend(true);
                
                mChart.invalidate();
                break;
            }          
            case R.id.actionSave: { 
//                mChart.saveToGallery("title"+System.currentTimeMillis());                
//                mChart.saveToPath("title"+System.currentTimeMillis(), "");
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
            float val = (float) (Math.random() * mult * 0.1) + 3;// + (float) ((mult * 0.1) / 10);
            yVals.add(val);
        }
        
        tvX.setText(""+ (mSeekBarX.getProgress() + 1));
        tvY.setText(""+ (mSeekBarY.getProgress() / 10));

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
