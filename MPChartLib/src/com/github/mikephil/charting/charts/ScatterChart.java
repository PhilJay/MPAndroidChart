
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.interfaces.dataprovider.ScatterDataProvider;
import com.github.mikephil.charting.renderer.ScatterChartRenderer;
import com.github.mikephil.charting.renderer.scatter.ChevronDownShapeRenderer;
import com.github.mikephil.charting.renderer.scatter.ChevronUpShapeRenderer;
import com.github.mikephil.charting.renderer.scatter.CircleShapeRenderer;
import com.github.mikephil.charting.renderer.scatter.CrossShapeRenderer;
import com.github.mikephil.charting.renderer.scatter.ShapeRenderer;
import com.github.mikephil.charting.renderer.scatter.SquareShapeRenderer;
import com.github.mikephil.charting.renderer.scatter.TriangleShapeRenderer;
import com.github.mikephil.charting.renderer.scatter.XShapeRenderer;

import java.util.HashMap;

/**
 * The ScatterChart. Draws dots, triangles, squares and custom shapes into the
 * Chart-View. CIRCLE and SCQUARE offer the best performance, TRIANGLE has the
 * worst performance.
 *
 * @author Philipp Jahoda
 */
public class ScatterChart extends BarLineChartBase<ScatterData> implements ScatterDataProvider {

    protected ShapeRendererHandler mShapeRendererHandler;


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

        mShapeRendererHandler = new ShapeRendererHandler();

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

    @Override
    public ScatterData getScatterData() {
        return mData;
    }

    @Override
    public void addShapeRenderer(ShapeRenderer shapeRenderer, String shapeIdentifier) {
        mShapeRendererHandler.addShapeRenderer(shapeRenderer, shapeIdentifier);
    }

    @Override
    public ShapeRenderer getShapeRenderer(String shapeIdentifier) {
        return mShapeRendererHandler.getShapeRenderer(shapeIdentifier);
    }

    public enum ScatterShape {
        SQUARE("SQUARE"), CIRCLE("CIRCLE"), TRIANGLE("TRIANGLE"), CROSS("CROSS"), X("X"), CHEVRON_UP("CHEVRON_UP"),
        CHEVRON_DOWN("CHEVRON_DOWN");

        private final String shapeIdentifier;

        ScatterShape(final String shapeIdentifier) {
            this.shapeIdentifier = shapeIdentifier;
        }

        @Override
        public String toString() {
            return shapeIdentifier;
        }

        public static ScatterShape[] getAllDefaultShapes() {
            return new ScatterShape[]{SQUARE, CIRCLE, TRIANGLE, CROSS, X, CHEVRON_UP, CHEVRON_DOWN};
        }
    }

    /**
     * Handler class for all different ShapeRenderers.
     */
    protected static class ShapeRendererHandler {

        /**
         * Dictionary of ShapeRenderer which are responsible for drawing custom shapes.
         * Can add to it your custom shapes.
         * CustomShapeRenderer Implements ShapeRenderer{}
         */
        protected HashMap<String, ShapeRenderer> shapeRendererList;

        public ShapeRendererHandler() {
            initShapeRenderers();
        }

        /**
         * Adds a new ShapeRenderer and the shapeIdentifier it is responsible for drawing.
         * This shapeIdentifier should correspond to a DataSet with the same identifier.
         *
         * @param shapeRenderer
         * @param shapeIdentifier
         */
        public void addShapeRenderer(ShapeRenderer shapeRenderer, String shapeIdentifier) {
            shapeRendererList.put(shapeIdentifier, shapeRenderer);
        }

        public ShapeRenderer getShapeRenderer(String shapeIdentifier) {
            return shapeRendererList.get(shapeIdentifier);
        }

        /**
         * Init default ShapeRenderers.
         */
        protected void initShapeRenderers() {
            shapeRendererList = new HashMap<>();

            shapeRendererList.put(ScatterShape.SQUARE.toString(), new SquareShapeRenderer());
            shapeRendererList.put(ScatterShape.CIRCLE.toString(), new CircleShapeRenderer());
            shapeRendererList.put(ScatterShape.TRIANGLE.toString(), new TriangleShapeRenderer());
            shapeRendererList.put(ScatterShape.CROSS.toString(), new CrossShapeRenderer());
            shapeRendererList.put(ScatterShape.X.toString(), new XShapeRenderer());
            shapeRendererList.put(ScatterShape.CHEVRON_UP.toString(), new ChevronUpShapeRenderer());
            shapeRendererList.put(ScatterShape.CHEVRON_DOWN.toString(), new ChevronDownShapeRenderer());
        }
    }
}
