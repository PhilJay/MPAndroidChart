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
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.interfaces.OnDrawListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Utils;

public class DrawChartActivity extends Activity implements OnChartValueSelectedListener, OnDrawListener {

	private LineChart mChart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_draw_chart);

		// create a color template for one dataset with only one color
		ColorTemplate ct = new ColorTemplate();
		ct.addColorsForDataSets(ColorTemplate.COLORFUL_COLORS, this);

		mChart = (LineChart) findViewById(R.id.chart1);
		mChart.setOnChartValueSelectedListener(this);
		mChart.setColorTemplate(ct);
		mChart.setDrawingEnabled(true);

		mChart.setOnDrawListener(this);
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

		// highlight index 2 and 6 in dataset 0
		// mChart.highlightValues(new Highlight[] {new Highlight(2, 0), new
		// Highlight(6, 0)});
		mChart.setDragEnabled(true);
		mChart.setTouchEnabled(true);

		TextView textView = new TextView(this);
		textView.setVisibility(View.VISIBLE);
		textView.setBackgroundColor(Color.WHITE);
		textView.setPadding(15, 15, 15, 15);
		textView.setText("Marker View");

		mChart.setDrawMarkerView(true);
		mChart.setMarkerView(textView);

		initWithDummyData();
	}

	private void initWithDummyData() {
		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < 148; i++) {
			xVals.add((i) + "h");
		}

		ArrayList<Entry> yVals = new ArrayList<Entry>();

		// create a dataset and give it a type (0)
		DataSet set1 = new DataSet(yVals, 0);

		ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
		dataSets.add(set1); // add the datasets

		// create a data object with the datasets
		ChartData data = new ChartData(xVals, dataSets);

		mChart.setData(data);
		mChart.setYRange(10f, 40f);
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
	public void onValuesSelected(Entry[] values, Highlight[] highlights) {
		Log.i("VALS SELECTED", "Value: " + values[0].getVal() + ", xIndex: " + highlights[0].getXIndex()
				+ ", DataSet index: " + highlights[0].getDataSetIndex());
	}

	@Override
	public void onNothingSelected() {
	}

	@Override
	public void onEntryAdded(Entry entry) {
		Log.i(Chart.LOG_TAG, entry.toString());
	}

	@Override
	public void onDrawFinished(DataSet dataSet) {
		Log.i(Chart.LOG_TAG, dataSet.toString());
	}
}
