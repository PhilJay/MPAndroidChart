package com.example.mpchartexample;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;

public class MultiLineChartActivity extends Activity implements OnSeekBarChangeListener, OnChartValueSelectedListener {

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
		
		ColorTemplate ct = new ColorTemplate();
		
		// COLOR VARIATIONS
//		ct.addDataSetColors(ColorTemplate.COLORFUL_COLORS, this);
//		ct.addDataSetColors(ColorTemplate.JOYFUL_COLORS, this);
//		ct.addDataSetColors(ColorTemplate.GREEN_COLORS, this);
		
		// ONE COLOR PER DATASET
		ct.addColorsForDataSets(ColorTemplate.COLORFUL_COLORS, this);
		
		mChart.setColorTemplate(ct);
//		mChart.setDrawTopYLegendEntry(false);
		mChart.setOnChartValueSelectedListener(this);

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

		ArrayList<Double[]> values = new ArrayList<Double[]>();

		for (int z = 0; z < 3; z++) {
		    
		    Double[] vals = new Double[mSeekBarX.getProgress()];
		    
			for (int i = 0; i < mSeekBarX.getProgress(); i++) {
				float mult = (mSeekBarY.getProgress() + 1);
				double val = (Math.random() * mult * 0.1) + 3;// + (float) ((mult * 0.1) / 10);
				vals[i] = val;
			}
			
			values.add(vals);
		}

//		ArrayList<Entry> filtered = approximator.filter(yVals1);
//
//		for (int i = 0; i < filtered.size(); i++) {
//			filtered.get(i).setType(1);
//		}

		tvX.setText("" + (mSeekBarX.getProgress() + 1));
		tvY.setText("" + (mSeekBarY.getProgress() / 10));
		
        ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.addAll(DataSet.makeDataSets(values));

        ChartData data = new ChartData(xVals, dataSets);

		mChart.setData(data);
		mChart.invalidate();
	}
	
	@Override
	public void onValuesSelected(Entry[] values, Highlight[] highlights) {
	    Log.i("VALS SELECTED", "Value: " + values[0].getVal() + ", xIndex: " + highlights[0].getXIndex() + ", DataSet index: " + highlights[0].getDataSetIndex());
	}
	
	@Override
	public void onNothingSelected() {
	    // TODO Auto-generated method stub
	    
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}
