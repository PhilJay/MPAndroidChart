package com.github.mikephil.charting.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.MPPointF;

import java.lang.ref.WeakReference;

/**
 * View that can be displayed when selecting values in the chart. Extend this class to provide custom layouts for your
 * markers.
 *
 * @author Philipp Jahoda
 */
public class MarkerImage implements IMarker {

    private Context mContext;
    private Drawable mDrawable;

    private MPPointF mOffset = new MPPointF();
    private MPPointF mOffset2 = new MPPointF();
    private WeakReference<Chart> mWeakChart;

    private FSize mSize = new FSize();
    private Rect mDrawableBoundsCache = new Rect();

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param drawableResourceId the drawable resource to render
     */
    public MarkerImage(Context context, int drawableResourceId) {
        mContext = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mDrawable = mContext.getResources().getDrawable(drawableResourceId, null);
        }
        else
        {
            mDrawable = mContext.getResources().getDrawable(drawableResourceId);
        }
    }

    public void setOffset(MPPointF offset) {
        mOffset = offset;

        if (mOffset == null) {
            mOffset = new MPPointF();
        }
    }

    public void setOffset(float offsetX, float offsetY) {
        mOffset.x = offsetX;
        mOffset.y = offsetY;
    }

    @Override
    public MPPointF getOffset() {
        return mOffset;
    }

    public void setSize(FSize size) {
        mSize = size;

        if (mSize == null) {
            mSize = new FSize();
        }
    }

    public FSize getSize() {
        return mSize;
    }

    public void setChartView(Chart chart) {
        mWeakChart = new WeakReference<>(chart);
    }

    public Chart getChartView() {
        return mWeakChart == null ? null : mWeakChart.get();
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {

        MPPointF offset = getOffset();
        mOffset2.x = offset.x;
        mOffset2.y = offset.y;

        Chart chart = getChartView();

        float width = mSize.width;
        float height = mSize.height;

        if (width == 0.f && mDrawable != null) {
            width = mDrawable.getIntrinsicWidth();
        }
        if (height == 0.f && mDrawable != null) {
            height = mDrawable.getIntrinsicHeight();
        }

        if (posX + mOffset2.x < 0) {
            mOffset2.x = - posX;
        } else if (chart != null && posX + width + mOffset2.x > chart.getWidth()) {
            mOffset2.x = chart.getWidth() - posX - width;
        }

        if (posY + mOffset2.y < 0) {
            mOffset2.y = - posY;
        } else if (chart != null && posY + height + mOffset2.y > chart.getHeight()) {
            mOffset2.y = chart.getHeight() - posY - height;
        }

        return mOffset2;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {

        if (mDrawable == null) return;

        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);

        float width = mSize.width;
        float height = mSize.height;

        if (width == 0.f && mDrawable != null) {
            width = mDrawable.getIntrinsicWidth();
        }
        if (height == 0.f && mDrawable != null) {
            height = mDrawable.getIntrinsicHeight();
        }

        mDrawable.copyBounds(mDrawableBoundsCache);
        mDrawable.setBounds(
                mDrawableBoundsCache.left,
                mDrawableBoundsCache.top,
                mDrawableBoundsCache.left + (int)width,
                mDrawableBoundsCache.top + (int)height);

        int saveId = canvas.save();
        // translate to the correct position and draw
        canvas.translate(posX + offset.x, posY + offset.y);
        mDrawable.draw(canvas);
        canvas.restoreToCount(saveId);

        mDrawable.setBounds(mDrawableBoundsCache);
    }
}
