
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.interfaces.dataprovider.ScatterDataProvider;
import com.github.mikephil.charting.renderer.ScatterChartRenderer;
import com.github.mikephil.charting.renderer.ShapeRenders.ChevronDownShapeRenderer;
import com.github.mikephil.charting.renderer.ShapeRenders.ChevronUpShapeRenderer;
import com.github.mikephil.charting.renderer.ShapeRenders.CircleShapeRenderer;
import com.github.mikephil.charting.renderer.ShapeRenders.CrossShapeRenderer;
import com.github.mikephil.charting.renderer.ShapeRenders.ShapeRenderer;
import com.github.mikephil.charting.renderer.ShapeRenders.SquareShapeRenderer;
import com.github.mikephil.charting.renderer.ShapeRenders.TriangleShapeRenderer;
import com.github.mikephil.charting.renderer.ShapeRenders.XShapeRenderer;

import java.util.HashMap;

/**
 * The ScatterChart. Draws dots, triangles, squares and custom shapes into the
 * Chart-View. CIRCLE and SCQUARE offer the best performance, TRIANGLE has the
 * worst performance.
 *
 * @author Philipp Jahoda
 */
public class ScatterChart extends BarLineChartBase<ScatterData> implements ScatterDataProvider {


    /**
     * Dictionary of ShapeRenderer which are responsible for drawing custom shapes.
     * Can add to it your custom shapes.
     * CustomShapeRenderer Implements ShapeRenderer{}
     */
    private static HashMap<String, ShapeRenderer> shapeRendererList;

    public static void registerShapeRenderer(String scatterShapeName, ShapeRenderer shapeRenderer) {
        if (shapeRendererList == null) {
            shapeRendererList = new HashMap<>();
        }
        shapeRendererList.put(scatterShapeName, shapeRenderer);
    }

    public static ShapeRenderer getShapeRenderer(String scatterShapeName) {
        if (shapeRendererList == null) {
            initShapeRenderer();
        }
        return shapeRendererList.get(scatterShapeName);
    }

    /**
     * Init ShapeRendererList
     */
    private static void initShapeRenderer() {
        registerShapeRenderer(com.github.mikephil.charting.renderer.ShapeRenders.ScatterShape.getScatterShapeNames()
                .get(com.github.mikephil.charting.renderer.ShapeRenders.ScatterShape.SQUARE), new SquareShapeRenderer());
        registerShapeRenderer(com.github.mikephil.charting.renderer.ShapeRenders.ScatterShape.getScatterShapeNames()
                .get(com.github.mikephil.charting.renderer.ShapeRenders.ScatterShape.CIRCLE), new CircleShapeRenderer());
        registerShapeRenderer(com.github.mikephil.charting.renderer.ShapeRenders.ScatterShape.getScatterShapeNames()
                .get(com.github.mikephil.charting.renderer.ShapeRenders.ScatterShape.TRIANGLE), new TriangleShapeRenderer());
        registerShapeRenderer(com.github.mikephil.charting.renderer.ShapeRenders.ScatterShape.getScatterShapeNames()
                .get(com.github.mikephil.charting.renderer.ShapeRenders.ScatterShape.CROSS), new CrossShapeRenderer());
        registerShapeRenderer(com.github.mikephil.charting.renderer.ShapeRenders.ScatterShape.getScatterShapeNames()
                .get(com.github.mikephil.charting.renderer.ShapeRenders.ScatterShape.X), new XShapeRenderer());
        registerShapeRenderer(com.github.mikephil.charting.renderer.ShapeRenders.ScatterShape.getScatterShapeNames()
                .get(com.github.mikephil.charting.renderer.ShapeRenders.ScatterShape.CHEVRON_UP), new ChevronUpShapeRenderer());
        registerShapeRenderer(com.github.mikephil.charting.renderer.ShapeRenders.ScatterShape.getScatterShapeNames()
                .get(com.github.mikephil.charting.renderer.ShapeRenders.ScatterShape.CHEVRON_DOWN), new ChevronDownShapeRenderer());
    }


    /**
     * enum that defines the shape that is drawn where the values are
     */
    public enum ScatterShape {
        SQUARE, CIRCLE, TRIANGLE, CROSS, X,
    }

    /**
     * Returns all possible predefined ScatterShapes.
     *
     * @return ScatterShape to array
     */
    public static ScatterShape[] getAllPossibleShapes() {
        return new ScatterShape[] {
                ScatterShape.SQUARE, ScatterShape.CIRCLE, ScatterShape.TRIANGLE, ScatterShape.CROSS
        };
    }

    public ScatterChart(Context context) {
        super(context);
    }

    public ScatterChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScatterChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void init() {
        super.init();

        mRenderer = new ScatterChartRenderer(this, mAnimator, mViewPortHandler);
        mXAxis.mAxisMinimum = -0.5f;
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        if (mXAxis.mAxisRange == 0 && mData.getYValCount() > 0)
            mXAxis.mAxisRange = 1;

        mXAxis.mAxisMaximum += 0.5f;
        mXAxis.mAxisRange = Math.abs(mXAxis.mAxisMaximum - mXAxis.mAxisMinimum);
    }


    public ScatterData getScatterData() {
        return mData;
    }

    ;
}
