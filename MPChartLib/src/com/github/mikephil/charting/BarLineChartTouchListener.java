
package com.github.mikephil.charting;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class BarLineChartTouchListener extends SimpleOnGestureListener implements OnTouchListener {

    private static final float MAX_SCALE = Float.MAX_VALUE; // 10f;
    private static final float MIN_SCALE = 0.5f;

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    PointF start = new PointF();
    PointF mid = new PointF();

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final int POSTZOOM = 3;
    private static final int LONGPRESS = 4;

    private int mode = NONE;
    private float oldDist = 1f;
    private Chart mChart;
    
    private GestureDetector mGestureDetector;

    public BarLineChartTouchListener(Chart ctx, Matrix start) {
        this.mChart = ctx;
        this.matrix = start;
        
        mGestureDetector = new GestureDetector(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        
        if(mode == NONE) {
            mGestureDetector.onTouchEvent(event);
        }

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                Log.d("TouchListener", "oldDist=" + oldDist);
                if (oldDist > 10f) { 
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    mChart.disableScroll();
                    Log.d("TouchListener","mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mode == NONE) {
                }
                Log.d("TouchListener","mode=NONE");
                mode = NONE;
                mChart.enableScroll();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d("TouchListener","mode=POSTZOOM");
                mode = POSTZOOM;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == NONE && distance(event.getX(), start.x, event.getY(), start.y) > 25f) {
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    Log.d("TouchListener","mode=DRAG");
                    mode = DRAG;
                    mChart.disableScroll();
                } else if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, 0);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    Log.d("TouchListener","newDist=" + newDist);
                    if (newDist > 10f) {
                        float scale = newDist / oldDist;
                        float[] values = new float[9];
                        matrix.getValues(values);
                        float oldScale = values[0];
                        if ((scale < 1 || oldScale < mChart.getMaxScale())
                                && (scale > 1 || oldScale > MIN_SCALE)) {
                            matrix.set(savedMatrix);
                            matrix.postScale(scale, 1, mid.x, mid.y);
                        }
                    }
                }
                else if (mode == LONGPRESS) {
                    mChart.disableScroll();
                }
                break;
        }

        // Perform the transformation
        matrix = mChart.refreshTouch(matrix);

        return true; // indicate event was handled
    }

    private PointF calcImagePosition(PointF klick) {
        PointF point = new PointF();
        Matrix inverse = new Matrix();
        matrix.invert(inverse);
        float[] pts = new float[2];
        float[] values = new float[9];
        pts[0] = klick.x;
        pts[1] = klick.y;
        inverse.mapPoints(pts);
        matrix.getValues(values);
        Log.d("TouchListener","Pts 0: " + pts[0] + ", Pts 1: " + pts[1]);
        point.x = (klick.x - values[Matrix.MTRANS_X]) / values[Matrix.MSCALE_X];
        point.y = (klick.y - values[Matrix.MTRANS_Y]) / values[Matrix.MSCALE_Y];
        Log.d("TouchListener","Pts X: " + point.x + ", Pts 1: " + point.y);
        return point;
    }

    public void resetMatrix(ImageView view) {
        matrix.reset();
        view.setImageMatrix(matrix);
    }

    private void limitScale() {
        // float[] values = new float[9];
        // matrix.getValues(values);
        // float sX = values[Matrix.MSCALE_X];
        //
        // float minScale = Math.max(minScale(), sX);
        // values[Matrix.MSCALE_X] = minScale;
        // values[Matrix.MSCALE_Y] = minScale;
        // matrix.setValues(values);
    }

    // public void moveTo(float x, float y) {
    // matrix.postTranslate(x, y);
    // limitPan();
    // view.setImageMatrix(matrix);
    // view.invalidate();
    // }
    //
    // private float minScale() {
    // return Math.max(view.getWidth() / PlanModel.imageWidth, view.getHeight()
    // / PlanModel.imageHeight);
    // }

    // public void setMatrixMinScale() {
    // DisplayMetrics metrics = new DisplayMetrics();
    // ctx.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    //
    // float minScale = Math.max(metrics.widthPixels / PlanModel.imageWidth,
    // (metrics.heightPixels - (50 + 25) * metrics.density)
    // / PlanModel.imageHeight);
    // matrix.setScale(minScale, minScale);
    // view.setImageMatrix(matrix);
    // }

    private static float distance(float eventX, float startX, float eventY, float startY) {
        float dx = eventX - startX;
        float dy = eventY - startY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private static void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private static float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /** Show an event in the LogCat view, for debugging */
    private void dumpEvent(MotionEvent event) {
        String names[] = {
                "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"
        };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }
        sb.append(", dist: " + distance(event.getX(), start.x, event.getY(), start.y) + "]");
        Log.d("TouchListener",sb.toString());
    }

    public Matrix getMatrix() {
        return matrix;
    }

//     @Override
//     public boolean onDoubleTap(MotionEvent e) {
//    
//     float[] values = new float[9];
//     matrix.getValues(values);
//     float sX = values[Matrix.MSCALE_X];
//     float minScale = minScale();
//    
//     if (sX > minScale * 1.5f) {
//     matrix.postScale(0.5f, 0.5f, e.getX(), e.getY());
//     } else {
//     matrix.postScale(2, 2, e.getX(), e.getY());
//     }
//     limitScale();
//     limitPan();
//     ctx.update();
//    
//     return true;
//     }
    
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {

        PointF pointF = calcImagePosition(start);
        
        mChart.highlightValues(new int[] {1});

        return super.onSingleTapConfirmed(e);
    }

    @Override
    public void onLongPress(MotionEvent arg0) {
        if (mode == NONE) {
            mode = LONGPRESS;
//            ctx.showValue(arg0, matrix);
        }
    };

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
//        ctx.showValue(e, matrix);
        return true;
    }

}
