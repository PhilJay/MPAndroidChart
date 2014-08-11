
package com.xxmassdeveloper.mpchartexample;

import android.graphics.Typeface;
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
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.Legend.LegendPosition;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.github.mikephil.charting.utils.YLabels;
import com.github.mikephil.charting.utils.YLabels.YLabelPosition;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;

public class BarChartActivity extends DemoBase implements OnSeekBarChangeListener {

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

        // enable the drawing of values
        mChart.setDrawYValues(true);

        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // sets the number of digits for values inside the chart
        mChart.setValueDigits(2);

        // disable 3D
        mChart.set3DEnabled(false);
        mChart.setYLabelCount(5);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        mChart.setDrawBarShadow(true);

        mChart.setUnit(" €");

        // change the position of the y-labels
        YLabels yLabels = mChart.getYLabels();
        yLabels.setPosition(YLabelPosition.BOTH_SIDED);

        XLabels xLabels = mChart.getXLabels();
        xLabels.setPosition(XLabelPosition.TOP);

        // mChart.setDrawXLabels(false);

        mChart.setDrawGridBackground(false);
        mChart.setDrawHorizontalGrid(true);
        mChart.setDrawVerticalGrid(false);
        // mChart.setDrawYLabels(false);

        mChart.setDrawBorder(false);
        // mChart.setBorderPositions(new BorderPosition[] {BorderPosition.LEFT,
        // BorderPosition.RIGHT});

        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        XLabels xl = mChart.getXLabels();
        xl.setPosition(XLabelPosition.BOTTOM);
        xl.setCenterXLabelText(true);
        xl.setTypeface(tf);

        YLabels yl = mChart.getYLabels();
        yl.setTypeface(tf);
        
        mChart.setValueTypeface(tf);

        // setting data
        mSeekBarX.setProgress(12);
        mSeekBarY.setProgress(50);

        Legend l = mChart.getLegend();
        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);

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

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < mSeekBarX.getProgress(); i++) {
            xVals.add(mMonths[i % 12]);
        }

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < mSeekBarX.getProgress(); i++) {
            float mult = (mSeekBarY.getProgress() + 1);
            float val = (float) (Math.random() * mult) + 3;
            yVals1.add(new Entry(val, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setBarSpacePercent(35f);
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
