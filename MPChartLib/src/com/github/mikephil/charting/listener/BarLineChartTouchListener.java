package com.github.mikephil.charting.listener;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PointD;

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
	private static final int DRAWING = 5;

	/** if ture, user can draw on the chart */
	private boolean mDrawingEnabled = false;

	private int mode = NONE;
	private float oldDistX = 1f;
	private float oldDistY = 1f;
	private float oldDist = 1f;
	private BarLineChartBase mChart;

	private GestureDetector mGestureDetector;

	public BarLineChartTouchListener(BarLineChartBase chart, Matrix start) {
		this.mChart = chart;
		this.matrix = start;

		mGestureDetector = new GestureDetector(chart.getContext(), this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (mode == NONE) {
			mGestureDetector.onTouchEvent(event);
		}

		if (mDrawingEnabled) {
			if (mChart instanceof LineChart) {
				ChartData data = mChart.getData();
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					if (mode == NONE || mode == LONGPRESS) {
						mode = DRAWING;
						data.createNewDrawingDataSet(1);
						Log.i("Drawing", "New drawing data set created");
					}
					break;

				case MotionEvent.ACTION_MOVE:
					if (mode == DRAWING) {
					    
					    PointD p = mChart.getValuesByTouchPoint(event.getX(), event.getY());
					    
					    int xIndex = (int) p.x;
					    float yVal = (float) p.y;
					    
					    if (xIndex < 0)
				            xIndex = 0;
				        if (xIndex >= data.getXValCount()) {
				            xIndex = data.getXValCount() - 1;
				        }

						Entry entry = new Entry((float) yVal, xIndex);
						boolean added = data.addNewDrawingEntry(entry);
						if (added) {
							Log.i("Drawing", "Added entry " + entry.toString());
							// TODO it is bad to call prepare all the time
							mChart.prepare();
							mChart.invalidate();
						}
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					if (mode == DRAWING) {
						data.finishNewDrawingEntry();
						mode = NONE;
						mChart.prepare();
						mChart.invalidate();
						Log.i("Drawing", "Drawing finished");
					}
					break;

				default:
					Log.i("Drawing", "Other action " + event.toString());
					break;
				}
				// currently no dragging when drawing
				return true;
			}
		} else {

			if (!mChart.isDragEnabled())
				return true;

			// Handle touch events here...
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				float dist = spacing(event);
				
				oldDistX = getXDist(event);
				oldDistY = getYDist(event);
				oldDist = spacing(event);
				
				Log.d("TouchListener", "oldDist=" + dist);
				if (dist > 10f) {
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZOOM;
					mChart.disableScroll();
				}
				break;

			case MotionEvent.ACTION_UP:
				if (mode == NONE) {
				}

				mode = NONE;
				mChart.enableScroll();
				break;
			case MotionEvent.ACTION_POINTER_UP:

				mode = POSTZOOM;
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == NONE && distance(event.getX(), start.x, event.getY(), start.y) > 25f) {
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());

					mode = DRAG;
					mChart.disableScroll();
				} else if (mode == DRAG) {
					matrix.set(savedMatrix);
					matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
				} else if (mode == ZOOM) {
					float newDist = spacing(event);

					if (newDist > 10f) {
					    
					    float xDist = getXDist(event);
					    float yDist = getYDist(event);
					    
						float scaleX = xDist / oldDistX;
						float scaleY = yDist / oldDistY;
						float scale = newDist / oldDist;
						
						float[] values = new float[9];
						matrix.getValues(values);
						
						float oldScaleX = values[Matrix.MSCALE_X];
						float oldScaleY = values[Matrix.MSCALE_Y];
						
						if(mChart.isPinchZoomEnabled()) {
						
		                      matrix.set(savedMatrix);
		                      matrix.postScale(scale, scale, mid.x, -mid.y);
						} else {
						    if (xDist > yDist) {
                                if ((scaleX < 1 || oldScaleX < mChart.getMaxScaleX())
                                        && (scaleX > 1 || oldScaleX > MIN_SCALE)) {
                                    matrix.set(savedMatrix);
                                    matrix.postScale(scaleX, 1f, mid.x, mid.y);
                                }
                            } else {
                                if ((scaleY < 1 || oldScaleY < mChart.getMaxScaleY())
                                        && (scaleY > 1 || oldScaleY > MIN_SCALE)) {
                                    matrix.set(savedMatrix);
                                    
                                    // y-axis comes from top to bottom, revert y
                                    matrix.postScale(1f, scaleY, mid.x, -mid.y);
                                }
                            }
						}
						
//						Log.i("scale", "scale-x: " + scaleX + ", scale-y: " + scaleY + ", oldDistX: " + oldDistX  + ", oldDistY: " + oldDistY);
//						Log.i("scale", "xDist: " + xDist + ", yDist: " + yDist);
					}
				} else if (mode == LONGPRESS) {
					mChart.disableScroll();
				}
			
				break;
			}
		}

		// Perform the transformation
		matrix = mChart.refreshTouch(matrix);

		return true; // indicate event was handled
	}

	public void setDrawingEnabled(boolean mDrawingEnabled) {
		this.mDrawingEnabled = mDrawingEnabled;
	}

//	private PointF calcImagePosition(PointF klick) {
//		PointF point = new PointF();
//		Matrix inverse = new Matrix();
//		matrix.invert(inverse);
//		float[] pts = new float[2];
//		float[] values = new float[9];
//		pts[0] = klick.x;
//		pts[1] = klick.y;
//		inverse.mapPoints(pts);
//		matrix.getValues(values);
//		Log.d("TouchListener", "Pts 0: " + pts[0] + ", Pts 1: " + pts[1]);
//		point.x = (klick.x - values[Matrix.MTRANS_X]) / values[Matrix.MSCALE_X];
//		point.y = (klick.y - values[Matrix.MTRANS_Y]) / values[Matrix.MSCALE_Y];
//		Log.d("TouchListener", "Pts X: " + point.x + ", Pts 1: " + point.y);
//		return point;
//	}

//	public void resetMatrix(ImageView view) {
//		matrix.reset();
//		view.setImageMatrix(matrix);
//	}

//	private void limitScale() {
//		// float[] values = new float[9];
//		// matrix.getValues(values);
//		// float sX = values[Matrix.MSCALE_X];
//		//
//		// float minScale = Math.max(minScale(), sX);
//		// values[Matrix.MSCALE_X] = minScale;
//		// values[Matrix.MSCALE_Y] = minScale;
//		// matrix.setValues(values);
//	}

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

	/**
	 * returns the distance between two points
	 * @param eventX
	 * @param startX
	 * @param eventY
	 * @param startY
	 * @return
	 */
	private static float distance(float eventX, float startX, float eventY, float startY) {
		float dx = eventX - startX;
		float dy = eventY - startY;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * returns the center point between two pointer touch points
	 * @param point
	 * @param event
	 */
	private static void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2f, y / 2f);
	}

	/**
	 * returns the distance between two pointer touch points
	 * @param event
	 * @return
	 */
	private static float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}
	
	private static float getXDist(MotionEvent e) {
	    float x = Math.abs(e.getX(0) - e.getX(1));
	    return x;
	}
	
	private static float getYDist(MotionEvent e) {
        float y = Math.abs(e.getY(0) - e.getY(1));
        return y;
    }

//	/** Show an event in the LogCat view, for debugging */
//	private void dumpEvent(MotionEvent event) {
//		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
//		StringBuilder sb = new StringBuilder();
//		int action = event.getAction();
//		int actionCode = action & MotionEvent.ACTION_MASK;
//		sb.append("event ACTION_").append(names[actionCode]);
//		if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
//			sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
//			sb.append(")");
//		}
//		sb.append("[");
//		for (int i = 0; i < event.getPointerCount(); i++) {
//			sb.append("#").append(i);
//			sb.append("(pid ").append(event.getPointerId(i));
//			sb.append(")=").append((int) event.getX(i));
//			sb.append(",").append((int) event.getY(i));
//			if (i + 1 < event.getPointerCount())
//				sb.append(";");
//		}
//		sb.append(", dist: " + distance(event.getX(), start.x, event.getY(), start.y) + "]");
//		Log.d("TouchListener", sb.toString());
//	}

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

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {

		Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());

		mChart.highlightValues(new Highlight[] { h });

		return super.onSingleTapConfirmed(e);
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		if (mode == NONE) {
			mode = LONGPRESS;
			// ctx.showValue(arg0, matrix);
		}
	};

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// ctx.showValue(e, matrix);
		return true;
	}

}
