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
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DrawingContext;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PointD;

public class BarLineChartTouchListener extends SimpleOnGestureListener implements OnTouchListener {

	private static final float MIN_SCALE = 0.5f;

	private Matrix mMatrix = new Matrix();
	private Matrix mSavedMatrix = new Matrix();

	/** point where the touch action started */
	private PointF mTouchStartPoint = new PointF();

	/** center between two pointers (fingers on the display) */
	private PointF mTouchPointCenter = new PointF();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int POSTZOOM = 3;
	private static final int LONGPRESS = 4;
	private static final int DRAWING = 5;

	private static final int X_ZOOM = 6;
	private static final int Y_ZOOM = 7;
	private static final int PINCH_ZOOM = 8;

	/** if ture, user can draw on the chart */
	private boolean mDrawingEnabled = false;

	private int mTouchMode = NONE;

	private float mSavedXDist = 1f;
	private float mSavedYDist = 1f;
	private float mSavedDist = 1f;

	private long mStartTimestamp = 0;

	private BarLineChartBase mChart;

	private DrawingContext mDrawingContext;
	private GestureDetector mGestureDetector;

	public BarLineChartTouchListener(BarLineChartBase chart, Matrix start) {
		this.mChart = chart;
		this.mMatrix = start;

		mGestureDetector = new GestureDetector(chart.getContext(), this);
		mDrawingContext = new DrawingContext();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (mTouchMode == NONE) {
			mGestureDetector.onTouchEvent(event);
		}

		if (!mChart.isDragEnabled() && !mDrawingEnabled)
			return true;

		mDrawingContext.init(mChart.getDrawListener(), mChart.isAutoFinishEnabled());
		ChartData data = mChart.getData();
		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			if (event.getPointerCount() == 1 && mDrawingEnabled) {
				mTouchMode = DRAWING;
				// TODO not always create new drawing sets
				mStartTimestamp = System.currentTimeMillis();
				mDrawingContext.createNewDrawingDataSet(data);
				Log.i("Drawing", "New drawing data set created");
			} else {
				mSavedMatrix.set(mMatrix);
				mTouchStartPoint.set(event.getX(), event.getY());
			}

			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			if (event.getPointerCount() == 2) {
				long deltaT = System.currentTimeMillis() - mStartTimestamp;
				if ((mTouchMode == DRAWING && deltaT < 1000) || !mDrawingEnabled) {
					mDrawingContext.deleteLastDrawingEntry(data);

					// get the distance between the pointers on the x-axis
					mSavedXDist = getXDist(event);

					// get the distance between the pointers on the y-axis
					mSavedYDist = getYDist(event);

					// get the total distance between the pointers
					mSavedDist = spacing(event);

					if (mSavedDist > 10f) {

						if (mChart.isPinchZoomEnabled()) {
							mTouchMode = PINCH_ZOOM;
						} else {
							if (mSavedXDist > mSavedYDist)
								mTouchMode = X_ZOOM;
							else
								mTouchMode = Y_ZOOM;
						}

						mSavedMatrix.set(mMatrix);
						midPoint(mTouchPointCenter, event);
						mChart.disableScroll();
					}
				}
			}
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchMode = NONE;
			if (mTouchMode == DRAWING) {
				mDrawingContext.finishNewDrawingEntry(data);
				mChart.notifyDataSetChanged();
				mChart.invalidate();
				Log.i("Drawing", "Drawing finished");
			} else {
				mChart.enableScroll();
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mTouchMode = POSTZOOM;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mTouchMode == DRAWING) {
				PointD p = mChart.getValuesByTouchPoint(event.getX(), event.getY());

				int xIndex = (int) p.x;
				float yVal = (float) p.y;

				if (xIndex < 0)
					xIndex = 0;
				if (xIndex >= data.getXValCount()) {
					xIndex = data.getXValCount() - 1;
				}

				Entry entry = new Entry((float) yVal, xIndex);
				boolean added = mDrawingContext.addNewDrawingEntry(entry, data);
				if (added) {
					Log.i("Drawing", "Added entry " + entry.toString());
					mChart.notifyDataSetChanged();
					mChart.invalidate();
				}
			}
			if (((mTouchMode == NONE && !mDrawingEnabled) || (mTouchMode != DRAG && event.getPointerCount() == 3))
					&& distance(event.getX(), mTouchStartPoint.x, event.getY(), mTouchStartPoint.y) > 25f) {
				// has to be in no mode, when drawing not enabled, or 3 fingers
				mSavedMatrix.set(mMatrix);
				mTouchStartPoint.set(event.getX(), event.getY());

				mTouchMode = DRAG;
				mChart.disableScroll();

			} else if (mTouchMode == DRAG) {

				mMatrix.set(mSavedMatrix);
				PointF dragPoint = new PointF(event.getX(), event.getY());
				mMatrix.postTranslate(dragPoint.x - mTouchStartPoint.x, dragPoint.y - mTouchStartPoint.y);

			} else if (mTouchMode == X_ZOOM || mTouchMode == Y_ZOOM || mTouchMode == PINCH_ZOOM) {

				// get the distance between the pointers of the touch
				// event
				float totalDist = spacing(event);

				if (totalDist > 10f) {

					float[] values = new float[9];
					mMatrix.getValues(values);

					// get the previous scale factors
					float oldScaleX = values[Matrix.MSCALE_X];
					float oldScaleY = values[Matrix.MSCALE_Y];

					// take actions depending on the activated touch
					// mode
					if (mTouchMode == PINCH_ZOOM) {

						float scale = totalDist / mSavedDist; // total
																// scale

						mMatrix.set(mSavedMatrix);
						mMatrix.postScale(scale, scale, mTouchPointCenter.x, -mTouchPointCenter.y);

					} else if (mTouchMode == X_ZOOM) {

						float xDist = getXDist(event);
						float scaleX = xDist / mSavedXDist; // x-axis
															// scale

						if ((scaleX < 1 || oldScaleX < mChart.getMaxScaleX()) && (scaleX > 1 || oldScaleX > MIN_SCALE)) {

							mMatrix.set(mSavedMatrix);
							mMatrix.postScale(scaleX, 1f, mTouchPointCenter.x, mTouchPointCenter.y);
						}

					} else if (mTouchMode == Y_ZOOM) {

						float yDist = getYDist(event);
						float scaleY = yDist / mSavedYDist; // y-axis
															// scale

						if ((scaleY < 1 || oldScaleY < mChart.getMaxScaleY()) && (scaleY > 1 || oldScaleY > MIN_SCALE)) {

							mMatrix.set(mSavedMatrix);

							// y-axis comes from top to bottom, revert y
							mMatrix.postScale(1f, scaleY, mTouchPointCenter.x, -mTouchPointCenter.y);
						}
					}
				}
			} else if (mTouchMode == LONGPRESS) {
				mChart.disableScroll();
			}

			break;
		}

		// Perform the transformation
		mMatrix = mChart.refreshTouch(mMatrix);

		return true; // indicate event was handled
	}

	/**
	 * returns the touch mode the listener is currently in
	 * 
	 * @return
	 */
	public int getTouchMode() {
		return mTouchMode;
	}

	/**
	 * enables drawing by finger on the listener
	 * 
	 * @param mDrawingEnabled
	 */
	public void setDrawingEnabled(boolean mDrawingEnabled) {
		this.mDrawingEnabled = mDrawingEnabled;
	}

	/**
	 * returns the distance between two points
	 * 
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
	 * 
	 * @param point
	 * @param event
	 */
	private static void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2f, y / 2f);
	}

	/**
	 * returns the center point between three pointer touch points
	 * 
	 * @param point
	 * @param event
	 */
	private static void midPointForThree(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1) + event.getX(2);
		float y = event.getY(0) + event.getY(1) + event.getY(2);
		point.set(x / 3f, y / 3f);
	}

	/**
	 * returns the distance between two pointer touch points
	 * 
	 * @param event
	 * @return
	 */
	private static float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * calculates the distance on the x-axis between two pointers (fingers on the display)
	 * 
	 * @param e
	 * @return
	 */
	private static float getXDist(MotionEvent e) {
		float x = Math.abs(e.getX(0) - e.getX(1));
		return x;
	}

	/**
	 * calculates the distance on the y-axis between two pointers (fingers on the display)
	 * 
	 * @param e
	 * @return
	 */
	private static float getYDist(MotionEvent e) {
		float y = Math.abs(e.getY(0) - e.getY(1));
		return y;
	}

	/**
	 * returns the correct translation depending on the provided x and y touch points
	 * 
	 * @param e
	 * @return
	 */
	private PointF getTrans(float x, float y) {

		float xTrans = x - mChart.getOffsetLeft();
		float yTrans = -(mChart.getHeight() - y - mChart.getOffsetBottom());

		return new PointF(xTrans, yTrans);
	}

	/**
	 * returns the matrix object the listener holds
	 * 
	 * @return
	 */
	public Matrix getMatrix() {
		return mMatrix;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {

		Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());

		mChart.highlightValues(new Highlight[] { h });

		return super.onSingleTapConfirmed(e);
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {

		PointF trans = getTrans(e.getX(), e.getY());

		// double tap --> zoom in
		mMatrix.postScale(1.4f, 1.4f, trans.x, trans.y);

		mChart.refreshTouch(mMatrix);
		return super.onDoubleTap(e);
	}

	@Override
	public void onLongPress(MotionEvent e) {

		PointF trans = getTrans(e.getX(), e.getY());

		// zoom out
		mMatrix.postScale(0.7f, 0.7f, trans.x, trans.y);
		mChart.refreshTouch(mMatrix);
	};

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// ctx.showValue(e, matrix);
		return true;
	}

}
