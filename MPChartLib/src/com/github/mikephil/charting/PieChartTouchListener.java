
package com.github.mikephil.charting;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PieChartTouchListener extends SimpleOnGestureListener implements OnTouchListener {

    private static final float MAX_SCALE = Float.MAX_VALUE; // 10f;
    private static final float MIN_SCALE = 0.5f;

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    PointF mid = new PointF();

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int ROTATE = 1;
    private static final int ZOOM = 2;
    private static final int POSTZOOM = 3;
    private static final int LONGPRESS = 4;

    private int mode = NONE;
    private float oldDist = 1f;
    private PieChart mChart;
    private float mPreviousY = 0f;
    private float mPreviousX = 0f;
    
    private Matrix mInverted = new Matrix();
    
    private GestureDetector mGestureDetector;

    public PieChartTouchListener(PieChart ctx) {
        this.mChart = ctx;
        
        mGestureDetector = new GestureDetector(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if (mode == NONE) {
            if (mGestureDetector.onTouchEvent(e))
                return true;
        }
        
        float x = e.getX();
        float y = e.getY();
        
        switch(e.getAction()) {
            
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
        
        mPreviousX = x;
        mPreviousY = y;
        
        return true;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    // @Override
    // public boolean onDoubleTap(MotionEvent e) {
    //
    // float[] values = new float[9];
    // matrix.getValues(values);
    // float sX = values[Matrix.MSCALE_X];
    // float minScale = minScale();
    //
    // if (sX > minScale * 1.5f) {
    // matrix.postScale(0.5f, 0.5f, e.getX(), e.getY());
    // } else {
    // matrix.postScale(2, 2, e.getX(), e.getY());
    // }
    // limitScale();
    // limitPan();
    // ctx.update();
    //
    // return true;
    // }

//    @Override
//    public boolean onSingleTapConfirmed(MotionEvent e) {
//
//        PointF pointF = calcImagePosition(start);
//        
//
//
//        return super.onSingleTapConfirmed(e);
//    }

    @Override
    public void onLongPress(MotionEvent arg0) {
        if (mode == NONE) {
            mode = LONGPRESS;
//            ctx.showValue(arg0, matrix);
        }
    };
    
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        
        PointF chartCenter = mChart.getCenter();
        PointF touchPoint = new PointF(e.getX(), e.getY());
        
        PointF v = new PointF(touchPoint.x - chartCenter.x, touchPoint.y - chartCenter.y);    
        
        Vector3 vector = new Vector3(v.x, v.y, 0f);
        
        vector.normalize();
        Vector3 one = new Vector3(1, 0, 0);
        
        float distance = mChart.distanceToCenter(e.getX(), e.getY());

        // check if a slice was touched
        if(distance < mChart.getRadius() / 2 || distance > mChart.getRadius()) {
            
            // if no slice was touched, highlight nothing
            mChart.highlightValues(new int[] {-1});
        } else {
            
            float f = one.dot(vector);
            
            int index = mChart.getIndexForAngle(mChart.getAngleForPoint(e.getX(), e.getY()));

            mChart.highlightValues(new int[] {index});
        }
        
        return true;
    }
}
