package com.xxmassdeveloper.mpchartexample;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.xxmassdeveloper.mpchartexample.custom.MyValueFormatter;
import com.xxmassdeveloper.mpchartexample.custom.MyYAxisValueFormatter;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

public class StackedBarActivity extends DemoBase implements OnSeekBarChangeListener, OnChartValueSelectedListener {

	private BarChart mChart;
	private SeekBar mSeekBarX, mSeekBarY;
	private TextView tvX, tvY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_barchart);

		tvX = (TextView) findViewById(R.id.tvXMax);
		tvY = (TextView) findViewById(R.id.tvYMax);

		mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
		mSeekBarX.setOnSeekBarChangeListener(this);

		mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
		mSeekBarY.setOnSeekBarChangeListener(this);

		mChart = (BarChart) findViewById(R.id.chart1);
		mChart.setOnChartValueSelectedListener(this);

		mChart.setDescription("");

		// if more than 60 entries are displayed in the chart, no values will be
		// drawn
		mChart.setMaxVisibleValueCount(60);

		// scaling can now only be done on x- and y-axis separately
		mChart.setPinchZoom(false);

		mChart.setDrawGridBackground(false);
		mChart.setDrawBarShadow(false);

		mChart.setDrawValueAboveBar(false);

		// change the position of the y-labels
		YAxis yLabels = mChart.getAxisLeft();
		yLabels.setValueFormatter(new MyYAxisValueFormatter());
		mChart.getAxisRight().setEnabled(false);

		XAxis xLabels = mChart.getXAxis();
		xLabels.setPosition(XAxisPosition.TOP);

		// mChart.setDrawXLabels(false);
		// mChart.setDrawYLabels(false);

		// setting data
		mSeekBarX.setProgress(12);
		mSeekBarY.setProgress(100);

		Legend l = mChart.getLegend();
		l.setPosition(LegendPosition.BELOW_CHART_RIGHT);
		l.setFormSize(8f);
		l.setFormToTextSpace(4f);
		l.setXEntrySpace(6f);

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
			for (DataSet<?> set : mChart.getData().getDataSets())
				set.setDrawValues(!set.isDrawValuesEnabled());

			mChart.invalidate();
			break;
		}
		case R.id.actionToggleHighlight: {
			if(mChart.getData() != null) {
				mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
				mChart.invalidate();
			}
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
		case R.id.actionToggleAutoScaleMinMax: {
			mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
			mChart.notifyDataSetChanged();
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
			mChart.getAxisLeft().setStartAtZero(!mChart.getAxisLeft().isStartAtZeroEnabled());
			mChart.getAxisRight().setStartAtZero(!mChart.getAxisRight().isStartAtZeroEnabled());
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
				Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!", Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT).show();
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
		for (int i = 0; i < mSeekBarX.getProgress() + 1; i++) {
			xVals.add(mMonths[i % mMonths.length]);
		}

		ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

		for (int i = 0; i < mSeekBarX.getProgress() + 1; i++) {
			float mult = (mSeekBarY.getProgress() + 1);
			float val1 = (float) (Math.random() * mult) + mult / 3;
			float val2 = (float) (Math.random() * mult) + mult / 3;
			float val3 = (float) (Math.random() * mult) + mult / 3;

			yVals1.add(new BarEntry(new float[] { val1, val2, val3 }, i));
		}

		BarDataSet set1 = new BarDataSet(yVals1, "Statistics Vienna 2014");
		set1.setColors(getColors());
		set1.setStackLabels(new String[] { "Births", "Divorces", "Marriages" });

		ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
		dataSets.add(set1);

		BarData data = new BarData(xVals, dataSets);
		data.setValueFormatter(new MyValueFormatter());

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

	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

		BarEntry entry = (BarEntry) e;

		if (entry.getVals() != null)
			Log.i("VAL SELECTED", "Value: " + entry.getVals()[h.getStackIndex()]);
		else
			Log.i("VAL SELECTED", "Value: " + entry.getVal());
	}

	@Override
	public void onNothingSelected() {
		// TODO Auto-generated method stub

	}

	private int[] getColors() {

		int stacksize = 3;

		// have as many colors as stack-values per entry
		int[] colors = new int[stacksize];

		for (int i = 0; i < stacksize; i++) {
			colors[i] = ColorTemplate.VORDIPLOM_COLORS[i];
		}

		return colors;
	}
}
