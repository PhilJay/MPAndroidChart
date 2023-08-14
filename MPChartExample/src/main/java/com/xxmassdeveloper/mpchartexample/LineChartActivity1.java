package com.xxmassdeveloper.mpchartexample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Example of a heavily customized {@link LineChart} with limit lines, custom line shapes, etc.
 */
public class LineChartActivity1 extends DemoBase implements OnSeekBarChangeListener, OnChartValueSelectedListener {

	private LineChart chart1;
	private SeekBar seekBarX, seekBarY;
	private TextView tvXMax, tvYMax;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_linechart);

		setTitle("LineChartActivity1");

		tvXMax = findViewById(R.id.tvXMax);
		tvYMax = findViewById(R.id.tvYMax);

		seekBarX = findViewById(R.id.seekBarX);
		seekBarX.setOnSeekBarChangeListener(this);

		seekBarY = findViewById(R.id.seekBarY);
		seekBarY.setMax(180);
		seekBarY.setOnSeekBarChangeListener(this);

		chart1 = findViewById(R.id.chart1);

		// background color
		chart1.setBackgroundColor(Color.WHITE);

		// disable description text
		chart1.getDescription().setEnabled(false);

		// enable touch gestures
		chart1.setTouchEnabled(true);

		// set listeners
		chart1.setOnChartValueSelectedListener(this);
		chart1.setDrawGridBackground(false);

		// create marker to display box when values are selected
		MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

		// Set the marker to the chart
		mv.setChartView(chart1);
		chart1.setMarker(mv);

		// enable scaling and dragging
		chart1.setDragEnabled(true);
		chart1.setScaleEnabled(true);

		// force pinch zoom along both axis
		chart1.setPinchZoom(true);

		XAxis xAxis;
		xAxis = chart1.getXAxis();

		// vertical grid lines
		xAxis.enableGridDashedLine(10f, 10f, 0f);

		YAxis yAxis;
		yAxis = chart1.getAxisLeft();

		// disable dual axis (only use LEFT axis)
		chart1.getAxisRight().setEnabled(false);

		// horizontal grid lines
		yAxis.enableGridDashedLine(10f, 10f, 0f);

		// axis range
		yAxis.setAxisMaximum(200f);
		yAxis.setAxisMinimum(-50f);

		LimitLine llXAxis = new LimitLine(9f, "Index 10");
		llXAxis.setLineWidth(4f);
		llXAxis.enableDashedLine(10f, 10f, 0f);
		llXAxis.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
		llXAxis.setTextSize(10f);
		llXAxis.setTypeface(tfRegular);

		LimitLine ll1 = new LimitLine(150f, "Upper Limit");
		ll1.setLineWidth(4f);
		ll1.enableDashedLine(10f, 10f, 0f);
		ll1.setLabelPosition(LimitLabelPosition.RIGHT_TOP);
		ll1.setTextSize(10f);
		ll1.setTypeface(tfRegular);

		LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
		ll2.setLineWidth(4f);
		ll2.enableDashedLine(10f, 10f, 0f);
		ll2.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
		ll2.setTextSize(10f);
		ll2.setTypeface(tfRegular);

		// draw limit lines behind data instead of on top
		yAxis.setDrawLimitLinesBehindData(true);
		xAxis.setDrawLimitLinesBehindData(true);

		// add limit lines
		yAxis.addLimitLine(ll1);
		yAxis.addLimitLine(ll2);
		//xAxis.addLimitLine(llXAxis);

		// add data
		seekBarX.setProgress(45);
		seekBarY.setProgress(180);
		setData(45, 180);

		// draw points over time
		chart1.animateX(1500);

		// get the legend (only possible after setting data)
		Legend l = chart1.getLegend();

		// draw legend entries as lines
		l.setForm(LegendForm.LINE);
	}

	private void setData(int count, float range) {

		ArrayList<Entry> values = new ArrayList<>();

		for (int i = 0; i < count; i++) {

			float val = (float) (Math.random() * range) - 30;
			values.add(new Entry(i, val, ContextCompat.getDrawable(this, R.drawable.star)));
		}

		LineDataSet lineDataSet0;

		if (chart1.getData() != null && chart1.getData().getDataSetCount() > 0) {
			lineDataSet0 = (LineDataSet) chart1.getData().getDataSetByIndex(0);
			lineDataSet0.setValues(values);
			lineDataSet0.notifyDataSetChanged();
			chart1.getData().notifyDataChanged();
			chart1.notifyDataSetChanged();
		} else {
			// create a dataset and give it a type
			lineDataSet0 = new LineDataSet(values, "DataSet 1");

			lineDataSet0.setDrawIcons(false);

			// draw dashed line
			lineDataSet0.enableDashedLine(10f, 5f, 0f);

			// black lines and points
			lineDataSet0.setColor(Color.BLACK);
			lineDataSet0.setCircleColor(Color.BLACK);

			// line thickness and point size
			lineDataSet0.setLineWidth(1f);
			lineDataSet0.setCircleRadius(3f);

			// draw points as solid circles
			lineDataSet0.setDrawCircleHole(false);

			// customize legend entry
			lineDataSet0.setFormLineWidth(1f);
			lineDataSet0.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
			lineDataSet0.setFormSize(15.f);

			// text size of values
			lineDataSet0.setValueTextSize(9f);

			// draw selection line as dashed
			lineDataSet0.enableDashedHighlightLine(10f, 5f, 0f);

			// set the filled area
			lineDataSet0.setDrawFilled(true);
			lineDataSet0.setFillFormatter((dataSet, dataProvider) -> chart1.getAxisLeft().getAxisMinimum());

			// set color of filled area
			if (Utils.getSDKInt() >= 18) {
				// drawables only supported on api level 18 and above
				Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
				lineDataSet0.setFillDrawable(drawable);
			} else {
				lineDataSet0.setFillColor(Color.BLACK);
			}

			ArrayList<ILineDataSet> dataSets = new ArrayList<>();
			dataSets.add(lineDataSet0); // add the data sets

			// create a data object with the data sets
			LineData data = new LineData(dataSets);

			// set data
			chart1.setData(data);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.line, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.viewGithub -> {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/LineChartActivity1.java"));
				startActivity(i);
			}
			case R.id.actionToggleValues -> {
				List<ILineDataSet> sets = chart1.getData().getDataSets();

				for (ILineDataSet iSet : sets) {

					LineDataSet set = (LineDataSet) iSet;
					set.setDrawValues(!set.isDrawValuesEnabled());
				}

				chart1.invalidate();
			}
			case R.id.actionToggleIcons -> {
				List<ILineDataSet> sets = chart1.getData().getDataSets();

				for (ILineDataSet iSet : sets) {

					LineDataSet set = (LineDataSet) iSet;
					set.setDrawIcons(!set.isDrawIconsEnabled());
				}

				chart1.invalidate();
			}
			case R.id.actionToggleHighlight -> {
				if (chart1.getData() != null) {
					chart1.getData().setHighlightEnabled(!chart1.getData().isHighlightEnabled());
					chart1.invalidate();
				}
			}
			case R.id.actionToggleFilled -> {

				List<ILineDataSet> sets = chart1.getData().getDataSets();

				for (ILineDataSet iSet : sets) {

					LineDataSet set = (LineDataSet) iSet;
					set.setDrawFilled(!set.isDrawFilledEnabled());
				}
				chart1.invalidate();
			}
			case R.id.actionToggleCircles -> {
				List<ILineDataSet> sets = chart1.getData().getDataSets();

				for (ILineDataSet iSet : sets) {

					LineDataSet set = (LineDataSet) iSet;
					set.setDrawCircles(!set.isDrawCirclesEnabled());
				}
				chart1.invalidate();
			}
			case R.id.actionToggleCubic -> {
				List<ILineDataSet> sets = chart1.getData().getDataSets();

				for (ILineDataSet iSet : sets) {

					LineDataSet set = (LineDataSet) iSet;
					set.setMode(set.getMode() == LineDataSet.Mode.CUBIC_BEZIER ? LineDataSet.Mode.LINEAR : LineDataSet.Mode.CUBIC_BEZIER);
				}
				chart1.invalidate();
			}
			case R.id.actionToggleStepped -> {
				List<ILineDataSet> sets = chart1.getData().getDataSets();

				for (ILineDataSet iSet : sets) {

					LineDataSet set = (LineDataSet) iSet;
					set.setMode(set.getMode() == LineDataSet.Mode.STEPPED ? LineDataSet.Mode.LINEAR : LineDataSet.Mode.STEPPED);
				}
				chart1.invalidate();
			}
			case R.id.actionToggleHorizontalCubic -> {
				List<ILineDataSet> sets = chart1.getData().getDataSets();

				for (ILineDataSet iSet : sets) {

					LineDataSet set = (LineDataSet) iSet;
					set.setMode(set.getMode() == LineDataSet.Mode.HORIZONTAL_BEZIER ? LineDataSet.Mode.LINEAR : LineDataSet.Mode.HORIZONTAL_BEZIER);
				}
				chart1.invalidate();
			}
			case R.id.actionTogglePinch -> {
				chart1.setPinchZoom(!chart1.isPinchZoomEnabled());

				chart1.invalidate();
			}
			case R.id.actionToggleAutoScaleMinMax -> {
				chart1.setAutoScaleMinMaxEnabled(!chart1.isAutoScaleMinMaxEnabled());
				chart1.notifyDataSetChanged();
			}
			case R.id.animateX -> chart1.animateX(2000);
			case R.id.animateY -> chart1.animateY(2000, Easing.EaseInCubic);
			case R.id.animateXY -> chart1.animateXY(2000, 2000);
			case R.id.actionSave -> {
				if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
					saveToGallery();
				} else {
					requestStoragePermission(chart1);
				}
			}
		}
		return true;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		tvXMax.setText(String.valueOf(seekBarX.getProgress()));
		tvYMax.setText(String.valueOf(seekBarY.getProgress()));

		setData(seekBarX.getProgress(), seekBarY.getProgress());

		// redraw
		chart1.invalidate();
	}

	@Override
	protected void saveToGallery() {
		saveToGallery(chart1, "LineChartActivity1");
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onValueSelected(Entry e, Highlight h) {
		Log.i("Entry selected", e.toString());
		Log.i("LOW HIGH", "low: " + chart1.getLowestVisibleX() + ", high: " + chart1.getHighestVisibleX());
		Log.i("MIN MAX", "xMin: " + chart1.getXChartMin() + ", xMax: " + chart1.getXChartMax() + ", yMin: " + chart1.getYChartMin() + ", yMax: " + chart1.getYChartMax());
	}

	@Override
	public void onNothingSelected() {
		Log.i("Nothing selected", "Nothing selected.");
	}
}
