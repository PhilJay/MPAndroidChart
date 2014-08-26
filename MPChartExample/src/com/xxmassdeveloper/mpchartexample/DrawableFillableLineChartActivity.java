package com.xxmassdeveloper.mpchartexample;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.DrawableFillableLineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.XLabels;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;

public class DrawableFillableLineChartActivity extends DemoBase implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    private DrawableFillableLineChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_drawable_fillable_line_chart);

        mChart = (DrawableFillableLineChart) findViewById(R.id.drawable_fillable_line_chart);

        tvX = (TextView) findViewById(R.id.tvXMax);
        tvY = (TextView) findViewById(R.id.tvYMax);

        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);

        mSeekBarX.setProgress(45);
        mSeekBarY.setProgress(100);

        mSeekBarY.setOnSeekBarChangeListener(this);
        mSeekBarX.setOnSeekBarChangeListener(this);

        ColorTemplate colorTemplate = new ColorTemplate();
        colorTemplate.addDataSetColors(new int[] {R.color.mono_5}, this);

        mChart.setOnChartValueSelectedListener(this);
        mChart.setColorTemplate(colorTemplate);

        /*
        * The drawable is used for below the chart line, the color is used above the line.
        *
        * In order to get a smooth gradient, fade into the background color and not into Color.Transparent
        */
//        mChart.enableChartFill(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
//                                                    new int[] {getBaseContext().getResources().getColor(R.color.gradient), Color.WHITE}),
//                               Color.WHITE);

        mChart.enableChartFill(new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                        new int[] {getBaseContext().getResources().getColor(R.color.gradient), Color.WHITE}),
                Color.WHITE);

        mChart.setFillInverted(true);

        //Clear Fill
        //mChart.disableChartFill();

        //Use Image As Fill
        //mChart.enableChartFill(getBaseContext().getResources().getDrawable(R.drawable.ic_launcher),
        //                       Color.WHITE);

        setChartData(25, 10);
        mChart.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line, menu);
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
            case R.id.actionToggleHighlight: {
                if (mChart.isHighlightEnabled())
                    mChart.setHighlightEnabled(false);
                else
                    mChart.setHighlightEnabled(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleFilled: {
                if (mChart.isDrawFilledEnabled())
                    mChart.disableChartFill();
                else
                    mChart.enableChartFill(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                    new int[] {getBaseContext().getResources().getColor(R.color.gradient), Color.WHITE}),
                            Color.WHITE);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleCircles: {
                if (mChart.isDrawPointsEnabled())
                    mChart.setDrawPointsEnabled(false);
                else
                    mChart.setDrawPointsEnabled(true);
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
            case R.id.actionTogglePinch: {
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                else
                    mChart.setPinchZoom(true);

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
                // the angle of filtering is 35°
                Approximator a = new Approximator(Approximator.ApproximatorType.DOUGLAS_PEUCKER, 35);

                if (!mChart.isFilteringEnabled()) {
                    mChart.enableFiltering(a);
                } else {
                    mChart.disableFiltering();
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionDashedLine: {
                if (!mChart.isDashedLineEnabled()) {
                    mChart.enableDashedLine(10f, 5f, 0f);
                } else {
                    mChart.disableDashedLine();
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionSave: {
                if(mChart.saveToPath("title" + System.currentTimeMillis(), "")) {
                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.actionInvertFill: {
                mChart.setFillInverted(!mChart.isFillInverted());
                mChart.invalidate();
                break;
            }
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvX.setText("" + (mSeekBarX.getProgress() + 1));
        tvY.setText("" + (mSeekBarY.getProgress()));

        setChartData(mSeekBarX.getProgress()+1, mSeekBarY.getProgress());

        mChart.invalidate();
    }

    @Override
    public void onValuesSelected(Entry[] values, Highlight[] highlights) {
    }

    @Override
    public void onNothingSelected() {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private void setChartData(int numOfEntries, float valuesRange) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < numOfEntries; i++) {
            xVals.add(String.valueOf(i));
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < numOfEntries; i++) {
            float mult = (valuesRange + 1);
            float val = (float) (Math.random() * mult) + 3;

            yVals.add(new Entry(val, i));
        }

        DataSet set1 = new DataSet(yVals, "DataSet 1");

        ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.add(set1);

        ChartData data = new ChartData(xVals, dataSets);

        mChart.setData(data);
    }
}
