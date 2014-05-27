package com.example.mpchartexample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.ChartData;
import com.github.mikephil.charting.DataSet;
import com.github.mikephil.charting.LineChart;
import com.github.mikephil.charting.Series;

import java.util.ArrayList;

public class LineChartActivity extends Activity implements OnSeekBarChangeListener {

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

		mChart = (LineChart) findViewById(R.id.chart1);
		// mChart.setColorTemplate(new ColorTemplate(ColorTemplate.getColors(this, ColorTemplate.LIBERTY_COLORS)));

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
		// mChart.highlightValues(new int[] {2, 6});
		mChart.setDragEnabled(true);
		mChart.setTouchEnabled(true);

		TextView textView = new TextView(this);
		textView.setVisibility(View.VISIBLE);
		textView.setBackgroundColor(Color.WHITE);
		textView.setPadding(15, 15, 15, 15);
		textView.setText("Marker View");

		mChart.setDrawMarkerView(true);
		mChart.setMarkerView(textView);

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
		case R.id.actionToggleRound: {
			if (mChart.isYLegendRounded())
				mChart.setRoundedYLegend(false);
			else
				mChart.setRoundedYLegend(true);
			mChart.invalidate();
			break;
		}
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

		ArrayList<Series> yVals = new ArrayList<Series>();

		for (int i = 0; i < mSeekBarX.getProgress(); i++) {
			float mult = (mSeekBarY.getProgress() + 1);
			float val = (float) (Math.random() * mult * 0.1) + 3;// + (float) ((mult * 0.1) / 10);
			yVals.add(new Series(val, 0, i));
		}

		tvX.setText("" + (mSeekBarX.getProgress() + 1));
		tvY.setText("" + (mSeekBarY.getProgress() / 10));
		
		DataSet set = new DataSet(yVals, 0);
		ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
		dataSets.add(set);

		ChartData data = new ChartData(xVals, dataSets);

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
