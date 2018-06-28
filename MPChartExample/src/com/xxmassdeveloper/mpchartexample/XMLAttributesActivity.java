package com.xxmassdeveloper.mpchartexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

public class XMLAttributesActivity extends DemoBase {

	private LineChart lineChart;
	private LineChart lineChart2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xmlattributes);

		lineChart = (LineChart) findViewById(R.id.line_chart);
		lineChart2 = (LineChart) findViewById(R.id.line_chart2);
	}

	private void createLineDataSet() {


	}
}
