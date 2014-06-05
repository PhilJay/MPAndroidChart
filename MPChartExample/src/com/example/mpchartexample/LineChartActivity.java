package com.example.mpchartexample;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;

public class LineChartActivity extends Activity implements OnSeekBarChangeListener, OnChartValueSelectedListener {

	private LineChart mChart;
	private SeekBar mSeekBarX, mSeekBarY;
	private TextView tvX, tvY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_linechart);

		tvX = (TextView) findViewById(R.id.tvXMax);
		tvY = (TextView) findViewById(R.id.tvYMax);

		mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
		mSeekBarX.setOnSeekBarChangeListener(this);

		mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
		mSeekBarY.setOnSeekBarChangeListener(this);

		// create a color template for one dataset with only one color
		ColorTemplate ct = new ColorTemplate();
		ct.addColorsForDataSets(new int[] { R.color.colorful_1 }, this);

		mChart = (LineChart) findViewById(R.id.chart1);
		mChart.setOnChartValueSelectedListener(this);
		mChart.setColorTemplate(ct);

		// mChart.setDrawFilled(true);
		// mChart.setRoundedYLegend(false);
		// mChart.setStartAtZero(true);
		mChart.setDrawYValues(false);
		mChart.setLineWidth(5f);
		mChart.setCircleSize(5f);
		// mChart.setSpacePercent(20, 10);
		mChart.setYLegendCount(6);
		mChart.setTouchEnabled(true);
		mChart.setHighlightEnabled(true);
		mChart.setPinchZoom(true);

		// highlight index 2 and 6 in dataset 0
		// mChart.highlightValues(new Highlight[] {new Highlight(2, 0), new
		// Highlight(6, 0)});
		mChart.setDragEnabled(true);
		mChart.setTouchEnabled(true);


		// mChart.setOffsets(60, 25, 15, 15);

		mSeekBarX.setProgress(45);
		mSeekBarY.setProgress(100);
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
				mChart.setDrawFilled(false);
			else
				mChart.setDrawFilled(true);
			mChart.invalidate();
			break;
		}
		case R.id.actionToggleCircles: {
			if (mChart.isDrawCirclesEnabled())
				mChart.setDrawCircles(false);
			else
				mChart.setDrawCircles(true);
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
			if (mChart.isAdjustXLegendEnabled())
				mChart.setAdjustXLegend(false);
			else
				mChart.setAdjustXLegend(true);

			mChart.invalidate();
			break;
		}
		case R.id.actionToggleFilter:
			if (mChart.isFilterSet()) {
				mChart.setFilter(ApproximatorType.NONE, 0);
			} else {
				mChart.setFilter(ApproximatorType.DOUGLAS_PEUCKER, 2);
			}
			mChart.invalidate();
			break;
		case R.id.actionSave: {
			// mChart.saveToGallery("title"+System.currentTimeMillis());
			mChart.saveToPath("title" + System.currentTimeMillis(), "");
			break;
		}
		}
		return true;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < mSeekBarX.getProgress(); i++) {
			xVals.add((i) + "");
		}

		ArrayList<Entry> yVals = new ArrayList<Entry>();

		for (int i = 0; i < mSeekBarX.getProgress(); i++) {
			float mult = (mSeekBarY.getProgress() + 1);
			float val = (float) (Math.random() * mult * 1) + 3;// + (float)
																// ((mult *
																// 0.1) / 10);
			yVals.add(new Entry(val, i));
		}

		tvX.setText("" + (mSeekBarX.getProgress() + 1));
		tvY.setText("" + (mSeekBarY.getProgress() / 10));

		// create a dataset and give it a type (0)
		DataSet set1 = new DataSet(yVals, 0);

		ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
		dataSets.add(set1); // add the datasets

		// create a data object with the datasets
		ChartData data = new ChartData(xVals, dataSets);

		mChart.setData(data);
		mChart.invalidate();

		// Log.i("pixel for value", mChart.getPixelsForValues(10, 10).toString());
		// Log.i("value for touch", mChart.getValuesByTouchPoint(300, 300).toString());
	}

	@Override
	public void onValuesSelected(Entry[] values, Highlight[] highlights) {
		Log.i("VALS SELECTED", "Value: " + values[0].getVal() + ", xIndex: " + highlights[0].getXIndex()
				+ ", DataSet index: " + highlights[0].getDataSetIndex());
	}

	@Override
	public void onNothingSelected() {
		// TODO Auto-generated method stub

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
