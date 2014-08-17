
package com.xxmassdeveloper.mpchartexample;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;

public class AnotherBarActivity extends DemoBase implements OnSeekBarChangeListener {

    private BarChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart);

        tvX = (TextView) findViewById(R.id.tvXMax);
        tvY = (TextView) findViewById(R.id.tvYMax);

        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarX.setOnSeekBarChangeListener(this);

        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
        mSeekBarY.setOnSeekBarChangeListener(this);

        mChart = (BarChart) findViewById(R.id.chart1);
        
        mChart.setDrawYValues(false);

        mChart.setUnit(" €");
        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // sets the number of digits for values inside the chart
        mChart.setValueDigits(2);

        // disable 3D
        mChart.set3DEnabled(false);
        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawBarShadow(false);
        
        mChart.setDrawVerticalGrid(false);
        mChart.setDrawHorizontalGrid(false);
        mChart.setDrawGridBackground(false);

        XLabels xLabels = mChart.getXLabels();
        xLabels.setPosition(XLabelPosition.BOTTOM);
        xLabels.setCenterXLabelText(true);
        xLabels.setSpaceBetweenLabels(0);

         mChart.setDrawYLabels(false);
         mChart.setDrawLegend(false);

        // setting data
        mSeekBarX.setProgress(10);
        mSeekBarY.setProgress(100);

//        Legend l = mChart.getLegend();
//        l.setPosition(LegendPosition.BELOW_CHART_CENTER);
//        l.setFormSize(8f);
//        l.setFormToTextSpace(4f);
//        l.setXEntrySpace(6f);

        // mChart.setDrawLegend(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                if (mChart.isDrawYValuesEnabled())
                    mChart.setDrawYValues(false);
                else
                    mChart.setDrawYValues(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggle3D: {
                if (mChart.is3DEnabled())
                    mChart.set3DEnabled(false);
                else
                    mChart.set3DEnabled(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (mChart.isHighlightEnabled())
                    mChart.setHighlightEnabled(false);
                else
                    mChart.setHighlightEnabled(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionTogglePinch: {
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                else
                    mChart.setPinchZoom(true);

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlightArrow: {
                if (mChart.isDrawHighlightArrowEnabled())
                    mChart.setDrawHighlightArrow(false);
                else
                    mChart.setDrawHighlightArrow(true);
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
                XLabels xLabels = mChart.getXLabels();

                if (xLabels.isAdjustXLabelsEnabled())
                    xLabels.setAdjustXLabels(false);
                else
                    xLabels.setAdjustXLabels(true);

                mChart.invalidate();
                break;
            }
            case R.id.animateX: {
                mChart.animateX(3000);
                break;
            }
            case R.id.animateY: {
                mChart.animateY(3000);
                break;
            }
            case R.id.animateXY: {

                mChart.animateXY(3000, 3000);
                break;
            }
            case R.id.actionToggleFilter: {

                Approximator a = new Approximator(ApproximatorType.DOUGLAS_PEUCKER, 25);

                if (!mChart.isFilteringEnabled()) {
                    mChart.enableFiltering(a);
                } else {
                    mChart.disableFiltering();
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionSave: {
                if (mChart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show();
                break;
            }
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText("" + (mSeekBarX.getProgress() + 1));
        tvY.setText("" + (mSeekBarY.getProgress()));

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < mSeekBarX.getProgress()+1; i++) {
            float mult = (mSeekBarY.getProgress() + 1);
            float val1 = (float) (Math.random() * mult) + mult / 3;
            yVals1.add(new Entry((int) val1, i));
        }
        
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < mSeekBarX.getProgress()+1; i++) {
            xVals.add((int) yVals1.get(i).getVal() + " " + mChart.getUnit());
        }

        BarDataSet set1 = new BarDataSet(yVals1, "Data Set");
        set1.setColors(ColorTemplate.createColors(getApplicationContext(),
                ColorTemplate.VORDIPLOM_COLORS));

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        mChart.setData(data);
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
