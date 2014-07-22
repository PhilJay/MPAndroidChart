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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;

public class PieChartActivity extends Activity implements OnSeekBarChangeListener, OnChartValueSelectedListener {

	private PieChart mChart;
	private SeekBar mSeekBarX, mSeekBarY;
	private TextView tvX, tvY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_piechart);

		tvX = (TextView) findViewById(R.id.tvXMax);
		tvY = (TextView) findViewById(R.id.tvYMax);

		mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
		mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);

		mSeekBarX.setOnSeekBarChangeListener(this);
		mSeekBarY.setOnSeekBarChangeListener(this);

		mChart = (PieChart) findViewById(R.id.chart1);
		
		ColorTemplate ct = new ColorTemplate();
	    
		ct.addDataSetColors(ColorTemplate.COLORFUL_COLORS, this);
		
		mChart.setColorTemplate(ct);

		mChart.setDrawYValues(true);
		mChart.setDrawCenterText(true);

		mChart.setDescription("This is a description."); 
		mChart.setDrawHoleEnabled(true);
		
		// draws the corresponding description value into the slice
		mChart.setDrawXValues(true);
		mChart.setTouchEnabled(true);
		
		// display percentage values
		mChart.setUsePercentValues(true);
		
		// add a selection listener
		mChart.setOnChartValueSelectedListener(this);

		mSeekBarX.setProgress(5);
		mSeekBarY.setProgress(100);
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
			if (mChart.isDrawYValuesEnabled())
				mChart.setDrawYValues(false);
			else
				mChart.setDrawYValues(true);
			mChart.invalidate();
			break;
		}
		case R.id.actionTogglePercent: {
			if (mChart.isUsePercentValuesEnabled())
				mChart.setUsePercentValues(false);
			else
				mChart.setUsePercentValues(true);
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
		case R.id.actionToggleXVals: {
			if (mChart.isDrawXValuesEnabled())
				mChart.setDrawXValues(false);
			else
				mChart.setDrawXValues(true);
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

        tvX.setText("" + (mSeekBarX.getProgress()));
        tvY.setText("" + (mSeekBarY.getProgress())); 

		ArrayList<Entry> yVals1 = new ArrayList<Entry>();
		
		// IMPORTANT: In a PieChart, no values (Entry) should have the same xIndex (even if from different DataSets), since no values can be drawn above each other.
		for (int i = 0; i < mSeekBarX.getProgress(); i++) {
			float mult = (mSeekBarY.getProgress());
			float val = (float) (Math.random() * mult) + mult / 5;// + (float) ((mult * 0.1) / 10);
			yVals1.add(new Entry(val, i));
		}

		ArrayList<String> xVals = new ArrayList<String>();

		for (int i = 0; i < mSeekBarX.getProgress(); i++)
			xVals.add("Text" + (i + 1));
		 
		DataSet set1 = new DataSet(yVals1, "DS 1");
		
        ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
        dataSets.add(set1);

        ChartData data = new ChartData(xVals, dataSets);
		mChart.setData(data);
		
		// undo all highlights
		mChart.highlightValues(null);
		
		// set a text for the chart center
		mChart.setCenterText("Total Value\n" + (int) mChart.getYValueSum() + "\n(all slices)");
		mChart.invalidate();
	}

	@Override
	public void onValuesSelected(Entry[] values, Highlight[] highs) {
	    
	    StringBuffer a = new StringBuffer();

        for (int i = 0; i < values.length; i++)
            a.append("val: " + values[i].getVal() + ", x-ind: " + highs[i].getXIndex() + ", dataset-ind: " + highs[i].getDataSetIndex() + "\n");

        Log.i("PieChart", "Selected: " + a.toString());
	}

	@Override
	public void onNothingSelected() {
		Log.i("PieChart", "nothing selected");
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
