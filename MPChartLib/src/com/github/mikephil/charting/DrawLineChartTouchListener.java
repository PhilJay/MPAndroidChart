package com.github.mikephil.charting;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class DrawLineChartTouchListener extends SimpleOnGestureListener implements OnTouchListener {

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

}
