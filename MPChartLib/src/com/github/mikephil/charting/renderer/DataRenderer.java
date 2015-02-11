package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Utils;

public abstract class DataRenderer extends Renderer {   
    
    protected ChartAnimator mAnimator;
    
    /** main paint object used for rendering */
    protected Paint mRenderPaint;
    
    /** paint used for highlighting values */
    protected Paint mHighlightPaint;

    /**
     * paint object for drawing values (text representing values of chart
     * entries)
     */
    protected Paint mValuePaint;
    
    public DataRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(viewPortHandler);
        this.mAnimator = animator;
        
        mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRenderPaint.setStyle(Style.FILL);
        
        mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint.setColor(Color.rgb(63, 63, 63));
        mValuePaint.setTextAlign(Align.CENTER);
        mValuePaint.setTextSize(Utils.convertDpToPixel(9f));
        
        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));
    }
    
    public Paint getPaintValues() {
        return mValuePaint;
    }
    
    public Paint getPaintHighlight() {
        return mHighlightPaint;
    }

    public abstract void drawData(Canvas c);
    
    public abstract void drawValues(Canvas c);
    
    public abstract void drawExtras(Canvas c);
    
    public abstract void drawHighlighted(Canvas c, Highlight[] indices);
}
