
package com.xxmassdeveloper.mpchartexample;

import android.os.Bundle;

public class HorizontalBarChartActivity extends BarChartActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//FIXME:  Bar Shadow is not yet supported
		mChart.setDrawBarShadow(false);
	}

	@Override
	protected int getLayout()
	{
		return R.layout.activity_horizontalbarchart;
	}
}
