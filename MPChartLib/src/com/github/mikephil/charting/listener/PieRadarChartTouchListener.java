
package com.github.mikephil.charting.listener;

import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.github.mikephil.charting.charts.PieRadarChartBase;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.interfaces.OnChartGestureListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.SelInfo;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

/**
 * Touchlistener for the PieChart.
 * 
 * @author Philipp Jahoda
 */
public class PieRadarChartTouchListener extends SimpleOnGestureListener implements OnTouchListener {

    private PieRadarChartBase mChart;

    private GestureDetector mGestureDetector;

    public PieRadarChartTouchListener(PieRadarChartBase ctx) {
        this.mChart = ctx;

        mGestureDetector = new GestureDetector(ctx.getContext(), this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {

        if (mGestureDetector.onTouchEvent(e))
            return true;

        // if rotation by touch is enabled
        if (mChart.isRotationEnabled()) {

            float x = e.getX();
            float y = e.getY();

            switch (e.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    mChart.setStartAngle(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mChart.updateRotation(x, y);
                    mChart.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent me) {
        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartLongPressed(me);
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return true;
    }

    /** reference to the last highlighted object */
    private Highlight mLastHighlight = null;

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartSingleTapped(e);
        }

        float distance = mChart.distanceToCenter(e.getX(), e.getY());

        // check if a slice was touched
        if (distance > mChart.getRadius()) {

            // if no slice was touched, highlight nothing
            mChart.highlightValues(null);
            mLastHighlight = null;

        } else {

            float angle = mChart.getAngleForPoint(e.getX(), e.getY());
            int index = mChart.getIndexForAngle(angle);

            // check if the index could be found
            if (index < 0) {
                
                mChart.highlightValues(null);
                mLastHighlight = null;
                
            } else {

                ArrayList<SelInfo> valsAtIndex = mChart.getYValsAtIndex(index);

                int dataSetIndex = 0;

                // get the dataset that is closest to the selection (PieChart
                // only
                // has one DataSet)
                if (mChart instanceof RadarChart) {

                    dataSetIndex = Utils.getClosestDataSetIndex(valsAtIndex, distance
                            / ((RadarChart) mChart).getFactor());
                }

                Highlight h = new Highlight(index, dataSetIndex);

                if (h.equalTo(mLastHighlight)) {

                    mChart.highlightTouch(null);
                    mLastHighlight = null;
                } else {

                    mChart.highlightTouch(h);
                    mLastHighlight = h;
                }
            }
        }

        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartDoubleTapped(e);
        }
        return super.onDoubleTap(e);
    }
}
