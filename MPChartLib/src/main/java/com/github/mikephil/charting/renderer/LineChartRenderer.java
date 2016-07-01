package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class LineChartRenderer extends LineRadarRenderer {

    private class DataSetImageCache {


        private Bitmap[] circleBitmaps;
        private int[] circleColors;

        private void ensureCircleCache(int size){
            if(circleBitmaps == null){
                circleBitmaps = new Bitmap[size];
            }else if(circleBitmaps.length < size){
                Bitmap[] tmp = new Bitmap[size];
                for(int i = 0 ; i < circleBitmaps.length ; i++){
                    tmp[i] = circleBitmaps[size];
                }
                circleBitmaps = tmp;
            }

            if(circleColors == null){
                circleColors = new int[size];
            }else if(circleColors.length < size){
                int[] tmp = new int[size];
                for(int i = 0 ; i < circleColors.length ; i++){
                    tmp[i] = circleColors[size];
                }
                circleColors = tmp;
            }
        }

    }


    protected LineDataProvider mChart;

    /**
     * paint for the inner circle of the value indicators
     */
    protected Paint mCirclePaintInner;

    /**
     * Bitmap object used for drawing the paths (otherwise they are too long if
     * rendered directly on the canvas)
     */
    protected WeakReference<Bitmap> mDrawBitmap;

    /**
     * on this canvas, the paths are rendered, it is initialized with the
     * pathBitmap
     */
    protected Canvas mBitmapCanvas;

    /**
     * the bitmap configuration to be used
     */
    protected Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;

    protected Path cubicPath = new Path();
    protected Path cubicFillPath = new Path();

    public LineChartRenderer(LineDataProvider chart, ChartAnimator animator,
                             ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mCirclePaintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaintInner.setStyle(Paint.Style.FILL);
        mCirclePaintInner.setColor(Color.WHITE);
    }

    @Override
    public void initBuffers() {

    }

    @Override
    public void drawData(Canvas c) {

        int width = (int) mViewPortHandler.getChartWidth();
        int height = (int) mViewPortHandler.getChartHeight();

        if (mDrawBitmap == null
                || (mDrawBitmap.get().getWidth() != width)
                || (mDrawBitmap.get().getHeight() != height)) {

            if (width > 0 && height > 0) {

                mDrawBitmap = new WeakReference<Bitmap>(Bitmap.createBitmap(width, height, mBitmapConfig));
                mBitmapCanvas = new Canvas(mDrawBitmap.get());
            } else
                return;
        }

        mDrawBitmap.get().eraseColor(Color.TRANSPARENT);

        LineData lineData = mChart.getLineData();

        ILineDataSet set;
        int setCount = lineData.getDataSets().size();
        for(int i = 0 ; i < setCount ; i++){
            set = lineData.getDataSets().get(i);

            if (set.isVisible() && set.getEntryCount() > 0)
                drawDataSet(c, set);
        }

        c.drawBitmap(mDrawBitmap.get(), 0, 0, mRenderPaint);
    }

    protected void drawDataSet(Canvas c, ILineDataSet dataSet) {

        if (dataSet.getEntryCount() < 1)
            return;

        mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
        mRenderPaint.setPathEffect(dataSet.getDashPathEffect());

        switch (dataSet.getMode()) {
            default:
            case LINEAR:
            case STEPPED:
                drawLinear(c, dataSet);
                break;

            case CUBIC_BEZIER:
                drawCubicBezier(dataSet);
                break;

            case HORIZONTAL_BEZIER:
                drawHorizontalBezier(dataSet);
                break;
        }

        mRenderPaint.setPathEffect(null);
    }

    protected void drawHorizontalBezier(ILineDataSet dataSet) {

        float phaseY = mAnimator.getPhaseY();

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        XBounds bounds = getXBounds(mChart, dataSet);

        cubicPath.reset();

        if (bounds.range >= 1) {

            Entry prev = dataSet.getEntryForIndex(bounds.min);
            Entry cur = prev;

            // let the spline start
            cubicPath.moveTo(cur.getX(), cur.getY() * phaseY);

            for (int j = bounds.min + 1; j <= bounds.range + bounds.min; j++) {

                prev = dataSet.getEntryForIndex(j - 1);
                cur = dataSet.getEntryForIndex(j);

                final float cpx = (prev.getX())
                        + (cur.getX() - prev.getX()) / 2.0f;

                cubicPath.cubicTo(
                        cpx, prev.getY() * phaseY,
                        cpx, cur.getY() * phaseY,
                        cur.getX(), cur.getY() * phaseY);
            }
        }

        // if filled is enabled, close the path
        if (dataSet.isDrawFilledEnabled()) {

            cubicFillPath.reset();
            cubicFillPath.addPath(cubicPath);
            // create a new path, this is bad for performance
            drawCubicFill(mBitmapCanvas, dataSet, cubicFillPath, trans, bounds);
        }

        mRenderPaint.setColor(dataSet.getColor());

        mRenderPaint.setStyle(Paint.Style.STROKE);

        trans.pathValueToPixel(cubicPath);

        mBitmapCanvas.drawPath(cubicPath, mRenderPaint);

        mRenderPaint.setPathEffect(null);
    }

    protected void drawCubicBezier(ILineDataSet dataSet) {

        float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));
        float phaseY = mAnimator.getPhaseY();

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        XBounds bounds = getXBounds(mChart, dataSet);

        float intensity = dataSet.getCubicIntensity();

        cubicPath.reset();

        if (bounds.range >= 1) {

            float prevDx = 0f;
            float prevDy = 0f;
            float curDx = 0f;
            float curDy = 0f;

            Entry prevPrev = dataSet.getEntryForIndex(bounds.min);
            Entry prev = prevPrev;
            Entry cur = prev;
            Entry next = dataSet.getEntryForIndex(bounds.min + 1);

            // let the spline start
            cubicPath.moveTo(cur.getX(), cur.getY() * phaseY);

            for (int j = bounds.min + 1; j <= bounds.range + bounds.min; j++) {

                prevPrev = dataSet.getEntryForIndex(j == 1 ? 0 : j - 2);
                prev = dataSet.getEntryForIndex(j - 1);
                cur = dataSet.getEntryForIndex(j);
                next = bounds.max > j + 1 ? dataSet.getEntryForIndex(j + 1) : cur;

                prevDx = (cur.getX() - prevPrev.getX()) * intensity;
                prevDy = (cur.getY() - prevPrev.getY()) * intensity;
                curDx = (next.getX() - prev.getX()) * intensity;
                curDy = (next.getY() - prev.getY()) * intensity;

                cubicPath.cubicTo(prev.getX() + prevDx, (prev.getY() + prevDy) * phaseY,
                        cur.getX() - curDx,
                        (cur.getY() - curDy) * phaseY, cur.getX(), cur.getY() * phaseY);
            }
        }

        // if filled is enabled, close the path
        if (dataSet.isDrawFilledEnabled()) {

            cubicFillPath.reset();
            cubicFillPath.addPath(cubicPath);
            // create a new path, this is bad for performance
            drawCubicFill(mBitmapCanvas, dataSet, cubicFillPath, trans, bounds);
        }

        mRenderPaint.setColor(dataSet.getColor());

        mRenderPaint.setStyle(Paint.Style.STROKE);

        trans.pathValueToPixel(cubicPath);

        mBitmapCanvas.drawPath(cubicPath, mRenderPaint);

        mRenderPaint.setPathEffect(null);
    }

    protected void drawCubicFill(Canvas c, ILineDataSet dataSet, Path spline, Transformer trans, XBounds bounds) {

        float fillMin = dataSet.getFillFormatter()
                .getFillLinePosition(dataSet, mChart);

        spline.lineTo(bounds.min + bounds.range, fillMin);
        spline.lineTo(bounds.min, fillMin);
        spline.close();

        trans.pathValueToPixel(spline);

        final Drawable drawable = dataSet.getFillDrawable();
        if (drawable != null) {

            drawFilledPath(c, spline, drawable);
        } else {

            drawFilledPath(c, spline, dataSet.getFillColor(), dataSet.getFillAlpha());
        }
    }

    private float[] mLineBuffer = new float[4];
    private XBounds xBoundsBuffer;

    /**
     * Draws a normal line.
     *
     * @param c
     * @param dataSet
     */
    protected void drawLinear(Canvas c, ILineDataSet dataSet) {

        int entryCount = dataSet.getEntryCount();

        final boolean isDrawSteppedEnabled = dataSet.isDrawSteppedEnabled();
        final int pointsPerEntryPair = isDrawSteppedEnabled ? 4 : 2;

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseY = mAnimator.getPhaseY();

        mRenderPaint.setStyle(Paint.Style.STROKE);

        Canvas canvas = null;

        // if the data-set is dashed, draw on bitmap-canvas
        if (dataSet.isDashedLineEnabled()) {
            canvas = mBitmapCanvas;
        } else {
            canvas = c;
        }

        if(xBoundsBuffer == null){
            xBoundsBuffer = getXBounds(mChart, dataSet);
        }else{
            xBoundsBuffer.set(mChart, dataSet);
        }
        final XBounds bounds = xBoundsBuffer;

        // more than 1 color
        if (dataSet.getColors().size() > 1) {

            if (mLineBuffer.length <= pointsPerEntryPair * 2)
                mLineBuffer = new float[pointsPerEntryPair * 4];

            for (int j = bounds.min; j <= bounds.range + bounds.min; j++) {

                Entry e = dataSet.getEntryForIndex(j);
                if (e == null) continue;

                mLineBuffer[0] = e.getX();
                mLineBuffer[1] = e.getY() * phaseY;

                if (j < bounds.max) {

                    e = dataSet.getEntryForIndex(j + 1);

                    if (e == null) break;

                    if (isDrawSteppedEnabled) {
                        mLineBuffer[2] = e.getX();
                        mLineBuffer[3] = mLineBuffer[1];
                        mLineBuffer[4] = mLineBuffer[2];
                        mLineBuffer[5] = mLineBuffer[3];
                        mLineBuffer[6] = e.getX();
                        mLineBuffer[7] = e.getY() * phaseY;
                    } else {
                        mLineBuffer[2] = e.getX();
                        mLineBuffer[3] = e.getY() * phaseY;
                    }

                } else {
                    mLineBuffer[2] = mLineBuffer[0];
                    mLineBuffer[3] = mLineBuffer[1];
                }

                trans.pointValuesToPixel(mLineBuffer);

                if (!mViewPortHandler.isInBoundsRight(mLineBuffer[0]))
                    break;

                // make sure the lines don't do shitty things outside
                // bounds
                if (!mViewPortHandler.isInBoundsLeft(mLineBuffer[2])
                        || (!mViewPortHandler.isInBoundsTop(mLineBuffer[1]) && !mViewPortHandler
                        .isInBoundsBottom(mLineBuffer[3]))
                        || (!mViewPortHandler.isInBoundsTop(mLineBuffer[1]) && !mViewPortHandler
                        .isInBoundsBottom(mLineBuffer[3])))
                    continue;

                // get the color that is set for this line-segment
                mRenderPaint.setColor(dataSet.getColor(j));

                canvas.drawLines(mLineBuffer, 0, pointsPerEntryPair * 2, mRenderPaint);
            }

        } else { // only one color per dataset

            if (mLineBuffer.length < Math.max((entryCount) * pointsPerEntryPair, pointsPerEntryPair) * 2)
                mLineBuffer = new float[Math.max((entryCount) * pointsPerEntryPair, pointsPerEntryPair) * 4];

            Entry e1, e2;

            e1 = dataSet.getEntryForIndex(bounds.min);

            if (e1 != null) {

                int j = 0;
                for (int x = bounds.min; x <= bounds.range + bounds.min; x++) {

                    e1 = dataSet.getEntryForIndex(x == 0 ? 0 : (x - 1));
                    e2 = dataSet.getEntryForIndex(x);

                    if (e1 == null || e2 == null) continue;

                    mLineBuffer[j++] = e1.getX();
                    mLineBuffer[j++] = e1.getY() * phaseY;

                    if (isDrawSteppedEnabled) {
                        mLineBuffer[j++] = e2.getX();
                        mLineBuffer[j++] = e1.getY() * phaseY;
                        mLineBuffer[j++] = e2.getX();
                        mLineBuffer[j++] = e1.getY() * phaseY;
                    }

                    mLineBuffer[j++] = e2.getX();
                    mLineBuffer[j++] = e2.getY() * phaseY;
                }

                if (j > 0) {
                    trans.pointValuesToPixel(mLineBuffer);

                    final int size = Math.max((bounds.range + 1) * pointsPerEntryPair, pointsPerEntryPair) * 2;

                    mRenderPaint.setColor(dataSet.getColor());

                    canvas.drawLines(mLineBuffer, 0, size, mRenderPaint);
                }
            }
        }

        mRenderPaint.setPathEffect(null);

        // if drawing filled is enabled
        if (dataSet.isDrawFilledEnabled() && entryCount > 0) {
            drawLinearFill(c, dataSet, trans, bounds);
        }
    }

    protected Path mGenerateFilledPathBuffer = new Path();

    /**
     * Draws a filled linear path on the canvas.
     *
     * @param c
     * @param dataSet
     * @param trans
     * @param bounds
     */
    protected void drawLinearFill(Canvas c, ILineDataSet dataSet, Transformer trans, XBounds bounds) {

        final Path filled = mGenerateFilledPathBuffer;

        final int startingIndex = bounds.min;
        final int endingIndex = bounds.range + bounds.min;
        final int indexInterval = 128;

        int currentStartIndex = 0;
        int currentEndIndex = indexInterval;
        int iterations = 0;

        // Doing this iteratively in order to avoid OutOfMemory errors that can happen on large bounds sets.
        do{
            currentStartIndex = startingIndex + (iterations * indexInterval);
            currentEndIndex = currentStartIndex + indexInterval;
            currentEndIndex = currentEndIndex > endingIndex ? endingIndex : currentEndIndex;

            if(currentStartIndex <= currentEndIndex) {
                generateFilledPath(dataSet, currentStartIndex, currentEndIndex, filled);



                trans.pathValueToPixel(filled);

                final Drawable drawable = dataSet.getFillDrawable();
                if (drawable != null) {

                    drawFilledPath(c, filled, drawable);
                } else {

                    drawFilledPath(c, filled, dataSet.getFillColor(), dataSet.getFillAlpha());
                }
            }

            iterations++;

        }while(currentStartIndex <= currentEndIndex);

    }

    /**
     * Generates a path that is used for filled drawing.
     *
     * @param dataSet The dataset from which to read the entries.
     * @param startIndex The index from which to start reading the dataset
     * @param endIndex The index from which to stop reading the dataset
     * @param outputPath The path object that will be assigned the chart data.
     *
     * @return
     */
    private void generateFilledPath(final ILineDataSet dataSet, final int startIndex, final int endIndex, final Path outputPath) {

        final float fillMin = dataSet.getFillFormatter().getFillLinePosition(dataSet, mChart);
        final float phaseY = mAnimator.getPhaseY();
        final boolean isDrawSteppedEnabled = dataSet.getMode() == LineDataSet.Mode.STEPPED;

        final Path filled = outputPath;
        filled.reset();

        final Entry entry = dataSet.getEntryForIndex(startIndex);

        filled.moveTo(entry.getX(), fillMin);
        filled.lineTo(entry.getX(), entry.getY() * phaseY);

        // create a new path
        Entry currentEntry = null;
        Entry previousEntry = null;
        for (int x = startIndex + 1 ; x <= endIndex ; x++) {

            currentEntry = dataSet.getEntryForIndex(x);

            if (isDrawSteppedEnabled && previousEntry != null) {
                filled.lineTo(currentEntry.getX(), previousEntry.getY() * phaseY);
            }

            filled.lineTo(currentEntry.getX(), currentEntry.getY() * phaseY);

            previousEntry = currentEntry;
        }

        // close up
        if(currentEntry != null) {
            filled.lineTo(currentEntry.getX(), fillMin);
        }

        filled.close();

    }

    @Override
    public void drawValues(Canvas c) {

        if (isDrawingValuesAllowed(mChart)) {

            List<ILineDataSet> dataSets = mChart.getLineData().getDataSets();

            for (int i = 0; i < dataSets.size(); i++) {

                ILineDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                // make sure the values do not interfear with the circles
                int valOffset = (int) (dataSet.getCircleRadius() * 1.75f);

                if (!dataSet.isDrawCirclesEnabled())
                    valOffset = valOffset / 2;

                XBounds bounds = getXBounds(mChart, dataSet);

                float[] positions = trans.generateTransformedValuesLine(dataSet, mAnimator.getPhaseX(), mAnimator
                        .getPhaseY(), bounds.min, bounds.max);

                for (int j = 0; j < positions.length; j += 2) {

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                        continue;

                    Entry entry = dataSet.getEntryForIndex(j / 2 + bounds.min);

                    drawValue(c, dataSet.getValueFormatter(), entry.getY(), entry, i, x,
                            y - valOffset, dataSet.getValueTextColor(j / 2));
                }
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
        drawCircles(c);
    }

    private Path mCirclePathBuffer = new Path();
    private float[] mCirclesBuffer = new float[2];
    private HashMap<IDataSet, DataSetImageCache> mImageCaches = new HashMap<>();
    private XBounds mXBoundsBuffer;
    protected void drawCircles(Canvas c) {

        mRenderPaint.setStyle(Paint.Style.FILL);

        float phaseY = mAnimator.getPhaseY();
        float[] circlesBuffer = mCirclesBuffer;

        circlesBuffer[0] = 0;
        circlesBuffer[1] = 0;

        List<ILineDataSet> dataSets = mChart.getLineData().getDataSets();
        final int dataSetCount = dataSets.size();
        for (int i = 0; i < dataSetCount ; i++) {

            ILineDataSet dataSet = dataSets.get(i);

            DataSetImageCache imageCache;

            if(mImageCaches.containsKey(dataSet)){
                imageCache = mImageCaches.get(dataSet);
            }else{
                imageCache = new DataSetImageCache();
                mImageCaches.put(dataSet, imageCache);
            }
            imageCache.ensureCircleCache(dataSet.getCircleColorCount());

            if (!dataSet.isVisible() || !dataSet.isDrawCirclesEnabled() ||
                    dataSet.getEntryCount() == 0)
                continue;

            mCirclePaintInner.setColor(dataSet.getCircleHoleColor());

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

            if(mXBoundsBuffer == null) {
                mXBoundsBuffer = getXBounds(mChart, dataSet);
            }else{
                mXBoundsBuffer.set(mChart,dataSet);
            }

            XBounds bounds = mXBoundsBuffer;

            float circleRadius = dataSet.getCircleRadius();
            float circleHoleRadius = dataSet.getCircleHoleRadius();
            boolean drawCircleHole = dataSet.isDrawCircleHoleEnabled() &&
                    circleHoleRadius < circleRadius &&
                    circleHoleRadius > 0.f;
            boolean drawTransparentCircleHole = drawCircleHole &&
                    dataSet.getCircleHoleColor() == ColorTemplate.COLOR_NONE;

            int boundsRangeCount = bounds.range + bounds.min;
            for (int j = bounds.min; j <= boundsRangeCount; j++) {
                Entry e = dataSet.getEntryForIndex(j);

                if (e == null) break;

                circlesBuffer[0] = e.getX();
                circlesBuffer[1] = e.getY() * phaseY;

                trans.pointValuesToPixel(circlesBuffer);

                if (!mViewPortHandler.isInBoundsRight(circlesBuffer[0]))
                    break;

                // make sure the circles don't do shitty things outside
                // bounds
                if (!mViewPortHandler.isInBoundsLeft(circlesBuffer[0]) ||
                        !mViewPortHandler.isInBoundsY(circlesBuffer[1]))
                    continue;

                final int circleColor = dataSet.getCircleColor(j);
                mRenderPaint.setColor(circleColor);

                Bitmap circleBitmap = null;

                final int dataSetColorCount = imageCache.circleColors.length;
                int colorIndex;
                for(colorIndex = 0 ; colorIndex < dataSetColorCount ; colorIndex++) {
                    int tempColor = imageCache.circleColors[colorIndex];
                    Bitmap tempBitmap = imageCache.circleBitmaps[colorIndex];
                    if(tempColor == circleColor) {
                        circleBitmap = tempBitmap;
                        break;
                    }else if(tempBitmap == null){
                        break;
                    }
                }


                if(circleBitmap == null){
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                    circleBitmap = Bitmap.createBitmap((int)(circleRadius * 2.1), (int)(circleRadius * 2.1), conf);
                    Canvas canvas = new Canvas(circleBitmap);
                    imageCache.circleBitmaps[colorIndex] = circleBitmap;
                    imageCache.circleColors[colorIndex] = circleColor;

                    if(drawTransparentCircleHole){
                        // Begin path for circle with hole
                        mCirclePathBuffer.reset();

                        mCirclePathBuffer.addCircle(
                                circleRadius,
                                circleRadius,
                                circleRadius,
                                Path.Direction.CW);

                        // Cut hole in path
                        mCirclePathBuffer.addCircle(
                                circleRadius,
                                circleRadius,
                                circleHoleRadius,
                                Path.Direction.CCW);

                        // Fill in-between
                        canvas.drawPath(mCirclePathBuffer, mRenderPaint);
                    }else{

                        canvas.drawCircle(
                                circleRadius,
                                circleRadius,
                                circleRadius,
                                mRenderPaint);

                        if (drawCircleHole) {
                            canvas.drawCircle(
                                    circleRadius,
                                    circleRadius,
                                    circleHoleRadius,
                                    mCirclePaintInner);
                        }
                    }
                }

                if(circleBitmap != null){

                    c.drawBitmap(circleBitmap, circlesBuffer[0] - circleRadius, circlesBuffer[1] - circleRadius, mRenderPaint);

                }
            }
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        LineData lineData = mChart.getLineData();

        for (Highlight high : indices) {

            ILineDataSet set = lineData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            Entry e = set.getEntryForXPos(high.getX());

            if (!isInBoundsX(e, set))
                continue;

            PointD pix = mChart.getTransformer(set.getAxisDependency()).getPixelsForValues(e.getX(), e.getY() * mAnimator
                    .getPhaseY());

            high.setDraw((float) pix.x, (float) pix.y);

            // draw the lines
            drawHighlightLines(c, (float) pix.x, (float) pix.y, set);
        }
    }

    /**
     * Sets the Bitmap.Config to be used by this renderer.
     * Default: Bitmap.Config.ARGB_8888
     * Use Bitmap.Config.ARGB_4444 to consume less memory.
     *
     * @param config
     */
    public void setBitmapConfig(Bitmap.Config config) {
        mBitmapConfig = config;
        releaseBitmap();
    }

    /**
     * Returns the Bitmap.Config that is used by this renderer.
     *
     * @return
     */
    public Bitmap.Config getBitmapConfig() {
        return mBitmapConfig;
    }

    /**
     * Releases the drawing bitmap. This should be called when {@link LineChart#onDetachedFromWindow()}.
     */
    public void releaseBitmap() {
        if (mBitmapCanvas != null) {
            mBitmapCanvas.setBitmap(null);
            mBitmapCanvas = null;
        }
        if (mDrawBitmap != null) {
            mDrawBitmap.get().recycle();
            mDrawBitmap.clear();
            mDrawBitmap = null;
        }
    }
}
