package com.example.mpchartexample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.interfaces.OnDrawListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;

import java.util.ArrayList;

/**
 * This Activity demonstrates drawing into the Chart with the finger. Both line, bar and scatter charts can be used for
 * drawing.
 * 
 * @author Philipp Jahoda
 */
public class DrawChartActivity extends Activity implements OnChartValueSelectedListener, OnDrawListener {

	private LineChart mChart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_draw_chart);

		mChart = (LineChart) findViewById(R.id.chart1);

		// create a color template, one color per dataset
		ColorTemplate ct = new ColorTemplate();
		ct.addColorsForDataSets(ColorTemplate.COLORFUL_COLORS, this);
		mChart.setColorTemplate(ct);

		// listener for selecting and drawing
		mChart.setOnChartValueSelectedListener(this);
		mChart.setOnDrawListener(this);

		// enable drawing with the finger
		mChart.setDrawingEnabled(true);

		// enable dragging and scaling
		mChart.setDragEnabled(true);

		mChart.setDrawYValues(false);
		mChart.setLineWidth(5f);
		mChart.setCircleSize(5f);
		mChart.setYLabelCount(6);
		mChart.setHighlightEnabled(true);

		// if disabled, drawn datasets with the finger will not be automatically finished
		mChart.setAutoFinish(false);

		// add data to the chart
		initWithDummyData();

		mChart.setYRange(-40f, 40f, true);
		// call this to reset the changed y-range
		// mChart.resetYRange(true);
	}

	private void initWithDummyData() {
		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < 24; i++) {
			xVals.add((i) + "h");
		}

		ArrayList<Entry> yVals = new ArrayList<Entry>();

		// create a dataset and give it a type (0)
		DataSet set1 = new DataSet(yVals, "DataSet");

		ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
		dataSets.add(set1); // add the datasets

		// create a data object with the datasets
		ChartData data = new ChartData(xVals, dataSets);

		mChart.setData(data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.draw, menu);
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
            if (mChart.isAdjustXLabelsEnabled())
                mChart.setAdjustXLabels(false);
            else
                mChart.setAdjustXLabels(true);

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

	/** callback for each new entry drawn with the finger */
	@Override
	public void onEntryAdded(Entry entry) {
		Log.i(Chart.LOG_TAG, entry.toString());
	}

	/** callback when a DataSet has been drawn (when lifting the finger) */
	@Override
	public void onDrawFinished(DataSet dataSet) {
		Log.i(Chart.LOG_TAG, "DataSet drawn. " + dataSet.toSimpleString());
	}

	@Override
	public void onEntryMoved(Entry entry) {
		Log.i(Chart.LOG_TAG, "Point moved " + entry.toString());
	}
}
