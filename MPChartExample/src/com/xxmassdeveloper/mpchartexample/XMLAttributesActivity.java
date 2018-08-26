package com.xxmassdeveloper.mpchartexample;

import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;
import java.util.List;

public class XMLAttributesActivity extends DemoBase {

	private LineChart lineChart;
	private LineChart lineChart2;
	private LineDataSet dataSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xmlattributes);

		createLineDataSet();

		lineChart = (LineChart) findViewById(R.id.line_chart);
		lineChart2 = (LineChart) findViewById(R.id.line_chart2);

		lineChart.setData(new LineData(dataSet));
		lineChart2.setData(new LineData(dataSet));
	}

	private void createLineDataSet() {

		List<Entry> entries = new ArrayList<>();

		entries.add(new Entry(1f, 2f));
		entries.add(new Entry(2f, 3f));
		entries.add(new Entry(3f, 2f));
		entries.add(new Entry(4f, 10f));
		entries.add(new Entry(5f, 8f));
		entries.add(new Entry(6f, 5f));
		entries.add(new Entry(7f, 10f));
		entries.add(new Entry(8f, 9f));
		entries.add(new Entry(9f, 15f));

		dataSet = new LineDataSet(entries, "");
	}
}
