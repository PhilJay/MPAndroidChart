package com.xxmassdeveloper.mpchartexample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView;
import com.xxmassdeveloper.mpchartexample.databinding.ActivityLinechartBinding;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.List;

/**
 * Example of a heavily customized {@link LineChart} with limit lines, custom line shapes, etc.
 */
public class LineChartActivity1 extends DemoBase implements OnSeekBarChangeListener, OnChartValueSelectedListener {

	private ActivityLinechartBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityLinechartBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setTitle("LineChartActivity1");

		binding.seekBarX.setOnSeekBarChangeListener(this);

		binding.seekBarY.setMax(180);
		binding.seekBarY.setOnSeekBarChangeListener(this);

		// background color
		binding.chart1.setBackgroundColor(Color.WHITE);

		// disable description text
		binding.chart1.getDescription().setEnabled(false);

		// enable touch gestures
		binding.chart1.setTouchEnabled(true);

		// set listeners
		binding.chart1.setOnChartValueSelectedListener(this);
		binding.chart1.setDrawGridBackground(false);

		// create marker to display box when values are selected
		MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

		// Set the marker to the chart
		mv.setChartView(binding.chart1);
		binding.chart1.setMarker(mv);

		// enable scaling and dragging
		binding.chart1.setDragEnabled(true);
		binding.chart1.setScaleEnabled(true);

		// force pinch zoom along both axis
		binding.chart1.setPinchZoom(true);

		// vertical grid lines
		binding.chart1.getXAxis().enableGridDashedLine(10f, 10f, 0f);

		// disable dual axis (only use LEFT axis)
		binding.chart1.getAxisRight().setEnabled(false);

		// horizontal grid lines
		binding.chart1.getAxisLeft().enableGridDashedLine(10f, 10f, 0f);

		// axis range
		binding.chart1.getAxisLeft().setAxisMaximum(200f);
		binding.chart1.getAxisLeft().setAxisMinimum(-50f);

		LimitLine llXAxis = new LimitLine(9f, "Index 10");
		llXAxis.setLineWidth(4f);
		llXAxis.enableDashedLine(10f, 10f, 0f);
		llXAxis.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
		llXAxis.setTextSize(10f);
		llXAxis.setTypeface(tfRegular);

		LimitLine LimitLine1 = new LimitLine(150f, "Upper Limit");
		LimitLine1.setLineWidth(4f);
		LimitLine1.enableDashedLine(10f, 10f, 0f);
		LimitLine1.setLabelPosition(LimitLabelPosition.RIGHT_TOP);
		LimitLine1.setTextSize(10f);
		LimitLine1.setTypeface(tfRegular);

		LimitLine LimitLine2 = new LimitLine(-30f, "Lower Limit");
		LimitLine2.setLineWidth(4f);
		LimitLine2.enableDashedLine(10f, 10f, 0f);
		LimitLine2.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
		LimitLine2.setTextSize(10f);
		LimitLine2.setTypeface(tfRegular);

		// draw limit lines behind data instead of on top
		binding.chart1.getAxisLeft().setDrawLimitLinesBehindData(true);
		binding.chart1.getXAxis().setDrawLimitLinesBehindData(true);

		// add limit lines
		binding.chart1.getAxisLeft().addLimitLine(LimitLine1);
		binding.chart1.getAxisLeft().addLimitLine(LimitLine2);
		//xAxis.addLimitLine(llXAxis);

		// add data
		binding.seekBarX.setProgress(45);
		binding.seekBarY.setProgress(180);
		Log.d("setDataCreate", "$count=45 range=180f");
		DataTools.Companion.setData(this, binding.chart1, 45, 180f);

		// draw points over time
		binding.chart1.animateX(1500);

		// get the legend (only possible after setting data)
		Legend legend = binding.chart1.getLegend();
		legend.setForm(LegendForm.LINE);
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
				List<ILineDataSet> sets = binding.chart1.getData().getDataSets();

				for (ILineDataSet iSet : sets) {
					LineDataSet set = (LineDataSet) iSet;
					set.setDrawValues(!set.isDrawValuesEnabled());
				}
				binding.chart1.invalidate();
			}
			case R.id.actionToggleIcons -> {
				List<ILineDataSet> sets = binding.chart1.getData().getDataSets();

				for (ILineDataSet iSet : sets) {
					LineDataSet set = (LineDataSet) iSet;
					set.setDrawIcons(!set.isDrawIconsEnabled());
				}

				binding.chart1.invalidate();
			}
			case R.id.actionToggleHighlight -> {
				if (binding.chart1.getData() != null) {
					binding.chart1.getData().setHighlightEnabled(!binding.chart1.getData().isHighlightEnabled());
					binding.chart1.invalidate();
				}
			}
			case R.id.actionToggleFilled -> {
				List<ILineDataSet> sets = binding.chart1.getData().getDataSets();

				for (ILineDataSet iSet : sets) {
					LineDataSet set = (LineDataSet) iSet;
					set.setDrawFilled(!set.isDrawFilledEnabled());
				}
				binding.chart1.invalidate();
			}
			case R.id.actionToggleCircles -> {
				List<ILineDataSet> sets = binding.chart1.getData().getDataSets();

				for (ILineDataSet iSet : sets) {
					LineDataSet set = (LineDataSet) iSet;
					set.setDrawCircles(!set.isDrawCirclesEnabled());
				}
				binding.chart1.invalidate();
			}
			case R.id.actionToggleCubic -> {
				List<ILineDataSet> sets = binding.chart1.getData().getDataSets();

				for (ILineDataSet iSet : sets) {
					LineDataSet set = (LineDataSet) iSet;
					set.setMode(set.getMode() == LineDataSet.Mode.CUBIC_BEZIER ? LineDataSet.Mode.LINEAR : LineDataSet.Mode.CUBIC_BEZIER);
				}
				binding.chart1.invalidate();
			}
			case R.id.actionToggleStepped -> {
				List<ILineDataSet> sets = binding.chart1.getData().getDataSets();

				for (ILineDataSet iSet : sets) {
					LineDataSet set = (LineDataSet) iSet;
					set.setMode(set.getMode() == LineDataSet.Mode.STEPPED ? LineDataSet.Mode.LINEAR : LineDataSet.Mode.STEPPED);
				}
				binding.chart1.invalidate();
			}
			case R.id.actionToggleHorizontalCubic -> {
				List<ILineDataSet> sets = binding.chart1.getData().getDataSets();

				for (ILineDataSet iSet : sets) {
					LineDataSet set = (LineDataSet) iSet;
					set.setMode(set.getMode() == LineDataSet.Mode.HORIZONTAL_BEZIER ? LineDataSet.Mode.LINEAR : LineDataSet.Mode.HORIZONTAL_BEZIER);
				}
				binding.chart1.invalidate();
			}
			case R.id.actionTogglePinch -> {
				binding.chart1.setPinchZoom(!binding.chart1.isPinchZoomEnabled());
				binding.chart1.invalidate();
			}
			case R.id.actionToggleAutoScaleMinMax -> {
				binding.chart1.setAutoScaleMinMaxEnabled(!binding.chart1.isAutoScaleMinMaxEnabled());
				binding.chart1.notifyDataSetChanged();
			}
			case R.id.animateX -> binding.chart1.animateX(2000);
			case R.id.animateY -> binding.chart1.animateY(2000, Easing.EaseInCubic);
			case R.id.animateXY -> binding.chart1.animateXY(2000, 2000);
			case R.id.actionSave -> {
				if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
					saveToGallery();
				} else {
					requestStoragePermission(binding.chart1);
				}
			}
		}
		return true;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		binding.tvXMax.setText(String.valueOf(binding.seekBarX.getProgress()));
		binding.tvYMax.setText(String.valueOf(binding.seekBarY.getProgress()));

		DataTools.Companion.setData(this, binding.chart1, binding.seekBarX.getProgress(), binding.seekBarY.getProgress());

		// redraw
		binding.chart1.invalidate();
	}

	@Override
	protected void saveToGallery() {
		saveToGallery(binding.chart1, "LineChartActivity1");
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
		Log.i("LOW HIGH", "low: " + binding.chart1.getLowestVisibleX() + ", high: " + binding.chart1.getHighestVisibleX());
		Log.i("MIN MAX", "xMin: " + binding.chart1.getXChartMin() + ", xMax: " + binding.chart1.getXChartMax() + ", yMin: " + binding.chart1.getYChartMin() + ", yMax: " + binding.chart1.getYChartMax());
	}

	@Override
	public void onNothingSelected() {
		Log.i("Nothing selected", "Nothing selected.");
	}
}
